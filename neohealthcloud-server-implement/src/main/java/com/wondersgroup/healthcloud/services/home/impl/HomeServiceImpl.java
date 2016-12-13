package com.wondersgroup.healthcloud.services.home.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import com.wondersgroup.healthcloud.services.home.HomeService;
import com.wondersgroup.healthcloud.services.home.apachclient.HealthApiClient;
import com.wondersgroup.healthcloud.services.home.apachclient.HealthRecordResponse;
import com.wondersgroup.healthcloud.services.home.apachclient.HealthResponse;
import com.wondersgroup.healthcloud.services.home.apachclient.JsonConverter;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineMsgDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.*;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.modulePortal.ModulePortalService;
import com.wondersgroup.healthcloud.services.user.FamilyService;
import com.wondersgroup.healthcloud.services.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 首页接口服务
 * Created by xianglinhai on 2016/12/13.
 */
public class HomeServiceImpl implements HomeService {

    @Autowired
    private ModulePortalService modulePortalService;

    @Autowired
    private CloudTopLineService cloudTopLineService;

    @Autowired
    private HealthApiClient healthApiClient;

    @Autowired
    private FamilyService familyService;

    @Autowired
    private UserService userService;

    @Override
    public List<ModulePortalDTO> findModulePortal() {
        List<ModulePortal> list = modulePortalService.queryAllModulePortal();
        List<ModulePortalDTO> dtoList = new ArrayList<ModulePortalDTO>();

        if (!CollectionUtils.isEmpty(list)) {
            for (ModulePortal entitiy : list) {
                dtoList.add(new ModulePortalDTO(entitiy));
            }
        }

        return dtoList;
    }

    @Override
    public CloudTopLineDTO findCloudTopLine() {
        List<CloudTopLine> list = cloudTopLineService.queryAllCloudTopLine();
        CloudTopLineDTO dto = new CloudTopLineDTO();
        if (!CollectionUtils.isEmpty(list)) {
            dto.setIconUrl(list.get(0).getIconUrl());
            List<CloudTopLineMsgDTO> message = new ArrayList<CloudTopLineMsgDTO>();

            for (CloudTopLine entity : list) {
                CloudTopLineMsgDTO msgDto = new CloudTopLineMsgDTO();
                msgDto.setJumpUrl(entity.getJumpUrl());
                msgDto.setTitle(entity.getTitle());
                msgDto.setType(String.valueOf(entity.getType()));
                message.add(msgDto);
            }

            dto.setMessage(message);

        }

        return dto;
    }

    @Override
    public FamilyHealthDTO findfamilyHealth(String registerId) {
        FamilyHealthDTO dto = new FamilyHealthDTO();
        UserHealthDTO userHealth = null; //用户健康
        FamilyMemberDTO familyMember = null; //家人健康

        Map<String, Object> input = new HashMap<String, Object>();
//        input.put("registerId","8a81c1fb555cab530155e7ef379e00a1");
        input.put("registerId", registerId);
        input.put("sex", "2");//性别
        input.put("moreThanDays", "100");
        input.put("limit", "10");
        input.put("personCard", "");
        input.put("cardType", "");
        input.put("cardId", "");

        //个人健康信息
        userHealth = getFamilyHealth(input);


        //家人健康信息
        List<FamilyMember> fmList = familyService.getFamilyMembers(registerId);
        if (!CollectionUtils.isEmpty(fmList)) {
            for (FamilyMember fm : fmList) {
                Map<String, Object> userInfoMap = userService.findUserInfoByUid(fm.getMemberId());
                if (null != userInfoMap && null != userInfoMap.get("personcard")) {
                    String idc = String.valueOf(userInfoMap.get("personcard"));
                    //1 家人最佳住院信息
                    UserHealthRecordDTO userhealthRecord = getFamilyHealthRecord(idc);

                    // 2 家人健康信息
                    Map<String, Object> familyMemberInput = new HashMap<String, Object>();
                    familyMemberInput.put("registerId", userInfoMap.get("registerid"));
                    familyMemberInput.put("sex", userInfoMap.get("gender"));//性别
                    familyMemberInput.put("moreThanDays", "100");
                    familyMemberInput.put("limit", "10");
                    familyMemberInput.put("personCard", "");
                    familyMemberInput.put("cardType", "");
                    familyMemberInput.put("cardId", "");

                    UserHealthDTO familyMemberHealth = getFamilyHealth(familyMemberInput);
                    if(null != familyMemberHealth){ //家庭成员有健康异常
                        buildFamilyMemberDTO(fm,familyMember,familyMemberHealth);
                    }

                    //3 家人风险评估结果 --春柳提供接口 TODO

                    //4 育苗信息 家庭栏目提供算法 TODO


                }
            }
        }


        if (null != userHealth) {
            dto.setUserHealth(userHealth);
        }

        if (null != familyMember) {
            dto.setFamilyMember(familyMember);
        }


        return dto;
    }

