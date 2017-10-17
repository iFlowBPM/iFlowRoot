  var W;
  var gNavPage = null;
  var gNavParam = null;
  var gTabNr = null;
  var gOldTabNr = null;
  var gContentPage = null;
  var gContentParam = null;
  var gContentType = null;
  var gFlowId = null;
  var gRunMax = null;
  var GLOBAL_helpDialog;
  var GLOBAL_tutorialDialog;
  var GLOBAL_showInfoDialog;
  var GLOBAL_popupDialog;
  
  var GLOBAL_session_config = {};
  GLOBAL_session_config.sections = new Array(0);
  
  //global number of allowed tabs
  var GLOBAL_MAX_TABS = 15;
  
  //height offset for viewing frame
  var GLOBAL_HEIGHT_OFFSET_SAVE = 135;
  var GLOBAL_HEIGHT_OFFSET = 135;
  
  var page_history = {};
  
  //default page locations
  var mainJSP="main.jsp";
  var gotoPersonalAccount="GoTo?goto=personal_account.jsp";
  var processLoadJSP="process_load.jsp";
  var pingJSP="ping.jsp";
  var auditChartServlet="AuditChart";
  var gotoOrganization="GoTo?goto=organization.jsp";
  var logoServlet="Logo";
  var userDialogServlet="UserDialog";
  var helpDialogServlet="HelpDialog";
  var flowInfoServlet="FlowInfo";
  var msgHandlerJSP="msgHandler.jsp";
  var processAnnotationsJSP="ProcessAnnotation/process_annotations.jsp";
  
  //tab links 
  var mainContentJSP="main_content.jsp";
  var actividadesFiltroJSP="actividades_filtro.jsp";
  var actividadesJSP="actividades.jsp";
  var userProcsFiltroJSP="user_procs_filtro.jsp";
  var userProcsJSP="user_procs.jsp";
  var gestaoTarefasNavJSP="gestao_tarefas_nav.jsp";
  var gestaoTarefasJSP="gestao_tarefas.jsp";
  var adminNavJSP="Admin/admin_nav.jsp";
  var flowSettingsJSP="Admin/flow_settings.jsp";
  var personalAccountJSP="personal_account.jsp";
  var personalAccountNavJSP="personal_account.jsp";
  var inboxJSP="inbox.jsp";
  var rssJSP="rss.jsp";
  var helpNavJSP="help_nav.jsp";
  var helpJSP="help.jsp";
  var reportsNavJSP="Reports/reports_nav.jsp";
  var reportsJSP="Reports/proc_perf.jsp";
  
    var prev_item = new Array();
    GLOBAL_session_config.sel = new Array();
   
    function selectedItem(tabnr, item) {    
       cur_item = 'li_a_' + tabnr + "_"+ item;
       if ($((prev_item[tabnr]))) $((prev_item[tabnr])).className = 'toolTipItemLink li_link';
     if($(cur_item)) {
       $(cur_item).className = 'toolTipItemLink li_link li_selected';
       $(cur_item).blur();
     }
       GLOBAL_session_config.sel[tabnr] = item;
       prev_item[tabnr] = cur_item;
       
    }
         
  
  function getBrowserWindowHeight() {
    var myHeight = 0;
    if( typeof( window.innerWidth ) == 'number' ) {
      //Non-IE
      myHeight = window.innerHeight;
    }
    else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
      myHeight = document.documentElement.clientHeight;
    }
    else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
      //IE 4 compatible
      myHeight = document.body.clientHeight;
    }
    return myHeight; 
  }
  
  function getBrowserWindowWidth() {
    var myWidth = 0;
    if( typeof( window.innerWidth ) == 'number' ) {
      //Non-IE
      myWidth = window.innerWidth;
    } 
    else if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
      myWidth = document.documentElement.clientWidth;
    } 
    else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
      //IE 4 compatible
      myWidth = document.body.clientWidth;
    }
    return myWidth;
  }
  
  
  function backgroundResize(locId) {  
/*    imgWidth = Math.round(getBrowserWindowWidth()*0.615,0);
    bg = "transparent url(rounded?c=white&bc=e9f1f5&w=" + imgWidth + "&h=3000&shadow=true&ah=10&aw=10&sw=2&o=.3) no-repeat right bottom";
    
    $(locId).style.background = bg;
*/
  }
  
  
  function updateSize(offset) {
    for (i = 1; i <= GLOBAL_MAX_TABS; i++) {
      if (document.getElementById('section' + i + '_div')) {
        document.getElementById('section' + i + '_div').style.height=(getBrowserWindowHeight()-offset)+'px';
      }
    }
    document.getElementById('open_proc_frame').style.height=(getBrowserWindowHeight()-offset)+'px';   
    resizeProcDetail();
  }
  
  function init(css) {
    //updateCSS('Themes/default/css/iflow_std.css');
    updateCSS(css);
    doTooltip($$('#div_tabs .tab_button'), 600, 'tab-tool');  // fetch all childs of div_tabs with class tab_button
    doTooltip($$('#div_menu_link .menu_link'), 600, 'tab-tool');  // fetch all childs of div_menu_link with class menu_link
    tabber(1, mainContentJSP , 'data=procs', mainContentJSP, 'data=tasks');
    // set up processes tab
    if(orgTheme() == "classic") {
      $('section3_content_div').style.height="100%";
    } else {
      $('section3_content_div').style.height=(getBrowserWindowHeight()-(GLOBAL_HEIGHT_OFFSET*1.5))+'px';
    }
    updateMessageCount();
    GLOBAL_session_config.sel['admin'] = 13;
    GLOBAL_session_config.sel['delegations'] = 1;
    GLOBAL_session_config.sel['reports'] = 1;
  }
  
  function showButInfo() {
    document.getElementById('butinfocol').style.display='block';
    document.getElementById('butinfoexp').style.display='block';
  }
  
  function hideButInfo() {
    document.getElementById('butinfocol').style.display='none';
    document.getElementById('butinfoexp').style.display='none';
  }

  function showFlowInfoItem(flowid) {
    var open_proc_frame = document.getElementById('open_proc_frame');
    if(open_proc_frame) {
      var innerDoc = open_proc_frame.contentDocument || open_proc_frame.contentWindow.document;
      if(innerDoc) {
        var pid = innerDoc.getElementById('pid');
        if(pid) {
          var params = 'flowid=' + flowid + '&pnumber=' + pid.value;
          makeRequest(flowInfoServlet + "/internal", params, showFlowInfoItemCallback, 'text');
        }
      }
    }
  }
  
  function showFlowInfoItemCallback(htmltext) {
    $('helpdialog').innerHTML = htmltext;
    //$('helpdialog').className="testexpto";
    
    GLOBAL_showInfoDialog = new YAHOO.widget.Dialog("helpdialog", {
      fixedcenter : true,
      width: '600px',
      visible : false, 
      modal: false, 
      constraintoviewport : false,
      close : true,
      draggable: true
    } );
        
    GLOBAL_showInfoDialog.render();
    GLOBAL_showInfoDialog.show();
  }   
  
  function expand () {
    document.getElementsByTagName('html')[0].style.width='99%';
    document.getElementsByTagName('body')[0].style.width='99%';
    document.getElementById('div_header').style.display='none';
    document.getElementById('div_menu').style.display='none';
    document.getElementById('div_proc_menu_expanded').style.display='block';
    document.getElementById('div_proc_menu_colapsed').style.display='none';
    document.getElementById('link_process_help').style.display='block';
    document.getElementById('link_process_help').style.display='none';
    document.getElementById('div_main').style.height='100%';
    document.getElementById('div_main').className='main_expanded';
    document.getElementById('section3_nav_div').style.display='none';
    document.getElementById('section3_div').className='tab_body_expanded';
    document.getElementById('section3_header_div').style.height='0px';
    document.body.style.margin = '0px'; 

    if(orgTheme() == "classic") {
      document.getElementById('section3_content_div').style.height="100%";
      document.getElementById('open_proc_frame').style.height=(getBrowserWindowHeight()-2)+'px';
    } else {
      document.getElementById('section3_content_div').style.height=(getBrowserWindowHeight()-2)+'px';
      document.getElementById('open_proc_frame').style.height=(getBrowserWindowHeight()-2)+'px';
    }
    document.getElementById('section3_content_div').className='content_div_expanded';
    $('footerwrapper').setStyle('display','none');
    GLOBAL_HEIGHT_OFFSET=2;
  }
  
  function colapse() {
    GLOBAL_HEIGHT_OFFSET=GLOBAL_HEIGHT_OFFSET_SAVE;
    document.getElementsByTagName('html')[0].style.width='';
    document.getElementsByTagName('body')[0].style.width='';
    document.getElementById('div_header').style.display='block';
    document.getElementById('div_menu').style.display='block';
    document.getElementById('div_proc_menu_expanded').style.display='none';
    document.getElementById('div_proc_menu_colapsed').style.display='block';
    try{
    	document.getElementById('link_process_help').style.display='none';
    	document.getElementById('link_process_help').style.display='block';
    } catch(err){} 
    document.getElementById('div_main').style.height='auto';
    document.getElementById('div_main').className='main';   
    document.getElementById('section3_header_div').style.height='35px';
    document.getElementById('section3_nav_div').style.display='block';
    document.getElementById('section3_div').className='tab_body';
    document.body.style.margin = '10px 20px 0 20px';

    if(orgTheme() == "classic") {
      document.getElementById('section3_content_div').style.height="100%";
      document.getElementById('open_proc_frame').style.height="100%";
    } else {
          var h1;
      if (window.frames[0].document.forms[0]) {
        h1 = window.frames[0].document.forms[0].offsetHeight;
      }
      else {
        h1 = 0;
      }
      
      if (h1 > 0) {
        document.getElementById('section3_content_div').style.height = (h1 + 40)+ 'px';
        document.getElementById('open_proc_frame').style.height=(h1 + 40)+ 'px';
      }

      //document.getElementById('section3_content_div').style.height=(getBrowserWindowHeight()-(GLOBAL_HEIGHT_OFFSET*1.5))+'px';
      //document.getElementById('open_proc_frame').style.height=(getBrowserWindowHeight()-(GLOBAL_HEIGHT_OFFSET*1.5))+'px';
    }
    document.getElementById('section3_content_div').className='content_div';
    
    $('footerwrapper').setStyle('display','block');
      
  }
  
  
  function open_process (tabnr, flowid, contentpage, contentparam, runMax) {
    hidePopup();
    var scrollpos = layout.getScrollPosition().toString();
    // do the pinging...
    procCallBack = function(text, extra) {
      if (text.indexOf("session-expired") > 0) {
        gContentType = 'open-process';
        openLoginIbox();
      } 
      else if (text.indexOf("session-reload") > 0) {
        pageReload(gotoPersonalAccount);
      } 
      else { 
        gContentType = 'open-process-prep';
        gFlowId = flowid;
        tabber(3,  mainContentJSP, 'data=procs&flowid=' + flowid+"&scroll="+scrollpos+"&" + contentparam , '', '');
        myframe = document.getElementById('open_proc_frame');
        myframe.style.display = "block";
        myframe.src = processLoadJSP+'?process_url=' + escape(contentpage + "?tabnr=" + tabnr + "&" + contentparam); 
        //myframe.src = contentpage + "?tabnr=" + tabnr + "&" + contentparam;
        gContentPage = contentpage;
        gContentParam = contentparam;
  
        if(runMax) {
          expand();
        }
      }
    };
    if(gTabNr != null) gOldTabNr = gTabNr;
    gTabNr = tabnr;
    gFlowId=flowid;
    gContentPage=contentpage;
    gContentParam=contentparam;
    gRunMax=runMax;
    makeRequest(pingJSP, '', procCallBack, 'text', tabnr);
    setScrollPosition(0);
  }
  
  function open_process_search (tabnr, flowid, contentpage, contentparam, runMax) {
    // do the pinging...
    procCallBack = function(text, extra) {
      if (text.indexOf("session-expired") > 0) {
        openLoginIbox();
      } 
      else if (text.indexOf("session-reload") > 0) {
        pageReload(gotoPersonalAccount);
      } 
      else { 
        document.getElementById('advanced_search_message').style.display = "none";
        gContentType = 'open-process-prep';
        gFlowId = flowid;
        myframe = document.getElementById('open_proc_frame_search');
        myframe.style.display = "block";
        myframe.src = processLoadJSP+'?process_url=' + escape(contentpage + "?tabnr=" + tabnr + "&" + contentparam); 
        gContentPage = contentpage;
        gContentParam = contentparam;
  
        if(runMax) {
          expand();
        }
      }
    };
    if(gTabNr != null) gOldTabNr = gTabNr;
    gTabNr = tabnr;
    gFlowId=flowid;
    gContentPage=contentpage;
    gContentParam=contentparam;
    gRunMax=runMax;
    setHistory(tabnr, '', '', '', '');
    makeRequest(pingJSP, '', procCallBack, 'text', tabnr);
  }
  
  function open_process_report(tabnr, flowid, contentpage, contentparam, runMax) {
    // do the pinging...
	if(gTabNr != null) gOldTabNr = gTabNr;
	gTabNr = tabnr;
    gFlowId=flowid;
    gContentPage=contentpage;
    gContentParam=contentparam;
    gRunMax=runMax;
    setHistory(tabnr, '', '', '', '');
    tabber(tabnr,  '', '', 'Reports/proc_report_exec.jsp', contentparam);
  }

  function open_process_report_exec(contentpage, contentparam) {
    procCallBack = function(text, extra) {
      if (text.indexOf("session-expired") > 0) {
        openLoginIbox();
      } 
      else if (text.indexOf("session-reload") > 0) {
        pageReload(gotoPersonalAccount);
      } 
      else { 
        gContentType = 'open-process-prep';
        myframe = document.getElementById('open_proc_frame_report');
        myframe.src = processLoadJSP+'?process_url=' + escape(contentpage + "?tabnr=10&" + contentparam); 
        gContentPage = contentpage;
        gContentParam = contentparam;
      }
    };
    
    makeRequest(pingJSP, '', procCallBack, 'text', 10);
  }
  
  function close_process (tabnr) {
	if(gOldTabNr == 2 && gTabNr == 3){
		this.blur();
		tabber_load(2,actividadesFiltroJSP);
	} else {
		colapse();
		myframe = document.getElementById('open_proc_frame');
	    myframe.style.display = "none";
	    myframe.src = '';
	    tabber(1, mainContentJSP , 'data=procs', mainContentJSP, 'data=tasks');
	}
  }
  
  function tabber_right(tabnr, contentpage, contentparam) {
    tabnr = convert_tabnr(tabnr);
    var hist = page_history[tabnr];
    tabber(tabnr, '', hist['navparam'], contentpage, contentparam); // preserve navparam
  }
  
  function showLoading(eid) {
    if (document.getElementById(eid).innerHTML == '') {
      document.getElementById(eid).innerHTML = '<div class="info_box">loading<br><img src="images/loading.gif"/></div>';
    }
  }
  
  function convert_tabnr(tabnr) {
    if (tabnr == 'dashboard') return 1;
    else if (tabnr == 'tasks') return 2;
    else if (tabnr == 'search') return 8;
    else if (tabnr == 'processes') return 3;
    else if (tabnr == 'delegations') return 5;
    else if (tabnr == 'admin') return 4;
    else if (tabnr == 'account') return 6;
    else if (tabnr == 'help') return 7;
    else if (tabnr == 'inbox') return 11;
    else if (tabnr == 'rss') return 9;
    else if (tabnr == 'reports') return 10;
    else if (tabnr > 0 && tabnr <= GLOBAL_MAX_TABS) return tabnr;
    return 1;
  }
  
  function parse_tabnr(tabnr) {
    if (tabnr == 1) return 'dashboard';
    else if (tabnr == 2) return 'tasks';
    else if (tabnr == 8) return 'search';
    else if (tabnr == 3) return 'processes';
    else if (tabnr == 5) return 'delegations';
    else if (tabnr == 4) return 'admin';
    else if (tabnr == 6) return 'account';
    else if (tabnr == 7) return 'help';
    else if (tabnr == 9) return 'rss';
    else if (tabnr == 10) return 'reports';
    else if (tabnr == 11) return 'inbox';
    else return 'dashboard';
  }
  
  function tooltips(locId) {
    // select all elements with class toolTipImg within element with ID locId
    doTooltip($$('#'+locId+' .toolTipImg'), 600);
  
    // select all elements with class toolTipItemLink within element with ID locId
    doTooltip($$('#'+locId+' .toolTipItemLink'), 1200);
  }
  
  function doTooltip(elements, delay, className) {
    opts = {
        initialize:function(){
      this.fx = new Fx.Style(this.toolTip, 'opacity', {duration: 500, wait: false}).set(0);
    },
    onShow: function(toolTip) {
      this.fx.start(1);
    },
    onHide: function(toolTip) {
      this.fx.start(0);
    },
    maxTitleChars: 50, showDelay: delay};
    if(className) opts['className'] = className; // if className is defined, add to options
    new Tips(elements, opts);
  }
  
  function untooltips() {
    $$('.tool-tip').each (
        function (el) {
          document.body.removeChild(el);
        }
    );
  }
  
  function hidetooltips() {
    $$('.tool-tip').each (
        function (el) {
          // not pretty, but....
          el.setStyle('visibility', 'hidden');
        }
    );
  }
  
  function setHistory(tabnr, navpage, navparam, contentpage, contentparam) {
    tabnr = convert_tabnr(tabnr);
    page_history[tabnr] = {
        tabnr: tabnr,
        navpage: navpage,
        navparam: navparam,
        contentpage: contentpage,
        contentparam: contentparam,
        sessionconfig: GLOBAL_session_config
    };
  
    //alert('tabnr: '+tabnr +' navpage:' + navpage+
    //  ' navparam: '+navparam+
    //  ' contentpage: '+contentpage+
    //  ' contentparam: '+contentparam);
  }
  
  function tabber_load(tabnr, navpage) {
	tabnr = convert_tabnr(tabnr);
    var hist = page_history[tabnr];
    tabber(tabnr, navpage, hist['navparam'], hist['contentpage'], hist['contentparam']);
    GLOBAL_session_config = hist['sessionconfig'];
  }
  
  function updateSessionConfig(tabnr) {
  
    if (convert_tabnr('admin') == tabnr 
      || convert_tabnr('processes') == tabnr 
      || convert_tabnr('dashboard') == tabnr 
      || convert_tabnr('delegations') == tabnr
      || convert_tabnr('reports') == tabnr ) {

      string_tabnr = parse_tabnr(tabnr);
      if (GLOBAL_session_config.sel[string_tabnr]) {
        selectedItem(string_tabnr, GLOBAL_session_config.sel[string_tabnr]);
      }
      
      if (GLOBAL_session_config.sections && GLOBAL_session_config.sections[tabnr]) {
        for (key in GLOBAL_session_config.sections[tabnr]) {
          val = GLOBAL_session_config.sections[tabnr][key];
//          alert(key + " = " + val);
          if (val == 'colapsed'){
            // colapse
            toggleItemBox (tabnr, $(key));
          }
        }
      }
    }

  }
  
  function tabber_save(tabnr, navpage, navparam, contentpage, contentparam) {
    setHistory(tabnr, navpage, navparam, contentpage, contentparam);
    tabber(tabnr, navpage, navparam, contentpage, contentparam);
  }
  
  function tabber(tabnr, navpage, navparam, contentpage, contentparam) {
    var i=0;
  
    untooltips();
  
    tabnr = convert_tabnr(tabnr);
  
    // se o tab � o 3 : process mostra botoes
    if (tabnr == 3) {
      document.getElementById('div_proc_menu_colapsed').style.display = 'block';
    }
    else {
      document.getElementById('div_proc_menu_colapsed').style.display = 'none';
    }
  
    // se veio do open_process � open_process
    if (gContentType == 'open-process-prep') gContentType = 'open-process';
    else gContentType = null;
  
    if (navpage) {
      document.getElementById('section' + tabnr + '_nav_div').innerHTML = '';
      setTimeout("showLoading('section" + tabnr + "_nav_div')", 1000);
      navparam = prepareParams('nav', tabnr, navparam);
      registerNav (navpage, navparam, tabnr);
      makeRequest(navpage, navparam, navdisplay, 'text', tabnr);
    }
    else {
      clearNav();
      if(gTabNr != null) gOldTabNr = gTabNr;
      gTabNr = tabnr;
    }
  
    while (i++ < GLOBAL_MAX_TABS) {
      if (i != tabnr) {
        if (document.getElementById('button' + i) || document.getElementById('section' + i + '_div')) {
          mybutton = document.getElementById('button' + i);
          mybuttonspan = document.getElementById('button' + i + "_span");
          section = document.getElementById('section' + i + '_div');
          if (mybutton) {
              mybutton.className='tab_button_first';
            if (mybuttonspan) {
              mybuttonspan.className='tab_button_span'; 
            }
          }
          if (section) {
            section.style.display = 'none';
          }
        }
      }
    }
    mybutton = document.getElementById('button' + tabnr);
    mybuttonspan = document.getElementById('button' + tabnr + "_span");
    section = document.getElementById('section' + tabnr + '_div');
    if (mybutton) {
      mybutton.className='tab_button_pressed_first';
      if (mybuttonspan) {
        mybuttonspan.className='tab_button_span_pressed';
      }
    }
    section.style.display = 'block';
  
    if (contentpage) {
      document.getElementById('section' + tabnr + '_content_div').innerHTML = '';
      setTimeout("showLoading('section" + tabnr + "_content_div')", 1000);
      contentparam = prepareParams('content', tabnr, contentparam);
      registerContent (contentpage, contentparam, tabnr);
      makeRequest(contentpage, contentparam, contentdisplay, 'text', tabnr);
    }
    else {
      clearContent();
      if(gTabNr != null) gOldTabNr = gTabNr;
      gTabNr = tabnr;
    }
    //updateSize(GLOBAL_HEIGHT_OFFSET);
  }

  function prepareParams(id, tabnr, params) {
    var ret = params;
    var tabnrparam = id + 'tabnr=' + tabnr;
    if (!params)
      ret = tabnrparam;
    else if (params.indexOf(tabnrparam) == -1) {
      ret = tabnrparam + '&' + params;
    }
    return ret;
  }

  function openLoginIbox () {
    if ($('autoSaveMessage').style.display == 'none') {
      iboxlogin.open('', 'autoSaveMessage', {width:320,height:200});
      $('idpassword').addEvent('keypress', getEnterKeyHandler(doassynclogin));
      try {
        $('idpassword').focus();
      } catch (err) {
        // ignore this error.
      }
    }
  }
  
  function navdisplay(text, navid) {
    if (text.indexOf("session-expired") > 0) {
      openLoginIbox();
    }
    else if (text.indexOf("session-reload") > 0) {
      pageReload(gotoPersonalAccount);
    } 
    else { 
      var locId = 'section' + navid + '_nav_div';
      set_html(locId, text);
      // document.getElementById(locId).innerHTML = text;
      tooltips(locId);
      updateSessionConfig(navid);
    }
  }
  
  function contentdisplay(text, navid) {
    if (text.indexOf("session-expired") > 0) {
      openLoginIbox();
    }
    else if (text.indexOf("session-reload") > 0) {
      pageReload(gotoPersonalAccount);
    } 
    else { 
      var locId = 'section' + navid + '_content_div';
      set_html(locId, text);
      // document.getElementById(locId).innerHTML = text;
      tooltips(locId);
      // test
      backgroundResize(locId);
    }
  }
  
  function gotopage () {
    if (document && document.getElementById('butcolapse') &&
        document.getElementById('butcolapse').style.display == 'block') {
      colapse();
    }
    if (gContentType == 'open-process') {
      open_process(gTabNr, gFlowId, gContentPage, gContentParam, gRunMax);
    } else {
      if (gNavParam) {
        makeRequest(gNavPage, gNavParam, navdisplay, 'text', gTabNr);
      }
      if (gContentPage) { 
        makeRequest(gContentPage, gContentParam, contentdisplay, 'text', gTabNr);
      }
    }
    clearNav();
    clearContent();
    gContentType = null;
    gFlowId = null;
  }
  
  function registerNav(navpage, navparam, tabnr) {
    gNavPage = navpage;
    gNavParam = navparam;
    if(gTabNr != null) gOldTabNr = gTabNr;
    gTabNr = tabnr;
  }
  
  function clearNav() {
    gNavPage = null;
    gNavParam = null;
    gTabNr = null;
  }
  
  function registerContent(contentpage, contentparam, tabnr) {
    gContentPage = contentpage;
    gContentParam = contentparam;
    if(gTabNr != null) gOldTabNr = gTabNr;
    gTabNr = tabnr;
  }
  
  function clearContent() {
    gContentPage = null;
    gContentParam = null;
    gTabNr = null;
    gContentType = null;
  }
  
  //Note: changed encode() to encodeURIComponent(). This is how mootools builds the query string from a form element.
  function get_params(obj) {
    var getstr = "";
    for (i=0; i<obj.elements.length; i++) {
      if (obj.elements[i].tagName == "INPUT") {
        if (obj.elements[i].type == "checkbox") {
          if (obj.elements[i].checked) {
            getstr += obj.elements[i].name + "=" + encodeURIComponent(obj.elements[i].value) + "&";
          } else {
            getstr += obj.elements[i].name + "=&";
          }
        }
        else if (obj.elements[i].type == "radio") {
          if (obj.elements[i].checked) {
            getstr += obj.elements[i].name + "=" + encodeURIComponent(obj.elements[i].value) + "&";
          }
        }
        else if (obj.elements[i].type != "button") {
          getstr += obj.elements[i].name + "=" + encodeURIComponent(obj.elements[i].value) + "&";
        }
      }
      if (obj.elements[i].tagName == "SELECT") {
        var sel = obj.elements[i];
        var options = sel.options;
        for (j = 0; j < options.length; j++) {
          if (options[j].selected == true) {        
            getstr += sel.name + "=" + encodeURIComponent(options[j].value) + "&";
          }
        }
      }
    }
    return getstr;
  }
  
  function toggleall(col, size) {
  
    var chk = true;
    var cols = new Array("R","C","W","A","S");
  
    if (document.getElementById('p0_' + cols[col]).checked == true) {
      chk = false;
    }
    else {
      chk = true;
    }
  
    for (i=0; i < size; i++) {
      document.getElementById('p' + i + '_' + cols[col]).checked = chk;
    }
  }
  
  function reloadPerfChart(paramflow, paramunit, paramtime, audittype , audituserperf , serverparamflowid, serverparamunit, serverparamtime, showOffline, ts) {
    var selobj = document.getElementById(paramflow); 
    var flowid = selobj.options[selobj.selectedIndex].value;

    var unitselobj = document.getElementById(paramunit); 
    var unitsel = unitselobj.options[unitselobj.selectedIndex].value;
    
    var timeselobj = document.getElementById(paramtime); 
    var timesel = timeselobj.options[timeselobj.selectedIndex].value;
    
    var f_date_a = document.getElementById('f_date_a');
    var f_date_c = document.getElementById('f_date_c');
    var timesel = f_date_a.value + ',' + f_date_c.value;
    
    var image = document.getElementById('chart');
    var imgsrc = auditChartServlet+'?' + audittype + '=' + audituserperf + '&' + serverparamflowid+ '=' + flowid + '&' + serverparamunit + '=' + unitsel + '&' + serverparamtime+ '=' + timesel + '&show_offline='+showOffline+'&ts=' + ts; 
    image.src = imgsrc;
  }
  
  function toggleSpan(cspan) {
    customize_span = document.getElementById(cspan);
    link_search_span = document.getElementById('link_search_span');
    if (customize_span.style.display == "none") {
      customize_span.style.display = "";
      link_search_span.style.display = "none";
    }
    else {
      link_search_span.style.display = "";
      customize_span.style.display = "none";
    }
  }
  
  
  function reloadSLAChart(paramflow, paramunit, paramtime, audittype , audituserperf , serverparamflowid, serverparamunit, serverparamtime, includeOpen,showOffline, ts) {
    var selobj = document.getElementById(paramflow); 
    var flowid = selobj.options[selobj.selectedIndex].value;

    var unitselobj = document.getElementById(paramunit); 
    var unitsel = unitselobj.options[unitselobj.selectedIndex].value;
    
    var timeselobj = document.getElementById(paramtime); 
    var timesel = timeselobj.options[timeselobj.selectedIndex].value;
    
    var f_date_a = document.getElementById('f_date_a');
    var f_date_c = document.getElementById('f_date_c');
    var timesel = f_date_a.value + ',' + f_date_c.value;
  
    
    var image = document.getElementById('chart');
    var imgsrc = auditChartServlet+'?' + audittype + '=' + audituserperf + '&'+ serverparamflowid+ '=' + flowid + '&' + serverparamunit + '=' + unitsel + '&' + serverparamtime+ '=' + timesel 
    + '&include_open=' + includeOpen + '&second_graph=false&show_offline='+showOffline + '&ts=' + ts; 
    image.src = imgsrc;
    
    if(flowid && flowid > 0) {
      var image2 = document.getElementById('chart2');
      var imgsrc2 = auditChartServlet+'?' + audittype + '=' + audituserperf + '&'+ serverparamflowid+ '=' + flowid + '&' + serverparamunit + '=' + unitsel + '&' + serverparamtime+ '=' + timesel
      + '&include_open=' + includeOpen + '&second_graph=true&show_offline='+showOffline + '&ts=' + ts;
      image2.src = imgsrc2;
    }
  }

  function reloadStatsChart(paramflowid, audittype, auditstyle, paramtime, paramdate, showOffline, ts) {
    var selobj = document.getElementById(paramflowid); 
    var flowid = selobj.options[selobj.selectedIndex].value;

    var f_date_a = document.getElementById('f_date_a');
    var f_date_c = document.getElementById('f_date_c');
    var timesel = f_date_a.value + ',' + f_date_c.value;

    var image = document.getElementById('chart');
    var imgsrc = auditChartServlet + '?' + audittype + '=' + auditstyle + '&'
        + paramflowid + '=' + flowid + '&' + paramtime + '=' + timesel
        + '&show_offline='+showOffline + '&ts=' + ts;
    image.src = imgsrc;
  }
  
  function toggleContents(el) {
    document.getElementById('display_time').disabled=true;
    toggleDisabled(document.getElementById('display_date'), true);
    
    toggleDisabled(document.getElementById(el, false));
  }
  
  function toggleDisabled(el, status) {
    try {
      el.disabled = status;
    }
    catch(E){ }
    if (el.childNodes && el.childNodes.length > 0) {
      for (var x = 0; x < el.childNodes.length; x++) {
        toggleDisabled(el.childNodes[x], status);
      }
    }
  }
  
  function loadProcStats()
  {
    var el = document.getElementById("stats_interval");
    if(el && (el.value == null || el.value == "")) {
      setIntervalValue(document.getElementById("display_time").value);
    }
  }

  function toggleProcStatsDate() {
    var el = document.getElementById("display_time");
    if (el && el.value == "const.choose") {
      document.getElementById("display_date").style.display="block";
    } else {
      document.getElementById("display_date").style.display="none";
    }
  }
  
  function toggleDisplayTimeUnits() {
    var el = document.getElementById("flowid");
    if (el && el.value == "-1") {
      document.getElementById("dinamicTime").style.display="none";
    } else {
      document.getElementById("dinamicTime").style.display="block";
    }
  }
  
  function setIntervalValue(value) {
    if (value && value != 'const.choose') {
      var f_date_a = document.getElementById('f_date_a');
      var f_date_c = document.getElementById('f_date_c');
      if(f_date_a) {  
        if(f_date_c) {
          var interval = value.split(",");
          f_date_a.value = interval[0];
          f_date_c.value = interval[1];
          //document.getElementById('interval').value = value;
        }
      }
    }
  }
  
  function proc_perf_execute(ts) {
    ts=new Date().getTime();
    document.getElementById('chart').src='images/loading.gif';
    reloadPerfChart('flowid','display_units','display_time','audit_type','USER_PERFORMANCE','flowid','display_units','display_time',document.getElementById('perf_offline').checked,ts);
  }

  function proc_stats_execute(ts) {
    ts=new Date().getTime();
    document.getElementById('chart').src='images/loading.gif';
    reloadStatsChart('flowid','audit_type','PROC_STATISTICS','display_time','display_date',document.getElementById('stats_offline').checked,ts);
  }

  function proc_sla_execute(ts) {
    ts=new Date().getTime();
    document.getElementById('chart').src='images/loading.gif';
    reloadSLAChart('flowid','display_units','display_time','audit_type','PROC_SLA','flowid','display_units','display_time',document.getElementById('sla_include').checked,document.getElementById('sla_offline').checked,ts);
  }
  
  function show_help (topic) {
    var spansi = new Array(
        "help_dashboard",
        "help_tasks",
        "help_processes",
        "help_delegations",
        "help_admin",
        "help_about_iflow",
        "help_about_help",
        "help_my_processes",
        "help_notifications",
        "help_reports",
        "help_concepts_flow"
    );
  
    for (itemx = 0; itemx < spansi.length; itemx++) {
      spantopic = document.getElementById(spansi[itemx]);
      if (spantopic) {
        spantopic.className = 'topic_hidden';
      }
    }
    mytopic = document.getElementById(topic);
    if (mytopic) {
      mytopic.className = 'topic_show';
    }
  }
  
  /*
  function helpBack (topic) {
    if (topic == 'help_dashboard') {
      tabber_load(1, mainContentJSP);
    }
    else if (topic == 'help_tasks') {
      tabber_load(2,actividadesFiltroJSP);
    }
    else if (topic == 'help_my_processes') {
      tabber_load(8,userProcsFiltroJSP);
    }
    else if (topic == 'help_processes') {
      tabber_load(3,mainContentJSP);
    }
    else if (topic == 'help_delegations') {
      tabber_load(5,gestaoTarefasNavJSP);
    }
    else {
      // by default go to dashboard
      tabber_load(1, mainContentJSP);
    }
  }
  */    

  //File upload callbacks
  
  function getStartUploadCallback(divId) {
    // turn on loading...
    var theDiv = divId;
    var f = function() { 
      return true;
    };
  
    return f;
  }
  
  function getUploadCompleteCallback(msg, tabId, gotoPage, gotoParam) {
    var message = msg;
    var tab = tabId;
    var page = gotoPage;
    var params = gotoParam;
  
    var callback = function (response) {
      /* alert(message); */
      if(response.indexOf('reload') != -1 && gotoParam=='type=org') {
        pageReload(gotoOrganization);
      } else {
        if(gotoParam=='type=org') {
          $('img_org_logo').src=logoServlet+'?ts='+Math.random(); // refresh logo
        }
        tabber_right(tab, page, params);
      }
    };
    return callback;
  }
  
  function showUserDialog (userid) {
    makeRequest(userDialogServlet, 'userid=' + userid, userDialogCallback, 'text');
  }
  
  function userDialogCallback (htmltext) {
    if (htmltext.indexOf("session-expired") > 0) {
      openLoginIbox();
    }
    else if (htmltext.indexOf("session-reload") > 0) {
      pageReload(gotoPersonalAccount);
    } 
    else {
      document.getElementById('userdialog').innerHTML = htmltext;
      dialog1 = new YAHOO.widget.Dialog("userdialog", {
        width : "300px", 
        fixedcenter : false, 
        visible : false,  
        constraintoviewport : true,
        buttons : [ { text:"ok", handler:function() {this.cancel();}, isDefault:true } ]
      } );
      dialog1.render();
      dialog1.show();
    }   
  }
  
  
  //Copied from: http://www.gamedev.net/community/forums/topic.asp?topic_id=281951
  //Note: newer versions of mozilla/firefox do not allow access to clipboard.
  
  function copy_clip(text) {
    if(do_copy_clip(text)) {
      alert(messages['copy_clip_error']);
    }
  }
  
  function do_copy_clip(meintext) {
    if (window.clipboardData) {
      // the IE-way
      window.clipboardData.setData("Text", meintext);
      // Probabely not the best way to detect netscape/mozilla.
      // I am unsure from what version this is supported
    }
    else if (window.netscape) { 
  
      // This is importent but it's not noted anywhere
      try {
        netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
      } catch(err) {
        return true;
      }
  
      // create interface to the clipboard
      var clip = Components.classes['@mozilla.org/widget/clipboard;[[[[1]]]]'].createInstance(Components.interfaces.nsIClipboard);
      if (!clip) return true;
  
      // create a transferable
      var trans = Components.classes['@mozilla.org/widget/transferable;[[[[1]]]]'].createInstance(Components.interfaces.nsITransferable);
      if (!trans) return true;
  
      // specify the data we wish to handle. Plaintext in this case.
      trans.addDataFlavor('text/unicode');
  
      // To get the data from the transferable we need two new objects
      var str = new Object();
      var len = new Object();
  
      var str = Components.classes["@mozilla.org/supports-string;[[[[1]]]]"].createInstance(Components.interfaces.nsISupportsString);
  
      var copytext=meintext;
  
      str.data=copytext;
  
      trans.setTransferData("text/unicode",str,copytext.length*[[[[2]]]]);
  
      var clipid=Components.interfaces.nsIClipboard;
  
      if (!clip) return true;
  
      clip.setData(trans,null,clipid.kGlobalClipboard);
    }
    alert(messages['copy_clip_success'] + meintext);
    return false;
  }
  
  
  //Handle enter/return keys
  function getEnterKeyHandler(eventToCall) {
    var evt = eventToCall;
    var handleEnterKey = function (e) {
      if (e) {
        var keynum;
        var node;
  
        if(window.event) keynum = e.keyCode;
        else if(e.which) keynum = e.which;
        else keynum = 0; // what to do?
  
        if(keynum == 13) { // with some luck, this is return key
          if(evt) return evt();
          return true;
        }
      }
      return true;
    };
  
    return handleEnterKey;
  }
  
  
  function nextField(fieldId) {
    var fid = fieldId;
    var evt = function () {
      var elem = document.getElementById(fid);
      if(elem) elem.focus();
      return false;
    };
  
    return evt;
  }
  
  function submitForm(formName) {
    var fn = formName;
    var evt = function () {
      var elem = document[fn];
      if(elem) elem.submit();
      return false;
    };
  
    return evt;
  }
  
  
  function registerFormKey(url, key, hasPID) {
    gContentType = 'open-process';
      if(hasPID) {
        // Override some things stored in javascript
        gContentPage=url;
        gContentParam=key;
      }
  }
  
  //inject some CSS into standard browsers
  function updateCSS(css) {
    if(navigator.appName.toLowerCase().indexOf('internet explorer') == -1) {
      var _style = document.createElement('link');
      _style.setAttribute('type', 'text/css');
      _style.setAttribute('href', css);
      _style.setAttribute('rel', 'stylesheet');
      document.head.appendChild(_style);
    }
  }
  
  function setCookie( name, value ) 
  {
    Cookie.set(name, value, { duration: 15, path: '/' });
  }
  
  function pageReload(dest) {
    page = mainJSP;
    if(dest) page = dest;
    window.location.href=page;
  }
  
  notificationTimer = -1;
  
  function updateMessageCount() {
    clearTimeout(notificationTimer); // just in case
    notificationTimer = setTimeout("updateMessageCount()",5*60000);// 5 minutes from now
    makeRequest(msgHandlerJSP, 'id=0&action=C', markNotificationCallback, 'text', {id:0,action:'C'});
  }
  
  function markNotification(id,action) {
    // do stuff
    hidetooltips();
    makeRequest(msgHandlerJSP, 'id='+id+'&action='+action, markNotificationCallback, 'text', {id:id,action:action});
  }
  
  
  function markNotificationCallback(text, params) {
    if (text.indexOf("session-expired") > 0) {
      openLoginIbox();
    }
    try {
      response = Json.evaluate(text); // use mootools json
      if(response.success) {
        id = params.id;
        action = params.action;
        $('new_msg_count').innerHTML=response.count; // update new count
        switch(action) {
        case 'M':  // mark read (dashboard)
          tabber_load(1, mainContentJSP);
          break;
        case 'R':  // mark read
        case 'U':  // unmark read
        case 'D':  // delete
          tabber(6,'','',inboxJSP,'');
        }
      } else {
        // error occurred
        alert(messages.mark_msg_error);
      }
    } catch(err) {}
  }
  
  
  function getSearchQuery(selElem, jsp, div) {
    name = selElem.name;
    value = selElem.options[selElem.selectedIndex].value;
  
    elem = document.getElementById('proc_search');
    if(elem) {
      if(value == -1) elem.value = 'false';
      else elem.value = 'true';
    }
    makeRequest(jsp, name+'='+value, getSearchQueryCallback, 'text', {id:div});
  }
  
  function getSearchQueryCallback(text, params) {
    if (text.indexOf("session-expired") > 0) {
      openLoginIbox();
    }
    try {
      elem = document.getElementById(params.id);
      elem.innerHTML=text;
    } catch(err) {
    }
  }
  
  function userProcFiltrosFunc() {
    var flowid,lbl,bdy;
    flowid = $('showflowid').options[$('showflowid').selectedIndex].value;
    lbl = $('targetuser_label');
    bdy = $('targetuser_body');
    if ($(flowid) || (flowid=='-1'&&$('atLeastOneSuper').value=='true')) {
      if(lbl) lbl.style.display='';
      if(bdy) bdy.style.display=''; 
    } else {
      if(lbl) lbl.style.display='none';
      if(bdy) bdy.style.display='none';
    }
  }
  
  function updateInternalLinks(jsUrls, extraParams, evento) {
    // prepare tab links
    var query = 'hist='+GLOBAL_MAX_TABS;
    if(gContentPage) {
      query += ('&content='+escape(gContentPage));
    }
    if(gNavPage) {
      query += '&nav='+escape(gNavPage);
    }
    for(i = 0; i < GLOBAL_MAX_TABS; i++) {
      var hist = page_history[i];
      if(hist) {
        query += ('&nav_'+i+'='+escape(hist.navpage)+'&content_'+i+'='+escape(hist.contentpage));
      }
    }
    if(extraParams) {
      query += ('&'+extraParams);
    }
    // i want a synchronous request to prevent some errors
    new XHR({method:'post',async:true,onSuccess:evento}).send(jsUrls,query);
  }

  function assyncLogin(authServlet, evento) {
    login = document.getElementById('idlogin');
    loginval = login.value;
    login.onkeypress=null; // destroy event
    password = document.getElementById('idpassword');
    passval = password.value;
    password.onkeypress=null; // destroy event
    var authQuery = 'source=assync&login=' + loginval + '&password=' + passval;
    updateInternalLinks(authServlet, authQuery, evento);
  }
  
  function doassynclogin() {
    assyncLogin('AuthenticationServlet', login_return);
  }
  

  function loginReturn(text, id) {
    eval(text);
    location.reload(true);
    iboxlogin.hideIbox();
  }
  
  function login_return(text, id) {
    eval(text);
    gotopage();
    iboxlogin.hideIbox();
  }
  
  function caltasks(id, format) {
    if (!format) {
      format = "%d/%m/%Y";
    }
    var showsHourHalf = format.indexOf('%I') > -1;
    var showsHourFull = format.indexOf('%H') > -1;
    var showsHour = showsHourHalf || showsHourFull;
    var showsMinute = format.indexOf('%M') > -1;
    var showsSeconds = format.indexOf('%S') > -1;
    var showsTime = showsHour || showsMinute || showsSeconds;
    var timeFormat = "24"; // keep default 24
    if (showsHourHalf) {
      timeFormat = "12";
    }
    if (showsHourFull) {
      timeFormat = "24";
    }
    Calendar.setup({
      inputField     :    id,   // id of the input field
      ifFormat       :    format,       // format of the input field
      showsTime      :    showsTime,
      timeFormat     :    timeFormat,
      electric       :    false
    });
  }
  
  function mainResize () {
    updateSize(GLOBAL_HEIGHT_OFFSET);
  }
  
    function toggleItemBox (tabnr, item) {
        
        tabnr = convert_tabnr(tabnr);
        GLOBAL_session_config.sections[tabnr] = GLOBAL_session_config.sections[tabnr] || [];
        
      if (item.className == 'item_title_show') {
        item.className = 'item_title_hide';
        item.src = 'images/plus.png';
        $((item.id + '_body')).setStyle('display', 'none');
        GLOBAL_session_config.sections[tabnr][item.id] = 'colapsed';
      }
      else {
        item.className = 'item_title_show';
        item.src = 'images/minus.png';
        $((item.id + '_body')).setStyle('display', 'block');
        GLOBAL_session_config.sections[tabnr][item.id] = 'expanded';
      }
  }
  
    function process_detail (tabnr, thePage, flowid, pid, subpid, procStatus) {
    var scrollpos = layout.getScrollPosition().toString();
      var params = 'flowid='+flowid+'&pid='+pid+'&subpid='+subpid+'&procStatus='+procStatus+'&scroll='+scrollpos;
      tabber_right(8, thePage, params);
    }
        

    function resizeProcDetail() {
      try {
        if(!$('iframe_proc_detail')) return
        var buttonsHeight = $('buttons_proc_detail').getCoordinates().height-0;
        var containerHeight = $('section8_content_div').getCoordinates().height-0;
        var iframe_height=(containerHeight-buttonsHeight-20)+'px';
        $('iframe_proc_detail').setStyle('height',iframe_height);
      } catch(err) {
      // ignore error....
      }
    }


    /**
     * Check if caps lock is ON and warn the user
     * @param e key event
     * @return
     */
