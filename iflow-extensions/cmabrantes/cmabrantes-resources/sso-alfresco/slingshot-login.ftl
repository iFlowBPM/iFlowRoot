<#import "../import/alfresco-template.ftl" as template />
<@template.header>
   <link rel="stylesheet" type="text/css" href="${url.context}/templates/login/login.css" />
   <link rel="stylesheet" type="text/css" href="${url.context}/themes/${theme}/login.css" />
</@>
<@template.body>
   <div id="alflogin" class="login-panel">
      <div class="login-logo"></div>
      <form id="loginform" accept-charset="UTF-8" method="post" action="${url.context}/login" onsubmit="authenticateiflow(); return alfLogin();">
         <fieldset>
            <div style="padding-top:96px">
               <label id="txt-username" for="username"></label>
            </div>
            <div style="padding-top:4px">
               <input type="text" id="username" name="username" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:12px">
               <label id="txt-password" for="password"></label>
            </div>
            <div style="padding-top:4px">
               <input type="password" id="password" name="password" maxlength="256" style="width:200px"/>
            </div>
            <div style="padding-top:16px">
               <input type="submit" id="btn-login" class="login-button" />
            </div>
            <div style="padding-top:32px">
               <span class="login-copyright">
                  &copy; 2005-2009 Alfresco Software Inc. All rights reserved.
               </span>
            </div>
            <input type="hidden" id="success" name="success" value="${successUrl}"/>
            <input type="hidden" name="failure" value="<#assign link><@link pageType='login'/></#assign>${url.servletContext}${link?html}&amp;error=true"/>
         </fieldset>
      </form>
   </div>
   
   <!-- iflow stuff -->
   <script type="text/javascript">

	/**
	*
	*  Base64 encode / decode
	*  http://www.webtoolkit.info/
	*
	**/
	 
	var Base64 = {
 
	// private property
	_keyStr : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",
 
	// public method for encoding
	encode : function (input) {
		var output = "";
		var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = Base64._utf8_encode(input);
 
		while (i < input.length) {
 
			chr1 = input.charCodeAt(i++);
			chr2 = input.charCodeAt(i++);
			chr3 = input.charCodeAt(i++);
 
			enc1 = chr1 >> 2;
			enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
			enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
			enc4 = chr3 & 63;
 
			if (isNaN(chr2)) {
				enc3 = enc4 = 64;
			} else if (isNaN(chr3)) {
				enc4 = 64;
			}
 
			output = output +
			this._keyStr.charAt(enc1) + this._keyStr.charAt(enc2) +
			this._keyStr.charAt(enc3) + this._keyStr.charAt(enc4);
 
		}
 
		return output;
	},
 
	// public method for decoding
	decode : function (input) {
		var output = "";
		var chr1, chr2, chr3;
		var enc1, enc2, enc3, enc4;
		var i = 0;
 
		input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
 
		while (i < input.length) {
 
			enc1 = this._keyStr.indexOf(input.charAt(i++));
			enc2 = this._keyStr.indexOf(input.charAt(i++));
			enc3 = this._keyStr.indexOf(input.charAt(i++));
			enc4 = this._keyStr.indexOf(input.charAt(i++));
 
			chr1 = (enc1 << 2) | (enc2 >> 4);
			chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
			chr3 = ((enc3 & 3) << 6) | enc4;
 
			output = output + String.fromCharCode(chr1);
 
			if (enc3 != 64) {
				output = output + String.fromCharCode(chr2);
			}
			if (enc4 != 64) {
				output = output + String.fromCharCode(chr3);
			}
 
		}
 
		output = Base64._utf8_decode(output);
 
		return output;
 
	},
 
	// private method for UTF-8 encoding
	_utf8_encode : function (string) {
		string = string.replace(/\r\n/g,"\n");
		var utftext = "";
 
		for (var n = 0; n < string.length; n++) {
 
			var c = string.charCodeAt(n);
 
			if (c < 128) {
				utftext += String.fromCharCode(c);
			}
			else if((c > 127) && (c < 2048)) {
				utftext += String.fromCharCode((c >> 6) | 192);
				utftext += String.fromCharCode((c & 63) | 128);
			}
			else {
				utftext += String.fromCharCode((c >> 12) | 224);
				utftext += String.fromCharCode(((c >> 6) & 63) | 128);
				utftext += String.fromCharCode((c & 63) | 128);
			}
 
		}
 
		return utftext;
	},
 
	// private method for UTF-8 decoding
	_utf8_decode : function (utftext) {
		var string = "";
		var i = 0;
		var c = c1 = c2 = 0;
 
		while ( i < utftext.length ) {
 
			c = utftext.charCodeAt(i);
 
			if (c < 128) {
				string += String.fromCharCode(c);
				i++;
			}
			else if((c > 191) && (c < 224)) {
				c2 = utftext.charCodeAt(i+1);
				string += String.fromCharCode(((c & 31) << 6) | (c2 & 63));
				i += 2;
			}
			else {
				c2 = utftext.charCodeAt(i+1);
				c3 = utftext.charCodeAt(i+2);
				string += String.fromCharCode(((c & 15) << 12) | ((c2 & 63) << 6) | (c3 & 63));
				i += 3;
			}
 
		}
 
		return string;
	}
 
   }

    function make_basic_auth(username, password) {
		var tok = username + ':' + password;
		var hash = Base64.encode(tok);
		return "Basic " + hash;
	}

	function ajaxhandler(xml) {
	    if (xml.readyState == 4) {
	        try {
	        	if (xml.status == 200) {
					return true;
	        	} else {
	            	return false;
	        	}
	        } catch(err) {
				return false;
	        }
	    }
	}

    function authenticateiflow() {
		// authenticate iflow
		username = document.getElementById('username').value;
		password = document.getElementById('password').value;
		var auth = make_basic_auth(username,password);
		  
		var url = 'http://instranetsrv1:8080/iflow/main.jsp';
		xml = new XMLHttpRequest();
		xml.open('GET',url, true);
		xml.onreadystatechange = function () {
			ajaxhandler(xml);
		};
			
		xml.setRequestHeader('Authorization', auth);
		xml.send();	  
   }
   </script>
   <!-- end iflow stuff -->
   
   <script type="text/javascript">//<![CDATA[

   function alfLogin()
   {
      YAHOO.util.Dom.get("btn-login").setAttribute("disabled", true);
	  
      return true;
   }
   
   YAHOO.util.Event.onContentReady("alflogin", function()
   {
      var Dom = YAHOO.util.Dom;
      
      // Prevent the Enter key from causing a double form submission
      var form = Dom.get("loginform");
      // add the event to the form and make the scope of the handler this form.
      YAHOO.util.Event.addListener(form, "submit", this._submitInvoked, this, true);
      var fnStopEvent = function(id, keyEvent)
      {
         if (form.getAttribute("alflogin") == null)
         {
            form.setAttribute("alflogin", true);
         }
         else
         {
            form.attributes.action.nodeValue = "";
         }
      }
      
      var enterListener = new YAHOO.util.KeyListener(form,
      {
         keys: YAHOO.util.KeyListener.KEY.ENTER
      }, fnStopEvent, "keydown");
      enterListener.enable();
      
      // set I18N labels
      Dom.get("txt-username").innerHTML = Alfresco.util.message("label.username") + ":";
      Dom.get("txt-password").innerHTML = Alfresco.util.message("label.password") + ":";
      Dom.get("btn-login").value = Alfresco.util.message("button.login");
      
      // generate and display main login panel
      var panel = new YAHOO.widget.Overlay(YAHOO.util.Dom.get("alflogin"), 
      {
         modal: false,
         draggable: false,
         fixedcenter: true,
         close: false,
         visible: true,
         iframe: false
      });
      panel.render(document.body);
      
      Dom.get("success").value += window.location.hash;
      Dom.get("username").focus();
   });
   
   <#if url.args["error"]??>
   Alfresco.util.PopupManager.displayPrompt(
      {
         title: Alfresco.util.message("message.loginfailure"),
         text: Alfresco.util.message("message.loginautherror")
      });
   </#if>
   //]]></script>
</@>
</body>
</html>