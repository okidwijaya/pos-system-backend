package com.kitadevelopers.pos.modules.cart.service;

import com.kitadevelopers.pos.modules.cart.dto.AddToCartRequest;
import com.kitadevelopers.pos.modules.cart.dto.CartResponse;
import com.kitadevelopers.pos.modules.cart.entity.Cart;
import com.kitadevelopers.pos.modules.cart.entity.CartItem;
import com.kitadevelopers.pos.modules.cart.enums.CartStatus;
import com.kitadevelopers.pos.modules.cart.repository.CartItemRepository;
import com.kitadevelopers.pos.modules.cart.repository.CartRepository;
import com.kitadevelopers.pos.modules.customer.repository.CustomerRepository;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.repository.ProductRepository;
import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.enums.Role;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks
    private CartService cartService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addToCart_whenCartIsEmpty_addsOneItemAndCalculatesTotal() {
        User user = currentUser();
        UUID cartId = UUID.randomUUID();
        Product product = product(UUID.randomUUID(), "Mie Goreng", "5000");
        Cart cart = activeCart(cartId, user);
        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserAndCartStatus(user, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cartId, product.getId())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(item);
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(List.of(item));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addToCart(new AddToCartRequest(product.getId(), 2));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(2);
        assertThat(response.total()).isEqualByComparingTo("10000");
    }

    @Test
    void addToCart_whenProductAlreadyExists_sumsQuantity() {
        User user = currentUser();
        UUID cartId = UUID.randomUUID();
        Product product = product(UUID.randomUUID(), "Kopi", "12000");
        Cart cart = activeCart(cartId, user);
        CartItem existingItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();
        cart.getItems().add(existingItem);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserAndCartStatus(user, CartStatus.ACTIVE)).thenReturn(Optional.of(cart));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cartId, product.getId())).thenReturn(Optional.of(existingItem));
        when(cartItemRepository.save(existingItem)).thenReturn(existingItem);
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(List.of(existingItem));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponse response = cartService.addToCart(new AddToCartRequest(product.getId(), 3));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(5);
        assertThat(response.total()).isEqualByComparingTo("60000");
    }

    private User currentUser() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .email("cashier@example.com")
                .name("Cashier")
                .role(Role.CASHIER)
                .password("encoded")
                .build();
        SecurityContextHolder.setContext(new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null)
        ));
        return user;
    }

    private Cart activeCart(UUID id, User user) {
        return Cart.builder()
                .id(id)
                .user(user)
                .cartStatus(CartStatus.ACTIVE)
                .items(new ArrayList<>())
                .total(BigDecimal.ZERO)
                .build();
    }

    private Product product(UUID id, String name, String price) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(new BigDecimal(price))
                .stock(20)
                .sku("SKU-" + id)
                .costPrice(BigDecimal.ZERO)
                .build();
    }
}
