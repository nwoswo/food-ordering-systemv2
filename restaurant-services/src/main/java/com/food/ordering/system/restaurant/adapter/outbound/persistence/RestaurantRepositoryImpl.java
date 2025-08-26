package com.food.ordering.system.restaurant.service.adapter.outbound.persistence;

import com.food.ordering.system.restaurant.service.infrastructure.persistence.mappers.RestaurantDataAccessMapper;
import com.food.ordering.system.restaurant.service.infrastructure.persistence.repositories.RestaurantJpaRepository;
import com.food.ordering.system.restaurant.service.domain.model.entities.Restaurant;
import com.food.ordering.system.restaurant.service.application.ports.out.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataAccessMapper restaurantDataAccessMapper;

    public RestaurantRepositoryImpl(RestaurantJpaRepository restaurantJpaRepository,
                                    RestaurantDataAccessMapper restaurantDataAccessMapper) {
        this.restaurantJpaRepository = restaurantJpaRepository;
        this.restaurantDataAccessMapper = restaurantDataAccessMapper;
    }

    @Override
    public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
        List<UUID> restaurantProducts =
                restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);
        Optional<List<com.food.ordering.system.common.dataaccess.restaurant.entity.RestaurantEntity>> restaurantEntities = restaurantJpaRepository
                .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(),
                        restaurantProducts);
        return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
    }
}
