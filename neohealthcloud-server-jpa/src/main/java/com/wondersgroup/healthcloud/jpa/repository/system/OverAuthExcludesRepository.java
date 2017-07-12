package com.wondersgroup.healthcloud.jpa.repository.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wondersgroup.healthcloud.jpa.entity.system.OverAuthExcludes;

public interface OverAuthExcludesRepository extends JpaRepository<OverAuthExcludes, Integer> {
    
    List<String> queryExcludesPathByType(String type);
}
