package com.example.ueberholserver.db.repo;

import com.example.ueberholserver.db.entity.RideEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<RideEntity, String> {}