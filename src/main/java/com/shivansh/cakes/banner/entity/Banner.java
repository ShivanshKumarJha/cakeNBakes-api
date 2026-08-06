package com.shivansh.cakes.banner.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Banner extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String banner;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 100)
    private String caption;

    private String category;
}