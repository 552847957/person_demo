package com.wondersgroup.healthcloud.services.home.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.helper.family.FamilyMemberRelation;
import com.wondersgroup.healthcloud.jpa.entity.cloudtopline.CloudTopLine;
import com.wondersgroup.healthcloud.jpa.entity.config.AppConfig;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeTabServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.homeservice.HomeUserServiceEntity;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;
import com.wondersgroup.healthcloud.jpa.entity.user.AnonymousAccount;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.jpa.entity.user.member.FamilyMember;
import com.wondersgroup.healthcloud.jpa.enums.FamilyHealthStatusEnum;
import com.wondersgroup.healthcloud.jpa.enums.ServiceTypeEnum;
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
import com.wondersgroup.healthcloud.services.homeservice.HomeServices;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeServiceDTO;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeTabServiceDTO;
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

    @Autowired
    private HomeServices homeServicesImpl;

//    @Autowired
//    private H5ServiceSecurityUtil h5ServiceSecurityUtil;

    @Autowired
    RegisterInfoRepository registerInfoRepository;


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

    /**
     * 根据注册id查询家人信息
     *
     * @param uid
     * @return
     */
    private List<FamilyMemberInfo> getFamilyMembers(String uid) {

        List<FamilyMemberInfo> list = new ArrayList<FamilyMemberInfo>();
        List<FamilyMember> familyMembers = familyService.getFamilyMembers(uid);
        if (CollectionUtils.isEmpty(familyMembers)) {
            return list;
        }

        for (FamilyMember familyMember : familyMembers) {
            FamilyMemberInfo entity = new FamilyMemberInfo();
            entity.setUid(familyMember.getMemberId());
            entity.setRelation(familyMember.getRelation());

            RegisterInfo info = registerInfoRepository.findByRegisterid(familyMember.getMemberId());
            if (info == null) {//查询匿名表
                AnonymousAccount ano = anonymousAccountRepository.findOne(familyMember.getMemberId());
                if (ano != null) {
                    entity.setPersonCard(ano.getIdcard());
                    entity.setBirthday(ano.getBirthDate());
                    entity.setGender(ano.getSex());
                    entity.setHeadPhoto(ano.getHeadphoto());
                }
            } else {
                entity.setPersonCard(info.getPersoncard());
                entity.setGender(info.getGender());
                entity.setBirthday(info.getBirthday());
                entity.setHeadPhoto(info.getHeadphoto());
            }

            list.add(entity);

        }

        return list;
    }

    @Override
    public FamilyHealthDTO findfamilyHealth(RegisterInfo registerInfo, Map<String, Object> paramMap) {
        String apiMeasureUrl = String.valueOf(paramMap.get("apiMeasureUrl"));
        String apiUserhealthRecordUrl = String.valueOf(paramMap.get("apiUserhealthRecordUrl"));
        String apiVaccineUrl = String.valueOf(paramMap.get("apiVaccineUrl"));

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
        input.put("moreThanDays", (null == paramMap.get("userLessThanDays")) ? "7" : paramMap.get("userLessThanDays"));//个人取一周的数据
        input.put("limit", "10");
        input.put("personCard", "");
        input.put("cardType", "");
        input.put("cardId", "");

        //个人健康信息  健康状态0:无数据 1:良好 2:异常
        userHealth = getUserHealthInfo(input, apiMeasureUrl);


        //家人健康信息  健康状态 0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常

        List<FamilyMemberInfo> fmList = getFamilyMembers(registerInfo.getRegisterid());

        List<FamilyMemberItemDTO> familyMemberHealthRecordList = new ArrayList<FamilyMemberItemDTO>(); //住院信息集合
        Map<FamilyMemberInfo, UserHealthDTO> familyMemberHealthMap = new HashMap<FamilyMemberInfo, UserHealthDTO>(); //家庭成员健康集合
        List<FamilyMemberItemDTO> familyMemberDangerousItemList = new ArrayList<FamilyMemberItemDTO>(); //风险评估集合
        List<FamilyMemberItemDTO> familyMemberVaccinetemList = new ArrayList<FamilyMemberItemDTO>(); //疫苗信息集合


        if (!CollectionUtils.isEmpty(fmList)) {
            for (FamilyMemberInfo fm : fmList) {
                //1 家人最近住院信息
                if (StringUtils.isNotBlank(fm.getPersonCard())) {
                    UserHealthRecordDTO userhealthRecord = getFamilyLastHealthRecord(fm.getPersonCard(), apiUserhealthRecordUrl);
                    if (null != userhealthRecord) {
                        FamilyMemberItemDTO familyMemberItemDTO = buildFamilyLastHealthRecord(fm, userhealthRecord);
                        if (null != familyMemberItemDTO) {
                            familyMemberItemDTO.setUid(fm.getUid());
                            familyMemberHealthRecordList.add(familyMemberItemDTO);
                        }
                    }
                }


                // 2 家人健康信息
                Map<String, Object> familyMemberInput = new HashMap<String, Object>();
                familyMemberInput.put("registerId", fm.getUid());
                familyMemberInput.put("sex", fm.getGender());//性别
                familyMemberInput.put("moreThanDays", (null == paramMap.get("familyLessThanDays")) ? "30" : paramMap.get("familyLessThanDays"));//家人取一个月的数据
                familyMemberInput.put("limit", "10");
                familyMemberInput.put("personCard", "");
                familyMemberInput.put("cardType", "");
                familyMemberInput.put("cardId", "");
                UserHealthDTO familyMemberHealth = getUserHealthInfo(familyMemberInput, apiMeasureUrl);
                if (null != familyMemberHealth) {
                    familyMemberHealthMap.put(fm, familyMemberHealth);
                }


                //3 家人风险评估结果 (取一个月以内的数据)
                FamilyMemberItemDTO fItemDTO = buildFamilyDangerousResult(fm, (null == paramMap.get("familyLessThanDays")) ? 30 : Integer.parseInt(String.valueOf(paramMap.get("familyLessThanDays"))));
                if (null != fItemDTO) {
                    fItemDTO.setUid(fm.getUid());
                    familyMemberDangerousItemList.add(fItemDTO);
                }

                //4 育苗信息(默认30天内) (暂时不显示，以后显示打开代码注释)
               /* if (null != fm.getBirthday()) {
                    Integer vaccineLessThanDays = Integer.parseInt(String.valueOf((null == paramMap.get("vaccineLessThanDays")) ? "30" : paramMap.get("vaccineLessThanDays")));
                    FamilyMemberItemDTO vaccinetemDTO = buildFamilyVaccineDate(fm, apiVaccineUrl, vaccineLessThanDays);
                    if (null != vaccinetemDTO) {
                        vaccinetemDTO.setUid(fm.getUid());
                        familyMemberVaccinetemList.add(vaccinetemDTO);
                    }
                }*/

            }
        } else { //无家人
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_NO_FAMILY.getId());

        }


