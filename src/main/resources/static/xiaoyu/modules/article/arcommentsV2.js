var $lock = true;
var url = document.URL;
var articleId = url.split('/')[4];

var getList = function(pageNum, pageSize) {
    var $coPromise = $.ajax({
        cache : false,
        type : "get",
        async : true,
        url : '/api/v1/article/' + articleId + "/comments",
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
        }
    });
    $coPromise.promise().done(function() {
        addHeadForImg();
    });
};
var handleData = function(data) {
    var obj = jQuery.parseJSON(data);
    if (obj.code != '0') {
        return;
    }
    var $coList = obj.data;
    if (checkNull($coList)) {
        return;
    }
    var $coComment = $(".co_list");
    $("#co_comment_num").html("("+obj.count+")");
    var $html="";
    $.each($coList, function(index, co) {
        if (co.authorId != co.replyerId) {
            $html += '<div class="left_comment">' + '<div class="photo">' + ' <img class="circle" src="' + co.replyerAvatar + '" />' + '</div>'
                    + '<div class="item">' + '<div class="msg">' + '<span>' + co.content + '</span>' + '</div>' + '<div class="left_triangle"></div>'
                    + ' </div>' + '<div style="clear: both"></div>' + '</div>';
        } else {
            $html += '<div class="right_comment">' + '<div class="photo" style="float: right;">' + ' <img class="circle" src="' + co.replyerAvatar
                    + '" />' + '</div>' + '<div class="item" style="float: right;">' + '<div class="msg">' + '<span>' + co.content + '</span>'
                    + '</div>' + '<div class="right_triangle"></div>' + ' </div>' + '<div style="clear: both"></div>' + '</div>';
        }

    });
    $coComment.append($html);

    $(".icon_like").on("click", function() {
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

$(document).ready(function() {
    getList(1, 12);
    $("#login").bind("click", function() {
        gotoLogin('/article/' + articleId + "/comments");
    });
    // 滚动事件触发
    var $pageNum = 2;
    window.onscroll = function() {
        if (getScrollTop() + getClientHeight() == getScrollHeight()) {
            if (!$lock) {
                $lock = true;
                $(".loading").css("visibility", "visible");
                setTimeout(function() {
                    getList($pageNum++, 12);
                    $(".loading").css("visibility", "hidden");
                }, 50);
            }
        }
    }
});