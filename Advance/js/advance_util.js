var tips = {'size':6,'list':[{},{
		'icon'	:'fa fa-line-chart fa-2x',
		'title'	:'Título 1',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-database fa-2x',
		'title'	:'Título 2',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-cogs fa-2x',
		'title'	:'Título 3',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{
		'icon'	:'fa fa-code-fork fa-2x',
		'title'	:'Título 4',
		'detail':'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras at facilisis justo, eget egestas augue.'
	},{}]};

var resume = {'en':'Hello Word', 'es':'Cras tincidunt bibendum justo at finibus. Suspendisse aliquet risus sit amet orci tempus, ut imperdiet lectus pretium. Morbi interdum dapibus ante nec dapibus. . <br> Etiam sed dolor eget odio fringilla maximus. Praesent mattis non lorem sit amet pretium. Morbi faucibus sollicitudin odio, nec posuere tellus rutrum et. Mauris nec volutpat quam. Sed ultricies dolor auctor, tincidunt tortor id, luctus felis.'};

var bulletsLeft = [{
		'icon'	: 'fa fa-wrench',
		'title'	: 'Titulo 1',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-diamond',
		'title'	: 'Titulo 2',
		'detail': 'Descripcion corta del elemento'
	},{
		'icon'	: 'fa fa-bolt',
		'title'	: 'Titulo 3',
		'detail': 'Descripcion corta del elemento'
	}];

fillTips = function(){
	
	var container = document.getElementById("tipContent");
	for(var i = 0; i < tips.list.length; i++){
		var colDiv = document.createElement("div");
			colDiv.className += "col-md-2 stuffs";
		if(tips.list[i].icon != undefined){			
			var imageDiv = document.createElement("div");
			imageDiv.className +="tipImage";
			colDiv.appendChild(imageDiv);
			var imageI = document.createElement("i");
			imageI.className += tips.list[i].icon;
			imageDiv.appendChild(imageI);
			container.appendChild(colDiv);

			var titleH2 = document.createElement("h2");
			titleH2.className +="tipTitle";
			titleH2.innerHTML = tips.list[i].title;
			colDiv.appendChild(titleH2);

			var descSpan = document.createElement("span");
			descSpan.className += "tipDescrition";
			descSpan.innerHTML = tips.list[i].detail;
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
	for(var i = 0; i<bulletsLeft.length; i++){

	}
}