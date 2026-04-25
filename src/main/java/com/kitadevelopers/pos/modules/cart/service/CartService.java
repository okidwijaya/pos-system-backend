package com.kitadevelopers.pos.modules.cart.service;

import com.kitadevelopers.pos.modules.cart.dto.AddToCartRequest;
import com.kitadevelopers.pos.modules.cart.dto.CartResponse;
import com.kitadevelopers.pos.modules.cart.dto.UpdateCartItemRequest;
import com.kitadevelopers.pos.modules.cart.entity.Cart;
import com.kitadevelopers.pos.modules.cart.entity.CartItem;
import com.kitadevelopers.pos.modules.cart.enums.CartStatus;
import com.kitadevelopers.pos.modules.cart.repository.CartItemRepository;
import com.kitadevelopers.pos.modules.cart.repository.CartRepository;
import com.kitadevelopers.pos.modules.customer.repository.CustomerRepository;
import com.kitadevelopers.pos.modules.cart.mapper.CartMapper;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.repository.ProductRepository;
import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
import com.kitadevelopers.pos.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    private User getCurrentUser(){
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getOrCreateCart(User user){
        return cartRepository.findByUserAndCartStatus(user, CartStatus.ACTIVE)
                .orElseGet(() -> cartRepository.save(
                        Cart.builder()
                                .user(user)
                                .cartStatus(CartStatus.ACTIVE)
                                .items(new ArrayList<>())
                                .total(BigDecimal.ZERO)
                                .build()
                ));
    }

    public void recalculateCart(Cart cart){
        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        items.sort(Comparator.comparing(i -> i.getProduct().getId()));

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setItems(items);
        cart.setTotal(total);
    }

    @Transactional(readOnly = true)
    public CartResponse getMyCart(){
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserAndCartStatus(user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not Found"));

        recalculateCart(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getById(UUID cartId){
        User user = getCurrentUser();

        Cart cart = cartRepository.findByIdAndUser(cartId, user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if(cart.getCartStatus() != CartStatus.ACTIVE){
            throw new RuntimeException("Cart is not active");
        }

//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new RuntimeException("Cart not found"));
//
//        if(!cart.getUser().getId().equals(user.getId())){
//            throw new RuntimeException("Unauthorized");
//        }
//
        recalculateCart(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(UpdateCartItemRequest request){
        User user = getCurrentUser();

        CartItem item = cartItemRepository.findById(request.itemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if(!item.getCart().getUser().getId().equals(user.getId())){
            throw  new RuntimeException("Unauthorized cart access");
        }

        if(request.quantity() <= 0){
            cartItemRepository.delete(item);
        }else{
            item.setQuantity(request.quantity());
            cartItemRepository.save(item);
        }

        Cart cart = item.getCart();
        recalculateCart(cart);

        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(UUID itemId){
        User user = getCurrentUser();

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item Not Found"));

        if(!item.getCart().getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauhtorized cart access");
        }

        Cart cart = item.getCart();

        cartItemRepository.delete(item);

        recalculateCart(cart);
        cartRepository.save(cart);

        return CartMapper.toResponse(cart);
    }

    @Transactional
    public void clearCart(){
        User user = getCurrentUser();

        Cart cart = cartRepository.findByUserAndCartStatus( user, CartStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteAll(cart.getItems());

        cart.setItems(new ArrayList<>());
        cart.setTotal(BigDecimal.ZERO);

        cartRepository.save(cart);
    }

    @Transactional
    public void deleteCart(UUID cartId){
        User user = getCurrentUser();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if(!cart.getUser().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized");
        }

        cartRepository.delete(cart);
    }

    @Transactional
    public CartResponse addToCart(AddToCartRequest request){

        User cashier = getCurrentUser();
        Cart cart = getOrCreateCart(cashier);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("product not found"));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if(item == null){
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .price(product.getPrice())
                    .build();

            if (cart.getItems() == null){
                cart.setItems(new ArrayList<>());
            }

            cart.getItems().add(item);
        }else{
            item.setQuantity(item.getQuantity() + request.quantity());
        }

        cartItemRepository.save(item);

        List<CartItem> items = cartItemRepository.findAllByCartId(cart.getId());

        BigDecimal total = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (cart.getItems() == null){
            cart.setItems(new ArrayList<>());
        }else{
            cart.getItems().clear();
        }

        cart.getItems().addAll(items);
        cart.setTotal(total);

        Cart savedCart = cartRepository.save(cart);

        return CartMapper.toResponse(savedCart);
    }

//    @Transactional
//    public Order checkout(CheckoutRequest request){
//
//        User cashier = getCurrentUser();
//        Cart cart = getOrCreateCart(cashier);
//
//        if(cart.getItems().isEmpty()){
//            throw new RuntimeException("Cart is Empty");
//        }
//
//        Customer customer = customerRepository.findByEmail(request.customerEmail())
//                .orElseGet(() -> customerRepository.save(
//                        Customer.builder()
//                                .name(request.customerName())
//                                .email(request.customerEmail())
//                                .phone(request.customerPhone())
//                                .build()
//                ));
//
//        List<OrderItem> orderItems = new ArrayList<>();
//        BigDecimal total = BigDecimal.ZERO;
//
//        for (CartItem cartItem : cart.getItems()){
//            Product product = cartItem.getProduct();
//
//            if(product.getStock() < cartItem.getQuantity()){
//                throw new RuntimeException("Stock not enough");
//            }
//
//            product.setStock(product.getStock() - cartItem.getQuantity());
//
//            BigDecimal subtotal = product.getPrice()
//                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
//
//            total = total.add(subtotal);
//
//            OrderItem orderItem = OrderItem.builder()
//                    .product(product)
//                    .quantity(cartItem.getQuantity())
//                    .price(product.getPrice())
//                    .subtotal(subtotal)
//                    .build();
//
//            orderItems.add(orderItem);
//            total = total.add(subtotal);
//        }
//
//        Order order = Order.builder()
//                .user(cashier)
//                .customer(customer)
//                .items(orderItems)
//                .totalAmount(total)
//                .orderStatus(OrderStatus.PENDING)
//                .build();
//
//        orderItems.forEach(i -> i.setOrder(order));
//
//        cart.setCartStatus(CartStatus.CHECKED_OUT);
//
//        return orderRepository.save(order);
//    }
}
