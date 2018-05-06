/**
 * 不要因为走了很远就忘记当初出发的目的:whatever happened,be yourself
 */
package com.xiaoyu.modules.constant;

/**
 * 2017年5月22日下午5:11:53
 * 
 * @author xiaoyu
 * @description 跳转的页面路径
 */
public class JumpPath {

    public static final String MOBILE = "mobile/";
    public static final class UserRelated {
        public static final String EDIT = "user/userForm";
        public static final String DETAIL = "user/userDetail";
        public static final String MESSAGE = "user/messageList";
    }

    public static final class ArticleRelated {
        public static final String EDIT = "article/articleEdit";
        public static final String DETAIL = "article/articleDetail";
        public static final String HOME = "article/home";
        public static final String WRITE = "article/articleForm";
        public static final String COMMENT_LIST = "article/commentList";
        public static final String SEARCH = "article/searchList";
    }

    public static final class CommonRelated {
        public static final String PAGE_404 = "common/404";
        public static final String LOGIN = "common/login";
        public static final String UPLOAD = "common/uploadFile";
    }
}
