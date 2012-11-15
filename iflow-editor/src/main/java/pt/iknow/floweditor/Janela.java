package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Janela
 *
 *  desc: janela principal
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsyntaxpane.DefaultSyntaxKit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMWindow;
import org.xml.sax.InputSource;

import pt.iflow.api.msg.IMessages;
import pt.iflow.api.transition.FlowStateHistoryTO;
import pt.iflow.api.transition.FlowStateLogTO;
import pt.iflow.api.utils.CheckSum;
import pt.iflow.api.utils.FlowInfo;
import pt.iflow.api.xml.FlowMarshaller;
import pt.iflow.api.xml.ProcessStateHistoryMarshaller;
import pt.iflow.api.xml.ProcessStateLogMarshaller;
import pt.iflow.api.xml.codegen.flow.XmlFlow;
import pt.iknow.XmlLetterType.TipoGrande;
import pt.iknow.XmlLetterType.TipoPequeno;
import pt.iknow.XmlLetterType.XmlLetterType;
import pt.iknow.floweditor.Cor.Fonts;
import pt.iknow.floweditor.blocks.AbstractAlteraAtributos;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.floweditor.mozilla.MozillaBrowser;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.iflow.RepositoryStatusListener;
import pt.iknow.iflow.RepositoryWebClient;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.swing.CloseableTabbedPane;
import pt.iknow.utils.swing.CloseableTabbedPaneListener;
import pt.iknow.utils.swing.GradientLabel;
import pt.iknow.utils.swing.TabClosedListener;

/*******************************************************************************
 * Janela
 */
public class Janela extends JFrame implements ActionListener, KeyListener, IJanela {
  private static final long serialVersionUID = 1L;

  private static final int BORDER_ALPHA = 0xC0;
  private static final int BORDER_CORNER_RADIUS = 12;
  private static final int INNER_CORNER_RADIUS = 4;
  private static final int BORDER_WIDTH = 2;

  private static final HashMap<String, Color> hmBColors = new HashMap<String, Color>();
  private static final Color BORDER_COLOR_RED = new Color(0xff, 0x5a, 0x3f, BORDER_ALPHA);
  private static final Color BORDER_COLOR_BROWN = new Color(0x8b, 0x8b, 0x5a, BORDER_ALPHA);
  private static final Color BORDER_COLOR_YELLOW = new Color(0xff, 0xcc, 0x00, BORDER_ALPHA);
  private static final Color BORDER_COLOR_BLUE = new Color(0x9f, 0xc8, 0xFF, BORDER_ALPHA);
  private static final Color BORDER_COLOR_GREEN = new Color(0x85, 0xcd, 0x00, BORDER_ALPHA);
  private static final Color BORDER_COLOR_BLACK = new Color(0x00, 0x00, 0x00, BORDER_ALPHA);
  private static final Color BORDER_COLOR_GREY = new Color(0xCC, 0xCC, 0xCC, BORDER_ALPHA);
  private static final Color DEFAULT_COLOR = new Color(0.86f, 0.85f, 0.81f, 0.82f);

  public static final String WEB_FORM_CLASS = "pt.iknow.floweditor.blocks.AlteraAtributosWebForm";

  public static FontMetrics FM_Tipo_8 = null;

  public static FontMetrics FM_Tipo_10 = null;

  static {
    hmBColors.put("stop.png", BORDER_COLOR_RED);
    hmBColors.put("sqldelete.png", BORDER_COLOR_RED);
    hmBColors.put("formulario.png", BORDER_COLOR_BROWN);
    hmBColors.put("condicao.png", BORDER_COLOR_YELLOW);
    hmBColors.put("copia.png", BORDER_COLOR_BLUE);
    hmBColors.put("start.png", BORDER_COLOR_GREEN);
    hmBColors.put("sqlselect.png", BORDER_COLOR_GREEN);
    hmBColors.put("sqlupdate.png", BORDER_COLOR_YELLOW);
    hmBColors.put("sqldelete.png", BORDER_COLOR_RED);
    hmBColors.put("sqlinsert.png", BORDER_COLOR_BROWN);
    hmBColors.put("sqlbatchinsert.png", BORDER_COLOR_BROWN);
    hmBColors.put("validacoes.png", BORDER_COLOR_GREY);
    hmBColors.put("email.png", BORDER_COLOR_GREEN);
    hmBColors.put("emailperfil.png", BORDER_COLOR_BLUE);
    hmBColors.put("emailintervenientes.png", BORDER_COLOR_RED);
    hmBColors.put("sms.png", BORDER_COLOR_YELLOW);
    hmBColors.put("smsperfil.png", BORDER_COLOR_BROWN);
    hmBColors.put("getuserinfo.png", BORDER_COLOR_BLUE);
    hmBColors.put("getuserinfo.png", BORDER_COLOR_BLUE);
    hmBColors.put("getuserup.png", BORDER_COLOR_BLUE);
    hmBColors.put("checkauthentication.png", BORDER_COLOR_BLUE);
    hmBColors.put("forwardto.png", BORDER_COLOR_GREEN);
    hmBColors.put("forwardup.png", BORDER_COLOR_GREEN);
    hmBColors.put("getorganicalunitinfo.png", BORDER_COLOR_YELLOW);
    hmBColors.put("getorganicalunitparent.png", BORDER_COLOR_YELLOW);
    hmBColors.put("forwardup.png", BORDER_COLOR_GREEN);
    hmBColors.put("isinprofiles.png", BORDER_COLOR_BROWN);
    hmBColors.put("isinprofilestext.png", BORDER_COLOR_BROWN);
    hmBColors.put("beanshell.png", BORDER_COLOR_BLACK);
    hmBColors.put("juncaoexclusiva.png", BORDER_COLOR_GREEN);
    hmBColors.put("bifurcacao.png", BORDER_COLOR_GREEN);
    hmBColors.put("sincronizacao.png", BORDER_COLOR_GREEN);
    hmBColors.put("date.png", BORDER_COLOR_GREY);
    hmBColors.put("subflow.png", BORDER_COLOR_BROWN);
    hmBColors.put("arraytrim.png", BORDER_COLOR_YELLOW);
    hmBColors.put("switchprocesso.png", BORDER_COLOR_GREEN);
    hmBColors.put("switchprocessoasuser.png", BORDER_COLOR_GREEN);
    hmBColors.put("agrupamento.png", BORDER_COLOR_BLUE);
    hmBColors.put("savetodb.png", BORDER_COLOR_BLACK);
    hmBColors.put("cleanprocessoerror.png", BORDER_COLOR_RED);
    hmBColors.put("pesquisaprocesso.png", BORDER_COLOR_YELLOW);
    hmBColors.put("detalheprocesso.png", BORDER_COLOR_BROWN);
    hmBColors.put("openprocesso.png", BORDER_COLOR_GREEN);
    hmBColors.put("eventos.png", BORDER_COLOR_RED);

    LerFicheiroLetras();

  }

  /* tratamento de menus */
  private Trata_Menus t_m = null;

  /* canvas com o desenho */
  // Desenho canvas = null;
  private ExampleFileFilter filter = new ExampleFileFilter(Mesg.FileExtension, Mesg.FileExtension);
  private ExampleFileFilter libFilter = new ExampleFileFilter(Mesg.LibraryExtension, Mesg.LibraryExtension);
  // JScrollPane scrollPane = null;

  private JTabbedPane libraryTabPane = new JTabbedPane(JTabbedPane.TOP);
  private JPanel blockSorterPane = new JPanel(new BorderLayout());
  
  // JTabbedPane tabPane = new JTabbedPane(JTabbedPane.TOP,
  // JTabbedPane.SCROLL_TAB_LAYOUT);
  private CloseableTabbedPane tabPane = new CloseableTabbedPane();
  private JToolBar toolBar = null;
  private JPanel libraryPane = new JPanel(new BorderLayout());
  private JPanel blockPane = new JPanel(new BorderLayout());
  private JToggleButton showLibButton = null;
  private JToggleButton showBlockSorterButton = null;
  private JPanel panelOnline = null;
  private File cacheFolder = null;
  private File cacheInfoFile = null;
  private File iconCacheFolder = null;
  private File libCacheFolder = null;
  private File flowCacheFolder = null;
  private File subFlowCacheFolder = null;
  private File i18nCacheFolder = null;
  private FlowRepUrl iFlowURL = null;
  private FlowEditorConfig cfg = null;
  private RepositoryClient repository = null; 

  private LibrarySet _librarySet = new LibrarySet();
  private File lastLibParent = new File("libraries"); //$NON-NLS-1$
  private File lastParent = new File("."); //$NON-NLS-1$

  private List<JComponent> onlineComponents = new LinkedList<JComponent>();
  private List<JComponent> desenhoDependentComponents = new LinkedList<JComponent>();
  private List<JComponent> processStateComponents = new LinkedList<JComponent>();

  private static Hashtable<String, Image> _htImageCache = new Hashtable<String, Image>();
  private GradientLabel gradientLabel = null;
  private boolean drawAll = false;
  private boolean drawLines = true;
  private boolean drawComponentes = true;
  private boolean antiAlias = true;
  private boolean confirmarSaida = true;
  private IMessages blockMsg = null;
  
  private static Janela instance = null;

