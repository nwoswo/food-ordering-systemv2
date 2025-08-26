package com.food.ordering.system.payment.service.adapter.outbound.persistence;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.infrastructure.persistence.mappers.CreditEntryDataAccessMapper;
import com.food.ordering.system.payment.service.infrastructure.persistence.repositories.CreditEntryJpaRepository;
import com.food.ordering.system.payment.service.domain.model.entities.CreditEntry;
import com.food.ordering.system.payment.service.application.ports.out.CreditEntryRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CreditEntryRepositoryImpl implements CreditEntryRepository {

    private final CreditEntryJpaRepository creditEntryJpaRepository;
    private final CreditEntryDataAccessMapper creditEntryDataAccessMapper;

    public CreditEntryRepositoryImpl(CreditEntryJpaRepository creditEntryJpaRepository,
                                     CreditEntryDataAccessMapper creditEntryDataAccessMapper) {
        this.creditEntryJpaRepository = creditEntryJpaRepository;
        this.creditEntryDataAccessMapper = creditEntryDataAccessMapper;
    }

    @Override
    public CreditEntry save(CreditEntry creditEntry) {
        return creditEntryDataAccessMapper
                .creditEntryEntityToCreditEntry(creditEntryJpaRepository
                        .save(creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)));
    }

    @Override
    public Optional<CreditEntry> findByCustomerId(CustomerId customerId) {
        return creditEntryJpaRepository
                .findByCustomerId(customerId.getValue())
                .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry);
    }
}
