<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE xsl:stylesheet [
<!ENTITY nbsp "&#160;">
<!ENTITY atilde "&#227;">
<!ENTITY otilde "&#245;">
<!ENTITY Atilde "&#195;">
<!ENTITY Otilde "&#213;">
<!ENTITY aacute "&#225;">
<!ENTITY eacute "&#233;">
<!ENTITY iacute "&#237;">
<!ENTITY oacute "&#243;">
<!ENTITY uacute "&#250;">
<!ENTITY Aacute "&#193;">
<!ENTITY Eacute "&#201;">
<!ENTITY Iacute "&#205;">
<!ENTITY Oacute "&#211;">
<!ENTITY Uacute "&#218;">
<!ENTITY agrave "&#224;">
<!ENTITY egrave "&#232;">
<!ENTITY igrave "&#236;">
<!ENTITY ograve "&#242;">
<!ENTITY ugrave "&#249;">
<!ENTITY Agrave "&#192;">
<!ENTITY Egrave "&#200;">
<!ENTITY Igrave "&#204;">
<!ENTITY Ograve "&#210;">
<!ENTITY Ugrave "&#217;">
<!ENTITY ccedil "&#231;">
<!ENTITY Ccedil "&#199;">
]>
<!-- TODO: change xsl:attribute stuff to attr="{xmlattr}", like in textarea type -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:encoder="xalan://java.net.URLEncoder" version="1.0">

  <!-- This will control output format, enconding and doctype -->
  <xsl:output method="html" encoding="UTF-8" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN" doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>

  <xsl:param name="lang_string" select="'en'"/>
  <xsl:param name="country_string" select="'US'"/>
  <xsl:param name="locale_string" select="'en_US'"/>
  <xsl:param name="url_prefix" select="'/iFlow/'"/>
  <xsl:param name="theme" select="'default'"/>
  <xsl:param name="full_url_prefix" select="'/iFlow/'"/>
  <xsl:param name="use_scanner" select="'false'"/>
  <xsl:template match="/">
    <html>
      <head>
        <link rel="stylesheet" type="text/css" href="{$url_prefix}/javascript/yahoo/fonts/fonts-min.css"/>
        <link rel="stylesheet" type="text/css" href="{$url_prefix}/javascript/yahoo/container/assets/container-core.css"/>
        <link rel="stylesheet" type="text/css" href="{$url_prefix}/Themes/generic/iflow_form.css" media="all" title="iflow_form"/>
        <link rel="stylesheet" type="text/css" href="{$url_prefix}/javascript/calendar/calendar-iflow.css" media="all"/>
	    <style type="text/css">
		html {
	font-family: Verdana, Arial, sans-serif;
	font-size: 12px;
	font-weight: normal;
}

body {
/*	background-color: #e3e3e3;
	background-color: #f7f5e8;*/
}
* html body { padding-left:5px !important; }

a {
	color: #03679e;
}

a:VISITED {
    color: #03679e;
}

a:HOVER {
    color: #B5B45D;
}

#main {
	padding: 20px;
	text-align: center;
	width: 100%;
	background: white;
	border: 1px solid #bababa;
	margin: 30px auto;
	width: 94%;
	display: table;
}

* html #main { margin-left:20px; }

.error_msg {
	font-size: 12px;
	font-weight: bold;
	color: red;
	padding: 10px 0px 10px 10px;
	text-align: left;
}

.info_msg {
	font-size: 12px;
	font-weight: bold;
	color: #1E5B78;
	padding: 10px 0px 10px 10px;
	text-align: left;
}

.header_msg {
	font-size: 16px;
	font-weight: bold;
	color: #1E5B78;
	padding: 10px 0px 10px 10px;
	text-align: center;
}

.normal_text {
	font-weight: normal;
}

.blockdivision {
	border: none;
	clear: left;
}

.columndivision {
	margin: 0;
	*position: relative;
}

.columnholder {
  *position: static;
}

.txt {
	color: #444444;
}

.button {
	font-weight: bold;
	color: #444444;
}

.center {
	text-align: center;
}

.border {
	border: 1px solid #cccccc;
}

hr {
	height: 1px;
	background-color: #B5B45D;
	border: none;
}

.table_main_header , .table_sub_header  {
	font-weight: bold;
	color: #FFFFFF;
	text-decoration: none;
	height: 23px;
}

.table_main_header {
	font-size: 12px;
	background-color: red;
}

.table_sub_header {
	font-size: 11px;
	background-color: #858585;
}

.document_header {
	font-size: 11px;
	background-color: white;
        color: #c5c5c5;
}

.note {
	font-size: 10px;
	color: #2C5586;
}

.normal {
	font-size: 11px;
	color: #444444;
	font-weight: bold;
}

ol {
	list-style: none;
	text-align: right;
	clear:both;
	padding: 0;
	margin: 0;
}

ol.submit {
	text-align: center;
}

label {
	float: left;
}

.multicol {
	/*margin-left: 5px;*/
}

.multicol_first {
	margin-left: 0px;
}

.multicoltext {
	position: relative;
	border: 1px solid #bababa;
	margin: 4px 0;
	padding: 3px;
	background-color: #f7f5e8;
	font-weight: bold;
	font-size:11px;
}

div.submit {
	float: none;
	clear: both;
	width: auto;
	padding-top: 25px;
	padding-bottom: 25px;
	border: none;
	background: transparent;
	text-align: center;
}


.txtVerm {
	color: #222244;
	font-weight: bold;
	width: 100%;
}

table.arraytable {
	border-spacing: 2px;
	empty-cells: show;
	width: 100%;
	margin-top: 10px;
	font-size: 12px;
}

table.arraytable td {
	border: 1px solid #cccccc;
	margin: 0;
	padding: 2px 5px 2px 5px;
}

.filler {
	height: 11px;
}

.tab_spacer {
	border-bottom: 1px solid #aea998;
	width: 5px;
}


div.multiupload{
  border: 1px solid #aea998;
  background-color: #dad7c9;
  float:left;
  padding:3px;
}

div.multiupload div.list{
  background-color: #dad7c9;
}
div.multiupload div.list div.item{
  margin:1px;
  background-color: #f7f5e8;
  font-size: 0.8em;
  margin: 4px 0;
}
div.multiupload div.list div.item:hover{
/*  background: #ccc;*/
}
div.multiupload div.list img{
  float:left;
  margin: 3px;
  cursor:pointer;
}

.cp_main_header {
	color:black;
	font: bold 14px verdana;
	text-align:center;
	margin:0;
	padding: 2px 0px;
	border-bottom: 1px solid black;
}

.cp_top_nav {
	color:black;
	font: bold 10px verdana;
	text-align:left;
	margin:0;
	padding: 2px 10px;
}

a.cp_top_nav_link {
    color: black;
}

input {
	border: 1px solid #999;
}

input.readonly {
	background-color: #f7f5e8;
}

textarea {
	border: 1px solid #999;
        font-family:verdana;
        font-size:12px;
        font-weight:normal;
}

textarea.readonly {
	background-color: #fff;
	border: 1px solid #d0d0d0;
        font-family:verdana;
        font-size:12px;
        font-weight:bold;
}

li.field {
	margin: 2px 0;
	text-align: right;
	clear: both;
	min-height: 1.3em;
}

li.field_multicol {
	margin: 0 0 2px 0;
	text-align: right;
	clear: both;
	min-height: 1.3em;
	padding-left: 10px;
	padding-right: 10px;
	margin-left: 5px;
	margin-right:5px;
}

li.submit {
	clear: none;
	width: inherit;
}

li.textmessage,
li.textlabel,
li.textbox,
li.password {
	font-weight: bold;
	min-height:20px;
  	height:auto !important;
  	height:20px;
	padding: 5px 5px 2px 0;
	/*background-color: #e0e0e0;*/
	border-bottom: 1px solid #cccccc;
}