/////////////////////////////////////begin 获取最新数据///////////////////////////////////////////

        if (!CollectionUtils.isEmpty(familyMemberHealthRecordList)) {//有就医记录
            FamilyMemberItemDTO familyMemberItemDTO = getMaxRecord(familyMemberHealthRecordList);
            if (null != familyMemberItemDTO) {
                familyMember.getExceptionItems().add(familyMemberItemDTO);
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
            }
        }


        if (!CollectionUtils.isEmpty(familyMemberHealthMap)) {//家人健康信息集合
            int goodsHealthCount = 0;
            int haveNoDataCount = 0;

            Iterator<FamilyMemberInfo> it = familyMemberHealthMap.keySet().iterator();

            while (it.hasNext()) {  //统计 无数据/健康 两种状态的数据
                FamilyMemberInfo fm = it.next();
                UserHealthDTO item = familyMemberHealthMap.get(fm);
                if (UserHealthStatusEnum.HAVE_GOOD_HEALTH == UserHealthStatusEnum.getEnumById(item.getHealthStatus())) {
                    goodsHealthCount++;
                } else if (UserHealthStatusEnum.HAVE_NO_DATA == UserHealthStatusEnum.getEnumById(item.getHealthStatus())) {
                    haveNoDataCount++;
                } else if (UserHealthStatusEnum.HAVE_UNHEALTHY == UserHealthStatusEnum.getEnumById(item.getHealthStatus())) {//异常数据，显示给前端
                    FamilyMemberItemDTO ftemDTO = buildFamilyMemberHealth(fm, item);
                    ftemDTO.setUid(fm.getUid()); //用于分组
                    familyMember.getExceptionItems().add(ftemDTO);
                    familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());

                } else {//未知状态(默认为 有家人家人正常)
                    familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
                }

            }

            if (goodsHealthCount == familyMemberHealthMap.size()) {
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
            } else if (haveNoDataCount == familyMemberHealthMap.size()) {
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA.getId());
            } else if ((goodsHealthCount + haveNoDataCount) == familyMemberHealthMap.size()) {//家庭成员数据是 HAVE_FAMILY_AND_HEALTHY,HAVE_FAMILY_WITHOUT_DATA 两种状态的集合
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
            }
        }


        if (!CollectionUtils.isEmpty(familyMemberDangerousItemList)) {// 风险评估记录
            FamilyMemberItemDTO item = getMaxRecord(familyMemberDangerousItemList);
            if (null != item) {
                familyMember.getExceptionItems().add(item);
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
            }

        }

        if (!CollectionUtils.isEmpty(familyMemberVaccinetemList)) {  //疫苗通知集合
            FamilyMemberItemDTO item = getLastFurtherRecord(familyMemberVaccinetemList);//获取最近要打疫苗的记录
            if (null != item) {
                familyMember.getExceptionItems().add(item);
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
            }

        }


        //取每个家庭成员最新的一条数据,汇总成最新的两条
        if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus()) && !CollectionUtils.isEmpty(familyMember.getExceptionItems())) {
            familyMember.setExceptionItems(getMaxTwoList(familyMember.getExceptionItems()));
        }

