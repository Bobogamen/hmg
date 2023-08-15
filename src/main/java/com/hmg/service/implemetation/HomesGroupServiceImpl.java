package com.hmg.service.implemetation;

import com.hmg.model.dto.AddHomesGroupDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.HomesGroup;
import com.hmg.model.entities.User;
import com.hmg.repository.HomesGroupRepository;
import com.hmg.service.HomesGroupService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class HomesGroupServiceImpl implements HomesGroupService {

    private final HomesGroupRepository homesGroupRepository;

    public HomesGroupServiceImpl(HomesGroupRepository homesGroupRepository) {
        this.homesGroupRepository = homesGroupRepository;
    }

    @Override
    public HomesGroup getHomesGroupById(long id) {
        return this.homesGroupRepository.getHomesGroupById(id);
    }

    @Override
    public HomesGroup addHomesGroup(AddHomesGroupDTO addHomesGroupDTO) {

        HomesGroup homesGroup = new HomesGroup();
        homesGroup.setName(addHomesGroupDTO.getName());
        homesGroup.setType(addHomesGroupDTO.getType());
        homesGroup.setSize(addHomesGroupDTO.getSize());
        homesGroup.setStartPeriod(addHomesGroupDTO.getStartPeriod());
        homesGroup.setBackgroundColor(addHomesGroupDTO.getBackgroundColor());

        return this.homesGroupRepository.save(homesGroup);
    }

    @Override
    public void editHomesGroup(AddHomesGroupDTO addHomesGroupDTO, long homesGroupId) {
        HomesGroup homesGroup = this.homesGroupRepository.getHomesGroupById(homesGroupId);

        homesGroup.setName(addHomesGroupDTO.getName());
        homesGroup.setSize(addHomesGroupDTO.getSize());
        homesGroup.setBackgroundColor(addHomesGroupDTO.getBackgroundColor());

        this.homesGroupRepository.save(homesGroup);
    }

    @Override
    public void homesGroupAssignment(List<HomesGroup> managerHomesGroup, User cashier, List<Long> homesGroupsIds) {

        List<HomesGroup> updated = new ArrayList<>();

        if (homesGroupsIds != null) {

            managerHomesGroup.forEach(hg -> {
                boolean isFound = cashier.getHomesGroups().stream().anyMatch(uhg -> uhg.getId() == hg.getId());
                boolean isRequested = homesGroupsIds.stream().anyMatch(id -> id == hg.getId());

                if (isFound) {
                    if (!isRequested) {
                        hg.removeUser(cashier);
                        updated.add(hg);

                    }
                } else {
                    if (isRequested) {
                        hg.addUser(cashier);
                        updated.add(hg);
                    }
                }
            });
        } else  {
            managerHomesGroup.forEach(hg -> {
                hg.removeUser(cashier);
                updated.add(hg);
            });
        }
        this.homesGroupRepository.saveAll(updated);
    }
}
