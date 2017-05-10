package com.wondersgroup.healthcloud.services.home;

import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyHealthDTO;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.homeservice.dto.HomeServiceDTO;
import com.wondersgroup.healthcloud.services.user.dto.Session;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/13.
 */
public interface HomeService {

    /**模块入口（慢病模块）
     * @return
     */
    public List<ModulePortalDTO> findModulePortal();

    /**
     * 云头条
     * @return
     */
    public CloudTopLineDTO findCloudTopLine();


    /**
     * 家庭健康栏目
     * @param registerInfo 当前登录用户
     * @param paramMap 参数
     * @return
     */
    public FamilyHealthDTO findfamilyHealth(RegisterInfo registerInfo, Map<String,Object> paramMap);


    /**
     * 中央区广告
     * @return
     */
    public List<CenterAdDTO> findCenterAdDTO(String mainArea);

    /**
     * 侧边浮层广告
     * @return
     */
    public SideAdDTO findSideAdDTO(String mainArea,String specArea);

    /**
     * 特色服务
     * @return
     */
    public List<SpecialServiceDTO> findSpecialServiceDTO(Session session,String appVersion,String mainArea, String specArea);

    /**
     * 主要功能区
     * @return
     */
    public List<FunctionIconsDTO> findFunctionIconsDTO(Session session,String appVersion,String mainArea, String specArea);

    /**
     * 首页我的服务(默认服务+基础服务)
     * @param paramMap
     * @return
     */
    public List<HomeServiceDTO> findMyHomeServices(Map paramMap);


    /**
     * 条件查询服务
     * @param paramMap
     * @return
     */
    public List<HomeServiceDTO> findBaseServices(Map paramMap);

    /**
     * 编辑我的服务
     * @param registerInfo
     * @param editServiceIds
     */
    void editHomeServices(RegisterInfo registerInfo,List<String> editServiceIds);

}
