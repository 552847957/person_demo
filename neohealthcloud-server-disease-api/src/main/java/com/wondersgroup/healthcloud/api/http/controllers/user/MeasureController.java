package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.user.RegisterInfo;
import com.wondersgroup.healthcloud.services.user.UserService;
import com.wondersgroup.healthcloud.utils.DateFormatter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Created by zhaozhenxing on 2016/12/21.
 */
@RestController
@RequestMapping("/api/measure")
public class MeasureController {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(MeasureController.class);

    @Autowired
    private UserService userService;

    @Value("${internal.api.service.measure.url}")
    private String host;
    private static final String requestUploadPath = "%s/api/measure/upload/%s";
    private static final String recentMeasureHistory = "%s/api/measure/3.0/recentHistory/%s?%s";
    private static final String recentMeasureHistoryByDate = "%s/api/measure/3.0/recentHistoryByDate/%s?%s";
    private static final String recentMeasureStatisticalData = "%s/api/measure/3.0/recentStatisticalData?%s";
    private static final String queryHistoryRecordByDate = "%s/api/measure/3.0/queryHistoryRecordByDate?%s";
    private static final String requestModifyPath = "%s/api/measure/3.0/modify/%s";
    private static final String queryNearestPath = "%s/api/measure/%s/nearest?%s";
    private static final String bmiH5ChartPath = "%s/api/measure/bmi/chart?%s";
    private static final String historyHba1cPath = "%s/api/measure/history/hba1c?%s";

    private RestTemplate template = new RestTemplate();

