/**
 * Pop up menu
 */

var PopUpMenu = new Class({
	initialize: function() {
		this.currentMenu = false;
		this.shadowDiv = new Element('div');
		this.shadowDiv.addClass('shadow');
		this.menuDiv = new Element('div');
		this.menuDiv.addClass('menu');
		this.shadowDiv.adopt(this.menuDiv);
		this.menuUl = new Element('ul');
		this.menuDiv.adopt(this.menuUl);
		this.shadowDiv.setStyle('display','none');
		this.shadowDiv.setStyle('z-index','500');
		$('body').adopt(this.shadowDiv);
		// Popup initialized!
		this.menuIsClosed=true;
	},
	
	open: function(dnd,liElem, x, y) {
		var menuLi,i,item,body,menu,ctx;
		
		this.menuUl.empty();
		
		// first: fixed ops
		menuLi = new Element('li');
		menuLi.set('text','Select');
		menuLi.addEvent('click', function (evt, dnd, liElem) {
			evt = new Event(evt).stop();
			this.close();
			dnd.selectElement(liElem);
		}.bindWithEvent(this,[dnd,liElem]));
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Delete');
		menuLi.addEvent('click', function (evt, dnd, liElem) {
			evt = new Event(evt).stopPropagation();
			this.close();
			dnd.removeWidgetEvt(evt,liElem);
		}.bindWithEvent(this,[dnd,liElem]));
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Read Only');
		menuLi.addEvent('click', function (evt, dnd, liElem) {
			evt = new Event(evt).stopPropagation();
			this.close();
			dnd.readonlyEvt(evt,liElem);
		}.bindWithEvent(this,[dnd,liElem]));
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Override Template');
		menuLi.addEvent('click', function (evt, dnd, liElem) {
			evt = new Event(evt).stopPropagation();
			this.close();
			dnd.overrideEvt(evt,liElem);
		}.bindWithEvent(this,[dnd,liElem]));
		this.menuUl.adopt(menuLi);

		ctx = liElem.retrieve(DragNSort.CTX_PROP);
		// then, custom ops
		if(ctx && ctx.getContextMenu) {
			menu = ctx.getContextMenu();
			if(menu && menu.length > 0) {
				// Add a separator
				menuLi = new Element('li');
				this.menuUl.adopt(menuLi.adopt(new Element('hr')));
				
				// add custom items
				for(i = 0; i < menu.length; i++) {
					item = menu[i];
					menuLi = new Element('li');
					menuLi.set('text',item.text);
					menuLi.addEvent('click', item.event.bindWithEvent(dnd,ctx));
					this.menuUl.adopt(menuLi);
				}
			}
		}

		this.shadowDiv.setStyles({'left':x,'top':y,'display':''});
		this.menuIsClosed=false;
	},

	openTab: function(dnd,x,y) {
		var menuLi,i,item,body,menu;
		
		this.menuUl.empty();
		
		menuLi = new Element('li');
		menuLi.set('text','Select');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Rename');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Delete');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		this.menuUl.adopt(menuLi.adopt(new Element('hr')));
		
		menuLi = new Element('li');
		menuLi.set('text','New Tab');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Duplicate');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Copy to');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		this.shadowDiv.setStyles({'left':x,'top':y,'display':''});
		this.menuIsClosed=false;
	},
	openDefault: function(dnd,x,y) {
		var menuLi,i,item,body,menu;
		
		this.menuUl.empty();
		
		menuLi = new Element('li');
		menuLi.set('text','Propriedades');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		menuLi = new Element('li');
		menuLi.set('text','Promover a Template');
		// menuLi.addEvent('click', item.event);
		this.menuUl.adopt(menuLi);
		
		this.shadowDiv.setStyles({'left':x,'top':y,'display':''});
		this.menuIsClosed=false;
	},
	close: function(ctx) {
		if(this.menuIsClosed) return;
		this.shadowDiv.setStyle('display','none');
		this.menuUl.empty();
		this.menuIsClosed=true;
	}

});
