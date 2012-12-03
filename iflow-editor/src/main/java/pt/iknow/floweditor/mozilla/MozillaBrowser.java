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
package pt.iknow.floweditor.mozilla;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.CloseWindowListener;
import pt.iknow.floweditor.MozInit;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.interfaces.nsICookie2;
import org.mozilla.interfaces.nsICookieManager2;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMDocumentEvent;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIServiceManager;
import org.mozilla.interfaces.nsISimpleEnumerator;
import org.mozilla.interfaces.nsIWebBrowser;
import org.mozilla.xpcom.Mozilla;

import pt.iknow.floweditor.FlowEditor;
import pt.iknow.floweditor.FlowEditorConfig;

public class MozillaBrowser extends Browser {

  public static final String JAVASCRIPT_GLOBAL_PROPERTY_CATEGORY = "JavaScript global property";

  public MozillaBrowser(Composite parent) {
    this(parent, SWT.NONE);
  }

  public MozillaBrowser(Composite parent, int style) {
    super(parent, style | SWT.MOZILLA);
    MozInit.getInstance();
    setupEvents();
    //    ensureInit ();
  }

  void setupEvents() {
    addTitleListener(new TitleListener() {
      public void changed(TitleEvent event) {
        Shell s = getShell();
        if(s != null) s.setText(event.title);
      }
    });

    addOpenWindowListener(new OpenWindowListener() {
      public void open(WindowEvent event) {
        // Certain platforms can provide a default full browser.
        // simply return in that case if the application prefers
        // the default full browser to the embedded one set below.
        //if(!event.required) return;
        FlowEditor.log("New window requested....");
        Shell s = new Shell(event.display);
        s.setLayout(new FillLayout());
        s.setText("New Window");
        MozillaBrowser browser = new MozillaBrowser(s);
        event.browser = browser;
      }
    });
    addVisibilityWindowListener(new VisibilityWindowListener() {
      public void hide(WindowEvent event) {
        MozillaBrowser browser = (MozillaBrowser)event.widget;
        Shell shell = browser.getShell();
        shell.setVisible(false);
      }
      public void show(WindowEvent event) {
        MozillaBrowser browser = (MozillaBrowser)event.widget;
        Shell shell = browser.getShell();
        if (event.location != null) shell.setLocation(event.location);
        if (event.size != null) {
          Point size = event.size;
          shell.setSize(shell.computeSize(size.x, size.y));
        }
        shell.open();
      }
    });
    addCloseWindowListener(new CloseWindowListener() {
      public void close(WindowEvent event) {
        MozillaBrowser browser = (MozillaBrowser)event.widget;
        Shell shell = browser.getShell();
        shell.close();
      }
    });

  }

  @Override
  protected void checkSubclass() {
  }

