var url = document.URL;
var $userId = url.split('/')[3];
var $lock = true;
var getList = function(item) {
    var $ajaxPromise = $.ajax({
        type : "get",
        url : '/api/v1/article/columns',
        data : {
            userId : item
        },
        beforeSend : function(xhr) {
            var $userInfo = jQuery.parseJSON($.session.get("user"));
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
    var arHtml = '';
    $.each(obj.data, function(index2, cu) {
        var childHtml = '<div>';
        childHtml += '<div class="list_column_item" id="' + cu.columnId + '">';
        childHtml += '<span><i class="icon_folder-open_alt"></i>' + cu.name + '</span>';
        if (cu.isOpen == 0) {
            childHtml += '<i class=" icon_lock"></i>';
        }
        childHtml += '<p>xixihaha</p>';
        childHtml += ' </div>';
        arHtml += childHtml;
    });
    $(".list_column").append(arHtml);

    $('.list_column_item').bind('click', function() {
    });
    return true;

};

var userInfo = jQuery.parseJSON($.session.get('user'));
$(document).ready(function() {
    getList($userId);
    if (checkNull(userInfo)) {
        $("#loginSpan").css("display", "block");
    } else {
        $("#userSpan").css("display", "block");
        $("#userSpan").find("#nickname").attr("href", "/user/" + userInfo.userId);
        $("#userSpan").find("#nickname").text(userInfo.nickname);
    }
});