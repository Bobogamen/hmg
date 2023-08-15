package com.hmg.service;

import com.hmg.model.dto.AddHomeDTO;
import com.hmg.model.dto.AddResidentDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.Resident;

public interface ResidentService {
    Resident getResidentById(long residentId);

    Resident addOwner(AddHomeDTO addHomeDTO);

    Resident addResident(AddHomeDTO addHomeDTO);

    Resident addResident(AddResidentDTO addResidentDTO);

    void addResidentToHome(Resident resident, Home home);

    void editResident(AddResidentDTO addResidentDTO, long id, Home home, boolean isResident);

    void deleteResidentById(long residentId);
}
