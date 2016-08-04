标准版区域版相关
========

1. 标准版可以提供区域选择, 但是只能使用标准版的功能, 所有标准版功能在区域版中也能提供, 且额外提供区域特色功能
2. 标准版三方服务只有推送, 无三方登录, 环信聊天等
3. 标准版和区域版相同账户存在互踢, 推送服务会推送到用户最后登录的客户端上
4. 标准版大多数功能都需要对区域代码进行识别, 选择不同的区域返回不同的数据

### 设计

客户端所有的请求, request-header都需要附带`main-area`表示当前使用的app版本, 附带`spec-area`表示用户选中的特定区域, 其中, `spec-area`必须是`main-area`的子集, 都使用国标区域代码, 只包含两级

标准版`main-area`值为`""`空字符串, 上海健康云使用`3101`, 广州健康通使用`4401`, 可选区域由后台配置, 若选择的代码服务端不支持, 则默认使用`main-area`

由于国标编码的规则, 在查询时, 若业务会细化到更小的范围(比如, 只发送给长宁的文章), 则需要按特定区域查询

### 例子

以文章为例

id | title | main_area | spec_area
---|-------|-----------|----------
1  | 标题1 |           | null
2  | 标题2 | 3101      | null
3  | 标题3 | 3101      | 310105

API查询时:

标准版: `select * from article_tb where main_area=''` 返回1  
上海版(未选区域): `select * from article_tb where (main_area='' or main_area = '3101') and spec_area is null` 返回1,2  
上海版(长宁): `select * from article_tb where (main_area='' or main_area = '3101') and spec_area = '310105'` 返回1,2,3  
上海版(静安): `select * from article_tb where (main_area='' or main_area = '3101') and spec_area = '310106'` 返回1,2  
广州版(未选区域): `select * from article_tb where (main_area='' or main_area = '4401') and spec_area is null` 返回1 

管理后台编辑时:

admin权限的可以插入`main_area=NULL`的文章  
`3101`权限的可以插入`main_area='3101' and spec_area=NULL`的文章  
`310105`权限的可以插入`main_area='3101' and spec_area='310105'`的文章  