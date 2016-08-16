package com.wondersgroup.healthcloud.api.http.controllers.article;

import com.wondersgroup.healthcloud.jpa.entity.article.Article;
import com.wondersgroup.healthcloud.services.article.ManageArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yanshuai on 15/6/26
 */
@RestController
@RequestMapping(value = "/web/article")
public class ArticleController {

    @Autowired
    private ManageArticleService manageArticleService;


}
