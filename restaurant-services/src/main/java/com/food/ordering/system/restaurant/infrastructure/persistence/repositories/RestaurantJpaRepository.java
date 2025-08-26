package com.food.ordering.system.restaurant.service.infrastructure.persistence.repositories;

import com.food.ordering.system.common.dataaccess.restaurant.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, UUID> {

    Optional<List<RestaurantEntity>> findByRestaurantIdAndProductIdIn(UUID restaurantId, List<UUID> productIds);
}
