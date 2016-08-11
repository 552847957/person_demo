package com.wondersgroup.healthcloud.api.http.controllers.dict;

import com.wondersgroup.healthcloud.api.utils.HttpRequestUtils;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.dict.DictCache;
import com.wondersgroup.healthcloud.jpa.entity.dic.Disease;
import com.wondersgroup.healthcloud.jpa.repository.dict.DiseaseRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/dict")
public class DictController {
	
	private static final String AREA_VERSION="1.0";
	private static final String SPORT_VERSION="1.0";
	private static final String FOOD_VERSION="1.0";
	
	private JdbcTemplate jt;
	@Autowired
	private DataSource ds;

	@Autowired
	private DiseaseRepository diseaseRepository;
	
	private JdbcTemplate getJt(){
		jt =new JdbcTemplate(ds);
		return jt;
	}
	
	private class Sport {
		private String sportName;
		private String calalogName;
		private Integer consume;
		private String picture;
		
		public Sport(Map<String,Object> result,HttpServletRequest request) {
			this.sportName = StringUtils.defaultString((String) result.get("SportName"));
			this.calalogName = StringUtils.defaultString((String) result.get("CatalogName"));
			this.consume =  (Integer) result.get("consume");
			this.picture= (String)result.get("picture");
		}
		public String getSportName() {
			return sportName;
		}
		public void setSportName(String sportName) {
			this.sportName = sportName;
		}
		public String getCalalogName() {
			return calalogName;
		}
		public void setCalalogName(String calalogName) {
			this.calalogName = calalogName;
		}
		public Integer getConsume() {
			return consume;
		}
		public void setConsume(Integer consume) {
			this.consume = consume;
		}
		public String getPicture() {
			return picture;
		}
		public void setPicture(String picture) {
			this.picture = picture;
		}
		
	}
	private class Food{
		private String foodName;
		private String calalogName;
		private BigDecimal calorie;
		private String picture;
		
		public Food(Map<String,Object> result,HttpServletRequest request) {
			this.foodName = StringUtils.defaultString((String) result.get("name"));
			this.calalogName = StringUtils.defaultString((String) result.get("catalog"));
			this.calorie =  (BigDecimal) result.get("calorie");
			this.picture = HttpRequestUtils.getAbsolutePath(request, (String) result.get("picture"));
		}
		public String getFoodName() {
			return foodName;
		}
		public void setFoodName(String foodName) {
			this.foodName = foodName;
		}
		public String getCalalogName() {
			return calalogName;
		}
		public void setCalalogName(String calalogName) {
			this.calalogName = calalogName;
		}
		public BigDecimal getCalorie() {
			return calorie;
		}
		public void setCalorie(BigDecimal calorie) {
			this.calorie = calorie;
		}
		public String getPicture() {
			return picture;
		}
		public void setPicture(String picture) {
			this.picture = picture;
		}
		
	}
	private class Area{
		private String areaName;
		private String areaCode;
		private String areaUpperCode;
		private List<Map<String, Object>> areas;
		public Area(Map<String,Object> result) {
			this.areaName=(String) result.get("explain_memo");
			this.areaCode= (String) result.get("code");
			this.areaUpperCode=(String) result.get("upper_code");
		}
		public String getAreaName() {
			return areaName;
		}
		public void setAreaName(String areaName) {
			this.areaName = areaName;
		}
		public String getAreaCode() {
			return areaCode;
		}
		public void setAreaCode(String areaCode) {
			this.areaCode = areaCode;
		}
		public String getAreaUpperCode() {
			return areaUpperCode;
		}
		public void setAreaUpperCode(String areaUpperCode) {
			this.areaUpperCode = areaUpperCode;
		}

		public List<Map<String, Object>> getAreas() {
			return areas;
		}

		public void setAreas(List<Map<String, Object>> areas) {
			this.areas = areas;
		}
	}

	private class DiseaseEntity{
		private String id;
		private String diseaseName;
		private DiseaseEntity(Disease disease){
			this.id = disease.getId();
			this.diseaseName = disease.getDiseaseName();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDiseaseName() {
			return diseaseName;
		}

		public void setDiseaseName(String diseaseName) {
			this.diseaseName = diseaseName;
		}
	}

   @Autowired
   private DictCache dictCache;

