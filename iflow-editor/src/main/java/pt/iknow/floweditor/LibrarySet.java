/*****************************************************************************************
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
****************************************************************************************/
package pt.iknow.floweditor;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: LibrarySet
 *
 *  desc: guarda um conjunto de _htLibraries
 *
 ****************************************************/

public class LibrarySet {

  private Hashtable<String,Library> _htLibraries = new Hashtable<String,Library>();
//  private ArrayList<String> _alLibraries = new ArrayList<String>();

  public LibrarySet() {
  }

  /***********************************************************
   * adiciona uma bilioteca a lista de _htLibraries
   */    
  public void addLibrary(Library bib) {
//    if (!_htLibraries.containsKey(bib.getName())) {
//      _alLibraries.add(bib.getName());
//    }

    _htLibraries.put(bib.getName(), bib);
  }


  /***************************************************************
   * procura um dado componente na lista de _htLibraries
   */
  public Componente_Biblioteca getComponent(String name) {
    Componente_Biblioteca cb = null;
    Enumeration<String> enumer = _htLibraries.keys();
    while (enumer.hasMoreElements()) {
      String key = enumer.nextElement();
      Library bib = _htLibraries.get(key);

      cb = bib.getComponent(name);
      if (cb != null) break;
    }
    return cb;
  }

  public String[] getLibraryKeys() {      
    Vector<String> v = new Vector<String>(_htLibraries.keySet());
    Collections.sort(v);
    return v.toArray(new String[v.size()]);
  }

  public Library getLibrary(String name) {
    return _htLibraries.get(name);
  }

  public boolean hasLibrary(Library bib) {
    return hasLibrary(bib.getName());
  }

  public boolean hasLibrary(String name) {
    return _htLibraries.containsKey(name);
  }

  public int size() {
    return _htLibraries.size();
  }

  /************************
   */    
   void addComposedComponent(Componente_Biblioteca b) {
   }

   public String toString() {
     StringBuffer sbtmp = new StringBuffer();
     for (String key : getLibraryKeys()) {
       sbtmp.append("\n\nKey: ").append(key); //$NON-NLS-1$
       sbtmp.append(_htLibraries.get(key));
     }
     return sbtmp.toString();
   }
}