li.textmessage {
	border: none;	
}

li.textbox {
	padding: 5px 5px 5px 0;
}

li.datecal {
	font-weight: bold;
	/*background-color: #e0e0e0;*/
	border-bottom: 1px solid #cccccc;
	padding: 5px 5px 5px 0;	
}

li.textarea {
	font-weight: bold;
	height: auto;
	/*background-color: #e0e0e0;*/
	border-bottom: 1px solid #cccccc;
	padding: 5px 5px 5px 0;
}

li.selection,
li.arraytable,
li.image,
li.file {
	padding: 2px 5px 2px 0;
}

li.link {
	padding: 2px 5px 2px 5px;
}

li.selection {
	border-bottom: 1px solid #cccccc;
	padding: 8px 5px 2px 0;
}


li.header {
	background-color:#6C95BE;
	border-bottom:1px solid #BABABA;
	border-right:1px solid #BABABA;
	height:19px;
	padding:5px 5px 2px 0;
}

li.subheader {
	margin: 10px 0 10px 0;
	height: 17px;
	padding: 2px 5px 2px 5px;
        background-color: #e0e0e0;
}

.labelField {
	font-weight: normal;
	color: #222244;
	margin-left: 5px;
}

label.field {
	font-weight: normal;
	color: #222244;
	margin-left: 5px;
}

label.textmessage,
label.textlabel,
label.textbox,
label.password,
label.datecal,
label.textarea,
label.selection,
label.image,
label.link,
label.file {
}

label.arraytable {
	display: none;
}

label.header {
	color: white;
	font-weight: bold;
    margin: 0px 5px;
}

label.subheader {
	color: black;
	font-weight: bold;
    margin: 0px 0px;
}

.table_row_title_item {
	background-color: #ccc;
	font-weight: bold;
}

.table_row_subheader {
	background-color: #ccc;
	font-weight: bold;
}

.toolTipImg {
	padding: 0 3px;
}

.sqllabel {
	white-space: pre;
	overflow-x: auto;
}

/* IE min-height fix */
* html li.field { height: 1em; }
* html li.field_multicol { height: 1em; }
* html li.textmessage { height: 20px; }
* html li.textlabel { height: 20px; }
* html li.textbox { height: 20px; }
* html li.password { height: 20px; }

</style>

<style type="text/css">

/* ********************   ALTERACOES **************** */
	html { 
		overflow-y:hidden; 
	}
 
	table {
		border-radius:5px;
		border: solid 1px #d2d2d2;
		padding: 1px;
		/*border-spacing:10px;*/
	}

	table.arraytable td {
		/*border: none;*/
		padding:0 4px;
		padding: 5px 4px;
	}

	table.arraytable td.table_sub_header {
		padding: 2px 4px;
		color: white;
		font-size: 1.1em;
	}

	.table_main_header {
		background-color: #666;
		padding: 2px 4px;
		font-size: 1.1em;
	}

	#main {
		border-radius: 10px 10px 10px 10px;
	}

	li.subheader {
		border-bottom: 1px solid #777;
		border-right: 1px solid #777;
		margin-top:20px;
		border-radius:5px 5px 5px 5px;
		padding-left: 10px;
	}

	li.header {
		border-bottom: 1px solid #5577bb;
		border-right: 1px solid #5577bb;
		padding: 5px 5px 2px 20px;
		border-radius:5px 5px 5px 5px;
		font-size: 1.2em;
		margin-bottom:20px;
	}

</style>
  		<script type="text/javascript" src="{$url_prefix}/javascript/yahoo/yahoo-dom-event/yahoo-dom-event.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/yahoo/dragdrop/dragdrop-min.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/yahoo/container/container-min.js"/>
        <script type="text/javascript" src="{$url_prefix}/javascript/ajax_processing.js"/>
        <script type="text/javascript" src="{$url_prefix}/javascript/tabs.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/iflow_main.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/calendar/calendar.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/calendar/lang/calendar-{$lang_string}.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/calendar/calendar-setup.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/mootools.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/FormFunctions.js"/>
  		<script type="text/javascript" src="{$url_prefix}/javascript/Stickman.MultiUpload.js"/>
        <script type="text/javascript" src="{$url_prefix}/javascript/livevalidation_standalone.js"> </script>
        <script type="text/javascript" src="{$url_prefix}/Themes/{$theme}/javascript/theme.js"> </script>
  		
		<!-- Carrega codigo javacript de interaccao com a applet e prepara o ambiente -->
		<script type="text/javascript" src="{$url_prefix}/javascript/applet_functions.js"> </script>
		<script type="text/javascript">
			window.addEvent('domready', getAppletElem);
		</script>

        <xsl:text disable-output-escaping="yes">
</xsl:text>
      </head>
      <body onload="initProcFrame();">
        <div style="margin:auto;">
          <xsl:apply-templates select="form"/>
        </div>
		<div id="_formLoadingDiv" style="text-align:center;width:99%;height:99%;position:absolute;left:0;top:0;z-index:99;display:none">
			<div class="loadingDivContainer"><div class="loadingDivInfo">
				<p><xsl:value-of select="//form/loadingLabel"/></p><img src="{$url_prefix}/images/loading.gif"/>
			</div></div>
		</div>
		<script type="text/javascript">
				window.parent.onresize = function(event) {initProcFrame();};
		</script>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="form">
    <form method="post" enctype="multipart/form-data">
      <xsl:attribute name="name">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
      <xsl:attribute name="style">
        <xsl:text>width: 100%;margin:auto;</xsl:text>
      </xsl:attribute>
      <xsl:attribute name="id">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
      <xsl:attribute name="action">
        <xsl:value-of select="action/text()"/>
      </xsl:attribute>
      <xsl:apply-templates select="hidden"/>

      <div id="main">
        <xsl:apply-templates select="error"/>
        <xsl:apply-templates select="blockdivision"/>
        <xsl:if test="count(.//button) &gt; 0">
          <div class="blockdivision">
            <div class="columndivision columnholder">
              <div class="submit">
                 <xsl:apply-templates select="button"/>
              </div>
            </div>
          </div>
        </xsl:if>
      </div>
    </form>
  </xsl:template>

  <xsl:template match="blockdivision">
    <div class="blockdivision">
      <xsl:choose>
        <xsl:when test="count(./columndivision) &gt; 1">
            <xsl:apply-templates select="columndivision"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates select="columndivision"/>
        </xsl:otherwise>
      </xsl:choose>
    </div>
  </xsl:template>


  <xsl:template match="columndivision">
    <xsl:apply-templates select="hidden"/><!-- first match all hidden field -->

    <xsl:variable name="colWidth">     
    	<xsl:choose>
        	<xsl:when test="string-length(@width) &gt; 0">
	    		<xsl:value-of select="@width"/>
    		</xsl:when>
    		<xsl:otherwise>
    			<xsl:value-of select="100 div count(../columndivision[count(*) &gt; 0]) - 0"/>
    		</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
    
    <xsl:if test="count(tabdivision|field) &gt; 0">

      <!-- aplicar apenas ao caso multicoluna -->
      <xsl:choose>
        <xsl:when test="count(../columndivision) &gt; 1">
          <div class="columndivision">
            <xsl:attribute name="style">
              <xsl:text>width:</xsl:text>
              <xsl:value-of select="$colWidth"/>
              <xsl:text>%; float: left;</xsl:text>
            </xsl:attribute>
              <ol>
				<xsl:attribute name="class">
					<xsl:choose>
					  <xsl:when test="position() = '1'">
					    <xsl:text>multicol_first</xsl:text>
					  </xsl:when>
					  <xsl:otherwise>
					    <xsl:text>multicol</xsl:text>
					  </xsl:otherwise>
	                </xsl:choose>
				</xsl:attribute>
                <xsl:apply-templates select="field"/><!-- then the others -->
              </ol>
          </div>
        </xsl:when>
        <xsl:otherwise>
          <div class="columndivision">
            <xsl:choose>
              <xsl:when test="count(field) &gt; 0">
                  <xsl:if test="name(../..) = 'tab'">
                    <xsl:attribute name="class">
                    <xsl:text>innertab</xsl:text>
                 </xsl:attribute>
                  </xsl:if>
                  <ol class="fieldlist">
                    <xsl:if test="name(../..) = 'tab'">
                      <xsl:attribute name="class">
                      <xsl:text>innertab</xsl:text>
                   </xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="field"/><!-- then the others -->
                  </ol>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates select="tabdivision"/><!-- then the others -->
              </xsl:otherwise>
            </xsl:choose>
          </div>
        </xsl:otherwise>
      </xsl:choose>

    </xsl:if>
  </xsl:template>

  <xsl:template match="tabdivision">
    <div class="tab_table" style="background-color:white;z-index:1;position: relative;top:1px;">
      <xsl:attribute name="id">
  		  <xsl:text>tabholder</xsl:text><xsl:apply-templates select="id"/>
  	   </xsl:attribute>
      <xsl:for-each select="tab">
        <td class="tabspacer">
          <xsl:attribute name="id">
  				<xsl:text>tabspace</xsl:text><xsl:apply-templates select="id"/>
  			</xsl:attribute>
          &nbsp;
        </td>
        <td id="tabbutton2" style="vertical-align:bottom;">
          <xsl:attribute name="id">
  				<xsl:text>tabspace</xsl:text><xsl:apply-templates select="id"/>
  			</xsl:attribute>
          <input type="button" id="button2" class="tab_button">
            <xsl:attribute name="id">
  					<xsl:text>tabbutton</xsl:text><xsl:apply-templates select="id"/>
  				</xsl:attribute>
            <xsl:attribute name="value">
  					<xsl:apply-templates select="name"/>
  				</xsl:attribute>
            <xsl:attribute name="onClick">
  					<xsl:text>javascript:inner_tabber(</xsl:text>
  					<xsl:apply-templates select="id"/>
  					<xsl:text>,</xsl:text>
  					<xsl:apply-templates select="../id"/>
  					<xsl:text>);</xsl:text>
  					<xsl:value-of select="action/text()"/>
  				</xsl:attribute>
          </input>
        </td>
      </xsl:for-each>
      <xsl:apply-templates select="tab"/>
    </div>
    <!--  TODO obter o primeiro tab filho e chamar-lhe o tabber -->
    <script language="JavaScript" type="text/javascript">
      <!-- Configuracao da nova tab. Está propositadamente mal identado para aparecer correctamente no HTML resultante. -->
      <xsl:text>
	var tmpArray = new Array();
