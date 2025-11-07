package com.cnpmnc.DreamCode.repository;

import com.cnpmnc.DreamCode.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    Optional<Supplier> findByName(String name);

    boolean existsByName(String name);

    boolean existsByIdAndIsActiveTrue(Integer id);

    boolean existsByNameAndIsActiveTrue(String name);

    // Custom query to count assets associated with a supplier
    @Query("SELECT COUNT(a) FROM Asset a WHERE a.supplier.id = :supplierId")
    long countAssetsBySupplierId(@Param("supplierId") Integer supplierId);

    Page<Supplier> findAllByIsActiveTrue(Pageable pageable);
}
