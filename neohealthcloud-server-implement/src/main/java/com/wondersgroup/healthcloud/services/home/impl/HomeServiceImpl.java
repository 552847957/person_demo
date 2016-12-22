package com.wondersgroup.healthcloud.services.home.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.enums.FamilyHealthStatusEnum;
import com.wondersgroup.healthcloud.jpa.enums.UserHealthStatusEnum;
import com.wondersgroup.healthcloud.jpa.repository.user.AnonymousAccountRepository;
import com.wondersgroup.healthcloud.jpa.repository.user.RegisterInfoRepository;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.assessment.AssessmentService;
import com.wondersgroup.healthcloud.services.cloudTopLine.CloudTopLineService;
import com.wondersgroup.healthcloud.services.config.AppConfigService;
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
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.modulePortal.ModulePortalService;
import com.wondersgroup.healthcloud.services.user.FamilyService;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 首页接口服务
 * Created by xianglinhai on 2016/12/13.
 */
@Service("homeService")
public class HomeServiceImpl implements HomeService {
    private static final Logger logger = LoggerFactory.getLogger(HomeServiceImpl.class);
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
    AssessmentService assessmentServiceImpl;

    @Autowired
    private ImageTextService imageTextService;

//    @Autowired
//    private H5ServiceSecurityUtil h5ServiceSecurityUtil;

    @Autowired
    RegisterInfoRepository registerInfoRepo;

    @Autowired
    ManageNewsArticleService manageNewsArticleServiceImpl;


    @Autowired
    private AppConfigService appConfigService;

    public final String keyWord = "app.home.cloudtoplineimage";//离散数据 key

    @Autowired
    private AnonymousAccountRepository anonymousAccountRepository;

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

        AppConfig rtnAppConfig = appConfigService.findSingleAppConfigByKeyWord("3101", null, keyWord);
        String iconUrl = null;
        if (null != rtnAppConfig && StringUtils.isNotBlank(rtnAppConfig.getData())) {
            Pattern p = Pattern.compile("\"iconUrl\":\"(.*?)\"");
            Matcher m = p.matcher(rtnAppConfig.getData());
            if (m.find()) {
                iconUrl = m.group(1);
            }
        }

        if (!CollectionUtils.isEmpty(list) && StringUtils.isNotBlank(iconUrl)) {
            dto.setIconUrl(iconUrl);
            List<CloudTopLineMsgDTO> message = new ArrayList<CloudTopLineMsgDTO>();

            for (CloudTopLine entity : list) {
                CloudTopLineMsgDTO msgDto = new CloudTopLineMsgDTO();
                msgDto.setJumpUrl(entity.getJumpUrl());
                msgDto.setTitle(entity.getTitle());
                msgDto.setType(String.valueOf(entity.getType()));
                msgDto.setId(entity.getJumpId());
                message.add(msgDto);
            }

            dto.setMessage(message);

        }

