
var EL_FORM, EL_LOGIN, EL_PASSWORD;

function gotoPass(evt) {
	e = evt || window.event;
	var key = e.which||e.keyCode;
	if (EL_PASSWORD && key == 13) {
		EL_PASSWORD.focus();
	}
}

function checkLogin(evt) {
	var e = evt || window.event;
	var key = e.which||e.keyCode;
	if (EL_FORM && key == 13) {
		EL_FORM.submit();
	}
}

function register(asForm, asLogin, asPassword) {
	EL_FORM = asForm;
	EL_LOGIN = asLogin;
	EL_PASSWORD = asPassword;
	
	EL_LOGIN.onkeyup = gotoPass;
	EL_PASSWORD.onkeyup = checkLogin;
	EL_LOGIN.focus();
}