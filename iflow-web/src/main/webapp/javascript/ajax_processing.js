
	function addHTML (html) {
		if (document.all) {
			document.body.insertAdjacentHTML('beforeEnd', html);
		}
		else if (document.createRange) {
			var range = document.createRange();
			range.setStartAfter(document.body.lastChild);
			var docFrag = range.createContextualFragment(html);
			document.body.appendChild(docFrag);
		}
		else if (document.layers) {
			var l = new Layer(window.innerWidth);
			l.document.open();
			l.document.write(html);
			l.document.close();
			l.top = document.height;
			document.height += l.document.height;
			l.visibility = 'show';
  		}
	}

	function url_encode(str) {
	    str = escape(str);
	    return str;
	}

	function url_decode(str) {
	    str = unescape(str);
	    return str;
	}

	function makeRequest(url, param, myfunction, return_type, extra) {
	    var http_request = false;
	    if (window.XMLHttpRequest) { // Mozilla, Safari, ...
	        http_request = new XMLHttpRequest();
	        if (http_request.overrideMimeType) {
	            http_request.overrideMimeType("text/xml");
	                // See note below about this line
	        }
	    } else {
	        if (window.ActiveXObject) { // IE
	            try {
	                http_request = new ActiveXObject("Msxml2.XMLHTTP");
	            }
	            catch (e) {
	                try {
	                    http_request = new ActiveXObject("Microsoft.XMLHTTP");
	                }
	                catch (e) {
	                }
	            }
	        }
	    }
	    if (!http_request) {
	        return false;
	    }
	    
	    http_request.onreadystatechange = function () {
	        ajaxprocess(myfunction, http_request, return_type, extra);
	    };
	    
	    http_request.open("POST", url, true);
	    http_request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	    http_request.send(param);
	}

	function ajaxprocess(myfunction, http_request, return_type, extra) {
	    if (http_request.readyState == 4) {
	        try {
	        	if (http_request.status == 401) {
					document.location.href = "logout.jsp";
					return false;
				}
	        	if (http_request.status == 200) {
		        	if (extra != "quiet" && document.getElementById("errormsg")) {
		            	document.getElementById("errormsg").innerHTML = "";
	            	}
	            	var xmldoc;
	            	if (return_type == "text") {
		                xmldoc = http_request.responseText;
	            	} else {
		                if (window.XMLHttpRequest) { // Mozilla, Safari, ...
	                    	xmldoc = http_request.responseXML;
	                	} else {
		                    if (window.ActiveXObject) { // IE
	                        	xmldoc = new ActiveXObject("Microsoft.XMLDOM");
	                        	xmldoc.async="false";
                                xmldoc.loadXML(http_request.responseText);
		                    }
	                	}
	            	}
	            	myfunction(xmldoc, extra);
	        	} else {
			        if (extra != "quiet" && document.getElementById("errormsg")) {
	            		document.getElementById("errormsg").innerHTML = "Ocorreu um erro no processamento da p&aacute;gina.";
	            	}
	            	return false;
	        	}
	        } catch(err) {
	        	document.location.href = "main.jsp";
				return false;
	        }
	    } else {
	        suffix = ".";
	        for (i = 0; i < http_request.readyState; i++) {
	            suffix += ".";
	        }
	        if (extra != "quiet" && document.getElementById("errormsg")) {
	        	document.getElementById("errormsg").innerHTML = "em processamento" + suffix;
	        }
	    }
	}


/**
*
* AJAX IFRAME METHOD (AIM)
* http://www.webtoolkit.info/
*
**/

AIM = {

    frame : function(c) {

        var n = 'f' + Math.floor(Math.random() * 99999);
        var d = document.createElement('DIV');
        d.innerHTML = '<iframe style="display:none" src="about:blank" id="'+n+'" name="'+n+'" onload="AIM.loaded(\''+n+'\')"></iframe>';
        document.body.appendChild(d);

        var i = document.getElementById(n);
        if (c && typeof(c.onComplete) == 'function') {
            i.onComplete = c.onComplete;
        }

        return n;
    },

    form : function(f, name) {
        f.setAttribute('target', name);
    },

    submit : function(f, c) {
        AIM.form(f, AIM.frame(c));
        if (c && typeof(c.onStart) == 'function') {
            return c.onStart();
        } else {
            return true;
        }
    },

    loaded : function(id) {
        var i = document.getElementById(id);
        if (i.contentDocument) {
            var d = i.contentDocument;
        } else if (i.contentWindow) {
            var d = i.contentWindow.document;
        } else {
            var d = window.frames[id].document;
        }
        if (d.location.href == "about:blank") {
            return;
        }

        if (typeof(i.onComplete) == 'function') {
            i.onComplete(d.body.innerHTML);
        }
    }

}

