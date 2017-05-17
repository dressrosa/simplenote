$(document).ready(
		function() {
			// 给所有图片加上前缀
			var imgs = document.getElementsByTagName('img');
			var len = imgs.length;
			for (var i = 0; i < len; i++) {
				imgs[i].src = "http://xiaoyu-0719.oss-cn-beijing.aliyuncs.com/"
						+ imgs[i].getAttribute('src');
			}
		});