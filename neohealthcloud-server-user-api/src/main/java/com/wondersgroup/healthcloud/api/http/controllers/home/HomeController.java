package com.wondersgroup.healthcloud.api.http.controllers.home;

import com.google.common.collect.Lists;
import com.wondersgroup.healthcloud.api.http.dto.faq.FaqDTO;
import com.wondersgroup.healthcloud.common.appenum.ImageTextEnum;
import com.wondersgroup.healthcloud.common.http.annotations.WithoutToken;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.session.AccessToken;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.jpa.entity.faq.Faq;
import com.wondersgroup.healthcloud.jpa.entity.imagetext.ImageText;
import com.wondersgroup.healthcloud.jpa.entity.notice.Notice;
import com.wondersgroup.healthcloud.services.article.ManageNewsArticleService;
import com.wondersgroup.healthcloud.services.article.dto.NewsArticleListAPIEntity;
import com.wondersgroup.healthcloud.services.faq.FaqService;
import com.wondersgroup.healthcloud.services.imagetext.ImageTextService;
import com.wondersgroup.healthcloud.services.imagetext.dto.BasicImageTextDTO;
import com.wondersgroup.healthcloud.services.notice.NoticeService;
import com.wondersgroup.healthcloud.services.user.dto.Session;
import com.wondersgroup.healthcloud.utils.DateFormatter;
import com.wondersgroup.healthcloud.utils.security.H5ServiceSecurityUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaozhenxing on 2016/8/16.
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    private static final Logger log = Logger.getLogger(HomeController.class);

    @Autowired
    private ImageTextService imageTextService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private ManageNewsArticleService manageNewsArticleService;

    @Autowired
    private FaqService faqService;

    @Autowired
    private H5ServiceSecurityUtil h5ServiceSecurityUtil;

    @RequestMapping(value = "/bannerFunctionAds", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity bannerFunctionAds(@RequestHeader(value = "main-area", required = true) String mainArea,
                                                @RequestHeader(value = "spec-area", required = false) String specArea,
                                                @RequestHeader(value = "app-version", required = true) String version,
                                                @AccessToken(required = false, guestEnabled = true) Session session) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();

        // 首页Banner
        ImageText imgTextA = new ImageText();
        imgTextA.setAdcode(ImageTextEnum.HOME_BANNER.getType());
        List<ImageText> imageTextsA = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgTextA);
        if (imageTextsA != null && imageTextsA.size() > 0) {
            int flag = 5;
            if (imageTextsA.size() < flag) {
                flag = imageTextsA.size();
            }
            List banners = new ArrayList();
            for (int i = 0; i < flag; i++) {
                BasicImageTextDTO bit = new BasicImageTextDTO(imageTextsA.get(i));
                banners.add(bit);
            }
            data.put("banners", banners);
        }

        // 首页功能栏
        List<ImageText> imageTextsB = imageTextService.findGImageTextForApp(mainArea, null, ImageTextEnum.G_HOME_FUNCTION.getType(), version);
        if (imageTextsB != null && imageTextsB.size() > 0) {
            List functionIcons = new ArrayList();
            Map map = null;
            for (ImageText imageText : imageTextsB) {
                map = new HashMap();
                map.put("imgUrl", imageText.getImgUrl());
                map.put("hoplink", h5ServiceSecurityUtil.secureUrl(imageText.getHoplink(), session));
                map.put("mainTitle", imageText.getMainTitle());
                map.put("subTitle", imageText.getSubTitle());
                functionIcons.add(map);
            }
            data.put("functionIcons", functionIcons);
        }

        // 首页广告
        ImageText imgTextC = new ImageText();
        imgTextC.setAdcode(ImageTextEnum.HOME_ADVERTISEMENT.getType());
        List<ImageText> imageTextsC = imageTextService.findImageTextByAdcodeForApp(mainArea, null, imgTextC);
        if (imageTextsC != null && imageTextsC.size() > 0) {
            List adImages = new ArrayList();
            int flag = 5;
            if (imageTextsC.size() <= flag) {
                flag = imageTextsC.size();
            }
            for (int i = 0; i < flag; i++) {
                BasicImageTextDTO bit = new BasicImageTextDTO(imageTextsC.get(i));
                adImages.add(bit);
            }
            data.put("advertisements", adImages);
        }
        if (data.size() > 0) {
            result.setData(data);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关数据！");
        }
        return result;
    }

    @RequestMapping(value = "/appTips", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity appTips(@RequestHeader(value = "main-area", required = true) String mainArea,
                                      @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        Notice notice = noticeService.findNoticeByAreaForApp(mainArea, specArea);
        if (notice != null) {
            Map data = new HashMap();
            data.put("content", notice.getContent());
            data.put("hoplink", notice.getHoplink());
            result.setData(data);
        } else {
            result.setCode(1000);
            result.setMsg("未查询到相关配置信息");
        }

        return result;
    }

    @RequestMapping(value = "/newsAndQuestions", method = RequestMethod.GET)
    @VersionRange
    @WithoutToken
    public JsonResponseEntity newsAndQuestions(@RequestHeader(value = "main-area", required = true) String mainArea,
                                               @RequestHeader(value = "spec-area", required = false) String specArea) {
        JsonResponseEntity result = new JsonResponseEntity();
        Map data = new HashMap();
        try {
            List<NewsArticleListAPIEntity> newsArticleList = manageNewsArticleService.findArticleForFirst(mainArea, 0, 10);
            if (newsArticleList != null && newsArticleList.size() > 0) {
                data.put("news", newsArticleList);
            }
        } catch (Exception ex) {
            log.error("HomeController.newsAndQuestions Error --> manageNewsArticleService.findArticleForFirst -->" + ex.getLocalizedMessage());
        }

        try {
            List<Faq> faqList = faqService.findHomeFaqList();
            if (faqList != null) {
                List<FaqDTO> questions = Lists.newArrayList();
                for (Faq faq : faqList) {
                    FaqDTO faqDTO = new FaqDTO(faq);
                    faqDTO.setAskTime(DateFormatter.questionListDateFormat(faq.getAskDate()));
                    //查询回答数
                    int commentCount = faqService.countCommentByQid(faq.getQId());
                    faqDTO.setCommentCount(commentCount);
                    questions.add(faqDTO);
                }
                data.put("questions", questions);
            }
        } catch (Exception ex) {
            log.error("HomeController.newsAndQuestions Error --> faqService.findHomeFaqList -->" + ex.getLocalizedMessage());
        }

        result.setData(data);
        return result;
    }
}
