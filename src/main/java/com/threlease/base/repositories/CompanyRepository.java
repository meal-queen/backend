package com.threlease.base.repositories;

import com.threlease.base.entities.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
    @Query("SELECT u FROM CompanyEntity u WHERE u.uuid = :uuid")
    Optional<CompanyEntity> findOneByUUID(@Param("uuid") String uuid);

    @Query("SELECT u FROM CompanyEntity u WHERE u.name = :name")
    List<CompanyEntity> findByName(@Param("name") String name);

    @Query("SELECT u FROM CompanyEntity u WHERE u.name LIKE %:name% ORDER BY u.createdAt")
    Page<CompanyEntity> findByNameLike(Pageable pageable, @Param("name") String name);

    @Query("SELECT u FROM CompanyEntity u ORDER BY u.createdAt")
    Page<CompanyEntity> findByPagination(Pageable pageable);

    @Query("SELECT u FROM CompanyEntity u WHERE u.bizNumber = :bizNumber")
    Optional<CompanyEntity> findOneByBizNumber(@Param("bizNumber") String bizNumber);

    @Query("SELECT u FROM CompanyEntity u WHERE u.inviteCode = :inviteCode")
    Optional<CompanyEntity> findOneByInviteCode(@Param("inviteCode") String inviteCode);
}
