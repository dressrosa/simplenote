var url = document.URL;
var userInfo = jQuery.parseJSON($.session.get("user"));
var before = function(xhr) {
    if (!checkNull(userInfo)) {
        xhr.setRequestHeader('token', userInfo.token);
        xhr.setRequestHeader('userId', userInfo.userId);
    }
};
var $ajaxPromise1 = $.ajax({
    type : "get",
    async : true,
    url : '/api/v1/user/' + userInfo.userId,
    beforeSend : function(xhr) {
        before(xhr);
    },
    success : function(data) {
        var obj = jQuery.parseJSON(data);
        if (obj.code != "0") {
            return false;
        }
        var $user = obj.data;
        if (checkNull($user)) {
            return false;
        }
        setTitle($user.nickname + '-编辑资料');
        var $userPanel = $(".panel-default");
        if (checkNull($user.avatar)) {
            $user.avatar = imgHead + 'common/avatar.png';
        }
        if (checkNull($user.background)) {
            $user.background = 'common/4.jpg';
        }
        $userPanel.find("img").attr("src",imgHead+ $user.avatar);
        $userPanel.find("img").attr("id", $user.userId);
        // $userPanel.find(".nickname_panel").html($user.nickname);
        // $userPanel.find(".des_panel").html($user.description);

        $("#item_name").find(".info_input").val($user.nickname);
        $("#item_sign").find(".info_input").val($user.signature);
        $("#item_desc").find(".info_input").val($user.description);
        $(".panel").css("background", 'url(' + imgHead + $user.background + ') no-repeat 0% 0%/contain');
        if(isPC()) {
            $(".panel_card").css("background", 'url(' + imgHead + $user.avatar + ')');
            $(".panel_card").css("background-size", 'contain');
            $(".card_u").find("label")[0].innerHTML = $user.nickname;
            $(".card_u").find("label")[1].innerHTML = $user.signature;
            $(".card_down").find("#ar_number").html($user.attr.articleNum);
            $(".card_down").find("#fo_number").html($user.attr.followerNum);
        }
    }
});

var hideBtn = function(id) {
    $("#" + id).find(".sub_btn").css("display", "none");
    $("#" + id).find(".sub_btn_cancel").css("display", "none");
};
var showBtn = function(id) {
    $("#" + id).find(".sub_btn").css("display", "initial");
    $("#" + id).find(".info_input").attr('data-content', $("#" + id).find(".info_input").val());
    $("#" + id).find(".sub_btn_cancel").css("display", "initial");
};
var editNickname = function() {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull(userInfo)) {
        window.location.href = "/login";
        return false;
    }
    var con = $('#item_name').find(".info_input").val();

    if (checkNull(con)) {
        new jBox('Tooltip', {
            content : '昵称不能为空',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $("#item_name")
        }).open();
        return false;
    }
    if (con.length > 10) {
        new jBox('Tooltip', {
            content : '昵称不能超过10个字',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $("#item_name")
        }).open();
        return false;
    }
    if (!checkNull(con) && con == $('#item_name').find(".info_input").attr("data-content")) {
        hideBtn("item_name");
        return false;
    }
    $.ajax({
        cache : false,
        type : "post",
        url : '/api/v1/user/edit',
        data : {
            content : con,
            flag : 3
        },
        async : true,
        beforeSend : function(xhr) {
            return before(xhr);
        },
        error : function(data) {
            console.log(data);
            return false;
        },
        success : function(data) {
            console.log(data);
            var obj = jQuery.parseJSON(data);
            if (obj.code == 0) {
                var $name = $("#item_name").find(".info_input").val();
                $(".card_u").find("label")[0].innerHTML = $name;
                userInfo.nickname = $name;
                $.session.set("user", JSON.stringify(userInfo), true);
                hideBtn("item_name");
            } else if (obj.code == '20001') {
                window.location.href = '/login';
            }
            return true;
        }
    });
};
var editSignature = function() {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull(userInfo)) {
        window.location.href = "/login";
        return false;
    }
    var con = $('#item_sign').find(".info_input").val();
    if (!checkNull(con) && con.length > 15) {
        new jBox('Tooltip', {
            content : '签名不能超过15个字',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $("#item_sign")
        }).open();
        return false;
    }
    if (!checkNull(con) && con == $('#item_sign').find(".info_input").attr("data-content")) {
        hideBtn("item_sign");
        return false;
    }
    $.ajax({
        cache : false,
        type : "post",
        url : '/api/v1/user/edit',
        data : {
            content : con,
            flag : 1
        },
        async : true,
        beforeSend : function(xhr) {
            return before(xhr);
        },
        error : function(data) {
            console.log(data);
            var obj = jQuery.parseJSON(data);
            new jBox('Tooltip', {
                content : data.message,
                pointer : false,
                animation : 'zoomIn',
                closeOnClick : 'body',
                target : $("#item_sign")
            }).open();
            return false;
        },
        success : function(data) {
            console.log(data);
            var obj = jQuery.parseJSON(data);
            if (obj.code == 0) {
                var $sign = $("#item_sign").find(".info_input").val()
                $(".card_u").find("label")[1].innerHTML = $sign;
                hideBtn("item_sign");
                userInfo = jQuery.parseJSON($.session.get("user"));
                userInfo.signature = $sign;
                $.session.set("user", JSON.stringify(userInfo), true);
            } else if (obj.code == '20001') {
                window.location.href = '/login';
            }

            return true;
        }
    });
};
var editDesc = function() {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    if (checkNull(userInfo)) {
        window.location.href = "/login";
        return false;
    }
    var con = $('#item_desc').find(".info_input").val();
    if (!checkNull(con) && con.length > 100) {
        new jBox('Tooltip', {
            content : '简介不能超过100个字',
            pointer : false,
            animation : 'zoomIn',
            closeOnClick : 'body',
            target : $("#item_desc")
        }).open();
        return false;
    }
    if (!checkNull(con) && con == $('#item_desc').find(".info_input").attr("data-content")) {
        hideBtn("item_desc");
        return false;
    }
    $.ajax({
        cache : false,
        type : "post",
        url : '/api/v1/user/edit',
        data : {
            content : con,
            flag : 2
        },
        async : true,
        beforeSend : function(xhr) {
            return before(xhr);
        },
        error : function(data) {
            var obj = jQuery.parseJSON(data);
            new jBox('Tooltip', {
                content : data.message,
                pointer : false,
                animation : 'zoomIn',
                closeOnClick : 'body',
                target : $("#item_desc")
            }).open();
            return false;
        },
        success : function(data) {
            var obj = jQuery.parseJSON(data);
            if (obj.code == 0) {
                hideBtn('item_desc');
                userInfo = jQuery.parseJSON($.session.get("user"));
                userInfo.description = con;
                $.session.set("user", JSON.stringify(userInfo), true);
            } else if (obj.code == '20001') {
                window.location.href = '/login';
            }
            return true;
        }
    });
};