function isCapslock(e){
    e = (e) ? e : window.event;
    var charCode = false;
    if (e.which) {
        charCode = e.which;
    } else if (e.keyCode) {
        charCode = e.keyCode;
    }
    var shifton = false;
    if (e.shiftKey) {
        shifton = e.shiftKey;
    } else if (e.modifiers) {
        shifton = !!(e.modifiers & 4);
    }
    if (charCode >= 97 && charCode <= 122 && shifton) {
        return true;
    }
    if (charCode >= 65 && charCode <= 90 && !shifton) {
        return true;
    }
    return false;
}

function openReleaseNotes(type) {
   var linkopenid = type + '_link_open';
   var linkcloseid = type + '_link_close';
     var rnid = type + '_release_notes';
     var linkopen = document.getElementById(linkopenid);
     var linkclose = document.getElementById(linkcloseid);
     var rn = document.getElementById(rnid);

     if (rn) {
       rn.style.display='';
       linkopen.style.display='none';
       linkclose.style.display='';
     }
}
function closeReleaseNotes(type) {
   var linkopenid = type + '_link_open';
   var linkcloseid = type + '_link_close';
     var rnid = type + '_release_notes';
     var linkopen = document.getElementById(linkopenid);
     var linkclose = document.getElementById(linkcloseid);
     var rn = document.getElementById(rnid);

     if (rn) {
       rn.style.display='none';
       linkopen.style.display='';
       linkclose.style.display='none';
     }
}
function set_html( id, html ) {
    // For the scripts to work in IE we need some changes
    // create orphan element set HTML to
    // We need one node do get the scripts
    var getScriptsNode = document.createElement('div');
    getScriptsNode.innerHTML = '<form/>' + html;
    // ... and one to remove them
    var orphNode = document.createElement('div');
    orphNode.innerHTML = html;
    
    // get the script nodes, add them into an arrary
    var scriptNodes = getScriptsNode.getElementsByTagName('script');
    var scripts = [];
    while(scriptNodes.length) {
        // push into script array
        var node = scriptNodes[0];
        scripts.push(node.text);
        // then remove it
        node.parentNode.removeChild(node);
    }

    // remove the scripts from orphan node
    var scriptNodes = orphNode.getElementsByTagName('script');
    while(scriptNodes.length) {
        // remove it
        var node = scriptNodes[0];
        node.parentNode.removeChild(node);
    }

    // add html to place holder element (note: we are adding the html before we execute the scripts)
    document.getElementById(id).innerHTML = orphNode.innerHTML;

    // execute stored scripts
    var head = document.getElementsByTagName('head')[0];
    while(scripts.length) {
        // create script node
        var scriptNode = document.createElement('script');
        scriptNode.type = 'text/javascript';
        scriptNode.text = scripts.shift(); // add the code to the script node
        head.appendChild(scriptNode); // add it to the page
        head.removeChild(scriptNode); // then remove it
    }   
}


