package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.services.user.UserActiveStatService;
import com.wondersgroup.healthcloud.services.user.impl.UserActiveStatExportServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matt on 17/7/5.
 */
@RestController
@RequestMapping("/admin/user")
public class UserStatisticsController {
    @Autowired
    UserActiveStatService userActiveStatServiceImpl;

    @Autowired
    UserActiveStatExportServiceImpl userActiveStatExportServiceImpl;

    /**
     * 按日统计用户数据
     * @param pager
     * @return
     */
    @PostMapping("/statistics")
    public Pager statistics(@RequestBody Pager pager){
        Map param = new HashMap();
        pager.setSize(-1);
        param.putAll(pager.getParameter());
        List list = userActiveStatServiceImpl.queryUserActiveStatList(param);
        pager.setData(list);
        return pager;
    }


    @GetMapping(value = "/statistics/export",produces = "application/vnd.ms-excel")
    public ResponseEntity<InputStreamResource> exportExcel(String startTime, String endTime, String type, HttpServletRequest request) throws IOException {
        Map param = new HashMap();
        param.put("startTime",startTime);
        param.put("endTime",endTime);
        param.put("pageSize",-1);
        param.put("pageNo", 0);
        param.put("type",type.toString());
        List excelData = userActiveStatServiceImpl.queryUserActiveStatList(param);
        String userAgent = request.getHeader("USER-AGENT");
        String excelName = generateExcelName(userAgent);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        userActiveStatExportServiceImpl.exportExcel(excelData, type, out);

        return ResponseEntity
                .ok()
                .header("Content-Disposition","attachment; filename=" + excelName )
                .contentLength(out.size())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(new ByteArrayInputStream(out.toByteArray())));
    }


    private String generateExcelName(String userAgent) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String dateStr = format.format(new Date());
        String fileName = String.format("用户数据统计-%s.xlsx", dateStr);
        try {
            if(StringUtils.contains(userAgent, "MSIE")){//IE浏览器
                fileName = URLEncoder.encode(fileName, "UTF8");
            }else if(StringUtils.contains(userAgent, "Mozilla")){//google,火狐浏览器
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            }else{
                fileName = URLEncoder.encode(fileName,"UTF8");//其他浏览器
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getCause());
        }
        return fileName;
    }
}
