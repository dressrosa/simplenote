$(document).ready(function() {

	// tab page
	$(".tab_ul").on('click', 'li', function() {
		var $selected = $(this);
		var $m = $selected.find(".mark");
		if ($m != null) {
			$m.remove();
		}
		$.each($selected.siblings(), function(i, v) {
			$(v).removeClass('li_active');
		});
		$selected.addClass('li_active');
	});

});
