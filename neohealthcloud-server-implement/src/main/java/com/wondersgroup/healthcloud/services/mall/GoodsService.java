package com.wondersgroup.healthcloud.services.mall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wondersgroup.healthcloud.common.utils.IdGen;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsItem;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsItemRepository;
import com.wondersgroup.healthcloud.jpa.repository.mall.GoodsRepository;
import com.wondersgroup.healthcloud.services.mall.dto.GoodsForm;
import com.wondersgroup.healthcloud.services.mall.dto.GoodsSearchForm;

@Service
@Transactional
public class GoodsService {

	@Autowired
	GoodsRepository goodsRepository;

	@Autowired
	GoodsItemRepository goodsItemRepository;

	@Autowired
	JdbcTemplate jdbcTemplate;

	public void save(GoodsForm form) {
		Date date = new Date();

		Goods goods = form.getGoods();
		goods.setNum(goods.getStockNum());
		goods.setSalesNum(0);
		goods.setCreateTime(date);
		goods.setUpdateTime(date);
		goods.setStatus(0);
		goods = goodsRepository.save(goods);

		if (goods.getType() == 0) {
			addGoodsItem(form.getItems(), goods.getId());
		}
	}

	public void addGoodsItem(Set<String> items, int goodsId) {
		Date date = new Date();
		if (items != null && !items.isEmpty()) {
			for (String code : items) {
				GoodsItem item = new GoodsItem();
				item.setId(IdGen.uuid());
				item.setCode(code);
				item.setGoodsId(goodsId);
				item.setStatus(0);
				item.setCreateTime(date);
				item.setUpdateTime(date);
				goodsItemRepository.save(item);
			}
		}
	}

	public void update(Goods goods) {
		Goods tbGoods = goodsRepository.findOne(goods.getId());

		tbGoods.setName(goods.getName());
		tbGoods.setIntroduce(goods.getIntroduce());
		tbGoods.setSortNo(goods.getSortNo());
		tbGoods.setUpdateTime(new Date());
		goodsRepository.save(tbGoods);
	}

	public Page<Goods> search(GoodsSearchForm searchForm) {
		int page = searchForm.getFlag();
		String property = searchForm.getProperty();
		Direction direction = Direction.fromString(searchForm.getDirection());
		PageRequest pageable = new PageRequest(page, searchForm.getPageSize(), direction, property);

		Specification<Goods> specification = buildSpecification(searchForm);
		return goodsRepository.findAll(specification, pageable);
	}

	public Goods findById(Integer id) {
		return goodsRepository.findOne(id);
	}

	public void save(Goods goods) {
		goodsRepository.save(goods);
	}

	public Page<GoodsItem> findItems(Map map, int page, int size) {
		String sql = "select a.*, b.`nickname`,b.`name` from goods_item_tb a left join app_tb_register_info b on a.user_id = b.registerid ";

		Integer goodsId = (Integer) map.get("goodsId");
		String code = (String) map.get("code");
		Integer status = (Integer) map.get("status");

		sql = sql + " where a.goods_id =" + goodsId;
		if (StringUtils.isNotBlank(code)) {
			sql = sql + " and a.code like '%" + code + "%'";
		}

		if (status != null) {
			sql = sql + " and a.status = " + status;
		}

		int start = (page - 1) * size;
		start = start > 0 ? start : 0;
		String limitSQL = sql + " limit " + start + ", " + (start + size);
		List<GoodsItem> list = jdbcTemplate.query(limitSQL, new BeanPropertyRowMapper<GoodsItem>(GoodsItem.class));

		sql = "select count(1) " + sql.substring(sql.indexOf("from"));
		int count = jdbcTemplate.queryForObject(sql, Integer.class);

		return new PageImpl<>(list, new PageRequest(page, size), count);
	}

	private Specification<Goods> buildSpecification(final GoodsSearchForm searchForm) {
		Specification<Goods> specification = new Specification<Goods>() {
			@Override
			public Predicate toPredicate(Root<Goods> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();

				String name = searchForm.getName();
				if (StringUtils.isNotBlank(name)) {
					predicateList.add(criteriaBuilder.like(root.<String> get("name"), "%" + name + "%"));
				}

				Integer type = searchForm.getType();
				if (type != null) {
					predicateList.add(criteriaBuilder.equal(root.<String> get("type"), type));
				}

				Integer status = searchForm.getStatus();
				if (status != null) {
					predicateList.add(criteriaBuilder.equal(root.<String> get("status"), status));
				}

				if (!predicateList.isEmpty()) {
					Predicate[] predicates = new Predicate[predicateList.size()];
					criteriaQuery.where(predicateList.toArray(predicates));
				}
				return null;
			}
		};
		return specification;
	}

	public Page<Goods> findByStatus(int status, Pageable pageable) {
		return goodsRepository.findByStatus(status, pageable);
	}

}
