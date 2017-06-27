package com.wondersgroup.healthcloud.services.doctor.impl;

import java.util.*;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.enums.IntervenEnum;
import com.wondersgroup.healthcloud.exceptions.Exceptions;
import com.wondersgroup.healthcloud.jpa.entity.diabetes.NeoFamIntervention;
import com.wondersgroup.healthcloud.jpa.entity.doctor.DoctorIntervention;
import com.wondersgroup.healthcloud.jpa.repository.diabetes.NeoFamInterventionRepository;
import com.wondersgroup.healthcloud.jpa.repository.doctor.DoctorInterventionRepository;
import com.wondersgroup.healthcloud.services.doctor.DoctorInterventionService;
import com.wondersgroup.healthcloud.services.interven.entity.IntervenEntity;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * Created by zhaozhenxing on 2016/12/07.
 */

@Service
public class DoctorInterventionServiceImpl implements DoctorInterventionService {
    private static final Logger logger = LoggerFactory.getLogger("exlog");

    @Autowired
    private DoctorInterventionRepository doctorInterventionRepository;

    @Autowired
    private NeoFamInterventionRepository neoFamInterventionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DoctorIntervention> list(DoctorIntervention doctorIntervention) {
        List<DoctorIntervention> rtnList = null;
        try {
            // 按照干预时间倒序排列
            rtnList = doctorInterventionRepository.findAll(Example.of(doctorIntervention), new Sort(Sort.Direction.DESC, "createTime"));
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return rtnList;
    }

    @Override
    public DoctorIntervention saveAndUpdate(DoctorIntervention doctorIntervention) {
        try {
            if(doctorIntervention.getId() == null) {
                doctorIntervention.setId(IdGen.uuid());
                doctorIntervention.setCreateTime(new Date());
                doctorIntervention.setDelFlag("0");
            }
            doctorIntervention.setUpdateTime(new Date());
            return doctorInterventionRepository.save(doctorIntervention);
        } catch (Exception ex) {
            logger.error(Exceptions.getStackTraceAsString(ex));
        }
        return null;
    }

    /**
     *
     * @param intervenId
     * @return
     */
    @Override
    public List<NeoFamIntervention> findPatientBGOutlierListByIntervenId(String intervenId) {
        List<NeoFamIntervention> neoFamInterventionList = Lists.newArrayList();
        //查询最新的一次首次异常
        NeoFamIntervention firstBg = neoFamInterventionRepository.findLatestBGByTypeAndInterventionId(intervenId, IntervenEnum.msgType1.getTypeCode());
        if(firstBg!=null){
            neoFamInterventionList.add(firstBg);
        }

        //查询七天连续异常
        NeoFamIntervention latestSeven = neoFamInterventionRepository.findLatestBGByTypeAndInterventionId(intervenId, IntervenEnum.msgType2.getTypeCode());
        if(latestSeven!=null){
            String sql = " select a.* from (\n" +
                    "(select if(date(warn_date)=@d,@n:=@n+1,@n:=1) as rownum,@d:=date(warn_date),i.* from neo_fam_intervention  i\n" +
                    "  where del_flag = '0' and i.register_id ='%s' " +
                    "  and type REGEXP '10000|20000|30000'" +
                    "  order by warn_date asc) a,(select @d:='') b,(select @n:=0) c\n" +
                    ") where rownum=1  and date(warn_date)<='%s' order by a.warn_date desc limit 7";

            sql = String.format(sql,latestSeven.getRegisterId(),new DateTime(latestSeven.getWarnDate()).toString("yyyy-MM-dd"));
            List<NeoFamIntervention> sevenIntervens = jdbcTemplate.query(sql,new BeanPropertyRowMapper(NeoFamIntervention.class));

            for(NeoFamIntervention intervention : sevenIntervens){
                if(!neoFamInterventionList.contains(intervention)){
                    neoFamInterventionList.add(intervention);
                }
            }

        }
        //排序
        Collections.sort(neoFamInterventionList,new Comparator(){
            @Autowired
            public int compare(Object o1, Object o2) {
                NeoFamIntervention interven1=(NeoFamIntervention)o1;
                NeoFamIntervention interven2=(NeoFamIntervention)o2;
                if(DateUtils.compareDate(interven2.getWarnDate(),interven1.getWarnDate())>0){
                    return 1;
                }else if(DateUtils.compareDate(interven1.getWarnDate(), interven2.getWarnDate())==0){
                    return 0;
                }else{
                    return -1;
                }
            }
                });
        return neoFamInterventionList;
    }

    @Override
    public List<NeoFamIntervention> findPatientPressureOutlierListByIntervenId(IntervenEntity interven) {
        List<NeoFamIntervention> resultList = Lists.newArrayList();

        String sunSql = " (select * from neo_fam_intervention a where a.register_id = '%s' and a.doctor_intervention_id = '%s' \n" +
                " and a.del_flag = '0' and a.is_deal = '1' %s order by a.warn_date desc limit 1) ";
        String sql = String.format(sunSql,interven.getRegister_id(),interven.getId()," and a.type like '%"+IntervenEnum.msgType4.getTypeCode()+"%'") + " UNION " +
        String.format(sunSql,interven.getRegister_id(),interven.getId()," and a.type like '%"+IntervenEnum.msgType5.getTypeCode()+"%'") + " UNION " +
        String.format(sunSql,interven.getRegister_id(),interven.getId()," and a.type like '%"+IntervenEnum.msgType6.getTypeCode()+"%'") + " UNION " +
        String.format(sunSql,interven.getRegister_id(),interven.getId()," and a.type like '%"+IntervenEnum.msgType7.getTypeCode()+"%'") + " UNION " +
        String.format(sunSql,interven.getRegister_id(),interven.getId()," and a.type like '%"+IntervenEnum.msgType8.getTypeCode()+"%'") ;

        List<NeoFamIntervention> interventionList = jdbcTemplate.query(sql,new BeanPropertyRowMapper(NeoFamIntervention.class));

        NeoFamIntervention threeDayInterven = null;

        if (interventionList!=null && interventionList.size()>0) {
            for (NeoFamIntervention intervention : interventionList){
                if(intervention.getType().contains(IntervenEnum.msgType7.getTypeCode())){
                    threeDayInterven = intervention;
                }
                if(!resultList.contains(intervention)){
                    resultList.add(intervention);
                }
            }
        }
        //查询最新的三天连续

        if(threeDayInterven!=null){
            String threeDaysSql = " select a.* from (\n" +
                    "(select if(date(warn_date)=@d,@n:=@n+1,@n:=1) as rownum,@d:=date(warn_date),i.* from neo_fam_intervention  i\n" +
                    "  where del_flag = '0' and i.register_id ='%s' " +
                    "  and type REGEXP '40000|41000|40001|40002|40003|40004'" +
                    "  order by warn_date asc) a,(select @d:='') b,(select @n:=0) c\n" +
                    ") where rownum=1  and date(warn_date)<='%s' order by a.warn_date desc limit 3";

            threeDaysSql = String.format(threeDaysSql, threeDayInterven.getRegisterId(), new DateTime(threeDayInterven.getWarnDate()).toString("yyyy-MM-dd"));
            List<NeoFamIntervention> threeDays = jdbcTemplate.query(threeDaysSql, new BeanPropertyRowMapper(NeoFamIntervention.class));

            if(threeDays!=null && threeDays.size()>0){
                for (NeoFamIntervention intervention : threeDays){
                    if(!resultList.contains(intervention)){
                        resultList.add(intervention);
                    }
                }
            }

        }

        //排序
        Collections.sort(resultList,new Comparator(){
            @Autowired
            public int compare(Object o1, Object o2) {
                NeoFamIntervention interven1=(NeoFamIntervention)o1;
                NeoFamIntervention interven2=(NeoFamIntervention)o2;
                if(DateUtils.compareDate(interven2.getWarnDate(),interven1.getWarnDate())>0){
                    return 1;
                }else if(DateUtils.compareDate(interven1.getWarnDate(), interven2.getWarnDate())==0){
                    return 0;
                }else{
                    return -1;
                }
            }
        });

        return resultList;
    }

    @Override
    public NeoFamIntervention findLatestByInterventionId(String intervenId) {
        return neoFamInterventionRepository.findLatestByInterventionId(intervenId);
    }
}