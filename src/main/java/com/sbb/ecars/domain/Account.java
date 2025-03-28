package com.sbb.ecars.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @Column(length = 16, nullable = false)
    private String id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 254, nullable = false)
    private String email;

    @Column(length = 200, nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdmin = false;
}
