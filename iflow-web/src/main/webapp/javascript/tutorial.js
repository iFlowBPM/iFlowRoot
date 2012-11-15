		function toggleHelpMode() {
			makeRequest('UpdateSettings/toggleHelpMode', '', toggleHelpModeCallback, 'text');
		}

		function toggleHelpModeCallback (htmltext) {
			if (htmltext.indexOf("error") < 0 && htmltext.indexOf("session-expired") < 0) {
				if ($('helpdivision')) {
					if ($('helpdivision').className == 'help_box_division')
						$('helpdivision').className = 'help_box_division hidden';
					else
						$('helpdivision').className = 'help_box_division';
				}			
			}
		}  	
					
			
		function generateOptions() {
			makeRequest('UpdateSettings/generateOptions', '', tutorialModeOnCallback, 'text');
		}
		
		function updateTutorial(optionSel, next) {
			makeRequest('UpdateSettings/updateTutorial', 'option=' + optionSel + '&next=' + next, tutorialModeOnCallback, 'text');
		}
		
		function updateTutorialCallback (htmltext) {
			if (htmltext.indexOf("error") < 0 && htmltext.indexOf("session-expired") < 0) {			
				$('select_tutorial').innerHTML = htmltext;
			}
		}  	
					
		function openTutorial (optionSel, next) {
		    if (optionSel && next) {
		       updateTutorial(optionSel, next);
		    }
			tutorialModeOn();
			showHelpDialog('admin_tutorial.htm', optionSel);
			tutorialModeOff();
		}

		function tutorialModeOn() {
			makeRequest('UpdateSettings/tutorialModeOn', '', tutorialModeOnCallback, 'text');
		}

		function tutorialModeOnCallback(htmltext) {
			if (htmltext.indexOf("error") < 0 && htmltext.indexOf("session-expired") < 0) {
			    $('tutorialdialog').innerHTML = htmltext;
				$('tutorialdialog').setStyle('opacity', '1');
				$('tutorialdialog').setStyle('display', 'block');
				$('tutorialdialog').makeDraggable();
			}
		}

		function tutorialModeOff() {
			makeRequest('UpdateSettings/tutorialModeOff', '', tutorialModeOffCallback, 'text');
		}

		function tutorialModeOffCallback(htmltext) {
			if (htmltext.indexOf("error") < 0 && htmltext.indexOf("session-expired") < 0) {			
				tutorialOff.animate();
			}
		}

		function resetTutorial() {
			updateTutorial('none','false');
			openTutorial('none','false');
		}
	
		function closeTutorial() {
		   tutorialModeOff();
		   showBubble();
		}

		// tutorial bubble
		function showBubble() {
			//bubbleOn.animate();
			//$('tutorial_bubble').setStyle('display', 'block');
		}

		function hideBubble() {
			bubbleOff.animate();
		}

	
	function showHelpDialog (id, item) {
		var params = 'id=' + id;
		if (item) {
			params = params + '&item=' + item;
		}
		makeRequest(helpDialogServlet, params, helpDialogCallback, 'text');
	}
	
	function showHelpDialogItem (id) {
		var params = 'id=' + id;
		makeRequest(helpDialogServlet + "/openHelp", params, helpDialogItemCallback, 'text');
	}
	
	function helpDialogItemCallback (htmltext) {
		$('helpdialog').innerHTML = htmltext;
		//$('helpdialog').className="testexpto";
		
		GLOBAL_helpDialog = new YAHOO.widget.Dialog("helpdialog", {
			fixedcenter : true,
			width: '800px',
			visible : false, 
			modal: false, 
			constraintoviewport : false,
			close : true,
			draggable: true
		} );
				
		GLOBAL_helpDialog.render();
		GLOBAL_helpDialog.show();
	}  	

	function helpDialogCallback (htmltext) {
		if (htmltext.indexOf("session-expired") > 0) {
			openLoginIbox();
		}
		else if (htmltext.indexOf("session-reload") > 0) {
			pageReload(gotoPersonalAccount);
		}	
		else {
			$('helpdialog').innerHTML = htmltext;
			GLOBAL_helpDialog = new YAHOO.widget.Dialog("helpdialog", {
				/*fixedcenter : true,*/
				xy: [150,10], 
				width: '400px',
				visible : false, 
				modal: false, 
				constraintoviewport : true,
				close : false,
				draggable: true
			} );
			GLOBAL_helpDialog.render();
			GLOBAL_helpDialog.show();
	
		}
	}  	
	
	// apenas chamar no inicio com showbubble. showbubble vai para o inicio
	function closeHelpDialog() {
		GLOBAL_helpDialog.hide();	
	}
	
	
	function helpNextPage() {
		var dts = document.getElementsByTagName('dt');
		var dds = document.getElementsByTagName('dd');
	
		var first = 0;
		var last = dts.length-1;
	
		var i = first;
		for (; i <= last; i++) {
			if (dts[i].className == 'active') {
				dts[i].className = 'inactive';
				dds[i].className = 'inactive';	        	
				if (i < dts.length-1) {
					dts[i+1].className = 'active';
					dds[i+1].className = 'active';
				}
				break;
			}
		}
		i++;
		if (i==last) {
			$('help_link_next').innerHTML = '';
			$('help_link_previous').innerHTML = 'anterior';
		}
		else {	
			$('help_link_next').innerHTML = 'pr�ximo';
			$('help_link_previous').innerHTML = 'anterior';
		}
	}	
	
	function helpPreviousPage() {
		var dts = document.getElementsByTagName('dt');
		var dds = document.getElementsByTagName('dd');
	
		var first = 0;
		var last = dts.length-1;
	
		var i = last;
		for (; i >= first; i--) {
			if (dts[i].className == 'active') {
				dts[i].className = 'inactive';
				dds[i].className = 'inactive';	        	
				if (i > first) {
					dts[i-1].className = 'active';
					dds[i-1].className = 'active';
				}
				break;
			}
		}
		i--;
		if (i==first) {
			$('help_link_previous').innerHTML = '';
			$('help_link_next').innerHTML = 'pr�ximo';
		}
		else {				
			$('help_link_previous').innerHTML = 'anterior';
			$('help_link_next').innerHTML = 'pr�ximo';
		}
	
	}	
	