</xsl:text>
      <xsl:for-each select="tab">
        <xsl:text>
	tmpArray[</xsl:text>
        <xsl:value-of select="position()"/>
        <xsl:text>] = </xsl:text>
        <xsl:value-of select="id"/>
        <xsl:text>;
</xsl:text>
      </xsl:for-each>
      <xsl:text>
	_tabArray[</xsl:text>
      <xsl:value-of select="id"/>
      <xsl:text>] = tmpArray;
</xsl:text>
      <xsl:text>
	inner_tabber(</xsl:text>
      <xsl:value-of select="tab[1]/id"/>
      <xsl:text>,</xsl:text>
      <xsl:value-of select="id"/>
      <xsl:text>);
</xsl:text>

    </script>
  </xsl:template>

  <xsl:template match="tab">
    <div class="tab_panel">
      <xsl:attribute name="id">
				<xsl:text>tabsection</xsl:text><xsl:apply-templates select="id"/><xsl:text>_div</xsl:text>
			</xsl:attribute>
      <xsl:apply-templates select="blockdivision"/>
    </div>
  </xsl:template>

  <xsl:template match="field">
    <xsl:variable name="multicol" select="count(../../columndivision)"/>
    <xsl:variable name="type" select="type"/>
    <xsl:choose>
	<xsl:when test="type = 'textmessage' or type = 'textlabel' or type = 'textbox' or type = 'textlabel' or type = 'password' or type = 'datecal' or type = 'textarea' or type = 'rich_textarea' or type = 'button' or type = 'popup_field'">
    <li>
		<xsl:attribute name="class">
			<xsl:value-of select="type"/><xsl:text> </xsl:text>
			<xsl:if test="name(../../..) = 'tab'">
				<xsl:text> innertab</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$multicol &gt; 1">
					<xsl:text> field_multicol</xsl:text>
				</xsl:when>
			    <xsl:otherwise>
					<xsl:text> field</xsl:text>				
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>
	  
      <xsl:if test="type = 'textmessage'">
        <!--<label>
			<xsl:attribute name="class">
				<xsl:value-of select="type" />
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="for">
             <xsl:value-of select="variable/text()" />
          </xsl:attribute>
          <xsl:if test="string-length(align) > 0">
            <xsl:attribute name="align">
              <xsl:apply-templates select="align" />
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(cssclass) > 0">
            <xsl:attribute name="class">
              <xsl:apply-templates select="cssclass" />
            </xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select="text" />
        </label>-->
		  <xsl:if test="string-length(align) &gt; 0">
            <xsl:attribute name="align">
              <xsl:apply-templates select="align"/>
            </xsl:attribute>
            <xsl:attribute name="style">
              <xsl:text>text-align:</xsl:text><xsl:apply-templates select="align"/><xsl:text>;</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(cssclass) &gt; 0">
            <xsl:attribute name="class">
              <xsl:apply-templates select="cssclass"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select="text"/>
      </xsl:if>

      <xsl:if test="type = 'textlabel'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="for">
             <xsl:value-of select="variable/text()"/>
          </xsl:attribute>
          <xsl:apply-templates select="text"/>
        </label>
        <xsl:if test="string-length(prefix) &gt; 0">
        	<xsl:value-of select="prefix/text()"/>
        </xsl:if>
        <xsl:apply-templates select="value"/>
        <xsl:if test="string-length(suffix) &gt; 0">
        	<xsl:value-of select="suffix/text()"/>
        </xsl:if>
      </xsl:if>

      <xsl:if test="type='textbox'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="id">
              <xsl:text>label_</xsl:text><xsl:value-of select="variable/text()"/>
            </xsl:attribute>
          <xsl:attribute name="for">
       	     <xsl:value-of select="variable/text()"/>
       	  </xsl:attribute>
          <xsl:apply-templates select="text"/>
          <xsl:if test="obligatory='true'">
            <em>*</em>
          </xsl:if>
        </label>
          <xsl:choose>  
   	    <xsl:when test="disabled='true'">
		<xsl:value-of select="value/text()"/>
            </xsl:when>
            <xsl:otherwise>
		<input type="text">
			<xsl:attribute name="class">
				<xsl:text>txt</xsl:text>
				<xsl:if test="disabled='true'">
					<xsl:text> readonly</xsl:text>			
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="id">
              <xsl:value-of select="variable/text()"/>
            </xsl:attribute>
          <xsl:attribute name="name">
                 <xsl:value-of select="variable/text()"/>
               </xsl:attribute>
          <xsl:attribute name="value">
                 <xsl:value-of select="value/text()"/>
               </xsl:attribute>
          <xsl:attribute name="size">
                 <xsl:value-of select="size/text()"/>
               </xsl:attribute>
          <xsl:attribute name="maxlength">
                 <xsl:value-of select="maxlength/text()"/>
               </xsl:attribute>
          <xsl:if test="string-length(onchange) &gt; 0">
            <xsl:attribute name="onchange">
           <xsl:value-of select="onchange/text()"/>
              </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(onblur) &gt; 0">
            <xsl:attribute name="onblur">
              <xsl:value-of select="onblur/text()"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(onfocus) &gt; 0">
            <xsl:attribute name="onfocus">
              <xsl:value-of select="onfocus/text()"/>
            </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="title">
              <xsl:value-of select="tooltip/text()"/>
            </xsl:attribute>
          </input>
        </xsl:otherwise>
      </xsl:choose>
        <xsl:value-of select="suffix/text()"/>
        <script type="text/javascript">
              <xsl:choose>
                <xsl:when test="datatype='pt.iknow.datatypes.Date'">
                    var fn<xsl:value-of select="variable/text()"/> = new LiveValidation('<xsl:value-of select="variable/text()"/>', { onValid: "", validMessage: "", insertAfterWhatNode: "label_<xsl:value-of select="variable/text()"/>" });
                    fn<xsl:value-of select="variable/text()"/>.add(Validate.Presence, { failureMessage: " " })
                    fn<xsl:value-of select="variable/text()"/>.add(Validate.Format, { pattern: /[0-3][0-9]\/[0-1][0-9]/i, failureMessage: "Formato Incorrecto!" } );
                </xsl:when>
                <xsl:when test="datatype='pt.iknow.datatypes.Text'">
                  <xsl:if test="obligatory='true'">
                    var fn<xsl:value-of select="variable/text()"/> = new LiveValidation('<xsl:value-of select="variable/text()"/>', { onValid: "", validMessage: " ", insertAfterWhatNode: "label_<xsl:value-of select="variable/text()"/>" });
                    fn<xsl:value-of select="variable/text()"/>.add(Validate.Presence, { failureMessage: " " })
                  </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
              </xsl:choose>
        </script>
      </xsl:if>

      <xsl:if test="type='password'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="for">
       	     <xsl:value-of select="variable/text()"/>
       	  </xsl:attribute>
          <xsl:apply-templates select="text"/>
          <xsl:if test="obligatory='true'">
            <em>*</em>
          </xsl:if>
        </label>
        <input type="password">
			<xsl:attribute name="class">
				<xsl:text>txt</xsl:text>
				<xsl:if test="disabled='true'">
					<xsl:text> readonly</xsl:text>			
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="id">
              <xsl:value-of select="variable/text()"/>
            </xsl:attribute>
          <xsl:attribute name="name">
                 <xsl:value-of select="variable/text()"/>
               </xsl:attribute>
          <xsl:attribute name="value">
                 <xsl:value-of select="value/text()"/>
               </xsl:attribute>
          <xsl:attribute name="size">
                 <xsl:value-of select="size/text()"/>
               </xsl:attribute>
          <xsl:attribute name="maxlength">
                 <xsl:value-of select="maxlength/text()"/>
               </xsl:attribute>
          <xsl:if test="disabled='true'">
            <xsl:attribute name="disabled">
                <xsl:text>true</xsl:text>
        </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(onchange) &gt; 0">
            <xsl:attribute name="onchange">
           <xsl:value-of select="onchange/text()"/>
              </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="title">
              <xsl:value-of select="tooltip/text()"/>
            </xsl:attribute>
        </input>
        <xsl:value-of select="suffix/text()"/>
      </xsl:if>
	  
      <xsl:if test="type='datecal'">
        <xsl:variable name="date"/>
        <label>
          <xsl:attribute name="class">
            <xsl:value-of select="type"/>
            <xsl:text> field</xsl:text>
            <xsl:if test="$multicol &gt; 1">
              <xsl:text> multicol</xsl:text>
            </xsl:if>
          </xsl:attribute>
          <xsl:attribute name="for">
            <xsl:value-of select="variable/text()"/>
          </xsl:attribute>
          <xsl:apply-templates select="text"/>
          <xsl:if test="obligatory='true'">
            <em>*</em>
          </xsl:if>
        </label>
        <xsl:choose>
          <xsl:when test="disabled='true'">
            <xsl:apply-templates select="value"/>
          </xsl:when>
          <xsl:otherwise>
            <input type="text" size="12">
              <xsl:attribute name="class">
                <xsl:text>txt</xsl:text>
                <xsl:if test="disabled='true'">
                  <xsl:text> readonly</xsl:text>
                </xsl:if>
              </xsl:attribute>
              <xsl:attribute name="id">
                <xsl:value-of select="variable/text()"/>
              </xsl:attribute>
              <xsl:attribute name="name">
                <xsl:value-of select="variable/text()"/>
              </xsl:attribute>
              <xsl:attribute name="value">
                <xsl:value-of select="value/text()"/>
              </xsl:attribute>
              <xsl:attribute name="size">
                <xsl:value-of select="size/text()"/>
              </xsl:attribute>
              <xsl:attribute name="maxlength">
                <xsl:value-of select="maxlength/text()"/>
              </xsl:attribute>
              <xsl:attribute name="onmouseover">
                <xsl:text>caltasks(this.id, '</xsl:text>
                <xsl:value-of select="dateformat/text()"/>
                <xsl:text>');this.onmouseover=null;</xsl:text>
              </xsl:attribute>
              <xsl:if test="string-length(onchange_submit) &gt; 0">
                <xsl:attribute name="onChange">
                  <xsl:apply-templates select="onchange_submit"/>
                </xsl:attribute>
              </xsl:if>
            </input>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="disabled!='true'">
          <img border="0" src="{$url_prefix}/images/icon_delete.png">
            <xsl:attribute name="onclick">
              <xsl:text>javascript:document.getElementById('</xsl:text>
              <xsl:value-of select="variable/text()"/>
              <xsl:text>').value=''</xsl:text>
            </xsl:attribute>
	      </img>
	    </xsl:if>
      </xsl:if>

      <xsl:if test="type='textarea'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:attribute name="for">
             <xsl:value-of select="variable/text()"/>
          </xsl:attribute>
          <xsl:apply-templates select="text"/>
          <xsl:if test="obligatory='true'">
            <em>*</em>
          </xsl:if>
        </label>
        
        <textarea name="{variable}" rows="{rows}">
          <xsl:if test="string-length(cols) &gt; 0">
	          <xsl:attribute name="cols">
	          	<xsl:value-of select="cols/text()"/>
	          </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(width) &gt; 0">
	          <xsl:attribute name="style">
	          	<xsl:text>width:</xsl:text><xsl:value-of select="width/text()"/>
	          </xsl:attribute>
          </xsl:if>
          <xsl:attribute name="class">
			<xsl:text>txt</xsl:text>
            <xsl:if test="disabled='true' or readonly='true'">
			  <xsl:text> readonly</xsl:text>			
            </xsl:if>
		  </xsl:attribute>
          <xsl:if test="disabled='true' or readonly='true'">
            <xsl:attribute name="readonly"><xsl:text>readonly</xsl:text>
            </xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select="value/text()"/>
        </textarea>

		        <xsl:apply-templates select="suffix"/>
      </xsl:if>
		
		
							<xsl:if test="type='button'">
						<xsl:text>&nbsp;</xsl:text>
						<button align="center" valign="center" class="button" type="submit">
							<xsl:attribute name="name">
								<xsl:value-of select="name/text()"/>
							</xsl:attribute>
							<xsl:attribute name="value">
								<xsl:value-of select="id/text()"/>
							</xsl:attribute>
							<xsl:attribute name="onClick">
								<xsl:value-of select="operation/text()"/>
							</xsl:attribute>
							<xsl:attribute name="title">
								<xsl:value-of select="tooltip/text()"/>
							</xsl:attribute>
							<xsl:if test="string-length(buttonimage) &gt; 0">
								<xsl:apply-templates select="buttonimage"/>
								<br/>
							</xsl:if>
							<xsl:apply-templates select="text"/>
						</button>
						<xsl:text>&nbsp;</xsl:text>
					</xsl:if>

                    <xsl:if test="type='popup_field'">
                        <xsl:text>&nbsp;</xsl:text>
                        <button align="center" valign="center" class="button" type="submit">
                            <xsl:attribute name="name">
                                <xsl:value-of select="name/text()"/>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                                <xsl:value-of select="id/text()"/>
                             </xsl:attribute>
                            <xsl:attribute name="onClick">
                                <xsl:value-of select="operation/text()"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:value-of select="tooltip/text()"/>
                            </xsl:attribute>
                            <xsl:if test="string-length(buttonimage) &gt; 0">
                                <xsl:apply-templates select="buttonimage"/>
                                <br/>
                            </xsl:if>
                            <xsl:apply-templates select="text"/>
                        </button>
                        <xsl:text>&nbsp;</xsl:text>
                    </xsl:if>

                    <xsl:if test="type='rich_textarea'">
                        <div class="yui-skin-sam" align="left">
                            <textarea name="{variable}" rows="{rows}">
                                <xsl:attribute name="id">
                                    <xsl:value-of select="variable/text()"/>
                                    </xsl:attribute>
                                <xsl:if test="string-length(cols) &gt; 0">
                                    <xsl:attribute name="cols">
                                    <xsl:value-of select="cols/text()"/>
                                    </xsl:attribute>
                                </xsl:if>
                                <xsl:if test="string-length(width) &gt; 0">
                                    <xsl:attribute name="style">
                                    <xsl:text>width:</xsl:text><xsl:value-of select="width/text()"/>
                                    </xsl:attribute>
                                </xsl:if>
                                <xsl:attribute name="class">
                                    <xsl:text>txt</xsl:text>
                                    <xsl:if test="disabled='true' or readonly='true'">
                                    <xsl:text> readonly</xsl:text>
                                    </xsl:if>
                                    </xsl:attribute>
                                <xsl:if test="disabled='true' or readonly='true'">
                                    <xsl:attribute name="readonly"><xsl:text>readonly</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>

                                <xsl:apply-templates select="value/text()"/>
                            </textarea>
                        </div>

                        <xsl:if test="string-length(is_rich_text_area) &gt; 0">
                            <script type="text/javascript">
                                <xsl:value-of select="rich_text_area_script/text()"/>
                            </script>
                        </xsl:if>

		
		
		
		
        <xsl:apply-templates select="suffix"/>
      </xsl:if>

	  
    </li>	  
	</xsl:when>
	<xsl:otherwise>

    <li>
		<xsl:attribute name="class">
			<xsl:value-of select="type"/><xsl:text> </xsl:text>
			<xsl:if test="name(../../..) = 'tab'">
				<xsl:text> innertab</xsl:text>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$multicol &gt; 1">
					<xsl:text> field_multicol</xsl:text>
				</xsl:when>
			    <xsl:otherwise>
					<xsl:text> field</xsl:text>				
				</xsl:otherwise>
			</xsl:choose>
		</xsl:attribute>

      <xsl:if test="type = 'header'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:apply-templates select="text"/>
        </label>
      </xsl:if>

      <xsl:if test="type = 'subheader'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
          <xsl:apply-templates select="text"/>
        </label>
      </xsl:if>

      <xsl:if test="type = 'filler'"/>

      <xsl:if test="type = 'separator'">
        <hr/>
      </xsl:if>

	<xsl:if test="type='selection'">
		<label>
		<xsl:attribute name="class">
			<xsl:value-of select="type"/>
			<xsl:text> field</xsl:text>
			<xsl:if test="$multicol &gt; 1">
				<xsl:text> multicol</xsl:text>
			</xsl:if>
		</xsl:attribute>
		<xsl:attribute name="for">
			<xsl:value-of select="variable/text()"/>
		</xsl:attribute>
		<xsl:apply-templates select="text"/>
		<xsl:if test="obligatory='true'">
			<em>*</em>
		</xsl:if>
		</label>
		<select class="txt">
		<xsl:attribute name="name">
			<xsl:value-of select="variable/text()"/>
          	</xsl:attribute>
		<xsl:if test="string-length(onchange_submit) &gt; 0">
			<xsl:attribute name="onChange">
				<xsl:apply-templates select="onchange_submit"/>
			</xsl:attribute>
		</xsl:if>
		<xsl:for-each select="option">
            <option>
              <xsl:attribute name="value">
                 <xsl:value-of select="value/text()"/>
              </xsl:attribute>
              <xsl:if test="selected='yes'">
                <xsl:attribute name="selected">
                  <xsl:text>selected</xsl:text>
                </xsl:attribute>
              </xsl:if>
              <xsl:value-of select="text/text()"/>
            </option>
          </xsl:for-each>
        </select>
      </xsl:if>

      <xsl:if test="type='arraytable'">
        <label>
			<xsl:attribute name="class">
				<xsl:value-of select="type"/>
				<xsl:text> field</xsl:text>
				<xsl:if test="$multicol &gt; 1">
					<xsl:text> multicol</xsl:text>
				</xsl:if>
			</xsl:attribute>
		</label>
        <table class="arraytable">
          <xsl:variable name="rowCount" select="count(row)"/>
          <xsl:variable name="colCount" select="count(row/col)"/>

          <xsl:for-each select="row">
            <tr>
						
			<xsl:choose>
				<xsl:when test="separator='true'">
					<xsl:attribute name="class">
						<xsl:text>table_row_title_item</xsl:text>
					</xsl:attribute>
				</xsl:when>
				<xsl:when test="subheader='true'">
					<xsl:attribute name="class">
						<xsl:text>table_row_subheader</xsl:text>
					</xsl:attribute>
				</xsl:when>
                <xsl:when test="string-length(bgcolor) &gt; 0">
					<xsl:attribute name="bgcolor">
						<xsl:value-of select="bgcolor/text()"/>
	                </xsl:attribute>
	            </xsl:when>
			    <xsl:otherwise>
	                <xsl:attribute name="bgcolor">
	                    <xsl:text>#FFFFFF</xsl:text>
	         	    </xsl:attribute>
		            <xsl:attribute name="class">
						<xsl:text>txt</xsl:text>
	           	    </xsl:attribute>
	            </xsl:otherwise>
	        </xsl:choose>
			
              <xsl:for-each select="col">
                <td>
                  <xsl:attribute name="class"><xsl:text>txt </xsl:text><xsl:if test="subheader='true'"><xsl:text>table_row_subheader</xsl:text></xsl:if></xsl:attribute>
                  <xsl:attribute name="align">
         	       <xsl:value-of select="align/text()"/>
         	     </xsl:attribute>
                  <xsl:choose>
                    <xsl:when test="header='true'">
                      <xsl:choose>
                        <xsl:when test="string-length(colspan) &gt; 0">
                          <xsl:attribute name="class">
	               	     	     <xsl:text>table_main_header</xsl:text>
           	       	          </xsl:attribute>
                          <xsl:attribute name="colspan">
  	       	                     <xsl:value-of select="colspan/text()"/>
  	       	                  </xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:attribute name="class">
	               	            <xsl:text>table_sub_header</xsl:text>
           	                  </xsl:attribute>
                        </xsl:otherwise>
                      </xsl:choose>
                    </xsl:when>

                    <xsl:otherwise>
                      <xsl:choose><!-- Oscar here: As tags "gt" e "lt" e "eq" tem prioridade sobre o bgcolor -->
                        <xsl:when test="string-length(gt) &gt; 0">
                          <xsl:attribute name="class">bgcolor1</xsl:attribute>
                        </xsl:when>
                        <xsl:when test="string-length(eq) &gt; 0">
                          <xsl:attribute name="class">bgcolor1</xsl:attribute>
                        </xsl:when>
                        <xsl:when test="string-length(lt) &gt; 0">
                          <xsl:attribute name="class">bgcolor2</xsl:attribute>
                        </xsl:when>
                        <xsl:when test="string-length(bgcolor) &gt; 0">
						  <xsl:if test="count(../separator) = 0">
	                          <xsl:attribute name="bgcolor"><xsl:value-of select="bgcolor/text()"/>
	                          </xsl:attribute>
						  </xsl:if>
                        </xsl:when>
                      </xsl:choose>
                    </xsl:otherwise>
                  </xsl:choose>
                  <xsl:choose>
                    <xsl:when test="string-length(value) = 0">
                      <xsl:text>&nbsp;
													</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:apply-templates select="value"/>
                      <xsl:choose>
                        <xsl:when test="string-length(suffix) &gt; 0">
                          <xsl:apply-templates select="suffix"/>
                        </xsl:when>
                      </xsl:choose>
                    </xsl:otherwise>
                  </xsl:choose>
                </td>
              </xsl:for-each>
            </tr>
          </xsl:for-each>

          <xsl:if test="$rowCount=1">
            <tr>
              <td class="info_msg" colspan="{$colCount}" align="center">
                <xsl:text>&lt;Vazio&gt;</xsl:text>
              </td>
            </tr>
          </xsl:if>

        </table>
        <xsl:if test="string-length(extra) &gt; 0">
          <script type="text/javascript">
            <xsl:value-of select="extra/text()"/>
          </script>
        </xsl:if>
        <xsl:if test="print='true' or export='true'">
          <table border="0" width="1" height="1" align="right" cellspacing="0" cellpadding="0">
            <xsl:if test="print='true'">
              <tr width="1" height="1" align="right">
                <td width="100%" height="1" align="right">
                  <a align="right" class="note">
                    <xsl:attribute name="href">
  	       	         <xsl:text>javascript:PrintService(</xsl:text><xsl:value-of select="fieldid/text()"/><xsl:text>);</xsl:text>
  	       	       </xsl:attribute>
                    <xsl:text>Imprimir&nbsp;Tabela</xsl:text>
                  </a>
                </td>
              </tr>
            </xsl:if>
            <xsl:if test="export='true'">
              <tr width="1" height="1" align="right">
                <td width="100%" height="1" align="right">
                  <a align="right" class="note">
                    <xsl:attribute name="href">
       	       	         <xsl:text>javascript:ExportService(</xsl:text><xsl:value-of select="fieldid/text()"/><xsl:text>);</xsl:text>
       	       	       </xsl:attribute>
                    <xsl:text>Exportar&nbsp;Tabela</xsl:text>
                  </a>
                </td>
              </tr>
            </xsl:if>
          </table>
        </xsl:if>
      </xsl:if>

      <xsl:if test="type='link'">
        <xsl:attribute name="style">
            text-align:<xsl:apply-templates select="align"/>
				  </xsl:attribute>
        <a>
          <xsl:choose>
            <xsl:when test="disabled='true'">
              <xsl:attribute name="href">
                          <xsl:text>javascript:;</xsl:text>
						</xsl:attribute>
              <xsl:attribute name="disabled">true</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
              <xsl:choose>
                <xsl:when test="newwindow='true'">
                  <xsl:attribute name="href">
                              <xsl:text>javascript:;</xsl:text>
							</xsl:attribute>
                  <xsl:attribute name="onclick">
                              <xsl:text>window.open('</xsl:text><xsl:apply-templates select="href"/><xsl:text>','</xsl:text><xsl:apply-templates select="newwindowname"/><xsl:text>');</xsl:text><xsl:apply-templates select="onclick"/>
                            </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                  <xsl:attribute name="href"><xsl:apply-templates select="href"/>
														</xsl:attribute>
                  <xsl:if test="string-length(onclick) &gt; 0">
                    <xsl:attribute name="onclick"><xsl:apply-templates select="onclick"/>
															</xsl:attribute>
                  </xsl:if>
                </xsl:otherwise>
              </xsl:choose>
            </xsl:otherwise>
          </xsl:choose>
          <xsl:if test="string-length(cssclass) &gt; 0">
            <xsl:attribute name="class"><xsl:apply-templates select="cssclass"/>
											</xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(onmouseover) &gt; 0">
            <xsl:attribute name="onMouseOver">
                        <xsl:text>window.status='</xsl:text><xsl:apply-templates select="onmouseover"/><xsl:text>';return true;</xsl:text>
                      </xsl:attribute>
            <xsl:attribute name="onMouseOut">
  	                     <xsl:text>window.status='';return true;</xsl:text>
                      </xsl:attribute>
          </xsl:if>
          <xsl:apply-templates select="text"/>
        </a>
      </xsl:if>

      <xsl:if test="type='image'">
        <xsl:attribute name="valign">
              <xsl:text>top</xsl:text>
           </xsl:attribute>
        <xsl:attribute name="align">
              <xsl:text>{align}</xsl:text>
           </xsl:attribute>
        <img src="{url}" alt="{alt_text}">
          <xsl:if test="string-length(width) &gt; 0">
            <xsl:attribute name="width">
                 <xsl:value-of select="width/text()"/>
               </xsl:attribute>
          </xsl:if>
          <xsl:if test="string-length(height) &gt; 0">
            <xsl:attribute name="height">
                 <xsl:value-of select="height/text()"/>
               </xsl:attribute>
          </xsl:if>
        </img>
      </xsl:if>

    <xsl:if test="type='file'">
      <!--  FILE  -->
      <table align="center" border="0" width="100%">
        <tr align="center">
          <td>
            <table border="0" width="100%">
              <xsl:if test="has_label_row='true'">
                <tr><!-- File Headers -->
                  <xsl:if test="string-length(text) &gt; 0">
                    <td class="" nowrap="true">
                      <xsl:apply-templates select="file_label"/>
                    </td>
                  </xsl:if>
                  <xsl:if test="show_link='true'">
                    <td class="document_header" nowrap="true">
                      <xsl:apply-templates select="link_label"/>
                    </td>
                  </xsl:if>
                  <xsl:if test="show_edition='true'">
                    <td class="document_header" nowrap="true">
                      <xsl:apply-templates select="edition_label"/>
                    </td>
                  </xsl:if>
                  <xsl:if test="show_remove='true'">
                    <td class="document_header" nowrap="true">
                      <xsl:apply-templates select="remove_label"/>
                    </td>
                  </xsl:if>
                </tr>
              </xsl:if>
              
              <xsl:for-each select="file">
              <tr>
                <xsl:if test="string-length(../text) &gt; 0">
                  <td class="normal_text" align="left" valign="middle" width="100%">
                    <xsl:apply-templates select="text"/>
                  </td>
                </xsl:if>
                <xsl:if test="../show_link='true'">
                  <td class="normal_text" align="center">
                    <xsl:text>&nbsp;</xsl:text>
                    <xsl:if test="string-length(link_url) &gt; 0">
                      <a target="_blank" href="{link_url}">
                        <xsl:apply-templates select="link_text"/>
                      </a>
                    </xsl:if>
                  </td>
                </xsl:if>
                <xsl:if test="../show_edition='true' or ../file_sign_existing='true'">
                  <td align="center" valign="middle">
	                <xsl:if test="../show_edition='true'">
	                  	<xsl:choose>
							<xsl:when test="($use_scanner='true' and ../scanner_enabled='true') or (../signatureType!='' and ../signatureType!='NONE')">
			                  	<img class="toolTipImg" border="0" width="16" height="16" src="{$url_prefix}/images/icon_resync.png" alt="Substituir" title="Substituir o documento original">
									<xsl:attribute name="onclick">replaceFile('<xsl:value-of select="../variable"/>','<xsl:value-of select="id"/>','<xsl:value-of select="../signatureType"/>','<xsl:value-of select="../encryptType"/>')</xsl:attribute>
			                  	</img>
							</xsl:when>
							<xsl:otherwise>
		                    	<input type="file" name="{../variable}_upd_[{id}]" size="20"/>
							</xsl:otherwise>
						</xsl:choose>
	                </xsl:if>

					<xsl:if test="../file_sign_existing='true'">
	                  	<img class="toolTipImg" border="0" width="16" height="16" src="{$url_prefix}/images/sign.png" alt="Assinar" title="Assinar o documento original">
							<xsl:attribute name="onclick">modifyFile('<xsl:value-of select="../variable"/>','<xsl:value-of select="id"/>','<xsl:value-of select="../signatureType"/>','<xsl:value-of select="../encryptType"/>')</xsl:attribute>
	                  	</img>
					</xsl:if>
					<xsl:if test="../file_sign_existing='true' and ../file_sign_method='true'">
	                  	<img class="toolTipImg" border="0" width="16" height="16" src="{$url_prefix}/images/sign.png" alt="Assinar" title="Assinar documento">
							<xsl:attribute name="onclick">
                                window.open('signpopup.jsp?oper=NaN&amp;docid=<xsl:value-of select="id"/>','Janela','toolbar=no,location=no,status=no,menubar=no,scrollbars=NO,resizable=NO,width=320,height=507'); return false;
							</xsl:attribute>
	                  	</img>
					</xsl:if>
					
                  	</td>

                </xsl:if>
                <xsl:if test="../add_enabled='true' and ../show_edition!='true'">
                  <td align="center" valign="middle"> <!-- add files is enabled but edit is disabled -->
                    &nbsp;
                  </td>
                </xsl:if>
                <xsl:if test="../show_remove='true'">
                  <td align="center" valign="middle">
                    <input type="checkbox" name="{../variable}_rem_[{id}]" value="true">
                      <xsl:if test="../onclick!=''">
                        <xsl:attribute name="onclick">
                          <xsl:value-of select="../onclick"/>
                        </xsl:attribute>
                      </xsl:if>
                    </input>
                  </td>
                </xsl:if>
                <td>
                 <xsl:if test="asSignatures='true'">
					<img class="toolTipImg" id="lock_{id}" border="0" width="16" height="16" src="{$url_prefix}/images/lock.png" alt="Assinado" title="Este documento já foi assinado">
					</img>
				 </xsl:if>
				 <xsl:if test="asSignatures!='true'">
					<img class="toolTipImg" id="lock_{id}" border="0" width="16" style="display:none" height="16" src="{$url_prefix}/images/lock.png" alt="Assinado" title="Este documento já foi assinado">
					</img>
				 </xsl:if>
               </td>
              </tr>
              </xsl:for-each>
              
              <xsl:if test="upload_enabled='true'"><!-- If edit is enabled, add more files -->
              <tr>
                  <td class="normal_text" align="left" valign="middle" width="100%">
                    <xsl:if test="show_link='true'">
                      <xsl:attribute name="colspan">2</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="upload_label"/>
                  </td>
                  <td align="center" valign="middle">
                    <xsl:if test="show_remove='true'">
                      <xsl:attribute name="colspan">2</xsl:attribute>
                    </xsl:if>
					<xsl:choose>
						<xsl:when test="($use_scanner='true' and scanner_enabled='true') or (signatureType!='' and signatureType!='NONE')">
							<div class="multiupload">
								<xsl:if test="$use_scanner='true' and scanner_enabled='true'">
		                    	<input id="scanfile" name="scanfile" type="button" value="Digitalizar" class="button">
									<xsl:attribute name="onClick">scanFile('<xsl:value-of select="variable"/>','<xsl:value-of select="signatureType"/>','<xsl:value-of select="encryptType"/>',<xsl:value-of select="upload_limit"/>)</xsl:attribute>
								</input>
								</xsl:if>
								<input id="loadfile" name="loadfile" type="button" value="Carregar" class="button">
									<xsl:attribute name="onclick">uploadFile('<xsl:value-of select="variable"/>','<xsl:value-of select="signatureType"/>','<xsl:value-of select="encryptType"/>',<xsl:value-of select="upload_limit"/>)</xsl:attribute>
								</input>
								<div class="list">
									<xsl:attribute name="id">list_<xsl:value-of select="variable"/></xsl:attribute>
								</div>
							</div>
							<SCRIPT LANGUAGE="JavaScript" type="text/javascript">
								checkAppletButtons('<xsl:value-of select="variable"/>');
							</SCRIPT>
						</xsl:when>
						<xsl:otherwise>
							<div id="{variable}_add_container">
						   		<input type="file" name="{variable}_add" size="20">
						   			<xsl:if test="accept!=''">
						   				<xsl:attribute name="accept">
						   					<xsl:value-of select="accept"/>
					   					</xsl:attribute>
					   				</xsl:if>
						   		</input>
								<script language="JavaScript" type="text/javascript">
									window.addEvent('domready', function() {
										new MultiUpload( $( '<xsl:value-of select="/form/name"/>' ).<xsl:value-of select="variable"/>_add, <xsl:value-of select="upload_limit"/>, '_[{id}]', true, true );
										// IE6 fix
										if (/msie|MSIE 6/.test(navigator.userAgent)) {
											$('<xsl:value-of select="variable"/>_add_container').style.paddingRight = '252px';
										}
									});
		                    	</script>
							</div>
						</xsl:otherwise>
					</xsl:choose>
                  </td>                  
              </tr>
              </xsl:if>
            </table>
          </td>
        </tr>
      </table>
    </xsl:if>

    </li>
	</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="button">
    <xsl:text>&nbsp;
		</xsl:text>
    <button align="center" valign="center" class="button" type="submit">
      <xsl:attribute name="name">
      <xsl:value-of select="name/text()"/>
    </xsl:attribute>
      <xsl:attribute name="value">
      <xsl:value-of select="id/text()"/>
    </xsl:attribute>
	<xsl:attribute name="onClick">
		<xsl:value-of select="operation/text()"/>
	</xsl:attribute>
    <xsl:attribute name="title">
      <xsl:value-of select="tooltip/text()"/>
    </xsl:attribute>
      <xsl:if test="string-length(buttonimage) &gt; 0">
        <xsl:apply-templates select="buttonimage"/>
        <br/>
      </xsl:if>
      <xsl:apply-templates select="text"/>
    </button>
    <xsl:text>&nbsp;
		</xsl:text>
  </xsl:template>

  <xsl:template match="buttonimage">
    <img src="{src}">
      <xsl:attribute name="alt">
      <xsl:apply-templates select="alt"/>
    </xsl:attribute>
    </img>
  </xsl:template>


  <xsl:template match="error">
    <div class="error_msg">
      <xsl:value-of select="text" disable-output-escaping="yes"/>
    </div>
  </xsl:template>

  <xsl:template match="hidden">
    <input type="hidden">
      <xsl:attribute name="name">
      <xsl:value-of select="name/text()"/>
    </xsl:attribute>
      <xsl:attribute name="id">
      <xsl:value-of select="name/text()"/>
    </xsl:attribute>
      <xsl:attribute name="value">
      <xsl:value-of select="value/text()"/>
    </xsl:attribute>
    </input><xsl:text><![CDATA[
    ]]></xsl:text>
  </xsl:template>


  <xsl:template match="input">
    <xsl:if test="type = 'checkbox'">
      <input type="checkbox" class="txt">
        <xsl:attribute name="name">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
        <xsl:attribute name="value">
        <xsl:value-of select="value/text()"/>
      </xsl:attribute>
        <xsl:if test="checked='true'">
          <xsl:attribute name="checked">true</xsl:attribute>
        </xsl:if>
        <xsl:if test="disabled='true'">
          <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="title">
        <xsl:value-of select="tooltip/text()"/>
      </xsl:attribute>
      </input>
    </xsl:if>
    <xsl:if test="type = 'radio'">
      <input type="radio" class="txt">
        <xsl:attribute name="name">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
        <xsl:attribute name="value">
        <xsl:value-of select="value/text()"/>
      </xsl:attribute>
        <xsl:if test="checked='true'">
          <xsl:attribute name="checked">true</xsl:attribute>
        </xsl:if>
        <xsl:if test="disabled='true'">
          <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
        <xsl:attribute name="title">
        <xsl:value-of select="tooltip/text()"/>
      </xsl:attribute>
      </input>
    </xsl:if>
    <xsl:if test="type = 'hidden'">
      <input type="hidden">
        <xsl:attribute name="name">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
        <xsl:attribute name="id">
        <xsl:value-of select="name/text()"/>
      </xsl:attribute>
        <xsl:attribute name="value">
        <xsl:value-of select="value/text()"/>
      </xsl:attribute>
      </input>
    </xsl:if>
    <xsl:if test="type = 'tabletext'">
      <input type="text" class="txt">
        <xsl:attribute name="name">
        <xsl:value-of select="variable/text()"/>
      </xsl:attribute>
        <xsl:attribute name="id">
        <xsl:value-of select="variable/text()"/>
      </xsl:attribute>
        <xsl:attribute name="value">
        <xsl:value-of select="value/text()"/>
      </xsl:attribute>
        <xsl:attribute name="size">
        <xsl:value-of select="size/text()"/>
      </xsl:attribute>
        <xsl:attribute name="maxlength">
        <xsl:value-of select="maxlength/text()"/>
      </xsl:attribute>
        <xsl:attribute name="title">
        <xsl:value-of select="tooltip/text()"/>
      </xsl:attribute>
        <xsl:if test="readonly='true'">
          <xsl:attribute name="disabled">true</xsl:attribute>
        </xsl:if>
        <xsl:if test="string-length(onchange) &gt; 0">
          <xsl:attribute name="onchange">
 		          <xsl:value-of select="onchange/text()"/>
  	      </xsl:attribute>
        </xsl:if>
        <xsl:if test="string-length(onblur) &gt; 0">
          <xsl:attribute name="onblur">
            <xsl:value-of select="onblur/text()"/>
          </xsl:attribute>
        </xsl:if>
        <xsl:if test="string-length(onfocus) &gt; 0">
          <xsl:attribute name="onfocus">
            <xsl:value-of select="onfocus/text()"/>
          </xsl:attribute>
        </xsl:if>
      </input>
    </xsl:if>
    <xsl:if test="type='tableselection'">
      <select class="txt">
        <xsl:attribute name="name">
        <xsl:value-of select="variable/text()"/>
      </xsl:attribute>
        <xsl:if test="string-length(onchange) &gt; 0">
          <xsl:attribute name="onChange">
          <xsl:apply-templates select="onchange"/>
        </xsl:attribute>
        </xsl:if>
        <xsl:for-each select="option">
          <option>
            <xsl:attribute name="value">
            <xsl:value-of select="value/text()"/>
          </xsl:attribute>
            <xsl:if test="selected='yes'">
              <xsl:attribute name="selected">
              <xsl:text>selected</xsl:text>
            </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="text/text()"/>
          </option>
        </xsl:for-each>
      </select>
    </xsl:if>
    <xsl:if test="type = 'tabledialog'">
      <img>
        <xsl:choose>
          <xsl:when test="eventType='hover'">
            <xsl:attribute name="class">tipz</xsl:attribute>
            <xsl:attribute name="title">
              <xsl:value-of select="event/text()"/>
            </xsl:attribute>
          </xsl:when>
          <xsl:otherwise>
            <xsl:attribute name="style">cursor:pointer;</xsl:attribute>
            <xsl:attribute name="onclick">
              <xsl:value-of select="event/text()"/>
            </xsl:attribute>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:attribute name="src">
           <xsl:value-of select="$url_prefix"/><xsl:value-of select="icon/text()"/>
        </xsl:attribute>
        <xsl:attribute name="alt">
          <xsl:value-of select="typeText/text()"/>
        </xsl:attribute>
      </img>
      <xsl:if test="eventType='click'">
        <div>
          <xsl:attribute name="id">
            <xsl:value-of select="variable/text()"/>
          </xsl:attribute>
        </div>
      </xsl:if>
    </xsl:if>
  </xsl:template>


  <xsl:template match="a">
    <a>
      <xsl:attribute name="class">
      <xsl:choose>
        <xsl:when test="string-length(stylesheet/text())&gt;0"><xsl:value-of select="stylesheet/text()"/>
        </xsl:when>
        <xsl:otherwise><xsl:text/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
      <xsl:attribute name="href">
      <xsl:value-of select="href/text()"/>
      <xsl:for-each select="arg">
        <xsl:choose>
          <xsl:when test="first='true'"><xsl:text>?</xsl:text>
          </xsl:when>
          <xsl:otherwise><xsl:text>&amp;</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="name/text()"/><xsl:text>=</xsl:text>
        <xsl:choose>
          <xsl:when test="function-available('encoder:encode')">
            <xsl:value-of select="encoder:encode(value)"/>
          </xsl:when>
          <xsl:otherwise>
            <xsl:call-template name="url-encode"><xsl:with-param name="str" select="value"/>
            </xsl:call-template>
				  </xsl:otherwise>
        </xsl:choose>
      </xsl:for-each>
    </xsl:attribute>
      <xsl:if test="disabled='true'">
        <xsl:attribute name="disabled">true</xsl:attribute>
      </xsl:if>
      <xsl:value-of select="text/text()"/>
    </a>
  </xsl:template>


  <xsl:template match="p">
    <P>
      <xsl:apply-templates/>
    </P>
  </xsl:template>

  <xsl:template match="br">
    <BR/>
  </xsl:template>

  <xsl:template match="b">
    <B>
      <xsl:apply-templates/>
    </B>
  </xsl:template>

  <xsl:template match="i">
    <I>
      <xsl:apply-templates/>
    </I>
  </xsl:template>

  <xsl:template match="u">
    <U>
      <xsl:apply-templates/>
    </U>
  </xsl:template>

  <xsl:template match="center">
    <CENTER>
      <xsl:apply-templates/>
    </CENTER>
  </xsl:template>



  <!--  URL ENCODING  Written by Mike J. Brown, mike@skew.org -->
  <!-- Characters we'll support. We could add control chars 0-31 and 127-159, but we won't. -->
  <xsl:variable name="ascii">
    !"#$%&amp;'()*+,-./0123456789:;&lt;=&gt;?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~
  </xsl:variable>
  <xsl:variable name="latin1">
     ¡¢£¤¥¦§¨©ª«¬­®¯°±²³´µ¶·¸¹º»¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ
  </xsl:variable>
  <!-- Characters that usually don't need to be escaped -->
  <xsl:variable name="safe">!'()*-.0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz~</xsl:variable>
  <xsl:variable name="hex">0123456789ABCDEF</xsl:variable>
  <xsl:template name="url-encode">
    <xsl:param name="str"/>
    <xsl:if test="$str">
      <xsl:variable name="first-char" select="substring($str,1,1)"/>
      <xsl:choose>
        <xsl:when test="contains($safe,$first-char)">
          <xsl:value-of select="$first-char"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:variable name="codepoint">
            <xsl:choose>
              <xsl:when test="contains($ascii,$first-char)">
                <xsl:value-of select="string-length(substring-before($ascii,$first-char)) + 32"/>
              </xsl:when>
              <xsl:when test="contains($latin1,$first-char)">
                <xsl:value-of select="string-length(substring-before($latin1,$first-char)) + 160"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message terminate="no">
                  Warning: string contains a character that is out of range! Substituting "?".
                </xsl:message>
                <xsl:text>63</xsl:text>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>
          <xsl:variable name="hex-digit1" select="substring($hex,floor($codepoint div 16) + 1,1)"/>
          <xsl:variable name="hex-digit2" select="substring($hex,$codepoint mod 16 + 1,1)"/>
          <xsl:value-of select="concat('%',$hex-digit1,$hex-digit2)"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:if test="string-length($str) &gt; 1">
        <xsl:call-template name="url-encode">
          <xsl:with-param name="str" select="substring($str,2)"/>
        </xsl:call-template>
      </xsl:if>
    </xsl:if>
  </xsl:template>
  <!-- END URL ENCODING  -->

</xsl:stylesheet>
