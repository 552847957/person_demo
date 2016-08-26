package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.api.utils.Pager;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.jpa.entity.article.ForwardArticle;
import com.wondersgroup.healthcloud.services.article.ForwardArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dukuanxin on 2016/8/25.
 */
@RestController
@RequestMapping("/back/home/article")
public class ForwardArticleController {

    @Autowired
    private ForwardArticleService forwardArticleService;
    @PostMapping("/list")
    public Pager findHomeArticleList(@RequestBody Pager pager) {
        Map param = new HashMap();
        param.putAll(pager.getParameter());
        if(param.get("id")!=null&&!param.get("id").equals("")){
            int id= (int) param.get("id");
            pager.setData(forwardArticleService.queryById(id));
            return pager;
        }
        int pageNo=pager.getNumber();
        int pageSize = pager.getSize();
        int status= (int) param.get("status");
        String areaCode=param.get("mainArea").toString();
        pager.setData(forwardArticleService.queryPageForWardArticle(status,pageNo,pageSize,areaCode));
        int total=forwardArticleService.getCount(status,areaCode);
        int totalPage = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;
        pager.setTotalElements(total);
        pager.setTotalPages(totalPage);
        return pager;
    }
    @PostMapping("/save")
    public JsonResponseEntity save(@RequestBody ForwardArticle forwardArticle){
        JsonResponseEntity response=new JsonResponseEntity();

        if(forwardArticle.getMain_area()==null){
            response.setCode(2000);
            response.setMsg("区域不能为空");
            return response;
        }

        forwardArticleService.updateForwardArticle(forwardArticle);
        response.setMsg("成功");
        return response;
    }

}
