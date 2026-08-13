package com.shivansh.cakes.address.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import com.shivansh.cakes.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Address extends BaseEntity {

    @Column(nullable = false, length = 25)
    private String name;

    @Column(nullable = false, length = 25)
    private String houseName;

    @Column(nullable = false, length = 50)
    private String street;

    private String landMark;

    @Column(nullable = false)
    private String pinCode;

    @Column(nullable = false)
    private String district;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private String contact;

    private Boolean selected;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}