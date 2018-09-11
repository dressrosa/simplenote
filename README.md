简约风格的写日志的网站  
(划重点:由于买的阿里云服务器灰常小,几乎所有东西都安在里面,所以访问接口速度如果慢,见谅)
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
1. rpc架构:[beacon](https://github.com/dressrosa/beacon)
2. 基于json+ajax对数据进行处理
3. 登录及权限操作redis+session处理
4. ~~搜索文章基于elasticsearch~~(内存不够了,暂停使用☻)
5. 评论和关注等消息基于activemq:[celery](https://github.com/dressrosa/celery)
6. 数据结果处理:[toolbox](https://github.com/dressrosa/toolbox)

##### 开发:
1. master采用的是rpc的微服务架构,正常的架构请查看dev分支,适合学习整体的项目,dev将不作为主要开发分支了.
2.

##### 目标:
1. 做一个发日志,问答的小网站
2. 可以基于Google的material design