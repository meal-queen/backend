package com.threlease.base.functions.restaurant;

import com.threlease.base.entities.*;
import com.threlease.base.repositories.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public void restaurantSave(RestaurantEntity data) {
        restaurantRepository.save(data);
    }

    public void restaurantRemove(RestaurantEntity data) {
        restaurantRepository.delete(data);
    }

    public Optional<RestaurantEntity> findOneByUuid(String uuid) {
        return restaurantRepository.findOneByUuid(uuid);
    }

    public Optional<RestaurantEntity> findOneByName(String name) {
        return restaurantRepository.findOneByName(name);
    }

    public Optional<RestaurantEntity> findOneByBizNumber(String bizNumber) {
        return restaurantRepository.findOneByBizNumber(bizNumber);
    }

    public Page<RestaurantEntity> findByNameLike(Pageable pageable, String name) {
        return restaurantRepository.findByNameLike(pageable, name);
    }

    public Page<RestaurantEntity> findByPagination(Pageable pageable) {
        return restaurantRepository.findByPagination(pageable);
    }

    public Page<RestaurantEntity> findByNameLikeOrLikeAddress(Pageable pageable, String name, String address) {
        return restaurantRepository.findByNameLikeOrLikeAddress(pageable, name, address);
    }
}
