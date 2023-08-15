package com.hmg.service.implemetation;

import com.hmg.model.dto.FeePaymentDTO;
import com.hmg.model.dto.HomePaymentsDTO;
import com.hmg.model.entities.MonthHomes;
import com.hmg.model.entities.Payment;
import com.hmg.repository.FeeRepository;
import com.hmg.repository.PaymentsRepository;
import com.hmg.service.PaymentsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentsRepository paymentsRepository;
    private final FeeRepository feeRepository;

    public PaymentsServiceImpl(PaymentsRepository paymentsRepository, FeeRepository feeRepository) {
        this.paymentsRepository = paymentsRepository;
        this.feeRepository = feeRepository;
    }

    @Override
    public List<HomePaymentsDTO> getPaymentsByHome(MonthHomes monthHome) {
        return monthHome.getHome().getFees().stream().map(f -> {
            HomePaymentsDTO homePaymentsDTO = new HomePaymentsDTO();

            homePaymentsDTO.setId(f.getFee().getId());
            homePaymentsDTO.setName(f.getFee().getName());
            homePaymentsDTO.setValue(f.getFee().getValue());
            homePaymentsDTO.setTimes(f.getTimes());
            homePaymentsDTO.calculateTotal();

            return homePaymentsDTO;
        }).toList();
    }

    @Override
    public double makePayments(MonthHomes monthHome, FeePaymentDTO[] feePaymentDTOS) {
        List<Payment> payments = new ArrayList<>();

        for (FeePaymentDTO feePaymentDTO : feePaymentDTOS) {
            Payment payment = new Payment();

            payment.setFeeValue(feePaymentDTO.getValue());
            payment.setTimesPaid(feePaymentDTO.getTimesPaid());
            payment.setValuePaid(feePaymentDTO.getTotal());
            payment.setFee(this.feeRepository.getFeeById(feePaymentDTO.getId()));
            payment.setMonthHomes(monthHome);

            payments.add(payment);
        }

        this.paymentsRepository.saveAll(payments);

        return payments.stream().mapToDouble(Payment::getValuePaid).sum();
    }

    @Override
    public List<HomePaymentsDTO> viewPaymentsByHome(MonthHomes monthHome) {

        List<HomePaymentsDTO> payments = new ArrayList<>();

        monthHome.getPayments().forEach(p -> {
            HomePaymentsDTO homePaymentsDTO = new HomePaymentsDTO();

            homePaymentsDTO.setName(p.getFee().getName());
            homePaymentsDTO.setValue(p.getFeeValue());
            homePaymentsDTO.setTimes(p.getTimesPaid());
            homePaymentsDTO.setTotal(p.getValuePaid());

            payments.add(homePaymentsDTO);
        });

        return payments;
    }
}