    /**
     * @param type  0－BMI(身高体重)
     *              1－血氧
     *              2－血压
     *              3－血糖
     *              4－记步
     *              5－腰臀比
     *              10-糖化血红蛋白
     * @param paras
     * @return
     */
    @VersionRange
    @PostMapping("upload/{type}")
    public JsonResponseEntity<?> uploadMeasureIndexs(@PathVariable int type, @RequestBody Map<String, Object> paras) {
        try {
            String registerId = (String) paras.get("registerId");
            RegisterInfo info = userService.findRegOrAnonymous(registerId);

            String personCard = info.getPersoncard();
            if (personCard != null) {
                paras.put("personCard", personCard);
            }

            String url = String.format(requestUploadPath, host, type);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add("access-token", "version3.0");
            ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "数据上传成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("上传体征数据失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据上传失败");
    }

    /**
     * 修改测量数据
     *
     * @param type  0－BMI(身高体重)
     *              1－血氧
     *              2－血压
     *              3－血糖
     *              4－记步
     *              5－腰臀比
     *              10-糖化血红蛋白
     * @param paras
     * @return
     */
    @VersionRange
    @PostMapping("modify/{type}")
    public JsonResponseEntity<?> updateMeasureIndexs(@PathVariable int type, @RequestBody Map<String, Object> paras) {
        try {
            String registerId = (String) paras.get("registerId");
            RegisterInfo info = userService.findRegOrAnonymous(registerId);

            String personCard = info.getPersoncard();
            if (personCard != null) {
                paras.put("personCard", personCard);
            }

            String url = String.format(requestModifyPath, host, type);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            headers.add("access-token", "version3.0");
            ResponseEntity<Map> response = template.postForEntity(url, new HttpEntity<>(paras, headers), Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "数据更新成功");
                }
            }
        } catch (RestClientException e) {
            log.info("体征数据更新失败", e);
        }
        return new JsonResponseEntity<>(1000, "数据更新失败");
    }


    @RequestMapping(value = "/lastWeekHistory", method = RequestMethod.GET)
    public JsonResponseEntity measureHistory(@RequestParam(name = "uid") String registerId,
                                             @RequestParam(required = false) String personCard) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> rtnMap = new HashMap<>();
        try {
            List<JsonNode> lastWeekData = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            int count = 0;
            for (int i = 0; i < 7; i++) {
                lastWeekData.add(((ObjectNode) mapper.readTree("{}")).put("date", DateFormatter.dateFormat(DateTime.now().plusDays(-i).toDate())));
            }
            RegisterInfo info = userService.getOneNotNull(registerId);

            try {
                String statisticalparam = "registerId=".concat(registerId)
                        .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard());
                String url = String.format(recentMeasureStatisticalData, host, statisticalparam);
                ResponseEntity<Map> response = buildGetEntity(url, Map.class);
                if (response.getStatusCode().equals(HttpStatus.OK)) {
                    if (0 == (int) response.getBody().get("code")) {
                        String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                        if (StringUtils.isNotEmpty(jsonStr)) {
                            JsonNode resultJson = mapper.readTree(jsonStr);
                            Map<String, Object> tmpMap = new HashMap<>();
                            tmpMap.put("high", resultJson.get("high").asInt());
                            tmpMap.put("normal", resultJson.get("normal").asInt());
                            tmpMap.put("low", resultJson.get("low").asInt());
                            rtnMap.put("status", tmpMap);
                        }
                    }
                }
            } catch (Exception ex) {
                log.error("MeasureController.measureHistory error --> " + ex.getLocalizedMessage());
            }

            String param = "registerId=".concat(registerId)
                    .concat("&sex=").concat(StringUtils.isEmpty(info.getGender()) ? "1" : info.getGender())
                    .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard());
            String url = String.format(recentMeasureHistory, host, "3", param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.get("content").iterator();// 最近10天测量记录
                        DateTime today = DateTime.now();
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();// 单日测量记录
                            String date = jsonNode.get("date").asText();
                            if (date != null
                                    && !new DateTime(date).isBefore(today.plusDays(-6).withTimeAtStartOfDay().getMillis())
                                    && new DateTime(date).isBefore(today.plusDays(1).withTimeAtStartOfDay())) {
                                String[] dayDatas = {"", "", "", "", "", "", "", ""};
                                Map<Integer, List<JsonNode>> testPeriodMap = groupByTestPerid(jsonNode);
                                if(null != testPeriodMap && testPeriodMap.keySet().size() > 0){
                                    for(Integer testPeriodKey:testPeriodMap.keySet()){
                                        List<JsonNode> testPeriodList =  testPeriodMap.get(testPeriodKey);
                                        sortList(testPeriodList);
                                        Iterator<JsonNode> itJsonNodeData = testPeriodList.iterator();
                                        while (itJsonNodeData.hasNext()) {
                                            JsonNode tmpJson = itJsonNodeData.next();
                                            int testPeriod = tmpJson.get("testPeriod").asInt();
                                            if (0 <= testPeriod && testPeriod <= 7) {
                                                //  7 0 1 2 3 4 5 6 这样排序,醉了
                                                if (testPeriod == 7) {
                                                    if (StringUtils.isBlank(dayDatas[0])) {
                                                        dayDatas[0] = tmpJson.get("fpgValue").asText();
                                                    } else {
                                                        String day = dayDatas[0];
                                                        if (day.split("&").length < 4) {// 同一时间段最新4条数据
                                                            dayDatas[0] = day + (StringUtils.isBlank(day) ? "" : "&") + tmpJson.get("fpgValue").asText();
                                                        }
                                                    }


                                                } else {
                                                    String day = dayDatas[testPeriod + 1];
                                                    if (day.split("&").length < 4) {// 同一时间段最新4条数据
                                                        dayDatas[testPeriod + 1] = day + (StringUtils.isBlank(day) ? "" : "&") + tmpJson.get("fpgValue").asText();
                                                    }
                                                }
                                            }
                                        }


                                    }

                                }

                                StringBuffer strBuf = new StringBuffer();
                                for (int j = 0; j < dayDatas.length; j++) {
                                    if (j == 0) {
                                        strBuf.append(dayDatas[j]);
                                    } else {
                                        strBuf.append(",").append(dayDatas[j]);
                                    }
                                }
                                ((ObjectNode) jsonNode).put("data", strBuf.toString());
                                lastWeekData.set(Days.daysBetween(new DateTime(date), today).getDays(), jsonNode);
                            }
                            count++;
                        }
                    }
                }
            }

            rtnMap.put("list", lastWeekData);
            rtnMap.put("count", count);
            result.setData(rtnMap);
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }


    /**
     * 将一天的血糖数据，按照 testPeriod 分组
     * @param jsonNode
     * @return
     */
    private Map<Integer, List<JsonNode>> groupByTestPerid(JsonNode jsonNode) {
        Map<Integer, List<JsonNode>> periodMap = new HashMap<Integer, List<JsonNode>>();
        JsonNode jsonNodeData = jsonNode.get("data");
        Iterator<JsonNode> itJsonNodeData = jsonNodeData.iterator();
        List<JsonNode> list = IteratorUtils.toList(itJsonNodeData);

        for (JsonNode node : list) {
            Integer testPeriod = node.get("testPeriod").asInt();
            if (periodMap.keySet().contains(testPeriod)) {
                periodMap.get(testPeriod).add(node);
            } else {
                List<JsonNode> keyList = new ArrayList<JsonNode>();
                keyList.add(node);
                periodMap.put(testPeriod, keyList);
            }

        }
        return periodMap;
    }

    /**
     * 按时间排序后，除去第一条，剩下的要按照 measureWay 值的2,3,1 顺序排序
     *
     * @param list
     * @return
     */
    private List<JsonNode> sortList(List<JsonNode> list) {
        if (CollectionUtils.isNotEmpty(list) && list.size() > 2) {
            JsonNode firstNode = list.get(0);
            list.remove(0);
            try {
                Collections.sort(list, new Compartor());
            } catch (Exception e) {
//                e.printStackTrace();
                log.info("排序异常 ", e);
            }
            list.add(0, firstNode);
        }
        return list;

    }

    @RequestMapping(value = "/getMeasureHistoryByDate", method = RequestMethod.GET)
    public JsonResponseEntity measureHistoryByDate(@RequestParam(name = "uid") String registerId,
                                                   @RequestParam(required = false) String personCard,
                                                   @RequestParam(name = "beginDate", required = false) String begin_date) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map<String, Object> rtnMap = new HashMap<>();
        String flagDate = StringUtils.isEmpty(begin_date) ? DateTime.now().toString("yyyy-MM-dd") : begin_date;
        DateTime beginDateTime = new DateTime(flagDate).plusMonths(-1).plusDays(1);
        DateTime endDateTime = new DateTime(flagDate);

        DateTime nextMonth = new DateTime(flagDate).plusMonths(1);
        if (!nextMonth.isAfter(DateTime.now())) {
            rtnMap.put("nextMonth", nextMonth.toString("yyyy-MM-dd"));
        }
        rtnMap.put("frontMonth", new DateTime(flagDate).plusMonths(-1).toString("yyyy-MM-dd"));
        try {
            List<JsonNode> monthDate = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            int days = -Days.daysBetween(endDateTime, beginDateTime).getDays() + 1;
            for (int i = 0; i < days; i++) {
                monthDate.add(((ObjectNode) mapper.readTree("{}")).put("date", DateFormatter.dateFormat(endDateTime.plusDays(-i).toDate())));
            }
            RegisterInfo info = userService.getOneNotNull(registerId);
            String param = "registerId=".concat(registerId)
                    .concat("&sex=").concat(StringUtils.isEmpty(info.getGender()) ? "1" : info.getGender())
                    .concat("&personCard=").concat(StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard())
                    .concat("&begin_date=").concat(beginDateTime.toString("yyyy-MM-dd"))
                    .concat("&end_date=").concat(endDateTime.toString("yyyy-MM-dd"));
            String url = String.format(recentMeasureHistoryByDate, host, "3", param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    String jsonStr = mapper.writeValueAsString(response.getBody().get("data"));
                    if (StringUtils.isNotEmpty(jsonStr)) {
                        JsonNode resultJson = mapper.readTree(jsonStr);
                        Iterator<JsonNode> contentJson = resultJson.get("content").iterator();// 最近10天测量记录
                        while (contentJson.hasNext()) {
                            JsonNode jsonNode = contentJson.next();// 单日测量记录
                            String date = jsonNode.get("date").asText();
                            if (date != null
                                    && !new DateTime(date).isBefore(beginDateTime.withTimeAtStartOfDay().getMillis())
                                    && new DateTime(date).isBefore(endDateTime.plusDays(1).withTimeAtStartOfDay())) {
                                String[] dayDatas = {"", "", "", "", "", "", "", ""};
                                JsonNode jsonNodeData = jsonNode.get("data");
                                Iterator<JsonNode> itJsonNodeData = jsonNodeData.iterator();
                                List<JsonNode> list = IteratorUtils.toList(itJsonNodeData);
                                String str = list.get(0).get("fpgValue").asText();
                                list.remove(0);
                                Collections.sort(list, new Compartor());
                                for (int i = 0; i < list.size(); i++) {
                                    JsonNode tmpJson = list.get(i);
                                    int testPeriod = tmpJson.get("testPeriod").asInt();
                                    if (0 <= testPeriod && testPeriod <= 7) {
                                        //  7 0 1 2 3 4 5 6 这样排序,醉了
                                        if (testPeriod == 7) {
                                            if (StringUtils.isEmpty(dayDatas[0])) {// 同一时间段仅获取最新数据
                                                dayDatas[0] = tmpJson.get("fpgValue").asText();
                                            }
                                        } else {
                                            String day = dayDatas[testPeriod + 1] + (i == 0 ? str : "");
                                            if (day.split("&").length < 4) {// 第一个是按时间最新的一条，后面3条是按上传类型排序
                                                dayDatas[testPeriod + 1] = day + (StringUtils.isBlank(day) ? "" : "&") + tmpJson.get("fpgValue").asText();
                                            }
                                        }
                                    }
                                }
                                StringBuffer strBuf = new StringBuffer();
                                for (int j = 0; j < dayDatas.length; j++) {
                                    if (j == 0) {
                                        strBuf.append(dayDatas[j]);
                                    } else {
                                        strBuf.append(",").append(dayDatas[j]);
                                    }
                                }
                                ((ObjectNode) jsonNode).put("data", strBuf.toString());
                                monthDate.set(Days.daysBetween(new DateTime(date), endDateTime).getDays(), jsonNode);
                            }
                        }
                    }
                }
            }
            rtnMap.put("before", dateBeforeIsExistData(registerId, StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard(), new DateTime(flagDate).plusMonths(-1).plusDays(-1).toString("yyyy-MM-dd"), true));
            rtnMap.put("after", dateBeforeIsExistData(registerId, StringUtils.isEmpty(info.getPersoncard()) ? "" : info.getPersoncard(), new DateTime(flagDate).plusDays(1).toString("yyyy-MM-dd"), false));
            rtnMap.put("thisMonth", monthDate);
            rtnMap.put("thisMonth", monthDate);
            result.setData(rtnMap);
        } catch (Exception e) {
            log.info("近期历史数据获取失败", e);
        }
        return result;
    }

    class Compartor implements Comparator<JsonNode> {
        @Override
        public int compare(JsonNode o1, JsonNode o2) {
            String o1va = o1.get("measureWay").asText();
            String o2va = o2.get("measureWay").asText();
            if (o1va.equals(o2va)) {
                return 1;
            }
            if ("2".equals(o1va)) {
                return -1;
            }
            if ("2".equals(o2va)) {
                return 1;
            }
            if ("3".equals(o1va)) {
                return -1;
            }
            if ("3".equals(o2va)) {
                return 1;
            }
            return -1;
        }
    }

    /**
     * 查询最新的一条数据
     *
     * @param type
     * @param registerId
     * @param personCard
     * @return
     */
    @VersionRange
    @GetMapping("{type}/nearest")
    public JsonResponseEntity<?> queryNearest(@PathVariable int type, String registerId, @RequestParam(defaultValue = "") String personCard) {
        try {

            StringBuffer param = new StringBuffer();
            param.append("registerId=" + registerId).append("&personCard=" + personCard);

            String url = String.format(queryNearestPath, host, type, param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("查询失败", e);
        }
        return new JsonResponseEntity<>(1000, "查询失败");
    }

    /**
     * BMI图表H5
     *
     * @param registerId
     * @param personCard
     * @param date       "2017-04-15"
     * @return
     */
    @VersionRange
    @GetMapping("chart/bmi")
    public JsonResponseEntity<?> chartBmi(
            String registerId,
            @RequestParam(required = false) String personCard,
            String date,
            @RequestParam(defaultValue = "false") Boolean isBefore,
            @RequestParam(defaultValue = "5") Integer dayAmount) {
        try {

            StringBuffer param = new StringBuffer();
            param.append("registerId=" + registerId).append("&personCard=" + personCard)
                    .append("&date=" + date).append("&isBefore=" + isBefore).append("&dayAmount=" + dayAmount);
            String url = String.format(bmiH5ChartPath, host, param);

            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("查询失败", e);
        }
        return new JsonResponseEntity<>(1000, "查询失败");
    }

    /**
     * 糖化血红蛋白历史数据分页
     *
     * @param registerId
     * @param personCard
     * @param flag       页数 从0 开始
     * @param pageSize   每页条数
     * @return
     */
    @VersionRange
    @GetMapping("history/hba1c")
    public JsonResponseEntity<?> queryHba1cHistoryData(@RequestParam(defaultValue = "0") Integer flag,
                                                       @RequestParam(defaultValue = "10") Integer pageSize,
                                                       @RequestParam String registerId, String personCard) {
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("registerId=" + registerId).append("&personCard=" + personCard)
                    .append("&flag=" + flag).append("&pageSize=" + pageSize);
            String url = String.format(historyHba1cPath, host, sb.toString());
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    return new JsonResponseEntity<>(0, "查询成功", response.getBody().get("data"));
                }
            }
        } catch (RestClientException e) {
            log.info("查询失败", e);
        }
        return new JsonResponseEntity<>(1000, "查询失败");
    }


    private <T> ResponseEntity<T> buildGetEntity(String url, Class<T> responseType, Object... urlVariables) {
        RestTemplate template = new RestTemplate();
        return template.exchange(url, HttpMethod.GET, new HttpEntity<>(buildHeader()), responseType, urlVariables);
    }

    private HttpHeaders buildHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("isStandard", "false");// 非标准版
        return headers;
    }

    public boolean dateBeforeIsExistData(String registerId, String personCard, String date, Boolean isBefore) {
        boolean result = false;
        try {
            String param = "registerId=".concat(registerId)
                    .concat("&personCard=").concat(personCard)
                    .concat("&date=").concat(date)
                    .concat("&isBefore=" + isBefore);
            String url = String.format(queryHistoryRecordByDate, host, param);
            ResponseEntity<Map> response = buildGetEntity(url, Map.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                if (0 == (int) response.getBody().get("code")) {
                    result = Integer.parseInt(response.getBody().get("data").toString()) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" dateBeforeIsExistData " + e.getMessage());
        }
        return result;
    }
}