$(document).ready(function() {
    $ajaxPromise1.promise().done(function() {
        addHeadForImg();
    });

    $("#login").bind("click", function() {
        gotoLogin('/user/' + userInfo.userId);
    });
    $("#item_name").find(".info_input").focus(function() {
        showBtn("item_name");
    });
    $("#item_name").find(".sub_btn_cancel").on('click', function() {
        hideBtn("item_name");
        $("#item_name").find(".info_input").val($("#item_name").find(".info_input").attr("data-content"));

    });
    $("#item_name").find('.sub_btn').on('click', function() {
        editNickname();
    });
    // ====================
    $("#item_sign").find(".info_input").focus(function() {
        showBtn("item_sign");
    });
    $("#item_sign").find(".sub_btn_cancel").on('click', function() {
        hideBtn("item_sign");
        $("#item_sign").find(".info_input").val($("#item_sign").find(".info_input").attr("data-content"));
    });
    $("#item_sign").find('.sub_btn').on('click', function() {
        editSignature();
    });
    // ====================
    $("#item_desc").find(".info_input").focus(function() {
        showBtn("item_desc");
    });
    $("#item_desc").find(".sub_btn_cancel").on('click', function() {
        hideBtn("item_desc");
        $("#item_desc").find(".info_input").val($("#item_desc").find(".info_input").attr("data-content"));
    });
    $("#item_desc").find('.sub_btn').on('click', function() {
        editDesc();
    });
    // ================
    $(".camera").css("display", "block");
    $(".avatar_wrapper").hover(function() {
        $(".mask").css("display", "initial");
    }, function() {
        $(".mask").css("display", "none");
    });
    $(".avatar_wrapper").on("click", function() {
        uploadFile();
    });

    $("#bgupload").fileupload({
        url : '/api/v1/upload/background',
        dataType : 'json',
        autoUpload : true,
        maxNumberOfFiles : 1,// single
        acceptFileTypes : /(\.|\/)(jpe?g|png)$/i,
        maxFileSize : 999000
    }).on('fileuploaddone', function(e, data) {
        var r = data.result;
        if (r.code == '0') {
            var d = r.data;
            $(".panel").css('background', 'url(' + d + ') no-repeat 0% 70%/cover');
        } else {
            var error = $('<span class="text-danger"/>').text(r.message);
            $(data.context).append('<br>').append(error);
        }
    });
});