$(document).ready(function(){
	$('#text').children('span').click(function(){
		var meaning = $(this).data('meaning');
		if(meaning){
			Android.showMeaning(meaning);
		} else {
			var word = $(this).text();
			openDictionary(word);
		}
	});
});

function openDictionary(word){
	closeDictionary();

	var url = $('#dict').data('url');
	if(!url || !word)
		return;
	url = url.replace('$$$', word);

	var iframe = $('<iframe>').attr({src: url, frameborder: 0, allowfullscreen: ''});
	iframe.on('load', function(){
        this.style.visibility = 'visible';
    });

	var close = $('<div>').attr({id: 'close', href: '#'}).text('CLOSE');
	close.click(function(e){
		e.preventDefault();
		closeDictionary();
	});

	$('#dict').append(iframe);
	$('#dict').append(close);
	$('#dict').show();
}

function closeDictionary(){
	$('#dict').hide();
	$('#dict').empty();
}
