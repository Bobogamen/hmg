package com.hmg.repository;

import com.hmg.model.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Bill getBillById(long billId);

    @Query("SELECT b FROM Bill b JOIN BillsPaid bp ON b.id = bp.bill.id")
    List<Bill> findAllUnpaidBillsByMonthId(long monthId);
}
