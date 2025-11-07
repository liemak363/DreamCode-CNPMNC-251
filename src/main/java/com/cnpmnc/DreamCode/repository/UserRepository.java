package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserName(String userName);


    boolean existsByUserName(String userName);

    boolean existsByIdAndIsActiveTrue(Integer id);

    boolean existsByUserNameAndIsActiveTrue(String userName);

    Page<User> findAllByIsActiveTrue(Pageable pageable);

    Page<User> findByRoles_Name(String roleName, Pageable pageable);

    Page<User> findByRoles_NameAndIsActiveTrue(String roleName, Pageable pageable);
}
