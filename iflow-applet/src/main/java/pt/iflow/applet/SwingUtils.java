package pt.iflow.applet;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.psicover.pdf.PDFViewerPanel;

/**
 * Classe utilitária para instanciação de objectos Swing vs AWT.
 * @author ombl
 *
 */
public class SwingUtils {
  
  private static Log log = LogFactory.getLog(SwingUtils.class);
  
  static boolean ALWAYS_USE_FRAME = true;
  
  static Window getComponentWindow(Component component) {
    if (component instanceof Window) {
      return (Window) component;
    }
    return SwingUtilities.getWindowAncestor(component);
  }
  

  static FileDialog getFileDialog(Component parentComponent) {
    FileDialog dialog;

    Window window = getComponentWindow(parentComponent);
    if (window instanceof Frame) {
      dialog = new FileDialog((Frame) window);
    } else {
      dialog = new FileDialog((Dialog) window);
    }
    return dialog;
  }

  static Window getDialog(Component parentComponent, String title, boolean modal) {
    if(ALWAYS_USE_FRAME) return new Frame(title);
    
    Window dialog;
    Window window = getComponentWindow(parentComponent);
    if (window instanceof Frame) {
      dialog = new Dialog((Frame) window, title, modal);
    } else {
      dialog = new Dialog((Dialog) window, title, modal);
    }
    return dialog;

  }

  
  static Container newPanel() {
    return new JPanel();
  }
  
  static Component newLabel(String text) {
    return new JLabel(text);
  }
  
  static Component newButton(String text, ActionListener ... listeners) {
    return newButton(text, null, listeners);
  }
  static Component newButton(String text, String actionCommand, ActionListener ... listeners) {
    JButton button = new JButton(text);
    button.setActionCommand(actionCommand);
    for(ActionListener l : listeners) 
      button.addActionListener(l);
    
    return button;
  }
  
  static void setBorder(Container panel, String title) {
    if(panel instanceof JPanel) {
      ((JPanel)panel).setBorder(BorderFactory.createTitledBorder(title));
    }

  }
  
  public static IVFile showOpenFileDialog(Component parent, String title, ExtensionFileFilter filter, String variable) {
    File theFile = null;
    if(OSUtils.isWindows()) {
      FileDialog jfc = SwingUtils.getFileDialog(parent);
      jfc.setFilenameFilter(filter);
      jfc.setTitle(title);
      jfc.setMode(FileDialog.LOAD);

      jfc.setVisible(true);

      String dirName = jfc.getDirectory();
      String fileName = jfc.getFile();
      if(fileName != null && dirName != null) {
        theFile = new File(dirName, fileName);
      }
    } else {
      JFileChooser jfc = new JFileChooser();
      jfc.setFileFilter(filter);
      jfc.setDialogTitle(title);
      int op = jfc.showOpenDialog(parent);
      if(op == JOptionPane.OK_OPTION)
        theFile = jfc.getSelectedFile();
    }

    log.debug("File '" + theFile + "' selected."); //$NON-NLS-1$ //$NON-NLS-2$
    
    if (theFile != null && theFile.canRead() && theFile.isFile()) {
      return new FileVFile(theFile, variable);
    }
    return null;
  }
  
  private static Map<String,Icon> iconCache = new HashMap<String, Icon>();
  
  private static byte [] getData(String name) {

    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    try (InputStream is = PDFViewerPanel.class.getResourceAsStream(name)){
      int r;
      byte [] b = new byte[4096];
      while((r = is.read(b))>=0) {
        bout.write(b, 0, r);
      }
      is.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bout.toByteArray();
  }

  static Icon loadIcon(String resource) {
    if(iconCache.containsKey(resource))
      return iconCache.get(resource);
    byte [] data = getData(resource);
    Icon icon = null;
    if(null != data) 
       icon = new ImageIcon(data);
    iconCache.put(resource, icon);
    return icon;
  }

  
  static void dispose() {
    // release icon cache
    iconCache.clear();
  }
}