/////////////////////////////////////end 获取最新数据///////////////////////////////////////////


        ////////////////////////////////////begin 根据状态 设置主标题，副标题//////////////////////

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
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus()) && familyMember.getExceptionItems().size() > 0) {
            familyMember.setMainTitle("");
            familyMember.setSubTitle("");
        } else {//有家庭成员，家人正常，无通知
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
            familyMember.setMainTitle("家庭成员 健康状况良好");
            familyMember.setSubTitle("家人健康状况良好，要继续保持。");
        }

        ////////////////////////////////////end 根据状态 设置主标题，副标题//////////////////////

        if (null == userHealth) {
            userHealth = new UserHealthDTO();
            userHealth.setMainTitle("请录入您的健康数据");
            userHealth.setSubTitle("添加您的健康数据>>");
            userHealth.setHealthStatus(UserHealthStatusEnum.HAVE_NO_DATA.getId());
        }

        //个人健康异常数据最多显示两条
        List<UserHealthItemDTO> userItemList = CollectionUtils.isEmpty(userHealth.getExceptionItems()) ? new ArrayList<UserHealthItemDTO>() : userHealth.getExceptionItems().size() > 2 ? userHealth.getExceptionItems().subList(0, 2) : userHealth.getExceptionItems();
        replaceUnitStr(userItemList);
        userHealth.setExceptionItems(userItemList);

        dto.setUserHealth(userHealth);
        dto.setFamilyMember(familyMember);

        return dto;
    }


    /**
     * 未来最近的记录
     *
     * @param list
     * @return
     */
    private FamilyMemberItemDTO getLastFurtherRecord(List<FamilyMemberItemDTO> list) {
        FamilyMemberItemDTO lastTimeDto = null;
        Iterator<FamilyMemberItemDTO> it = list.iterator();
        while (it.hasNext()) {
            FamilyMemberItemDTO dto = it.next();
            if (null == lastTimeDto) {
                lastTimeDto = dto;
            } else {
                if (null != dto.getTestTime() && null != lastTimeDto.getTestTime() && dto.getTestTime() < lastTimeDto.getTestTime()) {
                    lastTimeDto = dto;
                }
            }
        }

        return (lastTimeDto == null ? list.get(0) : lastTimeDto);
    }


    /**
     * 最新的就医记录
     *
     * @param list
     * @return
     */
    private FamilyMemberItemDTO getMaxRecord(List<FamilyMemberItemDTO> list) {
        FamilyMemberItemDTO maxTimeDto = null;
        Iterator<FamilyMemberItemDTO> it = list.iterator();
        while (it.hasNext()) {
            FamilyMemberItemDTO dto = it.next();
            if (null == maxTimeDto) {
                maxTimeDto = dto;
            } else {
                if (null != dto.getTestTime() && null != maxTimeDto.getTestTime() && dto.getTestTime() > maxTimeDto.getTestTime()) {
                    maxTimeDto = dto;
                }
            }
        }

        return (maxTimeDto == null ? list.get(0) : maxTimeDto);
    }


    /**
     * 根据 testTime字段 找出最大的两条记录
     *
     * @param allList
     * @return
     */
    private List<FamilyMemberItemDTO> getMaxTwoList(List<FamilyMemberItemDTO> allList) {

        if (CollectionUtils.isEmpty(allList) || allList.size() < 2) {
            return allList;
        }

        //先按照uid分组
        Map<String, List<FamilyMemberItemDTO>> relationMap = new HashMap<String, List<FamilyMemberItemDTO>>();

        for (FamilyMemberItemDTO dto : allList) {
            if (!relationMap.keySet().contains(dto.getUid())) {
                List<FamilyMemberItemDTO> valueList = new ArrayList<FamilyMemberItemDTO>();
                valueList.add(dto);
                relationMap.put(dto.getUid(), valueList);
            } else {
                relationMap.get(dto.getUid()).add(dto);
            }
        }


        //取每组里最新的一个
        List<FamilyMemberItemDTO> newList = new ArrayList<FamilyMemberItemDTO>();

        for (String key : relationMap.keySet()) {
            List<FamilyMemberItemDTO> tmpList = relationMap.get(key);
            if (tmpList.size() > 1) {
                FamilyHealthItemComparable sort = new FamilyHealthItemComparable();// false 按照 testTime 降序排序
                FamilyHealthItemComparable.sortASC = false;
                Collections.sort(tmpList, sort);
            }

            newList.add(tmpList.get(0));
        }


        //排序，找出最大的两条
        FamilyHealthItemComparable sort = new FamilyHealthItemComparable();
        FamilyHealthItemComparable.sortASC = false;
        Collections.sort(newList, sort);

        return CollectionUtils.isEmpty(newList) ? allList : newList.size() > 2 ? newList.subList(0, 2) : newList;
    }

    /**
     * 去掉单位
     *
     * @param userItemList
     */
    private void replaceUnitStr(List<UserHealthItemDTO> userItemList) {
        if (!CollectionUtils.isEmpty(userItemList)) {
            for (UserHealthItemDTO dto : userItemList) {
                dto.setData(dto.getData().replace("次/分钟", "").replace("mmHg", ""));
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
                RegisterInfo registerInfo = registerInfoRepository.findOne(userId);
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
                        dto.setHoplink(imageText.getHoplink().replace("{sfzh}", ""));
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
                    dto.setMainTitle("您的健康状况：良好");
                    dto.setSubTitle("你的健康状况良好，要继续保持哦");
                } else if (UserHealthStatusEnum.HAVE_UNHEALTHY == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(dataResponse.getData().getExceptionItems());
                    UserHealthItemComparable sort = new UserHealthItemComparable();// false 按照 testTime 降序排序
                    UserHealthItemComparable.sortASC = false;
                    Collections.sort(dto.getExceptionItems(), sort);

                    dto.setMainTitle("您的健康状况：" + dto.getExceptionItems().size() + "项异常");
                    dto.setSubTitle("[显示最新的2项异常指标数据]");

                } else {//未知 UserHealthStatusEnum 状态

                }
            }

        } else { //数据解析异常 TODO

        }

        return dto;
    }


    /**
     * 家人评估结果异常
     *
     * @param fm
     * @return
     */
    private FamilyMemberItemDTO buildFamilyDangerousResult(FamilyMemberInfo fm, Integer limitDays) {
        FamilyMemberItemDTO fItemDTO = null;
        limitDays = (null == limitDays) ? 30 : limitDays;
        Map<String, Object> resultMap = assessmentServiceImpl.getRecentAssessIsNormal(fm.getUid());
        if (!CollectionUtils.isEmpty(resultMap) && "2".equals(resultMap.get("state"))) { //state:2:风险人群
            Long testTime = 0L;
            String dateStr = String.valueOf(resultMap.get("date"));
            if (StringUtils.isNotBlank(dateStr)) {
                Date testDate = parseDate(dateStr, "YYYY-MM-DD");
                if (null != testDate) {
                    testTime = testDate.getTime();
                }
            }

            if (null != testTime && testTime > 0) {
                Calendar limitDay = Calendar.getInstance();
                limitDay.add(Calendar.DATE, -limitDays);//当前时间后退天数
                if (testTime >= limitDay.getTime().getTime()) { //没有被过滤
                    fItemDTO = new FamilyMemberItemDTO();
                    fItemDTO.setTestTime(testTime);
                    fItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
                    fItemDTO.setPrompt("风险评估结果 风险人群");
                }

            }

        }

        return fItemDTO;
    }

    /**
     * 疫苗接种
     *
     * @param fm
     * @return
     */
    private FamilyMemberItemDTO buildFamilyVaccineDate(FamilyMemberInfo fm, String apiVaccineUrl, Integer vaccineLessThanDays) {
        FamilyMemberItemDTO fItemDTO = null;
        String birthDate = getBirthDay(fm); // 计算出孩子的生日 16岁以下的,不是大人
        if (StringUtils.isBlank(birthDate)) {
            return null;
        }

        Map<String, Object> input = new HashMap<String, Object>();
        input.put("birthday", birthDate);
        String leftDays = healthApiClient.getLeftDaysByBirth(apiVaccineUrl, input);
        if (StringUtils.isNotBlank(leftDays)) {
            Integer leftDays_ = Integer.parseInt(leftDays);

            if (leftDays_ < vaccineLessThanDays) {
                fItemDTO = new FamilyMemberItemDTO();
                fItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
                fItemDTO.setPrompt("疫苗接种 " + leftDays_ + "天后");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, leftDays_);//当前时间相加天数
                fItemDTO.setTestTime(cal.getTime().getTime());
            }

        }

        return fItemDTO;
    }


    /**
     * 获得生日
     *
     * @param fm
     * @return
     */
    private String getBirthDay(FamilyMemberInfo fm) {
        String birthDay = null;
        Integer age = getAge(fm.getBirthday());
        if (null != age && age < 16) {
            birthDay = new SimpleDateFormat("yyyy-MM-dd").format(fm.getBirthday());
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
    private FamilyMemberItemDTO buildFamilyMemberHealth(FamilyMemberInfo fm, UserHealthDTO familyMemberHealth) {
        FamilyMemberItemDTO ftemDTO = null;

        if (!CollectionUtils.isEmpty(familyMemberHealth.getExceptionItems())) {
            for (UserHealthItemDTO dto : familyMemberHealth.getExceptionItems()) {
                ftemDTO = new FamilyMemberItemDTO();
                ftemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
                ftemDTO.setPrompt(dto.getName() + (dto.getHightAndLow().equals("1") ? "偏高" : "偏低"));
                ftemDTO.setTestTime(dto.getTestTime());
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
    private FamilyMemberItemDTO buildFamilyLastHealthRecord(FamilyMemberInfo fm, UserHealthRecordDTO userhealthRecord) {

        FamilyMemberItemDTO familyMemberItemDTO = new FamilyMemberItemDTO();
        familyMemberItemDTO.setRelationship(FamilyMemberRelation.getName(fm.getRelation()));
        familyMemberItemDTO.setPrompt("有新的就医记录");
        if (StringUtils.isNotBlank(userhealthRecord.getDate())) {
            Date compareDate = parseDate(userhealthRecord.getDate(), "YYYY-MM-DD");
            if (null != compareDate) {
                familyMemberItemDTO.setTestTime(compareDate.getTime());
            }
        }

        return familyMemberItemDTO;

    }

    @Override
    public List<HomeServiceDTO> findMyHomeServices(Map paramMap) {
        List<HomeServiceDTO> allServicesList = new ArrayList<HomeServiceDTO>();

        // 优先根据查询关系表里的 我的服务 信息
        List<HomeServiceEntity> myHomeServicesList = homeServicesImpl.findMyHomeServices(paramMap);

        if (!CollectionUtils.isEmpty(myHomeServicesList)) {
            for (HomeServiceEntity entity : myHomeServicesList) {
                allServicesList.add(new HomeServiceDTO(entity));
            }
        } else { //如果没有， 设置默认服务为 我的服务
            paramMap.put("serviceType", ServiceTypeEnum.DEFAULT_SERVICE.getType());
            List<HomeServiceEntity> defaultList = homeServicesImpl.findHomeServiceByCondition(paramMap);
            if (!CollectionUtils.isEmpty(defaultList)) {
                for (HomeServiceEntity entity : defaultList) {
                    allServicesList.add(new HomeServiceDTO(entity));
                }
            }
        }

        return allServicesList;
    }

    @Override
    public List<HomeServiceDTO> findBaseServices(Map paramMap) {
        List<HomeServiceDTO> baseServiceList = homeServicesImpl.findMyBaseHomeService(paramMap);
        return baseServiceList;
    }

    @Override
    public void editHomeServices(RegisterInfo registerInfo, List<String> editServiceIds) {
        Map paramMap = new HashMap();
        paramMap.put("registerId", registerInfo.getRegisterid());
        List<HomeUserServiceEntity> oldUserServicelist = homeServicesImpl.findHomeUserServiceByCondition(paramMap);
        List<HomeServiceEntity> oldServicelist = new ArrayList<HomeServiceEntity>(); //已有的服务
        for (HomeUserServiceEntity oldEntity : oldUserServicelist) {
            HomeServiceEntity entity = new HomeServiceEntity();
            entity.setId(oldEntity.getServiceId());
            oldServicelist.add(entity);
        }


        List<HomeServiceEntity> newServices = new ArrayList<HomeServiceEntity>(); //新编辑的服务
        for (String id : editServiceIds) {
            HomeServiceEntity entity = new HomeServiceEntity();
            entity.setId(id);
            newServices.add(entity);
        }
        if (!CollectionUtils.isEmpty(newServices)) {
            homeServicesImpl.editMyService(oldServicelist, newServices, registerInfo.getRegisterid());
        }
    }

    @Override
    public List<HomeTabServiceDTO> findHomeTableService(Map paramMap) {
        List<HomeTabServiceEntity> entitiyList = homeServicesImpl.findMyHomeTabService(paramMap);
        if(CollectionUtils.isEmpty(entitiyList)){
            return Collections.EMPTY_LIST;
        }

        List<HomeTabServiceDTO> dtoList = new ArrayList<HomeTabServiceDTO>();

        for(HomeTabServiceEntity entity:entitiyList){
            dtoList.add(new HomeTabServiceDTO(entity));
        }
        return dtoList;
    }

    /**
     * 过滤掉默认服务
     *
     * @param defaultUserServicelist
     * @param exitsEditServiceList
     */
    void filterDefaultService(List<HomeServiceEntity> defaultUserServicelist, List<HomeServiceEntity> exitsEditServiceList) {
        for (HomeServiceEntity defaultServiceEntity : defaultUserServicelist) {
            Iterator<HomeServiceEntity> it = exitsEditServiceList.iterator();
            while (it.hasNext()) {
                HomeServiceEntity entity = it.next();
                if (defaultServiceEntity.getId().equals(entity.getId())) {
                    it.remove();
                }
            }

        }
    }

    /**
     * for4.3 健康管理 个人和家庭首页显示
     *
     * @param registerInfo
     * @param paramMap
     * @return
     */
    @Override
    public FamilyHealthJKGLDTO findfamilyHealthForJKGL(RegisterInfo registerInfo, Map<String, Object> paramMap) {
        String apiMeasureUrl = String.valueOf(paramMap.get("apiMeasureUrl"));

        if (StringUtils.isBlank(apiMeasureUrl)) {
            logger.info("apiMeasureUrl  is blank ", apiMeasureUrl);
            return null;
        }

        FamilyHealthJKGLDTO dto = new FamilyHealthJKGLDTO();
        UserHealthJKGLDTO userHealth = null;   //用户健康对象
        FamilyMemberJKGLDTO familyMember = new FamilyMemberJKGLDTO();
        familyMember.setExceptionItems(new ArrayList<FamilyHealthItemJKGLDTO>());
        familyMember.setHealthItems(new ArrayList<FamilyHealthItemJKGLDTO>());


        Map<String, Object> input = new HashMap<String, Object>();
        input.put("registerId", registerInfo.getRegisterid());
        input.put("sex", registerInfo.getGender());
        input.put("moreThanDays", (null == paramMap.get("userLessThanDays")) ? "30" : paramMap.get("userLessThanDays"));//个人取一个月的数据
        input.put("limit", "10");
        input.put("personCard", "");
        input.put("cardType", "");
        input.put("cardId", "");

        //个人健康信息  健康状态0:无数据 1:良好 2:异常
        userHealth = getUserHealthInfoJKGL(input, apiMeasureUrl);


        //家人健康信息  健康状态 0:无家人 1:有家人家人无数据 2:有家人家人正常 3:异常

        List<FamilyMemberInfo> fmList = getFamilyMembers(registerInfo.getRegisterid());
        Map<FamilyMemberInfo, UserHealthJKGLDTO> familyMemberHealthMap = new HashMap<FamilyMemberInfo, UserHealthJKGLDTO>(); //家庭成员健康集合

        if (!CollectionUtils.isEmpty(fmList)) {
            for (FamilyMemberInfo fm : fmList) {
                //  家人健康信息,检查对方是否可以查看健康档案
                Boolean canReadRecord = familyService.canReadRecord(fm.getUid(),registerInfo.getRegisterid());
                if(!canReadRecord){
                    continue;
                }
                Map<String, Object> familyMemberInput = new HashMap<String, Object>();
                familyMemberInput.put("registerId", fm.getUid());
                familyMemberInput.put("sex", fm.getGender());//性别
                familyMemberInput.put("moreThanDays", (null == paramMap.get("familyLessThanDays")) ? "30" : paramMap.get("familyLessThanDays"));//家人取一个月的数据
                familyMemberInput.put("limit", "10");
                familyMemberInput.put("personCard", "");
                familyMemberInput.put("cardType", "");
                familyMemberInput.put("cardId", "");
                UserHealthJKGLDTO familyMemberHealth = getUserHealthInfoJKGL(familyMemberInput, apiMeasureUrl);
                if (null != familyMemberHealth) {
                    familyMemberHealthMap.put(fm, familyMemberHealth);
                }
            }
        } else { //无家人
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_NO_FAMILY.getId());
            familyMember.setHealthItems(null);
            familyMember.setExceptionItems(null);
        }

/////////////////////////////////////begin 获取最新数据///////////////////////////////////////////
        FamilyMemberInfo maxExTimeF = null;
        UserHealthItemDTO maxExTimeU = null; 
        FamilyMemberInfo maxHealthTimeF = null;
        UserHealthItemDTO maxHealthTimeU = null; 
        
        if (!CollectionUtils.isEmpty(familyMemberHealthMap)&&!CollectionUtils.isEmpty(fmList)) {//家人健康信息集合
            int exNum = 0;
            Collection<UserHealthJKGLDTO> values = familyMemberHealthMap.values();
            Iterator<UserHealthJKGLDTO> iterator = values.iterator();
            while(iterator.hasNext()){
                UserHealthJKGLDTO healthJKGLDTO = iterator.next();
                if(null==healthJKGLDTO.getExceptionItems()) continue;
                exNum+=healthJKGLDTO.getExceptionItems().size();
            }
            Iterator<FamilyMemberInfo> it = familyMemberHealthMap.keySet().iterator();
            while (it.hasNext()) {  
                FamilyMemberInfo fm = it.next();
                UserHealthJKGLDTO item = familyMemberHealthMap.get(fm);
                //获取最新的健康数据
                if(exNum==0){
                    if (!CollectionUtils.isEmpty(item.getHealthItems())) {
                        for(UserHealthItemDTO dto1:item.getHealthItems()){
                            if(maxHealthTimeU==null){
                                maxHealthTimeU = dto1;
                                maxHealthTimeF = fm;
                            }
                            if(dto1.getTestTime() >maxHealthTimeU.getTestTime()){
                                maxHealthTimeU = dto1;
                                maxHealthTimeF = fm;
                            }
                        }
                    }
                }else{
                    if (!CollectionUtils.isEmpty(item.getExceptionItems())) {
                        for(UserHealthItemDTO dto1:item.getExceptionItems()){
                            if(maxExTimeU == null){
                                maxExTimeU = dto1;
                                maxExTimeF = fm;
                            }
                            if(dto1.getTestTime() >maxExTimeU.getTestTime() ){
                                maxExTimeU = dto1;
                                maxExTimeF = fm;
                            }
                        }
                    } 
                }
                
            }
            if(null !=maxExTimeF&&null!=maxExTimeU){
                UserHealthJKGLDTO userHealthJKGLDTO = familyMemberHealthMap.get(maxExTimeF);
                for(UserHealthItemDTO dto1 :userHealthJKGLDTO.getExceptionItems()){
                    FamilyHealthItemJKGLDTO familyHealthItemJKGLDTO= new FamilyHealthItemJKGLDTO();
                    familyHealthItemJKGLDTO.setName(dto1.getName());
                    familyHealthItemJKGLDTO.setData(dto1.getData());
                    familyHealthItemJKGLDTO.setHightAndLow(dto1.getHightAndLow());
                    familyHealthItemJKGLDTO.setTestTime(dto1.getTestTime());
                    familyHealthItemJKGLDTO.setTestPeriod(dto1.getTestPeriod());
                    familyMember.getExceptionItems().add(familyHealthItemJKGLDTO);
                }
                familyMember.setHeadPhoto(maxExTimeF.getHeadPhoto());
                familyMember.setRelation(maxExTimeF.getRelation());
                familyMember.setUid(maxExTimeF.getUid());
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY.getId());
                familyMember.setExceptionItems(familyMember.getExceptionItems().size() > 2 ? familyMember.getExceptionItems().subList(0, 2) : familyMember.getExceptionItems()); 
                familyMember.setHealthItems(null);
            }else if(null!=maxHealthTimeF&&null!=maxHealthTimeU){
                UserHealthJKGLDTO userHealthJKGLDTO = familyMemberHealthMap.get(maxHealthTimeF);
                for (UserHealthItemDTO dto1 : userHealthJKGLDTO.getHealthItems()) {
                    FamilyHealthItemJKGLDTO familyHealthItemJKGLDTO= new FamilyHealthItemJKGLDTO();
                    familyHealthItemJKGLDTO.setName(dto1.getName());
                    familyHealthItemJKGLDTO.setData(dto1.getData());
                    familyHealthItemJKGLDTO.setHightAndLow(dto1.getHightAndLow());
                    familyHealthItemJKGLDTO.setTestPeriod(dto1.getTestPeriod());
                    familyHealthItemJKGLDTO.setTestTime(dto1.getTestTime());
                    familyMember.getHealthItems().add(familyHealthItemJKGLDTO);
                }
                familyMember.setHeadPhoto(maxHealthTimeF.getHeadPhoto());
                familyMember.setRelation(maxHealthTimeF.getRelation());
                familyMember.setUid(maxHealthTimeF.getUid());
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY.getId());
                familyMember.setHealthItems(familyMember.getHealthItems().size()>2 ? familyMember.getHealthItems().subList(0, 2):familyMember.getHealthItems());
                familyMember.setExceptionItems(null);
            }else{
                familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA.getId());
                familyMember.setHealthItems(null);
                familyMember.setExceptionItems(null);
            }
            
        }else if(CollectionUtils.isEmpty(familyMemberHealthMap)&&!CollectionUtils.isEmpty(fmList)){
            familyMember.setHealthStatus(FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA.getId());
            familyMember.setHealthItems(null);
            familyMember.setExceptionItems(null);
        }
/////////////////////////////////////end 获取最新数据///////////////////////////////////////////


        ////////////////////////////////////begin 根据状态 设置主标题，副标题//////////////////////

                /*HAVE_NO_FAMILY("0","无家人"),
                HAVE_FAMILY_WITHOUT_DATA("1","有家人家人无数据"),
                HAVE_FAMILY_AND_HEALTHY("2","有家人家人正常"),
                HAVE_FAMILY_AND_UNHEALTHY("3","异常");*/

        if (FamilyHealthStatusEnum.HAVE_NO_FAMILY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("请添加您的家庭成员");
            familyMember.setSubTitle("");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_HEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle(FamilyMemberRelation.getName(familyMember.getRelation())+"近一月健康状况良好");
            familyMember.setSubTitle("");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_WITHOUT_DATA == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus())) {
            familyMember.setMainTitle("您的家人近一月无健康数据");
            familyMember.setSubTitle("");
        } else if (FamilyHealthStatusEnum.HAVE_FAMILY_AND_UNHEALTHY == FamilyHealthStatusEnum.getEnumById(familyMember.getHealthStatus()) && familyMember.getExceptionItems().size() > 0) {
            familyMember.setMainTitle(FamilyMemberRelation.getName(familyMember.getRelation())+"近一月有" + intNumToString(familyMember.getExceptionItems().size()) + "项异常");
            familyMember.setSubTitle("");
        } 

        ////////////////////////////////////end 根据状态 设置主标题，副标题//////////////////////

        if (null == userHealth) {
            userHealth = new UserHealthJKGLDTO();
            userHealth.setMainTitle("您近一月无健康数据");
            userHealth.setSubTitle("");
            userHealth.setHealthStatus(UserHealthStatusEnum.HAVE_NO_DATA.getId());
        }

        //个人健康异常数据最多显示两条
        List<UserHealthItemDTO> userItemList=null;
        if(!CollectionUtils.isEmpty(userHealth.getExceptionItems())){
            userItemList=userHealth.getExceptionItems().size() > 2 ? userHealth.getExceptionItems().subList(0, 2) : userHealth.getExceptionItems();
            userHealth.setExceptionItems(userItemList);
        }else if(!CollectionUtils.isEmpty(userHealth.getHealthItems())){
            userItemList=userHealth.getHealthItems().size() > 2 ? userHealth.getHealthItems().subList(0, 2) :userHealth.getHealthItems();
            userHealth.setHealthItems(userItemList);
        }else{
            userHealth.setExceptionItems(userItemList);
            userHealth.setHealthItems(userItemList);
        }
        //replaceUnitStr(userItemList);
        userHealth.setHeadPhoto(registerInfo.getHeadphoto());

        dto.setUserHealth(userHealth);
        dto.setFamilyMember(familyMember);

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
    private UserHealthJKGLDTO getUserHealthInfoJKGL(Map<String, Object> userInfoMap, String apiMeasureUrl) {
        UserHealthJKGLDTO dto = null;
        String userHealthResponse = healthApiClient.userHealth(apiMeasureUrl, userInfoMap);
        if (StringUtils.isNotBlank(userHealthResponse)) {

            DataMsg<HealthResponse<List<UserHealthItemDTO>>> dataResponse = JsonConverter.toObject(userHealthResponse, new TypeReference<DataMsg<HealthResponse<List<UserHealthItemDTO>>>>() {
            });

            if (null != dataResponse && null != dataResponse.getData() && StringUtils.isNotBlank(dataResponse.getData().getHealthStatus())) {
                // dataResponse.getData().getHealthStatus() 状体  UserHealthStatusEnum 枚举

                if (UserHealthStatusEnum.HAVE_NO_DATA == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthJKGLDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(new ArrayList<UserHealthItemDTO>());
                    dto.setMainTitle("您近一月无健康数据");
                    dto.setSubTitle("");
                } else if (UserHealthStatusEnum.HAVE_GOOD_HEALTH == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthJKGLDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setHealthItems(dataResponse.getData().getHealthList());
                    UserHealthItemComparable sort = new UserHealthItemComparable();// false 按照 testTime 降序排序
                    UserHealthItemComparable.sortASC = false;
                    Collections.sort(dto.getHealthItems(), sort);
                    dto.setMainTitle("您近一月健康状况良好");
                    dto.setSubTitle("");

                } else if (UserHealthStatusEnum.HAVE_UNHEALTHY == UserHealthStatusEnum.getEnumById(dataResponse.getData().getHealthStatus())) {
                    dto = new UserHealthJKGLDTO();
                    dto.setHealthStatus(dataResponse.getData().getHealthStatus());
                    dto.setExceptionItems(dataResponse.getData().getExceptionItems());
                    UserHealthItemComparable sort = new UserHealthItemComparable();// false 按照 testTime 降序排序
                    UserHealthItemComparable.sortASC = false;
                    Collections.sort(dto.getExceptionItems(), sort);
                    dto.setMainTitle("您近一月有" + intNumToString(dto.getExceptionItems().size()) + "项异常");
                    dto.setSubTitle("");

                } else {//未知 UserHealthStatusEnum 状态

                }
            }

        } else { //数据解析异常 TODO

        }

        return dto;
    }
    
    private String intNumToString(int i){
        String item="";
        switch (i) {
        case 1:
            item="一";
            break;
        case 2:
            item="两";
            break;
        case 3:
            item="三";
            break;
        case 4:
            item="四";
            break;
        }
        return item;
    }
}
