package com.hmg.service;

import com.hmg.model.dto.AddHomesGroupDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.User;

import java.util.List;

public interface HomesGroupService {
    HomesGroup getHomesGroupById(long id);

    HomesGroup addHomesGroup(AddHomesGroupDTO addHomesGroupDTO);

    void editHomesGroup(AddHomesGroupDTO addHomesGroupDTO, long homesGroupId);

    void homesGroupAssignment(List<HomesGroup> managerHomesGroup, User cashier, List<Long> homesGroupsIds);

}
