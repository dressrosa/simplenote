/*jslint unparam: true, regexp: true */
/*global window, $ */
$(function() {
    'use strict';
    // Change this to the location of your server-side upload handler:
    var url = window.location.hostname === 'blueimp.github.io' ? '//jquery-file-upload.appspot.com/' : '/api/v1/upload/avatar', uploadButton = $(
            '<button />').addClass('btn').addClass('sub').prop('disabled', true).text('上传中').on('click', function() {
        var $this = $(this), data = $this.data();
        $this.off('click').text('取消').on('click', function() {
            $this.remove();
            data.abort();
        });
        data.submit().always(function() {
            $this.remove();
        });
    });
    $('#fileupload').fileupload({
        url : url,
        dataType : 'json',
        autoUpload : false,
        maxNumberOfFiles : 1,// single
        acceptFileTypes : /(\.|\/)(jpe?g|png)$/i,
        maxFileSize : 999000,
        // Enable image resizing, except for Android and Opera,
        // which actually support image resizing, but fail to
        // send Blob objects via XHR requests:
        /*
         * disableImageResize : /Android(?!.*Chrome)|Opera/
         * .test(window.navigator.userAgent),
         */
        previewMaxWidth : 420,
        previewMaxHeight : 352,
        imageCrop : true,
        previewCrop : true
    }).on('fileuploadadd', function(e, data) {
        $("#files").empty();
        data.context = $('<div/>').appendTo('#files');
        // .addClass("aa");
        $.each(data.files, function(index, file) {
            var node = $('<p/>');
            if (!index) {
                node.append(uploadButton.clone(true).data(data));
            }
            node.appendTo(data.context);
            // $('#fileupload').attr("disabled", true);
        });
        $(".loc").css({
            'top' : '82%',
            'left' : '3%'
        });
    }).on('fileuploadprocessalways', function(e, data) {
        var index = data.index, file = data.files[index], node = $(data.context.children()[index]);
        if (file.preview) {
            node.prepend('<br>').prepend(file.preview);
        }
        if (file.error) {
            node.append('<br>').append($('<span class="text-danger"/>').text(file.error));
        }
        if (index + 1 === data.files.length) {
            data.context.find('button').text('上传图片').prop('disabled', !!data.files.error);
        }
    }).on('fileuploadprogressall', function(e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
        $('#progress .progress-bar').css('width', progress + '%');
    }).on('fileuploaddone', function(e, data) {
        var r = data.result;
        console.log(r);
        if (r.code == '0') {
            var d = r.data;
            console.log("img" + d);
            var link = $('<a>').attr('target', '_blank').prop('href', d);
            $(data.context).wrap(link);
            $(".avatar").attr('src', d);
            $(".panel_card").css('background', 'url(' + d + ')');
            myModal.destroy(); // 从dom移除,否则再次打开会失效

            var userInfo = jQuery.parseJSON($.session.get("user"));
            userInfo.avatar = d.substring(d.lastIndexOf("/"));
            console.log("avat" + ":" + userInfo.avatar);
            $.session.set("user", JSON.stringify(userInfo), true);
        } else {
            var error = $('<span class="text-danger"/>').text(r.message);
            $(data.context).append('<br>').append(error);
        }

        /*
         * $.each(data.result, function(index, file) { if (file.url) { var link =
         * $('<a>').attr('target', '_blank') .prop('href', file.url);
         * $(data.context.children()[index]).wrap(link); var s = file.url;
         * 
         * s = s.replace("avatar", "avatar/thumbnail"); $(".avatar").attr('src',
         * s); $( "#xyForm", window.parent.document) .find(
         * $("input[name='img']")) .val(file.url);
         * 
         * myModal.destroy(); // 从dom移除,否则再次打开会失效 } else if (file.error) { var
         * error = $('<span class="text-danger"/>') .text(file.error);
         * $(data.context.children()[index]) .append('<br>').append(error); }
         * });
         */
    }).on('fileuploadfail', function(e, data) {
        /*
         * $.each(data.files, function(index) { var error = $('<span
         * class="text-danger"/>').text( '上传失败');
         * $(data.context.children()[index]).append('<br>') .append(error);
         * });
         */
    }).prop('disabled', !$.support.fileInput).parent().addClass($.support.fileInput ? undefined : 'disabled');
});