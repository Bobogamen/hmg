package com.hmg.repository;

import com.hmg.model.entities.Fee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    Fee getFeeById(Long id);
}
