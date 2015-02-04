$(document).ready(function(){
	$('a[href^="#"]').click(function (e) {
	    e.preventDefault();

	    var target = this.hash;
	    var $target = $(target);

	    $('html, body').stop().animate({
	        'scrollTop': $target.offset().top-100
	    }, 900, 'swing');
	});
	$('#menu a').click(function(){
		// $(".navbar-collapse").toggle('in');
		$("#menu-button").click();
   	 	// $('.nav-collapse').collapse('hide');
	});
});

$(function() {
		// new Maplace({
		// 	locations: [{lat:-0.203680,lon:-78.485120, zoom: 17}],
		// 	controls_on_map: false
		// }).Load();
var LocsAv2 = [{lat:-0.203680,lon:-78.485120, zoom: 17}];
new Maplace({
	locations: LocsAv2,
	map_div: '#gmap-polyline',
	    // controls_div: '#controls-polyline',
	    controls_type: 'list',
	    controls_on_map: false,
	    view_all_text: 'Start',
	    type: 'polyline'
	}).Load(); 
});

var tips = [{
		'icon'	:'fa fa-line-chart fa-3x',
		'title'	:'Título 1',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-database fa-3x',
		'title'	:'Título 2',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-cogs fa-3x',
		'title'	:'Título 3',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-code-fork fa-3x',
		'title'	:'Título 4',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	}];

var resume = {'en':'Hello Word', 'es':'Cras tincidunt bibendum justo at finibus. Suspendisse aliquet risus sit amet orci tempus, ut imperdiet lectus pretium. Morbi interdum dapibus ante nec dapibus. . <br> Etiam sed dolor eget odio fringilla maximus. Praesent mattis non lorem sit amet pretium. Morbi faucibus sollicitudin odio, nec posuere tellus rutrum et. Mauris nec volutpat quam. Sed ultricies dolor auctor, tincidunt tortor id, luctus felis.'};

var bulletsLeft = [{
		'icon'	: 'fa fa-wrench fa-2x',
		'title'	: 'Titulo 1',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-diamond fa-2x',
		'title'	: 'Titulo 2',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-bolt fa-2x',
		'title'	: 'Titulo 3',
		'detail': 'Descripcion corta del elemento'
	}];

var bulletsRight = [{
		'icon'	: 'fa fa-wrench fa-2x',
		'title'	: 'Titulo 1',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-diamond fa-2x',
		'title'	: 'Titulo 2',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-bolt fa-2x',
		'title'	: 'Titulo 3',
		'detail': 'Descripcion corta del elemento'
	}];

fillTips = function(){
	
	var container = document.getElementById("tipContent");
	for(var i = 0; i < tips.length; i++){
		var colDiv = document.createElement("div");
			colDiv.className += "col-md-3 center tip-margen";
		if(tips[i].icon != undefined){			
			var imageDiv = document.createElement("div");
			imageDiv.className +="tipImage";
			colDiv.appendChild(imageDiv);
			var imageI = document.createElement("i");
			imageI.className += tips[i].icon;
			imageDiv.appendChild(imageI);
			container.appendChild(colDiv);

			var titleH2 = document.createElement("h2");
			// titleH2.className +="tipTitle";
			titleH2.innerHTML = tips[i].title;
			colDiv.appendChild(titleH2);

			var descSpan = document.createElement("span");
			descSpan.className += "tipDescrition";
			descSpan.innerHTML = tips[i].detail;
			colDiv.appendChild(descSpan);		
		}			
		container.appendChild(colDiv);					
	}		
}

fillResume = function(lenguage){
	switch(lenguage){
		case 'en':
			$('.textResume').html(resume.en);
		break;
		case 'es':
			$('.textResume').html(resume.es);			
		break;		
	}
}

fillBulletsLeft = function(){
	var container = document.getElementById("bulletsContent");
	for(var i = 0; i < bulletsLeft.length; i++){
		var outterRow = document.createElement("div");
		outterRow.className="row tip-margen";
		var col1 = document.createElement("div");
		col1.className="col-md-8 col-xs-offset-1 col-xs-7";
		outterRow.appendChild(col1);

		var titleRow = document.createElement("div");
		titleRow.className="row title-thumb-purple";
		titleRow.innerHTML = bulletsLeft[i].title;
		col1.appendChild(titleRow);

		var descRow = document.createElement("div");
		descRow.className="row title-thumb-gray";
		descRow.innerHTML = bulletsLeft[i].detail;
		col1.appendChild(descRow);

		var col2 = document.createElement("div");
		col2.className="col-md-3 col-xs-2 circle-border-purple center";
		outterRow.appendChild(col2);

		var imageI = document.createElement("i");
		imageI.className += bulletsLeft[i].icon;
		col2.appendChild(imageI);

		container.appendChild(outterRow);
	}
}

fillBulletsRight = function(){
	var container = document.getElementById("bulletContent-right");
	for(var i = 0; i < bulletsRight.length; i++){
		var outterRow = document.createElement("div");
		outterRow.className="row tip-margen";
		var col1 = document.createElement("div");
		col1.className="col-md-3 col-xs-3 circle-border-purple center";
		outterRow.appendChild(col1);

		var imageI = document.createElement("i");
		imageI.className += bulletsRight[i].icon;
		col1.appendChild(imageI);

		var col2 = document.createElement("div");
		col2.className="col-md-9 col-xs-7";
		outterRow.appendChild(col2);

		var titleRow = document.createElement("div");
		titleRow.className="row title-thumb-purple title-thumb-purple-right";
		titleRow.innerHTML = bulletsRight[i].title;
		col2.appendChild(titleRow);

		var descRow = document.createElement("div");
		descRow.className="row title-thumb-gray title-thumb-gray-right";
		descRow.innerHTML = bulletsRight[i].detail;
		col2.appendChild(descRow);

		container.appendChild(outterRow);
	}
}
