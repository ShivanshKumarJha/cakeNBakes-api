package com.shivansh.cakes.wishlist.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_wishlist_product",
                columnNames = {"wishlist_id", "product_id"}
        )
})
public class WishlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}
