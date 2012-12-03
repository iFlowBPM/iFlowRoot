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
package pt.iflow.applet;

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.iflow.applet.signer.SignatureType;

/**
 * Utility class to manage applet resources.
 * <br>
 * Implementations should be generated automatically and class name added to helper class list.
 * 
 * @author ombl
 *
 */
public abstract class AbstractAppletServletHelper {
  private static Log log = LogFactory.getLog(AbstractAppletServletHelper.class);
  
  // Must match applet.vm
  public static final String VAR_APPLET_CLASS_NAME = "appletClassName"; //$NON-NLS-1$
  public static final String VAR_APPLET_CLASS_PATH = "appletDependencies"; //$NON-NLS-1$
  public static final String VAR_APPLET_LANG = "appletLang"; //$NON-NLS-1$

  private static final String[] HELPER_CLASSES  = {
    "pt.iflow.applet.AppletServletHelper", //$NON-NLS-1$
    "pt.iflow.utils.scanner.AppletServletHelper", //$NON-NLS-1$
  };
  
  private static final String[] FEATURES;

  static {
    SignatureType[] types = SignatureType.values();
    FEATURES = new String[types.length];
    for (int i = 0; i < types.length; i++)
      FEATURES[i] = types[i].name();
  }

  private static AbstractAppletServletHelper instance = null;
  private static boolean initComplete = false;
  
  public static AbstractAppletServletHelper getInstance() {
    if(!initComplete) {
      initComplete = true;
      Class<?> c = null;
      for(String className : HELPER_CLASSES) {
        try {
          c = Class.forName("pt.iflow.applet.AppletServletHelper"); //$NON-NLS-1$
          instance = (AbstractAppletServletHelper) c.newInstance();
          break;
        } catch (ClassNotFoundException e) {
          log.warn("Class "+className+" not found."); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (InstantiationException e) {
          log.warn("Class "+className+" can't be instantiated (abstract or interface class)."); //$NON-NLS-1$ //$NON-NLS-2$
        } catch (IllegalAccessException e) {
          log.warn("Class "+className+" can't be instantiated (illegal access)."); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      
      if(null == instance) {
        System.err.println("AbstractAppletServletHelper not created. Did someone forget to run 'mvn generate-source' in iflow-applet project?"); //$NON-NLS-1$
        new Exception().printStackTrace(System.err);
      }
    }
    
    return instance;
  }
  
  /**
   * Iterate through all dependencies
   * 
   * @return
   */
  public abstract Iterator<String> dependenecies();

  /**
   * Get applet version
   * 
   * @return Applet version number
   */
  public abstract String getVersion();

  /**
   * Devolve o contexto a usar pelo velocity para processar a template <br>
   * <br>
   * Nota: isto esta assim para eliminar a dependencia de velocity na applet. Ao mesmo tempo convem manter o codigo relacionado com
   * a applet junto da applet. Nao me levem a mal por isso.
   * 
   * @return
   * @see getAppletScriptTemplate()
   */
  public abstract Hashtable<String, Object> getAppletScriptContext();

  public static String[] getFeatures() {
    return FEATURES.clone();
  }

  public static void main(String [] args) {
    AbstractAppletServletHelper h = AbstractAppletServletHelper.getInstance();
    System.out.println(h.getVersion());
  }
}
