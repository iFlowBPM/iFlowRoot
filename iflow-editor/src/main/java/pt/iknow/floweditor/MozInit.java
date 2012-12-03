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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;

import org.eclipse.swt.SWT;
import org.mozilla.interfaces.nsIConsoleMessage;
import org.mozilla.interfaces.nsIConsoleService;
import org.mozilla.interfaces.nsIPrefBranch;
import org.mozilla.interfaces.nsIPrefService;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.xpcom.Mozilla;

import pt.iknow.floweditor.mozilla.IJavaScriptConsole;
import pt.iknow.floweditor.mozilla.JavaScriptConsole;
import pt.iknow.floweditor.mozilla.JavaScriptConsoleListener;


/**
 * Funcões de inicialização do GRE/XUL/MOZILLA
 * 
 * A definição no package org.eclipse.swt.browser permite aceder a 
 * métodos/propriedades de classes internas do SWT com acesso "package" ou "protected".
 * 
 * @author ombl
 *
 */
public class MozInit {
  static final String SEPARATOR_OS = System.getProperty ("file.separator"); //$NON-NLS-1$
  static final String PROFILE_DIR = SEPARATOR_OS+"floweditor"+SEPARATOR_OS; //$NON-NLS-1$

  private static final int PREF_BOOL_FALSE = 0;
  private static final int PREF_BOOL_TRUE = 1;

  private static MozInit instance;
  private JavaScriptConsoleListener listener = null;
  private static boolean profileDirSet = false;
  
  public synchronized static MozInit getInstance() {
    if (null == instance)
      instance = new MozInit();

    return instance;
  }

  private MozInit() {
    setDefaultPreferences();
    createConsoleListener(new JavaScriptConsole());
  }

  /**
   * Define as propriedades para avisos e debug.
   */
  private void setDefaultPreferences() {
    try {
      FlowEditor.log("Setting preferences...");
      // request preferences service
      nsIServiceManager serviceManager = Mozilla.getInstance().getServiceManager();
      nsIPrefService prefService = (nsIPrefService) serviceManager.getServiceByContractID("@mozilla.org/preferences-service;1",
          nsIPrefService.NS_IPREFSERVICE_IID);

      // root branch (stored as user preferences)
      nsIPrefBranch pref = prefService.getBranch("");

      // set debug preferences
      pref.setBoolPref("javascript.options.strict", PREF_BOOL_TRUE);

      // disable insecure submit warning
      pref.setBoolPref("security.warn_submit_insecure", PREF_BOOL_FALSE);

      FlowEditor.log("Preferences Set!");
    } catch (Throwable t) {
      FlowEditor.log("Error setting preferences", t);
    }

  }

