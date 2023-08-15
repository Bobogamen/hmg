package com.hmg.service;

import com.hmg.model.dto.AddFeeDTO;
import com.hmg.model.entities.Fee;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;

import java.util.List;

public interface FeeService {
    Fee getFeeById(long feeId);

    Fee addFee(AddFeeDTO addFeeDTO);

    void setFeeToHomesGroup(Fee fee, HomesGroup homesGroup);

    Fee changeFee(Fee fee, AddFeeDTO addFeeDTO);

    List<Home> getAllHomesByFeeId(long feeId);
}
