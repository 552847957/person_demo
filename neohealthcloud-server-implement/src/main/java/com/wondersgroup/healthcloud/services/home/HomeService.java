package com.wondersgroup.healthcloud.services.home;

import com.wondersgroup.healthcloud.services.home.dto.advertisements.CenterAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.advertisements.SideAdDTO;
import com.wondersgroup.healthcloud.services.home.dto.cloudTopLine.CloudTopLineDTO;
import com.wondersgroup.healthcloud.services.home.dto.familyHealth.FamilyHealthDTO;
import com.wondersgroup.healthcloud.services.home.dto.functionIcons.FunctionIconsDTO;
import com.wondersgroup.healthcloud.services.home.dto.modulePortal.ModulePortalDTO;
import com.wondersgroup.healthcloud.services.home.dto.specialService.SpecialServiceDTO;
import com.wondersgroup.healthcloud.services.user.dto.Session;

import java.util.List;

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
     * @param registerId 当前用户ID
     * @return
     */
    public FamilyHealthDTO findfamilyHealth(String registerId);


    /**
     * 中央区广告
     * @return
     */
    public List<CenterAdDTO> findCenterAdDTO(String mainArea);

    /**
     * 侧边浮层广告
     * @return
     */
    public SideAdDTO findSideAdDTO(String mainArea);

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



}
