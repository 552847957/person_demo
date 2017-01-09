package com.wondersgroup.healthcloud.services.localspread.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.spread.Evangelist;
import com.wondersgroup.healthcloud.jpa.repository.spread.EvangelistRepository;
import com.wondersgroup.healthcloud.services.localspread.LocalSpreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by zhaozhenxing on 2017/1/9.
 */
@Service
public class LocalSpreadServiceImpl implements LocalSpreadService {

    private static final Logger logger = LoggerFactory.getLogger("exlog");

    @Autowired
    private EvangelistRepository evangelistRepository;

    @Override
    public List<Evangelist> list(Evangelist evangelist) {
        List<Evangelist> rtnList = null;
        try {
            Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC,"updateTime"));
            rtnList = evangelistRepository.findAll(Example.of(evangelist), sort);
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return rtnList;
    }

    @Override
    public Evangelist saveAndUpdate(Evangelist evangelist) {
        try {
            if(evangelist.getId() == null) {
                evangelist.setId(IdGen.uuid());
                evangelist.setCreateTime(new Date());
                evangelist.setDelFalg("0");
                evangelist.setSpreadCode("88" + evangelist.getStaffId());
            }
            evangelist.setUpdateTime(new Date());
            return evangelistRepository.save(evangelist);
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return null;
    }
}
