
package com.example.demo.repository.spec;

import com.example.demo.entities.ActiveStatus;
import com.example.demo.entities.Product;
import com.example.demo.dto.request.ProductFilterCriteria;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    private ProductSpecification() {}

    public static Specification<Product> matching(ProductFilterCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("productType").get("category").get("id"), c.getCategoryId()));
            }
            if (c.getProductTypeId() != null) {
                predicates.add(cb.equal(root.get("productType").get("id"), c.getProductTypeId()));
            }
            if (c.getVendorId() != null) {
                predicates.add(cb.equal(root.get("vendor").get("id"), c.getVendorId()));
            }
            if (c.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), c.getMinPrice()));
            }
            if (c.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), c.getMaxPrice()));
            }
            if (c.isActiveOnly()) {
                predicates.add(cb.equal(root.get("activeStatus"), ActiveStatus.ACTIVE));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