function proc_rpt_offline(checkbox, fn) {
  var offline = checkbox.checked;
  var flowid = $('flowid').options[$('flowid').selectedIndex].value;
  var url = 'Reports/proc_flow_list.jsp';
  var params = 'offline='+offline+'&flowid='+flowid;
  makeRequest(url, params, proc_rpt_offline_callback, 'text', fn);  
}

function proc_rpt_offline_callback(txt, fn) {
  $('flowid').innerHTML = txt;
  fn(new Date().getTime());
}

function toggle_all_cb(cb,cba) {
  if(!cb || !cba) return;
  if(cba.length) {
    for (i = 0; i < cba.length; i++) {
      cba[i].checked = cb.checked;
    }
  } else {
    cba.checked = cb.checked;
  }
}

  // Javascript Scroll Position Persistence (C)2007
 
 var layout = {
    getScrollPosition: function() {
        if (document.documentElement && document.documentElement.scrollTop)
            return document.documentElement.scrollTop; // IE6 +4.01
        if (document.body && document.body.scrollTop)
            return document.body.scrollTop; // IE5 or DTD 3.2
        return 0;
    }  
  };

  function setScrollPosition(yPosition) {
    scrollTo(0, yPosition);
  }

    function InicializeRichTextField(elementName, richTextComponentTitle, richTextComponentWidth, richTextComponentHeight){
    	CKEDITOR.replace(elementName);
    }

    function blockPopupCallerForm(){
        var form = document.getElementById('dados');
        form.innerHTML = form.innerHTML + '<div id=\'_formLoadingDiv\' style=\'width:95%;height:98%;position:absolute;left:0;top:0;z-index:99;background-color:white;display:block;opacity:0.5;\'></ div>';
    }

    function getPopupUrlParams() {
      var url = 'op=';

      var op=$('op');
      if (op!=null) url += op.value;
      else url += 3;
      var _0_MAX_ROW=$('0_MAX_ROW');
      if (_0_MAX_ROW!=null) url += '&0_MAX_ROW=' + _0_MAX_ROW.value;
      var _1_MAX_ROW=$('1_MAX_ROW');
      if (_1_MAX_ROW!=null) url += '&1_MAX_ROW=' + _1_MAX_ROW.value;
      var subpid=$('subpid');
      if (subpid!=null) url += '&subpid=' + subpid.value;
      var flowExecType=$('flowExecType');
      if (flowExecType!=null) url += '&flowExecType=' + flowExecType.value;
      var _2_MAX_ROW=$('2_MAX_ROW');
      if (_2_MAX_ROW!=null) url += '&2_MAX_ROW=' + _2_MAX_ROW.value;
      var pid=$('pid');
      if (pid!=null) url += '&pid=' + pid.value;
      var flowid=$('flowid');
      if (flowid!=null) url += '&flowid=' + flowid.value;
      var _serv_field_=$('_serv_field_');
      if (_serv_field_!=null) url += '&_serv_field_=' + _serv_field_.value;
      var curmid=$('curmid');
      if (curmid!=null) url += '&curmid=' + curmid.value;
      var popupStartBlockId=$('popupStartBlockId');
      if (popupStartBlockId!=null) url += '&popupStartBlockId=' + popupStartBlockId.value;
      var _button_clicked_id=$('_button_clicked_id');
      if (_button_clicked_id!=null) url += '&_button_clicked_id=' + _button_clicked_id.value;

      return url;
    }

    function showPopup(params, popupWidth, popupHeight) {
        var url = 'Form/form.jsp?openPopup=true&' + params;

        $('popupdialog').innerHTML = "<div class=\"hd\" style=\"visibility: inherit; height: 5%; \">Popup</div><div class=\"bd\" style=\"visibility: inherit; height: 90%; \">" +
        "<div class=\"dialogcontent\" style=\"visibility: inherit; height: 100%; \"><div id=\"helpwrapper\" class=\"help_box_wrapper\" style=\"visibility: inherit; height: 100%; \"><div id=\"helpsection\" class=\"help_box\" style=\"visibility: inherit; height: 100%; \">" +
        "<iframe onload=\"parent.calcFrameHeight('open_proc_frame_popup');\" id=\"open_proc_frame_popup\" name=\"open_proc_frame_popup\" frameborder=\"0\" scrolling=\"auto\" " +
        "marginheight=\"0\" marginwidth=\"0\" width=\"100%\" height=\"100%\" class=\"open_proc_frame\" style=\"display:block;\" src=\""+ url +"\">" +
        "</iframe></div></div></div>";

        if (popupWidth == undefined){
            popupWidth = '800px';
        }
        if (popupHeight == undefined){
            popupHeight = null;
        }

      GLOBAL_popupDialog = new YAHOO.widget.Dialog("popupdialog", {
        fixedcenter : true,
        width: popupWidth,
        height: popupHeight,
        visible : false, 
        modal: false, 
        constraintoviewport : false,
        close : true,
        draggable: true
      } );

      GLOBAL_popupDialog.cancelEvent.subscribe(
              function () {
                  var urlClose = 'Form/closePopup.jsp?' + params;
                  var myframe = parent.document.getElementById('open_proc_frame_popup');
                  myframe.style.display = "block";
                  myframe.src = urlClose;
              }
      );
      GLOBAL_popupDialog.render();
      GLOBAL_popupDialog.show();
    }

    function hidePopup() {
      if (GLOBAL_popupDialog != null)
        GLOBAL_popupDialog.hide();
    }   
    
    function menuonoff (id) {
      if (document.getElementById(id).style.display=='block')
        document.getElementById(id).style.display='none';
      else
        document.getElementById(id).style.display='block';
    }
