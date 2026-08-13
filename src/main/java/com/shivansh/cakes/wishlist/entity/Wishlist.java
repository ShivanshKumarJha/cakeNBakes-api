package com.shivansh.cakes.wishlist.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Wishlist extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistItem> wishlistItems = new ArrayList<>();

    public void addWishlistItem(WishlistItem wishlistItem) {
        wishlistItems.add(wishlistItem);
        wishlistItem.setWishlist(this);
    }

    public void removeWishlistItem(WishlistItem wishlistItem) {
        wishlistItems.remove(wishlistItem);
        wishlistItem.setWishlist(null);
    }
}