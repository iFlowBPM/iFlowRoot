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
<html>
<head>
<script type="text/javascript" src="../javascript/mootools.js"></script>

<script type="text/javascript">
function submit() {
	var options = {data: $('form').toQueryString(), method: 'get', update: 'result'};
	var ajax = new Ajax('series.jsp', options);
    ajax.request();
}
function enable(enable) {
	var actionEn = (enable == true) ? 'enable' : '';
	var actionDi = (enable == true) ? '' : 'disable';
	var name = $('name').value;
	var data = Object.toQueryString({'name': name, 'enable': actionEn, 'disable': actionDi});
	var options = {data: data, method: 'get', update: 'result'};
	var ajax = new Ajax('series.jsp', options);
    ajax.request();	
}
function list() {
	var data = Object.toQueryString({'list': true, 'enabled': $('enabled').checked, 'disabled': $('disabled').checked, 'new': $('new').checked, 'used': $('used').checked, 'burned': $('burned').checked});
	var options = {data: data, method: 'get', update: 'list'};
	var ajax = new Ajax('series.jsp', options);
    ajax.request();		
}
</script>
</head>
<body onLoad="list();">
<div id="list"></div>
<form name="form" id="form" onsubmit="return false;">
<p>Filtro</p>
<p>
Enabled: <input type="checkbox" name="enabled" id="enabled" />&nbsp;
Disabled: <input type="checkbox" name="disabled" id="disabled"/>&nbsp;
Nova: <input type="checkbox" name="new" id="new"/>&nbsp;
Usada: <input type="checkbox" name="used" id="used"/>&nbsp;
Queimada: <input type="checkbox" name="burned" id="burned" />&nbsp;
</p>
<p><a href="javascript:list();"> REFRESH</a></p>
<br/>
NAME: <input type="text" name="name" id="name" value="test"/>&nbsp;
PREFIX: <input type="text" name="prefix" id="prefix"/>&nbsp;
SUFFIX: <input type="text" name="suffix" id="suffix"/>&nbsp;
LENGTH: <input type="text" name="format" id="format"/>&nbsp;
START: <input type="text" name="start" id="start" value="1"/>&nbsp;
MAX:  <input type="text" name="max" id="max"/>&nbsp;

<br/>
CREATE: <input type="checkbox" name="create" id="create"/><br/>
NEXT: <input type="checkbox" name="next" id="next" checked/><br/>

<br/>
<a href="javascript:submit();"> GO </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:enable(true);"> ENABLE </a>&nbsp;&nbsp;<a href="javascript:enable(false);"> DISABLE </a>
<br/>
</form>

<p> RESULT </p>

<div id="result">
</div>

</body>
</html>
