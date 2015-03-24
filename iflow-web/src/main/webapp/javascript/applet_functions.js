// TODO injeccao da applet on demand

var BIG_MAX = 0;
var TIMEOUT_DELAY=1000;
var IFLOW_APPLET_ELEM;
if(!window['IFLOW_APPLET_NAME']) IFLOW_APPLET_NAME = parent.IFLOW_APPLET_NAME;
if(!window['IFLOW_APPLET_NAME']) IFLOW_APPLET_NAME = top.IFLOW_APPLET_NAME;
if(!window['IFLOW_APPLET_NAME']) IFLOW_APPLET_NAME = 'iflowUtilityApplet';

function getAppletElem() {
	if(IFLOW_APPLET_ELEM) return;
	IFLOW_APPLET_ELEM = document.getElementById(IFLOW_APPLET_NAME);
	if(!IFLOW_APPLET_ELEM) {
		// top frame (window) or parent frame?
		IFLOW_APPLET_ELEM = parent.IFLOW_APPLET_ELEM;
		if(!IFLOW_APPLET_ELEM) {
			IFLOW_APPLET_ELEM = top.IFLOW_APPLET_ELEM;
		}
	}
}

if(!window['Json']) Json = parent.Json;
if(!window['Json']) Json = top.Json;

//metodos utilitarios (syntactic sugar) para invocacao da applet
//As chamadas ah applet sao feitas atraves de strings em formato JSON

/**
 * Carregamento de ficheiros para o iFlow
 * 
 * @param flowid Identificador do fluxo
 * @param pid Identificador do processo
 * @param subpid Identificador do subprocesso
 * @param varName Vari?vel onde ser? guardado o ficheiro
 * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
 * @param encryptionType Tipo de encripta??o a efectuar (NONE, PKI)
 * 
 * @return Identificador do ficheiro carregado
 */
function uploadFile(varName, signatureType, encryptionType, max, sig_pos_style_java) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}
	updateMax(max);
	if (checkLimits(varName)) {
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.SIGNATURE_TYPE] = signatureType;
	obj[IFLOW_APPLET_ELEM.ENCRYPTION_TYPE] = encryptionType;

	var taskId = IFLOW_APPLET_ELEM.uploadFile(
			document.cookie,
			Json.toString(obj),
			sig_pos_style_java
	);

	checkLimits(varName);
	// setTimeout('checkTaskStatus("'+taskId+'")', TIMEOUT_DELAY);
}

function updateMax(max) {
	if ($defined ( BIG_MAX )) {
		this.BIG_MAX = max;
	} else {
		this.BIG_MAX = 0;
	}
}
/**
 * Modificar ficheiro existente. Permite reassinar ou encriptar um documento existente na base de dados.
 * 
 * @param varName Variavel do documento
 * @param fileId Identificador do ficheiro
 * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
 * @param encrptionType Tipo de encripta??o a efectuar (NONE, PKI)
 * 
 * @return Identificador do ficheiro carregado
 */
function modifyFile(varName, fileId, signatureType, encryptionType, sig_pos_style_java) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.FILEID] = fileId;
	obj[IFLOW_APPLET_ELEM.SIGNATURE_TYPE] = signatureType;
	obj[IFLOW_APPLET_ELEM.ENCRYPTION_TYPE] = encryptionType;

	return IFLOW_APPLET_ELEM.modifyFile(
			document.cookie,
			Json.toString(obj), 
			sig_pos_style_java
	);
}

/**
 * Substituir ficheiro existente. O ficheiro poder? ser assinado ou encriptado.
 * 
 * @param varName Variavel do documento
 * @param fileId Identificador do ficheiro
 * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
 * @param encrptionType Tipo de encripta??o a efectuar (NONE, PKI)
 * 
 * @return Identificador do ficheiro carregado
 */
