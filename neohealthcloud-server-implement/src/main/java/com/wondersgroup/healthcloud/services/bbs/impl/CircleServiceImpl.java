package com.wondersgroup.healthcloud.services.bbs.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import com.wondersgroup.healthcloud.common.utils.AppUrlSchemaUtils;
import com.wondersgroup.healthcloud.exceptions.CommonException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.constant.CircleConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleCategoryRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.CircleRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.TopicTabRepository;
import com.wondersgroup.healthcloud.jpa.repository.bbs.UserCircleRepository;
import com.wondersgroup.healthcloud.services.bbs.CircleService;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.AdminCircleDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleBannerDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleCategoryDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleInfoDto;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.CircleListDto;

/**
 * Created by ys on 2016/08/11.
 *
 * @author ys
 */
@Service("circleService")
public class CircleServiceImpl implements CircleService {

    private static final Logger logger = LoggerFactory.getLogger("CircleServiceImpl");

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TopicTabRepository topicTabRepository;

    @Autowired
    private CircleCategoryRepository circleCategoryRepository;
    @Autowired
    private CircleRepository circleRepository;
    @Autowired
    private UserCircleRepository userCircleRepository;

    @Override
    public Circle getCircleInfoById(Integer circleId) {
        return circleRepository.findOne(circleId);
    }

    @Override
    public List<CircleCategoryDto> getCircleCategoryDtoList() {
        List<CircleCategoryDto> cateDtoList = new ArrayList<>();

        List<CircleCategory> catList = null;
        catList = circleCategoryRepository.queryByDelFlagOrderByRankDesc("0");
        if (catList != null && catList.size() > 0) {
            for (CircleCategory circleCategory : catList) {
                CircleCategoryDto dto = new CircleCategoryDto();
                BeanUtils.copyProperties(circleCategory, dto);
                cateDtoList.add(dto);
            }
            CircleCategoryDto categoryDto = new CircleCategoryDto();
            CircleCategoryDto copyRecCirle  = copyRecommendCirle(categoryDto);
            //将推荐的放到第一个位置
            cateDtoList.add(0,copyRecCirle);
        }
        return cateDtoList;
    }

    @Override
    public List<CircleBannerDto> getCircleBannerList() {
        List<CircleBannerDto> dtoList = new ArrayList<>();
        Date now = new Date();
        String sql = "SELECT " +
                " t.id, " +
                " t.img_url, " +
                " t.hoplink, " +
                " t.sequence " +
                " FROM " +
                " app_tb_neoimage_text t " +
                " WHERE " +
                " t.adcode = 13 " +
                " AND start_time <= '" + DateUtils.sdf.format(now) + "' " +
                " AND end_time >= '" + DateUtils.sdf.format(now) + "' " +
                " AND t.del_flag = '0' order by sequence DESC";
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
        if (data != null && data.size() > 0) {
            for (Map<String, Object> map : data) {
                CircleBannerDto dto = new CircleBannerDto();
                dto.setId(String.valueOf(map.get("id")));
                dto.setPic(String.valueOf(map.get("img_url")));
                Integer topicId = Integer.parseInt(String.valueOf(map.get("hoplink")));
                dto.setTopicId(topicId);// 存的是id
                dto.setPicOrder(Integer.parseInt(String.valueOf(map.get("sequence"))));
                dto.setJumpUrl(AppUrlSchemaUtils.bbsTopicView(topicId));
                dtoList.add(dto);
            }
        } else {
            logger.info("未查询到可用的圈子banner图");
        }
        return dtoList;
    }

