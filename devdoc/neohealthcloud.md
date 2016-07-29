## 新健康云服务端开发规约

### db及dao
db使用mysql, `JPA`做orm, 建表规则参考已有的表, 由于历史原因, 一般表以`app_tb_`开头, 已`id`为主键名, 使用`UUID`, 建议加上`del_flag`,`source_id`,`create_date`,`create_by`,`update_date`,`update_by`字段
数据库不建立外键约束, 由程序管理关联, 是使用外键字符串还是使用`@ManyToOne`等`JPA`关联注解, 根据业务自行决定

开发流程为:
1. 理解业务
2. 建表
3. 建立实体类
4. 实现查询语句

### service层
无特殊要求, 异常处理可以抛出`Class<? extends BaseException`, 会在上层捕捉到并返回相应的错误`json`  
日志地址在`/usr/local/dir_samba/HC_logs`下, 非继承`BaseException`的错误, 会将错误堆栈打印在ex目录下的日志文件中, 可以查询相应的错误信息, 以及请求参数

### API
**`GET`**, **`DELETE`**方法采用key-value形式，(网址+path+参数), 不使用~~PathVariable~~

```bash
curl http://wondersgroup.com/user/coupon/getOrderCoupons?total_price=234&item_id=106
```

**`POST`**方法使用`request body`传递参数, 参数以`json`形式传递  

对于图片, 客户端会将图片直接上传至七牛, 再将存储的url传给服务端, 所以API接口不使用`multipart-form`, 若一定服务端一定需要图片的数据, 则可以从七牛上再下载下来

可以使用`JsonKeyReader`帮助解析json请求

```java
@RequestMapping(value = "/user/feedback", method = RequestMethod.POST)
@VersionRange
public JsonResponseEntity<String> saveFeedback(@RequestBody String request) {
    JsonResponseEntity<String> response = new JsonResponseEntity<>();
    JsonKeyReader reader = new JsonKeyReader(request);
    String uid = reader.readString("uid", true);
    String comments = reader.readString("comments", false);
    String contact = reader.readString("contact", true);

    feedbackService.saveFeedback(uid, comments, contact, "0");
    response.setData("反馈成功");
    return response;
}
```

#### response规则
* `code`类型为`integer`
* `code`为`0`则表示请求成功
* 若某字段为`null`则不返回该字段
* `data`必为一个对象, 不是数组

```json
{
	"code": 0,
	"data": {
		"list_name": "运动衫",
		"list_id": 123
	}
}
```

* `msg`只在需要客户端显示时存在, 即返回结果存在`msg`字段, 手机显示`msg`内容:

```json
{
	"code": 1024,
	"msg": "显示内容"
}
```

* 若返回的结果是列表, 则`data`结构如下
* `more`字段表示是否存在后续分页, 若`more`为`true`, 则返回该次查询最后的标记
* `more_params`中的参数表示分页请求的标记, 使手机端请求下一页时不会出现数据交叉
* 分页接口, 服务端接口会有两个参数`order`与`flag`用于接收前一页请求传给手机端的`more_params`字段中的数据, 若`more_params`为`null`则无需回传给服务端
* `extras`字段, 用于添加各个接口的业务数据, 里面的字段由各个接口自行定义

```bash
http://de.wdjky.me/healthcloud/list_request?param=1&....&order=time_desc&flag=1234567789
```

```json
{
	"code": 0,
	"data": {
		"more":true,
		"more_params": {
			"order":"time_desc",
			"flag":"1427874958"
		},
		"extras": {
			"key1":"value1",
			"key2":"value2"
		},
		"content": [
			{
				"list_name": "运动衫",
				"list_id": 123
			}
		]
	}
}
```

#### tricks
`@AccessToken`  

Session是几乎所有接口都需要用到的东西, 可以在Controller方法的参数中增加`@AccessToken Session session`, 就可以得到session对象

`@VersionRange`

接口需要指定版本, 默认值为当前服务端支持的最小版本到最大版本, 若需指定版本, 可以这样写`@VersionRange(from = "3.0", to = "3.1")`, 若方法没有标明该注解, 服务将无法启动
