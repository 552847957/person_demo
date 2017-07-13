package com.wondersgroup.healthcloud.services.user.impl;

import com.wondersgroup.healthcloud.utils.DateFormatter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by matt on 17/7/6.
 */
@Component("userActiveStatExportServiceImpl")
public class UserActiveStatExportServiceImpl {
    private final static String template = "template/UserActiveStatByDate.xlsx";
    private final static String template_total = "template/UserActiveStatByTotal.xlsx";

    public void exportExcel(List excels,String type,OutputStream out) throws IOException{
        String temp = template;
        if(!type.equals("1")){
            temp = template_total;
        }
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(temp);
        XSSFWorkbook xwb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = xwb.getSheetAt(0);

        if(type.equals("1")){
            buildDataBydate(excels,sheet);
        }else{
            buildDataByTotal(excels,sheet);
        }

        xwb.write(out);
        out.flush();
        out.close();

    }

    private void buildDataByTotal(List excels, XSSFSheet sheet) {
        int index = 2;
        Row row = null;
        Iterator it = excels.iterator();
        while(it.hasNext()){
            Map data = (Map)it.next();
            row = sheet.createRow(index++);
            createCell(row, 0, DateFormatter.format((Date) data.get("stat_date"), "yyyy-MM-dd"));
            createCell(row, 1, Integer.parseInt(data.get("regtotal").toString()));
            createCell(row, 2, Integer.parseInt(data.get("identifytotal").toString()));
        }

    }

    private void buildDataBydate(List excels, XSSFSheet sheet) {
        int index = 2;
        Row row = null;
        Iterator it = excels.iterator();
        while(it.hasNext()){
            Map data = (Map)it.next();
            row = sheet.createRow(index++);
            createCell(row, 0, DateFormatter.format((Date)data.get("stat_date"),"yyyy-MM-dd"));
            createCell(row, 1, (int)data.get("regname"));
            createCell(row, 2, (int)data.get("identifynum"));
            createCell(row, 3, (int)data.get("loginusernum"));
            createCell(row, 4, (int)data.get("logindevicenum"));
        }
    }

    private void createCell(Row row, int index, int value){
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
    }

    private void createCell(Row row, int index, String value){
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
    }
}
