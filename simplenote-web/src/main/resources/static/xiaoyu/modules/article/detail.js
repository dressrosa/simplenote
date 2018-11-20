var url = document.URL;
var articleId = url.split('/')[4];

var before = function(xhr) {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (!checkNull(userInfo)) {
        xhr.setRequestHeader('token', userInfo.token);
        xhr.setRequestHeader('userId', userInfo.userId);
    }
};
var $arPromise = $.ajax({
    cache : false,
    type : "get",
    async : true,
    url : '/api/v1/article/' + articleId,
    success : function(data) {
        var obj = jQuery.parseJSON(data);
        if (obj.code != '0') {
            window.location.href = "/common/404";
            return false;
        }
        var ar = obj.data;
        if (checkNull(ar)) {
            window.location.href = "/common/404";
            return false;
        }
        setTitle('详情-' + ar.title);
        $("#titleSpan").html(ar.title);
        var $partUp = $(".part_up");
        $partUp.find("img").attr('src', imgHead+ar.user.avatar);
        $partUp.find("img").attr('id', ar.user.userId);
        $partUp.find("img").on("click", function() {
            window.location.href = "/user/" + ar.user.userId;
        });
        $partUp.find(".p_description").find("span").html(ar.user.description);
        $partUp.find(".p_username").find(".nickname").html(ar.user.nickname);

        $partUp.find(".red").find("label").html(ar.attr.likeNum);
        $partUp.find(".blue").find("label").html(ar.attr.collectNum);
        $partUp.find(".green").find("label").html(ar.attr.commentNum);
        var $co = $partUp.find(".p_comment");
        $co.find("label").html('<a>' + ar.user.nickname + ':' + '</a>');
        $co.find("p").html(ar.user.description);
        $("#co_title").html('半点故事');
        var $partDown = $(".part_down");
        $partDown.attr("id", ar.articleId);
        $partDown.find(".ar_date").html(ar.createDate);
        $partDown.find(".ar_title").find("h2").html(ar.title);
        $partDown.find(".ar_time").find("label").html(ar.createTime);
        $partDown.find(".ar_view").html('浏览量:' + ar.attr.readNum);
        $partDown.find(".ar_content").attr("id", ar.articleId);
        var converter = new showdown.Converter();
        $partDown.find(".ar_content").html(converter.makeHtml(ar.content));
        $partDown.find(".ar_content").find("img").each(function(i) {
            var _this = $(this);
            _this.attr("width", "100%");
            var src = _this.attr("src");
            _this.bind("click",function(){
                var t = $("#bigImgdiv");
                if (t.attr("src") == undefined) {
                   $( '<div id="bigImgdiv" style="text-align: center;position: fixed;z-index: 1000;top: 0;left: 0;'
                           +'-webkit-user-drag: none;-moz-user-drag: none;-ms-user-drag: none;user-drag: none;' 
                            + 'width: 100%;height: 100%;background-color: rgba(255,255,255,0.9);display:none;">'
                            + '<img id="bigimg" style="height: 90%;width: 90%;border: 0;'
                            + 'margin: auto;position: absolute;top: 0;bottom: 0;left: 0;right: 0;" src="' + src + '" /></div>')
                  .appendTo("body");
                   //new RTP.PinchZoom($("#bigimg"), {});
                }
                t = $("#bigImgdiv");
                $("#bigimg").attr("src", src);
                t.attr("display", "block");
                t.fadeIn("fast");
                $("#bigImgdiv").click(function() {
                    $(this).attr("display", "none");
                    $(this).fadeOut("fast");
                });
            });
        });
        $('pre code').each(function(i, block) {
            hljs.highlightBlock(block);
        });
        return true;
    }
});
var $coPromise = $
        .ajax({
            cache : false,
            type : "get",
            async : true,
            url : '/api/v1/article/' + articleId + "/new-comments",
            beforeSend : function(xhr) {
                var $userInfo = jQuery.parseJSON($.session.get("user"));
                if (!checkNull($userInfo)) {
                    xhr.setRequestHeader('token', $userInfo.token);
                    xhr.setRequestHeader('userId', $userInfo.userId);
                }

            },
            success : function(data) {
                var obj = jQuery.parseJSON(data);
                if (obj.code != '0') {
                    return false;
                }
                var $coList = obj.data;
                if (checkNull($coList)) {
                    return false;
                }
                var $coComment = $(".co_comment");
                var $html = '<div class="co_num"><span>最新评论</span></div>';
                $html += '<div class="co_list">';
                $
                        .each(
                                $coList,
                                function(index, co) {
                                    $html += '<div class="co_item" data-id="'
                                            + co.commentId
                                            //style="margin-top: -30px; margin-left: -10px;"
                                            + '"><div class="item_up"><div >	<img img-type="avatar " class="avatar tiny" src="'
                                            +imgHead+ co.replyerAvatar + '"></div><div class="item_p"><label class="item_p_username"><a>' + co.replyerName
                                            + '</a></label>';
                                    if (!checkNull(co.parentReplyerName)) {
                                        $html += '<label class="item_p_label">回复</label> <label class="item_p_username">' + co.parentReplyerName
                                                + '</label>';
                                    }

                                    $html += '<p>' + co.content + '</p></div>';
                                    if (co.isLike == "1") {
                                        $html += '<div class="item_like"><i class="icon_like" style="color:#fd4d4d;"  data-like="1"></i><label class="co_item_label">'
                                                + co.num + '</label></div>';
                                    } else {
                                        $html += '<div class="item_like"><i class="icon_like"  data-like="0"></i><label class="co_item_label">'
                                                + co.num + '</label></div>';
                                    }
                                    $html += '</div>';
                                    $html += '<div class="item_down"><label class="item_p_title_pure">' + co.createDate + '</label></div></div>';
                                });
                $html += '</div>';
                $html += '<div class="co_all"><span><a href="/article/' + articleId + '/comments">查看全部</a></span></div>';
                $coComment.html($html);

                $(".co_comment .icon_like").on("click", function() {
                    var $icon = $(this);
                    var elem = $icon.parent().parent().parent();
                    var $next = $icon.next();
                    var num = $next.html();
                    var $isLike;
                    if ($icon.attr('data-like') == '0') {
                        $icon.css("color", "#fd4d4d");
                        $icon.attr("data-like", "1");
                        $next.html(num - (-1));
                        $isLike = 0;
                    } else if ($icon.attr('data-like') == '1') {
                        $icon.css("color", "#a7a7a7");
                        $icon.attr("data-like", "0");
                        $next.html(num - 1);
                        $isLike = 1;
                    }
                    $.ajax({
                        type : "post",
                        async : true,
                        url : '/api/v1/article/comments/like',
                        data : {
                            commentId : elem.attr("data-id"),
                            isLike : $isLike
                        },
                        beforeSend : function(xhr) {
                            var $userInfo = jQuery.parseJSON($.session.get("user"));
                            if (!checkNull($userInfo)) {
                                xhr.setRequestHeader('token', $userInfo.token);
                                xhr.setRequestHeader('userId', $userInfo.userId);
                            }
                        },
                        success : function(data) {
                            var obj = jQuery.parseJSON(data);
                            if (obj.code == "20001") {
                                console.log("未登录");
                                $.session.remove("user");
                            }
                            return true;
                        },
                        error : function(data) {
                            return false;
                        }
                    });
                });
                return true;
            }
});

