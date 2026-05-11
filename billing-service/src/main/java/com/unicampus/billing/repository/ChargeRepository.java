package com.unicampus.billing.repository;

import com.unicampus.billing.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChargeRepository extends JpaRepository<Charge, UUID> {
    List<Charge> findByAccount_AccountId(UUID accountId);
}
