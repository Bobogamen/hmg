package com.hmg.repository;

import com.hmg.model.entities.HomesGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface HomesGroupRepository extends JpaRepository<HomesGroup, Long> {

    HomesGroup getHomesGroupById(long id);
}