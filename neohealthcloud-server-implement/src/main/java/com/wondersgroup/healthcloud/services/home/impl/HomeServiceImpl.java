package com.wondersgroup.healthcloud.services.home.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.identify.HealthQuestion;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.enums.FamilyHealthStatusEnum;
import com.wondersgroup.healthcloud.jpa.enums.UserHealthStatusEnum;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import com.wondersgroup.healthcloud.services.home.HomeService;
import com.wondersgroup.healthcloud.services.home.apachclient.*;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineMsgDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.*;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.identify.PhysicalIdentifyService;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.modulePortal.ModulePortalService;
import com.wondersgroup.healthcloud.services.user.FamilyService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 首页接口服务
 * Created by xianglinhai on 2016/12/13.
 */
@Service("homeService")
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

    @Autowired
    private PhysicalIdentifyService physicalIdentifyService;

    @Autowired
    private ImageTextService imageTextService;

//    @Autowired
//    private H5ServiceSecurityUtil h5ServiceSecurityUtil;

    @Autowired
    RegisterInfoRepository registerInfoRepo;

    private static final String requestStationNearby = "%s/api/exam/station/nearby?";
    private static final String requestStationDetail = "%s/api/exam/station/detail?id=%s";


    private RestTemplate template = new RestTemplate();

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
    public FamilyHealthDTO findfamilyHealth(RegisterInfo registerInfo, String apiMeasureUrl, String apiUserhealthRecordUrl) {
        FamilyHealthDTO dto = new FamilyHealthDTO();
        UserHealthDTO userHealth = null; //用户健康对象
        FamilyMemberDTO familyMember = null; //家人健康对象

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("registerId", registerInfo.getRegisterid());
        input.put("sex", registerInfo.getGender());
        input.put("moreThanDays", "7");//个人取一周的数据
        input.put("limit", "10");
        input.put("personCard", "");
        input.put("cardType", "");
        input.put("cardId", "");

        //个人健康信息  健康状态0:无数据 1:良好 2:异常
        userHealth = getUserHealthInfo(input, apiMeasureUrl);

        boolean familyLastHealthRecordFlag = true; // 如果多个家人都有就医记录，取一个

        //家人健康信息  健康状态 0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常
        List<FamilyMember> fmList = familyService.getFamilyMembers(registerInfo.getRegisterid());
        if (!CollectionUtils.isEmpty(fmList)) {
            for (FamilyMember fm : fmList) {
                Map<String, Object> userInfoMap = userService.findUserInfoByUid(fm.getMemberId());
                if (null != userInfoMap && null != userInfoMap.get("personcard")) {
                    String idc = String.valueOf(userInfoMap.get("personcard"));

                    //1 家人最近住院信息
                    if (familyLastHealthRecordFlag) {
                        UserHealthRecordDTO userhealthRecord = getFamilyLastHealthRecord(idc, apiUserhealthRecordUrl);
                        if (null != userhealthRecord) {
                            if (null == familyMember) {
                                familyMember = new FamilyMemberDTO();
                                familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
                            }

                            FamilyMemberItemDTO familyMemberItemDTO = buildFamilyLastHealthRecord(fm, userhealthRecord);
                            familyMember.getExceptionItems().add(familyMemberItemDTO);
                            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
                            familyLastHealthRecordFlag = false;
                        }

                    }


                    // 2 家人健康信息
                    Map<String, Object> familyMemberInput = new HashMap<String, Object>();
                    familyMemberInput.put("registerId", userInfoMap.get("registerid"));
                    familyMemberInput.put("sex", userInfoMap.get("gender"));//性别
                    familyMemberInput.put("moreThanDays", "30");//家人取一个月的数据
                    familyMemberInput.put("limit", "10");
                    familyMemberInput.put("personCard", "");
                    familyMemberInput.put("cardType", "");
                    familyMemberInput.put("cardId", "");
                    UserHealthDTO familyMemberHealth = getUserHealthInfo(familyMemberInput, apiMeasureUrl);
                    if (null != familyMemberHealth) { //家庭成员有健康异常

                        if (null == familyMember) {
                            familyMember = new FamilyMemberDTO();
                            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
                            familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
                        }

                        FamilyMemberItemDTO ftemDTO = buildFamilyMemberHealth(fm, familyMemberHealth);
                        if (null != ftemDTO) {
                            familyMember.getExceptionItems().add(ftemDTO);
                            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
                        }
                    }


                    //3 家人风险评估结果
                    FamilyMemberItemDTO fItemDTO = buildFamilyDangerousResult(fm, String.valueOf(userInfoMap.get("registerid")));

                    if (null == familyMember) {
                        familyMember = new FamilyMemberDTO();
                        familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_NO_FAMILY.getId());
                        familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
                    }

                    if (null != fItemDTO) {
                        familyMember.getExceptionItems().add(fItemDTO);
                        familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
                    }


                    //4 育苗信息 养橙：孟华实现接口
                }
            }
        } else { //无家人
            familyMember = new FamilyMemberDTO();
            familyMember.setMainTitle("设置您的家庭成员数据");
            familyMember.setSubTitle("您可以添加家人>>");

            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_NO_FAMILY.getId()); //健康状态0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常
            familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
        }


        //情况一：个人健康无数据情况
        //情况二：个人健康有数据，且数据正常情况
        //情况三：个人健康有数据，且数据异常情况
        if (null == userHealth) {
            userHealth = new UserHealthDTO();
            userHealth.setMainTitle("请录入您的健康数据");
            userHealth.setSubTitle("添加您的健康数据>>");
            userHealth.setHealthStatus(UserHealthStatusEnum.HAVE_NO_DATA.getId());
            dto.setUserHealth(userHealth);
        } else if (userHealth.getHealthStatus().equals(UserHealthStatusEnum.HAVE_GOOD_HEALTH.getId())) {
            userHealth.setMainTitle("您的健康状况：良好");
            userHealth.setSubTitle("你的健康状况良好，要继续保持哦");
        }

        dto.setUserHealth(userHealth);


        //情况一：无家人，显示添加家人。
        //情况二：有家人，家人正常，近期无通知提示。
        //情况三：有家人，家人无任何数据。

        if (null == familyMember) { //有家人，家人正常，近期无通知提示。
            familyMember = new FamilyMemberDTO();
            familyMember.setMainTitle("家庭成员 健康状况良好");
            familyMember.setSubTitle("家人健康状况良好，要继续保持。");
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
            familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());
        } else if (null != familyMember && familyMember.getHealthStatus().equals(FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA.getId())) {
            familyMember.setMainTitle("设置您的家庭成员数据");
            familyMember.setSubTitle("添加您家人的健康数据吧>>");
        }

        dto.setFamilyMember(familyMember);

        return dto;
    }


    @Override
    public List<CenterAdDTO> findCenterAdDTO(String mainArea) {
        ImageText imgTextC = new ImageText();
        imgTextC.setAdcode(ImageTextEnum.HOME_BANNER.getType());
        List<ImageText> imageTextsC = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgTextC);
        List<CenterAdDTO> list = new ArrayList<CenterAdDTO>();

        if (!CollectionUtils.isEmpty(imageTextsC)) {
            int flag = imageTextsC.size() <= 3 ? imageTextsC.size() : 3; //最多3个
            for (int i = 0; i < flag; i++) {
                list.add(new CenterAdDTO(imageTextsC.get(i)));
            }
        }

        return list;
    }


    @Override
    public SideAdDTO findSideAdDTO(String mainArea, String specArea) {
        SideAdDTO sideAd = new SideAdDTO();
        ImageText imgTextD = new ImageText();
        imgTextD.setAdcode(ImageTextEnum.HOME_FLOAT_AD.getType());
        List<ImageText> imageTextsD = imageTextService.findImageTextByAdcodeForApp(mainArea, specArea, imgTextD);
        if (!CollectionUtils.isEmpty(imageTextsD)) {
            sideAd = new SideAdDTO(imageTextsD.get(0));
        }
        return sideAd;
    }

    @Override
    public List<SpecialServiceDTO> findSpecialServiceDTO(Session session, String appVersion, String mainArea, String specArea) {
        List<SpecialServiceDTO> list = new ArrayList<SpecialServiceDTO>();
        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_SERVICE_BTN.getType(), appVersion);

        if (!CollectionUtils.isEmpty(imageTextsB)) {
            String idCard = null;
            int loginOrRealName = 0;// 0:需登录,1:需实名制,2:正常
            if (session != null && StringUtils.isNotEmpty(session.getUserId())) {
                String userId = session.getUserId();
                RegisterInfo registerInfo = registerInfoRepo.findOne(userId);
                if (registerInfo != null) {
                    // 未实名认证
                    if (!"1".equals(registerInfo.getIdentifytype()) && !"2".equals(registerInfo.getIdentifytype())) {
                        loginOrRealName = 1;
                    } else {
                        loginOrRealName = 2;
                        idCard = registerInfo.getPersoncard();
                    }
                }
            }


            for (ImageText imageText : imageTextsB) {
                SpecialServiceDTO dto = new SpecialServiceDTO();
                dto.setImgUrl(imageText.getImgUrl());

                if (imageText.getHoplink() != null && imageText.getHoplink().contains("{sfzh}")) {// 需获取身份证
                    if (loginOrRealName == 2) {
                        dto.setLoginOrRealName(2);
                        dto.setHoplink(imageText.getHoplink().replace("{sfzh}", idCard));
                    } else {
                        dto.setLoginOrRealName(loginOrRealName);
                        dto.setHoplink(imageText.getHoplink());
                    }

                } else {// 不需要身份证信息
                    dto.setLoginOrRealName(2);
                    dto.setHoplink(imageText.getHoplink());
                }

                dto.setMainTitle(imageText.getMainTitle());
                dto.setSubTitle(imageText.getSubTitle());
                list.add(dto);
            }
        }


        return list;
    }

    @Override
    public List<FunctionIconsDTO> findFunctionIconsDTO(Session session, String appVersion, String mainArea, String specArea) {
        List<FunctionIconsDTO> list = new ArrayList<FunctionIconsDTO>();

        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_HOME_FUNCTION.getType(), appVersion);
        if (imageTextsB != null && imageTextsB.size() > 0) {
            for (ImageText imageText : imageTextsB) {
                FunctionIconsDTO dto = new FunctionIconsDTO();
                dto.setImgUrl(imageText.getImgUrl());
//                dto.setHoplink(h5ServiceSecurityUtil.parseUrl(imageText.getHoplink(), session));
                dto.setHoplink(imageText.getHoplink());
                dto.setMainTitle(imageText.getMainTitle());
                dto.setSubTitle(imageText.getSubTitle());
                list.add(dto);
            }
        }
        return list;
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
     * 家庭成员最近住院信息
     *
     * @param idc                    身份证号码
     * @param apiUserhealthRecordUrl
     * @return
     */
    private UserHealthRecordDTO getFamilyLastHealthRecord(String idc, String apiUserhealthRecordUrl) {
        UserHealthRecordDTO dto = null;
        Map<String, Object> userHealthInput = new HashMap<String, Object>();
        userHealthInput.put("idc", idc);

        final int theDayBeforeToday = -30; //最近一个月
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(new Date());//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, theDayBeforeToday);


        String userHealthResponse = healthApiClient.userHealthRecord(apiUserhealthRecordUrl, userHealthInput);
        if (StringUtils.isNotBlank(userHealthResponse)) {

            DataMsg<HealthRecordResponse<List<UserHealthRecordDTO>>> dataMsg = JsonConverter.toObject(userHealthResponse, new TypeReference<DataMsg<HealthRecordResponse<List<UserHealthRecordDTO>>>>() {
            });
            if (null != dataMsg.getData()) {
                List<UserHealthRecordDTO> healthResponse = (List<UserHealthRecordDTO>) dataMsg.getData().getContent();
                for (UserHealthRecordDTO healthDto : healthResponse) {
                    Date compareDate = parseDate(healthDto.getDate(), "YYYY-MM-DD");
                    if (null != compareDate && compareDate.getTime() >= calendar.getTime().getTime()) { // 有最近的就医记录
                        dto = healthDto;
                        break;
                    }

                }
            }
        }

        return dto;
    }


    /**
     * 查询个人健康信息
     *
     * @param userInfoMap   参数
     * @param apiMeasureUrl 请求地址
     * @return
     */
    private UserHealthDTO getUserHealthInfo(Map<String, Object> userInfoMap, String apiMeasureUrl) {
        UserHealthDTO dto = null;
        String userHealthResponse = healthApiClient.userHealth(apiMeasureUrl, userInfoMap);
        if (StringUtils.isNotBlank(userHealthResponse)) {

            DataMsg<HealthResponse<List<UserHealthItemDTO>>> dataResponse = JsonConverter.toObject(userHealthResponse, new TypeReference<DataMsg<HealthResponse<List<UserHealthItemDTO>>>>() {
            });

            if (null != dataResponse && null != dataResponse.getData()) {
                HealthResponse healthResponse = dataResponse.getData();
                dto = new UserHealthDTO();
                dto.setHealthStatus(healthResponse.getHealthStatus());
                dto.setMainTitle(healthResponse.getMainTitle());
                dto.setSubTitle("[显示最新的2项异常指标数据]");
                dto.setExceptionItems((List<UserHealthItemDTO>) healthResponse.getExceptionItems());
            }
        }

        return dto;
    }


    /**
     * 家人评估结果异常
     *
     * @param fm
     * @param registerid
     * @return
     */
    private FamilyMemberItemDTO buildFamilyDangerousResult(FamilyMember fm, String registerid) {


        FamilyMemberItemDTO fItemDTO = null;
        HealthQuestion healthQuestion = physicalIdentifyService.getRecentPhysicalIdentify(registerid);
        if (null != healthQuestion && StringUtils.isNotBlank(healthQuestion.getResult())) {
            fItemDTO = new FamilyMemberItemDTO();
            fItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
            fItemDTO.setPrompt(fItemDTO.getRelationship() + " ,风险评估结果 " + healthQuestion.getResult());//话术

        }

        return fItemDTO;
    }


    /**
     * 家人健康异常
     *
     * @param fm
     * @param familyMemberHealth
     * @return
     */
    private FamilyMemberItemDTO buildFamilyMemberHealth(FamilyMember fm, UserHealthDTO familyMemberHealth) {
        FamilyMemberItemDTO ftemDTO = null;

        if (!CollectionUtils.isEmpty(familyMemberHealth.getExceptionItems())) {
            for (UserHealthItemDTO dto : familyMemberHealth.getExceptionItems()) {
                ftemDTO = new FamilyMemberItemDTO();
                ftemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
                ftemDTO.setPrompt(dto.getName() + " " + dto.getData() + " " + (dto.getHightAndLow().equals("1") ? "偏高" : "偏低"));
                break;//家人有多项异常，只取一项
            }
        }

        return ftemDTO;

    }

    /**
     * 家人就医记录
     *
     * @param fm
     * @param userhealthRecord
     */
    private FamilyMemberItemDTO buildFamilyLastHealthRecord(FamilyMember fm, UserHealthRecordDTO userhealthRecord) {

        FamilyMemberItemDTO familyMemberItemDTO = new FamilyMemberItemDTO();
        familyMemberItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
        familyMemberItemDTO.setPrompt(familyMemberItemDTO.getRelationship() + " ，有新的就医记录");//话术

        return familyMemberItemDTO;

    }

}
