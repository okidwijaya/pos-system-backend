package com.kitadevelopers.pos.modules.order.service;

import com.kitadevelopers.pos.common.util.TaxCalculator;
import com.kitadevelopers.pos.modules.cart.entity.Cart;
import com.kitadevelopers.pos.modules.cart.entity.CartItem;
import com.kitadevelopers.pos.modules.cart.enums.CartStatus;
import com.kitadevelopers.pos.modules.cart.repository.CartItemRepository;
import com.kitadevelopers.pos.modules.cart.repository.CartRepository;
import com.kitadevelopers.pos.modules.customer.entity.Customer;
import com.kitadevelopers.pos.modules.customer.repository.CustomerRepository;
import com.kitadevelopers.pos.modules.order.dto.CheckoutRequest;
import com.kitadevelopers.pos.modules.order.dto.OrderHistoryResponse;
import com.kitadevelopers.pos.modules.order.dto.OrderResponse;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.entity.OrderItem;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.mapper.OrderMapper;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.repository.ProductRepository;
import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
//import com.kitadevelopers.pos.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void rollbackStock(Order order){

        for(OrderItem item : order.getItems()){
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request, String key){
        Optional<Order> existingOrder = orderRepository.findByIdempotencyKey(key);
        if(existingOrder.isPresent()){
            return OrderMapper.toResponse(existingOrder.get());
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        Cart cart = cartRepository.findByUserIdAndCartStatus(user.getId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart Not Found"));

        if(cart.getCartStatus() == CartStatus.CHECKED_OUT){
            throw new RuntimeException("Cart Already Checkout");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(cart.getId());

        if(cartItems.isEmpty()){
            throw new RuntimeException("Cart is empty");
        }

        Customer customer = null;
        if(request.customerEmail() != null){
            customer = customerRepository.findByEmail(request.customerEmail())
                    .orElseGet(() -> customerRepository.save(
                            Customer.builder()
                                    .name(request.customerName())
                                    .email(request.customerEmail())
                                    .build()
                    ));
        }

        Order order = Order.builder()
                .orderNumber("ORD-" + UUID.randomUUID())
                .cashier(user)
                .user(user)
                .idempotencyKey(key)
                .customer(customer)
                .orderStatus(OrderStatus.PENDING)
                .notes(request.notes())
                .build();

//        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for(CartItem cartItem : cartItems){
            Product product = productRepository.findByIdForUpdate(
                    cartItem.getProduct().getId()
            ).orElseThrow();

            if(product.getStock() < cartItem.getQuantity()){
                throw new RuntimeException("Stock not enough for: " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());

            BigDecimal subTotalBeforeTax = product.getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            BigDecimal taxAmount = TaxCalculator.Calculatetax(
                    product.getPrice(), cartItem.getQuantity(), product.getTaxRate()
            );

            BigDecimal subTotal = subTotalBeforeTax.add(taxAmount);

            OrderItem item = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .price(product.getPrice())
                    .quantity(cartItem.getQuantity())
                    .taxRate(product.getTaxRate())
                    .taxAmount(taxAmount)
                    .subtotalBeforeTax(subTotalBeforeTax)
                    .subtotal(subTotal)
                    .build();

            order.addItem(item);
            total = total.add(subTotal);
            totalTax = totalTax.add(taxAmount);
        }

//        for(CartItem cartItem : cartItems){
//
//            Product product = productRepository.findByIdForUpdate(
//                    cartItem.getProduct().getId()
//            ).orElseThrow();
//
//            if(product.getStock() < cartItem.getQuantity()){
//                throw new RuntimeException("Stock not enough. for " + product.getName());
//            }
//
//            product.setStock(product.getStock() - cartItem.getQuantity());
//
//            BigDecimal subtotal = product.getPrice()
//                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//
//            OrderItem item = OrderItem.builder()
//                    .product(product)
//                    .productName(product.getName())
//                    .price(product.getPrice())
//                    .quantity(cartItem.getQuantity())
//                    .subtotal(subtotal)
//                    .build();
//            order.addItem(item);
//            total = total.add(subtotal);
//        }

        order.setTotalAmount(total);
        order.setTotalTax(totalTax);
        order.setTotalBeforeTax(total.subtract(totalTax));

        try{
            order = orderRepository.save(order);
        } catch (DataIntegrityViolationException e) {
            return orderRepository.findByIdempotencyKey(key)
                    .map(OrderMapper::toResponse)
                    .orElseThrow();
        }

        cartItemRepository.deleteAll(cartItems);
        cart.setCartStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return OrderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderHistoryResponse> getOrderHistory(
            int page,
            int size,
            OrderStatus status,
            LocalDate startDate,
            LocalDate endDate
    ){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not Found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        LocalDateTime start = startDate != null
                ? startDate.atStartOfDay()
                : LocalDateTime.of(1970, 1, 1, 0, 0);

        LocalDateTime end = endDate != null
                ? endDate.atTime(23, 59, 59)
                : LocalDateTime.now();

        Page<Order> orders = orderRepository.findOrdersWithFilter(
                user.getId(),
                status,
                start,
                end,
                pageable
        );

        return  orders.map(OrderMapper::toHistoryRespnoe);
    }

//    @Transactional
//    public void mockPay(UUID orderId){
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow();
//
//        if(order.getOrderStatus() != OrderStatus.PENDING){
//            throw new RuntimeException("Order already processed");
//        }
//
//        order.setOrderStatus(OrderStatus.PAID);
//    }
}
