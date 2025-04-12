package com.onhz.server.repository;

import com.onhz.server.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, Long> {
    List<CommonCodeEntity> findByCode(String code);
}
