var imgHead = "http://xiaoyu1-1253813687.costj.myqcloud.com/";
var blankPage = '<div class="blank_mug"><span><i class="icon_mug" style="cursor:default;"></i></span></div>';
function addHeadForImg() {
    // 给所有图片加上前缀
    var imgs = document.getElementsByTagName('img');
    var len = imgs.length;

    for (var i = 0; i < len; i++) {
        var $img = $(imgs[i]);
        if ($img.attr("withHead") == 0) {
            continue;
        }
        var $src = $img.attr("src");
        if (checkNull($src)) {
        } else if (!$src.startsWith('http')) {
            $img.attr("src", imgHead + $img.attr("src"));
        }
    }
}
function addHeadForOneImg(item) {
    var $imgs = item;
    $img.attr("src", imgHead + $img.attr("src"));
}
function writeBox() {
    var jBoxId;
    var writeButton = new jBox('Notice', {
        content : '您有未读新消息☺',
        position : {
            x : '0',
            y : '50'
        },
        autoClose : 5000,
        closeOnEsc : false, //  
        closeOnClick : 'box', //   
        closeOnMouseleave : false,//   
        closeButton : false,
        color : 'crimson',
        onInit : function() {
            jBoxId = this.id;
        },
        onClose : function() {
            window.location.href = "/message";
        }
    })
    writeButton.open();
    return jBoxId;
}

$(document).ready(function() {
    var userInfo = jQuery.parseJSON($.session.get("user"));
    $.ajax({
        type : "get",
        async : true,
        url : '/api/v1/message/unread-num',
        beforeSend : function(xhr) {
            var $u = jQuery.parseJSON($.session.get("user"));
            if (!checkNull(userInfo)) {
                xhr.setRequestHeader('token', $u.token);
                xhr.setRequestHeader('userId', $u.userId);
            }
        },
        success : function(data) {
            if(data != null && data !='') {
                var obj = jQuery.parseJSON(data);
                if (obj.code == '0') {
                    writeBox();
                } 
            }
        }
    });
});