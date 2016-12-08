package com.wondersgroup.healthcloud.api.http.controllers.mall;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.mall.Goods;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

	@RequestMapping(value = "/excel", method = RequestMethod.POST)
	public Object readExcel(@RequestParam MultipartFile file) throws IOException {
		JsonResponseEntity<List<String>> responseEntity = new JsonResponseEntity<>();
		InputStream stream = file.getInputStream();
		List<String> list = readExcel(stream);
		responseEntity.setData(list);
		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Object save(@RequestBody Goods goods) {
		return null;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Object list(String name, String type, Integer status) {
		return null;
	}

	@RequestMapping(value = "/stocknum", method = RequestMethod.GET)
	public Object updateStocknum(int id, int stocknum) {
		return null;
	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	public Object updateStatus(int id) {
		return null;
	}

	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public Object details(String id) {
		return null;
	}

	@RequestMapping(value = "/items", method = RequestMethod.GET)
	public Object items(Integer id) {
		return null;
	}

	private List<String> readExcel(InputStream stream) throws IOException {
		List<String> list = new ArrayList<>();
		HSSFWorkbook workbook = new HSSFWorkbook(stream);
		HSSFSheet sheet = workbook.getSheetAt(0);
		if (sheet != null) {
			int rowNum = sheet.getLastRowNum();
			for (int i = 1; i < rowNum; i++) {
				HSSFRow row = sheet.getRow(i);
				String code = row.getCell(1).getStringCellValue();
				list.add(code);
			}
		}
		return list;
	}

}
