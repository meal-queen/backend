package com.threlease.base.functions.company;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyConnectEntity;
import com.threlease.base.entities.CompanyEntity;
import com.threlease.base.repositories.CompanyConnectRepository;
import com.threlease.base.repositories.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyConnectRepository companyConnectRepository;

    public CompanyService(
            CompanyRepository companyRepository, CompanyConnectRepository companyConnectRepository
    ) {
        this.companyRepository = companyRepository;
        this.companyConnectRepository = companyConnectRepository;
    }

    public void companySave(CompanyEntity data) {
        companyRepository.save(data);
    }

    public void connectSave(CompanyConnectEntity data) {
        companyConnectRepository.save(data);
    }

    public void companyRemove(CompanyEntity data) {
        companyRepository.delete(data);
    }

    public void connectRemove(CompanyConnectEntity data) {
        companyConnectRepository.delete(data);
    }

    public Optional<CompanyEntity> findOneByUuid(String uuid) {
        return companyRepository.findOneByUUID(uuid);
    }

    public Optional<CompanyConnectEntity> findOneByConnectUuid (String uuid) {
        return companyConnectRepository.findOneByUuid(uuid);
    }

    public List<CompanyConnectEntity> findByConnectCompany(CompanyEntity data) {
        return companyConnectRepository.findByCompany(data);
    }

    public Page<CompanyConnectEntity> findOneByConnectCompanyPagination(Pageable pageable, CompanyEntity data) {
        return companyConnectRepository.findByCompanyPagination(pageable, data);
    }

    public Optional<CompanyConnectEntity> findOneByConnectAuthor(CompanyEntity company, AuthEntity author) {
        return companyConnectRepository.findByAuthor(company, author);
    }

    public List<CompanyEntity> findByName(String name) {
        return companyRepository.findByName(name);
    }

    public Optional<CompanyEntity> findOneByBizNumber(String bizNumber) {
        return companyRepository.findOneByBizNumber(bizNumber);
    }

    public Page<CompanyEntity> findByNameLike(Pageable pageable, String name) {
        return companyRepository.findByNameLike(pageable, name);
    }

    public Page<CompanyEntity> findByPagination(Pageable pageable) {
        return companyRepository.findByPagination(pageable);
    }

    public Optional<CompanyEntity> findOneByInviteCode(String inviteCode) {
        return companyRepository.findOneByInviteCode(inviteCode);
    }
}
