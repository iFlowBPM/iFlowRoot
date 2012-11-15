// The AutoSaver class
function AutoSaver (opts) {
  var options = new Object();
  if(opts) options = opts;
  
  var saveSuffix = '.save';
  var loadSuffix = '.load';
  this.location = opts.name;
  this.iBox = opts.iBox;
  
  this.messageDivId = options.messageDivId==undefined?'autoSaveMessage':options.messageDivId;
  this.successDivId = options.successDivId==undefined?'autoSaveDone':options.successDivId;
  this.errorDivId = options.errorDivId==undefined?'autoSaveError':options.errorDivId;
  
  if(opts.saveTimeout == undefined)
    this.saveTimeout = 50*60*1000; // 50 minutes by default
  else
    this.saveTimeout = opts.saveTimeout;
  
  if(opts.logoutTimeout == undefined)
    this.logoutTimeout = 7*60*1000; // 7 minutes by default
  else
    this.logoutTimeout = opts.logoutTimeout;
  
  this.start = function (autoLoad) {
    if(autoLoad)
      this.loadForm();
    else
      this.restart();
  };
  
  this.restart = function () {
    var doTheAutoSave = this.clDoTheAutoSave;
    this.autoSaveTimerID = setTimeout(doTheAutoSave, this.saveTimeout);
  };
  
  this.stop = function () {
    clearTimeout(this.autoSaveTimerID);
  };

  this.doTheAutoSave = function () {
    if(this.iBox) this.iBox.waiter();
    var func = this.clFinishAutoSave;
    new Ajax(this.location+saveSuffix, {
		method: 'post',
		data: $('dados').toQueryString(),
		onComplete: func
	}).request();
  };
  
  this.finishAutoSave = function (sResponse) {
    var response = this.parseResponse(sResponse);
    var logout = this.clLogout;
    this.confirmTimeOut = setTimeout(logout, this.logoutTimeout);
    if(this.iBox) {
      var errDiv, okDiv;
      errDiv = document.getElementById(this.errorDivId);
      okDiv = document.getElementById(this.successDivId);
      if(okDiv) {
        if(errDiv) {
          if(response.error) {
            errDiv.style.display='';
            okDiv.style.display='none';
          } else {
            errDiv.style.display='none';
            okDiv.style.display='';
	      }
        } else {
          okDiv.style.display='';
        }
      }
      this.iBox.open('', this.messageDivId, {width:450,height:250});
    }
  };

  this.logout = function () {
    clearTimeout(this.confirmTimeOut);
    window.location.href='logout.jsp';
  };
  
  this.stayInPage = function () {
    clearTimeout(this.confirmTimeOut);
    if(this.iBox) this.iBox.hideIbox();
    this.restart();
  };
  
  this.loadForm = function () {
    var func = this.clFinishAutoLoad;
    new Ajax(this.location+loadSuffix, {
		method: 'post',
		data: $('dados').toQueryString(),
		onComplete: func
	}).request();
  };

  this.finishAutoLoad = function (sResponse) {
    var response = this.parseResponse(sResponse);
    if(!response.error && response.form != null) {
      // Nao sei se faca isto a partir do form ou se do objecto json.
      // Para ja fica JSON.
      for (var i in response.form) {
        this.updateForm(i, response.form[i]);
      }
    }
    this.restart();
  };

  this.updateForm = function (name,value) {
    var form = $('dados');
    var elems = $('dados')[name];
    // lidar com situacoes pontuais. nao actualizar os seguites....
    if('flowid'==name || 'pid'==name || 'subpid'==name || 'op'==name) {
      return;
    }

    var elem;
    if(elems && (elems.constructor==Array) && elems.length > 0) {
      // TODO multiple names...
      this.updateFormElem(name,value,elems[0]);
    } else {
      this.updateFormElem(name,value,elems);
    }
  };

  this.updateFormElem = function (name,value,elem) {
    // first simple version. Assumes one field with the same name
    if(elem) {
      var tag = elem.nodeName.toLowerCase();
      if (tag == 'select') {
        // TODO ver se permite multi
        for( var option = elem.options, i = 0; option[i]; i++ ) {
          if(option.value==value) {
            elem.selectedIndex = i;
          }
          // option[i].selected = (option.value==value);
        }
      } else if (tag == 'textarea') {
        elem.value = value;
      } else { // regular input....
        var type = elem.type.toLowerCase();
        if( type == 'radio' || type == 'checkbox' ) {
          // TODO isto nao esta bem..... eh necessario saber como se vai fazer...
          elem.checked = value ? true : false;
        } else {
          elem.value = value;
        }
      }
    }
  };

  this.updateFormElems = function (name,value,elems) {
    // first simple version. Assumes one field with the same name
    if(elems && elems.length > 0) {
      var tag = elem.nodeName.toLowerCase();
      if (tag == 'select') {
        // TODO ver se permite multi
        for( var option = elem.options, i = 0; option[i]; i++ ) {
          if(option.value==value) {
            elem.selectedIndex = i;
          }
          // option[i].selected = (option.value==value);
        }
      } else if (tag == 'textarea') {
        elem.value = value;
      } else { // regular input....
        var type = elem.type.toLowerCase();
        if( type == 'radio' || type == 'checkbox' ) {
          // TODO isto nao esta bem..... eh necessario saber como se vai fazer...
          elem.checked = value ? true : false;
        } else {
          elem.value = value;
        }
      }
    }
  };

  // simple JSON parser
  this.parseResponse = function (jsonText) {
    var result = eval('('+jsonText+')');
    return result;
  };

  /**
   * Stripped version of mootools bind
   * This closure will bind the function passed by argument to 'this' object 
   */
  this.closure = function (func, params) {
    var obj = this;
    var result = function(event) {
      var args = params || arguments;
      var resturn = function() {
      	return func.apply(obj, args);
      };
      return resturn();
    };
    return result;
  };
    

  // TODO fix closure to remove mootool's bind
  this.clFinishAutoLoad = this.finishAutoLoad.bind(this);
  this.clFinishAutoSave = this.finishAutoSave.bind(this);
  this.clLogout = this.closure(this.logout, []);
  this.clDoTheAutoSave = this.closure(this.doTheAutoSave, []);

};

function purge(d) {
    var a = d.attributes, i, l, n;
    if (a) {
        l = a.length;
        for (i = 0; i < l; i += 1) {
            n = a[i].name;
            if (typeof d[n] === 'function') {
                d[n] = null;
            }
        }
    }
    a = d.childNodes;
    if (a) {
        l = a.length;
        for (i = 0; i < l; i += 1) {
            purge(d.childNodes[i]);
        }
    }
}



