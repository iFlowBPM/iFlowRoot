/********************************************************
For more info & download: http://www.ibegin.com/blog/p_ibox.html
Created for iBegin.com - local search done right
MIT Licensed Style
*********************************************************/

/********************************************************
Oscar Lopes:
I have made some minor hacks to display a single loader
or a box (using hidden div method). I have also added the
'ib' prefix to all methods and variables. I made some
hacks to create a class and closures. This must be
extensively tested in IE due to known memory leaks.
********************************************************/

function iBox(opts) {
  var options = new Object();
  if(opts) options = opts;
  
  this.indicatorImgPath = options.indicatorImg?options.indicatorImg:"../images/indicator.gif";

  this.bgOpacityLevel = options.bgOpacity?options.bgOpacity:8; // how transparent our overlay bg is
  this.fgOpacityLevel = options.fgOpacity?options.fgOpacity:10;
  this.ibAttr = "rel"; 	// our attribute identifier for our iBox elements
  this.bgColor = options.bgColor;
  this.closeLink = options.closeLink==undefined?true:options.closeLink;

  this.imgPreloader = new Image(); // create an preloader object

  this.ibox_w_height = 0;
  this.loadCancelled = false;

  this.initIbox = function() {
	var elem_wrapper = "ibox";
	
	this.createIbox(document.getElementsByTagName("body")[0]); //create our ibox

	//	elements here start the look up from the start non <a> tags
	//var docRoot = (document.all) ? document.all : document.getElementsByTagName("*");
	
	// Or make sure we only check <a> tags
	var docRoot = document.getElementsByTagName("a");

	var e;
	for (var i = 0; i < docRoot.length - 1; i++) {
			e = docRoot[i];
			if(e.getAttribute(this.ibAttr)) {
				var t = e.getAttribute(this.ibAttr);
				if ((t.indexOf("ibox") != -1)  ||  t.toLowerCase() == "ibox") { // check if this element is an iBox element
						var ibObj = this;
						e.onclick = function() { // rather assign an onclick event
							var t = this.getAttribute(ibObj.ibAttr);
							var params = ibObj.parseQuery(t.substr(5,999));
							var url = this.href;
							if(this.target != "") {url = this.target} 
	
							var title = this.title;

							if(ibObj.showIbox(url,title,params)) {
								ibObj.showBG();
								window.onscroll = ibObj.clMaintPos;
								window.onresize = ibObj.clMaintPos;
							}
							return false;
						}; 
						
				}
			}
     }
  }

  this.showBG = function() {
	var box_w = this.getElem('ibox_w');
	
	box_w.style.opacity = 0;
	box_w.style.filter = 'alpha(opacity=0)';
	// setBGOpacity = setOpacity;
	for (var i=0;i<=this.bgOpacityLevel;i++) {
		setTimeout(this.clOpacitiesB[i],70*i);
	} // from quirksmode.org
	
	
	box_w.style.display = "";
	var pagesize = new this.getPageSize();
	var scrollPos = new this.getScrollPos();
	var ua = navigator.userAgent;
	
	if(ua.indexOf("MSIE ") != -1) {box_w.style.width = pagesize.width+'px';} 
	/*else {box_w.style.width = pagesize.width-20+'px';}*/ // scrollbars removed! Hurray!
	box_w.style.height = pagesize.height+scrollPos.scrollY+'px';
  };

  this.hideBG = function() {
	var box_w = this.getElem('ibox_w');
	box_w.style.display = "none";
  };

  this.ibShowIndicator = function() {
	var ibox_p = this.getElem('ibox_progress');
	ibox_p.style.display = "";
	this.posToCenter(ibox_p);
	// todo: create a closure!
	ibox_p.onclick = function() {this.hideIbox();this.hideIndicator();this.loadCancelled = true;}
  };


  this.hideIndicator = function() {
	var ibox_p = this.getElem('ibox_progress');
	ibox_p.style.display = "none";
	ibox_p.onclick = null;
  };

  this.createIbox = function(elem) {
    var bg = "";
	if(this.bgColor) {
	  bg = ";background-color:"+this.bgColor;
	}
    
    var indicator_img_html = "<img name=\"ibox_indicator\" src=\""+ /**this.indicatorImgPath+**/ "\" alt=\"Loading...\" style=\"width:128px;height:128px;\"/>"; // don't remove the name
	// a trick on just creating an ibox wrapper then doing an innerHTML on our root ibox element
	var strHTML = "<div id=\"ibox_w\" style=\"display:none"+bg+";\"></div>";
	strHTML +=	"<div id=\"ibox_progress\" style=\"display:none;\">";
	strHTML +=  indicator_img_html;
	strHTML +=  "</div>";
	strHTML +=	"<div id=\"ibox_wrapper\" style=\"display:none\">";
	strHTML +=	"<div id=\"ibox_content\"></div>";
	strHTML +=	"<div id=\"ibox_footer_wrapper\">"
	if(this.closeLink) {
	strHTML +=	"<div id=\"ibox_close\" style=\"float:right;\">";
	strHTML +=	"<a id=\"ibox_close_a\" href=\"javascript:void(null);\" >Click here to close</a></div>";
	}
	strHTML +=  "<div id=\"ibox_footer\">&nbsp;</div></div></div></div>";

	var docBody = document.getElementsByTagName("body")[0];
	var ibox = document.createElement("div");
	ibox.setAttribute("id","ibox");
	ibox.style.display = '';
	ibox.innerHTML = strHTML;
	elem.appendChild(ibox);
  };

  this.showIbox = function(url,title,params) {
    var ibObj = this;  // closure thing...
	var ibox = this.getElem('ibox_wrapper');
	var ibox_type = 0;
												
	// set title here
	var ibox_footer = this.getElem('ibox_footer');
	if(title != "") {ibox_footer.innerHTML = title;} else {ibox_footer.innerHTML = "&nbsp;";}
	
	// file checking code borrowed from thickbox
	var urlString = /\.jpg|\.jpeg|\.png|\.gif|\.html|\.htm|\.php|\.cfm|\.asp|\.aspx|\.jsp|\.jst|\.rb|\.rhtml|\.txt/g;
	
	var urlType = url.match(urlString);

	if(urlType == '.jpg' || urlType == '.jpeg' || urlType == '.png' || urlType == '.gif'){
		ibox_type = 1;
	} else if(url.indexOf("#") != -1) {
		ibox_type = 2;
	} else if(urlType=='.htm'||urlType=='.html'||urlType=='.php'||
			 urlType=='.asp'||urlType=='.aspx'||urlType=='.jsp'||
			 urlType=='.jst'||urlType=='.rb'||urlType=='.txt'||urlType=='.rhtml'||
			 urlType=='.cfm') {
		ibox_type = 3;
	} else {
		// override our ibox type if forced param exist
		if(params['type']) {ibox_type = parseInt(params['type']);}
		else{this.hideIbox();return false;}
	}
	
	ibox_type = parseInt(ibox_type);


	switch(ibox_type) {
		
		case 1:

			this.ibShowIndicator();
			
			this.imgPreloader = new Image();
			// closure!!
			this.imgPreloader.onload = function(){
	
				ibObj.imgPreloader = ibObj.resizeImageToScreen(ibObj.imgPreloader);
				ibObj.hideIndicator();
	
				var strHTML = "<img name=\"ibox_img\" src=\""+url+"\" style=\"width:"+ibObj.imgPreloader.width+"px;height:"+ibObj.imgPreloader.height+"px;border:0;cursor:hand;margin:0;padding:0;position:absolute;\"/>";
	
				if(ibObj.loadCancelled == false) {
					
					// set width and height
					ibox.style.height = ibObj.imgPreloader.height+'px';
					ibox.style.width = ibObj.imgPreloader.width+'px';
				
					ibox.style.display = "";
					ibox.style.visibility = "hidden";
					ibObj.posToCenter(ibox); 	
					ibox.style.visibility = "visible";

					ibObj.setIBoxContent(strHTML);
				}
					
			};
			
			this.loadCancelled = false;
			this.imgPreloader.src = url;
			
			break;

		case 2:
			this.hideIndicator();
			
			var strHTML = "";

			
			if(params['height']) {ibox.style.height = params['height']+'px';} 
			else {ibox.style.height = '280px';}
			
			if(params['width']) {ibox.style.width = params['width']+'px';} 
			else {ibox.style.width = '450px';}

		
			ibox.style.display = "";
			ibox.style.visibility = "hidden";
			this.posToCenter(ibox); 	
			ibox.style.visibility = "visible";
			
			this.getElem('ibox_content').style.overflow = "auto";
			
			var elemSrcId = url.substr(url.indexOf("#") + 1,1000);
			
			var elemSrc = this.getElem(elemSrcId);
			
			if(elemSrc) {strHTML = elemSrc.innerHTML;}
		
			this.setIBoxContent(strHTML);
			
			break;
			
		case 3:
			this.ibShowIndicator();
			this.http.open('get',url,true);

			// Closure....
			this.http.onreadystatechange = function() {
				if(ibObj.http.readyState == 4){
					ibObj.hideIndicator();
					
					if(params['height']) {ibox.style.height = params['height']+'px';} 
					else {ibox.style.height = '280px';}
					
					if(params['width']) {ibox.style.width = params['width']+'px';} 
					else {ibox.style.width = '450px';}
		
					ibox.style.display = "";
					ibox.style.visibility = "hidden";
					ibObj.posToCenter(ibox);
					ibox.style.visibility = "visible";
					ibObj.getElem('ibox_content').style.overflow = "auto";
					
					//var response = ibObj.http.responseText;
					//ibObj.setIBoxContent(response);
					
				}
			}
			
			this.http.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
			this.http.send(null);
			break;
		
		case 4:
			this.ibShowIndicator();
			break;
			
		default:
			
	 } 
	 
	
	ibox.style.opacity = 0;
	ibox.style.filter = 'alpha(opacity=0)';	
	
	// this.setIboxOpacity = this.setOpacity;
	// for (var i=0;i<=fgOpacityLevel;i++) {setTimeout("setIboxOpacity('ibox_wrapper',"+i+")",30*i);}
	for (var i=0;i<=this.fgOpacityLevel;i++) {
		setTimeout(this.clOpacitiesF[i],70*i);
	} // from quirksmode.org
	
	if(this.getElem("ibox_close_a")) {
		if(ibox_type == 2 || ibox_type == 3 || ibox_type == 4) {
			ibox.onclick = null;
			this.getElem("ibox_close_a").onclick = this.clHideIbox;
		} else {
			ibox.onclick = this.clHideIbox; // closure
			this.getElem("ibox_close_a").onclick = null;
		}
	}
	return true;
  };

  this.setOpacity = function (elemid,value)	{
		var e = this.getElem(elemid);
		e.style.opacity = value/10;
		e.style.filter = 'alpha(opacity=' + value*10 + ')';
  };

  this.resizeImageToScreen = function(objImg) {
	
	var pagesize = new this.getPageSize();
	
	var x = pagesize.width - 100;
	var y = pagesize.height - 100;

	if(objImg.width > x) { 
		objImg.height = objImg.height * (x/objImg.width); 
		objImg.width = x; 
		if(objImg.height > y) { 
			objImg.width = objImg.width * (y/objImg.height); 
			objImg.height = y; 
		}
	} 

	else if(objImg.height > y) { 
		objImg.width = objImg.width * (y/objImg.height); 
		objImg.height = y; 
		if(objImg.width > x) { 
			objImg.height = objImg.height * (x/objImg.width); 
			objImg.width = x;
		}
	}

	return objImg;
  };

  this.maintPos = function() {

	var ibox = this.getElem('ibox_wrapper');
	var box_w = this.getElem('ibox_w');
	var pagesize = new this.getPageSize();
	var scrollPos = new this.getScrollPos();
	var ua = navigator.userAgent;

	if(ua.indexOf("MSIE ") != -1) {box_w.style.width = pagesize.width+'px';} 
	/*else {box_w.style.width = pagesize.width-20+'px';}*/

	if(ua.indexOf("Opera/9") != -1) {box_w.style.height = document.body.scrollHeight+'px';}
	else {box_w.style.height = pagesize.height+scrollPos.scrollY+'px';}
	
	// alternative 1
	//box_w.style.height = document.body.scrollHeight+50+'px';	
	
	this.posToCenter(ibox);
  };

  this.hideIbox = function() {
	this.hideBG();
	var ibox = this.getElem('ibox_wrapper');
	ibox.style.display = "none";

	this.clearIboxContent();
	window.onscroll = null;
  };

  this.posToCenter = function(elem) {
	var scrollPos = new this.getScrollPos();
	var pageSize = new this.getPageSize();
	var emSize = new this.getElementSize(elem);
	var x = Math.round(pageSize.width/2) - (emSize.width /2) + scrollPos.scrollX;
	var y = Math.round(pageSize.height/2) - (emSize.height /2) + scrollPos.scrollY;	
	elem.style.left = x+'px';
	elem.style.top = y+'px';	
  };

  this.getScrollPos = function() {
	var docElem = document.documentElement;
	this.scrollX = self.pageXOffset || (docElem&&docElem.scrollLeft) || document.body.scrollLeft;
	this.scrollY = self.pageYOffset || (docElem&&docElem.scrollTop) || document.body.scrollTop;
  };

  this.getPageSize = function() {
	var docElem = document.documentElement
	this.width = self.innerWidth || (docElem&&docElem.clientWidth) || document.body.clientWidth;
	this.height = self.innerHeight || (docElem&&docElem.clientHeight) || document.body.clientHeight;
  };

  this.getElementSize = function(elem) {
	this.width = elem.offsetWidth ||  elem.style.pixelWidth;
	this.height = elem.offsetHeight || elem.style.pixelHeight;
  };

  //this.setIBoxContent = function(str) {
	//this.clearIboxContent();
	//var e = this.getElem('ibox_content');
	//e.style.overflow = "auto";
	//e.innerHTML = str;
  //};

  this.clearIboxContent = function() {
	var e = this.getElem('ibox_content');
	e.innerHTML = "";
  };


  this.getElem = function(elemId) {
	return document.getElementById(elemId);	
  };

  // parseQuery code borrowed from thickbox, Thanks Cody!
  this.parseQuery = function(query) {
   var Params = new Object ();
   if (!query) return Params; 
   var Pairs = query.split(/[;&]/);
   for ( var i = 0; i < Pairs.length; i++ ) {
      var KeyVal = Pairs[i].split('=');
      if ( ! KeyVal || KeyVal.length != 2 ) continue;
      var key = unescape( KeyVal[0] );
      var val = unescape( KeyVal[1] );
      val = val.replace(/\+/g, ' ');
      Params[key] = val;

   }
   return Params;
  };

/********************************************************
 Make this IE7 Compatible ;)
 http://ajaxian.com/archives/ajax-on-ie-7-check-native-first
*********************************************************/
  this.createRequestObject = function() {
	var xmlhttp;
		/*@cc_on
	@if (@_jscript_version>= 5)
			try {xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
			} catch (e) {
					try {xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");}
					catch (E) {xmlhttp = false;}
			}
	@else
		xmlhttp = false;
	@end @*/
	if (!xmlhttp && typeof XMLHttpRequest != "undefined") {
			try {xmlhttp = new XMLHttpRequest();} catch (e) {xmlhttp = false;}
	}
	return xmlhttp;
  };

  this.http = this.createRequestObject();

  this.addEvent = function (obj, evType, fn){ 
   if (obj.addEventListener){ 
     obj.addEventListener(evType, fn, false); 
     return true; 
   } else if (obj.attachEvent){ 
     var r = obj.attachEvent("on"+evType, fn); 
     return r; 
   } else { 
     return false; 
   }
  }


  // Oscar Lopes some of the hacks...
  this.open = function(status, divId, params) {
	if(params==undefined) params = {height:300};
	if(this.showIbox('#'+divId,status,params)) {
		this.showBG();
		window.onscroll = this.clMaintPos;
		window.onresize = this.clMaintPos;
	}
  };

  this.waiter = function(params) {
	if(params==undefined) params = {height:300};
	params.type=4;
	if(this.showIbox('dummy','',params)) {
		this.showBG();
		window.onscroll = this.clMaintPos;
		winclMaintPosze = this.clMaintPos;
	}
  };

/**
  Stripped version of mootools bind
  This closure will bind the function passed by argument to 'this' object 
*/
  this.closure = function (func, params) {
    var obj = this;
    var result = function(event) {
      var args = params || arguments;
      var resturn = function() {
      	return func.apply(obj, args);
      };
      return resturn();
    };
    return result;
  };
    

// TODO create closures
  this.clInitIBox = this.closure(this.initIbox, []);
  this.clHideIbox = this.closure(this.hideIbox, []);
  this.clMaintPos = this.closure(this.maintPos, []);
  
  this.clOpacitiesF = new Array();
  for (var i=0;i<=this.fgOpacityLevel;i++) {
	this.clOpacitiesF[i] = this.closure(this.setOpacity, ['ibox_wrapper', i]);
  }
  this.clOpacitiesB = new Array();
  for (var i=0;i<=this.bgOpacityLevel;i++) {
	this.clOpacitiesB[i] = this.closure(this.setOpacity, ['ibox_w', i]);
  }


// hookup the event
  this.addEvent(window, 'load', this.clInitIBox);
}

