package com.wondersgroup.healthcloud.api.http.controllers.yyService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wondersgroup.healthcloud.api.http.dto.medical.MedicalAPIEntity;
import com.wondersgroup.healthcloud.api.http.dto.medical.ServiceOrderAPIEntity;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonListResponseEntity;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.yyService.VisitUserService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 医养融合 Created by dukuanxin 13/5/16.
 *
 */
@RestController
@RequestMapping("/api/medicalService")
public class MedicalController {

    @Autowired
    private Environment env;

    @Autowired
    private VisitUserService visitUserService;


    // 查询服务列表
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/queryService", method = RequestMethod.GET)
    public JsonListResponseEntity<MedicalAPIEntity> queryService() {
        JsonListResponseEntity<MedicalAPIEntity> res = new JsonListResponseEntity<>();
        String[] query = { "pageIndex", "0", "pageSize", "18" };
        String url = getURL()
                + "rest/order/orderApplyInfoAction!orderFwnrsInfo.action？pageIndex=0&pageSize=18";
        JsonNode result = visitUserService.postRequest(url, null);
        int code = result.get("status").asInt();
        if (code == 1) {
            JsonNode data = result.get("response");
            List<MedicalAPIEntity> resData = toEntity(data);
            res.setContent(resData);
            // 头部广告图片
            List<String> keys = new ArrayList<String>();
            keys.add("yyservice.top.banner");// 医养融合服务列表头部引导图

            res.setCode(0);
        } else {
            String msg = result.get("message").asText();
            res.setMsg(msg);
            res.setCode(2000);
        }

        return res;
    }

    // 购买服务
    @VersionRange
    @RequestMapping(value = "/buyService", method = RequestMethod.POST)
    public JsonResponseEntity<Object> buyService(@RequestBody String request) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String userId = reader.readString("userId", false);
        String id = reader.readString("id", false);
        String addressID = reader.readString("addressId", false);
        String orderId = reader.readString("orderId", true);

        Object time = reader.readObject("time", false, List.class);

