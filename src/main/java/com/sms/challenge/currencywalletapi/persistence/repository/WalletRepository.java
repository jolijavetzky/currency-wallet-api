package com.sms.challenge.currencywalletapi.persistence.repository;

import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The interface Wallet repository.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