    @Override
    public List<CircleListDto> getCirclesByCId(Integer categoryId, String uId ,Integer flag,Integer pageSize) {
        List<CircleListDto> cDtoList = new ArrayList<>();
        List<Circle> cList = null;
        // 查询是否为“推荐圈子”分类
        if (categoryId == CircleConstant.CIRCLE_CATEGORY_ID) {
            cList = getRecommendCircles(flag,pageSize);
            // circleRepository.queryByIsRecommendAndDelFlagOrderByRankDesc(1, "0");
        } else {
            cList = getCateIdCircles(categoryId,flag,pageSize);
            //circleRepository.queryByCateIdAndDelFlagOrderByRankDesc(categoryId, "0");
        }

        if (cList != null && cList.size() > 0) {
            for (Circle circle : cList) {
                CircleListDto dto = new CircleListDto();
                BeanUtils.copyProperties(circle, dto);
                // 设置是否加入了圈子
                UserCircle userCircle = userCircleRepository.queryByUIdAndCircleIdAndDelFlag(uId, circle.getId(), "0");
                if (userCircle != null) {
                    dto.setIfAttent(1);
                }// if end
                cDtoList.add(dto);
            }// for end
        }// if end

        return cDtoList;
    }

    @Override
    public UserCircle getAndCheckIsDefaultJoin(Integer circleId, String uid) {
        UserCircle userCircle = userCircleRepository.queryByUIdAndCircleId(uid, circleId);
        if (null != userCircle){
            if (userCircle.getDelFlag().equals("0")){
                return userCircle;
            }else {
                return null;
            }
        }else {
            Circle circle = circleRepository.findOne(circleId);
            if (null == circle){
                throw new CommonException(1031, "圈子不存在");
            }
            //如果这个圈子为默认关注 给用户关注上
            if (circle.getIsDefaultAttent() == 1){
                userCircle = new UserCircle();
                userCircle.setCircleId(circleId);
                userCircle.setUId(uid);
                Date now = new Date();
                userCircle.setCreateTime(now);
                userCircle.setUpdateTime(now);
                userCircleRepository.saveAndFlush(userCircle);
            }
        }
        return userCircle;
    }

    @Override
    public UserCircle queryByUIdAndCircleId(String uId, Integer circleId) {
        UserCircle userCircle = userCircleRepository.queryByUIdAndCircleId(uId, circleId);
        return userCircle;
    }

    @Override
    @Transactional
    public UserCircle saveUserCircle(UserCircle userCircle) {
        userCircle.setCreateTime(new Date());
        userCircle.setUpdateTime(new Date());
        userCircle.setDelFlag("0");
        UserCircle result = userCircleRepository.save(userCircle);
        return result;
    }

    @Override
    public UserCircle updateUserCircle(UserCircle userCircle) {
        userCircle.setUpdateTime(new Date());
        UserCircle result = userCircleRepository.saveAndFlush(userCircle);
        return result;
    }

    @Override
    public List<TopicTab> getCircleTopicTabs(Integer circleId) {
        return topicTabRepository.getTopicTabsByCircleId(circleId);
    }

