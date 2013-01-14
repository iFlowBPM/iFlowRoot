var processAnnotationsJSP="ProcessAnnotation/process_annotations.jsp";
var callUpdateAnnotations="ProcessAnnotation/call/updateAnnotations";
var viewTaskAnnotationJSP = "ProcessAnnotation/view_task_annotation.jsp";
var processParams = '';
var initialDeadline = '';
var initialLabels = null;
var addLabels = '&addLabels=';
var removeLabels = '&removeLabels=';

function getDialogContext(extra) {
	if (extra == "dashboard")
		return "view_proc_annotation_dashboard";
	else
		return "view_proc_annotation";
}

//Show Annotation From Task List
function viewAnnotations(flowid,pid,subpid, extra) {
  var obj = document.getElementById(getDialogContext(extra));
  if (obj != null && obj.style != null && obj.style.display=='block'){
      obj.style.display='none';
      return;
  }

  hideButtonId = 'hide_annotation';
  if (extra == "dashboard") {
    hideButtonId = 'hide_annotation_dashboard';
  }

  processParams = 'flowid='+flowid+'&pid='+pid+'&subpid='+subpid+'&hidebuttonid='+hideButtonId;
  makeRequest(viewTaskAnnotationJSP, processParams, showTaskAnnotationsCallBack, 'text', getDialogContext(extra));
}

function showTaskAnnotationsCallBack(htmltext,extra) {
  if (htmltext.indexOf("session-expired") > 0) {
    openLoginIbox();
  } else if (htmltext.indexOf("session-reload") > 0) {
    pageReload(gotoPersonalAccount);
  } else {

	document.getElementById(extra).innerHTML = htmltext;

	if (extra == "view_proc_annotation_dashboard") {
		YAHOO.util.Event.onDOMReady(showAnnotationDialogDashboard);
	}
	else {
		YAHOO.util.Event.onDOMReady(showAnnotationDialogTasks);
	}
  }
}
function showAnnotationDialogTasks() {
	showAnnotationDialog("view_proc_annotation", '');
}

function showAnnotationDialogDashboard() {
	showAnnotationDialog("view_proc_annotation_dashboard");
}

function showAnnotationDialog(location) {
	annotDialog = new YAHOO.widget.Dialog(location, {
		fixedcenter : false,
		underlay: 'shadow',
		width: '360px',
		visible : true, 
		modal: false, 
		constraintoviewport : true,
		close : true,
		draggable: true
	} );
			
	annotDialog.render();
	annotDialog.show();
	
	annotDialog.setHeader("");

    YAHOO.util.Event.addListener("hide_annotation_dashboard", "click", annotDialog.hide, annotDialog, true);
	YAHOO.util.Event.addListener("hide_annotation", "click", annotDialog.hide, annotDialog, true);
}


//ShowAnnotations
var vFrom = '';
function showAnnotations(flowid,pid,subpid,from) {
  processParams = 'flowid='+flowid+'&pid='+pid+'&subpid='+subpid+'&from='+from;
  makeRequest(processAnnotationsJSP, processParams, showAnnotationsCallBack, 'text', 0);
}

function showProcessFowardAnnotations(flowid,pid,subpid,from, forwardToLabelId, forwardToLabelName) {
  processParams = 'flowid='+flowid+'&pid='+pid+'&subpid='+subpid+'&from='+from+'&forwardToLabelId='+forwardToLabelId+'&forwardToLabelName='+forwardToLabelName;
  makeRequest(processAnnotationsJSP, processParams, showEndOfProcessAnnotationsCallBack, 'text', 0);
}

function showAnnotationsCallBack(htmltext) {
  if (htmltext.indexOf("session-expired") > 0) {
    openLoginIbox();
  }
  else if (htmltext.indexOf("session-reload") > 0) {
    pageReload(gotoPersonalAccount);
  } else {
    var obj = document.getElementById('process_annotations_span_colapsed');
    if (obj != null) {
      obj.style.display='inline-block';
      obj.innerHTML = htmltext;
    }
    obj = document.getElementById('process_annotations_span_expanded');
    if (obj != null) {
      obj.style.display='inline-block';
      obj.innerHTML = htmltext;
    }
  }
  initialDeadline = document.getElementById('deadline').value;
}

function showEndOfProcessAnnotationsCallBack(htmltext) {
  if (htmltext.indexOf("session-expired") > 0) {
    openLoginIbox();
  } else if (htmltext.indexOf("session-reload") > 0) {
    pageReload(gotoPersonalAccount);
  } else {
    var open_proc_frame = document.getElementById('open_proc_frame');
    if(open_proc_frame) {
      var innerDoc = open_proc_frame.contentDocument || open_proc_frame.contentWindow.document;
      if(innerDoc) {
        obj = innerDoc.getElementById('end_process_process_annotations_span');
      }
    }

    if (obj != null) {
      obj.style.display='inline-block';
      obj.innerHTML = htmltext;
    }
  }
}