function replaceFile(varName, fileId, signatureType, encryptionType, sig_pos_style_java) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.FILEID] = fileId;
	obj[IFLOW_APPLET_ELEM.SIGNATURE_TYPE] = signatureType;
	obj[IFLOW_APPLET_ELEM.ENCRYPTION_TYPE] = encryptionType;

	return IFLOW_APPLET_ELEM.replaceFile(
			document.cookie,
			Json.toString(obj), 
			sig_pos_style_java
	);
}

/**
 * Descarregar documento existente. Desencriptar um documento previamente encriptado ou simplesmente efectuar o download do mesmo.
 * 
 * @param flowid Identificador do fluxo
 * @param pid Identificador do processo
 * @param subpid Identificador do subprocesso
 * @param fileId Identificador do ficheiro
 * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
 * @param encrptionType Tipo de encripta??o a efectuar (NONE, PKI)
 * 
 * @return Identificador do ficheiro carregado
 */
function downloadFile(varName, fileId, signatureType, encryptionType) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.FILEID] = fileId;
	obj[IFLOW_APPLET_ELEM.SIGNATURE_TYPE] = signatureType;
	obj[IFLOW_APPLET_ELEM.ENCRYPTION_TYPE] = encryptionType;

	return IFLOW_APPLET_ELEM.downloadFile(
			document.cookie,
			Json.toString(obj)
	);
}

/**
 * Verifica a assinatura digital de um determinado documento.
 * 
 * @param flowid Identificador do fluxo
 * @param pid Identificador do processo
 * @param subpid Identificador do subprocesso
 * @param fileId Identificador do ficheiro
 * 
 * @return Mensagem com resultado da valida??o
 */
function verifiyFile(varName, fileId) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.FILEID] = fileId;

	return IFLOW_APPLET_ELEM.verifyFile(
			document.cookie,
			Json.toString(obj)
	);
}

/**
 * Scan e Carregamento de ficheiros para o iFlow
 * 
 * @param flowid Identificador do fluxo
 * @param pid Identificador do processo
 * @param subpid Identificador do subprocesso
 * @param varName Vari?vel onde ser? guardado o ficheiro
 * @param signatureType Tipo de assinatura a efectuar (NONE, XADES, PDF, PKCS7)
 * @param encrptionType Tipo de encripta??o a efectuar (NONE, PKI)
 * @param fileFormat Formato do ficheiro (PDF, JPG, TIFF, etc)
 * 
 * @return Identificador do ficheiro carregado
 */
function scanFile(varName, signatureType, encryptionType, fileFormat, max) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}
	updateMax(max);
	if (checkLimits(varName)) {
		return null;
	}
	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.SIGNATURE_TYPE] = signatureType;
	obj[IFLOW_APPLET_ELEM.ENCRYPTION_TYPE] = encryptionType;
	obj[IFLOW_APPLET_ELEM.FILE_FORMAT] = fileFormat;

	var ret = IFLOW_APPLET_ELEM.scanFile(
			document.cookie,
			Json.toString(obj)
	);
	checkLimits(varName);
	return ret;
}

// Gest?o de certificados

/**
 * Carregamento e/ou substitui??o de certificados digitais de utilizador
 * @param userid Identificador do utilizador
 * @param certificateType Tipo de certificado (autentica??o, encripta??o, assinatura)
 * 
 * @return 
 */
function setCertificate(userid, certificateType) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.USERID] = userid;
	obj[IFLOW_APPLET_ELEM.CERTIFICATE_TYPE] = certificateType;

	return IFLOW_APPLET_ELEM.setCertificate(
			document.cookie,
			Json.toString(obj)
	);
}


