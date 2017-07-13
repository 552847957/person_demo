package com.wondersgroup.healthcloud.jpa.repository.system;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wondersgroup.healthcloud.jpa.entity.system.OverAuthExcludes;

public interface OverAuthExcludesRepository extends JpaRepository<OverAuthExcludes, Integer> {
    @Query(nativeQuery = true,value="SELECT t.excludesPath FROM tb_overauth_excludes t WHERE t.type=?1")
    List<String> queryExcludesPathByType(String type);
}
