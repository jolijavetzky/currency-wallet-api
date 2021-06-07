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
     * <p>
     * Whenever we want to just read data and don't encounter dirty reads, we could use PESSIMISTIC_READ (shared lock). We won't be able to make any updates or deletes though.
     * It sometimes happens that the database we use doesn't support the PESSIMISTIC_READ lock, so it's possible that we obtain the PESSIMISTIC_WRITE lock instead.
     *
     * @param id the id
     * @return the wallet
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select w from Wallet w where w.id = :id")
    Wallet findByIdForRead(@Param("id") Long id);

    /**
     * Find by id for write wallet.
     * <p>
     * Any transaction that needs to acquire a lock on data and make changes to it should obtain the PESSIMISTIC_WRITE lock.
     * According to the JPA specification, holding PESSIMISTIC_WRITE lock will prevent other transactions from reading, updating or deleting the data.
     *
     * @param id the id
     * @return the wallet
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.id = :id")
    Wallet findByIdForWrite(@Param("id") Long id);
}
