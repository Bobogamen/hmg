package com.hmg.repository;

import com.hmg.model.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentsRepository extends JpaRepository<Payment, Long> {

    List<Payment> findPaymentByMonthHomesId(long monthHomesId);
}