        ObjectMapper objectMapper=new ObjectMapper();
        String jsonArray = null;
        try {
            jsonArray = objectMapper.writeValueAsString(time);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        String url = getURL()
                + "rest/order/orderApplyInfoAction!buyOrderApplyInfo.action";

        String[] query = null;
        if (null != orderId && !orderId.equals("")) {
            query = new String[] { "id", id, "addressID", addressID, "orderid",
                    orderId, "time", jsonArray, "source", "05", "status", "0" };
        } else {
            query = new String[] { "id", id, "addressID", addressID, "time",
                    jsonArray, "source", "05", "status", "0" };
        }
        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1) {
            String msg = result.get("message").asText();
            res.setCode(0);
            res.setData(result.get("response"));
            res.setMsg(msg);
        } else {
            res.setCode(2000);
            res.setMsg("购买失败");
            System.out.println(result.get("message").asText());
        }
        return res;
    }

    // 取消订单
    @VersionRange
    @RequestMapping(value = "/cancleOrder", method = RequestMethod.DELETE)
    public JsonResponseEntity<Object> cancleOrder(@RequestParam String orderNo,
            @RequestParam String userId) {
        JsonResponseEntity<Object> res = new JsonResponseEntity<>();

        String url = getURL() + "/rest/client/order/cancel.action";
        String[] query = { "orderNo", orderNo };
        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1) {
            String msg = result.get("message").asText();
            res.setCode(0);
            res.setMsg(msg);
        } else {
            res.setCode(2000);
            res.setMsg(result.get("message").asText());
        }
        return res;
    }

    // 上门服务订单列表
    @VersionRange
    @RequestMapping(value = "/currentOrderList", method = RequestMethod.GET)
    public JsonListResponseEntity<ServiceOrderAPIEntity> currentOrderList(
            @RequestParam(required = false, defaultValue = "0") Integer flag,
            @RequestParam String type, @RequestParam String userId) {

        JsonListResponseEntity<ServiceOrderAPIEntity> res = new JsonListResponseEntity<ServiceOrderAPIEntity>();

        String url = getURL() + "/rest/users/orderAction!currentOrder.action";
        String status = "";
        if (type.equals("0")) {
            status = "0,1,2";
        } else if (type.equals("1")) {
            status = "3,4";
        } else {
            status = "0,1,2,3,4,9";
        }
        String[] query = { "pageIndex", String.valueOf(flag), "pageSize", "10",
                "type", status };
        String[] query2 = { "pageIndex", String.valueOf(flag + 1), "pageSize",
                "10", "type", status };


        JsonNode result = visitUserService.postRequest(userId, url, query);

        JsonNode result2 = visitUserService.postRequest(userId, url, query2);
        int code = result.get("status").asInt();
        if (code == 1) {

            JsonNode jn = result.get("response");
            if (null != jn) {
                List<ServiceOrderAPIEntity> data = toServiceEntity(jn);
                JsonNode jn2 = result2.get("response");
                if (null != jn2) {
                    res.setContent(data, true, "", String.valueOf(flag + 1));
                } else {
                    res.setContent(data);
                    res.setCode(0);
                }
            }else{
                res.setContent(new ArrayList<ServiceOrderAPIEntity>(), false, "","");
            }
        } else {
            String msg = result.get("message").asText();
            res.setMsg(msg);
            res.setCode(2000);
        }
        return res;
    }

    // 获取地址信息
    @VersionRange
    @RequestMapping(value = "/getAddressInfo", method = RequestMethod.GET)
    public JsonResponseEntity<Object> getAddressInfo(@RequestParam String userId) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        String url = getURL()
                + "/rest/order/orderApplyInfoAction!elderAddressInfo.action";
        String[] query = {"pageIndex","0","pageSize","20"};
        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1 || code == 0) {
            JsonNode data = result.get("response");
            res.setData(data);
            res.setCode(0);
        } else {
            String msg = result.get("message").asText();
            res.setMsg(msg);
            res.setCode(2000);
        }
        return res;
    }

    // 添加地址信息
    @VersionRange
    @RequestMapping(value = "/saveAddressInfo", method = RequestMethod.POST)
    public JsonResponseEntity<Object> saveAddressInfo(
            @RequestBody String request) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        JsonKeyReader reader = new JsonKeyReader(request);
        String userId = reader.readString("userId", false);
        String id = reader.readString("id", true);
        String qx = reader.readString("qx", false);
        String qxcode = reader.readString("qxcode", false);
        String jd = reader.readString("jd", false);
        String jdcode = reader.readString("jdcode", false);
        String jw = reader.readString("jw", false);
        String jwcode = reader.readString("jwcode", false);
        String mph = reader.readString("mph", false);//详细地址
        String personName = reader.readString("personName", false);
        String isDefault = reader.readString("isDefault", false);
        String phone = reader.readString("phone", false);

        /*
         * "qx":"普陀区"//区县 "qxcode":"310107000000"//区县代码 "jd":""//街道
         * "jdcode":""//街道代码 "jw":""//居委 "jwcode":""//居委代码 "lun":""//路，弄
         * "mph":""//门牌号
         */
        String url = getURL()
                + "/rest/order/orderApplyInfoAction!saveElderAddressInfo.action";

        String addressDetail = qx + jd + jw + mph;
        String[] query = null;
        if (null != id && !id.equals("")) {
            query = new String[] { "id", id, "personName", personName,
                    "isDefault", isDefault, "phone", phone, "qx", qx, "qxcode",
                    qxcode, "jd", jd, "jdcode", jdcode, "jw", jw, "jwcode",
                    jwcode,"mph", mph, "addressDetail",
                    addressDetail };
        } else {
            query = new String[] { "personName", personName, "isDefault",
                    isDefault, "phone", phone, "qx", qx, "qxcode", qxcode,
                    "jd", jd, "jdcode", jdcode, "jw", jw, "jwcode", jwcode,
                    "mph", mph, "addressDetail", addressDetail };
        }

        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1) {
            String msg = result.get("message").asText();
            res.setCode(0);
            res.setMsg(msg);
        } else {
            res.setCode(2000);
            res.setMsg("添加失败");
        }
        return res;
    }

    // 修改地址
    @VersionRange
    @RequestMapping(value = "/updateAddressInfo", method = RequestMethod.POST)
    public JsonResponseEntity<Object> updateAddressInfo(
            @RequestParam String userId) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        String url = getURL()
                + "/rest/order/orderApplyInfoAction!saveElderAddressInfo.action";
        String[] query = {};

        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1) {
            String msg = result.get("message").asText();
            res.setCode(0);
            res.setMsg(msg);
        } else {
            res.setCode(2000);
            res.setMsg("修改失败");
        }
        return res;
    }

    // 删除地址
    @VersionRange
    @RequestMapping(value = "/delAddressInfo", method = RequestMethod.DELETE)
    public JsonResponseEntity<Object> delAddressInfo(@RequestParam String id,
            @RequestParam String userId) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        String url = getURL()
                + "/rest/order/orderApplyInfoAction!delElderAddressInfo.action";
        String[] query = { "id", id };

        JsonNode result = visitUserService.postRequest(userId, url, query);
        int code = result.get("status").asInt();
        if (code == 1) {
            String msg = result.get("message").asText();
            res.setCode(0);
            res.setMsg(msg);
        } else {
            String msg = result.get("message").asText();
            res.setCode(2000);
            res.setMsg(msg);
        }
        return res;
    }

    // 查询街道
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/getJdInfo", method = RequestMethod.GET)
    public JsonResponseEntity<Object> getJdInfo() {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        String url = getURL() + "/rest/users/jdInfo.action";
        JsonNode result = visitUserService.postRequest(url,null);
        int code = result.get("status").asInt();
        if (code == 1) {
            JsonNode data = result.get("response");
            res.setCode(0);
            res.setData(data);
        } else {
            res.setCode(2000);
            res.setMsg("查询失败");
        }
        return res;
    }

    // 根据街道id查询居委会
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/getJwInfo", method = RequestMethod.GET)
    public JsonResponseEntity<Object> getJwInfo(
            @RequestParam(required = false) String jdId) {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        String url = getURL() + "/rest/users/jwInfo.action";

        JsonNode result = visitUserService.postRequest(url, new String[]{
                "jdId", jdId});
        int code = result.get("status").asInt();
        if (code == 1) {
            JsonNode data = result.get("response");
            res.setCode(0);
            res.setData(data);
        } else {
            res.setCode(2000);
            res.setMsg("查询失败");
        }
        return res;
    }

    // 获取系统时间
    @WithoutToken
    @VersionRange
    @RequestMapping(value = "/getSystemTime", method = RequestMethod.GET)
    public JsonResponseEntity<Object> getSystemTime() {

        JsonResponseEntity<Object> res = new JsonResponseEntity<>();
        SimpleDateFormat dateFormater = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        res.setData(dateFormater.format(date));
        return res;
    }

    private String getURL() {
        return this.env.getProperty("yyservice.service.host")+"/";
    }

    private String getImgUrl(){
        return this.env.getProperty("yyservice.img.host");
    }

    private List<MedicalAPIEntity> toEntity(JsonNode data) {
        List<MedicalAPIEntity> list = new ArrayList<>();
        JSONArray arry = JSONArray.fromObject(data.toString());
        ObjectMapper objectMapper=new ObjectMapper();
        String jsonArray = null;
        try {
            jsonArray = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < arry.size(); i++) {
            MedicalAPIEntity entity = new MedicalAPIEntity();
            JSONObject job = arry.getJSONObject(i);
            entity.setId(job.get("id").toString());
            entity.setFwnrName(job.get("fwnr_name").toString());
            entity.setMemo(job.get("memo").toString());
            entity.setGrFee(job.get("gr_fee"));
            entity.setYbFee(job.get("yb_fee"));
            entity.setSmallimg(getImgUrl() + "/" + job.get("smallimg").toString());
            entity.setBigimg(getImgUrl() + "/" + job.get("bigimg").toString());
            entity.setDj1(job.get("dj1"));
            entity.setIsneedphoto(job.getString("isneedphoto"));
            list.add(entity);
        }
        return list;
    }

    private List<ServiceOrderAPIEntity> toServiceEntity(JsonNode data) {
        List<ServiceOrderAPIEntity> list = new ArrayList<>();
        JSONArray arry = JSONArray.fromObject(data.toString());
        for (int i = 0; i < arry.size(); i++) {
            ServiceOrderAPIEntity entity = new ServiceOrderAPIEntity();
            JSONObject job = arry.getJSONObject(i);
            entity.setId(job.getString("id"));
            entity.setFwnrname(job.getString("fwnrname"));
            entity.setCreateTime(job.get("create_time"));
            entity.setAddress(job.getString("address"));
            entity.setIspay(job.getString("ispay"));
            entity.setLxr(job.getString("lxr"));
            entity.setLxrlxdh(job.getString("lxrlxdh"));
            entity.setTotalfee(job.get("totalfee"));
            JSONArray orderList = job.getJSONArray("orderList");
            for (int j=0; j<orderList.size(); j++){
                JSONObject orderObj = orderList.getJSONObject(j);
                orderObj.put("begintime", getServiceShowTime(getString(orderObj, "begintime"), null));
                orderObj.put("endtime", getServiceShowTime(getString(orderObj, "endtime"), null));
                orderObj.put("realyytime", getServiceShowTime(getString(orderObj, "realyytime"), getString(orderObj, "realyysjd")));
                orderObj.put("yytime", getServiceShowTime(getString(orderObj, "yytime"), getString(orderObj, "yysjd")));
            }
            entity.setOrderList(job.get("orderList"));
            entity.setStatus(job.getString("status"));
            entity.setSmallimg(getImgUrl() + "/" + job.getString("smallimg"));
            List<Map<String, Object>> obj = (List) job.get("photoList");
            if (null != obj && obj.size() > 0) {
                entity.setPhotoList(photoList(obj));
            }
            list.add(entity);
        }
        return list;
    }

    private String getString(JSONObject obj, String key){
        if (obj.containsKey(key)){
            return obj.get(key) != null ? obj.getString(key) : "";
        }else {
            return "";
        }
    }


    public List<Object> photoList(List<Map<String, Object>> obj) {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < obj.size(); i++) {
            String photoaddress = getImgUrl() + "/" + obj.get(i).get("photoaddress");
            list.add(photoaddress);
        }

        return list;
    }

    private SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    /**
     * 时间格式转换
     * @param dateTime 时间 2016-05-05 12:12:22
     * @param apm 上午/下午
     * @return
     */
    private String getServiceShowTime(String dateTime, String apm){
        if (StringUtils.isEmpty(dateTime)){
            return null;
        }
        String showTime = "";
        try {
            Date date = sdf.parse(dateTime);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
            w = w < 0 ? 0 : w;
            String week = weekDays[w];
            String day = dateFm.format(date);
            if (StringUtils.isEmpty(apm)){
                apm = date.getHours() < 12 ? "上午" : "下午";
            }else {
                if (apm.equalsIgnoreCase("pm")){
                    apm = "下午";
                }else if (apm.equalsIgnoreCase("am")){
                    apm = "上午";
                }
            }
            showTime = day + " " + week + " " + apm;
        }catch (Exception e){

        }
        return StringUtils.isEmpty(showTime) ? dateTime : showTime;
    }
}
