var $lock = true;
var getList = function(pageNum, pageSize) {
    var $ajaxPromise = $.ajax({
        type : "get",
        // async : true,
        url : '/api/v1/home?' + new Date().getTime(),
        beforeSend : function(xhr) {
            var $userInfo = jQuery.parseJSON($.session.get("user"));
            xhr.setRequestHeader('pageNum', pageNum);
            xhr.setRequestHeader('pageSize', pageSize);
            if (!checkNull($userInfo)) {
                xhr.setRequestHeader('token', $userInfo.token);
                xhr.setRequestHeader('userId', $userInfo.userId);
            }
        },
        success : function(data) {
            handleData(data);
            $lock = false;
            return true;
        },
        error : function(data) {
            return false;
        }
    });
    $ajaxPromise.promise().done(function() {
        if (!isPC()) {
            $(".hot").css("display", "block");
            $(".hot").css("width", "100%");
            $("#footer").css("display", "none");
        }
        $(".item_list").find("img").hover(function() {
            var $avatar = $(this);
            var $info = $.session.get('$u_' + $avatar.attr("id"));

            if (!checkNull($info) && $info != 'null') {
                var $uinfo = jQuery.parseJSON($info);
                fillInfo($avatar, JSON.stringify($uinfo));
                return true;
            }
            $.ajax({
                type : "get",
                async : true,
                url : '/api/v1/user/' + $avatar.attr("id"),
                success : function(data) {
                    var obj = jQuery.parseJSON(data);
                    if (obj.code == 0) {
                        $.session.set('$u_' + obj.data.userId, data, 30 * 60);
                        fillInfo($avatar, data);
                    }
                    return true;
                }
            });

        }, function() {
            var $userCard = $(".panel_card_wrapper_smaller");
            $userCard.find(".panel_card").css("background", 'rgba(215, 215, 215, 0.5)');
            $userCard.find(".panel_card").css("background-size", 'contain');
            $userCard.css({
                "display" : "none"
            });
        });
    });
};

// handle the data
var handleData = function(data) {
    var obj = jQuery.parseJSON(data);
    if (obj.code != '0') {
        return false;
    }
    if (checkNull(obj.data)) {
        return false;
    }
    var arHtml = '<div class="item_list" >';
    $.each(obj.data, function(index2, ar) {
        var childHtml = '<dl>';

        childHtml += '<div class="list_item">';
        childHtml += '<span class="item_userinfo" ><img class="avatar small" img-type="avatar" src="' + imgHead + ar.user.avatar + '" id="'
                + ar.user.userId + '" />';
        childHtml += '<p class="item_desc">' + ar.user.signature + '</p>';
        childHtml += '</span>';
        childHtml += '<dt class="item_username">' + ar.user.nickname + '</dt>';
        childHtml += '<div class="item_ar" id="' + ar.articleId + '">';
        childHtml += '<dt class="item_ar_title">' + ar.title + '</dt>';
        childHtml += '<p class="item_ar_content">' + ar.content + '</p>';
        childHtml += ' </div>';

        childHtml += '<div class="comment_bar"><div class="bar_part">';
        if (ar.isLike == "1") {
            childHtml += '<i class="icon_like" style="color:#fd4d4d;" data-like="1"></i>';
        } else {
            childHtml += '<i class="icon_like" data-like="0"></i>';
        }

        childHtml += '<label style="margin: 2px;">' + ar.attr.likeNum + '</label></div>';
        childHtml += '<div class="bar_part"><i class="icon_comment_alt"></i><label style="margin: 2px;">' + ar.attr.commentNum + '</label></div>';
        childHtml += '<div class="bar_part">';
        if (ar.isCollect == "1") {
            childHtml += '<i class="icon_heart_alt" style="color:#fd4d4d;" data-heart="1"></i>';
        } else {
            childHtml += '<i class="icon_heart_alt" data-heart="0"></i>';
        }
        childHtml += '<label style="margin: 2px;">' + ar.attr.collectNum + '</label></div>';
        childHtml += '</div>';
        childHtml += '</li>';
        childHtml += ' </div>';
        childHtml += '</dl>';
        arHtml += childHtml;
    });
    arHtml += '</div>';
    $(".list_hot").append(arHtml);

    $(".icon_heart_alt").on("click", function() {
        var $userInfo = jQuery.parseJSON($.session.get("user"));
        if (checkNull($userInfo)) {
            showTip('您未登录或登录失效,请重新登录');
            return false;
        }

        var $icon = $(this);
        var elem = $icon.parent().parent().parent();
        var $ar = elem.find(".item_ar");
        var $next = $icon.next();
        var num = $next.html();
        var $isCollect;
        if ($icon.attr('data-heart') == '0') {
            $icon.css("color", "#fd4d4d");
            $icon.attr("data-heart", "1");
            $next.html(num - (-1));
            $isCollect = 0;
        } else if ($icon.attr('data-heart') == '1') {
            $icon.css("color", "#a7a7a7");
            $icon.attr("data-heart", "0");
            $next.html(num - 1);
            $isCollect = 1;
        }
        $.ajax({
            type : "post",
            async : true,
            url : '/api/v1/article/collect',
            data : {
                articleId : $ar.attr("id"),
                isCollect : $isCollect
            },
            beforeSend : function(xhr) {
                if (!checkNull($userInfo)) {
                    xhr.setRequestHeader('token', $userInfo.token);
                    xhr.setRequestHeader('userId', $userInfo.userId);
                }

            },
            success : function(data) {
                var obj = jQuery.parseJSON(data);
                if (obj.code == "20001") {
                    showTip('您未登录或登录失效,请重新登录');
                    return false;
                }
                return true;
            },
            error : function(data) {
                return false;
            }
        });
    });
    $(".icon_comment_alt").on("click", function() {
        var $icon = $(this);
        var elem = $icon.parent().parent().parent();
        var $ar = elem.find(".item_ar");
        window.location.href = "/article/" + $ar.attr("id") + "/comments"
    });
    $(".icon_like").on("click", function() {
        var $icon = $(this);
        var elem = $icon.parent().parent().parent();
        var $ar = elem.find(".item_ar");
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
            url : '/api/v1/article/like',
            data : {
                articleId : $ar.attr("id"),
                isLike : $isLike
            },
            beforeSend : function(xhr) {
                var userInfo = jQuery.parseJSON($.session.get("user"));
                if (!checkNull(userInfo)) {
                    xhr.setRequestHeader('token', userInfo.token);
                    xhr.setRequestHeader('userId', userInfo.userId);
                }
            },
            success : function(data) {
                var obj = jQuery.parseJSON(data);
                if (obj.code == "20001") {
                    console.log("未登录");
                }
                return true;
            },
            error : function(data) {
                return false;
            }
        });
    });
    $('.item_ar').bind('click', function() {
        var $elem = $(this);
        window.location.href = "/article/" + $elem.attr('id');
    });
    $('.avatar').bind('click', function() {
        var $elem = $(this);
        window.location.href = "/user/" + $elem.attr('id');
    });
    return true;

};

