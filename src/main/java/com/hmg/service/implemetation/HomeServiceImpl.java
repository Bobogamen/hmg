package com.hmg.service.implemetation;

import com.hmg.model.dto.AddHomeDTO;
import com.hmg.model.dto.EditHomeDTO;
import com.hmg.model.entities.*;
import com.hmg.repository.HomeRepository;
import com.hmg.service.HomeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HomeServiceImpl implements HomeService {

    private final HomeRepository homeRepository;

    public HomeServiceImpl(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    @Override
    public Home getHomeById(long id) {
        return this.homeRepository.getHomeById(id);
    }

    @Override
    public Home addHome(AddHomeDTO addHomeDTO) {

        Home home = new Home();
        home.setFloor(addHomeDTO.getFloor());
        home.setName(addHomeDTO.getName());
        home.setTotalForMonth(0);

        return this.homeRepository.save(home);
    }

    @Override
    public void setOwnerToHome(Home home, Resident owner) {
        home.setOwner(owner);
        this.homeRepository.save(home);
    }

    @Override
    public void setHomeToHomesGroup(Home home, HomesGroup homesGroup) {
        home.setHomesGroup(homesGroup);
        this.homeRepository.save(home);
    }

    @Override
    public void editHome(Home home, EditHomeDTO editHomeDTO) {
        home.setFloor(editHomeDTO.getFloor());
        home.setName(editHomeDTO.getName());

        this.homeRepository.save(home);
    }

    @Override
    public void setFeeToHome(Home home, Fee fee) {

        boolean hasFee = home.getFees().stream().anyMatch(hf -> hf.getFee().getId() == fee.getId());

        if (!hasFee) {
            HomesFee homesFee = new HomesFee();
            homesFee.setFee(fee);
            homesFee.setTimes(1);
            homesFee.setHome(home);

            home.addFee(homesFee);

            home.setTotalForMonth(calculateTotalForMonth(home));
            this.homeRepository.save(home);
        }
    }

    @Override
    public void unsetFeeToHome(Home home, Fee fee) {

        List<HomesFee> feesList = home.getFees();

        if (feesList.size() > 0) {
            boolean hasFee = feesList.stream().anyMatch(hf -> hf.getFee().getId() == fee.getId());
            if (hasFee) {
                feesList = feesList.stream()
                        .filter(hf -> hf.getFee().getId() != fee.getId())
                        .toList();

                home.setFees(feesList);
            }

            home.setTotalForMonth(calculateTotalForMonth(home));
            this.homeRepository.save(home);
        }
    }

    @Override
    public void changeTimesOfFeeById(long homeId, long feeId, int times) {

        Home home = getHomeById(homeId);

        home.getFees().stream().filter(f -> f.getId() == feeId).
                iterator().next().setTimes(times);

        home.setTotalForMonth(calculateTotalForMonth(home));

        this.homeRepository.save(home);
    }

    @Override
    public double calculateTotalForMonth(Home home) {
        return home.getFees().stream().mapToDouble(f -> f.getFee().getValue() * f.getTimes()).sum();
    }

    @Override
    public void changeHomesFee(Map<Home, Boolean> homesMap, Fee fee) {
        for (Map.Entry<Home, Boolean> entry : homesMap.entrySet()) {
            Home home = entry.getKey();
            boolean value = entry.getValue();
            if (value) {
                setFeeToHome(home, fee);
            } else {
                unsetFeeToHome(home, fee);
            }
        }
    }
}