function removeFile(fileId, varName, max) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return null;
	}

	// preparar o objecto com argumentos
	var obj = {};
	obj[IFLOW_APPLET_ELEM.FLOWID] = document.getElementById('flowid').value;
	obj[IFLOW_APPLET_ELEM.PID] = document.getElementById('pid').value;
	obj[IFLOW_APPLET_ELEM.SUBPID] = document.getElementById('subpid').value;
	obj[IFLOW_APPLET_ELEM.VARIABLE] = varName;
	obj[IFLOW_APPLET_ELEM.FILEID] = fileId;

	var result = IFLOW_APPLET_ELEM.removeFile(
			document.cookie,
			Json.toString(obj)
	);
	var elemId = 'elem_'+fileId;
	if(result == 'true') {
		var fileElem = document.getElementById(elemId);
		var fileDivParent = fileElem.parentNode;
		fileDivParent.removeChild(fileElem);
	}
	checkLimits(varName);
}

// Testes de disponibilidade

function canScan(fmt) {
	return IFLOW_APPLET_ELEM && IFLOW_APPLET_ELEM.canScan(fmt);
}

function canSign(fmt) {
	return IFLOW_APPLET_ELEM && IFLOW_APPLET_ELEM.canSign(fmt);
}

function canEncrypt(fmt) {
	return IFLOW_APPLET_ELEM && IFLOW_APPLET_ELEM.canEncrypt(fmt);
}


function updateForm(status) {
	if(window.frames['open_proc_frame']) {
		// delegate to children frame
		window.frames['open_proc_frame'].updateForm(status);
		return;
	}
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return;
	}

	if(!status) return;
	var statusObj = Json.evaluate(status);

	if(statusObj['status'] == 'complete') {
		addFileElem(statusObj['result']);
	}
	// else ignore
}

function changeFileState(docid) {	
	if(window.frames['open_proc_frame']) {		// delegate to children frame
		window.frames['open_proc_frame'].changeFileState(docid);
		return;
	}
		document.getElementById('lock_'+docid).src= "/iFlow/images/my_lock.png";
		document.getElementById('lock_'+docid).style.display="inline";
}

//As funcoes de exemplo nas quais me devo basear s?o estas:
function loadSignedFile(signatureType,varname) {
	var fileDesc;
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return;
	}
	fileDesc = IFLOW_APPLET_ELEM.loadAndSign(signatureType,varname);
	if(fileDesc) {
		addFileElem(fileDesc);
	}
}

function addFileElem(myObject) {
	var filename, fileid, varname, item, delete_button, row_element, insertLocation;
	
	if(!myObject) return;
	
	filename = myObject.name.replace('&apos;', '\'');
	fileid = myObject.id;
	varname = myObject.varname;

	item = new Element('span').setText( filename );
	delete_button = new Element('img',{
		'src':'../images/cross_small.gif',
		'alt':'Delete',
		'title':'Delete'
	});
//	delete_button.setAttribute('onclick',"removeFile('" + fileid + "','"+varname+"')"); //N?o funciona no IE
	
	delete_button.onclick = function(){ // Solu??o para IE
		removeFile(fileid,varname);
	    return true;
	};
	
	
	row_element = new Element( 'div', {
		'class':'item',
		'id':'elem_'+fileid
	}).adopt( delete_button ).adopt( item );

	insertLocation = document.getElementById('list_'+varname);
	row_element.injectInside(insertLocation); 
	//insertLocation.adopt(row_element);
}

function removeSignedFile(fileid, varname) {
	if(!IFLOW_APPLET_ELEM)  {
		alert('Applet nao foi configurada');
		return;
	}
	var res = IFLOW_APPLET_ELEM.remove(fileid, varname);
	if(res == 'true') {
		var fileElem = document.getElementById('elem_'+fileid);
		var fileDivParent = fileElem.parentNode;
		fileDivParent.removeChild(fileElem);
	}
	checkLimits(varName);
}

function checkLimits(varName) {
	var loader = document.getElementById('loadfile');
	var isMaxed = (this.BIG_MAX && this.BIG_MAX > 0
				&& this.BIG_MAX <= document.getElementById('list_' + varName).getElementsByTagName('div').length);
	loader.disabled = isMaxed;
	return isMaxed;
}