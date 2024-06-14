package com.threlease.base.functions.pay;

import com.threlease.base.entities.AuthEntity;
import com.threlease.base.entities.CompanyEntity;
import com.threlease.base.entities.OrderEntity;
import com.threlease.base.entities.RestaurantEntity;
import com.threlease.base.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PayService {
    private final OrderRepository payLogRepository;

    public void save(OrderEntity data) {
        payLogRepository.save(data);
    }

    public void remove(OrderEntity data) {
        payLogRepository.delete(data);
    }

    public Optional<OrderEntity> findOneByUuid(String uuid) {
        return payLogRepository.findOneByUuid(uuid);
    }

    public Page<OrderEntity> findByAuthorPagination(Pageable pageable, AuthEntity author) {
        return payLogRepository.findByAuthorPagination(pageable, author);
    }

    public Page<OrderEntity> findByCompanyPagination(Pageable pageable, CompanyEntity company) {
        return payLogRepository.findByCompanyPagination(pageable, company);
    }

    public Page<OrderEntity> findByRestaurantPagination(Pageable pageable, RestaurantEntity restaurant) {
        return payLogRepository.findByRestaurantPagination(pageable, restaurant);
    }
}
