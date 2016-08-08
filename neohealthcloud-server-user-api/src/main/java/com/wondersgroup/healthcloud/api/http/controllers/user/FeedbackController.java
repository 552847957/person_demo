package com.wondersgroup.healthcloud.api.http.controllers.user;

import com.google.common.io.ByteStreams;
import com.squareup.okhttp.Request;
import com.wondersgroup.common.http.HttpRequestExecutorManager;
import com.wondersgroup.common.http.builder.RequestBuilder;
import com.wondersgroup.common.http.entity.StringResponseWrapper;
import com.wondersgroup.healthcloud.common.http.dto.JsonResponseEntity;
import com.wondersgroup.healthcloud.common.http.support.misc.JsonKeyReader;
import com.wondersgroup.healthcloud.common.http.support.version.VersionRange;
import com.wondersgroup.healthcloud.services.user.FeedbackService;
import com.wondersgroup.healthcloud.utils.easemob.EasemobAccountUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api")
public class FeedbackController {

  @Autowired
  private FeedbackService feedbackService;

  @Autowired
  private EasemobAccountUtil util;

  @Autowired
  private HttpRequestExecutorManager httpRequestExecutorManager;

  /**
   * 意见反馈
   */
  @PostMapping(path = "/user/feedback")
  @VersionRange
  public JsonResponseEntity<String> saveFeedback(@RequestHeader("main_area") String mainArea, @RequestBody String request) {
    JsonResponseEntity<String> response = new JsonResponseEntity<>();
    JsonKeyReader reader = new JsonKeyReader(request);
    String uid = reader.readString("uid", true);
    String comments = reader.readString("comments", false);
    String contact = reader.readString("contact", true);

    feedbackService.saveFeedback(uid, comments, contact, mainArea, "0");
    response.setData("反馈成功");
    return response;
  }

  @GetMapping(path = "/user/test")
  @VersionRange
  public void test(HttpServletResponse response) throws Exception {
    Request request = new RequestBuilder().get().url("http://www.baidu.com").build();
    StringResponseWrapper wrapper =
        (StringResponseWrapper) httpRequestExecutorManager.newCall(request)
            .run()
            .as(StringResponseWrapper.class);
    ByteStreams.copy(wrapper.nativeResponse().body().byteStream(), response.getOutputStream());
  }

  @GetMapping(path = "/user/chinese")
  @VersionRange
  public String testString() {
    return "中文";
  }
}