  private Shell iflowShell = null;
  private MozillaBrowser iflowBrowser = null;
  private Runnable iflowWindowDispatcher = new Runnable() {
    public void run() {
      if(null != iflowShell) {
        iflowShell.forceActive();
        iflowShell.forceFocus();
        return;
      }

      iflowShell = new Shell();
      iflowShell.setLayout(new FillLayout());
      iflowShell.addShellListener(new ShellListener() {

        public void shellActivated(ShellEvent e) {
          iflowBrowser.forceFocus();
        }

        public void shellClosed(ShellEvent e) {
          iflowShell = null;
        }

        public void shellDeactivated(ShellEvent e) {
        }

        public void shellDeiconified(ShellEvent e) {
          iflowBrowser.forceFocus();
        }

        public void shellIconified(ShellEvent e) {
        }

      });

      iflowBrowser = new MozillaBrowser(iflowShell);

      iflowBrowser.addProgressListener(new ProgressListener() {
        public void changed(ProgressEvent event) {}

        public void completed(ProgressEvent event) {
          if(event.total == event.current) {
            nsIDOMWindow domWindow = (nsIDOMWindow) iflowBrowser.getBrowser().getContentDOMWindow();
            nsIDOMDocument doc = domWindow.getDocument();
            nsIDOMHTMLDocument htmlDoc = (nsIDOMHTMLDocument)doc.queryInterface(nsIDOMHTMLDocument.NS_IDOMHTMLDOCUMENT_IID);
            if(null == htmlDoc) return;
            nsIDOMElement elem = htmlDoc.getElementById("top_logout_link");
            if(null == elem) return;
            nsIDOMNode parent = elem.getParentNode();
            nsIDOMNode prev = elem.getPreviousSibling();
            parent.removeChild(elem);  // remove link
            parent.removeChild(prev);  // remove adjacent " : "
          }
        }
      });
      iflowBrowser.importCookies(getRepository().getCookies());
      iflowBrowser.setUrl(getRepository().getBaseURL()+"/main.jsp");

      iflowShell.setSize(1024, 768);
      iflowShell.setText("iFlow");
      iflowShell.setActive();
      iflowShell.open();
    }
  };



  public static Janela getInstance() {
    return instance;
  }

  /*****************************************************************************
   * Cria a janela sem informacao de biblioteca
   */
  public Janela() {
    this(null);
  }

