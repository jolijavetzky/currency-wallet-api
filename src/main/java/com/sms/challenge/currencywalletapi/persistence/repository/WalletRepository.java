package com.sms.challenge.currencywalletapi.persistence.repository;

import com.sms.challenge.currencywalletapi.persistence.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

/**
 * The interface Wallet repository.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Find by id for read wallet.
     *
     * @param id the id
     * @return the wallet
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select w from Wallet w where w.id = :id")
    Wallet findByIdForRead(@Param("id") Long id);

    /**
     * Find by id for write wallet.
     *
     * @param id the id
     * @return the wallet
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Wallet findByIdForWrite(@Param("id") Long id);
}
