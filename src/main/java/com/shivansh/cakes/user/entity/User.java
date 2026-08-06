package com.shivansh.cakes.user.entity;

import com.shivansh.cakes.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_tbl")
public class User extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private Long contactNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean isVerified;

    private String image;

    @Column(nullable = false)
    private Boolean status;
}