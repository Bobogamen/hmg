package com.hmg.service;

import com.hmg.model.dto.FeePaymentDTO;
import com.hmg.model.dto.HomePaymentsDTO;
import com.hmg.model.entities.MonthHomes;

import java.util.List;

public interface PaymentsService {
    List<HomePaymentsDTO> getPaymentsByHome(MonthHomes monthHome);

    double makePayments(MonthHomes monthHome, FeePaymentDTO[] feePaymentDTOS);

    List<HomePaymentsDTO> viewPaymentsByHome(MonthHomes monthHome);
}