var comment = function() {
    var $userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull($userInfo)) {
        window.location.href = "/login";
        return false;
    }
    var $text = $("textarea[name=co_tt]").val();
    
    if (checkNull($text)) {
        new jBox('Tooltip', {
            content : '评论不能空',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $(".co_tt")
        }).open();
        return false;
    }
    if ($text.length > 100) {
        new jBox('Tooltip', {
            content : '评论字数仅限100以内',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $(".co_tt")
        }).open();
        return false;
    }
    $(".co_btn").attr("disabled", "disabled");
    $.ajax({
        type : 'post',
        async : true,
        url : '/api/v1/article/' + $(".part_down").attr("id") + "/comment",
        data : {
            content : $text
        },
        beforeSend : function(xhr) {
            if (!checkNull($userInfo)) {
                xhr.setRequestHeader('token', $userInfo.token);
                xhr.setRequestHeader('userId', $userInfo.userId);
            }
        },
        error : function(data) {
            $(".co_btn").removeAttr("disabled");
            return false;
        },
        success : function(data) {
            var jsonObj = jQuery.parseJSON(data);
            if (jsonObj.code == '0') {
                new jBox('Tooltip', {
                    content : '评论成功',
                    pointer : false,
                    animation : 'zoomIn',
                    closeOnClick : 'body',
                    target : $(".co_tt")
                }).open();
                $(".co_tt").val("");
                var $coItem = '<div class="co_item"><div class="item_up">' + '<div style="margin-top: -30px; margin-left: -10px;">'
                        + '	<img img-type="avatar " class="avatar small" src="' + imgHead + jsonObj.data.replyerAvatar + '">' + '</div>'
                        + '<div class="item_p">' + '<label class="item_p_username"><a>' + jsonObj.data.replyerName + '</a></label>' + '<p>'
                        + jsonObj.data.content + '</p>' + '</div><div class="item_like"><i class="icon_like"></i>'
                        + '<label class="co_item_label">0</label>' + '</div></div>' + '<div class="item_down">' + '<label class="item_p_title_pure">'
                        + jsonObj.data.createDate + '</label>' + '</div></div>';
                $(".co_list").prepend($coItem);
            } else if (jsonObj.code == '20001') {
                showTip('请先登录');
                $.session.remove("user");
            }
            $(".co_btn").removeAttr("disabled");
            return true;
        }
    });

};
var isFollow = function() {
    // is loved
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull(userInfo)) {
        return;
    }
    $.ajax({
        cache : false,
        type : "post",
        async : true,
        url : '/api/v1/user/is-followed',
        data : {
            userId : userInfo.userId,
            followTo : $(".avatar").attr('id')
        },
        beforeSend : function(xhr) {
            return before(xhr);
        },
        success : function(data) {
            var obj = jQuery.parseJSON(data);
            if (obj.code == 0 || checkNull(obj.data)) {
                return false;
            }
            if (obj.data.isFollow == '1') {
                $(".p_love").text("取消关注");
                $(".p_love").css({
                    "background" : "#e2e2e2"
                });
                $(".p_love").attr("data-love", '1');
            }

        }

    });

};

