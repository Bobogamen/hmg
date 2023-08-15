package com.hmg.service.implemetation;

import com.hmg.model.dto.AddFeeDTO;
import com.hmg.model.entities.Fee;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.repository.FeeRepository;
import com.hmg.service.FeeService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class FeeServiceImpl implements FeeService {

    private final FeeRepository feeRepository;

    public FeeServiceImpl(FeeRepository feeRepository) {
        this.feeRepository = feeRepository;
    }

    @Override
    public Fee getFeeById(long feeId) {
        return this.feeRepository.getFeeById(feeId);
    }

    @Override
    public Fee addFee(AddFeeDTO addFeeDTO) {

        Fee fee = new Fee();
        fee.setName(addFeeDTO.getName());
        fee.setValue(addFeeDTO.getValue());
        fee.setAddedOn(LocalDate.now());

        return this.feeRepository.save(fee);
    }

    @Override
    public void setFeeToHomesGroup(Fee fee, HomesGroup homesGroup) {
        fee.setHomesGroup(homesGroup);
        this.feeRepository.save(fee);
    }

    @Override
    public Fee changeFee(Fee fee, AddFeeDTO addFeeDTO) {
        fee.setName(addFeeDTO.getName());
        fee.setValue(addFeeDTO.getValue());

        return this.feeRepository.save(fee);
    }

    @Override
    public List<Home> getAllHomesByFeeId(long feeId) {
        Fee fee = this.feeRepository.getFeeById(feeId);

        return new ArrayList<>();

    }
}
