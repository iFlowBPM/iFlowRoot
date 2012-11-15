    var nrtabs = 3;
    var seltab = 1;
    var prevseltab = 1;
    
    var _tabArray = new Array();

    function inner_tab_init() {
      inner_tabber(1);
    }
    
    function inner_tabber(tabnr, tabholder) {
      var tmpArray = _tabArray[tabholder];
      if(!tmpArray) return;
      
      for (p=1; p < tmpArray.length; p++) {  // Start at 1
        i = tmpArray[p];
        if (i != tabnr) {
          mybutton = document.getElementById('tabbutton' + i);
          section = document.getElementById('tabsection' + i + '_div');
          mytab = document.getElementById('tabbutton' + i);
          mybutton.className = 'tab_button';
          mytab.style.verticalAlign = 'bottom';
          section.style.display = 'none';
          }
      }
      prevseltab = seltab;
      seltab = tabnr;
      mybutton = document.getElementById('tabbutton' + tabnr);
      section = document.getElementById('tabsection' + tabnr + '_div');
      mytab = document.getElementById('tabbutton' + tabnr);
      mybutton.className = 'tab_button_pressed';

      mybutton.blur();          
      
      mytab.style.verticalAlign = 'top';
      section.style.display = 'block';
    } 
  