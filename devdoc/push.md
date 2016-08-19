## 推送相关

服务端以用户最后登录的app为准, 所有对用户的推送都推到最后登录的app上

### 推送登录表 `app_tb_user_push_info`

列名 | 类型    | 含义
-----|---------|------------------
id   | uuid    |
uid  | uuid    | 用户id
cid  | varchar | 个推clientid
area | varchar | 用户最后登录的app

### 推送标签表 `app_tb_push_tag`

列名       | 类型    | 含义
-----------|---------|-------
id         | 自增int |
tagname    | varchar | 标签名
updatetime | date    |

### 用户标签表 `app_tb_user_push_tag`

列名  | 类型 | 含义
------|------|-----
id    | uuid |
uid   |      |
tagid |      |

### 推送计划列表 `app_tb_push_plan`

列名        | 类型    | 含义
------------|---------|-----
id          | 自增int |
creator     |         |
title       |         |
content     |         |
url         |         |
target_type |         |
target      |         |
create_time |         |
plan_time   |         |
status      |         |


### url规则表

列名        | 类型 | 含义
------------|------|-----
id          |      |
is_user     |      |
is_standard |      |
url         |      |
params      |      |
