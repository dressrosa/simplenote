var url = document.URL;
var articleId = url.split('/')[5];

var $arPromise = $.ajax({
    cache : false,
    type : "get",
    async : true,
    url : '/api/v1/article/' + articleId,
    success : function(data) {
        var obj = jQuery.parseJSON(data);
        if (obj.code == '0') {
            var ar = obj.data;
            if (!checkNull(ar)) {
                $(".note_input").val(ar.title);
                $(".textarea").html(ar.content);
            } else {
                window.location.href = "/common/404";
            }
        } else if (obj.code == '2') {
            window.location.href = "/common/404";
            return false;
        }
        addHeadForImg();
        return true;
    }
});

function modify() {
    var tip = "";
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull(userInfo)) {
        tip = "登录后再来编辑吧";
        $('.tooltip').jBox('Tooltip', {
            content : tip,
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $(".btn")
        }).open();
        return false;
    }
    var content = $("#articleContent");
    var title = $(".note_input");
    console.log("title:" + title.val());
    if (checkNull(title.val())) {
        tip = "标题怎么一个字都不留呀"
        $('.tooltip').jBox('Tooltip', {
            content : tip,
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $(".btn")
        }).open();
        return false;
    }
    if (checkNull(content.val())) {
        tip = "空空荡荡的内容,扎心了"
        $('.tooltip').jBox('Tooltip', {
            content : tip,
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $(".btn")
        }).open();
        return false;
    }
    var userId = userInfo.userId;
    var token = userInfo.token;
    $.ajax({
        type : "post",
        url : "/api/v1/article/edit",
        data : {
            articleId : articleId,
            userId : userId,
            token : token,
            content : content.val(),
            title : title.val()
        },
        async : true,
        error : function(data) {
            tip = "没成功,是不是没网啊"
            $('.tooltip').jBox('Tooltip', {
                content : tip,
                pointer : false,
                animation : 'zoomIn',
                closeOnClick : 'body',
                target : $(".btn")
            }).open();
            return false;
        },
        success : function(data) {
            var jsonObj = jQuery.parseJSON(data);
            if (jsonObj.code == '0') {
                window.location.href = "/article/" + jsonObj.data;
            } else if (jsonObj.code = '20001') {
                $.session.remove('user');
                $('.tooltip').jBox('Tooltip', {
                    content : jsonObj.message,
                    pointer : false,
                    animation : 'zoomIn',
                    closeOnClick : 'body',
                    target : $(".btn")
                }).open();

            } else {
                $('.tooltip').jBox('Tooltip', {
                    content : jsonObj.message,
                    pointer : false,
                    animation : 'zoomIn',
                    closeOnClick : 'body',
                    target : $(".btn")
                }).open();
            }
            return true;
        }
    });
}
$(document).ready(function() {
    $("#publish").bind("click", function() {
        modify();
    });

});