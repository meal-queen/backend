package com.threlease.base.repositories;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyConnectEntity;
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
public interface CompanyConnectRepository extends JpaRepository<CompanyConnectEntity, String> {
    @Query("SELECT u FROM CompanyConnectEntity u WHERE u.uuid = :uuid")
    Optional<CompanyConnectEntity> findOneByUuid(@Param("uuid") String uuid);

    @Query("SELECT u FROM CompanyConnectEntity u WHERE u.company = :company")
    List<CompanyConnectEntity> findByCompany(@Param("company") CompanyEntity company);

    @Query("SELECT u FROM CompanyConnectEntity u WHERE u.author = :author AND u.company = :company")
    Optional<CompanyConnectEntity> findByAuthor(@Param("company") CompanyEntity company, @Param("author") AuthEntity author);

    @Query("SELECT u FROM CompanyConnectEntity u WHERE u.company = :company")
    Page<CompanyConnectEntity> findByCompanyPagination(Pageable pageable, @Param("company") CompanyEntity company);
}
