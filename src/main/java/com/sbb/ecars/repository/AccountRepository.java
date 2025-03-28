package com.sbb.ecars.repository;

import com.sbb.ecars.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByEmail(String email);

    boolean existsById(String id);

    boolean existsByEmail(String email);
}