  /*****************************************************************************
   * Cria janela e abre um ficheiro por default
   */
  public Janela(String fileName) {
    if (null != instance)
      throw new InvalidParameterException("There can be only one! Please reuse existing instance.");
    instance = this;
    FM_Tipo_8 = getFontMetrics(Cor.getInstance().getFont(Fonts.Tipo_8_Plain));
    FM_Tipo_10 = getFontMetrics(Cor.getInstance().getFont(Fonts.Tipo_10_Plain));

    setTitle("Flow Editor " + Version.VERSION);

    cfg = FlowEditorConfig.loadConfig();

    (new AcessoFicheiroCores()).LerFicheiroCores();

    setupFrame();
    setVisible(true);
    iFlowURL = login(cfg);

    updateLocalCache();
    
    // load block messages
    blockMsg = getCachedBlockMsg();

    // load libraries
    FlowEditor.log("Loading libraries....");
    File[] libs = getCachedLibraries();

    for (File file : libs) {
      Library library = null;
      try {
        library = Ler_Biblioteca.getLibraryFromFile(file, _librarySet);
        _librarySet.addLibrary(library);
      } catch (Exception e) {
        FlowEditor.log(Messages.getString("Desenho.lib_read_error") + file); //$NON-NLS-1$
      }
    }

    adicionaComponentesJanela();

    if (null != fileName) {
      /* abrir ficheiro */
      openFlowFile(new File(fileName));
    }

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent evt) {
        Desenho d = getSelectedDesenho();
        if (null != d)
          d.updateCanvasSize();
      }
    });

    // Initialize Synthax Highlighting
    DefaultSyntaxKit.initKit();

    setVisible(true);
    this.repaint();

    /* visualizar janela */
    // canvas.ActualizaTamanho();
    // this.requestFocus();

    FlowEditor.setStatusChangedListener(new FlowEditor.StatusChangedListener() {
      public void statusChanged(boolean oldStatus, boolean newStatus) {
        notifyOnline(true);
      }
    });

    notifyOnline(false);
  }

  public void addOnlineComponent(JComponent component) {
    onlineComponents.add(component);
  }

  public void addDesenhoDependentComponent(JComponent component) {
    desenhoDependentComponents.add(component);
  }
  
  public void addProcessStateComponents(JComponent component) {
    processStateComponents.add(component);
  }


  /*****************************************************************************
   * adiciona os componentes a janela - desenho, componentes de biblioteca
   * menus, toolbar
   */
  private void adicionaComponentesJanela() {
    /* limpa */
    // janela.jTabbedPane1.removeAll();
    /* adiciona */
    Container contentPane = getContentPane();
    libraryTabPane.setTabPlacement(JTabbedPane.BOTTOM);
    actualizaLibrarySet(null);
    SimpleCrossIcon icon = new SimpleCrossIcon();
    contentPane.setLayout(new BorderLayout());
    
    // BLOCK LIBRARY
    JPanel panel = new JPanel(new BorderLayout());
    gradientLabel = new GradientLabel("  " + Messages.getString("Janela.blockLibraryTitle")); //$NON-NLS-1$
    gradientLabel.setFont(gradientLabel.getFont().deriveFont(Font.BOLD));
    // O valor 20 foi determinado pelos testes com o look and feel actual
    gradientLabel.setPreferredSize(new Dimension(-1, 20));

    JButton closeButton = new JButton(icon);
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        toggleLibraryPane(); // se clicou é porque está visivel
      }
    });
    closeButton.setBorderPainted(false);
    gradientLabel.setPreferredSize(new Dimension(20, -1));
    panel.add(closeButton, java.awt.BorderLayout.EAST);
    panel.add(gradientLabel, java.awt.BorderLayout.CENTER);

    libraryPane.add(panel, java.awt.BorderLayout.NORTH);
    libraryPane.add(libraryTabPane, java.awt.BorderLayout.CENTER);
    libraryPane.setBorder(BorderFactory.createEtchedBorder());
    
    // MAIN PANE
    contentPane.add(tabPane, java.awt.BorderLayout.CENTER);
    contentPane.add(libraryPane, java.awt.BorderLayout.EAST);
    contentPane.add(blockPane,java.awt. BorderLayout.SOUTH);

    //contentPane.add(new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, tabPane, libraryPane), BorderLayout.CENTER);

    // criar evento para notificar a mudanca de tab
    tabPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        notifyDesenho();
      }
    });
    tabPane.addCloseableTabbedPaneListener(new CloseableTabbedPaneListener() {
      public boolean closeTab(int tabIndexToClose) {
        return !closeFlow(false);
      }
    });

    tabPane.addTabClosedListener(new TabClosedListener() {
      public void tabClosed() {
        notifyDesenhoDependentComponents();
      }
    });

    /* config */
    createToolbar();

    /* menus */
    t_m = new Trata_Menus();
    t_m.Inicia_Popup_Vazio(_librarySet, this);
    t_m.Inicia_Popup_Componente(_librarySet, this);
    t_m.Inicia_Popup_Linha(this);
    setJMenuBar(t_m.Cria_Menus(_librarySet, this));

    // updateLabel();

    notifyDesenhoDependentComponents();
  }

  private void notifyDesenhoDependentComponents() {
    boolean enabled = tabPane.getTabCount() > 0;
    for(JComponent component : desenhoDependentComponents) {
      component.setEnabled(enabled);
    }
    notifyProcessStateComponents();
  }

  private void notifyProcessStateComponents() {
    boolean enabled = (getSelectedDesenho() != null && !getSelectedDesenho().isEditable());
    for (JComponent component : processStateComponents) {
      component.setEnabled(enabled);
    }
    displaySortedBlocks();
  }
  
  private void displaySortedBlocks() {
    boolean visible = false;
    if(getSelectedDesenho() != null && !getSelectedDesenho().isEditable()) {
      DesenhoEstadoProcesso desenho = (DesenhoEstadoProcesso) getSelectedDesenho();
      JPanel panel = new JPanel(new java.awt.BorderLayout());
      GradientLabel gradientLabel = new GradientLabel("  " + Messages.getString("Janela.blockSorter.title"));
      gradientLabel.setFont(gradientLabel.getFont().deriveFont(Font.BOLD));
      gradientLabel.setPreferredSize(new Dimension(-1, 20));
      JButton closeButton = new JButton(new SimpleCrossIcon());
      closeButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          toggleBlockSorterPane();
        }
      });
      closeButton.setBorderPainted(false);
      gradientLabel.setPreferredSize(new Dimension(20, -1));
      panel.add(closeButton, java.awt.BorderLayout.EAST);
      panel.add(gradientLabel, java.awt.BorderLayout.CENTER);

      blockPane.add(panel, java.awt.BorderLayout.NORTH);
      blockPane.add(blockSorterPane, java.awt.BorderLayout.CENTER);
      blockPane.setBorder(BorderFactory.createEtchedBorder());   

      visible = desenho.showSortedBlocks();
      blockPane.remove(blockSorterPane);
      blockSorterPane = desenho.getBlockPane();
      blockPane.add(blockSorterPane);
      
      desenho.setGlow(createImage("aureola.png", false, false));
      
      blockPane.repaint();
    }
    blockPane.setVisible(visible);
    if(showBlockSorterButton != null) {
      showBlockSorterButton.setSelected(visible);
    }
  }


  /*****************************************************************************
   * actualiza componentes de biblioteca
   */
  private void actualizaLibrarySet(String sSelTab) {

    int TAM = 0;
    /* adiciona _htLibraries */
    String[] saLibs = _librarySet.getLibraryKeys();
    for (int i = 0; i < saLibs.length; i++) {

      Library bib = _librarySet.getLibrary(saLibs[i]);
      if (bib.getName().equals("Default")) //$NON-NLS-1$
        continue;
      
      String displayName = bib.getName();
      if(StringUtils.isNotBlank(bib.getNameKey())) {
        displayName = blockMsg.getString(bib.getNameKey());
      }
      
      String tooltip = bib.getDescription();
      if(StringUtils.isNotBlank(bib.getDescriptionKey())) {
        tooltip = blockMsg.getString(bib.getDescriptionKey());
      }
      
      Canvas_Janela_Biblioteca cjb = new Canvas_Janela_Biblioteca(bib, this);
      
      int tabPos = libraryTabPane.indexOfTab(displayName);
      if (tabPos == -1) {
        libraryTabPane.addTab(displayName, null, cjb.sp, tooltip);
      } else {
        libraryTabPane.setComponentAt(tabPos, cjb.sp);
        libraryTabPane.setToolTipTextAt(tabPos, tooltip);
      }

      TAM = Math.max(TAM, cjb.tamX);
    }

    if (null != t_m) {
      t_m.Inicia_Popup_Vazio(_librarySet, this);
      t_m.Cria_Menu_C_Biblioteca(_librarySet, this);
    }

    if (sSelTab != null) {
      int nSelTab = this.libraryTabPane.indexOfTab(sSelTab);
      this.libraryTabPane.setSelectedIndex(nSelTab);
    }
  }
  
  private void toggleBlockSorterPane() {
    if (!getSelectedDesenho().isEditable()) {
      DesenhoEstadoProcesso desenho = (DesenhoEstadoProcesso) getSelectedDesenho();
      boolean visible = !desenho.showSortedBlocks();
      desenho.setShowSortedBlocks(visible);
      blockPane.setVisible(visible);
      if(showBlockSorterButton != null) {
        showBlockSorterButton.setSelected(visible);
      }
    }
    repaint();
  }
  
  private void toggleLibraryPane() {
    boolean visible = !libraryPane.isVisible();
    libraryPane.setVisible(visible);
    t_m.setShowLibSelected(visible);
    showLibButton.setSelected(visible);
    repaint();
  }

  private void notifyDesenho() {
    // updateLabel();
    Desenho d = getSelectedDesenho();
    if (null != d) {
      d.updateCanvasSize();
      // FlowEditor.log("Event on " + d.getName()); //$NON-NLS-1$
    }
    notifyDesenhoDependentComponents();
  }

  /**
   * Get the currently selected instance of Desenho or null if not found
   * 
   * @return
   */
  public Desenho getSelectedDesenho() {
    Component component = tabPane.getSelectedComponent();
    if (null == component || !(component instanceof DesenhoScrollPane))
      return null;

    return ((DesenhoScrollPane) component).getDesenho();
  }

  public void setSelectedDesenho(Desenho d) {
    if(null == d) return;
    int componentCount = tabPane.getComponentCount();
    for(int i = 0; i < componentCount; i++) {
      Component c = tabPane.getComponentAt(i);
      if(null == c || !(c instanceof DesenhoScrollPane)) continue;
      if(((DesenhoScrollPane) c).getDesenho() == d) {
        tabPane.setSelectedIndex(i);
        return;
      }
    }
  }

  /*****************************************************************************
   * funcao que trata de accoes NO menu ficheiro
   * 
   * Tratar do código que fica do lado do desenho
   */
  public void processFileMenuActions(String accao) {
    Desenho desenho = getSelectedDesenho();

    /* iSAIR do programa */
    if (accao.equals(Mesg.Sair)) {

      exit();

    } else if ((accao.equals(Mesg.ImportarSubFlow) || accao.equals(Mesg.ImportarFicheiro)) && repository.checkConnection()) {

      FlowEditor.log("Downloading from Repository ..."); //$NON-NLS-1$

      DownloadFlowDialog df = null;
      boolean isFlow = accao.equals(Mesg.ImportarFicheiro);
      df = new DownloadFlowDialog(this, isFlow);
      if (df.getFlowName() == null)
        return;

      this.aEspera();

      String flowName = df.getFlowName();
      int flowVersion = df.getFlowVersion();

      byte[] flowData = downloadFlow(flowName, isFlow, flowVersion);

      if (flowData != null) {
        try {
          DesenhoScrollPane scrollPane = new DesenhoScrollPane(this, flowData, flowName, flowVersion);
          tabPane.addTab(scrollPane.getName(), scrollPane);
          tabPane.setSelectedComponent(scrollPane);
          scrollPane.getDesenho().setLastParent(this.lastParent);
          scrollPane.getDesenho().updateCanvasSize();
          repaint();
        } catch(InvalidFlowException e) {
          new Erro(e.getMessage(), this);
        }
      }

    } else if (accao.equals(Mesg.ExportarFicheiro)) {
      FlowEditor.log("Uploading to Repository ..."); //$NON-NLS-1$

      desenho.uploadFlow();
    } else if (accao.equals(Mesg.ExportarSubFlow)) {
      FlowEditor.log("Uploading SubFlow to Repository ..."); //$NON-NLS-1$

      desenho.uploadSubFlow();
    } else if (accao.equals(Mesg.MudaNomeFluxo)) {
      desenho.renameFlow();
    } else if (accao.equals(Mesg.GravarFicheiroComo)) {
      /* gravar ficheiro */
      exportFlowToFile(true);
    } else if (accao.equals(Mesg.FileClose)) {
      /* fechar fluxo */
      closeFlow();
    } else if (accao.equals(Mesg.FileCloseAll)) {
      /* fechar todos os fluxos */
      closeAllFlows();
    } else if (accao.equals(Mesg.FileCloseOthers)) {
      /* fechar os outros fluxos */
      closeOtherFlows();
    } else if (accao.equals(Mesg.SwitchWorkspace)) {
      switchWorkspace();
    } else if (accao.equals(Mesg.MenuPrint)) {
      desenho.Printing(accao);
    } else if (accao.equals(Mesg.AdicionarBiblioteca)) {
      this.aEspera();
      String sSelTab = null;
      JFileChooser fd = new JFileChooser(this.lastLibParent); //$NON-NLS-1$
      fd.setFileFilter(this.libFilter);
      fd.setMultiSelectionEnabled(true);
      int returnVal = fd.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        this.lastLibParent = fd.getCurrentDirectory();
        File[] _file = fd.getSelectedFiles();
        for (int z = 0; z < _file.length; z++) {
          try {
            Library b = Ler_Biblioteca.getLibraryFromFile(_file[z], _librarySet);
            _librarySet.addLibrary(b);
            sSelTab = b.getName();
            b.setDirectory(fd.getSelectedFile().getPath());
          } catch (Exception er) {
            new Erro(Mesg.ErroBiblioteca, this);
          }
        }
        actualizaLibrarySet(sSelTab);

        this.repaint();
      }
    } else if (accao.equals(Mesg.DownloadBiblioteca) && repository.checkConnection()) {
      FlowEditor.log("Downloading library from Repository ..."); //$NON-NLS-1$
      DownloadLibrary dl = new DownloadLibrary(this, true, _librarySet);

      if (dl.isOk()) {
          String sSelTab = null;

          Library[] ba = dl.getSelectedLibraries();
          if (ba != null) {
            this.aEspera();

            for (int i = 0; i < ba.length; i++) {
              _librarySet.addLibrary(ba[i]);
              sSelTab = ba[i].getName();
            }

            actualizaLibrarySet(sSelTab);
            this.repaint();
          }
        }
    } else if (accao.equals(Mesg.Novo)) { /* novo projecto */
      // Pede o nome
      String[] newName = getFlowName(Mesg.Novo, "", "");
      if (null != newName) {
        // desenho = new Desenho(this, newName[0], newName[1]);
        DesenhoScrollPane scrollPane = new DesenhoScrollPane(this, newName[0], newName[1]);
        tabPane.addTab(scrollPane.getName(), scrollPane);
        tabPane.setSelectedComponent(scrollPane);
        scrollPane.getDesenho().updateCanvasSize();
        repaint();
      }
    } else if (accao.equals(Mesg.LerFicheiro)) { /* ler ficheiro */
      JFileChooser fd = new JFileChooser(this.lastParent);
      fd.setFileFilter(this.filter);
      fd.setMultiSelectionEnabled(false);
      int returnVal = fd.showOpenDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        openFlowFile(fd.getSelectedFile());
      }
    } else if (StringUtils.equals(accao, Mesg.ViewProcessState)) {
      viewProcessState();
    } else if (StringUtils.equals(accao, Mesg.RefreshProcessState)) {
      refreshProcessState();
    }
    // force return to normal
    this.normal();
  }
  
  private void refreshProcessState() {
    FlowEditor.log("Refresh process state");

    Desenho desenho = getSelectedDesenho();
    if (!desenho.isEditable()) {
      DesenhoEstadoProcesso estadoProcesso = (DesenhoEstadoProcesso) desenho;
      String flowName = estadoProcesso.getFlowName();
      String flowid = estadoProcesso.getFlowId();
      String pnumber = estadoProcesso.getPNumber();
      estadoProcesso.clearCaches();
      if (StringUtils.isNotBlank(flowName) && StringUtils.isNotBlank(flowid) && StringUtils.isNotBlank(pnumber)) {
        this.processState(flowName, Integer.parseInt(flowid), pnumber, false);
      }
    }
  }
  
  private void viewProcessState() {
    FlowEditor.log("View process state");

    boolean exit;
    while(true) {
      exit = true;
      ViewProcessStateDialog dialog = new ViewProcessStateDialog(this);
      Integer flowid = dialog.getFlowId();
      String pnumber = dialog.getProcessNumber();
      if (flowid == null) {
        return;      
      }

      this.aEspera();

      String flowName = dialog.getFullFlowName();
      exit = processState(flowName, flowid, pnumber, true);
      if(exit) {
        break;
      }
    }
  }
  
  private boolean processState(String flowName, int flowid, String pnumber, boolean createNewTab) {
    boolean exit = true;
    int flowVersion = -1;
    byte[] flowData = downloadFlow(flowName, true, flowVersion);
    List<FlowStateHistoryTO> flowStateHist = downloadProcessStateHistory(flowid, pnumber);
    
    if (flowData != null) {
      int response = JOptionPane.YES_OPTION;
      if (flowStateHist.isEmpty()) {
        response = JOptionPane.showConfirmDialog(this, Messages.getString("ViewProcessStateDialog.empty.message", pnumber, "" + flowid), Messages
            .getString("ViewProcessStateDialog.empty.title"), JOptionPane.YES_NO_OPTION);
      }
      if(response == JOptionPane.YES_OPTION) {
        try {
          DesenhoScrollPane scrollPane = new DesenhoScrollPane(this, flowData, flowName, flowVersion, flowStateHist);
          scrollPane.getDesenho().setFlowId("" + flowid);
          scrollPane.getDesenho().setPNumber(pnumber);
          if(createNewTab) {
            tabPane.addTab(scrollPane.getName(), scrollPane);
            if(libraryPane.isVisible()) {
              toggleLibraryPane();
            }
          } else {
            int index = tabPane.getSelectedIndex();
            this.closeFlow();
            tabPane.addTab(scrollPane.getName(), scrollPane, null, index);
          }
          tabPane.setSelectedComponent(scrollPane);
          
          scrollPane.getDesenho().setLastParent(this.lastParent);
          scrollPane.getDesenho().updateCanvasSize();
          repaint();
        } catch(InvalidFlowException e) {
          new Erro(e.getMessage(), this);
        }
      } else {
        exit = false;
      }
    }
    return exit;
  }

  /*****************************************************************************
   * trata accoes do barMenus de opcoes
   */

  public void processOptionMenu(String nome) {
    if (nome.equals(Mesg.DrawAll)) {
      drawAll = !drawAll;
    } else if (nome.equals(Mesg.VerLinhas)) {
      drawLines = !drawLines;
    } else if (nome.equals(Mesg.VerComponentes)) {
      drawComponentes = !drawComponentes;
    } else if (nome.equals(Mesg.AntiAlias)) {
      antiAlias = !antiAlias;
    } else if (nome.equals(Mesg.MudaCores)) {
      Cor.getInstance().alteraCores(this);
    } else if (nome.equals(Mesg.SaveColors)) {
      (new AcessoFicheiroCores()).GravarFicheiroCores();
    } else if (nome.equals(Mesg.About)) {
      new About(this);
    } else if (nome.equals(Mesg.Validate)) {
      processComponentMenuActions(Mesg.Validate);
    } else if (nome.equals(Mesg.RunGC)) {
      new RunGC(this);
    } else if (nome.equals(Mesg.ShowBlockLibrary)) {
      toggleLibraryPane();
    } else if (nome.equals(Mesg.OpenIFlow)) {
      openIFlowWindow();
    } else if (nome.equals(Mesg.ChooseBlock)) {
      Desenho d = getSelectedDesenho();
      if (null != d)
        d.searchComponenteBibilioteca();
    } else if (nome.equals(Mesg.SearchBlock)) {
      Desenho d = getSelectedDesenho();
      if (null != d)
        d.searchBlockByName();
    } else if (nome.equals(Mesg.SearchBlockWithVar)) {
      Desenho d = getSelectedDesenho();
      if (null != d)
        d.searchBlockByContents();
    } else if (nome.equals(Mesg.TemplateManager) || nome.equals(Mesg.TemplateManagerTooltip)) {
      Desenho d = getSelectedDesenho();
      if (null != d)
        d.templateManager();
    } else if(nome.equals(Mesg.ConfirmarSaida)) {
      FlowEditorConfig cfg = FlowEditorConfig.loadConfig();
      confirmarSaida = !confirmarSaida;
      cfg.setConfirmExit(!confirmarSaida);
      cfg.saveConfig();
    }

    this.repaint();
  }

  /**
   * Funcao que trata de accoes de popup menu.
   */
  public void processComponentMenuActions(String accao) {
    Desenho d = getSelectedDesenho();
    if (null == d)
      return;

    if (accao.equals(Mesg.Validate)) {
      d.verifyFlow();
    } else if (accao.equals(Mesg.MenuUndo)) {
      d.undo();
    } else if (accao.equals(Mesg.MenuInserePontoQuebra)) {
      d.insertBreakPoint();
    } else if (accao.equals(Mesg.MenuRetiraPontoQuebra)) {
      d.removeBreakPoint();
    } else if (accao.equals(Mesg.MenuDuplicar)) {
      d.duplica();
    } else if (accao.equals(Mesg.MenuMudarNome)) {
      d.renameBlock();
    } else if (accao.equals(Mesg.MenuEliminar)) {
      d.removeBlock();
    } else if (accao.equals(Mesg.AlteraAtributos)) {
      d.changeBlockAttributes();
    } else if (accao.equals(Mesg.MenuGotoStart)) {
      d.gotoLineStart();
    } else if (accao.equals(Mesg.MenuGotoEnd)) {
      d.gotoLineEnd();
    } else if (accao.equals(Mesg.MenuVerRegistos)) {
      d.viewStateLogs();
    }
  }

  public boolean isDrawAll() {
    return drawAll;
  }

  public boolean isDrawLines() {
    return drawLines;
  }

  public boolean isDrawComponentes() {
    return drawComponentes;
  }

  public boolean isAntiAlias() {
    return antiAlias;
  }

  private void openFlowFile(File flowFile) {
    try {
      DesenhoScrollPane scrollPane = new DesenhoScrollPane(this, flowFile);
      Desenho d = scrollPane.getDesenho();
      this.lastParent = flowFile.getParentFile();
      d.setLastParent(this.lastParent);
      tabPane.addTab(scrollPane.getName(), scrollPane);
      tabPane.setSelectedComponent(scrollPane);
      scrollPane.getDesenho().updateCanvasSize();
      repaint();
    } catch(InvalidFlowException e) {
      new Erro(e.getMessage(), this);
    }
  }

  private byte[] downloadFlow(String flowName, boolean isFlow, int version) {
    RepositoryClient rep = getRepository();
    if(!rep.checkConnection()) {
      return null;
    }
    if (flowName == null) {
      return null;
    }
    byte[] data = null;
    if (isFlow) {
      if(version < 0)
        data = rep.getFlow(flowName);
      else
        data = rep.getFlowVersion(flowName, version);
    } else {
      if(version < 0)
        data = rep.getSubFlow(flowName);
      else
        data = rep.getSubFlowVersion(flowName, version);
    }
    return data;
  }
  
  private List<FlowStateHistoryTO> downloadProcessStateHistory(int flowid, String pnumber) {
    List<FlowStateHistoryTO> retObj = new ArrayList<FlowStateHistoryTO>();
    RepositoryClient rep = getRepository();
    if(!rep.checkConnection()) {
      return retObj;
    }
    try {
      retObj = ProcessStateHistoryMarshaller.unmarshal(rep.getProcessStateHistory(flowid, pnumber));
    } catch (Exception e) {
      FlowEditor.log("Unable to unmarshal data.", e);
    }
    return retObj;
  }
  
  public List<FlowStateLogTO> downloadProcessStateLogs(int flowid, String pnumber, int state) {
    List<FlowStateLogTO> retObj = new ArrayList<FlowStateLogTO>();
    RepositoryClient rep = getRepository();
    if(!rep.checkConnection()) {
      return retObj;
    }
    byte[] data = rep.getProcessStateLog(flowid, pnumber, state);
    try {
      retObj = ProcessStateLogMarshaller.unmarshal(data);
    } catch (Exception e) {
      FlowEditor.log("Unable to unmarshal data.", e);
    }
    return retObj;
  }

  /*****************************************************************************
   * Funcao que grava o Ficheiro actual
   */
  private int exportFlowToFile(boolean force) {
    Desenho d = getSelectedDesenho();
    if (null == d)
      return Sair.NO;
    /* perguntar se quer gravar */
    if (force || d.isFlowChanged()) {
      File lastParent = d.getLastParent();
      if(null == lastParent) lastParent = this.lastParent;
      JFileChooser fd = new JFileChooser(lastParent);
      String filename = FilenameUtils.removeExtension(d.getFlowId()) + ".xml";
      fd.setSelectedFile(new File(lastParent, filename));
      fd.setFileFilter(this.filter);
      fd.setMultiSelectionEnabled(false);
      int returnVal = fd.showSaveDialog(this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File f = fd.getSelectedFile();
        if(f.exists()) {
          int r = new Sair(this, Mesg.ConfirmaExistente).getResposta();
          if(r != Sair.YES) return r;
        }

        lastParent = fd.getCurrentDirectory();
        this.lastParent = lastParent;
        d.setLastParent(lastParent);
        d.exportFlowToFile(fd.getSelectedFile());
      } else {
        return Sair.CANCEL;
      }
    }
    return Sair.YES;
  }

  private String[] getFlowName(String title, String lastName, String lastFlowId) {
    boolean flowNameSet = false;
    String flowId = lastFlowId;
    String flowName = lastName;
    while (!flowNameSet) {
      UploadFlow uf = new UploadFlow(this, title, lastName, lastFlowId);
      flowId = uf.getFlowId();
      flowName = uf.getFlowName();
      if (uf.wasCanceled()) {
        return null;
      }

      if (StringUtilities.isNotEmpty(flowId) && StringUtilities.isNotEmpty(flowName)) {
        RepositoryClient rep = getRepository();
        boolean fileExists = false;
        if(rep.checkConnection()) {
          // test if file exists in repository
          if (rep.hasExtendedAPI()) {
            FlowInfo finfo = rep.getFlowInfo(flowId);
            fileExists = (finfo != null);
          } else {
            String[] flows = rep.listFlows(); // hard way: brute force
            for (String flow : flows) {
              if (StringUtilities.isEqual(flow, flowId)) {
                fileExists = true;
                break;
              }
            }
          }
        }

        if (fileExists) {
          FlowEditor.log("Flow exists. Please confirm."); //$NON-NLS-1$
          Object [] options = new Object[]{ Mesg.Sim, Mesg.Nao };
          int opt = JOptionPane.showOptionDialog(this, Messages.getString("Desenho.confirm.flowExists"),  //$NON-NLS-1$
              Messages.getString("Desenho.title_warning"), JOptionPane.YES_NO_OPTION,  //$NON-NLS-1$
              JOptionPane.WARNING_MESSAGE, (Icon) null, options, options[1]);
          if (opt == JOptionPane.OK_OPTION) {
            flowNameSet = true;
          }
        } else {
          flowNameSet = true;
        }

      } else {
        flowNameSet = true;
      }
    }
    return new String[] { flowId, flowName };
  }

  /*****************************************************************************
   * cria a toolbar
   */
  private void createToolbar() {
    // Create the toolbar.
    toolBar = new JToolBar();

    JButton button = null;

    /* new flow */
    button = new JButton(new ImageIcon(createImage("menu_icon_new.png", false, false)));//$NON-NLS-1$
    button.setToolTipText(Mesg.ToolNovo);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.Novo);
      }
    });
    toolBar.add(button);

    /* new flow */
    button = new JButton(new ImageIcon(createImage("menu_icon_open.png", false, false)));//$NON-NLS-1$
    button.setToolTipText(Mesg.ToolAbre);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.LerFicheiro);
      }
    });
    toolBar.add(button);

    /* gravar ficheiro */
    button = new JButton(new ImageIcon(createImage("menu_icon_save.png", false, false)));//$NON-NLS-1$
    button.setToolTipText(Mesg.ToolGrava);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.GravarFicheiroComo);
      }
    });
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    toolBar.addSeparator();

    /* imprime */
    button = new JButton(new ImageIcon(createImage("menu_icon_print.png", false, false)));//$NON-NLS-1$
    button.setToolTipText(Mesg.ToolImprime);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.MenuPrint);
      }
    });
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    toolBar.addSeparator();

    boolean repOn = repository.checkConnection();
    
    /* validar entradas e saidas */
    button = new JButton(new ImageIcon(createImage("menu_icon_download.png", false, false))); //$NON-NLS-1$
    button.setToolTipText(Mesg.ImportarFicheiro);
    button.setEnabled(repOn);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.ImportarFicheiro);
      }
    });
    toolBar.add(button);
    onlineComponents.add(button);

    /* correr script */
    button = new JButton(new ImageIcon(createImage("menu_icon_upload.png", false, false))); //$NON-NLS-1$
    button.setToolTipText(Mesg.ExportarFicheiro);
    button.setEnabled(repOn);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.ExportarFicheiro);
      }
    });
    toolBar.add(button);
    onlineComponents.add(button);

    /* open iflow */
    button = new JButton(new ImageIcon(createImage("iflow.png", false, false)));//$NON-NLS-1$
    button.setToolTipText(Mesg.OpenIFlow);
    button.setEnabled(repOn);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        openIFlowWindow();
      }
    });
    toolBar.add(button);
    onlineComponents.add(button);

    toolBar.addSeparator();
    
    /* escolher novo bloco */
    button = new JButton(new ImageIcon(createImage("menu_icon_choose_block.png", false, false))); //$NON-NLS-1$
    button.setToolTipText(Mesg.ChooseBlock); //$NON-NLS-1$
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processOptionMenu(Mesg.ChooseBlock);
      }
    });
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    button = new JButton(new ImageIcon(createImage("menu_icon_search_block.png", false, false))); //$NON-NLS-1$
    button.setToolTipText(Mesg.SearchBlock);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processOptionMenu(Mesg.SearchBlock);
      }
    });
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    button = new JButton(new ImageIcon(createImage("menu_icon_search_var.png", false, false))); //$NON-NLS-1$
    button.setToolTipText(Mesg.SearchBlockWithVar);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processOptionMenu(Mesg.SearchBlockWithVar);
      }
    });
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    toolBar.addSeparator();
    button = new JButton(new ImageIcon(createImage("template_man.png", false, false))); //$NON-NLS-1$
    button.setEnabled(false);
    button.setToolTipText(Mesg.TemplateManagerTooltip);
    // Check WebForm class availability and enable 
    // TODO Criar um esquema de plugins!!!!!!
    try {
      loadGUIClass(Janela.WEB_FORM_CLASS);
      button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          processOptionMenu(Mesg.TemplateManager);
        }
      });
      button.setEnabled(true);
    } catch (ClassNotFoundException e) {}
    toolBar.add(button);
    addDesenhoDependentComponent(button);

    toolBar.addSeparator();
    
    /* ver estado processo */
    button = new JButton(new ImageIcon(createImage("menu_icon_process_view.png", false, false)));
    button.setToolTipText(Mesg.ViewProcessState);
    button.setEnabled(repOn);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.ViewProcessState);
      }
    });
    toolBar.add(button);
    onlineComponents.add(button);
    
    /* refrescar o estado do processo */
    button = new JButton(new ImageIcon(createImage("menu_icon_process_refresh.png", false, false)));
    button.setToolTipText(Mesg.RefreshProcessState);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        processFileMenuActions(Mesg.RefreshProcessState);
      }
    });
    toolBar.add(button);
    addProcessStateComponents(button);
    
    /* mostrar blocos percorridos */
    showBlockSorterButton = new JToggleButton();
    showBlockSorterButton.setSelectedIcon(new ImageIcon(createImage("menu_icon_process_show_on.png", false, false)));
    showBlockSorterButton.setIcon(new ImageIcon(createImage("menu_icon_process_show_off.png", false, false)));
    showBlockSorterButton.setSelected(true);
    showBlockSorterButton.setToolTipText(Mesg.ToggleProcessState);
    showBlockSorterButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        toggleBlockSorterPane();
      }
    });
    toolBar.add(showBlockSorterButton);
    addProcessStateComponents(showBlockSorterButton);
    
    toolBar.addSeparator();
    
    showLibButton = new JToggleButton();
    showLibButton.setSelectedIcon(new ImageIcon(createImage("menu_icon_show_lib_on.png", false, false)));
    showLibButton.setIcon(new ImageIcon(createImage("menu_icon_show_lib_off.png", false, false)));
    showLibButton.setSelected(true);
    showLibButton.setToolTipText("Liga/desliga a biblioteca de blocos");
    showLibButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        toggleLibraryPane();
      }
    });
    toolBar.add(showLibButton);
    
    toolBar.addSeparator();
    panelOnline = new JPanel();
    panelOnline.setBorder(new  LineBorder(Color.black));
    panelOnline.setBackground(new Color(0xf7f5e8));
    JLabel lblOffline = new JLabel(Messages.getString("Janela.status.offline"), new ImageIcon(createImage("warning.png", false, false)), JLabel.LEFT);
    panelOnline.add(lblOffline);
    toolBar.add(panelOnline);

    // Lay out the content pane.
    getContentPane().add(toolBar, BorderLayout.NORTH);
    toolBar.setFloatable(false);

  }

  /**
   * Close active flow
   * 
   * @return true if action canceled
   */
  private boolean closeFlow() {
    return closeFlow(true);
  }

  /**
   * Close active flow
   * 
   * @return true if action canceled
   */
  private boolean closeFlow(boolean doClose) {
    int idx = tabPane.getSelectedIndex();
    if (idx == -1)
      return false;

    Desenho d = ((DesenhoScrollPane) tabPane.getComponentAt(idx)).getDesenho();
    if (d.isFlowChanged()) {
      Sair s = new Sair(this, Mesg.MenuGravar);
      int r = s.getResposta();
      if (r == Sair.YES)
        r = exportFlowToFile(false);
      if (r == Sair.CANCEL)
        return true;
    }

    if (doClose) {
      tabPane.remove(idx);
    }
    return false;
  }

  /**
   * Close all opened flows
   * 
   * @return true if action canceled
   */
  private boolean closeAllFlows() {
    // foreach canvas in tabbed browser check if modified.
    int nComponents = tabPane.getTabCount();
    boolean changed = false;
    for (int i = 0; i < nComponents; i++) {
      DesenhoScrollPane dsp = (DesenhoScrollPane) tabPane.getComponentAt(i);
      if (dsp.getDesenho().isFlowChanged()) {
        changed = true;
        break;
      }
    }

    /* existem fluxos por gravar */
    if (changed) {
      Sair s = new Sair(this, Mesg.ConfirmaAlterados);
      if (s.getResposta() != Sair.YES)
        return true;
    }

    tabPane.removeAll();
    return false;
  }

  /**
   * Close all other flows
   * 
   * @return true if action canceled
   */
  private boolean closeOtherFlows() {
    int idx = tabPane.getSelectedIndex();
    if (idx == -1)
      return false;
    // foreach canvas in tabbed browser check if modified.
    ArrayList<Component> toRemove = new ArrayList<Component>();
    int nComponents = tabPane.getTabCount();
    boolean changed = false;
    for (int i = 0; i < nComponents; i++) {
      if (i == idx)
        continue;
      DesenhoScrollPane dsp = (DesenhoScrollPane) tabPane.getComponentAt(i);
      toRemove.add(dsp);
      if (dsp.getDesenho().isFlowChanged()) {
        changed = true;
        break;
      }
    }

    /* existem fluxos por gravar */
    if (changed) {
      Sair s = new Sair(this, Mesg.ConfirmaAlterados);
      if (s.getResposta() != Sair.YES)
        return true;
    }

    for (Component component : toRemove) {
      tabPane.remove(tabPane.indexOfComponent(component));
    }
    return false;
  }

  private void openIFlowWindow() {
    if(!repository.checkConnection()) return; // improve this
    FlowEditor.getRootDisplay().asyncExec(iflowWindowDispatcher);
  }


  protected void processKeyEvent(KeyEvent e) {
    super.processKeyEvent(e);
    Desenho d = getSelectedDesenho();
    if (d == null)
      return;
    switch (e.getID()) {
    case KeyEvent.KEY_PRESSED:
    case KeyEvent.KEY_TYPED:
      // case KeyEvent.KEY_FIRST:
      d.keyPressed(e);
      break;
    case KeyEvent.KEY_RELEASED:
      d.keyReleased(e);
      break;
    }

  }

  /*****************************************************************************
   * Tratamento dos eventos associados a janela
   */
  private void setupFrame() {
    /* evento de eliminar janela */
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        exit();
      }
    });

    /* nao permitir que se feche a janela ao carregar na X */
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

    /* tratar da janela */
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds(0, 0, screenSize.width * 3 / 4, screenSize.height * 3 / 4);
    setLocationRelativeTo(null); // Screen Centered
    Image imagem = createImage("icon1.gif", false, false); //$NON-NLS-1$

    if (imagem != null)
      setIconImage(imagem);
  }

  /*****************************************************************************
   * Cria uma nova imagem, recebendo o nome do ficheiro
   */
  public Image createImage(String nomeficheiro, boolean isBiblioteca) {
    return createImage(nomeficheiro, true, isBiblioteca, null);
  }
  public Image createImage(String nomeficheiro, boolean isBiblioteca, pt.iflow.api.xml.codegen.library.Color xmlColor) {
    return createImage(nomeficheiro, true, isBiblioteca, xmlColor);
  }

  private static final int MAX_WIDTH = 50;
  private static final int MAX_HEIGHT = 50;

  /*****************************************************************************
   * Cria uma nova imagem, recebendo o nome do ficheiro
   */
  public Image createImage(String nomeFicheiro, boolean abTryRep, boolean isBiblioteca) {
    return createImage(nomeFicheiro, abTryRep, isBiblioteca, null);
  }

  public Image createImage(String nomeFicheiro, boolean abTryRep, boolean isBiblioteca, pt.iflow.api.xml.codegen.library.Color xmlColor) {
    // ptgm - saves time with server01.gif (transactions.xml)
    if (_htImageCache.containsKey(nomeFicheiro)) {
      return _htImageCache.get(nomeFicheiro);
    }

    byte byteArray[] = new byte[0];
    try {
      if (abTryRep && repository.checkConnection()) {
        // first must test if it is in cache...
        if (null != iconCacheFolder) {
          File iconFile = new File(iconCacheFolder, nomeFicheiro);

          int r = 0;
          byte[] b = new byte[8192];

          InputStream input = null;
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          try {
            input = new FileInputStream(iconFile);
            while ((r = input.read(b)) != -1)
              out.write(b, 0, r);
            byteArray = out.toByteArray();
          } catch (IOException e) {
            byteArray = null;
          } finally {
            if (null != input) {
              try {
                input.close();
              } catch (IOException e) {
              }
            }
          }
        }

        if (null == byteArray || byteArray.length == 0) {
          RepositoryClient rep = getRepository();
          byteArray = rep.getIcon(nomeFicheiro); //$NON-NLS-1$
          if (null != iconCacheFolder && null != byteArray || byteArray.length > 0) {
            // save to local cache
            File iconFile = new File(iconCacheFolder, nomeFicheiro);
            OutputStream out = null;
            try {
              out = new FileOutputStream(iconFile);
              out.write(byteArray);
              FlowEditor.log("Icon " + nomeFicheiro + " saved to cache");
            } catch (IOException e) {
              byteArray = null;
            } finally {
              if (null != out) {
                try {
                  out.close();
                } catch (IOException e) {
                }
              }
            }
          }
        }

      }

      if (null == byteArray || byteArray.length == 0) { // try local file...

        InputStream ficheiro = Janela.class.getResourceAsStream("/images/" + nomeFicheiro); //$NON-NLS-1$
        if (null == ficheiro) {
          ficheiro = new FileInputStream("images/" + nomeFicheiro); //$NON-NLS-1$
        }
        ByteArrayOutputStream bbbbb = new ByteArrayOutputStream();

        int r = 0;
        byte[] b = new byte[8192];

        while ((r = ficheiro.read(b)) != -1)
          bbbbb.write(b, 0, r);

        ficheiro.close();

        byteArray = bbbbb.toByteArray();
      }
    } catch (Exception e) {
      FlowEditor.log(Messages.getString("System.out.ERROR_CREATING_IMAGE") + nomeFicheiro + "."); //$NON-NLS-1$ //$NON-NLS-2$
    }

    if (byteArray == null) {
      return null;
    }

    ImageIcon imageIcon = new ImageIcon(byteArray);
    Image imagem = imageIcon.getImage();

    if (isBiblioteca) {
      double w = imagem.getWidth(null);
      double h = imagem.getHeight(null);

      // ensure that the image is not bigger than MAX_WIDTH x MAX_HEIGHT
      if (w > MAX_WIDTH || h > MAX_HEIGHT) {
        // scale image....
        int nw = (int) (MAX_WIDTH * w / h);
        int nh = (int) (MAX_HEIGHT * h / w);
        if (nh < MAX_HEIGHT) {
          nw = MAX_WIDTH;
        }
        imagem = new ImageIcon(imagem.getScaledInstance(nw, nh, Image.SCALE_DEFAULT)).getImage();
        w = imagem.getWidth(null);
        h = imagem.getHeight(null);
      }

      int width = MAX_WIDTH;
      int height = MAX_HEIGHT;
      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) bi.getGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      if(null != xmlColor) {
        Color c = new Color(xmlColor.getR(), xmlColor.getG(), xmlColor.getB(), xmlColor.getA());
        g.setColor(c);
      } else if (hmBColors.containsKey(nomeFicheiro)) {
        g.setColor(hmBColors.get(nomeFicheiro));
      } else {
        g.setColor(DEFAULT_COLOR);
      }

      g.fillRoundRect(0, 0, width, height, BORDER_CORNER_RADIUS, BORDER_CORNER_RADIUS);

      g.setPaint(new GradientPaint(new Point(2, 2), new Color(0.73f, 0.73f, 0.73f, 0.75f), new Point(width - (2 * BORDER_WIDTH),
          height), new Color(0.93f, 0.93f, 0.93f, 0.85f)));

      g.fillRoundRect(BORDER_WIDTH, BORDER_WIDTH, width - (2 * BORDER_WIDTH), height - (2 * BORDER_WIDTH), INNER_CORNER_RADIUS,
          INNER_CORNER_RADIUS);
      // g.fillRect(BORDER_WIDTH, BORDER_WIDTH, width-(2*BORDER_WIDTH),
      // height-(2*BORDER_WIDTH));

      int posx = (int) (width / 2 - w / 2);
      int posy = (int) (height / 2 - h / 2);
      g.drawImage(imagem, posx, posy, null);

      imagem = bi;
    }

    _htImageCache.put(nomeFicheiro, imagem);
    return imagem;
  }

  /*****************************************************************************
   * Tratamentos de outros eventos chamar a funcao do Desenho desta janela
   */
  public void actionPerformed(ActionEvent evt) {
    Desenho d = getSelectedDesenho();
    if (null != d)
      d.actionPerformed(evt);
    return;
  }

  /*****************************************************************************
   * Funcao que trata de entrar do programa
   */
  private FlowRepUrl login(FlowEditorConfig cfg) {
    Login login = new Login(this, cfg);

    RepositoryWebClient rep = login.getRepository();
    this.repository = rep;
    FlowRepUrl result = rep.getIFlowURL();
    
    if (rep.checkConnection()) {
      // reload icon image
      Image imagem = createImage("icon1.gif", true, false); //$NON-NLS-1$
      if (imagem != null) {
        setIconImage(imagem);
      }
    }

    // Carregar as configuracoes guardadas sobre locale
    String useLocale = cfg.getUseLocale();
    if (FlowEditorConfig.LOCALE_USE_IFLOW.equals(useLocale)) {
      String localeKey = result.getLanguage();
      if (rep.checkConnection()) {
        String iflowKey = rep.getUserLocale();
        if(!StringUtilities.isEqual(iflowKey, localeKey)) {
          localeKey = iflowKey;
          result.setLanguage(localeKey);
        }
      }
      Mesg.setLocale(localeKey);
    } else if(StringUtils.isBlank(useLocale) || FlowEditorConfig.LOCALE_USE_DEFAULT.equals(useLocale)) {
      // use default
      result.setLanguage(Locale.getDefault().toString());
    } else {
      // use selected
      result.setLanguage(cfg.getSelectedLocale());
    }
    cfg.saveConfig();
    FlowEditor.log("User locale: "+result.getLanguage());
    
    return result;
  }

  /*****************************************************************************
   * Funcao que trata de iSAIR do programa
   */
  private void exit() {
    synchronizeFlows();
    boolean doExit = confirmExit();
    if(doExit) {
      // System.exit(0);
      FlowEditor.shutdown();
    }
  }

  private boolean confirmExit() {
    /* perguntar se quer mesmo iSAIR */
    Sair s = new Sair(this, Mesg.MenuSair);
    if (s.getResposta() != Sair.YES)
      return false;

    boolean canceled = closeAllFlows();
    if (canceled)
      return false;

    if (repository.checkConnection()) {
      repository.logout();
    }

    /* eliminar janelas */
    setVisible(false);
    dispose();
    instance = null;

    return true;
  }

  private void switchWorkspace() {
    boolean doExit = confirmExit();
    if(doExit) {
      // Notificar a main class
      FlowEditor.runEditor(null);
    }
  }

  private List<DesenhoScrollPane> getOpenTabs() {
    int tabCount = tabPane.getTabCount();
    List<DesenhoScrollPane> tabs = new ArrayList<DesenhoScrollPane>(tabCount);
    for(int i = 0; i < tabCount; i++)
      tabs.add((DesenhoScrollPane) tabPane.getComponentAt(i));

    return tabs;
  }

  public void repaint() {
    super.repaint();
    Desenho d = getSelectedDesenho();
    if (null != d)
      d.updateCanvasSize();
  }

  /* cursores */
  public void aEspera() {
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
  }

  public void normal() {
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }

  public int getLibraryWidth() {
    int w = 0;
    if(libraryPane.isVisible()) w = libraryPane.getSize().width;
    return w;
  }

  public void setTabTitle(Component c, String text, String tooltip) {
    int pos = tabPane.indexOfComponent(c);
    if(pos != -1) {
      tabPane.setTitleAt(pos, text);
      tabPane.setToolTipTextAt(pos, tooltip);
    }
  }

  public LibrarySet getLibrarySet() {
    return this._librarySet;
  }

  public JPopupMenu getLinePopup(Desenho d) {
    return this.t_m.Da_Popup_Linha(d);
  }

  public JPopupMenu getCanvasPopup(Desenho d) {
    return this.t_m.Da_Popup_Vazio(d);
  }

  public JPopupMenu getBlockPopup(Desenho d) {
    return this.t_m.Da_Popup_Componente(d, getOpenTabs());
  }


  public void keyPressed(java.awt.event.KeyEvent keyEvent) {
    Desenho d = getSelectedDesenho();
    if (null != d)
      d.keyPressed(keyEvent);
  }

  public void keyReleased(java.awt.event.KeyEvent keyEvent) {
    Desenho d = getSelectedDesenho();
    if (null != d)
      d.keyReleased(keyEvent);
  }

  public void keyTyped(java.awt.event.KeyEvent keyEvent) {
    Desenho d = getSelectedDesenho();
    if (null != d)
      d.keyTyped(keyEvent);
  }

  /*****************************************************************************
   * ler ficheiro com descrticao dos tipos de letra a serem utilizados
   */
  private static void LerFicheiroLetras() {
    try {
      // Unmarshal XML file.
      XmlLetterType letters = FlowEditorConfig.getLetters();

      TipoGrande letter = letters.getTipoGrande();
      if (letter != null) {
        String nome = letter.getNome();
        int tamanho = letter.getTamanho();
        String tipo = letter.getTipe();
        Cor.getInstance().setFont(new Font(nome, getFontType(tipo), tamanho));
      }
      TipoPequeno letter2 = letters.getTipoPequeno();
      if (letter2 != null) {
        String nome = letter2.getNome();
        int tamanho = letter2.getTamanho();
        String tipo = letter2.getTipe();
        // set font
        Cor.getInstance().setFont(new Font(nome, getFontType(tipo), tamanho));
      }
    } catch (Exception e) {
      FlowEditor.log("error", e);
    }
  }

  private static int getFontType(String typeDesc) {
    if ("bold".equals(typeDesc)) { //$NON-NLS-1$
      return Font.BOLD;
    } else if ("italic".equals(typeDesc)) { //$NON-NLS-1$
      return Font.ITALIC;

    } else if ("bolditalic".equals(typeDesc)) { //$NON-NLS-1$
      return Font.BOLD | Font.ITALIC;
    }

    return Font.PLAIN;
  }

  public File[] getCachedLibraries() {
    return libCacheFolder.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
        return pathname.isFile() && FilenameUtils.isExtension(pathname.getName().toLowerCase(), "xml");
      }
    });
  }

  // FIXME este metodo deve passar para o RepositoryWebClient quando se faz login
  private void updateLocalCache() {
    String nFolder = iFlowURL.getUserKey();

    FlowEditor.log("Local cache dir: " + nFolder);

    this.cacheFolder = new File(FlowEditorConfig.CONFIG_DIR, nFolder);
    if (!cacheFolder.exists()) {
      cacheFolder.mkdirs();
    }

    File cacheInfoFile = new File(cacheFolder, "cache.xml");

    File libCacheFolder = new File(cacheFolder, "lib");
    if (!libCacheFolder.exists()) {
      libCacheFolder.mkdirs();
    }
    File iconCacheFolder = new File(cacheFolder, "icon");
    if (!iconCacheFolder.exists()) {
      iconCacheFolder.mkdirs();
    }
    File flowCacheFolder = new File(cacheFolder, "flows");
    if (!flowCacheFolder.exists()) {
      flowCacheFolder.mkdirs();
    }
    File subFlowCacheFolder = new File(cacheFolder, "subflows");
    if (!subFlowCacheFolder.exists()) {
      subFlowCacheFolder.mkdirs();
    }
    
    File i18nCacheFolder = new File(cacheFolder, "i18n");
    if(!i18nCacheFolder.exists()) {
      i18nCacheFolder.mkdirs();
    }

    this.cacheInfoFile = cacheInfoFile;
    this.iconCacheFolder = iconCacheFolder;
    this.libCacheFolder = libCacheFolder;
    this.flowCacheFolder = flowCacheFolder;
    this.subFlowCacheFolder = subFlowCacheFolder;
    this.i18nCacheFolder = i18nCacheFolder;

    FlowEditor.log("Loading cache info file");
    UserCacheEntry localCache = loadUserCacheEntry(this.cacheInfoFile);
    localCache.setLanguage(iFlowURL.getLanguage());

    RepositoryClient rep = getRepository();
    FlowEditor.log("Retrieving libraries from repository ..."); //$NON-NLS-1$

    String iconsHash = rep.getIconsHash();
    String libsHash = rep.getLibrariesHash();
    byte[] b = new byte[8192];
    int r = 0;

    if (!StringUtils.equalsIgnoreCase(iconsHash, localCache.getIconsHash())) {
      FlowEditor.log("iFlow icons update available");
      localCache.setIconsHash(iconsHash);
      cfg.saveConfig();

      FlowEditor.log("Removing old icons from local cache.");
      File[] localIcons = iconCacheFolder.listFiles();
      for (File file : localIcons) {
        file.delete();
      }

      FlowEditor.log("Retrieving icons zip from repository.");
      byte[] iconZipData = rep.getZippedIcons();
      if (null != iconZipData) {
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(iconZipData));

        try {
          ZipEntry ze = null;
          while ((ze = zin.getNextEntry()) != null) {
            String name = ze.getName();
            File iconFile = new File(iconCacheFolder, name);
            FileOutputStream fout = new FileOutputStream(iconFile);
            while ((r = zin.read(b)) != -1) {
              fout.write(b, 0, r);
            }
            fout.close();
            FlowEditor.log("Icon file " + name + " added to local cache");
          }
          zin.close();
        } catch (Throwable t) {
          FlowEditor.log("error", t);
        }
      }
    }

    if (!StringUtils.equalsIgnoreCase(libsHash, localCache.getLibrariesHash())) {
      FlowEditor.log("iFlow libraries update available");
      localCache.setLibrariesHash(libsHash);
      cfg.saveConfig();

      FlowEditor.log("Removing old libraries from local cache.");
      File[] localLibs = libCacheFolder.listFiles();
      for (File file : localLibs) {
        file.delete();
      }

      // Retrieving zip file with library content
      FlowEditor.log("Retrieving libraries zip from repository.");
      byte[] libZipData = rep.getZippedLibraries();
      if (null != libZipData) {
        ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(libZipData));

        try {
          ZipEntry ze = null;
          while ((ze = zin.getNextEntry()) != null) {
            String name = ze.getName();
            File libFile = new File(libCacheFolder, name);
            FileOutputStream fout = new FileOutputStream(libFile);
            while ((r = zin.read(b)) != -1) {
              fout.write(b, 0, r);
            }
            fout.close();
            FlowEditor.log("Library file " + name + " added to local cache");
          }
          zin.close();
        } catch (Throwable t) {
          FlowEditor.log("error", t);
        }
      } else {
        FlowEditor.log("Zip not found. Retrieving libraries from repository.");
        String[] libs = rep.listLibraries();
        for (String libname : libs) {
          try {
            byte[] data = rep.getLibrary(libname);
            if (null == data || data.length == 0)
              continue;
            // read, the regular way!
            File libFile = new File(libCacheFolder, libname);
            FileOutputStream fout = new FileOutputStream(libFile);
            fout.write(data);
            fout.close();
            FlowEditor.log("Library file " + libFile + " added to local cache");
          } catch (Throwable t) {
            FlowEditor.log("error", t);
          }
        }
      }
    }

    // compute local classes hashes and check updated/changed files
    FlowEditor.log("Checking for modified class files"); //$NON-NLS-1$
    File classesCacheFolder = ((RepositoryWebClient)repository).getClassCacheFolder();
    List<CheckSum> classChecksum = new ArrayList<CheckSum>();
    computeClassChecksums(classesCacheFolder, "", classChecksum);
    CheckSum [] modified = CheckSum.unmarshall(rep.getModifiedClasses(CheckSum.marshall(classChecksum)));

    // remove modified files
    if(null != modified) {
      for(CheckSum sum : modified) {
        FlowEditor.log("Cached file has changed: "+sum.getFile());
        File toRemove = new File(classesCacheFolder, sum.getFile());
        toRemove.delete();
      }
    }
    
    
    // TODO i18n cache
    FlowEditor.log("Checking for modified i18n files"); //$NON-NLS-1$
    List<CheckSum> i18nChecksum = new ArrayList<CheckSum>();
    
    File [] i18nResources = this.i18nCacheFolder.listFiles();
    for(File f : i18nResources) {
      if(!f.isFile()) continue;
      CheckSum sum = new CheckSum(CheckSum.digest(f), f.getName());
      i18nChecksum.add(sum);
    }
    
    modified = CheckSum.unmarshall(rep.getModifiedMessages(CheckSum.marshall(i18nChecksum)));

    // remove modified files
    if(null != modified) {
      for(CheckSum sum : modified) {
        FlowEditor.log("Cached file has changed: "+sum.getFile());
        File toRemove = new File(this.i18nCacheFolder, sum.getFile());
        toRemove.delete();
      }
    }
    

    FlowEditor.log("Writing updated cache info file");
    saveUserCacheEntry(this.cacheInfoFile, localCache);

    synchronizeFlows();
  }

  private static UserCacheEntry loadUserCacheEntry(File cacheFile) {
    InputStream in = null;
    try {
      in = new FileInputStream(cacheFile);
      return (UserCacheEntry) Unmarshaller.unmarshal(UserCacheEntry.class, new InputSource(in));
    } catch(Exception e) {
      FlowEditor.log("Error loading cache file. Assuming empty...");
    } finally {
      if(null != in) {
        try {
          in.close();
        } catch(IOException e) {
        }
        
      }
    }
    return new UserCacheEntry();
  }
  
  private static void saveUserCacheEntry(File cacheFile, UserCacheEntry entry) {
    try {

      // if config file dows not exist create it and stuff it
      FileOutputStream fos = new FileOutputStream(cacheFile);

      OutputStreamWriter osw = new OutputStreamWriter(fos, FlowEditorConfig.CONFIG_FILE_ENCODING);
      Marshaller marshaller = new Marshaller(osw);
      marshaller.setEncoding(FlowEditorConfig.CONFIG_FILE_ENCODING);
      marshaller.marshal(entry);
      osw.close();
      fos.close();
    }
    catch (Exception e) {
      FlowEditor.log("Error writing cache file", e);
    }
  }
  
  private void computeClassChecksums(File parent, String prefix, List<CheckSum> sums) {
    File [] children = parent.listFiles();
    
    for(File f : children) {
      if(f.isFile()) {
        sums.add(new CheckSum(CheckSum.digest(f), prefix+f.getName()));
      } else if (f.isDirectory()) {
        computeClassChecksums(f, prefix+f.getName()+"/", sums);
      }
    }
  }

  
  
  private void synchronizeFlows() {

    RepositoryClient rep = getRepository();
    
    if(!rep.checkConnection()) return;
    
    FlowEditor.log("Checking for flows to synchronize"); //$NON-NLS-1$
    File[] files;
    // check flow state changes


    // foreach cached flow, upload
    files = this.flowCacheFolder.listFiles();
    for (File file : files) {
      FlowEditor.log("Uploading flow "+file); //$NON-NLS-1$
      byte [] filedata = new byte[(int)file.length()];
      FileInputStream fin = null;
      try {
        fin = new FileInputStream(file);
        fin.read(filedata);
      } 
      catch (IOException e) {}
      finally {
        try {
          if(null != fin) fin.close();
        } catch (IOException e) {
        }
      }
      fin = null;
      try {
        XmlFlow flow = FlowMarshaller.unmarshal(filedata);
        String name = flow.getName();
        String description = flow.getDescription();
        FlowEditor.log("subflow name: "+name+"; description: "+description); //$NON-NLS-1$
        rep.deployFlow(name, description, filedata);
      } catch (Throwable t) {

      }
      file.delete();
    }

    // foreach cached subflow, upload
    files = this.subFlowCacheFolder.listFiles();
    for (File file : files) {
      FlowEditor.log("Uploading subflow "+file); //$NON-NLS-1$
      byte [] filedata = new byte[(int)file.length()];
      FileInputStream fin = null;
      try {
        fin = new FileInputStream(file);
        fin.read(filedata);
      } 
      catch (IOException e) {}
      finally {
        try {
          if(null != fin) fin.close();
        } catch (IOException e) {
        }
      }
      fin = null;
      try {
        XmlFlow flow = FlowMarshaller.unmarshal(filedata);
        String name = flow.getName();
        String description = flow.getDescription();
        FlowEditor.log("subflow name: "+name+"; description: "+description); //$NON-NLS-1$
        rep.deploySubFlow(name, description, filedata);
      } catch (Throwable t) {
      }
      file.delete();
    }

    FlowEditor.log("All synchronized"); //$NON-NLS-1$
  }

  public int saveFlow(Desenho d, String comment, boolean isFlow, boolean version, RepositoryStatusListener listener) {
    boolean saveOk = false;
    int flowId = -1;

    try {
      byte [] data = d.getFlowData();
      if(null == data) throw new Exception("Invalid flow data");
      // test iFlow connectivity
      if(repository.checkConnection()) {
        // is OK
        RepositoryClient rep = getRepository();
        rep.setStatusListener(listener);
        FlowEditor.log("Uploading to Repository ..."); //$NON-NLS-1$

        if(isFlow && version)
          flowId = rep.deployFlowVersion(d.getFlowId(), d.getName(), data, comment);
        else if (isFlow && !version)
          flowId = rep.deployFlow(d.getFlowId(), d.getName(), data);
        else if (!isFlow && version)
          flowId = rep.deploySubFlowVersion(d.getFlowId(), d.getName(), data, comment);
        else
          flowId = rep.deploySubFlow(d.getFlowId(), d.getName(), data);
        saveOk = (flowId > 0);
        rep.setStatusListener(null);
        FlowEditor.log("Flow uploaded."); //$NON-NLS-1$
      } else {
        FlowEditor.log("Saving into local cache..."); //$NON-NLS-1$
        if(isFlow)
          saveOk = cacheFlow(d.getFlowId(), data, this.flowCacheFolder);
        else
          saveOk = cacheFlow(d.getFlowId(), data, this.subFlowCacheFolder);

        if(saveOk) {
          flowId = 1;
        }
        FlowEditor.log("Flow saved."); //$NON-NLS-1$
      }

      d.setFlowChanged(d.isFlowChanged()&&!saveOk); // if save ok, mark flow not changed
    } catch (Throwable t) {
      FlowEditor.log("Error saving flow.", t); //$NON-NLS-1$
      flowId = -1;
    }
    return flowId;
  }

  private boolean cacheFlow(String name, byte[] data, File parent) {
    FlowEditor.log("Caching local Flow "+name+" into "+parent); //$NON-NLS-1$
    boolean saveOk = false;
    // save to disk
    File file = new File(parent, name);
    FileOutputStream fout = null;
    try {
      fout = new FileOutputStream(file);
      fout.write(data);
      saveOk = true;
    } catch (IOException e) {
      FlowEditor.log("error writing file "+file, e);
    } finally {
      try {
        if(null != fout) fout.close();
      } catch (IOException e) {
      }
    }
    FlowEditor.log("Flow saved."); //$NON-NLS-1$

    return saveOk;
  }

  private synchronized void notifyOnline(boolean fromEvent) {
    boolean repOn = repository.checkConnection();
    if(null != panelOnline) panelOnline.setVisible(!repOn);

    setTitle("Flow Editor " + Version.VERSION+" on "+iFlowURL.url+(repOn?"":" (offline)"));

    if(!repOn) {
      String msg = Messages.getString("Janela.offline.msg");
      String title = Messages.getString("Janela.offline.title");
      JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    if(fromEvent && repOn) {
      String msg = Messages.getString("Janela.online.msg");
      String title = Messages.getString("Janela.online.title");
      JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }
    if(repOn) synchronizeFlows();

    Iterator<JComponent> iterCo = onlineComponents.iterator();
    while(iterCo.hasNext())
      iterCo.next().setEnabled(repOn);

  }


  /**
   * The class which generates the 'X' icon for the tabs. The constructor
   * accepts an icon which is extra to the 'X' icon, so you can have tabs like
   * in JBuilder. This value is null if no extra icon is required.
   */
  private static class SimpleCrossIcon implements Icon {
    /**
     * the width the icon
     */
    private int width = 16;

    /**
     * the height the icon
     */
    private int height = 16;

    /**
     * Draw the icon at the specified location. Icon implementations may use the
     * Component argument to get properties useful for painting, e.g. the
     * foreground or background color.
     * 
     * @param c
     *          the component which the icon belongs to
     * @param g
     *          the graphic object to draw on
     * @param x
     *          the upper left point of the icon in the x direction
     * @param y
     *          the upper left point of the icon in the y direction
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
      int y_p = y + 1;

      y_p++;

      Color col = g.getColor();

      g.setColor(Color.black);
      g.drawLine(x + 3, y_p + 3, x + 10, y_p + 10);
      g.drawLine(x + 3, y_p + 4, x + 9, y_p + 10);
      g.drawLine(x + 4, y_p + 3, x + 10, y_p + 9);
      g.drawLine(x + 10, y_p + 3, x + 3, y_p + 10);
      g.drawLine(x + 10, y_p + 4, x + 4, y_p + 10);
      g.drawLine(x + 9, y_p + 3, x + 3, y_p + 9);
      g.setColor(col);
    }

    /**
     * Returns the icon's width.
     * 
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
      return width;
    }

    /**
     * Returns the icon's height.
     * 
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
      return height;
    }

    @SuppressWarnings("unused")
    public Dimension getSize() {
      return new Dimension(width, height);
    }
  }

  @SuppressWarnings("unchecked")
  public Class<AbstractAlteraAtributos> loadGUIClass(String className) throws ClassNotFoundException {
    Class<AbstractAlteraAtributos> dialogClass = null;
    RepositoryClient _rep = getRepository();

    if (_rep.checkConnection()) {
      dialogClass = (Class<AbstractAlteraAtributos>) _rep.loadClass(className);
    } else {
      dialogClass = (Class<AbstractAlteraAtributos>) Class.forName(className);
    }
    return dialogClass;
  }


  public RepositoryClient getRepository() {
    return repository;
  }
  
  private IMessages getCachedBlockMsg() {
    String resourceName = "editor_blocks_"+iFlowURL.getLanguage()+".properties";
    // byte [] propContents = null;
    Properties props = new Properties();
    File cachedProp = new File(this.i18nCacheFolder, resourceName);
    if(!cachedProp.exists()) {
      // retrieve from iFlow
      byte [] data = repository.getMessages(resourceName);
      if(null == data) {
        // try to retrieve default file...
        resourceName = "editor_blocks.properties";
        cachedProp = new File(this.i18nCacheFolder, resourceName);
        if(!cachedProp.exists()) {
          data = repository.getMessages(resourceName);
        }
      }
      
      // at this point return data is null if localized and default versions of properties
      // are not found
      if(null == data) return new BlockMessages(props);
      
      OutputStream out = null;
      
      try {
        out = new FileOutputStream(cachedProp);
        out.write(data);
      } catch (Exception e) {
        
      } finally {
        if(null != out) {
          try {
            out.close();
          } catch (IOException e) {
          }
        }
      }
      
    }
    
    InputStream in = null;
    
    try {
      props.load(in = new FileInputStream(cachedProp));
    } catch(Exception e) {
      
    } finally {
      if(null != in) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
    
    return new BlockMessages(props);
  }

  public IMessages getBlockMessages() {
    return blockMsg;
  }

}
