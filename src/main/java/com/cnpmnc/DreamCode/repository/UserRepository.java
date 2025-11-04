package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
