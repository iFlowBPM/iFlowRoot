    function initProcFrame() {
        var h1, div;
		if (document.getElementById('dados'))
			h1 = document.getElementById('dados').offsetHeight;
		else
			h1 = 0;
		if (h1 > 0) {
			div = window.parent.document.getElementById('section3_content_div');
			if(div) div.style.height = (h1 + 40)+ 'px';
		}
		//scroll to anchor
		//var anchorName = document.getElementById(document.getElementById('_button_clicked_id').value);
		//if(anchorName!=null)
		//	anchorName.scrollIntoView(true);
		//window.parent.scrollBy(x,y)				
		if(isErrorMsgNotVisible())
			window.parent.$jQuery(document.getElementById('error_msg_hidden')).dialog({
				  width: (((document.getElementById('main').offsetWidth - 10))/2),
				  position: {  my: 'left top', at: 'left top', of: document.getElementById('main')}
			});						
	}

    function isErrorMsgNotVisible()
    {
       if (window.parent.pageYOffset > (window.parent.$('section3_content_div').offsetTop + window.$('error_msg').offsetHeight))
    	   return true;
       else
    	   return false;
    }