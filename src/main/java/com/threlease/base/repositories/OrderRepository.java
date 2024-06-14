package com.threlease.base.repositories;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyEntity;
import com.threlease.base.entities.OrderEntity;
import com.threlease.base.entities.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {
    @Query("SELECT u FROM OrderEntity u WHERE u.uuid = :uuid ORDER BY u.createdAt")
    Optional<OrderEntity> findOneByUuid(@Param("uuid") String uuid);

    @Query("SELECT u FROM OrderEntity u WHERE u.restaurant = :restaurant ORDER BY u.createdAt")
    Page<OrderEntity> findByRestaurantPagination(Pageable pageable, @Param("restaurant") RestaurantEntity restaurant);

    @Query("SELECT u FROM OrderEntity u WHERE u.company = :company ORDER BY u.createdAt")
    Page<OrderEntity> findByCompanyPagination(Pageable pageable, @Param("company") CompanyEntity company);

    @Query("SELECT u FROM OrderEntity u WHERE u.author = :author ORDER BY u.createdAt")
    Page<OrderEntity> findByAuthorPagination(Pageable pageable, @Param("author") AuthEntity author);
}
