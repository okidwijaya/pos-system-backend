package com.kitadevelopers.pos.modules.order.service;

import com.kitadevelopers.pos.modules.cart.entity.Cart;
import com.kitadevelopers.pos.modules.cart.entity.CartItem;
import com.kitadevelopers.pos.modules.cart.enums.CartStatus;
import com.kitadevelopers.pos.modules.cart.repository.CartItemRepository;
import com.kitadevelopers.pos.modules.cart.repository.CartRepository;
import com.kitadevelopers.pos.modules.order.dto.CheckoutRequest;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.repository.ProductRepository;
import com.kitadevelopers.pos.modules.user.entity.User;
import com.kitadevelopers.pos.modules.user.repository.UserRepository;
import com.kitadevelopers.pos.modules.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private OrderService orderService;

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    @Test
    void checkout_throwsWhenCartIsEmpty() {
        mockSecurityContext("cashier@test.com");

        User user = User.builder().id(UUID.randomUUID()).email("cashier@test.com").build();
        Cart cart = Cart.builder().id(UUID.randomUUID()).cartStatus(CartStatus.ACTIVE).build();

        when(userRepository.findByEmail("cashier@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserIdAndCartStatus(user.getId(), CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() ->
                orderService.checkout(new CheckoutRequest(null, null, null), "key-123")
        ).isInstanceOf(RuntimeException.class).hasMessageContaining("empty");
    }

    @Test
    void checkout_throwsWhenStockInsufficient() {
        mockSecurityContext("cashier@test.com");

        UUID productId = UUID.randomUUID();
        User user = User.builder().id(UUID.randomUUID()).email("cashier@test.com").build();
        Cart cart = Cart.builder().id(UUID.randomUUID()).cartStatus(CartStatus.ACTIVE).build();

        Product product = Product.builder()
                .id(productId)
                .name("Mie Goreng")
                .price(new BigDecimal("3000"))
                .stock(1) // only 1 in stock
                .taxRate(BigDecimal.ZERO)
                .build();

        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity(5) // wants 5
                .build();

        when(userRepository.findByEmail("cashier@test.com")).thenReturn(Optional.of(user));
        when(cartRepository.findByUserIdAndCartStatus(user.getId(), CartStatus.ACTIVE))
                .thenReturn(Optional.of(cart));
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(List.of(cartItem));
        when(orderRepository.findByIdempotencyKey("key-123")).thenReturn(Optional.empty());
        when(productRepository.findByIdForUpdate(productId)).thenReturn(Optional.of(product));

        assertThatThrownBy(() ->
                orderService.checkout(new CheckoutRequest(null, null, null), "key-123")
        ).isInstanceOf(RuntimeException.class).hasMessageContaining("Stock not enough");
    }

    @Test
    void checkout_idempotency_returnsSameOrderOnDuplicateKey() {
        mockSecurityContext("cashier@test.com");

        Order existing = Order.builder()
                .orderNumber("ORD-EXISTING")
                .totalAmount(new BigDecimal("15000"))
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByIdempotencyKey("dup-key")).thenReturn(Optional.of(existing));

        var result = orderService.checkout(new CheckoutRequest(null, null, null), "dup-key");

        assertThat(result.orderNumber()).isEqualTo("ORD-EXISTING");
        verify(cartRepository, never()).findByUserIdAndCartStatus(any(), any());
    }
}