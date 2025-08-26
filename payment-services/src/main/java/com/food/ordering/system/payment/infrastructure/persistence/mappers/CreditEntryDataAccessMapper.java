package com.food.ordering.system.payment.service.infrastructure.persistence.mappers;

import com.food.ordering.system.common.domain.valueobject.CustomerId;
import com.food.ordering.system.common.domain.valueobject.Money;
import com.food.ordering.system.payment.service.infrastructure.persistence.entities.CreditEntryEntity;
import com.food.ordering.system.payment.service.domain.model.entities.CreditEntry;
import com.food.ordering.system.payment.service.domain.model.valueobjects.CreditEntryId;
import org.springframework.stereotype.Component;

@Component
public class CreditEntryDataAccessMapper {

    public CreditEntryEntity creditEntryToCreditEntryEntity(CreditEntry creditEntry) {
        return CreditEntryEntity.builder()
                .id(creditEntry.getId().getValue())
                .customerId(creditEntry.getCustomerId().getValue())
                .totalCreditAmount(creditEntry.getTotalCreditAmount().getAmount())
                .build();
    }

    public CreditEntry creditEntryEntityToCreditEntry(CreditEntryEntity creditEntryEntity) {
        return CreditEntry.builder()
                .creditEntryId(new CreditEntryId(creditEntryEntity.getId()))
                .customerId(new CustomerId(creditEntryEntity.getCustomerId()))
                .totalCreditAmount(new Money(creditEntryEntity.getTotalCreditAmount()))
                .build();
    }
}
