package com.hmg.repository;


import com.hmg.model.entities.Home;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<Home, Long> {

    Home getHomeById(Long id);
}
