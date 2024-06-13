package com.threlease.base.functions.pay;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyEntity;
import com.threlease.base.entities.PayLogEntity;
import com.threlease.base.entities.RestaurantEntity;
import com.threlease.base.repositories.PayLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PayService {
    private final PayLogRepository payLogRepository;

    public void save(PayLogEntity data) {
        payLogRepository.save(data);
    }

    public void remove(PayLogEntity data) {
        payLogRepository.delete(data);
    }

    public Optional<PayLogEntity> findOneByUuid(String uuid) {
        return payLogRepository.findOneByUuid(uuid);
    }

    public Page<PayLogEntity> findByAuthorPagination(Pageable pageable, AuthEntity author) {
        return payLogRepository.findByAuthorPagination(pageable, author);
    }

    public Page<PayLogEntity> findByCompanyPagination(Pageable pageable, CompanyEntity company) {
        return payLogRepository.findByCompanyPagination(pageable, company);
    }

    public Page<PayLogEntity> findByRestaurantPagination(Pageable pageable, RestaurantEntity restaurant) {
        return payLogRepository.findByRestaurantPagination(pageable, restaurant);
    }
}
