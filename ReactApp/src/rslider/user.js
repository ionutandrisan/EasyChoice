document.addEventListener("DOMContentLoaded", function(event) {
	var config1 = {
		"container_id": "slider1",
		"slides": [
			{"type": "img", "src": "images/batman-min.jpg" ,"link": "http://www.imdb.com/title/tt0468569/?ref_=nv_sr_1", "text":"The Dark Knight", "subtext": "2008"},
			{"type": "img", "src": "images/departed-min.jpg", "text":"The departed", "subtext": "2006"},
			{"type": "img", "src": "images/wardogs-min.jpg", "text":"War dogs", "subtext": "2016"},
			{"type": "img", "src": "images/whiplash-min.jpg", "text":"Whiplash", "subtext": "2014"},
			{"type": "img", "src": "images/rush-min.jpg", "text":"Rush", "subtext": "2013"},
			{"type": "img", "src": "images/wardogs-min.jpg", "text":"War dogs", "subtext": "2016"},
			{"type": "img", "src": "images/whiplash-min.jpg", "text":"Whiplash", "subtext": "2014"},
		],
		// "notouch": true,
		"bullets": true,
		"arrows": true,
		"animation": "rsl_carousel",
		"autoplay": 8000,
	}

	var config2 = {
		"container_id": "slider2",
		"slides": [
			{"type": "img", "src": "images/kings-min.jpg"}, 
			{"type": "img", "src": "images/bigshort-min.jpg"},
			{"type": "img", "src": "images/hateful-min.jpg"},
			{"type": "img", "src": "images/wick-min.jpg"},
		],
		"arrows": true,
		"animation": "rsl_shiftLeft",
		"autoplay": 5000,
	}

	var config4 = {
		"container_id": "slider4",
		"slides": [
			{"type": "img", "src": "images/prisoners-min.jpg"}, 
			{"type": "img", "src": "images/split-min.jpg"},
			{"type": "img", "src": "images/xmen-min.jpg"},
			{"type": "img", "src": "images/inception-min.jpg"},
			{"type": "img", "src": "images/deadpool-min.jpg"},
			{"type": "img", "src": "images/wardogs-min.jpg"},
		],
		"arrows": true,
		"animation": "rsl_cubeFace",
		"autoplay": 7000,
	}
	var config5 = {
		"container_id": "slider5",
		"slides": [
			{"type": "img", "src": "images/hateful-min.jpg"},
			{"type": "img", "src": "images/wick-min.jpg"},
			{"type": "img", "src": "images/bigshort-min.jpg"},
			{"type": "img", "src": "images/kings-min.jpg"}, 
		],
		// "notouch": true,
		"bullets": true,
		"arrows": true,
		"animation": "rsl_cardTurn",
		"autoplay": 3000,
	}

	var config6 = {
		"container_id": "slider6",
		"slides": [
			{"type": "img", "src": "images/wick-min.jpg"},
			{"type": "img", "src": "images/hateful-min.jpg"},
			{"type": "img", "src": "images/bigshort-min.jpg"},
			{"type": "img", "src": "images/kings-min.jpg"}, 
		],
		"arrows": true,
		"animation": "rsl_shiftTop",
		"autoplay": 5000,
	}

	var config7 = {
		"container_id": "slider7",
		"slides": [
			{"type": "img", "src": "images/whiplash-min.jpg", "text":"Whiplash", "subtext": "2014"},
			{"type": "img", "src": "images/departed-min.jpg", "text":"The departed", "subtext": "2006"},
			{"type": "img", "src": "images/rush-min.jpg", "text":"Rush", "subtext": "2013"},
			{"type": "img", "src": "images/wardogs-min.jpg", "text":"War dogs", "subtext": "2016"},
		],
		"bullets": true,
		"arrows": true,
		"animation": "rsl_cursive",
		"autoplay": 6000,
	}

	var config8 = {
		"container_id": "slider8",
		"slides": [
			{"type": "video", "src": "trailer.mp4", "text":"Chips", "subtext": "2017"},
			{"type": "iframe", "src": "https://www.youtube.com/embed/DekuSxJgpbY"},
			{"type": "iframe", "src": "https://www.youtube.com/embed/9n2B5WW4SKk"},
		],
		// "bullets": true,
		"arrows": true,
		// "autoplay": 7000,
	}

	var config3 = {
		"container_id": "slider3",
		"slides": [
			{"type": "img", "src": "images/whiplash-min.jpg", },
			{"type": "img", "src": "images/departed-min.jpg", },
			{"type": "img", "src": "images/rush-min.jpg", },
			{"type": "img", "src": "images/wardogs-min.jpg", "link": "http://www.imdb.com/title/tt0468569/?ref_=nv_sr_1"},
			{"type": "img", "src": "images/prisoners-min.jpg"}, 
			{"type": "img", "src": "images/inception-min.jpg"},
			{"type": "img", "src": "images/deadpool-min.jpg"},
			{"type": "img", "src": "images/wardogs-min.jpg"},
			{"type": "img", "src": "images/rush-min.jpg", },
			{"type": "img", "src": "images/wardogs-min.jpg", },
			{"type": "img", "src": "images/prisoners-min.jpg"}, 
			{"type": "img", "src": "images/split-min.jpg"},
		],
		"group": 4,
		"arrows": true,
		"animation": "rsl_cursive",
		"autoplay": 6000,
	}

	rsl.init(config1);
	rsl.init(config2);
	rsl.init(config4);
	rsl.init(config5);
	rsl.init(config6);
	rsl.init(config7);
	rsl.init(config8);
	rsl.init(config3);
});