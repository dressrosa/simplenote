function getData() {
	var url = document.URL;
	var articleId = url.substring(url.lastIndexOf('/'));
	$.ajax({
		type : "get",
		async : false,
		url : '/api/v1/article' + articleId,
		success : function(data) {
			var obj = jQuery.parseJSON(data);
			if (obj.code == '0') {
				var ar = obj.data;
				if (ar != null) {
					$(".img_shadow").find("img").attr('src', ar.user.avatar);
					$(".quote").find("span").html(ar.user.description);

					var $mainRight = $(".main_right");
					$mainRight.find(".note_date").html(ar.createDate);
					$mainRight.find("#readNum").html(ar.readNum);
					$mainRight.find(".note_content").attr("id", ar.articleId);
					$mainRight.find(".note_content").html(ar.content);
					$mainRight.find(".note_username").find(".nickname").html(
							ar.user.nickname);
				}
			}
			
			return true;
		}
	});
	$("#login").bind("click", function() {
		gotoLogin('/article/' + articleId);
	});
}

$(document).ready(function() {
	getData();
	addHeadForImg();
	var item = $(".note_content").attr("id");
	$.ajax({
		type : 'POST',
		async : true,
		url : '/public/article/changeView/' + item,
		error : function(data) {
			console.log(data);
		},
		success : function(data) {
			console.log(data);
		}
	});
	if (!isPC()) {
		$(".main").css("display", "block");
		$(".siderbar_left").css("display", "flex");
		var img = $(".img_shadow").find("img")
		img.css("border-radius", "50%");
		img.css("width", "100px");
		img.css("height", "100px");
	}
});