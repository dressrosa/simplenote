$(document).ready(
		function() {
			var userInfo = jQuery.parseJSON($.session.get("user"));
			// 给所有图片加上前缀
			var imgs = document.getElementsByTagName('img');
			var len = imgs.length;
			for (var i = 0; i < len; i++) {
				imgs[i].src = "http://xiaoyu-0719.oss-cn-beijing.aliyuncs.com/"
						+ imgs[i].getAttribute('src');
			}
			
			//点击头像
			$(".avatar").bind("click",function(){
				$user = $(this);
				if(!checkNull(userInfo)&&userInfo.id==$user.attr("id")) {
					window.location.href = "/private/user/modify/"+userInfo.id;
				}
			});
			
		});

function writeBox() {
	var jBoxId;
	var writeButton = new jBox('Notice', {
		/* content : "＋", */
		position : {
			x : '3',
			y : '30'
		},
		closeOnEsc : false, //  
		closeOnClick : 'box', //   
		closeOnMouseleave : false, //   
		closeButton : false,
		color : '#292421',
		addClass : 'jBoxOpacity',
		onInit : function() {
			jBoxId = this.id;
		},
		onClose : function() {
			window.location.href = "/public/article/write";
		}
	})
	writeButton.open();
	setTimeout(function() {
		$("#" + jBoxId).css("opacity", 0.7);
		$("#" + jBoxId).find(".jBox-container").css({
			"background" : "url(/xiaoyu/img/publish.jpg) center center",
			"cursor" : "pointer",
			"height" : "39px"
		});

	}, 200);

	return jBoxId;
}