	@VersionRange
    @RequestMapping(value = "/area", method = RequestMethod.GET)
    public JsonListResponseEntity<Area> getUnderAddress(@RequestParam(value = "code") String code,
														@RequestParam(value = "islist") Boolean islist) {
		JsonListResponseEntity<Area> areaJsonListResponseEntity = new JsonListResponseEntity<>();
		List<Area> list = new ArrayList<Area>();
		String sqlwhere="code";
		if(islist) {
			sqlwhere="upper_code";
		}
		List<Map<String, Object>> resultList = getJt().queryForList(String.format("select code, explain_memo  from t_dic_area where "+sqlwhere+"='%s' ", code));

		for(Map<String,Object> result:resultList){
			Area area = new Area(result);
			if(islist) {
				List<Map<String, Object>> resultList2 = new ArrayList<>();
				Map<String, Object> tmp = new HashMap<>();
				tmp.put("code", "");
				tmp.put("explain_memo", "");
				resultList2.add(tmp);
				List<Map<String, Object>> resultList2_more = getJt().queryForList(String.format("select code, explain_memo  from t_dic_area where upper_code ='%s' ", (String) result.get("code")));
				resultList2.addAll(resultList2_more);
				area.setAreas(resultList2);
			}
			list.add(area);
		}
		areaJsonListResponseEntity.setContent(list);
		return areaJsonListResponseEntity;
    }


	@VersionRange
    @RequestMapping(value="/versions",method = RequestMethod.GET)
    public Map<String,String> getDictVersions(){
    	Map<String,String> versions = new HashMap<String,String>();
    	versions.put("area", AREA_VERSION);
    	versions.put("sport", SPORT_VERSION);
    	versions.put("food", FOOD_VERSION);
    	return versions;
    }

	@VersionRange
    @RequestMapping(value="/sports",method = RequestMethod.GET)
    public JsonListResponseEntity<Sport> getSportsDict(HttpServletRequest request){
		JsonListResponseEntity<Sport> sportJsonListResponseEntity = new JsonListResponseEntity<>();
		List<Sport> list = new ArrayList<Sport>();
    	List<Map<String,Object>> resultList = getJt().queryForList("select SportName,CatalogName,consume,picture  from app_dic_sport where SportName is not NULL and CatalogName is not null and consume is not null and picture is not null");
    	for(Map<String,Object> result:resultList){
    		list.add(new Sport(result,request));
    	}
		sportJsonListResponseEntity.setContent(list);
    	return sportJsonListResponseEntity;
    }

	@VersionRange
    @RequestMapping(value="/areas",method = RequestMethod.GET)
    public JsonListResponseEntity<Area> getAreasDict(HttpServletRequest request){
		JsonListResponseEntity<Area> areaJsonListResponseEntity = new JsonListResponseEntity<>();
		List<Area> list = new ArrayList<Area>();
    	List<Map<String,Object>> resultList =getJt().queryForList("select code, explain_memo,upper_code  from t_dic_area where 1=1");
    	for(Map<String,Object> result:resultList){
    		list.add(new Area(result));
    	}
		areaJsonListResponseEntity.setContent(list);
    	return areaJsonListResponseEntity;
    }

	@VersionRange
    @RequestMapping(value="/foods",method = RequestMethod.GET)
    public JsonListResponseEntity<Food> getFoodsDict(HttpServletRequest request){
		JsonListResponseEntity<Food> foodJsonListResponseEntity = new JsonListResponseEntity<>();
		List<Food> list = new ArrayList<Food>();
    	List<Map<String,Object>> resultList = getJt().queryForList("select name,catalog,calorie,picture  from app_dic_food where name is not null and catalog is not null and calorie is not null and picture is not null");
    	for(Map<String,Object> result:resultList){
    		list.add(new Food(result,request));
    	}
		foodJsonListResponseEntity.setContent(list);
    	return foodJsonListResponseEntity;
    }

	@VersionRange
	@RequestMapping(value="/disease",method = RequestMethod.GET)
	public JsonListResponseEntity<DiseaseEntity> getDisease(HttpServletRequest request){
		JsonListResponseEntity<DiseaseEntity> foodJsonListResponseEntity = new JsonListResponseEntity<DiseaseEntity>();
		List<Disease> list = diseaseRepository.findAllDisease();
		List<DiseaseEntity> apiList = new ArrayList<DiseaseEntity>();
		for(Disease disease:list){
			apiList.add(new DiseaseEntity(disease));
		}
		foodJsonListResponseEntity.setContent(apiList);
		return foodJsonListResponseEntity;
	}

}
