$(document).ready(function(){
	$('#text').children('span').click(function(){
		var meaning = $(this).data('meaning');
		if(meaning)
			Android.showMeaning(meaning);
	});
});
