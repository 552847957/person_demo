目前, api分两种, 经由互联网访问的外部接口, 和只有内部调用的内网接口  
外部接口包括neohealthcloud-user-api和neohealthcloud-doctor-api, 内部接口为了方便, 暂时统一由一个internal-api提供, 但为了以后拆分方便, internal-api的路径需要合理设计, 并且调用方调用不同的服务需要分开配置

例如

文章和活动都属于internal-api, 也都需要由user-api调用, 但是在user-api的配置中, 推荐将他们分开配置

```
article-api=http://localhost:8080/neohealthcloud-internal-api/public/article
activity-api=http://localhost:8080/neohealthcloud-internal-api/public/activity
```

```java
@Value("${article-api}") private String articleApiUrl;
@Value("${activity-api}") private String activityApiUrl;
```

而不是

```
internal-api=http://localhost:8080/neohealthcloud-internal-api
```

```java
@Value("${internal-api}") private String internalApiUrl;
private String artielApiUrl = internalApiUrl+"/public/article";
private String activityApiUrl = internalApiUrl+"/public/activity";
```

url         | feature
------------|------------------------------------------------------
`/admin`    | 管理后台需要调用的接口
`/public`   | 公共服务, 包括文章, 活动, 中医体质辨识, 风险评估等...
`/message`  | 消息服务, 包括推送
`/doctor`   | 医生相关接口, 主要提供给web端调用
`/question` | 轻问诊相关
`/measure`  | 测量相关, 已经分出