var userInfo = jQuery.parseJSON($.session.get('user'));
var fillInfo = function(item1, item2) {
    var $avatar = item1;
    var $user = jQuery.parseJSON(item2).data;
    var $userCard = $(".panel_card_wrapper_smaller");
    $userCard.find(".panel_card").css("background", 'url(' + imgHead + $user.avatar + ')');
    $userCard.find(".panel_card").css("background-size", 'contain');
    $userCard.find(".card_u").find("label")[0].innerHTML = $user.nickname;
    $userCard.find(".card_u").find("label")[1].innerHTML = $user.signature;
    $userCard.find("#ar_number").html($user.attr.articleNum);
    $userCard.find("#fo_number").html($user.attr.followerNum);
    var $top = $avatar.parent().parent().position().top;
    $userCard.css({
        "position" : "absolute",
        "top" : $top - 190,
        "left" : "65px",
        "display" : "initial"
    });
}
$(document).ready(function() {
    var $pageNum = 1;
    getList($pageNum, 12);
    if (checkNull(userInfo)) {
        $("#loginSpan").css("display", "block");
    } else {
        $("#userSpan").css("display", "block");
        $("#userSpan").find("#nickname").attr("href", "/user/" + userInfo.userId);
        $("#userSpan").find("#nickname").text(userInfo.nickname);
    }
    // 搜索框隐藏
    $(window).scroll(function() {
        var $top = document.body.scrollTop;
        // $(".top_n1_banner").css("background-position", "center " + $top);
        // console.log("top:" + $top);
        if ($top > 300) {
            $(".top_n1_info").css("opacity", 1);
            $(".header").find(".search_span1").css("display", "initial");
            $(".header").find(".user_dropdown").css("background", "#d64444");
            $(".header").removeClass("transparent_header");
            $(".top_n1").find(".search_span").css("display", "none");
        } else {
            $(".top_n1_info").css("opacity", 1 - $top / 300.0);
            $(".header").find(".search_span1").css("display", "none");
            $(".top_n1").find(".search_span").css("display", "table");
            $(".header").addClass("transparent_header");
            $(".header").find(".user_dropdown").css("background", "rgba(214, 68, 68, 0)");
        }

    });

    $(".icon_search").on('click', function() {
        var $search = $(this);
        var word = $search.parent().find("input").val();
        if (!checkNull(word))
            window.location.href = "/search?keyword=" + word;
    });
    $(".input_search").keyup(function(e) {
        var curKey = e.which;
        if (curKey == 13)
            $(".icon_search").click();
    });
    $(".bigger").keyup(function(e) {
        var curKey = e.which;
        if (curKey == 13)
            $(".icon_search").click();
    });

    // 滚动事件触发
    window.onscroll = function() {
        // console.log("可视高度:" + getClientHeight() + " 滚动位置:" + getScrollTop() +
        // " doc高度:" + getScrollHeight() + " 相加:"
        // + (getScrollTop() + getClientHeight()) + "位置:" +
        // $(".header").offset().top)

        if (getScrollTop() + getClientHeight() == getScrollHeight()) {
            if (!$lock) {
                $lock = true;
                $(".loading").css("visibility", "visible");
                setTimeout(function() {
                    getList(++$pageNum, 12);
                    $(".loading").css("visibility", "hidden");
                }, 50);
            }
        }
    }
});