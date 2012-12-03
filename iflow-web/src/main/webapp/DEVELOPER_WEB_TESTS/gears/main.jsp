<!--
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
-->
<%@page import="pt.iflow.api.utils.Const"%><%
String msg = "[" + new java.util.Date() + "] gears main...";
Logger.debugJsp("", "main.jsp", msg);
%>
<%@page import="pt.iflow.api.utils.Logger"%><html>
<head>
	<script type="text/javascript" src="../../javascript/mootools.js"></script>
	<script type="text/javascript" src="gears_init.js"></script>
  	<script type="text/javascript" src="ifoff.js"></script>

<script type="text/javascript">

textOut('<%=msg%>.12');

var uploadedFiles = new Array();
var filesToUpload = new Array();

function getData() {
	var datamap = {
			'flowid': '1',
			'<%=Const.ATTR_PREFIX%>var1': $('var1').getValue(), 
			'<%=Const.ATTR_PREFIX%>var2': $('var2').getValue(), 
			'<%=Const.ATTR_PREFIX%>var3': $('var3').getValue()
	};
	if (uploadedFiles && uploadedFiles.length > 0) {
		datamap['offline_files'] = uploadedFiles.join();
		uploadedFiles = new Array();	
	}
	
	var data = Object.toQueryString(datamap);

	textOut("data: " + data);
	
	return data;
}	

function submit() {
	var data = getData();
	send(data);
}

function send(data) {
	var options = {data: data, method: 'get', async: false};
	var ajax = new Ajax('<%=Const.APP_URL_PREFIX%>inicio_flow.jsp', options);
    ajax.request();		

	textOut("created process..." + Date());	
}

function uploadOfflineContent() {

	var i=0;
	for (;i < filesToUpload.length; i++) {
		uploadFile(filesToUpload[i]);
	}
	filesToUpload = new Array();
	
	textOut("files done.. going for processes");
	
	var db = prepareDB();
	var rs = db.execute('select rowid,data from ProcStartLog');
	rowid = -1;
	while (rs.isValidRow()) {
		rowid = rs.field(0);
	  	send(rs.field(1));
	  	textOut('sent content for row ' + rowid);
	  	db.execute('delete from ProcStartLog where rowid=?', [rowid]);
	  	textOut('deleted proc entry for row ' + rowid);
	  	rs.next();
	}
	rs.close();

	textOut("procs done..");	

	db.remove();

	textOut("removed database..");	
}

function submitOffline() {
	var data = getData();

	var db = prepareDB();
	db.execute('insert into ProcStartLog values (?)', [data]);
	textOut('inserted ' + data + ' into db');
}

function prepareDB() {
	var db = google.gears.factory.create('beta.database');
	db.open('db-ifoff');
	db.execute('create table if not exists ProcStartLog' +
	           ' (data text)');
	return db;
}

function openFilesCallback(files) {
	if (files.length == 0) {
  		alert('Warning: no files selected');
	}

  	var i=0;
	for (; i < files.length; i++) {
		var filename = files[i].name;
		textOut(i + ">" + filename + "[" + files[i].blob.length + "]");

		textOut("going for upload file " + filename);

		if (isOffline()) {
			uploadFileOffline(files[i]);			
		}
		else {
			uploadFile(files[i]);
		}

// XXX uploaded files should have the session id or some other id to 
// match the file with the request
		
		uploadedFiles[uploadedFiles.length] = filename;
	}   
}

function uploadFileOffline(file) {

	filesToUpload[filesToUpload.length] = file;
	return;
}

function uploadFile(file) {
	var request = google.gears.factory.create('beta.httprequest');
	var filename = file.name;
	request.open('POST', 'fileuploader.jsp?filename=' + filename);
	request.onreadystatechange = function() {
	  if (request.readyState == 4) {
	    textOut("file " + filename + " uploaded");
	  }
	};
	request.send(file.blob);
}

function delOffData() {
	var db = prepareDB();
	db.execute('delete from ProcStartLog');
}

</script>

</head>
<body onLoad="init();">

	<p></p>
	
	<div id="offswitcher">
      	<p id="go_offline" style="display:none;">
      		<button onclick="createStore()" > GO Offline </button>
      	</p>
      	<p id="go_online" style="display:none;">
  			<button onclick="removeStore(uploadOfflineContent)" > GO Online </button>
		</p>	
	</div>

	<p></p>

	<button onclick="desktop.openFiles(openFilesCallback)" > Files </button>

	<p></p>

	<form name="form" id="form" onsubmit="return false;">
		
		<input type="text" id="var1" name="var1" value="gears1"/>
		<input type="text" id="var2" name="var2" value="gears2"/>
		<input type="text" id="var3" name="var3" value="99"/>
	
	</form>
	<p></p>
		
	<div id="submit_online" style="display:none"><button  onclick="submit();return false;"> Submit online </button></div>
	<div id="submit_offline" style="display:none"><button onclick="submitOffline();return false;" > Submit offline </button></div>


</body>
</html>
