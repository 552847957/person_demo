package com.wondersgroup.healthcloud.jpa.repository.doctor;

import com.wondersgroup.healthcloud.jpa.entity.dic.DepartGB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by shenbin on 16/8/9.
 */
public interface DepartGBRepository extends JpaRepository<DepartGB, String> {


}
