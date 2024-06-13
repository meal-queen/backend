package com.threlease.base.repositories;

import com.threlease.base.entities.RestaurantEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, String> {
    @Query("SELECT u FROM RestaurantEntity u WHERE u.uuid = :uuid")
    Optional<RestaurantEntity> findOneByUuid(@Param("uuid") String uuid);

    @Query("SELECT u FROM RestaurantEntity u WHERE u.name = :name")
    Optional<RestaurantEntity> findOneByName(@Param("name") String name);

    @Query("SELECT u FROM RestaurantEntity u WHERE u.name LIKE %:name% ORDER BY u.createdAt")
    Page<RestaurantEntity> findByNameLike(Pageable pageable, @Param("name") String name);

    @Query("SELECT u FROM RestaurantEntity u WHERE u.name LIKE %:name% OR u.address LIKE %:address% ORDER BY u.createdAt")
    Page<RestaurantEntity> findByNameLikeOrLikeAddress(
            Pageable pageable,
            @Param("name") String name,
            @Param("address") String address
    );

    @Query("SELECT u FROM RestaurantEntity u ORDER BY u.createdAt")
    Page<RestaurantEntity> findByPagination(Pageable pageable);

    @Query("SELECT u FROM RestaurantEntity u WHERE u.bizNumber = :bizNumber")
    Optional<RestaurantEntity> findOneByBizNumber(@Param("bizNumber") String bizNumber);
}
