package com.food.ordering.system.payment.service.adapter.outbound.persistence;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.infrastructure.persistence.mappers.CreditHistoryDataAccessMapper;
import com.food.ordering.system.payment.service.infrastructure.persistence.repositories.CreditHistoryJpaRepository;
import com.food.ordering.system.payment.service.domain.model.entities.CreditHistory;
import com.food.ordering.system.payment.service.application.ports.out.CreditHistoryRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    private final CreditHistoryJpaRepository creditHistoryJpaRepository;
    private final CreditHistoryDataAccessMapper creditHistoryDataAccessMapper;

    public CreditHistoryRepositoryImpl(CreditHistoryJpaRepository creditHistoryJpaRepository,
                                       CreditHistoryDataAccessMapper creditHistoryDataAccessMapper) {
        this.creditHistoryJpaRepository = creditHistoryJpaRepository;
        this.creditHistoryDataAccessMapper = creditHistoryDataAccessMapper;
    }

    @Override
    public CreditHistory save(CreditHistory creditHistory) {
        return creditHistoryDataAccessMapper
                .creditHistoryEntityToCreditHistory(creditHistoryJpaRepository
                        .save(creditHistoryDataAccessMapper.creditHistoryToCreditHistoryEntity(creditHistory)));
    }

    @Override
    public List<CreditHistory> findByCustomerId(CustomerId customerId) {
        Optional<List<com.food.ordering.system.payment.service.infrastructure.persistence.entities.CreditHistoryEntity>> creditHistoryEntities =
                creditHistoryJpaRepository.findByCustomerId(customerId.getValue());
        return creditHistoryEntities.map(creditHistoryEntityList -> creditHistoryEntityList.stream()
                .map(creditHistoryDataAccessMapper::creditHistoryEntityToCreditHistory)
                .toList()).orElse(List.of());
    }
}
