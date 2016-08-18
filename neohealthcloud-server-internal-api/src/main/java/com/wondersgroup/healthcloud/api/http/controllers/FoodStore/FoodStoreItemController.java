package com.wondersgroup.healthcloud.api.http.controllers.FoodStore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.wondersgroup.healthcloud.api.utils.MapToBeanUtil;
import com.wondersgroup.healthcloud.api.utils.PropertyFilterUtil;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.foodStore.FoodStoreItem;
import com.wondersgroup.healthcloud.services.foodStore.FoodStoreItemService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenbin on 16/8/17.
 */
@RestController
@RequestMapping("api")
public class FoodStoreItemController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FoodStoreItemService foodStoreItemService;

    /**
     * 查询食物库列表
     * @param item
     * @param pageable
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStoreItem/list", method = RequestMethod.POST)
    public String findFoodStoreItemList(@RequestBody FoodStoreItem item,
                                        @PageableDefault(size = 20) Pageable pageable) throws JsonProcessingException {
        Page<FoodStoreItem> foodStoreItems = foodStoreItemService.findByExample(item, pageable);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreItem.class,
                new String[]{"id", "category", "rank", "food_name", "icon", "use_suggest", "heat", "is_show", "update_time"});
        filterMap.put(PageImpl.class, new String[]{"content", "total_pages", "total_elements", "size", "number", "last"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.filterOutAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreItems);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 查询食物详情
     * @param id
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping(value = "foodStoreItem/{id}", method = RequestMethod.GET)
    public String findFoodStoreItem(@PathVariable int id) throws JsonProcessingException {
        FoodStoreItem foodStoreItem = foodStoreItemService.findById(id);
        Map<Class, Object> filterMap = new HashMap<>();
        filterMap.put(FoodStoreItem.class, new String[]{"update_time", "create_time"});
        SimpleFilterProvider filterProvider = PropertyFilterUtil.serializeAllExceptFilter(filterMap);
        JsonResponseEntity response = new JsonResponseEntity(0, "查询成功", foodStoreItem);

        return PropertyFilterUtil.getObjectMapper().setFilterProvider(filterProvider).writeValueAsString(response);
    }

    /**
     * 新增食物
     * @param para
     * @return
     */
    @RequestMapping(value = "saveFoodStoreItem", method = RequestMethod.POST)
    public JsonResponseEntity saveFoodStoreItem(@RequestBody Map para){
        FoodStoreItem foodStoreItem = MapToBeanUtil.fromMapToBean(FoodStoreItem.class, para);
        foodStoreItem.setCreateTime(new Date());
        foodStoreItem.setUpdateTime(new Date());
        foodStoreItemService.saveFoodStoreItem(foodStoreItem);

        return new JsonResponseEntity(0, "新增成功");
    }

    /**
     * 修改食物
     * @param para
     * @return
     */
    @RequestMapping(value = "updateFoodStoreItem", method = RequestMethod.POST)
    public JsonResponseEntity updateFoodStoreItem(@RequestBody Map para){
        FoodStoreItem foodStoreItem = MapToBeanUtil.fromMapToBean(FoodStoreItem.class, para);
        foodStoreItem.setUpdateTime(new Date());
        foodStoreItem.setCreateTime(new Date());
        foodStoreItemService.saveFoodStoreItem(foodStoreItem);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 查询状态
     * @return
     */
    @RequestMapping(value = "findIsShow/item", method = RequestMethod.GET)
    public Object findIsShow(){
        Map<String, Object> map = new HashMap<>();
        map.put("is_show", foodStoreItemService.findIsShow());
        return map;
    }

    /**
     * 查询分类
     * @return
     */
    @RequestMapping(value = "findCategoryName", method = RequestMethod.GET)
    public Object findCategoryName(){
        Map<String, Object> map = new HashMap<>();
        map.put("category_name", foodStoreItemService.findCategoryName());
        return map;
    }

    /**
     * 批量更新显示/不显示
     * @param isShow
     * @param itemId
     * @return
     */
    @RequestMapping(value = "updateIsShow", method = RequestMethod.POST)
    public JsonResponseEntity updateIsShow(@RequestParam int isShow,
                                           @RequestParam List<Integer> itemId){
        foodStoreItemService.updateIsShowByIds(isShow, new Date(), itemId);

        return new JsonResponseEntity(0, "修改成功");
    }

    /**
     * 批量导入
     * @param category_id
     * @param is_show
     * @param export_url
     * @return
     */
    @ResponseBody
    @RequestMapping(value="postExportFood", method= RequestMethod.POST)
    public JsonResponseEntity postExportFood(@RequestParam Integer category_id,
                                             @RequestParam Integer is_show,
                                             @RequestParam String export_url){
        FoodStoreItem foodStoreItem = null;
        try {
            URL aURL = new URL(export_url);
            if (aURL.getHost().equals("wikidiabetes.izhangkong.com")){
                foodStoreItem = this.deal_izhangkong_url(export_url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (null == foodStoreItem || StringUtils.isEmpty(foodStoreItem.getFoodName())){
            return new JsonResponseEntity(201, "获取信息失败!", foodStoreItem);
        }
        foodStoreItem.setRank(100);
        foodStoreItem.setCategoryId(category_id);
        foodStoreItem.setIsShow(is_show > 0 ? 1 : 0);

        Boolean isOk = foodStoreItemService.updateFoodStoreItem(foodStoreItem);
        if (isOk){
            return new JsonResponseEntity(0, "保存成功!", foodStoreItem);
        }else {
            return new JsonResponseEntity(301, "保存失败!", foodStoreItem);
        }
    }

    /**
     * 抓取wikidiabetes.izhangkong.com
     * @param url
     * @return
     */
    private FoodStoreItem deal_izhangkong_url(String url){
        FoodStoreItem foodStoreItem = null;
        String id = this.getUrlQueryParms(url, "id");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse;
        String crawalBaseUrl = "http://wikidiabetes.izhangkong.com/mobile/tool/getIngredientsById?id="+id;
        HttpGet get = new HttpGet(crawalBaseUrl);
        try {
            httpResponse = httpClient.execute(get);
            HttpEntity entity = httpResponse.getEntity();
            JsonNode json = objectMapper.readValue(EntityUtils.toString(entity), JsonNode.class);
            Boolean isSuccess = json.get("success").asBoolean();
            if (isSuccess){
                JsonNode obj = json.get("obj");
                foodStoreItem = this.coverJson2FoodItemModel(obj);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (foodStoreItem != null){
            foodStoreItem.setSource(crawalBaseUrl);
        }
        return foodStoreItem;
    }

    /**
     * 解析url 的get参数
     * @param url
     * @return Map<String, String>
     */
    private String getUrlQueryParms(String url, String key) {
        String[] splitUrl = url.split("[?]");
        Map<String, String> parms = new HashMap<>();
        if (splitUrl.length > 1 && StringUtils.isNotEmpty(splitUrl[1])) {
            //每个键值为一组
            String[] paramsArr = splitUrl[1].split("[&]");
            for (String strSplit : paramsArr) {
                String[] arrSplitEqual;
                arrSplitEqual = strSplit.split("[=]");
                //解析出键值
                if (arrSplitEqual.length > 1) {
                    //正确解析
                    parms.put(arrSplitEqual[0], arrSplitEqual[1]);
                } else {
                    if (arrSplitEqual[0] != "") {
                        //只有参数没有值，不加入
                        parms.put(arrSplitEqual[0], "");
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(key) && parms.containsKey(key)){
            return parms.get(key);
        }
        return "";
    }

    private FoodStoreItem coverJson2FoodItemModel(JsonNode jsonObject){
        FoodStoreItem foodStoreItem = new FoodStoreItem();
        foodStoreItem.setFoodName(jsonObject.get("name").asText());
        foodStoreItem.setIcon(jsonObject.get("picurl").asText());
        String foodleveltext = jsonObject.get("foodleveltext").asText();
        String use_suggest = foodleveltext;
        if (FoodStoreItem.foodlevelConf.containsKey(foodleveltext)){
            use_suggest = FoodStoreItem.foodlevelConf.get(foodleveltext);
        }
        foodStoreItem.setUseSuggest(use_suggest);

        String gi = getCoverString(jsonObject.get("gi").asText());
        foodStoreItem.setGi(StringUtils.isEmpty(gi) ? "" : gi);

        //营养成分
        foodStoreItem.setFat(getCoverString(jsonObject.get("fat").asText()));
        foodStoreItem.setHeat(getCoverString(jsonObject.get("heat").asText()));
        foodStoreItem.setCarbohydrates(getCoverString(jsonObject.get("carbohydrates").asText()));
        foodStoreItem.setProtein(getCoverString(jsonObject.get("protein").asText()));

        foodStoreItem.setNutrition(getCoverString(jsonObject.get("nutrition").asText()));
        foodStoreItem.setRecommend(getCoverString(jsonObject.get("recommend").asText()));
        foodStoreItem.setBenefit(getCoverString(jsonObject.get("benefit").asText()));
        return foodStoreItem;
    }

    private String getCoverString(String str){
        return null == str || str.equalsIgnoreCase("null") ? "" : str;
    }

}
