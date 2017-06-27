package com.wondersgroup.healthcloud.jpa.repository.user;

import com.wondersgroup.healthcloud.jpa.entity.user.TubeDiabetes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by limenghua on 2017/6/27.
 *
 * @author limenghua
 */
public interface TubeDiabetesRepository extends JpaRepository<TubeDiabetes, String> {

    TubeDiabetes queryFirst1ByZjhm(String zjhm);

}
