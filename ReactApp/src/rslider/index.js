import './rslider.css';

var rsl = {
	// init
	init: function(config){
		var container = document.getElementById(config.container_id);

		container.className += " rsl_container";

		// build_slides
		rsl.build_slides(config);

		// build_bullets
		if(config.bullets){
			var slength = config.slides.length;
			if(config.group && config.group > 1 && config.group<=8){slength = Math.ceil(slength/config.group);}
			rsl.build_bullets(container, slength); 
		}

		// build_arrows
		if(config.arrows){ rsl.build_arrows(container); }

		// touch
		if(!config.notouch){
			container.addEventListener('touchstart', rsl.handleTouchStart, false);    
		    container.addEventListener('touchmove', rsl.handleTouchMove, false);
		    container.addEventListener('touchend', rsl.handleTouchEnd, false);
		}

	    // animation
	    rsl.build_animation(config);

	    // launch
		rsl.showDivs(config.container_id, 0);

		// set interval
		var time = config.autoplay;
		if(time){
			if(time<3000){time = 3000}
			setInterval(function(){
				if(!(container.querySelector(':hover')) && document.hasFocus()){
					rsl.showDivs(config.container_id, 0.5);
				}
			}, time);
		}
	},
	build_slides: function(config){
		var slides = config.slides;
		var container = document.getElementById(config.container_id);

		var sub_container = document.createElement("div");
		sub_container.className = "rsl_subContainer";
		container.appendChild(sub_container);

		rsl.slideIndex[config.container_id] = 0;
		rsl.first[config.container_id] = 0;
		rsl.last[config.container_id] = 0;

		var i;
		for(i=0;i<slides.length;++i){

			var el_container = document.createElement("figure");

			if(config.group && config.group>1 && config.group<=8 ){
				el_container.className = "rsl_grouped_element";
			}else {el_container.className = "rsl_element_container";}

			switch(slides[i]["type"]) {
			    case "img":
			    	el_container.style.backgroundImage = "url("+slides[i]["src"]+")";
			    	if(slides[i]["link"]){
			    		var a = document.createElement("a");
			    		a.setAttribute('href', slides[i]["link"]);
			    		el_container.appendChild(a);
			    	}
			    	sub_container.appendChild(el_container);
			        break;
			    case "iframe":
			    	var element = document.createElement("iframe");
			    	element.setAttribute('src', slides[i]["src"]);
			    	element.setAttribute('frameborder', '0');
					element.setAttribute('allowfullscreen', '');

			    	el_container.appendChild(element);
			    	el_container.style.backgroundColor = "black";
			    	sub_container.appendChild(el_container);
			        break;
			    case "video":
			    	var element = document.createElement("video");
			    	element.setAttribute('controls', '');

			    	var source = document.createElement("source");
			    	source.setAttribute('src', slides[i]["src"]);
			    	source.setAttribute('type', 'video/mp4');
			    	element.appendChild(source);

			    	el_container.appendChild(element);
			    	sub_container.appendChild(el_container);
			    	break;
			}
			if(slides[i]["text"]){el_container.innerHTML += "<h2>"+slides[i]["text"]+"</h2>"}
	        if(slides[i]["subtext"]){el_container.innerHTML += "<h4>"+slides[i]["subtext"]+"</h4>"}
		}

		// group
		if(config.group && config.group > 1 && config.group<=8){
			rsl.group(sub_container, config.group);
		}
	},
	// group
	group: function(sub_container, groupnr){
		var elements = sub_container.getElementsByClassName("rsl_grouped_element");
		var i, j;
		var elength = elements.length;
		var nrOfSlides = Math.ceil(elength/groupnr);

		for (i=0;i<nrOfSlides;++i){
			var el_container = document.createElement("figure");
			el_container.className = "rsl_element_container";

			for(j=0;j<groupnr;++j){
				
				if(j<elength-1){
					el_container.appendChild(elements[0]);
				}
			}

			sub_container.appendChild(el_container);
		}	
		for (i = 0; i<elength; i++) {
			elements[0].className = elements[0].className.replace("rsl_grouped_element", ("rsl_grouped_element" + groupnr));
		}
	},
	// bullets
	build_bullets: function(container, slength){
		var bullets_container = document.createElement("div");
		bullets_container.className = "rsl_bullets_container";

		var i;
		for(i=0;i<slength;++i){
			var bullet = document.createElement("span");
			bullet.innerHTML = "&#8226;";
			bulletEvent(bullet, i);

			bullets_container.appendChild(bullet);
		}
		function bulletEvent(bullet, i){bullet.addEventListener("click", function(){rsl.showDivs(this.parentNode.parentNode.id, i)});}
		container.appendChild(bullets_container);
	},
	// arrows
	build_arrows: function(container){
		var arrow = document.createElement("div");
		arrow.setAttribute("class", "rsl_arrowL");
		arrow.innerHTML = "&#10094;";
		arrow.addEventListener("click", function(){rsl.showDivs(this.parentNode.id, -0.5)});
		container.appendChild(arrow);

		arrow = document.createElement("div");
		arrow.setAttribute("class", "rsl_arrowR");
		arrow.innerHTML = "&#10095;";
		arrow.addEventListener("click", function(){rsl.showDivs(this.parentNode.id, 0.5)});
		container.appendChild(arrow);
	},
	// build animation
	build_animation: function(config){
		var elements = document.getElementById(config.container_id).getElementsByClassName("rsl_element_container");
		var container = document.getElementById(config.container_id).getElementsByClassName("rsl_subContainer")[0];
		var animation = "rsl_basic";
		
		var i;			
		if(config.animation == "rsl_carousel"){
			var tz = Math.round( ( container.offsetWidth / 2 ) / Math.tan( Math.PI / elements.length ) );
			for(i=0;i<elements.length;++i){
				elements[i].style.webkitTransform = "rotateY(" + (i*360/elements.length) + "deg)" + " translate3d(0, 0, " + tz +"px)";
				elements[i].style.MozTransform = "rotateY(" + (i*360/elements.length) + "deg)" + " translate3d(0, 0, " + tz +"px)";
				elements[i].style.transform = "rotateY(" + (i*360/elements.length) + "deg)" + " translate3d(0, 0, " + tz +"px)";
			}
				
			animation = config.animation;
		}

		if(config.animation == "rsl_cubeFace" && config.slides.length == 6){	

			for(i=0;i<elements.length;++i){
				elements[i].style.webkitTransform = rsl.cubeRotations[i] + " translate3d(0, 0, " + (container.offsetWidth*85/200) +"px)";
				elements[i].style.MozTransform = rsl.cubeRotations[i] + " translate3d(0, 0, " + (container.offsetWidth*85/200) +"px)";
				elements[i].style.transform = rsl.cubeRotations[i] + " translate3d(0, 0, " + (container.offsetWidth*85/200) +"px)";
			}
			animation = config.animation;
		}

		if(config.animation && (config.animation != "rsl_cubeFace")){
			animation = config.animation;
		}

		if(['rsl_cubeFace', 'rsl_carousel', 'rsl_cardTurn'].indexOf(animation) >= 0){
			container.className = container.className.replace("rsl_subContainer", "rsl_subContainer_3D");
		}
		
		rsl.animation[config.container_id] = animation;

		for(i=0;i<elements.length;++i){
			elements[i].className = elements[i].className + " " + animation;
		}
	},
	// show figure
	showDivs: function(container_id, newS){
		var oldS = rsl.slideIndex[container_id];
		var orNewS;
		var slides = document.getElementById(container_id).getElementsByClassName("rsl_element_container");
		var dots_container = document.getElementById(container_id).getElementsByClassName("rsl_bullets_container")[0];
		var slen = slides.length;

		if(newS == -0.5){newS = oldS-1;}
		if(newS == 0.5){newS = oldS+1;}

		orNewS = newS;

		if(newS<0){newS = slen-1;}
		if(newS>slen-1){newS = 0;}

		// animations tree
		switch(rsl.animation[container_id]){
			case "rsl_basic":
			case "rsl_shiftLeft":
			case "rsl_shiftTop":
			case "rsl_shiftBottom":
			case "rsl_shiftRight":
				rsl.animate_shift(slides, oldS, newS);
				break;
			case "rsl_cursive":
				rsl.animate_cursive(slides, oldS, newS, orNewS)
				break;
			case "rsl_cardTurn":
				rsl.animate_cardTurn(slides, oldS, newS);
				break;
			case "rsl_cubeFace":
				rsl.animate_cubeFace(slides, oldS, newS);
				break;
			case "rsl_carousel":
				rsl.animate_carousel(slides, oldS, orNewS)
				break;
		}

		// change dot
		if(dots_container){
			var dots = dots_container.getElementsByTagName("span");
			dots[oldS].className = dots[oldS].className.replace(" rsl_dotselected", "");
			dots[newS].className += " rsl_dotselected";
		}

		rsl.slideIndex[container_id] = newS;
	},
	// animations
	animate_shift: function(slides, oldS, newS){
		slides[oldS].className = slides[oldS].className.replace(" rsl_shiftSide_selslideN", " rsl_shiftSide_selslideO");
		slides[newS].className += " rsl_shiftSide_selslideN";
		var myVar = setTimeout(function(){slides[oldS].className = slides[oldS].className.replace(" rsl_shiftSide_selslideO", "");}, 400);
	},
	animate_cursive: function(slides, oldS, newS, orNewS){
		var tranzOrder = [];

		if(orNewS < oldS){ tranzOrder = [" rsl_shiftLeft", " rsl_shiftRight"] }else{ tranzOrder = [" rsl_shiftRight", " rsl_shiftLeft"] }

		// put in place the new slide
		slides[newS].className += " notransition";
		slides[newS].className += tranzOrder[0];
		slides[newS].offsetHeight; // eslint-disable-line
		slides[newS].className = slides[newS].className.replace(" notransition", "");

		// slide the old out
		slides[oldS].className = slides[oldS].className.replace(" rsl_shiftSide_selslideN", "");
		slides[oldS].className += tranzOrder[1];

		// slide the new in
		slides[newS].className = slides[newS].className.replace(" rsl_shiftRight", "");
		slides[newS].className = slides[newS].className.replace(" rsl_shiftLeft", "");
		slides[newS].className += " rsl_shiftSide_selslideN";

		setTimeout(function(){ 
			// clear old after animation and replace top with no transition
			slides[oldS].className += " notransition";
			slides[oldS].className = slides[oldS].className.replace(" rsl_shiftLeft", "");
			slides[oldS].className = slides[oldS].className.replace(" rsl_shiftRight", "");
			slides[oldS].offsetHeight; // eslint-disable-line
			slides[oldS].className = slides[oldS].className.replace(" notransition", "");
		}, 350);
	},
	animate_cardTurn: function(slides, oldS, newS){
		var i;
		
		if(slides[oldS].parentNode.className == "rsl_subContainer_3D"){
			for (i = 0; i < slides.length; i++) {
			  slides[i].className = slides[i].className.replace(" rsl_cardTurn_face2", "");
			}
			slides[newS].className += " rsl_cardTurn_face2";
			slides[oldS].parentNode.className += " rsl_cardTurn_flipped";
		}else{
			for (i = 0; i < slides.length; i++) {
			  slides[i].className = slides[i].className.replace(" rsl_cardTurn_face1", "");
			}
			slides[newS].className += " rsl_cardTurn_face1";
			slides[oldS].parentNode.className = "rsl_subContainer_3D";
		}
	},
	animate_cubeFace: function(slides, oldS, newS){
		slides[newS].parentNode.style.webkitTransform = "translate3d(0, 0, " + (-slides[newS].offsetWidth/2) +"px)" + rsl.cubeRotations[newS];
		slides[newS].parentNode.style.MozTransform = "translate3d(0, 0, " + (-slides[newS].offsetWidth/2) +"px)" + rsl.cubeRotations[newS];
		slides[newS].parentNode.style.transform = "translate3d(0, 0, " + (-slides[newS].offsetWidth/2) +"px)" + rsl.cubeRotations[newS];
	},
	animate_carousel: function(slides, oldS, orNewS){
		var parent = slides[oldS].parentNode;
		var tz = Math.round( -( parent.offsetWidth / 2 ) / Math.tan( Math.PI / slides.length ) );

		// loop carousel
		if(orNewS>slides.length-1){ reset_carousel(0); }else if(orNewS<0){ reset_carousel(slides.length-1); }

		// blink from -1 to max and from max to -1
		function reset_carousel(nn){
			setTimeout(function(){ 
				parent.className += " notransition";

				parent.style.webkitTransform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-(nn)*360/slides.length) +"deg)";
				parent.style.MozTransform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-(nn)*360/slides.length) +"deg)";
				parent.style.transform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-(nn)*360/slides.length) +"deg)";

				parent.offsetHeight; // eslint-disable-line
				parent.className = parent.className.replace(" notransition", "");
			}, 350);
		}

		parent.style.webkitTransform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-orNewS*360/slides.length) +"deg)";
		parent.style.MozTransform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-orNewS*360/slides.length) +"deg)";
		parent.style.transform = "translate3d(0, 0, " + tz +"px)" + " rotateY("+ (-orNewS*360/slides.length) +"deg)";
	},
	// touch
	handleTouchStart: function(evt){ rsl.first[this.rsl_id] = evt.touches[0].clientX; },
	handleTouchMove: function(evt){ 
		rsl.last[this.rsl_id] = evt.touches[0].clientX;
	},
	handleTouchEnd: function(evt) {
		if(rsl.first[this.rsl_id] && rsl.last[this.rsl_id]){
			var diff = rsl.first[this.rsl_id] - rsl.last[this.rsl_id];
			if ( Math.abs(diff) > 100 ) {
			    if ( diff > 0 ) {
			        rsl.showDivs(this.id, 0.5);
			    } else {
			        rsl.showDivs(this.id, -0.5);
			    }                                                                                      
			}
		}
		rsl.first[this.rsl_id] = 0;
		rsl.last[this.rsl_id] = 0;
	},
	// variables
	slideIndex: {},
	animation: {},
	first: {},
	last: {},
	cubeRotations: ["rotateY(0)", "rotateX(180deg)", "rotateY(90deg)", "rotateY(-90deg)", "rotateX(90deg)", "rotateX(-90deg)"],
}

export default rsl;