  // Borrowed from ATF eclipse plugin
  /**
   * Constroi e regista um listener para a consola de JavaScript do GRE registado
   */
  private void createConsoleListener(IJavaScriptConsole console) {

    try {
      Mozilla mozilla = Mozilla.getInstance();
      FlowEditor.log("Creating console listener...");
      // registering the listener
      nsIConsoleService consoleService = (nsIConsoleService) mozilla.getServiceManager().getServiceByContractID(
          "@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID);
      consoleService.registerListener(listener = new JavaScriptConsoleListener(console));

      FlowEditor.log("Retrieving pendding messages...");
      // populate cons0le with initial items
      nsIConsoleMessage[][] messageArray = new nsIConsoleMessage[1][];
      consoleService.getMessageArray(messageArray, null);

      for (int i = 0; i < messageArray[0].length; i++)
        console.logConsoleMessage(messageArray[0][i]);
    } catch (Exception e) {
      FlowEditor.log("Error registering console listener", e);
    }
  }

  /**
   * Configura o directório de perfil do mozila para usar "floweditor" em vez de "eclipse".
   * Além disso, também procura um GRE com JavaXPCOM incorporado. Caso não seja encontrado, 
   * faz o download e configura os paths respectivos.
   */
  public synchronized static void setupMozilla() {
    if(profileDirSet) return;
    profileDirSet = true;
    
    File parent = new File(FlowEditorConfig.CONFIG_DIR);
    File xulHome = new File(parent, "xulrunner");
    // xulHome = new File("/home/ombl/Desktop/xul/191src/obj-xulrunner/dist/bin");
    if(!xulHome.exists()) {
      try {
        // expand file
        java.io.InputStream xulZip = MozInit.class.getResourceAsStream("/xulrunner.zip");
        if(xulZip == null) throw new Exception("Could not find xulrunner resource. Aborting...");
        ZipInputStream zin = new ZipInputStream(xulZip);
        ZipEntry ze = null;
        final byte [] buffer = new byte[8192];
        int r;
        try {
          while((ze=zin.getNextEntry())!=null) {
            File f = new File(parent, ze.getName());
            if(ze.isDirectory()) {
              f.mkdirs();
            } else {
              f.getParentFile().mkdirs();// just in case...
              OutputStream out = null;
              try {
                out = new BufferedOutputStream(new FileOutputStream(f));
                while((r = zin.read(buffer))!=-1) out.write(buffer, 0, r);
              } catch (Throwable t) {
                FlowEditor.log("Error unpacking file "+ze.getName(), t);
              } finally {
                try {
                  if(out!=null) out.close();
                } catch (Throwable t) {}
              }
            }
          }
        } catch(IOException e) {
          FlowEditor.log("Error reading xulrunner package", e);
          throw new Exception("Error reading xulrunner package", e);
        } finally {
          try {
            zin.close();
          } catch(Throwable t){}
          try {
            xulZip.close();
          } catch(Throwable t){}
        }
      } catch(Exception e) {
        // the "fail" case... Clean up the mess
        FlowEditor.log("Could not unpack xulrunner. Trying to clean up the mess...");
        if(xulHome.exists())
          recurseDelete(xulHome);
        if(xulHome.exists())
          FlowEditor.log("The mess was not clean! I REPEAT! The mess was not clean!");
        
        // rewrap exception...
        throw new RuntimeException(e.getMessage(), e.getCause());
      }
    }
    
    System.setProperty("org.eclipse.swt.browser.XULRunnerPath", xulHome.getAbsolutePath());
    FlowEditor.log("XulRunner set to: "+System.getProperty("org.eclipse.swt.browser.XULRunnerPath"));
    
    try {
      // required by One-JAR
      ClassPool classPool = ClassPool.getDefault();
      classPool.appendClassPath(new ClassClassPath(SWT.class)); // use SWT class object to lookup classes
      // Load org.eclipse.swt.browser.Mozilla and hack it!
      CtClass ct = classPool.get("org.eclipse.swt.browser.Mozilla");
      ct.setModifiers(Modifier.setPublic(ct.getModifiers()));
      CtField field = ct.getField("PROFILE_DIR");
      field.setModifiers(Modifier.STATIC); // remove final
      int mod = field.getModifiers();
      mod = Modifier.setPublic(mod);
      field.setModifiers(mod); // remove final
      // inject into classloader
      Class<?> c =  classPool.toClass(ct, SWT.class.getClassLoader(), null); // write to SWT classloader
      c.getField("PROFILE_DIR").set(null, PROFILE_DIR);
      FlowEditor.log("SWT Mozilla class hacked. New profile dir is: "+c.getField("PROFILE_DIR").get(null));
    } catch (Throwable t) {
      FlowEditor.log("Error hacking SWT Mozilla class", t);
    }
  }
  
  private static void recurseDelete(File f) {
    if(null == f) return;
    if(f.isDirectory()) {
      File[] contents = f.listFiles();
      for(File c : contents)
        recurseDelete(c);
    }
    f.delete();
  }
  
  /**
   * Remove os listeners registados durante o processo de inicialização.
   * 
   * TODO Registar como shutdown hook?
   */
  public static void unregisterHandlers() {
    if (null != instance && null != instance.listener) {
      try {
        nsIConsoleService consoleService = (nsIConsoleService) Mozilla.getInstance().getServiceManager().getServiceByContractID(
            "@mozilla.org/consoleservice;1", nsIConsoleService.NS_ICONSOLESERVICE_IID);
        consoleService.unregisterListener(instance.listener);
        instance.listener = null;
        FlowEditor.log("Javascript console Listener unregistered");
      } catch (Throwable t) {
        FlowEditor.log("Error unregistering javascript console listener", t);
      }

    }
  }

  /**
   * Imprime todas as preferencias do GRE configurado actualmente
   * @param pref
   */
  public static void dumpAllPreferences() {
    // request preferences service
    nsIServiceManager serviceManager = Mozilla.getInstance().getServiceManager();
    if(serviceManager == null) return;
    nsIPrefService prefService = (nsIPrefService) serviceManager.getServiceByContractID("@mozilla.org/preferences-service;1",
        nsIPrefService.NS_IPREFSERVICE_IID);
    if(prefService == null) return;

    // root branch (stored as user preferences)
    nsIPrefBranch pref = prefService.getBranch("");
    if(pref == null) return;
    long[] count = new long[1];
    String[] prefs = pref.getChildList("", count);

    System.out.println(" =================================== ");
    System.out.println(" Preference count: " + count[0] + " (" + prefs.length + ")");
    System.out.println(" =================================== ");
    for (int i = 0; i < count[0]; i++) {
      System.out.println(" => " + prefs[i]);
    }
    System.out.println(" =================================== ");
  }

}
