var url = document.URL;
var articleId = url.substring(url.lastIndexOf('/'));
var $arPromise = $.ajax({
	cache : false,
	type : "get",
	async : true,
	url : '/api/v1/article' + articleId,
	success : function(data) {
		var obj = jQuery.parseJSON(data);
		if (obj.code == '0') {
			var ar = obj.data;
			if (ar != null) {
				var $partUp = $(".part_up");
				$partUp.find("img").attr('src', ar.user.avatar);
				$partUp.find("img").on("click",function(){
					window.location.href = "/user/"+ar.user.userId;
				});
				$partUp.find(".p_description").find("span").html(
						ar.user.description);
				$partUp.find(".p_username").find(".nickname").html(
						ar.user.nickname);
				$partUp.find(".red").find("label").html(ar.attr.likeNum);
				$partUp.find(".blue").find("label").html(ar.attr.collectNum);
				$partUp.find(".green").find("label").html(ar.attr.commentNum);
				var $partDown = $(".part_down");
				$partDown.find(".ar_date").html(ar.createDate);
				$partDown.find(".ar_title").find("label").html(ar.title);
				$partDown.find(".ar_time").find("label").html(ar.createTime);
				$partDown.find("#readNum").html(ar.attr.readNum);
				$partDown.find(".ar_content").attr("id", ar.articleId);
				$partDown.find(".ar_content").html(ar.content);
			}
		} else {
			// window.location.href = "/common/404";
			return false;
		}
		addHeadForImg();
		return true;
	}
});

$(document).ready(
		function() {
			$("#login").bind("click", function() {
				gotoLogin('/article/' + articleId);
			});
			$arPromise.promise()
					.done(
							function() {
								var item = $(".ar_content").attr("id");
								$.ajax({
									type : 'POST',
									async : true,
									url : '/api/v1/article/viewNum/' + item,
									error : function(data) {
										console.log(data);
									},
									success : function(data) {
										console.log(data);
									}
								});
								// comment
								$(".part_comment").bind("click", function() {
									window.location.href = "/article/comments";
								});

								$arContent = $(".ar_content");

								console.log($arContent.css("height") + ","
										+ $arContent.css("max-height"))
								if ($arContent.css("height") >= $arContent
										.css("max-height")) {
									$arContent.find(".ar_more").css("display",
											"block");

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
		});