//hideAnnotations
function hideAnnotations() {
  var obj = document.getElementById('process_annotations_span_colapsed');
  if (obj != null) obj.style.display='none';
  obj = document.getElementById('process_annotations_span_expanded');
  if (obj != null) obj.style.display='none';
}

//saveProcessAnnotations
function saveProcessAnnotations(saveHistory){		
  var params = '';
  //DEADLINE
  var newDeadline = document.getElementById('deadline').value;
  initialDeadline = document.getElementById('deadlineini').value;
  
  if (newDeadline != "" && (newDeadline != initialDeadline || saveHistory))
	  params += '&deadline='+newDeadline;
  else if (newDeadline == initialDeadline)
	  params += '&deadline=nochange';
  else
	  params += '&deadline=remove';
  
  //COMMENTS
  var newcomment = document.getElementById('comment').value;
  var oldcomment = document.getElementById('old_comment').value;
  
  if (newcomment != "" && (newcomment != oldcomment || saveHistory))
	  params += '&comment='+newcomment;
  else if (newcomment == oldcomment)
	  params += '&comment=nochange';
  else
	  params += '&comment=remove';
  
  params += '&saveHistory=' + saveHistory;
  
  //LABELS
  if (saveHistory) {
    var id = 0;
    var chk = document.getElementById('checkLabel_'+id);
    while (id <= 20) {
      if (chk != null) {
        managerLabels(id, false);
        chk = document.getElementById('checkLabel_'+ (id));
      }
      id++;
    }
  }
  params += addLabels;
  params += removeLabels;
  makeRequest(callUpdateAnnotations, processParams + params, callBackFunction, 'text', 0);
  resetVars();
  menuonoff('anotacoes',1);
}

//saveForwardToProcessAnnotations
function saveForwardToProcessAnnotations(saveHistory){
  var params = '';
  var open_proc_frame = document.getElementById('open_proc_frame');
  if(open_proc_frame) {
    var innerDoc = open_proc_frame.contentDocument || open_proc_frame.contentWindow.document;
    if(innerDoc) {
      //DEADLINE
      var newDeadline = innerDoc.getElementById('deadline').value;
      initialDeadline = innerDoc.getElementById('deadlineini').value;

      if (newDeadline != "" && (newDeadline != initialDeadline || saveHistory))
        params += '&deadline='+newDeadline;
      else if (newDeadline == initialDeadline)
        params += '&deadline=nochange';
      else
        params += '&deadline=remove';

      //COMMENTS
      var newcomment = innerDoc.getElementById('comment').value;
      var oldcomment = innerDoc.getElementById('old_comment').value;

      if (newcomment != "" && (newcomment != oldcomment || saveHistory))
        params += '&comment='+newcomment;
      else if (newcomment == oldcomment)
        params += '&comment=nochange';
      else
        params += '&comment=remove';

      params += '&saveHistory=' + saveHistory;

      //LABELS
      var forwardToSelectedLabels = '&addLabels=';
      var forwardToNotSelectedLabels = '&removeLabels=';
      var id = 0;
      var chk = innerDoc.getElementById('checkLabel_'+id);
      while (id <= 20) {
        if (chk != null) {
          if (chk.checked == true){
            forwardToSelectedLabels += id + '§§§';
          } else {
            forwardToNotSelectedLabels += id + '§§§';
          }
        }
        id +=1;
        chk = innerDoc.getElementById('checkLabel_'+id);
      }

      params += forwardToSelectedLabels;
      params += forwardToNotSelectedLabels;

      var forwardToLabelId = innerDoc.getElementById('forwardToLabelId').value;
      if (forwardToLabelId != null && "" != forwardToLabelId){
        params += "&forwardToLabelId=" + forwardToLabelId;
      }

      makeRequest(callUpdateAnnotations, processParams + params, callBackFunction, 'text', 0);
    }
  }
}

function resetVars(){
   processParams = '';
   initialDeadline = '';
   initialLabels = null;
   addLabels = '&addLabels=';
   removeLabels = '&removeLabels=';
}

//callBackFunction
function callBackFunction(error) {
  if (error.indexOf("session-expired") > 0) {
    openLoginIbox();
  } else if (error.indexOf("session-reload") > 0) {
    pageReload(gotoPersonalAccount);
  } else if (error.lenght > 0) {
    alert(error);
  }
}

//managerLabels
function managerLabels(id, checkini){
	if(document.getElementById('checkLabel_'+id).checked == true){
		if(checkini)
			removeLabels = removeLabels.replace(id + '§§§','');	
		else
			addLabels += id + '§§§';

	}else{
		if(checkini)
			removeLabels += id + '§§§';
		else
			addLabels = addLabels.replace(id + '§§§','');
	}
}

//menuonoff
function menuonoff(id,oper) {
	if(document.getElementById(id) != null){
	  if(oper == 0){
	    document.getElementById(id).style.display='none';
	  }else{
	    if (document.getElementById(id).style.display=='block'){
	      document.getElementById(id).style.display='none';
	    } else{
	      document.getElementById(id).style.display='block';
	    }
	  }
	}
}
