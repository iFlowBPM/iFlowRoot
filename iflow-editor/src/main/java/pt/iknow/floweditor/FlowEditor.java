package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: FlowEditor
 *
 *  desc: classe de arranque da aplicação
 *
 ****************************************************/

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import pt.iknow.iflow.RepositoryClient;

import com.jgoodies.looks.plastic.PlasticTheme;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/*******************************************************************************
 * Programa
 */
public class FlowEditor implements Runnable {

  private static final long serialVersionUID = 100L;

  transient public static boolean bREP_ONx = false;
  transient private static boolean lastStatus = false;
  transient private static RepositoryClient _rRepository = null;
  transient private static Logger logger = null;
  transient private static StatusChangedListener statusChangedListener = null;
  transient private static SWTRunner swtRunner = new SWTRunner();

  static {
    File homeConfig = new File(FlowEditorConfig.CONFIG_DIR);
    File logFile = new File(homeConfig, "floweditor.log");
    String lF = logFile.getAbsolutePath();
    System.out.println(lF);

    BasicConfigurator.configure();
    Logger rootLogger = Logger.getRootLogger();
    rootLogger.setLevel(Level.WARN);
    rootLogger.removeAllAppenders();
    Appender appender = new ConsoleAppender(new PatternLayout("%m%n"));
    appender.setName("console");
    rootLogger.addAppender(appender);
    try {
      Layout layout = new PatternLayout("%d [%t] %-5p %c - %m%n");
      appender = new FileAppender(layout, lF, false);
      appender.setName("file");
      rootLogger.addAppender(appender);
    } catch (Throwable t) {t.printStackTrace();}
    logger = Logger.getLogger("FlowEditor");
    logger.setLevel(Level.INFO);
    logger.info("Logger setup complete");
  }

  public static RepositoryClient getRepositoryx() {
    if (testRespositoryx()) {
      return _rRepository;
    }
    return null;
  }

  public static void setRepositoryx(RepositoryClient arRepository) {
    _rRepository = arRepository;
    bREP_ONx = _rRepository!=null;
  }

  public static boolean testRespositoryx() {
    if(_rRepository == null) return false; // nothing to do
    bREP_ONx = _rRepository.checkConnection();

    if(lastStatus != bREP_ONx) {
      boolean last = lastStatus;
      boolean current = bREP_ONx;
      lastStatus = bREP_ONx;
      fireStatusChanged(last, current);
    }

    return bREP_ONx;
  }

  // apenas um listener eh suficiente para ja.

  public static void setStatusChangedListener(StatusChangedListener listener) {
    statusChangedListener = listener;
  }

  protected static void fireStatusChanged(boolean oldStatus, boolean newStatus) {
    if(null != statusChangedListener) statusChangedListener.statusChanged(oldStatus, newStatus);
  }

  static void printProps(Map<?,?> map) {
    Object oo=null;
    for(Iterator<?> i =(new TreeSet<Object>(map.keySet())).iterator(); i.hasNext();)
      System.out.println((oo=i.next())+"="+map.get(oo));
  }

  /*****************************************************************************
   * funcao que inicia o programa quando corremos o programa normalmente
   */
  public static void main(String argv[]) {
    // Hard coded file encoding. Only works if this is the first class to be called?
    System.setProperty("file.encoding", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
    System.setProperty("swing.aatext", "true"); //$NON-NLS-1$ //$NON-NLS-2$
    MozInit.setupMozilla();
    FlowEditor.log("Default charset: " + Charset.defaultCharset());

    try {
      // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//      UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticLookAndFeel");
//      UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
//      UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
      PlasticXPLookAndFeel laf = new PlasticXPLookAndFeel();
      PlasticTheme theme = new ExperienceBlue();//Royale();
      PlasticXPLookAndFeel.setPlasticTheme(theme);
      UIManager.setLookAndFeel(laf);

//      UIManager.setLookAndFeel("com.lipstikLF.LipstikLookAndFeel"); //$NON-NLS-1$
    } catch (Exception ex) {
      FlowEditor.log("Unable to load native look and feel"); //$NON-NLS-1$
    }

    // Carregar as configuracoes guardadas sobre locale
    FlowEditorConfig cfg = FlowEditorConfig.loadConfig();
    String useLocale = cfg.getUseLocale();
    if(FlowEditorConfig.LOCALE_USE_SELECTED.equals(useLocale)) {
      Mesg.setLocale(cfg.getSelectedLocale()); // Last iFlow
    } // else use default

    /* criar janela */
    String fileName = null;
    if (argv.length == 1) {
      fileName = argv[0];
    }

    runMvcWebServer();
    runSWT();
    runEditor(fileName);
  }

  public static void runEditor(String fileName) {
    // new Janela(fileName);
    // new FlowEditor(fileName).run();
    FlowEditor editor = new FlowEditor(fileName);
    SwingUtilities.invokeLater(editor);

  }


  public static void runSWT() {
    new Thread(swtRunner).start();
  }

  public static void runMvcWebServer() {
    MVCWebServer.getInstance().start();
  }

  public static void stopMvcWebServer() {
    MVCWebServer.getInstance().stop();
  }


  public static void shutdown() {
    MozInit.unregisterHandlers();
    disposeSWT();
    stopMvcWebServer();

    // flush anything
    try {
      Thread.sleep(1000); // wait 1 second
    } catch (InterruptedException e) {
    }

    System.exit(0);
  }



  private String fileName = null;

  public FlowEditor() {
    super();
  }

  private FlowEditor(String fileName) {
    this.fileName = fileName;
  }

  public void run() {
    new Janela(fileName);
  }

  public static void log(String msg) {
    //logger.info(msg);
  }

  public static void log(String msg, Throwable t) {
    logger.info(msg, t);
  }

  public static interface StatusChangedListener {
    public abstract void statusChanged(boolean oldStatus, boolean newStatus);
  }

  public static String getActionURL() {
    return MVCWebServer.getInstance().getActionURL();
  }

  public static Display getRootDisplay() {
    return swtRunner.getRootDisplay();
  }

  public static Shell getRootShell() {
    return swtRunner.getRootShell();
  }

  public static void disposeSWT() {
    swtRunner.disposeSWT();
  }


  private static final class SWTRunner implements Runnable {
    private Display rootDisplay = null;
    private Shell rootShell = null;

    public void runSWT() {
      new Thread(this).start();
    }

    public Display getRootDisplay() {
      return rootDisplay;
    }

    public Shell getRootShell() {
      return rootShell;
    }

    public void disposeSWT() {
      if(null == rootDisplay || rootShell == null) return;
      rootDisplay.syncExec(new Runnable(){
        public void run(){
          rootShell.dispose();
        }
      });
    }

    public void run() {
      rootDisplay = new Display();
      rootShell = new Shell(rootDisplay);
      while (!rootShell.isDisposed()) {
        if (!rootDisplay.readAndDispatch()) {
          rootDisplay.sleep();
        }
      }
      rootDisplay.dispose();
      rootShell = null;
      rootDisplay = null;
    }
  }


}
