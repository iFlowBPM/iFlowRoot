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
