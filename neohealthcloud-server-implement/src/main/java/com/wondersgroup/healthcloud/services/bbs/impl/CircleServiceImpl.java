package com.wondersgroup.healthcloud.services.bbs.impl;

import com.wondersgroup.healthcloud.common.appenum.AppJumpUrlEnum;
import com.wondersgroup.healthcloud.common.utils.DateUtils;
import com.wondersgroup.healthcloud.jpa.constant.CircleConstant;
import com.wondersgroup.healthcloud.jpa.entity.bbs.Circle;
import com.wondersgroup.healthcloud.jpa.entity.bbs.CircleCategory;
import com.wondersgroup.healthcloud.jpa.entity.bbs.TopicTab;
import com.wondersgroup.healthcloud.jpa.entity.bbs.UserCircle;
import com.wondersgroup.healthcloud.jpa.repository.bbs.*;
import com.wondersgroup.healthcloud.services.bbs.CircleService;
import com.wondersgroup.healthcloud.services.bbs.dto.circle.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public List<Circle> getCircleList(Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public List<Circle> getCircleListByCateId(Integer cateId, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public Circle getCircleInfoById(Integer circleId) {
        return circleRepository.findOne(circleId);
    }

    @Override
    public List<CircleCategory> getCircleCateList() {
        return null;
    }

    @Override
    public List<Circle> defaultAttentList() {
        List<Circle> list = circleRepository.queryByIsDefaultAttentAndDelFlag(1, "0");
        return list;
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
        }
        return cateDtoList;
    }

    @Override
    public List<MyCircleDto> getMyCircleList(String userId) {
        List<MyCircleDto> circleList = new ArrayList<>();

        MyCircleDto circleDto01 = new MyCircleDto();
        circleDto01.setId(10001);
        circleDto01.setRank(1);
        circleDto01.setIcon("http://circleIco01.com");
        circleDto01.setName("圈子1");

        MyCircleDto circleDto02 = new MyCircleDto();
        circleDto02.setId(10002);
        circleDto02.setRank(2);
        circleDto02.setIcon("http://circleIco02.com");
        circleDto02.setName("圈子2");

        circleList.add(circleDto01);
        circleList.add(circleDto02);
        return circleList;
    }

    @Override
    public List<CircleBannerDto> getCircleBannerList() {
        List<CircleBannerDto> dtoList = new ArrayList<>();
        Date now = new Date();
        String sql = "SELECT " +
                " t.id, " +
                " t.img_url, " +
                " t.hoplinks, " +
                " t.sequence " +
                " FROM " +
                " tb_app_advertisement t " +
                " WHERE " +
                " t.adcode = 3 " +
                " AND start_time <= '" + DateUtils.sdf.format(now) + "' " +
                " AND end_time >= '" + DateUtils.sdf.format(now) + "' " +
                " AND t.del_flag = '0' order by sequence DESC";
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql);
        if (data != null && data.size() > 0) {
            for (Map<String, Object> map : data) {
                CircleBannerDto dto = new CircleBannerDto();
                dto.setId(String.valueOf(map.get("id")));
                dto.setPic(String.valueOf(map.get("img_url")));
                Integer topicId = Integer.parseInt(String.valueOf(map.get("hoplinks")));
                dto.setTopicId(topicId);// 存的是id
                dto.setPicOrder(Integer.parseInt(String.valueOf(map.get("sequence"))));
                dto.setJumpUrl(String.format(AppJumpUrlEnum.TOPIC_URL.getValue(), topicId));
                dtoList.add(dto);
            }
        } else {
            logger.info("未查询到可用的圈子banner图");
        }
        return dtoList;
    }

    @Override
    public List<CircleListDto> getCirclesByCId(Integer categoryId, String uId) {
        List<CircleListDto> cDtoList = new ArrayList<>();
        List<Circle> cList = null;
        // 查询是否为“推荐圈子”分类
        CircleCategory circleCategory = circleCategoryRepository.findOne(categoryId);
        if (circleCategory != null && CircleConstant.RECOMMEND_CATE_NAME.equals(circleCategory.getName())) {
            cList = circleRepository.queryByIsRecommendAndDelFlagOrderByRankDesc(1, "0");
        } else {
            cList = circleRepository.queryByCateIdAndDelFlagOrderByRankDesc(categoryId, "0");
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
    public UserCircle queryByUIdAndCircleIdAndDelFlag(String uId, Integer circleId, String delFlag) {
        UserCircle userCircle = userCircleRepository.queryByUIdAndCircleIdAndDelFlag(uId, circleId, delFlag);
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
        userCircle.setUpdateTime(new Date());
        userCircle.setCreateTime(new Date());
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
        String querySql = "select count(*) from tb_bbs_topic t where t.circle_id = ? and t.status != -1 and t.create_time >= ?";
        Integer count = jdbcTemplate.queryForObject(querySql, new Object[]{circleId, DateUtils.getTodayBegin()}, Integer.class);
        return count == null ? 0 : count;
    }

    @Override
    public int getTodayPublishTopicCommentCount(Integer circleId) {
        String querySql = "select count(*) from tb_bbs_comment c left join tb_bbs_topic t on c.topic_id=t.id where t.circle_id = ? and c.create_time >= ?";
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
    public int getAttentCount(Integer circleId, String delFlag) {
        int count = userCircleRepository.getAttentCount(circleId, delFlag);
        return count;
    }

    /**
     *
     * @param uId
     * @return 加入默认圈子成功，返回1；反则返回0
     */
    @Override
    public int joinInDefaultCircles(String uId) {
        int result = 0;
        List<Circle> defaultCircleList = this.defaultAttentList();
        if (defaultCircleList != null && defaultCircleList.size() > 0) {
            // 读取默认关注圈子列表
            for (Circle circle : defaultCircleList) {
                int circleId = circle.getId();
                // 检查是否已经加入过这个圈子
                UserCircle exist = this.queryByUIdAndCircleId(uId, circleId);
                if (exist == null) {// 不存在该记录
                    UserCircle userCircle = new UserCircle();
                    userCircle.setCircleId(circleId);
                    userCircle.setUId(uId);
                    // 加入圈子
                    UserCircle savedResult = this.saveUserCircle(userCircle);
                    if (savedResult != null) {
                        logger.info(String.format("user [%s] default join in circle [%s]", uId, circleId));
                    }// end if
                } else {
                    logger.info(String.format("user [%s] already join in circle [%s]", uId, circleId));
                }

            }// end for
            result = 1;
        } else {
            logger.info("没有默认要加入的圈子");
        }// end else
        return result;
    }

    @Override
    public void increaseAttentionCount(Integer circleId, Integer num) {
        Circle circle = circleRepository.findOne(circleId);
        if (circle != null) {
            int attenttionCount = circle.getAttentionCount();
            attenttionCount += num;
            if (attenttionCount >= 0) {
                circle.setAttentionCount(attenttionCount);
                circle.setUpdateTime(new Date());
                circleRepository.save(circle);
            }
        }
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

        int isRecommend = newData.getIsRecommend();
        dealRecommendCircle(isRecommend);
        return result;
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
    public int checkCircleNameByName(int id, String circleName) {
        return 0;
    }

    @Override
    public List<AdminCircleDto> searchCircle(String name, Integer cateId, Integer isRecommend, Integer isDefaultAttent, String delFlag, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public int countSearchCircle(String name, Integer cateId, Integer isRecommend, Integer isDefaultAttent, String delFlag) {
        return 0;
    }
}
