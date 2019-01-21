简约风格网站  
(划重点:暂时没重点)
---
### 简介:
札记
##### 技术要点:  
1. RPC:[beacon](https://github.com/dressrosa/beacon)
2. 登录及权限操作redis+session处理
3. ~~搜索文章基于elasticsearch~~(内存不够了,暂停使用☻)
4. 评论和关注等消息基于activemq:[celery](https://github.com/dressrosa/celery)
5. 数据结果处理:[toolbox](https://github.com/dressrosa/toolbox)

##### 开发:
1. master采用的是rpc的微服务架构.  
2. 正常的架构请查看dev分支,适合学习整体的项目,dev不再作为主要开发分支了.  
3. dev_modules分支采用rpc架构,html在simplenote-web里面,适合学习spring如何搭建html.dev_modules不再作为开发分支.  
4. 已dev_1.1为主,专注服务端接口.  
5. 前端页面见:[simplenote-html](https://github.com/dressrosa/simplenote-html)  
##### 目标:
1. 做一个发日志,问答的小网站
2. 可以基于Google的material design
