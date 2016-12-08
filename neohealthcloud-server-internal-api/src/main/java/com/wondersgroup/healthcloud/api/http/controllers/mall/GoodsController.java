package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;
import com.wondersgroup.healthcloud.jpa.entity.mall.GoodsItem;
import com.wondersgroup.healthcloud.services.mall.GoodsService;
import com.wondersgroup.healthcloud.services.mall.dto.GoodsForm;
import com.wondersgroup.healthcloud.services.mall.dto.GoodsSearchForm;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

	@Autowired
	GoodsService goodsService;

	/**
	 * 导入券码
	 * 
	 * @param file
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/excel", method = RequestMethod.POST)
	public Object readExcel(@RequestParam MultipartFile file, HttpServletRequest request) throws IOException {
		JsonResponseEntity<Set<String>> responseEntity = new JsonResponseEntity<>();
		InputStream stream = file.getInputStream();
		Set<String> list = readExcel(stream);
		responseEntity.setData(list);
		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody GoodsForm form) {
		JsonResponseEntity<Set<String>> responseEntity = new JsonResponseEntity<>();

		Goods goods = form.getGoods();
		Integer id = goods.getId();
		if (id == null) {
			goodsService.save(form);
		} else {
			goodsService.update(goods);
		}
		return responseEntity;
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Object list(@RequestBody Pager pager) {
		Map params = pager.getParameter();
		int number = pager.getNumber();
		int size = pager.getSize();
		GoodsSearchForm searchForm = new GoodsSearchForm(params, number, size);

		Page<Goods> page = goodsService.search(searchForm);

		pager.setData(page.getContent());
		pager.setTotalElements(Integer.valueOf(page.getTotalElements() + ""));
		pager.setTotalPages(page.getTotalPages());
		return pager;
	}

	@RequestMapping(value = "/stocknum", method = RequestMethod.GET)
	public Object updateStocknum(int id, int stocknum) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();

		Goods goods = goodsService.findById(id);
		if (goods != null) {
			goods.setNum(goods.getNum() + stocknum);
			goods.setStockNum(goods.getStockNum() + stocknum);
			goods.setUpdateTime(new Date());
			goodsService.save(goods);
		}

		responseEntity.setMsg("OK");
		return responseEntity;
	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public Object updateStatus(int id) {
		JsonResponseEntity<String> responseEntity = new JsonResponseEntity<>();

		Goods goods = goodsService.findById(id);
		if (goods != null) {
			int status = goods.getStatus() == 0 ? 1 : 0;
			goods.setStatus(status);
			goods.setUpdateTime(new Date());
			goodsService.save(goods);
		}
		responseEntity.setMsg("OK");
		return responseEntity;
	}

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public Object details(Integer id) {
		JsonResponseEntity<Goods> responseEntity = new JsonResponseEntity<>();
		Goods goods = goodsService.findById(id);
		responseEntity.setData(goods);
		return responseEntity;
	}

	@RequestMapping(value = "/items", method = RequestMethod.POST)
	public Object items(@RequestBody Pager pager) {
		Page<GoodsItem> page = goodsService.findItems(pager.getParameter(), pager.getNumber(), pager.getSize());

		pager.setData(page.getContent());
		pager.setTotalElements(Integer.valueOf(page.getTotalElements() + ""));
		pager.setTotalPages(page.getTotalPages());
		return pager;
	}

	private Set<String> readExcel(InputStream stream) throws IOException {
		Set<String> list = new HashSet<>();
		XSSFWorkbook workbook = new XSSFWorkbook(stream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		if (sheet != null) {
			int rowNum = sheet.getLastRowNum();
			for (int i = 0; i <= rowNum; i++) {
				XSSFRow row = sheet.getRow(i);
				String code = row.getCell(0).getStringCellValue();
				if (StringUtils.isNotBlank(code)) {
					list.add(code);
				}
			}
		}
		return list;
	}

}
