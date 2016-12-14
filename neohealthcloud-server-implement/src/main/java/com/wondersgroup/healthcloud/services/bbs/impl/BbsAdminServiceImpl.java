package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.constant.UserConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.AdminVestUser;
import com.wondersgroup.healthcloud.jpa.entity.permission.User;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.repository.bbs.AdminVestUserRepository;
import com.wondersgroup.healthcloud.jpa.repository.permission.UserRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.bbs.BbsAdminService;
import com.wondersgroup.healthcloud.services.bbs.criteria.AdminVestSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.criteria.UserSearchCriteria;
import com.wondersgroup.healthcloud.services.bbs.dto.AdminVestInfoDto;
import com.wondersgroup.healthcloud.utils.searchCriteria.JdbcQueryParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("bbsAdminService")
public class BbsAdminServiceImpl implements BbsAdminService {

    @Autowired
    private RegisterInfoRepository registerInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminVestUserRepository adminVestUserRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    @Override
    public Boolean bindAppUser(String adminId, String mobile) {
        User adminInfo = userRepository.findOne(adminId);
        if (null == adminInfo){
            throw new CommonException(2001, "管理员无效");
        }
        //暂不提供修改/取消关联, 如要做记得要先把原关联的app user管理员重置掉
        if (StringUtils.isNotEmpty(adminInfo.getBindUid())){
            throw new CommonException(2002, "已关联手机段用户!");
        }
        RegisterInfo registerInfo = registerInfoRepository.findByMobile(mobile);
        if (null == registerInfo){
            throw new CommonException(2002, "手机号没有注册!");
        }
        User bindUser = userRepository.findByBindUid(registerInfo.getRegisterid());
        if (null != bindUser && !bindUser.getUserId().equals(adminId)){
            throw new CommonException(2003, "该手机号已被其他管理员绑定!");
        }
        //给绑定的用户设置手机断管理员
        registerInfo.setBanStatus(UserConstant.BanStatus.OK);
        registerInfo.setIsBBsAdmin(1);
        registerInfo.setUpdateDate(new Date());
        registerInfoRepository.saveAndFlush(registerInfo);
        //关联admin管理员
        adminInfo.setBindUid(registerInfo.getRegisterid());
        userRepository.saveAndFlush(adminInfo);
        return true;
    }

    @Override
    public void cancelBBSAdmin(String mobile) {

    }

    @Override
    public List<AdminVestInfoDto> findAdminVestUsers(AdminVestSearchCriteria searchCriteria) {
        StringBuffer querySql = new StringBuffer("select vest.id, user.registerid as uid, user.nickname,user.headphoto as avatar," +
                " user.birthday, user.gender from tb_bbs_admin_vest vest " +
                " left join app_tb_register_info user on user.registerid=vest.vest_uid ");
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<AdminVestInfoDto> vestUsers = jdbcTemplate.query(querySql.toString(), elelmentType.toArray(), new BeanPropertyRowMapper(AdminVestInfoDto.class));
        return vestUsers;
    }

    @Override
    public AdminVestInfoDto getAdminVestInfo(Integer id) {
        String sql = "select vest.id, user.registerid as uid, user.nickname,user.headphoto as avatar, user.birthday, user.gender from tb_bbs_admin_vest vest " +
                " left join app_tb_register_info user on user.registerid=vest.vest_uid " +
                " where vest.id=?";
        List<AdminVestInfoDto> vestUsers = jdbcTemplate.query(sql, new Object[]{id}, new BeanPropertyRowMapper(AdminVestInfoDto.class));
        return null != vestUsers ? vestUsers.get(0) : null;
    }

    @Override
    public int countAdminVestNum(AdminVestSearchCriteria searchCriteria) {
        StringBuffer querySql = new StringBuffer("select count(*) from tb_bbs_admin_vest vest " +
                " left join app_tb_register_info user on user.registerid=vest.vest_uid ");
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        Integer count = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return count == null ? 0 : count;
    }

    @Transactional
    @Override
    public void addUpdateAdminVestUser(String adminUid, AdminVestInfoDto vestUser){
        RegisterInfo registerInfo;
        Date nowDate = new Date();
        if (StringUtils.isNotEmpty(vestUser.getUid())){
            registerInfo = registerInfoRepository.findOne(vestUser.getUid());
            if (registerInfo == null){
                throw new CommonException(2001, "编辑马甲不存在");
            }
        }else {
            registerInfo = new RegisterInfo();
            registerInfo.setCreateDate(nowDate);
            registerInfo.setRegisterid(IdGen.uuid());
            AdminVestUser adminVest = new AdminVestUser();
            adminVest.setAdmin_uid(adminUid);
            adminVest.setCreateTime(nowDate);
            adminVest.setVest_uid(registerInfo.getRegisterid());
            adminVestUserRepository.saveAndFlush(adminVest);
        }
        registerInfo.setUpdateDate(nowDate);
        Boolean isUsedNickName = registerInfoRepository.checkNickNameisUsedIgnoreAppointUid(vestUser.getNickname(), registerInfo.getRegisterid());
        if (isUsedNickName){
            throw new CommonException(2002, "昵称被使用,请重新设置");
        }
        registerInfo.setNickname(vestUser.getNickname());
        registerInfo.setBirthday(vestUser.getBirthday());
        registerInfo.setGender(vestUser.getGender());
        registerInfo.setHeadphoto(vestUser.getAvatar());
        registerInfo.setRegtime(nowDate);
        registerInfo.setChannelType(6);
        registerInfoRepository.saveAndFlush(registerInfo);
    }

    @Override
    public List<String> getAdminVestUidsByAdminUid(String admin_bindUid) {
        List<AdminVestUser> list = adminVestUserRepository.getVestUsersByAdminUid(admin_bindUid);
        if (null == list){
            return null;
        }
        List<String> vestUids = new ArrayList<>();
        for (AdminVestUser adminVestUser : list){
            vestUids.add(adminVestUser.getVest_uid());
        }
        return vestUids;
    }


    @Override
    public List<Map<String, Object>> findUserListByCriteria(UserSearchCriteria searchCriteria) {
        StringBuffer querySql = new StringBuffer("select user.registerid as uid, user.regmobilephone as phone,user.name, user.gender,user.nickname," +
                " user.create_date, user.ban_status, user.is_bbs_admin, user.identifytype,user.headphoto as avatar " +
                " from app_tb_register_info user ");
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where user.channeltype<>6 and " + queryParams.getQueryString());
        }
        querySql.append(searchCriteria.getOrderInfo());
        querySql.append(searchCriteria.getLimitInfo());
        List<Map<String, Object>> list = jdbcTemplate.queryForList(querySql.toString(), elelmentType.toArray());
        return list;
    }

    @Override
    public int countUserByCriteria(UserSearchCriteria searchCriteria) {
        StringBuffer querySql = new StringBuffer("select count(*) from app_tb_register_info user ");
        JdbcQueryParams queryParams = searchCriteria.toQueryParams();
        List<Object> elelmentType = queryParams.getQueryElementType();
        if (!elelmentType.isEmpty()){
            querySql.append(" where " + queryParams.getQueryString());
        }
        Integer rs = jdbcTemplate.queryForObject(querySql.toString(), queryParams.getQueryElementType().toArray(), Integer.class);
        return rs == null ? 0 : rs;
    }
}
