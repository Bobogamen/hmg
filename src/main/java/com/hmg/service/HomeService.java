package com.hmg.service;

import com.hmg.model.dto.AddHomeDTO;
import com.hmg.model.dto.EditHomeDTO;
import com.hmg.model.entities.*;

import java.util.Map;

public interface HomeService {
    Home getHomeById(long id);

    Home addHome(AddHomeDTO addHomeDTO);

    void setOwnerToHome(Home home, Resident owner);

    void setHomeToHomesGroup(Home home, HomesGroup homesGroup);

    void setFeeToHome(Home home, Fee fee);

    void unsetFeeToHome(Home home, Fee fee);

    void changeTimesOfFeeById(long homeId, long feeId, int times);

    double calculateTotalForMonth(Home home);

    void changeHomesFee(Map<Home, Boolean> homesMap, Fee fee);

    void editHome(Home home, EditHomeDTO editHomeDTO);
}