        return dto;
    }

    @Override
    public FamilyHealthDTO findfamilyHealth(RegisterInfo registerInfo, Map<String, Object> urlMap) {
        String apiMeasureUrl = String.valueOf(urlMap.get("apiMeasureUrl"));
        String apiUserhealthRecordUrl = String.valueOf(urlMap.get("apiUserhealthRecordUrl"));
        String apiVaccineUrl = String.valueOf(urlMap.get("apiVaccineUrl"));

        if (StringUtils.isBlank(apiMeasureUrl) || StringUtils.isBlank(apiUserhealthRecordUrl) || StringUtils.isBlank(apiVaccineUrl)) {
            logger.info("apiMeasureUrl or apiUserhealthRecordUrl or apiVaccineUrl is blank ", apiMeasureUrl, apiUserhealthRecordUrl, apiVaccineUrl);
            return null;
        }


        FamilyHealthDTO dto = new FamilyHealthDTO();
        UserHealthDTO userHealth = null;   //用户健康对象
        FamilyMemberDTO familyMember = new FamilyMemberDTO();
        familyMember.setExceptionItems(new ArrayList<FamilyMemberItemDTO>());


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


        //家人健康信息  健康状态 0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常

        List<FamilyMember> fmList = familyService.getFamilyMembers(registerInfo.getRegisterid());



        List<FamilyMemberItemDTO> familyMemberHealthRecordList = new ArrayList<FamilyMemberItemDTO>(); //住院信息集合
        Map<FamilyMember, UserHealthDTO> familyMemberHealthMap = new HashMap<FamilyMember, UserHealthDTO>(); //家庭成员健康集合
        List<FamilyMemberItemDTO> familyMemberDangerousItemList = new ArrayList<FamilyMemberItemDTO>(); //风险评估集合
        List<FamilyMemberItemDTO> familyMemberVaccinetemList = new ArrayList<FamilyMemberItemDTO>(); //疫苗信息集合


        if (!CollectionUtils.isEmpty(fmList)) {
            for (FamilyMember fm : fmList) {
                Map<String, Object> userInfoMap = userService.findUserInfoByUid(fm.getMemberId());
                if (null != userInfoMap && null != userInfoMap.get("personcard")) {
                    String idc = String.valueOf(userInfoMap.get("personcard"));

                    //1 家人最近住院信息
                    UserHealthRecordDTO userhealthRecord = getFamilyLastHealthRecord(idc, apiUserhealthRecordUrl);
                    if (null != userhealthRecord) {
                        FamilyMemberItemDTO familyMemberItemDTO = buildFamilyLastHealthRecord(fm, userhealthRecord);
                        if (null != familyMemberItemDTO) {
                            familyMemberHealthRecordList.add(familyMemberItemDTO);
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
                    if (null != familyMemberHealth) {
                        familyMemberHealthMap.put(fm, familyMemberHealth);
                    }


                    //3 家人风险评估结果
                    FamilyMemberItemDTO fItemDTO = buildFamilyDangerousResult(fm, String.valueOf(userInfoMap.get("registerid")));
                    if (null != fItemDTO) {
                        familyMemberDangerousItemList.add(fItemDTO);
                    }

                    //4 育苗信息
                    FamilyMemberItemDTO vaccinetemDTO = buildFamilyVaccineDate(fm, apiVaccineUrl);
                    if (null != vaccinetemDTO) {
                        familyMemberVaccinetemList.add(vaccinetemDTO);
                    }
                }
            }
        } else { //无家人
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_NO_FAMILY.getId());
        }




        if (!CollectionUtils.isEmpty(familyMemberHealthRecordList)) {//有就医记录
            FamilyMemberItemDTO familyMemberItemDTO = familyMemberHealthRecordList.get(0);
            familyMember.getExceptionItems().add(familyMemberItemDTO);
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
        }

        if (!CollectionUtils.isEmpty(familyMemberHealthMap)) {//家人健康信息集合
            int goodsHealthCount = 0;
            int haveNoDataCount = 0;

            int showErrorCount = 0;
            Iterator<FamilyMember> it = familyMemberHealthMap.keySet().iterator();

            while (it.hasNext()) {  //统计 无数据/健康 两种状态的数据
                FamilyMember fm = it.next();
                UserHealthDTO item = familyMemberHealthMap.get(fm);
                if (UserHealthStatusEnum.HAVE_GOOD_HEALTH == UserHealthStatusEnum.getEnumById(item.getHealthStatus())) {
                    goodsHealthCount++;
                } else if (UserHealthStatusEnum.HAVE_NO_DATA == UserHealthStatusEnum.getEnumById(item.getHealthStatus())) {
                    haveNoDataCount++;
                } else if(UserHealthStatusEnum.HAVE_UNHEALTHY == UserHealthStatusEnum.getEnumById(item.getHealthStatus())){//异常数据，显示给前端
                    FamilyMemberItemDTO ftemDTO = buildFamilyMemberHealth(fm, item);
                    familyMember.getExceptionItems().add(ftemDTO);
                    familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
                    showErrorCount++;
                    if (showErrorCount == 2) { //显示最新的2项异常指标数据,TODO 需要排序的
                        break;
                    }

                }else{//未知状态(默认为 有家人家人正常)
                    familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
                }

            }

            if (goodsHealthCount == familyMemberHealthMap.size()) {
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
            } else if (haveNoDataCount == familyMemberHealthMap.size()) {
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA.getId());
            }
        }


        if (!CollectionUtils.isEmpty(familyMemberDangerousItemList)) {// 风险评估记录
            FamilyMemberItemDTO item = familyMemberDangerousItemList.get(0);
            familyMember.getExceptionItems().add(item);
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
        }

        if (!CollectionUtils.isEmpty(familyMemberVaccinetemList)) {  //疫苗通知集合
            FamilyMemberItemDTO item = familyMemberVaccinetemList.get(0);
            familyMember.getExceptionItems().add(item);
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
        }


                /*HAVE_NO_FAMILY("0","无家人"),
                HAVE_FAMILY_WITHOUT_DATA("1","有家人家人无数据"),
                HAVE_FAMILY_AND_HEALTHY("2","有家人家人正常"),
                HAVE_FAMILY_AND_UNHEALTHY("3","异常");*/

        if (FamilyHealthStatusEnum.HAVE_NO_FAMILY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("设置您的家庭成员数据");
            familyMember.setSubTitle("您可以添加家人>>");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("家庭成员 健康状况良好");
            familyMember.setSubTitle("家人健康状况良好，要继续保持。");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("设置您的家庭成员数据");
            familyMember.setSubTitle("添加您家人的健康数据吧>>");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("");
            familyMember.setSubTitle("");
        }

        if (null == userHealth) {
            userHealth = new UserHealthDTO();
            userHealth.setMainTitle("请录入您的健康数据");
            userHealth.setSubTitle("添加您的健康数据>>");
            userHealth.setHealthStatus(UserHealthStatusEnum.HAVE_NO_DATA.getId());
        }

        //个人数据最多显示两条
        List<UserHealthItemDTO> userItemList = CollectionUtils.isEmpty(userHealth.getExceptionItems()) ? new ArrayList<UserHealthItemDTO>():userHealth.getExceptionItems().size() > 2 ? userHealth.getExceptionItems().subList(0,2) : userHealth.getExceptionItems();
        replaceUnitStr(userItemList);
        userHealth.setExceptionItems(userItemList);

        dto.setUserHealth(userHealth);
        dto.setFamilyMember(familyMember);

        return dto;
    }

    /**
     * 去掉单位
     * @param userItemList
     */
    private void replaceUnitStr( List<UserHealthItemDTO> userItemList){
        if(!CollectionUtils.isEmpty(userItemList)){
            for(UserHealthItemDTO dto:userItemList){
                dto.setData(dto.getData().replace("次/分钟","").replace("mmHg",""));
            }

        }

    }


    @Override
    public List<CenterAdDTO> findCenterAdDTO(String mainArea) {
        ImageText imgTextC = new ImageText();
        imgTextC.setAdcode(ImageTextEnum.HOME_ADVERTISEMENT.getType());
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
        // 获取公共服务
        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, null, ImageTextEnum.G_HOME_SPECIAL_SERVICE.getType(), appVersion);
        if (StringUtils.isNotEmpty(specArea)) {// 获取特色服务
            List<ImageText> specImageTests = imageTextService.findGImageTextForApp(mainArea, specArea, ImageTextEnum.G_HOME_SPECIAL_SERVICE.getType(), appVersion);
            if (CollectionUtils.isEmpty(imageTextsB)) {
                imageTextsB = specImageTests;
            } else {
                if (!CollectionUtils.isEmpty(specImageTests)) {
                    imageTextsB.addAll(specImageTests);
                }

            }
        }

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
                    if (null != compareDate && compareDate.getTime() >= calendar.getTime().getTime()) { // 取最近的就医记录
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
     * @return //情况一：个人健康无数据情况
     * //情况二：个人健康有数据，且数据正常情况
     * //情况三：个人健康有数据，且数据异常情况
     */
    private UserHealthDTO getUserHealthInfo(Map<String, Object> userInfoMap, String apiMeasureUrl) {
        UserHealthDTO dto = null;
        String userHealthResponse = healthApiClient.userHealth(apiMeasureUrl, userInfoMap);
        if (StringUtils.isNotBlank(userHealthResponse)) {

            DataMsg<HealthResponse<List<UserHealthItemDTO>>> dataResponse = JsonConverter.toObject(userHealthResponse, new TypeReference<DataMsg<HealthResponse<List<UserHealthItemDTO>>>>() {
            });

            if (null != dataResponse && null != dataResponse.getData() && StringUtils.isNotBlank(dataResponse.getData().getHealthStatus())) {
                // dataResponse.getData().getHealthStatus() 状体  UserHealthStatusEnum 枚举

                if (UserHealthStatusEnum.HAVE_NO_DATA == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(new ArrayList<UserHealthItemDTO>());
                    dto.setMainTitle("请录入您的健康数据");
                    dto.setSubTitle("添加您的健康数据>>");
                } else if (UserHealthStatusEnum.HAVE_GOOD_HEALTH == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(new ArrayList<UserHealthItemDTO>());
                    dto.setMainTitle("您的健康状况：良好");
                    dto.setSubTitle("你的健康状况良好，要继续保持哦");

                } else if (UserHealthStatusEnum.HAVE_UNHEALTHY == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(dataResponse.getData().getExceptionItems());
                    Collections.reverse(dto.getExceptionItems());//按照 testTime 降序排序
                    dto.setMainTitle("您的健康状况：" + dto.getExceptionItems().size() + "项异常");
                    dto.setSubTitle("[显示最新的2项异常指标数据]");

                } else {//未知 UserHealthStatusEnum 状态

                }
            }

        } else { //数据解析异常 TODO

        }

        return dto;
    }

    public static void main(String[]args){
        UserHealthItemDTO BMI = new UserHealthItemDTO();
        BMI.setTestTime(1482389215000L);
        BMI.setName("BMI");

        UserHealthItemDTO dto1 = new UserHealthItemDTO();
        dto1.setTestTime(1482389155000L);
        dto1.setName("血糖");

        UserHealthItemDTO dto2 = new UserHealthItemDTO();
        dto2.setTestTime(1482326097000L);
        dto2.setName("心率");

        UserHealthItemDTO dto3 = new UserHealthItemDTO();
        dto3.setTestTime(1482326097000L);
        dto3.setName("血压");

        List<UserHealthItemDTO> list = new ArrayList<UserHealthItemDTO>();
        list.add(BMI);
        list.add(dto1);
        list.add(dto2);
        list.add(dto3);


        UserHealthDTO dto = new UserHealthDTO();
        dto.setExceptionItems(list);
        Collections.sort(dto.getExceptionItems());

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
        Map<String, Object> resultMap = assessmentServiceImpl.getRecentAssessIsNormal(registerid);
        if (null != resultMap && resultMap.size() > 0 && !Boolean.parseBoolean(String.valueOf(resultMap.get("state")))) {
            fItemDTO = new FamilyMemberItemDTO();
            fItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
            fItemDTO.setPrompt("风险评估结果 高危人群");

        }

        return fItemDTO;
    }

    /**
     * 疫苗接种
     *
     * @param fm
     * @return
     */
    private FamilyMemberItemDTO buildFamilyVaccineDate(FamilyMember fm, String apiVaccineUrl) {
        FamilyMemberItemDTO fItemDTO = null;
        String birthDate = getBirthDay(fm); // 计算出孩子的生日 16岁以下的,不是大人
        if (StringUtils.isBlank(birthDate)) {
            return null;
        }

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("birthday", birthDate);
        String leftDays = healthApiClient.getLeftDaysByBirth(apiVaccineUrl, input);
        if (StringUtils.isNotBlank(leftDays)) {
            fItemDTO = new FamilyMemberItemDTO();
            fItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
            fItemDTO.setPrompt("疫苗接种 " + leftDays + "天后");
        }

        return fItemDTO;
    }

    /**
     * 获得生日
     *
     * @param fm
     * @return
     */
    private String getBirthDay(FamilyMember fm) {
        String birthDay = null;
        if (null != fm.getIsAnonymous() && fm.getIsAnonymous().equals(0)) {//非匿名
            RegisterInfo registerInfo = registerInfoRepo.findOne(fm.getMemberId());
            if (null != registerInfo && null != registerInfo.getBirthday()) {
                //16岁以下认为需要打疫苗

                Integer age = getAge(registerInfo.getBirthday());
                if (null != age && age < 16) {
                    birthDay = new SimpleDateFormat("yyyy-MM-dd").format(registerInfo.getBirthday());
                }

            }
        } else if (null != fm.getIsAnonymous() && !fm.getIsAnonymous().equals(0)) {//匿名
            AnonymousAccount account = anonymousAccountRepository.findOne(fm.getMemberId());
            if (null != account && null != account.getBirthDate()) {
                Integer age = getAge(account.getBirthDate());
                if (null != age && age < 16) {
                    birthDay = new SimpleDateFormat("yyyy-MM-dd").format(account.getBirthDate());
                }
            }
        }

        return birthDay;
    }

    public Integer getAge(Date birthDay) {
        //获取当前系统时间
        Calendar cal = Calendar.getInstance();
        //如果出生日期大于当前时间，则抛出异常
        if (cal.before(birthDay)) {
            /*throw new IllegalArgumentException(
                    "The birthDay is before Now.It's unbelievable!");*/
            return null;
        }
        //取出系统当前时间的年、月、日部分
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        //将日期设置为出生日期
        cal.setTime(birthDay);
        //取出出生日期的年、月、日部分
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        //当前年份与出生年份相减，初步计算年龄
        int age = yearNow - yearBirth;
        //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
        if (monthNow <= monthBirth) {
            //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            } else {
                age--;
            }
        }

        return age;
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
                ftemDTO.setPrompt(dto.getName() +  (dto.getHightAndLow().equals("1") ? "偏高" : "偏低"));
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
        familyMemberItemDTO.setPrompt("有新的就医记录");

        return familyMemberItemDTO;

    }


}
