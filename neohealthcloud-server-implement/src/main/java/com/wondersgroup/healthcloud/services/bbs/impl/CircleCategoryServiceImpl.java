package com.wondersgroup.healthcloud.services.bbs.impl;

import com.google.common.collect.Maps;
import com.wondersgroup.healthcloud.jpa.constant.CircleConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleCategoryRepository;
import com.wondersgroup.healthcloud.services.bbs.CircleCategoryService;
import com.wondersgroup.healthcloud.services.bbs.CircleService;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleCategoryDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by limenghua on 2016/8/21.
 *
 * @author limenghua
 */
@Service
public class CircleCategoryServiceImpl implements CircleCategoryService {
    private static final Logger logger = LoggerFactory.getLogger("CircleCategoryServiceImpl");
    @Autowired
    private CircleCategoryRepository circleCategoryRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CircleService circleService;

    @Override
    public CircleCategory saveCircleCategory(CircleCategory circleCategory) {
        CircleCategory result = circleCategoryRepository.saveAndFlush(circleCategory);
        return result;
    }

    @Override
    public boolean ifCategoryNameExist(String name,Integer id) {
        boolean result = false;
        if (id == null) {
            CircleCategory exist = circleCategoryRepository.queryByName(name);
            if (exist != null) {// 名称已存在
                logger.info(String.format("category name [%s] already exist", exist.getName()));
                result = true;
            }
        } else {
            CircleCategory exist = circleCategoryRepository.queryByNameAndNotEqualsId(name, id);
            if (exist != null) {// 除了该id，还有一样的名字
                logger.info(String.format("[update]category name [%s] already exist", exist.getName()));
                result = true;
            }
        }
        return result;
    }

    @Override
    public List<CircleCategoryDto> searchCategory(String name, String delFlag, int pageNo, int pageSize) {
        // 注：不显示的圈子，不进行“|”拼接
        String searchSql = "SELECT " +
                " ct.id, " +
                " ct.`name`, " +
                " ct.rank, " +
                " ct.del_flag, " +
                " GROUP_CONCAT(ci.`name` SEPARATOR '|') AS c_name " +
                "FROM " +
                " tb_bbs_circle_category ct " +
                "LEFT JOIN (select * from tb_bbs_circle where del_flag = '0') ci ON ci.cate_id = ct.id " +
                "GROUP BY " +
                " ct.id " +
                "HAVING " +
                " 1 = 1 ";
        searchSql = appendWhereSql(searchSql, name, delFlag);
        // 排序
        String orderSql = " order by ct.rank desc,ct.create_time desc ";
        searchSql += orderSql;
        // 分页
        int offset = (pageNo - 1) * pageSize;
        String limitSql = String.format(" limit %s,%s", offset, pageSize);
        searchSql += limitSql;

        List<Map<String, Object>> list = jdbcTemplate.queryForList(searchSql);
        List<CircleCategoryDto> dtoList = null;

        if (list != null && list.size() > 0) {
            dtoList = new ArrayList<>();
            for (Map<String, Object> map : list) {
                CircleCategoryDto dto = initCatogoryDto(map);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    private String appendWhereSql(String sql, String name, String delFlag) {
        StringBuffer whereSql = new StringBuffer("");
        if (StringUtils.isNotBlank(name)) {
            whereSql.append(" AND ct.name LIKE '%" + name + "%'");
        }
        if (StringUtils.isNotBlank(delFlag)) {
            whereSql.append(" AND ct.del_flag = '" + delFlag + "'");
        }
        // 追加条件查询语句
        sql += whereSql.toString();
        return sql;
    }

    @Override
    public int searchCategoryCount(String name, String delFlag) {
        int count = 0;
        String countSql = "SELECT " +
                " count(*) " +
                "FROM " +
                " ( " +
                "SELECT " +
                " ct.id, " +
                " ct.`name`, " +
                " ct.rank, " +
                " ct.del_flag, " +
                " GROUP_CONCAT(ci.`name` SEPARATOR '|') AS c_name " +
                "FROM " +
                " tb_bbs_circle_category ct " +
                "LEFT JOIN (select * from tb_bbs_circle where del_flag = '0') ci ON ci.cate_id = ct.id " +
                "GROUP BY " +
                " ct.id " +
                "HAVING " +
                " 1 = 1 ";
        countSql = appendWhereSql(countSql, name, delFlag);
        countSql += " ) v";
        Object result = jdbcTemplate.queryForObject(countSql, Integer.class);
        if (result != null) {
            count = Integer.parseInt(String.valueOf(result));
        }
        return count;
    }

    @Override
    public LinkedHashMap<Integer, String> getCategoryComboMap() {
        LinkedHashMap<Integer, String> resultMap = Maps.newLinkedHashMap();
        List<CircleCategory> cList = this.findAllOrderByRankDesc();
        if (cList != null && cList.size() > 0) {
            for (CircleCategory circleCategory : cList) {
                resultMap.put(circleCategory.getId(), circleCategory.getName());
            }
        }
        return resultMap;
    }

    @Override
    public List<CircleCategoryDto> getCategoryComboList() {
        List<CircleCategory> cList = this.findAllOrderByRankDesc();
        List<CircleCategoryDto> dtoList = new ArrayList<>();
        if (cList != null && cList.size() > 0) {
            for (CircleCategory circleCategory : cList) {
                CircleCategoryDto dto = new CircleCategoryDto();
                BeanUtils.copyProperties(circleCategory, dto);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public List<CircleCategory> findAllOrderByRankDesc() {
        Sort sort = new Sort(Sort.Direction.DESC,"rank");
        List<CircleCategory> list = circleCategoryRepository.findAll(sort);
        return list;
    }

    @Override
    public CircleCategory findOne(int id) {
        CircleCategory circleCategory = circleCategoryRepository.findOne(id);
        return circleCategory;
    }

    private CircleCategoryDto initCatogoryDto(Map<String, Object> map) {
        CircleCategoryDto dto = new CircleCategoryDto();
        try {
            dto.setId(Integer.parseInt(map.get("id").toString()));
            String name = String.valueOf(map.get("name"));
            dto.setName(name);
            dto.setRank(Integer.parseInt(String.valueOf(map.get("rank"))));
            dto.setDelFlag(String.valueOf(map.get("del_flag")));
            String cName = String.valueOf(map.get("c_name"));
            // 如果是推荐分类，统计下属圈子的方式和其他圈子不同
            if (CircleConstant.RECOMMEND_CATE_NAME.equals(name)) {
                cName = circleService.getRecommendCircleNames();
            }
            if (StringUtils.isBlank(cName) || "null".equals(cName)) {
                cName = "";
            }
            dto.setCircleNames(cName);
        } catch (NumberFormatException e) {
            logger.error("设置分类查询结果时出错", e);
        }
        return dto;
    }
}
