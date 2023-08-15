package com.hmg.service;

import com.hmg.model.dto.AddBillDTO;
import com.hmg.model.dto.PayBillDTO;
import com.hmg.model.entities.Bill;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Month;

import java.util.List;

public interface BillService {

    void addBill(AddBillDTO addBillDTO, HomesGroup homesGroup);

    Bill getBillById(long billId);

    void editBill(String name, Bill bill);

    void payBill(long billId, PayBillDTO payBillDTO, Month month);

    List<Bill> findUnpaidBillsByMonthId(long monthId);
}
