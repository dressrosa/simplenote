/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.simplenote.modules.biz.article.vo;

/**
 * @author hongyu
 * @date 2019-02
 * @description
 */
public class ArticleContentVo {

    private String articleId;
    private String content;

    public String getArticleId() {
        return articleId;
    }

    public ArticleContentVo setArticleId(String articleId) {
        this.articleId = articleId;
        return this;
    }

    public String getContent() {
        return content;
    }

    public ArticleContentVo setContent(String content) {
        this.content = content;
        return this;
    }

}
