package com.kitadevelopers.pos.modules.cart.repository;

import com.kitadevelopers.pos.modules.cart.entity.Cart;
import com.kitadevelopers.pos.modules.cart.enums.CartStatus;
import com.kitadevelopers.pos.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserAndCartStatus(User user, CartStatus cart);
    Optional<Cart> findByUserIdAndCartStatus(UUID userId, CartStatus status);
    Optional<Cart> findByIdAndUser(UUID cartId, User user);
}
