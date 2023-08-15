package com.hmg.service.implemetation;

import com.hmg.model.dto.AddBillDTO;
import com.hmg.model.dto.PayBillDTO;
import com.hmg.model.entities.Bill;
import com.hmg.model.entities.BillsPaid;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.Month;
import com.hmg.repository.BillRepository;
import com.hmg.service.BillService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    private final BillRepository billRepository;

    public BillServiceImpl(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @Override
    public void addBill(AddBillDTO addBillDTO, HomesGroup homesGroup) {

        Bill bill = new Bill();
        bill.setAddedOn(LocalDate.now());
        bill.setName(addBillDTO.getName());
        bill.setHomesGroup(homesGroup);

        this.billRepository.save(bill);
    }

    @Override
    public Bill getBillById(long billId) {
        return this.billRepository.getBillById(billId);
    }

    @Override
    public void editBill(String name, Bill bill) {
        bill.setName(name);
        this.billRepository.save(bill);
    }

    @Override
    public void payBill(long billId, PayBillDTO payBillDTO, Month month) {
        Bill bill = this.getBillById(billId);

        BillsPaid billsPaid = new BillsPaid();
        billsPaid.setPaidOn(LocalDate.now());
        billsPaid.setValue(payBillDTO.getBillValue());
        billsPaid.setDocumentNumber(payBillDTO.getBillDocumentNumber());
        billsPaid.setDocumentDate(payBillDTO.getBillDocumentDate());
        billsPaid.setBill(bill);
        billsPaid.setMonth(month);

        bill.setBillsPaid(billsPaid);

        this.billRepository.save(bill);
    }

    @Override
    public List<Bill> findUnpaidBillsByMonthId(long monthId) {
        return this.billRepository.findAllUnpaidBillsByMonthId(monthId);

    }
}
