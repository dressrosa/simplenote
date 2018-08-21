简约风格的写日志的网站  
(划重点:由于买的阿里云服务器灰常小,几乎所有东西都安在里面,所以访问接口速度可能有点慢,见谅)
---
### 简介:
基于springboot+html+jquery  
主要适合pc端,谷歌浏览器(由于本人不是前端,所以兼容性等细节以后慢慢考虑中)
目前实现的功能:  
1.登录注册
2.发日志
3.收藏评价点赞关注
4....  
##### 技术要点:  
1. ~~基础结构:[bamboo](https://github.com/dressrosa/bamboo)~~
2. 基于json+ajax对数据进行处理
3. 登录及权限操作redis+session处理
4. 搜索文章基于elasticsearch
5. 评论和关注等消息基于activemq:[celery](https://github.com/dressrosa/celery)
6. 数据结果处理:[toolbox](https://github.com/dressrosa/toolbox)
7. rpc:[beacon](https://github.com/dressrosa/beacon)

##### 目标:
1. 做一个发日志,问答的小网站
2. 可以基于Google的material design