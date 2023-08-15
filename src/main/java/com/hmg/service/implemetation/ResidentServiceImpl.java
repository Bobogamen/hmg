package com.hmg.service.implemetation;

import com.hmg.model.dto.AddHomeDTO;
import com.hmg.model.dto.AddResidentDTO;
import com.hmg.model.entities.Home;
import com.hmg.model.entities.Resident;
import com.hmg.repository.ResidentRepository;
import com.hmg.service.ResidentService;
import org.springframework.stereotype.Service;

@Service
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;

    public ResidentServiceImpl(ResidentRepository residentRepository) {
        this.residentRepository = residentRepository;
    }

    @Override
    public Resident getResidentById(long residentId) {
        return this.residentRepository.getResidentById(residentId);
    }

    @Override
    public Resident addOwner(AddHomeDTO addHomeDTO) {

        Resident owner = new Resident();
        owner.setFirstName(addHomeDTO.getOwnerFirstName());
        owner.setMiddleName(addHomeDTO.getOwnerMiddleName());
        owner.setLastName(addHomeDTO.getOwnerLastName());
        owner.setEmail(addHomeDTO.getOwnerEmail());
        owner.setPhoneNumber(addHomeDTO.getOwnerPhoneNumber());

        return this.residentRepository.save(owner);
    }

    @Override
    public Resident addResident(AddHomeDTO addHomeDTO) {

        Resident resident = new Resident();
        resident.setFirstName(addHomeDTO.getResidentFirstName());
        resident.setMiddleName(addHomeDTO.getResidentMiddleName());
        resident.setLastName(addHomeDTO.getResidentLastName());
        resident.setEmail(addHomeDTO.getResidentEmail());
        resident.setPhoneNumber(addHomeDTO.getResidentPhoneNumber());

        return this.residentRepository.save(resident);
    }

    @Override
    public Resident addResident(AddResidentDTO addResidentDTO) {

        Resident resident = new Resident();
        resident.setFirstName(addResidentDTO.getFirstName());
        resident.setMiddleName(addResidentDTO.getMiddleName());
        resident.setLastName(addResidentDTO.getLastName());
        resident.setEmail(addResidentDTO.getEmail());
        resident.setPhoneNumber(addResidentDTO.getPhoneNumber());

        return this.residentRepository.save(resident);
    }

    @Override
    public void addResidentToHome(Resident resident, Home home) {
        resident.setHome(home);
        this.residentRepository.save(resident);
    }

    @Override
    public void editResident(AddResidentDTO addResidentDTO, long id, Home home, boolean isResident) {
        Resident resident = this.residentRepository.getResidentById(id);

        resident.setFirstName(addResidentDTO.getFirstName());
        resident.setMiddleName(addResidentDTO.getMiddleName());
        resident.setLastName(addResidentDTO.getLastName());
        resident.setEmail(addResidentDTO.getEmail());
        resident.setPhoneNumber(addResidentDTO.getPhoneNumber());

        if (!isResident) {
            resident.setHome(null);
        } else {
            resident.setHome(home);
        }

        this.residentRepository.save(resident);
    }

    @Override
    public void deleteResidentById(long residentId) {
        this.residentRepository.delete(this.residentRepository.getResidentById(residentId));
    }
}

