简约风格的写日志的网站  
(划重点:由于买的阿里云服务器灰常小,几乎所有东西都安在里面,所以访问接口速度有点慢,见谅)
---
##### 简介:
基于springboot+html+jquery  
主要适合pc端,谷歌浏览器(由于本人不是前端,所以兼容性等细节以后慢慢考虑中)
目前实现的功能:  
0.登录注册 1.发日志.2收藏评价点赞.3关注他人  
##### 技术要点:  
1. 基础结构封装参考我的另一个项目[bamboo](https://github.com/dressrosa/bamboo)
2. 基于json+ajax对数据进行处理
3. 登录及权限操作redis+session处理
4. 搜索文章基于elasticsearch
5. 评论和关注等消息基于activemq,封装是用我的另一个项目[celery](https://github.com/dressrosa/celery)

##### 目标:
1. 做一个基本都有的小网站
2. 可以基于Google的material design