    /**
     * 发帖数
     */
    @Override
    public int getTodayPublishTopicCount(Integer circleId) {
        String querySql = "select count(*) from tb_bbs_topic t where t.circle_id = ? and t.status > 0 and t.create_time >= ?";
        Integer count = jdbcTemplate.queryForObject(querySql, new Object[]{circleId, DateUtils.getTodayBegin()}, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public int getTodayPublishTopicCommentCount(Integer circleId) {
        String querySql = "select count(*) from tb_bbs_comment c left join tb_bbs_topic t on c.topic_id=t.id " +
                " where t.circle_id = ? and c.create_time >= ? AND c.status>0 ";
        Integer count = jdbcTemplate.queryForObject(querySql,  new Object[]{circleId, DateUtils.getTodayBegin()}, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public CircleInfoDto getCircleInfo(Integer circleId) {
        CircleInfoDto dto = null;
        try {
            String querySql = String.format("select c.id ,c.name,c.description, c.icon, c.fake_attention_count,c.topic_count,c.attention_count, cc.id as categoryId,cc.`name` as categoryName " +
                    "from tb_bbs_circle c , tb_bbs_circle_category cc "
                    + "where c.cate_id = cc.id and c.id = %s", circleId);
            List<Map<String, Object>> dtoList = jdbcTemplate.queryForList(querySql);
            if (dtoList != null && dtoList.size() > 0) {
                Map<String, Object> one = dtoList.get(0);
                dto = new CircleInfoDto();
                dto.setId(Integer.parseInt(one.get("id").toString()));
                dto.setName(String.valueOf(one.get("name")));
                dto.setDescription(String.valueOf(one.get("description")));
                dto.setIcon(String.valueOf(one.get("icon")));
                dto.setCategoryId(Integer.parseInt(one.get("categoryId").toString()));
                dto.setCategoryName(String.valueOf(one.get("categoryName")));
                dto.setFakeAttentionCount(Integer.parseInt(String.valueOf(one.get("fake_attention_count"))));
                dto.setTopicCount(Integer.parseInt(String.valueOf(one.get("topic_count"))));
                dto.setAttentionCount(Integer.parseInt(String.valueOf(one.get("attention_count"))));
            }
        } catch (DataAccessException e) {
            logger.error("查询圈子信息时出错", e);
        }
        return dto;
    }

    @Override
    public int updateActuallyAttentionCount(Integer circleId) {
        int result = circleRepository.updateActuallyAttentionCount(circleId);
        return result;
    }

    @Override
    public String getRecommendCircleNames() {
        String sql = "select GROUP_CONCAT(`name` SEPARATOR  '|') from tb_bbs_circle t where t.is_recommend = 1 and del_flag = '0'";
        String circleNames = jdbcTemplate.queryForObject(sql, String.class);
        return circleNames;
    }

    @Transactional
    public Boolean saveOrUpdateCircle(Circle newData) {
        boolean result = false;
        Date nowDate = new Date();
        newData.setUpdateTime(nowDate);
        if (null == newData.getId()){
            newData.setCreateTime(nowDate);
        }else {
            //update
            Circle circle = circleRepository.findOne(newData.getId());
            if(circle != null){
                newData.setCreateTime(circle.getCreateTime());
                newData.setTopicCount(circle.getTopicCount());
                newData.setAttentionCount(circle.getAttentionCount());
            }
        }
        circleRepository.save(newData);

//        int isRecommend = newData.getIsRecommend();
//        dealRecommendCircle(isRecommend);
        return !result;
    }

    /**
     * 如果是推荐的圈子，需要对“推荐”圈子分类进行置顶或者添加操作
     * @param isRecommend
     */
    private void dealRecommendCircle(int isRecommend) {
        // 处理“推荐圈子”
        if( isRecommend == CircleConstant.CIRCLE_IS_RECOMMEND) {
            // 判断“推荐”是否存在
            CircleCategory recommendCircleCategory = circleCategoryRepository.queryByName(CircleConstant.RECOMMEND_CATE_NAME);
            Integer topRank = circleCategoryRepository.getTopRankExcludeName(CircleConstant.RECOMMEND_CATE_NAME);
            // 如果“推荐”圈子不存在
            if (recommendCircleCategory == null) {
                CircleCategory newCate = new CircleCategory();
                newCate.setName(CircleConstant.RECOMMEND_CATE_NAME);
                // 默认“推荐”圈子置顶
                newCate.setRank(++topRank);
                newCate.setCreateTime(new Date());
                circleCategoryRepository.save(newCate);
            } else {// 如果“推荐”圈子存在
                // 设置圈子置顶
                if (recommendCircleCategory.getRank() <= topRank) {
                    topRank++;
                    recommendCircleCategory.setRank(topRank);
                    circleCategoryRepository.save(recommendCircleCategory);
                }
            }// end if
        }// end if
    }

    //----------------------------------//

    @Override
    public Circle getCircleByName(String circleName) {
        return circleRepository.queryByName(circleName);
    }

    @Override
    public List<AdminCircleDto> searchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag, int pageNo, int pageSize) {
        String searchSql = "SELECT " +
                " ci.id, " +
                " ci.NAME, " +
                " ci.description, " +
                " ci.cate_id, " +
                " ct.`name` AS cateName, " +
                " ci.icon, " +
                " ci.rank, " +
                " ci.del_flag, " +
                " ci.is_recommend, " +
                " ci.is_default_attent " +
                " FROM " +
                " tb_bbs_circle ci " +
                " LEFT JOIN tb_bbs_circle_category ct ON ci.cate_id = ct.id " +
                " WHERE " +
                " 1 = 1 ";
        // 拼接条件语句
        searchSql += appendWhereSql(name, cateId, isRecommend, isDefaultAttent, delFlag);
        String orderSql = " order by ci.rank DESC ";
        // 拼接排序语句
        searchSql += orderSql;
        // 分页
        int offset = (pageNo - 1) * pageSize;
        String limitSql = String.format(" limit %s,%s", offset, pageSize);
        searchSql += limitSql;

        List<Map<String, Object>> data = jdbcTemplate.queryForList(searchSql);
        List<AdminCircleDto> dtoList = null;
        if (data != null && data.size() > 0) {
            dtoList = new ArrayList<>();
            for (Map<String, Object> map : data) {
                AdminCircleDto dto = initCircleDto(map);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public int countSearchCircle(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delFlag) {
        String countSql = "SELECT count(ci.id) " +
                " FROM tb_bbs_circle ci " +
                " LEFT JOIN tb_bbs_circle_category ct ON ci.cate_id = ct.id " +
                " WHERE 1 = 1";
        countSql += appendWhereSql(name, cateId, isRecommend, isDefaultAttent, delFlag);

        int count = 0;
        count = jdbcTemplate.queryForObject(countSql, Integer.class);
        return count;
    }
    
    private AdminCircleDto initCircleDto(Map<String, Object> map) {
        AdminCircleDto dto = new AdminCircleDto();
        try {
            dto.setId(Integer.parseInt(String.valueOf(map.get("id"))));
            dto.setName(String.valueOf(map.get("name")));
            dto.setDescription(String.valueOf(map.get("description")));
            dto.setCateId(Integer.parseInt(String.valueOf(map.get("cate_id"))));
            dto.setCateName(String.valueOf(map.get("cateName")));
            dto.setIcon(String.valueOf(map.get("icon")));
            dto.setRank(Integer.parseInt(String.valueOf(map.get("rank"))));
            dto.setDelFlag(String.valueOf(map.get("del_flag")));
            dto.setIsRecommend(Integer.parseInt(String.valueOf(map.get("is_recommend"))));
            dto.setIsDefaultAttent(Integer.parseInt(String.valueOf(map.get("is_default_attent"))));
        } catch (NumberFormatException e) {
            logger.error("设置圈子数据出错", e);
        }
        return dto;
    }

    private String appendWhereSql(String name, Integer cateId, Boolean isRecommend, Boolean isDefaultAttent, String delflag) {
        StringBuffer whereSql = new StringBuffer(" ");
        if (StringUtils.isNotBlank(name)) {
            whereSql.append(" AND ci.`name` LIKE '%" + name + "%'");
        }
        if (cateId != null && cateId > 0 ) {
            whereSql.append(" AND ci.cate_id = " + cateId);
        }
        if (isRecommend != null && isRecommend) {
            whereSql.append(" AND ci.is_recommend = 1");
        }
        if (isDefaultAttent != null && isDefaultAttent) {
            whereSql.append(" AND ci.is_default_attent = 1");
        }
        if (StringUtils.isNotBlank(delflag)) {
            whereSql.append(" AND ci.del_flag = '" + delflag + "'");
        }
        return whereSql.toString();
    }

    @Override
    public List<CircleListDto> findGuessLikeCircles(String uid) {
        List<Circle> cList = circleRepository.findGuessLikeCircles(uid);
        List<CircleListDto> dtoList = new ArrayList<>();
        if (cList != null && cList.size() > 0) {
            for (Circle circle : cList) {
                CircleListDto dto = new CircleListDto();
                dto.setId(circle.getId());
                dto.setName(circle.getName());
                dto.setIcon(circle.getIcon());
                dto.setDescription(circle.getDescription());
                dto.setForbidden(!circle.getDelFlag().equals("0"));
                // 我的圈子猜你喜欢，都是未已关注
                dto.setIfAttent(0);
                dtoList.add(dto);
            }
        }
        return dtoList;
    }
    //推荐分类圈子
    private CircleCategoryDto copyRecommendCirle(CircleCategoryDto categoryDto) {
        categoryDto.setId(CircleConstant.CIRCLE_CATEGORY_ID);
        categoryDto.setName(CircleConstant.RECOMMEND_CATE_NAME);
        categoryDto.setDelFlag("0");
        Integer topRank = circleCategoryRepository.getTopRankExcludeName(CircleConstant.RECOMMEND_CATE_NAME);
        categoryDto.setRank(topRank+1);
        return categoryDto;
    }
    
    private List<Circle> getCateIdCircles(Integer categoryId, Integer flag, Integer pageSize) {
        String searchSql = "SELECT " +
                " ci.id, " +
                " ci.NAME, " +
                " ci.description, " +
                " ci.cate_id, " +
                " ci.icon, " +
                " ci.rank, " +
                " ci.del_flag, " +
                " ci.is_recommend, " +
                " ci.is_default_attent " +
                " FROM " +
                " tb_bbs_circle ci " +
                " WHERE " +
                " 1 = 1 ";
        // 拼接条件语句
        searchSql += appendWhereSql("", categoryId, false, false,"0");
        String orderSql = " order by ci.rank DESC ";
        // 拼接排序语句
        searchSql += orderSql;
        // 分页
        int offset = (flag - 1) * pageSize;
        String limitSql = String.format(" limit %s,%s", offset, pageSize);
        searchSql += limitSql;

        List<Map<String, Object>> data = jdbcTemplate.queryForList(searchSql);
        List<Circle> list = null;
        if (data != null && data.size() > 0) {
            list = new ArrayList<>();
            for (Map<String, Object> map : data) {
                Circle dto = initCircle(map);
                list.add(dto);
            }
        }
        return list;
        
    }

    private List<Circle> getRecommendCircles(Integer flag, Integer pageSize) {
        String searchSql = "SELECT " +
                " ci.id, " +
                " ci.NAME, " +
                " ci.description, " +
                " ci.cate_id, " +
                " ci.icon, " +
                " ci.rank, " +
                " ci.del_flag, " +
                " ci.is_recommend, " +
                " ci.is_default_attent " +
                " FROM " +
                " tb_bbs_circle ci " +
                " WHERE " +
                " 1 = 1 ";
        // 拼接条件语句
        searchSql += appendWhereSql("", 0, true, false, "0");
        String orderSql = " order by ci.rank DESC ";
        // 拼接排序语句
        searchSql += orderSql;
        // 分页
        int offset = (flag - 1) * pageSize;
        String limitSql = String.format(" limit %s,%s", offset, pageSize);
        searchSql += limitSql;

        List<Map<String, Object>> data = jdbcTemplate.queryForList(searchSql);
        List<Circle> list = null;
        if (data != null && data.size() > 0) {
            list = new ArrayList<>();
            for (Map<String, Object> map : data) {
                Circle dto = initCircle(map);
                list.add(dto);
            }
        }
        return list;
    }
    
    private Circle initCircle(Map<String, Object> map) {
        Circle dto = new Circle();
        try {
            dto.setId(Integer.parseInt(String.valueOf(map.get("id"))));
            dto.setName(String.valueOf(map.get("name")));
            dto.setDescription(String.valueOf(map.get("description")));
            dto.setCateId(Integer.parseInt(String.valueOf(map.get("cate_id"))));
            dto.setIcon(String.valueOf(map.get("icon")));
            dto.setRank(Integer.parseInt(String.valueOf(map.get("rank"))));
            dto.setDelFlag(String.valueOf(map.get("del_flag")));
            dto.setIsRecommend(Integer.parseInt(String.valueOf(map.get("is_recommend"))));
            dto.setIsDefaultAttent(Integer.parseInt(String.valueOf(map.get("is_default_attent"))));
        } catch (Exception e) {
            logger.error("设置圈子数据出错", e);
        }
        return dto;
    }

    @Override
    public void deleteUserCircle(Integer circleId, String uid) {
        userCircleRepository.deleteUserCirle(circleId, uid);
    }
}