    @Override
    public List<CenterAdDTO> findCenterAdDTO() {
        return null;
    }

    @Override
    public SideAdDTO findSideAdDTO() {
        return null;
    }

    @Override
    public List<SpecialServiceDTO> findSpecialServiceDTO() {
        return null;
    }

    @Override
    public List<FunctionIconsDTO> findFunctionIconsDTO() {
        return null;
    }

    private static Date parseDate(String dateStr, String formatStr) {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    /**
     * 根据身份证号查询出最近住院信息
     *
     * @return
     */
    private UserHealthRecordDTO getFamilyHealthRecord(String idc) {
        UserHealthRecordDTO dto = null;
        Map<String, Object> userHealthInput = new HashMap<String, Object>();
//        userHealthInput.put("idc","310104194004244814");
        userHealthInput.put("idc", idc);

        String userHealthResponse = healthApiClient.userHealthRecord(userHealthInput);
        if (StringUtils.isNotBlank(userHealthResponse)) {
            HealthRecordResponse<List<UserHealthRecordDTO>> healthResponse = JsonConverter.toObject(userHealthResponse, new TypeReference<HealthRecordResponse<List<UserHealthRecordDTO>>>() {
            });
            if (!CollectionUtils.isEmpty(healthResponse.getContent())) {
                Date maxDate = null;
                for (UserHealthRecordDTO healthDto : healthResponse.getContent()) {
                    Date compareDate = parseDate(healthDto.getDate(), "YYYY-MM-DD");
                    if (null != compareDate && null == maxDate) {
                        maxDate = compareDate;
                    } else if (null != compareDate && null != maxDate && compareDate.getTime() > maxDate.getTime()) {
                        maxDate = compareDate;
                    }

                }

                if (null != maxDate /*比较时间逻辑*/) { //TODO 提示家人有住院信息


                }

            }
        }
        return dto;
    }


    /**
     * 查询个人健康信息
     *
     * @param userHealthInput
     * @return
     */
    private UserHealthDTO getFamilyHealth(Map<String, Object> userHealthInput) {
        UserHealthDTO dto = null;
        String userHealthResponse = healthApiClient.userHealth(userHealthInput);
        if (StringUtils.isNotBlank(userHealthResponse)) {
            HealthResponse<List<UserHealthItemDTO>> healthResponse = JsonConverter.toObject(userHealthResponse, new TypeReference<HealthResponse<List<UserHealthItemDTO>>>() {
            });

            if (null != healthResponse) {
                dto = new UserHealthDTO();
                dto.setHealthStatus(healthResponse.getHealthStatus());
                dto.setMainTitle(healthResponse.getMainTitle());
                dto.setExceptionItems(healthResponse.getExceptionItems());
            }

        }

        return dto;
    }


    private void buildFamilyMemberDTO(FamilyMember fm,FamilyMemberDTO familyMember,UserHealthDTO familyMemberHealth){
        if(null == familyMember){
            familyMember = new FamilyMemberDTO();
            familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
        }

        for(UserHealthItemDTO dto:familyMemberHealth.getExceptionItems()){
            FamilyMemberItemDTO  familyMemberItemDTO = new FamilyMemberItemDTO();
            familyMemberItemDTO.setPrompt("");//TODO 拼接话术
            familyMemberItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));

        }


    }

}
