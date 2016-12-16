package com.wondersgroup.healthcloud.api.http.controllers.mall.admin;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.MallBanner;
import com.wondersgroup.healthcloud.services.mall.MallBannerService;
import com.wondersgroup.healthcloud.services.mall.dto.MallBannerDto;
import com.wondersgroup.healthcloud.utils.mapper.JsonMapper;

@RestController
@RequestMapping("/api/mall/banner")
public class MallBannerController {

	@Autowired
	MallBannerService mallBannerService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Object list(@RequestBody Pager pager) {
		Page<MallBannerDto> page = mallBannerService.search(pager.getParameter(), pager.getNumber(), pager.getSize());

		pager.setData(page.getContent());
		pager.setTotalElements(Integer.valueOf(page.getTotalElements() + ""));
		pager.setTotalPages(page.getTotalPages());
		return pager;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Object save(@RequestBody String json) {
		MallBanner banner = JsonMapper.nonDefaultMapper().fromJson(json, MallBanner.class);
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();
		String id = banner.getId();
		if (StringUtils.isBlank(id)) {
			mallBannerService.save(banner);
		} else {
			mallBannerService.update(banner);
		}

		responseEntity.setMsg("更新横幅成功");
		return responseEntity;
	}
}