  private String getBrowserPath(String browser, String defVal) {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(new File(FlowEditorConfig.CONFIG_DIR, "browsers.properties")));
    } catch (Exception e) {
    }
    String ret = props.getProperty(browser, defVal);
    return StringUtils.isEmpty(ret)?defVal:ret;
  }

  public void openInternetExplorer() {
    FlowEditor.getRootDisplay().asyncExec(new Runnable() {
      public void run() {
        String sURL = getUrl();
        FlowEditor.log("Request Open Internet Explorer URL: " + sURL);
        try {
          Runtime.getRuntime().exec(new String[] { getBrowserPath("iexplore", "iexplore"), sURL });
        } catch (IOException e1) {
          FlowEditor.log("Error launching Internet Explorer instance", e1);
        }
      }
    });
  }

  public void openFirefox() {
    FlowEditor.getRootDisplay().asyncExec(new Runnable() {
      public void run() {
        String sURL = getUrl();
        FlowEditor.log("Request Open firefox URL: " + sURL);
        try {
          Runtime.getRuntime().exec(new String[] { getBrowserPath("firefox", "firefox"), "-new-tab", sURL });
        } catch (IOException e1) {
          FlowEditor.log("Error launching firefox instance", e1);
        }
      }
    });
  }

  public void openOpera() {
    FlowEditor.getRootDisplay().asyncExec(new Runnable() {
      public void run() {
        String sURL = getUrl();
        FlowEditor.log("Request Open opera URL: " + sURL);
        try {
          Runtime.getRuntime().exec(new String[] { getBrowserPath("opera", "opera"), "-remote", "openURL(" + sURL + ",new-page)" });
        } catch (IOException e1) {
          FlowEditor.log("Error launching opera instance", e1);
        }
      }
    });
  }

  public nsIWebBrowser getBrowser() {
    return (nsIWebBrowser) super.getWebBrowser();

  }

  public boolean jsexec(String script) {
    nsIDOMWindow domWindow = (nsIDOMWindow) getBrowser().getContentDOMWindow();
    nsIDOMDocument doc = domWindow.getDocument();
    nsIDOMHTMLDocument htmlDoc = (nsIDOMHTMLDocument)doc.queryInterface(nsIDOMHTMLDocument.NS_IDOMHTMLDOCUMENT_IID);
    //    nsIDOMXPathEvaluator evaluator = (nsIDOMXPathEvaluator) doc.queryInterface(nsIDOMXPathEvaluator.NS_IDOMXPATHEVALUATOR_IID);
    //    nsIDOMXPathNSResolver resolver = evaluator.createNSResolver(doc);
    //    nsISupports obj = evaluator.evaluate("//script[@id='scriptPlaceHolder']", doc, resolver, nsIDOMXPathResult.FIRST_ORDERED_NODE_TYPE, null);
    //    nsIDOMXPathResult result = (nsIDOMXPathResult) obj.queryInterface(nsIDOMXPathResult.NS_IDOMXPATHRESULT_IID);
    //    nsIDOMHTMLScriptElement scriptElem = (nsIDOMHTMLScriptElement)(doc.createElement( "SCRIPT" ).queryInterface(nsIDOMHTMLScriptElement.NS_IDOMHTMLSCRIPTELEMENT_IID));
    nsIDOMElement elem = htmlDoc.getElementById("scriptPlaceHolder");
    if(elem == null) {
      nsIDOMElement scriptElem = doc.createElement( "SCRIPT" );
      nsIDOMNode txtNode =doc.createTextNode("function trigEval(elem) {" +
          "if(elem && elem.value && elem.value != '') {" +
          "FormEditor.debug('evaluating '+elem.value);"+
          "try {eval(elem.value)}catch(err){FormEditor.debug('deu erro', err)}" +
          "}" +
      "}");
      scriptElem.appendChild(txtNode);
      doc.getElementsByTagName("HEAD").item(0).appendChild(scriptElem);


      elem = doc.createElement( "INPUT" );
      elem.setAttribute("id", "scriptPlaceHolder");
      elem.setAttribute("name", "scriptPlaceHolder");
      elem.setAttribute("type", "hidden");
      elem.setAttribute("value", "");
      elem.setAttribute("class", "jsEvaluator");
      elem.setAttribute("onchange", "trigEval(this);");
      htmlDoc.getBody().appendChild(elem);
    }
    elem.setAttribute("value",script);
    fireEvent(elem, "change");

    return true;
  }

  public boolean fireEvent(nsIDOMNode node, String eventType) {
    nsIDOMWindow domWindow = (nsIDOMWindow) getBrowser().getContentDOMWindow();
    nsIDOMDocument doc = domWindow.getDocument();
    // Create event
    nsIDOMDocumentEvent docEvent = (nsIDOMDocumentEvent)doc.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);
    nsIDOMEvent event = docEvent.createEvent( "Event" );
    event.initEvent( eventType, true, true ); // can bubble and can be prevented

    // trigger the event
    nsIDOMEventTarget target = (nsIDOMEventTarget)node.queryInterface( nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID );
    target.dispatchEvent( event );

    return true;
  }
  public boolean fireEvent(String id, String eventType) {
    if(null == id) return false;
    nsIDOMWindow domWindow = (nsIDOMWindow) getBrowser().getContentDOMWindow();
    nsIDOMDocument doc = domWindow.getDocument();
    nsIDOMHTMLDocument htmlDoc = (nsIDOMHTMLDocument)doc.queryInterface(nsIDOMHTMLDocument.NS_IDOMHTMLDOCUMENT_IID);
    if(null == htmlDoc) return false;
    nsIDOMElement elem = htmlDoc.getElementById(id);

    // Create event
    nsIDOMDocumentEvent docEvent = (nsIDOMDocumentEvent)doc.queryInterface(nsIDOMDocumentEvent.NS_IDOMDOCUMENTEVENT_IID);
    nsIDOMEvent event = docEvent.createEvent( "Event" );
    event.initEvent( eventType, true, true ); // can bubble and can be prevented

    // trigger the event
    nsIDOMEventTarget target = (nsIDOMEventTarget)elem.queryInterface( nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID );
    target.dispatchEvent( event );

    return true;
  }

  public boolean execute(String script) {
    boolean result = super.execute(script);
    FlowEditor.log("Script '"+script+"' = "+result);
    return result;
  }

  public void dumpCookies() {
    nsIServiceManager serviceMan = Mozilla.getInstance().getServiceManager();

    nsICookieManager2 cookieManager = (nsICookieManager2) serviceMan.getServiceByContractID("@mozilla.org/cookiemanager;1", nsICookieManager2.NS_ICOOKIEMANAGER2_IID);
    nsISimpleEnumerator enumerator = cookieManager.getEnumerator();

    FlowEditor.log("Cookie: 'aDomain' 'aPath' 'aName' 'aValue' 'aIsSecure' 'aIsHttpOnly' 'aIsSession' 'aExpiry'"); 
    while(enumerator.hasMoreElements()) {
      nsICookie2 cookie = (nsICookie2) enumerator.getNext().queryInterface(nsICookie2.NS_ICOOKIE2_IID);
      // aDomain, aPath, aName, aValue, aIsSecure, aIsHttpOnly, aIsSession, aExpiry
      FlowEditor.log("Cookie: '"+cookie.getHost() + "' '" 
          + cookie.getPath() + "' '"
          + cookie.getName() + "' '"
          + cookie.getValue() + "' '"
          + cookie.getIsSecure() + "' '"
          + cookie.getIsHttpOnly() + "' '" 
          + cookie.getIsSession() + "' '"
          + cookie.getExpiry() + "'");
    }

  }
  public void importCookies(Cookie[] cookies) {
    if(null == cookies || cookies.length == 0) return;
    nsIServiceManager serviceMan = Mozilla.getInstance().getServiceManager();
    nsICookieManager2 cookieManager = (nsICookieManager2) serviceMan.getServiceByContractID("@mozilla.org/cookiemanager;1", nsICookieManager2.NS_ICOOKIEMANAGER2_IID);
    for(int i = 0; i < cookies.length; i++) {
      Cookie cookie = cookies[i];
      if(null == cookie) continue;
      FlowEditor.log("Injecting cookie: '"+cookie.getDomain()+"' '"+cookie.getPath()+"' '"+cookie.getName()+"' '"+cookie.getValue()+"'");
      Date expiryDate = cookie.getExpiryDate();
      long expiry = System.currentTimeMillis() + (36000000*24); // expira daqui a 24 horas...
      if(null != expiryDate) expiry = expiryDate.getTime();
      cookieManager.add(cookie.getDomain(), cookie.getPath(), cookie.getName(), cookie.getValue(), cookie.getSecure(), cookie.isDomainAttributeSpecified(), cookie.isPersistent(), expiry);
    }
  }

}
