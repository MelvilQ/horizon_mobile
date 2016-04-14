$(document).ready(function(){
	$('span').each(function(i){
		var word = $(this).text().toLowerCase();
		$(this).click(function(){
			Android.showMeaning(word);
		})
		$(this).addClass(Android.getStrengthClass(word));
	});
});