/* Set of validation functions of forms */


/*
 *
 * Generic: Strings and Numbers
 *
*/

/* check if a string is valid */
function isValidText(str) {
    if(isEmpty(str))
      return false;
    else
      return true;
}

/* check if a string is empty */
function isEmpty(str) {
    if(str.length == 0)
      return true;
    
    var r = new RegExp("^ *$","i");
    return r.test(str);
}

/* check if a string has a digit */
function hasDigit(str) {
    var r = new RegExp("\\d*","i");
    return r.test(str);
}

/* check if a string represents a number - positive integer */
function isInteger(str) {
    var r = new RegExp("^\\d{1,}$","i");
    return r.test(str);
}

/* check if str represents a positive float number */
function isFloat(str) {
    var r = new RegExp("^\\d{1,},d{1,}*$","i");
    return r.test(str);
}

/* check if a string has only alphabetic chars and whitespaces */
function isValidString(str) {
    var r = new RegExp("^[A-Za-z ]$","i");
    alert(r.test(str));
    return r.test(str);
}


/*
 *
 * Currency
 *
*/

/* check is str represents a EUR currency value */
/* accepted format: #+,## */
function checkEUR(str) {
    var r = new RegExp("^\\d{1,},\\d{2}$","i");
    return r.test(str);
}


/*
 *
 * Telephone numbers
 *
*/


/* check if a string represents a phone number */
/* 2######## */
/* 91####### */
/* 93####### */
/* 96####### */
function checkTelephoneNumber(strPhoneNumber) {
    var r = new RegExp("^(2[0-9]{8}|(91|93|96)[0-9]{7})$","i");
    return r.test(strPhoneNumber);
}


/*
 *
 * Date and Time
 *
*/


/* Checks for hour strings in the format HH:MM */
function checkHour(strHour) {
}


/*
 *
 * BI, NIF, Credit Cards....
 *
*/


/* check a credit card number */
function checkCreditCard(strCreditCard) { 
    
    var s=strCreditCard
    var ccnumber="" 
    var digits = "0123456789" 
    var sum=0; 
    
    var i 
    
    //Trim to digits 
    for(i=0;i<s.length;i++){ 
	t=s.charAt(i) ;
	if (digits.indexOf(t)>=0) 
	    ccnumber=ccnumber+t ;
	else
	    return false;
    } 
    
    var lastDoubled=0; 
    for(i=(ccnumber.length - 1);i>=0;i--) { 
	digit=ccnumber.charAt(i);
	sum += parseInt(digit);
	lastDoubled++;
	if(lastDoubled>1) { 
	    lastDoubled=0;
	    sum += parseInt(digit);
	    if(digit>4) sum-=9; 
	} 
    } 
    
    s=sum.toString();
    if (s.charAt(s.length-1)!="0" || ccnumber.length!=16 || sum==0) { 
	return false;
    } 
    return true;
}
	 


/* check if strNIF is a valid NIF */
function checkNIF(strNIF){
    var s = strNIF;
    var digits = "0123456789";
    var soma = 0;
    //
    if (s.length != 9 )
	return false;
    
    for(i=0,val=9;val>1;i++,val--){ 
	t=s.charAt(i);
	if (digits.indexOf(t)< 0)
	    return false;
	else
	    soma = soma + t*val;
    } 
    resto = soma % 11;
    if (11 - resto > 9)
	checkDigit = 0;
    else
	checkDigit = 11 - resto;
    
    if (checkDigit == s.charAt(8))
	return true;
    else
	return false;
}

function validateNIF(formField) {
  if(checkNIF(formField.value) == false) {
    alert("Numero de contribuinte invalido!");
    formField.value = "";
  }
}

function checkNIF2(strNIF) {
  var s = strNIF;
  var digits = "0123456789";
  var soma = 0;
  var message = "O NIF introduzido não é válido.";
  
  // if NIF is not filled, ignore it...
    
  if (s == null || s == '' || s.length == 0 || s == 0) {
    return true;
  }
    
  // validate it...
    
  if (s.length != 9 ) {
    alert (message);    
    return false;
  }
  
  for(i=0,val=9;val>1;i++,val--) { 
    t=s.charAt(i);
    if (digits.indexOf(t)< 0) {
      alert (message);    
      return false;
    }
    else
      soma = soma + t*val;
  } 
  resto = soma % 11;
  if (11 - resto > 9) {
    checkDigit = 0;
  }
  else {
    checkDigit = 11 - resto;
  }
  
  if (checkDigit == s.charAt(8)) {
    return true;
  }
  else {
    alert (message); 
    return false;
  }
  return true;
} 


