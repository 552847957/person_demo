package com.wondersgroup.healthcloud.api.http.controllers.game;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.game.GamePrize;
import com.wondersgroup.healthcloud.jpa.enums.GameType;
import com.wondersgroup.healthcloud.jpa.repository.game.GamePrizeRepository;
import com.wondersgroup.healthcloud.jpa.repository.game.GameRepository;
import com.wondersgroup.healthcloud.services.game.GameService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 奖品后台管理
 * Created by zhuchunliu on 2016/10/21.
 */
@RestController
@RequestMapping("/prize")
public class PrizeController {
    @Autowired
    private GamePrizeRepository gamePrizeRepo;
    @Autowired
    private GameRepository gameRepo;
    @Autowired
    private GameService gameService;

    @PostMapping(path = "/update")
    public JsonResponseEntity update(@RequestBody GamePrize gamePrize){
        gamePrize.setDelFlag("0");
        gamePrize.setGameId(gameRepo.getTopGame(GameType.TURNTABLE.type).getId());
        gamePrize.setUpdateDate(new Date());
        gamePrizeRepo.save(gamePrize);
        return new JsonResponseEntity(0,"保存成功");
    }

    @DeleteMapping(path = "/delete")
    public JsonResponseEntity update(@RequestParam(name = "id",required = true) Integer id){
        GamePrize gamePrize = gamePrizeRepo.findOne(id);
        gamePrize.setDelFlag("1");
        gamePrize.setUpdateDate(new Date());
        gamePrizeRepo.save(gamePrize);
        return new JsonResponseEntity(0,"删除成功");
    }

    @GetMapping(path = "/list")
    public JsonResponseEntity list(){
        List<Map<String,Object>> list = gameService.getGamePrize(GameType.TURNTABLE.type);
        return new JsonResponseEntity(0,null,list);
    }


    @PostMapping(path = "/win")
    public JsonResponseEntity win(@RequestBody Pager pager){
        List<Map<String,Object>> list = gameService.getPrizeWin(pager.getNumber(), pager.getSize(),
                pager.getParameter().get("activityid").toString(), GameType.TURNTABLE.type);
        for(Map<String,Object> map : list){
            map.put("date",map.get("date").toString().substring(0, 19));
        }
        pager.setData(list);
        pager.setTotalElements(gameService.getPrizeWinTotal(pager.getParameter().get("activityid").toString(), GameType.TURNTABLE.type));
        return new JsonResponseEntity(0,null,pager);
    }

    @GetMapping(path = "/export")
    public void export(
            @RequestParam(value = "activityid",required = true) String activityid,
            HttpServletResponse response) throws Exception{
        List<Map<String,Object>> list = gameService.getPrizeWinList(activityid , GameType.TURNTABLE.type);
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("content-disposition","attachment;filename=" + URLEncoder.encode(GameType.TURNTABLE.name+"中奖信息","UTF-8") + ".xls");
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(GameType.TURNTABLE.name+"中奖信息");
        sheet.setColumnWidth(0,40*256);
        sheet.setColumnWidth(1,25*256);
        sheet.setColumnWidth(2,30*256);

        HSSFRow titleRow = sheet.createRow((short) 0);

        HSSFFont font = workbook.createFont();
        font.setBold(true);

        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        titleRow.setRowStyle(style);

        HSSFCell cellFirst = titleRow.createCell(0);
        cellFirst.setCellValue("用户主键");
        cellFirst.setCellStyle(style);

        HSSFCell cellSecond = titleRow.createCell(1);
        cellSecond.setCellValue("奖品信息");
        cellSecond.setCellStyle(style);

        HSSFCell cellThird = titleRow.createCell(2);
        cellThird.setCellValue("中奖日期");
        cellThird.setCellStyle(style);


        for(int index=0 ;index < list.size(); index++){
            HSSFRow row = sheet.createRow(index+1);
            row.createCell(0).setCellValue(list.get(index).get("registerid").toString());
            row.createCell(1).setCellValue(list.get(index).get("name").toString());
            row.createCell(2).setCellValue(list.get(index).get("date").toString().substring(0, 19));
        }
        OutputStream stream = response.getOutputStream();
        workbook.write(stream);
        stream.flush();
        stream.close();
    }

}
