package com.wondersgroup.healthcloud.services.modulePortal;

import com.wondersgroup.healthcloud.jpa.entity.moduleportal.ModulePortal;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/12.
 */
public interface ModulePortalService {
    /**
     * 保存
     * @param entity
     * @return
     */
    ModulePortal saveModulePortal(ModulePortal entity);

    /**
     * 查询所有的模块入口
     * @return
     */
    List<ModulePortal> queryAllModulePortal();


    /**
     * 根据条件查询
     * @param paramMap
     * @return
     */
    List<ModulePortal> queryModulePortalByCondition(Map<String,Object> paramMap);

    /**
     * 根据主键修改
     * @param entity
     * @return
     */
    boolean updateModulePortalById(ModulePortal entity);


    /** 删除
     * @param id
     * @return
     */
    boolean delModulePortalById(Integer id);

}