var follow = function() {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    var $id = '';
    if (!checkNull(userInfo)) {
        $id = userInfo.userId;
    }
    var url = '/api/v1/user/follow';
    if ($(".p_love").attr("data-love") == '1') {
        url = '/api/v1/user/unfollow';
    } else {
        url = '/api/v1/user/follow';
    }
    $.ajax({
        cache : false,
        type : "post",
        async : true,
        url : url,
        data : {
            userId : $id,
            followTo : $(".avatar").attr("id")
        },
        beforeSend : function(xhr) {
            return before(xhr);

        },
        success : function(data) {
            console.log(data);
            var obj = jQuery.parseJSON(data);
            if (obj.code == 0) {
                if (obj.data.isFollow == '1') {
                    $(".p_love").text("取消关注");
                    $(".p_love").css({
                        "background" : "#e2e2e2"
                    });
                    $(".p_love").attr("data-love", '1');
                    showTip("关注成功");
                } else if (obj.data.isFollow == '0') {
                    $(".p_love").text("关注");
                    $(".p_love").css({
                        "background" : "#fff9f9"
                    });
                    $(".p_love").attr("data-love", '0');
                    showTip("取消成功");
                }
            } else if (obj.code = '20001') {
                showTip(obj.message);
            }
        }
    });
};
$(document).ready(function() {
    $("#login").bind("click", function() {
        gotoLogin('/article/' + articleId);
    });
    $arPromise.promise().done(function() {
        isFollow();
        var item = $(".ar_content").attr("id");
        $.ajax({
            type : 'POST',
            async : true,
            url : '/api/v1/article/views/' + item,
            error : function(data) {
                console.log(data);
                return false;
            },
            success : function(data) {
                console.log(data);
                return true;
            }
        });
        // comment
        $(".part_comment").bind("click", function() {
            // window.location.href =
            // "/article/comments";
        });

        $arContent = $(".ar_content");

        console.log($arContent.css("height") + "," + $arContent.css("max-height"))
        if ($arContent.css("height") >= $arContent.css("max-height")) {
            $arContent.find(".ar_more").css("display", "block");

        }
        if (!isPC()) {
            $(".main").css("display", "block");
            $(".siderbar_left").css("display", "flex");
            var $shadow = $(".img_shadow");
            var img = $(".img_shadow").find("img")
            $shadow.find("img").css({
                "border-radius" : "50%",
                "width" : "100px",
                "height" : "100px"
            });
        }
    });
    //TODO 
    var win_h = $(window).height();//关键代码
    window.addEventListener('resize', function () {
        if($(window).height() < win_h){
            $(".part_comment").css("position","fixed");
        }else{
            $(".part_comment").css("position","fixed");
        }
    });
    
    $coPromise.promise().done(function() {
    });

    $(".co_btn").on('click', function() {
        comment();
    });

    $(".p_love").on('click', function() {
        follow();
    });
    // 标题呈现在header
    $(window).scroll(function() {
        var $top = document.body.scrollTop;
        // console.log("top:" + $top);
        if ($top > 305) {
            $("#titleSpan").css("visibility", "visible");
        } else {
            $("#titleSpan").css("visibility", "hidden");
        }

    });
});