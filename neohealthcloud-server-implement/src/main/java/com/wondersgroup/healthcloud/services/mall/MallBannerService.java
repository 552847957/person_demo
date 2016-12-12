package com.wondersgroup.healthcloud.services.mall;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.exceptions.CommonException;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.jpa.entity.mall.MallBanner;
import com.wondersgroup.healthcloud.jpa.repository.mall.MallBannerRepository;
import com.wondersgroup.healthcloud.services.mall.dto.MallBannerDto;

@Service
@Transactional
public class MallBannerService {

	@Autowired
	MallBannerRepository mallBannerRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public Page<MallBannerDto> search(Map map, int page, int size) {

		String sql = "from mall_banner_tb a left join goods_tb b on a.goods_id = b.id";

		Integer status = (Integer) map.get("status");
		if (status != null) {
			sql += " and a.status = " + status;
		}

		Integer goodsId = (Integer) map.get("goodsId");
		if (goodsId != null) {
			sql += " and a.goods_id = " + goodsId;
		}

		int start = page > 0 ? (page - 1) * size : 0;
		String query = "select a.*, b.name as goodsName " + sql + " order by a.sort_no asc limit " + start + ","
				+ (start + size);
		List<MallBannerDto> content = jdbcTemplate.query(query,
				new BeanPropertyRowMapper<MallBannerDto>(MallBannerDto.class));

		String count = "select count(1) " + sql;
		int total = jdbcTemplate.queryForObject(count, Integer.class);

		return new PageImpl<>(content, new PageRequest(page, size), total);
	}

	public void save(MallBanner banner) {
		MallBanner tbBanner = mallBannerRepository.findByGoodsId(banner.getGoodsId());
		if (tbBanner != null) {
			throw new CommonException(1001, "不能重复设置同一商品");
		}

		Date date = new Date();
		banner.setId(IdGen.uuid());
		banner.setCreateTime(date);
		banner.setUpdateTime(date);
		banner.setStatus(0);
		mallBannerRepository.save(banner);
	}

	public void update(MallBanner banner) {
		MallBanner tbBanner = mallBannerRepository.findOne(banner.getId());
		if (tbBanner != null) {
			Integer sortNo = banner.getSortNo();
			if (sortNo != null) {
				tbBanner.setSortNo(sortNo);
			}
			Integer status = banner.getStatus();
			if (status != null) {
				tbBanner.setStatus(status);
			}
			Date startTime = banner.getStartTime();
			if (startTime != null) {
				tbBanner.setStartTime(startTime);
			}
			Date endTime = banner.getEndTime();
			if (endTime != null) {
				tbBanner.setEndTime(endTime);
			}
			mallBannerRepository.save(tbBanner);
		}
	}

	public List<Goods> findHomeBanner() {
		String sql = "select b.*  from `mall_banner_tb` a inner join  goods_tb b on a.goods_id = b.id and a.status = 1 order by sort_no asc limit 3";
		List<Goods> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<Goods>(Goods.class));
		return list;
	}
	
	public void soldOut(Integer goodsId) {
		MallBanner banner = mallBannerRepository.findByGoodsId(goodsId);
		if(banner != null){
			banner.setStatus(0);
		}
		banner.setUpdateTime(new Date());
		mallBannerRepository.save(banner);
	}

}
