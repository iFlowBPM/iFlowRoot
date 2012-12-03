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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Library
 *
 *  desc: guarda informacao base sobre os blocos
 *
 ****************************************************/



/**************************
 * Library
 */
public class Library {
    /* _name da biblioteca */
    private String _name;
    private String _directory;
    private String description;
    private String nameKey;
    private String descriptionKey;
    
    /* listas da componentes */
    /*lista com os elemenos basicos da biblioteca */
    private ArrayList<Componente_Biblioteca> _alSimpleComponent;
    /* lista dos elementos compostos */
    private ArrayList<Componente_Biblioteca> _alComposedComponent;
    
    private Hashtable<String, Componente_Biblioteca> _htElements;
    
    
    /****************************
     *  Construtor
     *
     * @param _name _name da biblioteca
     */
    public Library(String nome,String directoria) {
        /* iniciar variaveis */
        this._name= nome;
        this._directory=directoria;
        
        _alSimpleComponent   = new ArrayList<Componente_Biblioteca>();
        _alComposedComponent = new ArrayList<Componente_Biblioteca>();
        _htElements          = new Hashtable<String, Componente_Biblioteca>();
    }
    
    /*********************************************
     * Devolve o _name da biblioteca
     * @return _name da biblioteca
     */
    public String getName() {
        return _name;
    }
    
    /*********************************************
     * Altera o _name da biblioteca
     * @param novo _name da biblioteca
     * @return _name da biblioteca
     */
    public void setName(String name) {
        _name = name;
    }
    
    /*********************************************
     * Devolve a _directory  da biblioteca
     * @return _directory da biblioteca
     */
    public String getDirectory() {
        return _directory;
    }
    
    /*********************************************
     * Altera a _directory da biblioteca
     * @param novo _directory da biblioteca
     * @return _directory da biblioteca
     */
    public void setDirectory(String dir) {
        _directory = dir;
    }
    
    /******************************************************************
     * 	funcoes para acrescentar componentes a biblioteca
     */
    /*********************************************************************
     * Funcao que acrescenta um componentes a lista de componentes normais
     * @param cb componente de biblioteca a acrescentar
     */
    public void addSimpleComponent(Componente_Biblioteca cb) {
        _alSimpleComponent.add(cb);
        _htElements.put(cb.Nome, cb);
        _htElements.put(cb.Descricao, cb);
        
        String descr = cb.Descricao;
        if(StringUtils.isNotBlank(cb.descrKey))
          descr = Janela.getInstance().getBlockMessages().getString(cb.descrKey);

        _htElements.put(descr, cb);
    }
    
    /***********************************************************************
     * Funcao que acrescenta um componentes a lista de componentes compostos
     * @param cb componente de biblioteca a acrescentar
     */
    public void addComposedComponent(Componente_Biblioteca cb) {
        _alComposedComponent.add(cb);
        _htElements.put(cb.Nome, cb);
    }
    
    /***********************************************************************
     * Funcao que remove um componente a lista de componentes compostos
     * @param cb componente de biblioteca a remover
     */
    public void removeComposedComponent(Componente_Biblioteca cb) {
        _alComposedComponent.remove(cb);
        _htElements.remove(cb);
    }
    
    /******************************************************************
     * Funcao que devolve o componente pelo seu _name
     * @param Nome _name do componente
     * @return compoente com o determinado _name
     */
    public Componente_Biblioteca getComponent(String name) {
        if (_htElements.containsKey(name)) {
            return _htElements.get(name);
        }
        return null;
    }
    
    public Iterator<Componente_Biblioteca> getAllComponents() {
        ArrayList<Componente_Biblioteca> altmp = new ArrayList<Componente_Biblioteca>();
        altmp.addAll(_alSimpleComponent);
        altmp.addAll(_alComposedComponent);
        
        return altmp.iterator();
    }
    
    /****************************************************************
     * Devolve o numero de componentes  que esta biblioteca contem
     * @return numero de componentes guardados
     */
    public int size() {
        return _htElements.size();
    }

    
    /*************************************************/
    public ArrayList<Componente_Biblioteca> procuraBiblioteca(String subString) {
        ArrayList<Componente_Biblioteca> alMatch = new ArrayList<Componente_Biblioteca>();
        Iterator<String> it = _htElements.keySet().iterator();
        
        while (it.hasNext()) {
            String stmp = it.next();
            if (stmp.toLowerCase().indexOf(subString.toLowerCase()) > 0) {
                alMatch.add(_htElements.get(stmp));
            }
        }
        return alMatch;
    }
    
    public String toString() {
        StringBuffer sbtmp = new StringBuffer();
        Enumeration<String> enumer = _htElements.keys();
        while (enumer.hasMoreElements()) {
            String key = enumer.nextElement();
            sbtmp.append("\nComp: " + key); //$NON-NLS-1$
        }
        return sbtmp.toString();
    }
    
    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getNameKey() {
      return nameKey;
    }

    public void setNameKey(String nameKey) {
      this.nameKey = nameKey;
    }

    public String getDescriptionKey() {
      return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
      this.descriptionKey = descriptionKey;
    }

}
