package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: TrataMenus
 *
 *  desc: trata dos menus da janela
 *
 ****************************************************/

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import org.apache.commons.lang.StringUtils;

import pt.iknow.floweditor.messages.Messages;

/*******************************************************************************
 * Trata_Menus
 */
public class Trata_Menus {
  /* menus e popupmenus */
  protected JMenuBar menubar = null;

  protected JPopupMenu pop_vazio = null;
  protected JPopupMenu pop_componente = null;
  protected JPopupMenu pop_linha = null;
  protected JPopupMenu pop_vazio_PS = null;
  protected JPopupMenu pop_componente_PS = null;
  protected JPopupMenu pop_linha_PS = null;
  
  protected JMenu menu_tabs = null;

  private JMenuItem showBlockLibItem = null;

  static int Tecla_Eliminar = KeyEvent.VK_R;
  static int Mnm_Eliminar = 'R';
  static int Tecla_Undo = KeyEvent.VK_U;
  static int Mnm_Undo = 'U';

  static int Tecla_Duplicar = KeyEvent.VK_V;
  static int Mnm_Duplicar = 'V';
  static int Mnm_Componente = 'C';
  static int Mnm_Ficheiro = 'F';
  static int Tecla_Simulacao = KeyEvent.VK_T;
  static int Mnm_Simulacao = 'a';
  static int Tecla_ExportarComposto = KeyEvent.VK_E;
  static int Mnm_ExportarComposto = 'E';
  static int Tecla_ImportarComposto = KeyEvent.VK_I;
  static int Mnm_ImportarComposto = 'I';
  static int Tecla_LerBiblioteca = KeyEvent.VK_B;

  static int Mnm_LerBiblioteca = 'B';
  static int Tecla_GravarFicheiro = KeyEvent.VK_S;
  static int Mnm_GravarFicheiro = 'G';
  static int Tecla_LerFicheiro = KeyEvent.VK_O;
  static int Mnm_LerFicheiro = 'o';
  static int Tecla_Novo = KeyEvent.VK_N;
  static int Mnm_Novo = 'N';
  static int Tecla_Sair = KeyEvent.VK_X;
  static int Mnm_Sair = 'S';
  static int Tecla_Muda_Nome = KeyEvent.VK_M;
  static int Mnm_Muda_Nome = 'm';
  static int Tecla_Close = KeyEvent.VK_W;
  static int Mnm_Close = 'w';
  static int Tecla_Close_All = KeyEvent.VK_W|KeyEvent.VK_SHIFT;
  static int Mnm_Close_All = 'l';

  static int Tecla_ImportarFicheiro = KeyEvent.VK_U;
  static int Mnm_ImportarFicheiro = 'd';
  static int Tecla_ExportarFicheiro = KeyEvent.VK_P;
  static int Mnm_ExportarFicheiro = 'p';

  static int Tecla_ImportarSubFlow = KeyEvent.VK_F;
  static int Mnm_ImportarSubFlow = 'f';
  static int Tecla_ExportarSubFlow = KeyEvent.VK_C;
  static int Mnm_ExportarSubFlow = 'c';

  static int Mnm_Editar = 'E';
  /* variavel auxiliar */
  private boolean menu_comp = false;

  private int bibIndex = -1;
  
  /*****************************************************************************
   * criar menus de barra
   */
  public JMenuBar Cria_Menus(LibrarySet bib, Janela janela) {
    /* inicializar barra */
    if (menubar != null)
      janela.remove(menubar);
    menubar = new JMenuBar();

    /* menu ficheiro */
    JMenu menu = new JMenu(Mesg.MenuFicheiro);
    menu.setMnemonic(Mnm_Ficheiro);

    menu.add(Faz_MenuItem_File(Mesg.Novo, janela, Tecla_Novo, Mnm_Novo));
    menu.add(Faz_MenuItem_File(Mesg.MudaNomeFluxo, janela, Tecla_Muda_Nome, Mnm_Muda_Nome));

    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.FileClose, janela, Tecla_Close, Mnm_Close));
    menu.add(Faz_MenuItem_File(Mesg.FileCloseOthers, janela, 0, 0));
    menu.add(Faz_MenuItem_File(Mesg.FileCloseAll, janela, 0, 0));

    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.LerFicheiro, janela, Tecla_LerFicheiro, Mnm_LerFicheiro));
    // menu.add(Faz_MenuItem_File(Mesg.GravarFicheiro,janela,Tecla_GravarFicheiro,Mnm_GravarFicheiro));
    menu.add(Faz_MenuItem_File(Mesg.GravarFicheiroComo, janela, 0, 0));

    // repository stuff
    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.ImportarFicheiro, janela, Tecla_ImportarFicheiro, Mnm_ImportarFicheiro, true));
    menu.add(Faz_MenuItem_File(Mesg.ExportarFicheiro, janela, Tecla_ExportarFicheiro, Mnm_ExportarFicheiro, true));

    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.ImportarSubFlow, janela, Tecla_ImportarSubFlow, Mnm_ImportarSubFlow, true));
    menu.add(Faz_MenuItem_File(Mesg.ExportarSubFlow, janela, Tecla_ExportarSubFlow, Mnm_ExportarSubFlow, true));

    menu.addSeparator();
    // menu.add(Faz_MenuItem_File(Mesg.LerBiblioteca,janela,Tecla_LerBiblioteca,Mnm_LerBiblioteca));
    menu.add(Faz_MenuItem_File(Mesg.DownloadBiblioteca, janela, 0, 0, true));
    menu.add(Faz_MenuItem_File(Mesg.AdicionarBiblioteca, janela, 0, 0));

    // menu.addSeparator();
    // menu.add(Faz_MenuItem_File(Mesg.ImportarComposto,janela,Tecla_ImportarComposto,Mnm_ImportarComposto));
    // menu.add(Faz_MenuItem_File(Mesg.ExportarComposto,janela,Tecla_ExportarComposto,Mnm_ExportarComposto));
    menu.addSeparator();
    menu.add(Faz_MenuItem_Print(Mesg.MenuPageFormat, janela, 0, 0));
    menu.add(Faz_MenuItem_Print(Mesg.MenuPrint, janela, 0, 0));

    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.SwitchWorkspace, janela, 0, 0));

    menu.addSeparator();
    menu.add(Faz_MenuItem_File(Mesg.Sair, janela, Tecla_Sair, Mnm_Sair));

    menubar.add(menu);

    /* menu Editar */
    menu = new JMenu(Mesg.MenuEditar);
    menu.setMnemonic(Mnm_Editar);

    menu.add(Faz_MenuItem_Componente(Mesg.MenuDuplicar, janela, Tecla_Duplicar, Mnm_Duplicar));
    menu.add(Faz_MenuItem_Componente(Mesg.MenuEliminar, janela, Tecla_Eliminar, Mnm_Eliminar));
    menu.add(Faz_MenuItem_Componente(Mesg.MenuMudarNome, janela));

    menu.add(Faz_MenuItem_Componente(Mesg.MenuUndo, janela, Tecla_Undo, Mnm_Undo));
    menu.add(Faz_MenuItem_Componente(Mesg.AlteraAtributos, janela));
    menubar.add(menu);

    FlowEditorConfig cfg = FlowEditorConfig.loadConfig();
    /* menu de componentes */
    Cria_Menu_C_Biblioteca(bib, janela);
    bibIndex = menubar.getComponentCount() - 1;
    
    menu = new JMenu(Mesg.MenuOpcoes);
    menu.add(Faz_MenuItem_Opcoes(Mesg.VerLinhas, janela, true));
    menu.add(Faz_MenuItem_Opcoes(Mesg.VerComponentes, janela, true));
    menu.add(Faz_MenuItem_Opcoes(Mesg.DrawAll, janela, true));
    menu.add(Faz_MenuItem_Opcoes(Mesg.AntiAlias, janela, true));
    showBlockLibItem = Faz_MenuItem_Opcoes(Mesg.ShowBlockLibrary, janela, true);
    menu.add(showBlockLibItem);
    menu.add(Faz_MenuItem_Opcoes(Mesg.ConfirmarSaida, janela, cfg.isConfirmExit()));
    menu.add(Faz_MenuItem_Opcoes2(Mesg.MudaCores, janela));
    menu.add(Faz_MenuItem_Opcoes2(Mesg.SaveColors, janela));

    // Language
    boolean isDefault = FlowEditorConfig.LOCALE_USE_DEFAULT.equals(cfg.getUseLocale());
    boolean isIFlow = FlowEditorConfig.LOCALE_USE_IFLOW.equals(cfg.getUseLocale());
    boolean isSelected = FlowEditorConfig.LOCALE_USE_SELECTED.equals(cfg.getUseLocale());
    
    ButtonGroup langButtonGroup = new ButtonGroup(  );

    JMenu langMenu = new JMenu(Messages.getString("Menu.Options.Language"));
    JRadioButtonMenuItem item = null;

    langMenu.add(item = new JRadioButtonMenuItem(Messages.getString("Menu.Options.Language.Default"), isDefault));
    item.addActionListener(new ActionListenerLanguage(janela, FlowEditorConfig.LOCALE_USE_DEFAULT, null));
    langButtonGroup.add(item);

    langMenu.add(item = new JRadioButtonMenuItem(Messages.getString("Menu.Options.Language.iFlow"), isIFlow));
    item.addActionListener(new ActionListenerLanguage(janela, FlowEditorConfig.LOCALE_USE_IFLOW, null));
    langButtonGroup.add(item);

    for (Locale lang : FlowEditorConfig.AVAILABLE_LANGS) {
      langMenu.add(item = new JRadioButtonMenuItem(StringUtils.capitalize(lang.getDisplayLanguage(lang)), isSelected && lang.equals(cfg.getSelectedLocale())));
      item.addActionListener(new ActionListenerLanguage(janela, FlowEditorConfig.LOCALE_USE_SELECTED, lang));
      langButtonGroup.add(item);

    }

    menu.add(langMenu);

    menubar.add(menu);

    /** ************************ */
    /* menu about */
    menu = new JMenu(Messages.getString("Trata_Menus.tools")); //$NON-NLS-1$
    menu.add(Faz_MenuItem_Opcoes2(Mesg.Validate, janela));
    menu.add(Faz_MenuItem_Opcoes2(Mesg.RunGC, janela));
    menu.addSeparator();
    menu.add(Faz_MenuItem_Opcoes2(Mesg.ChooseBlock, janela));
    menu.add(Faz_MenuItem_Opcoes2(Mesg.SearchBlock, janela));
    menu.add(Faz_MenuItem_Opcoes2(Mesg.SearchBlockWithVar, janela));
    menu.addSeparator();
    menu.add(Faz_MenuItem_Opcoes2(Mesg.TemplateManager, janela));
    menu.addSeparator();
    menu.add(Faz_MenuItem_Opcoes2(Mesg.OpenIFlow, janela, true));
    // menubar.setHelpMenu(menu);
    menubar.add(menu);

    /** ***menu ready2run *** *** *****/
    menu = new JMenu(Messages.getString("Trata_Menus.ready2run")); //$NON-NLS-1$
    menu.add(Faz_MenuItem_Opcoes2(Mesg.s2rInstall, janela));
    // menubar.setHelpMenu(menu);
    menubar.add(menu);

    /* menu about */
    // menu=new JMenu(Mesg.About);
    menu = new JMenu(Messages.getString("Trata_Menus.help")); //$NON-NLS-1$
    menu.add(Faz_MenuItem_Opcoes2(Mesg.About, janela));
    // menubar.setHelpMenu(menu);
    menubar.add(menu);

    return menubar;
  }

  public void setShowLibSelected(boolean b) {
    if(null != showBlockLibItem) {
      showBlockLibItem.setSelected(b);
    }
  }

  /*****************************************************************************
   * cria um novo item para menu de opcoes
   */
  private JMenuItem Faz_MenuItem_Opcoes(String nome, Janela janela, boolean _enable) {
    JCheckBoxMenuItem menuitem = new JCheckBoxMenuItem(nome);
    menuitem.addActionListener(new ActionListenerOpcoes(janela, nome));
    menuitem.setState(_enable);
    return menuitem;
  }

  /*****************************************************************************
   * cria um novo item para menu de opcoes
   */
  private JMenuItem Faz_MenuItem_Opcoes2(String nome, Janela janela) {
    return Faz_MenuItem_Opcoes2(nome, janela, false);
  }

  private JMenuItem Faz_MenuItem_Opcoes2(String nome, Janela janela, boolean isOnline) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerOpcoes(janela, nome));
    if(isOnline) {
      menuitem.setEnabled(janela.getRepository().checkConnection());
      janela.addOnlineComponent(menuitem);
    }
    return menuitem;
  }

  /*****************************************************************************
   * cria um novo item para menus, item este que fica associado a uma funcao da
   * janela a que pertence (Desenho.Trata_File)
   */
  private JMenuItem Faz_MenuItem_File(String nome, Janela janela, int tecla, int mnm) {
    return this.Faz_MenuItem_File(nome, janela, tecla, mnm, false);
  }

  private JMenuItem Faz_MenuItem_File(String nome, Janela janela, int tecla, int mnm, boolean bIsOnline) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerFile(janela, nome));
    if (tecla != 0)
      // menuitem.setAccelerator(KeyStroke.getKeyStroke(tecla, ActionEvent.ALT_MASK));
      menuitem.setAccelerator(KeyStroke.getKeyStroke(tecla, ActionEvent.CTRL_MASK));

    if (mnm != 0)
      menuitem.setMnemonic(mnm);

    if(bIsOnline) {
      menuitem.setEnabled(janela.getRepository().checkConnection());
      janela.addOnlineComponent(menuitem);
    }
    return menuitem;
  }

  /*****************************************************************************
   * cria um novo item para menus, item este que fica associado a uma funcao da
   * janela a que pertence (Desenho.Trata_Componente)
   */
  private JMenuItem Faz_MenuItem_Componente(String nome, Janela janela, int tecla, int mnm) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerComponente(janela, nome));
    if (tecla != 0)
      // menuitem.setAccelerator(KeyStroke.getKeyStroke(tecla,
      // ActionEvent.ALT_MASK));
      menuitem.setAccelerator(KeyStroke.getKeyStroke(tecla, ActionEvent.CTRL_MASK));

    if (mnm != 0)

      menuitem.setMnemonic(mnm);
    return menuitem;
  }

  private JMenuItem Faz_MenuItem_Componente(String nome, Janela janela) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerComponente(janela, nome));
    return menuitem;
  }

  /*****************************************************************************
   * cria um novo item para menus, item este que fica associado a uma funcao da
   * janela a que pertence (Desenho.Trata_Vazio)
   */
  private JMenuItem Faz_MenuItem_Vazio(String nome, Janela janela, Library b) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerVazio(janela, nome, b));
    return menuitem;
  }

  /*****************************************************************************
   * cria um novo item para menu de Routing
   * 
   * private JMenuItem Faz_MenuItem_Route(String nome,Janela janela) { JMenuItem
   * menuitem=new JMenuItem(nome); menuitem.addActionListener(new
   * ActionListenerRoute(janela,new String(nome))); return menuitem; }
   */
  /*****************************************************************************
   * cria um novo item para menu de Print
   */
  private JMenuItem Faz_MenuItem_Print(String nome, Janela janela, int tecla, int mnm) {
    JMenuItem menuitem = new JMenuItem(nome);
    menuitem.addActionListener(new ActionListenerPrint(janela, nome));
    if (tecla != 0)
      menuitem.setAccelerator(KeyStroke.getKeyStroke(tecla,
          // ActionEvent.ALT_MASK));
          ActionEvent.CTRL_MASK));
    if (mnm != 0)
      menuitem.setMnemonic(mnm);
    return menuitem;
  }

  /*****************************************************************************
   * cria o menu com a lista dos componentes
   */
  public JMenu Cria_Menu_C_Biblioteca(LibrarySet bibs, Janela janela) {
    /* criar menu */
    if (menu_comp)
      menubar.remove(bibIndex);
    JMenu menu = new JMenu(Mesg.MenuComponente);
    menu.setMnemonic(Mnm_Componente);

    String[] saLibs = bibs.getLibraryKeys();
    for (int int_lib = 0; int_lib < saLibs.length; int_lib++) {
      Library bib = bibs.getLibrary(saLibs[int_lib]);

      int items = 0;

      JMenu subMenu = new JMenu(bib.getName());
      menu.add(subMenu);

      Iterator<Componente_Biblioteca> it = bib.getAllComponents();
      while (it.hasNext()) {
        Componente_Biblioteca cb = it.next();
        if(cb.isAutomatic()) continue; // ignore automatic components

        String descr = cb.Descricao;
        if(StringUtils.isNotBlank(cb.descrKey))
          descr = janela.getBlockMessages().getString(cb.descrKey);

        subMenu.add(Faz_MenuItem_Vazio(descr, janela, bib));
        items++;
      }

      if (items == 0) {
        JMenuItem mi = new JMenuItem(Mesg.Vazio);
        mi.setEnabled(false);
        subMenu.add(mi);
      }
    }

    menubar.add(menu, bibIndex);
    menu_comp = true;
    return menu;
  }

  /*****************************************************************************
   * devolve o popup-menu referente a nenhum item activo
   */
  public JPopupMenu Da_Popup_Vazio(Desenho d) {
    if(d.isEditable()) {
      return pop_vazio;
    } else {
      return pop_vazio_PS;
    }
  }

  /*****************************************************************************
   * cria o popup referido acima
   */
  public JPopupMenu Inicia_Popup_Vazio(LibrarySet bibs, Janela janela) {
    JPopupMenu retObj = initPopVazio(bibs, janela);
    initPopVazioPS(janela);
    return retObj;
  }
  
  private JPopupMenu initPopVazio(LibrarySet bibs, Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_vazio != null)
      cp.remove(pop_vazio);
    /* vazio (sem comonentes */
    pop_vazio = new JPopupMenu(Mesg.MenuVazio);

    pop_vazio.add(Faz_MenuItem_Componente(Mesg.MenuUndo, janela, Tecla_Undo, Mnm_Undo));
    pop_vazio.add(Faz_MenuItem_Vazio(Mesg.SearchBlock, janela, null));
    pop_vazio.add(Faz_MenuItem_Vazio(Mesg.SearchBlockWithVar, janela, null));
    pop_vazio.addSeparator();

    if (bibs != null) {
      JMenu menu = new JMenu(Mesg.MenuNovoComponente);

      String[] saLibs = bibs.getLibraryKeys();
      for (int int_lib = 0; int_lib < saLibs.length; int_lib++) {
        Library bib = bibs.getLibrary(saLibs[int_lib]);
        JMenu m = new JMenu(bib.getName());

        Iterator<Componente_Biblioteca> it = bib.getAllComponents();
        while (it.hasNext()) {
          Componente_Biblioteca cb = it.next();
          if(cb.isAutomatic()) continue; // ignore automatic components
          
          String descr = cb.Descricao;
          if(StringUtils.isNotBlank(cb.descrKey))
            descr = janela.getBlockMessages().getString(cb.descrKey);
          m.add(Faz_MenuItem_Vazio(descr, janela, bib));
        }
        menu.add(m);
      }

      pop_vazio.add(menu);
      pop_vazio.addSeparator();
      pop_vazio.add(Faz_MenuItem_Vazio(Mesg.MenuNovaLinha, janela, null));
    }
    return pop_vazio;
  }
  
  private JPopupMenu initPopVazioPS(Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_vazio_PS != null)
      cp.remove(pop_vazio_PS);
    pop_vazio_PS = new JPopupMenu(Mesg.MenuVazio);
    pop_vazio_PS.add(Faz_MenuItem_Vazio(Mesg.SearchBlock, janela, null));
    pop_vazio_PS.add(Faz_MenuItem_Vazio(Mesg.SearchBlockWithVar, janela, null));
    return pop_vazio_PS;
  }
  
  /**
   * Devolve popup-menu referente a componentes.
   */
  public JPopupMenu Da_Popup_Componente(Desenho origem, List<DesenhoScrollPane> tabs) {
    buildTabPopup(origem, tabs);
    if(origem.isEditable()) {
      return pop_componente;
    } else {
      return pop_componente_PS;
    }
  }

  private void buildTabPopup(Desenho origem, List<DesenhoScrollPane> tabs) {
    menu_tabs.removeAll();
    menu_tabs.setEnabled(false);
    if(tabs != null) {
      for (DesenhoScrollPane desenhoScrollPane : tabs) {
        if(origem == desenhoScrollPane.getDesenho()) continue; // same instance
        JMenuItem item = new JMenuItem(desenhoScrollPane.getName());
        item.addActionListener(new ActionListenerCopyToTab(origem, desenhoScrollPane.getDesenho()));
        menu_tabs.add(item);
      }
      menu_tabs.setEnabled(menu_tabs.getSubElements().length > 0);
    }
  }

  /*****************************************************************************
   * cria popup-menu acima referido
   */
  public JPopupMenu Inicia_Popup_Componente(LibrarySet bibs, Janela janela) {
    JPopupMenu retObj = initPopComponente(bibs, janela);
    initPopComponentePS(janela);
    return retObj;
  }

  public JPopupMenu initPopComponente(LibrarySet bibs, Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_componente != null)
      cp.remove(pop_componente);
    pop_componente = new JPopupMenu(Mesg.MenuAccaoComponente);
    menu_tabs = new JMenu(Mesg.MenuDuplicarPara);
    menu_tabs.setEnabled(false);

    /* juntar as opcoes de duplicar,apagar e de mudar nome */
    pop_componente.add(Faz_MenuItem_Componente(Mesg.MenuDuplicar, janela, Tecla_Duplicar, Mnm_Duplicar));
    pop_componente.add(menu_tabs);
    pop_componente.add(Faz_MenuItem_Componente(Mesg.MenuEliminar, janela, Tecla_Eliminar, Mnm_Eliminar));
    pop_componente.add(Faz_MenuItem_Componente(Mesg.MenuMudarNome, janela));
    pop_componente.add(Faz_MenuItem_Componente(Mesg.MenuUndo, janela, Tecla_Undo, Mnm_Undo));

    /* ver interior do sub circuito */
    // pop_componente.add(Faz_MenuItem_Componente(Mesg.MenuSubCircuito,janela));
    pop_componente.add(Faz_MenuItem_Componente(Mesg.AlteraAtributos, janela));

    return pop_componente;
  }
  
  public JPopupMenu initPopComponentePS(Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_componente_PS != null)
      cp.remove(pop_componente_PS);
    pop_componente_PS = new JPopupMenu(Mesg.MenuAccaoComponente);
    pop_componente_PS.add(Faz_MenuItem_Vazio(Mesg.SearchBlock, janela, null));
    pop_componente_PS.add(Faz_MenuItem_Vazio(Mesg.SearchBlockWithVar, janela, null));
    pop_componente_PS.add(Faz_MenuItem_Componente(Mesg.MenuVerRegistos, janela));
    return pop_componente_PS;
  }
  

  /*****************************************************************************
   * devolve popup-mene referente a linhas
   */
  public JPopupMenu Da_Popup_Linha(Desenho d) {
    if(d.isEditable()) {
      return pop_linha;
    } else {
      return pop_linha_PS;
    }
  }

  /*****************************************************************************
   * cria popup-menu acima referido
   */
  public JPopupMenu Inicia_Popup_Linha(Janela janela) {
    JPopupMenu retObj = initPopLinha(janela);
    initPopLinhaPS(janela);
    return retObj;
  }
  
  private JPopupMenu initPopLinha(Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_linha != null)
      cp.remove(pop_linha);
    pop_linha = new JPopupMenu(Mesg.MenuLinha);
    /* juntar as opcoes de duplicar,apagar e de mudar nome */
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuDuplicar, janela));
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuEliminar, janela));
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuInserePontoQuebra, janela));
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuRetiraPontoQuebra, janela));
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuGotoStart, janela));
    pop_linha.add(Faz_MenuItem_Componente(Mesg.MenuGotoEnd, janela));
    return pop_linha;
  }
  
  private JPopupMenu initPopLinhaPS(Janela janela) {
    Container cp = janela.getContentPane();
    if (pop_linha_PS != null)
      cp.remove(pop_linha_PS);
    pop_linha_PS = new JPopupMenu(Mesg.MenuLinha);
    pop_linha_PS.add(Faz_MenuItem_Componente(Mesg.MenuGotoStart, janela));
    pop_linha_PS.add(Faz_MenuItem_Componente(Mesg.MenuGotoEnd, janela));
    return pop_linha_PS;
  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Trata_File
   */
  static class ActionListenerFile implements ActionListener {
    Janela janela;
    String nome;

    public ActionListenerFile(Janela j, String n) {
      janela = j;
      nome = n;
    }

    public void actionPerformed(ActionEvent evt) {
      janela.processFileMenuActions(nome);
    }
  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Trata_Componente
   */
  static class ActionListenerComponente implements ActionListener {
    Janela janela;
    String nome;

    public ActionListenerComponente(Janela j, String n) {
      janela = j;
      nome = n;
    }

    public void actionPerformed(ActionEvent evt) {
      janela.processComponentMenuActions(nome);
    }
  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Trata_Componente
   */
  static class ActionListenerCopyToTab implements ActionListener {
    Desenho origem;
    Desenho destino;

    public ActionListenerCopyToTab(Desenho origem, Desenho destino) {
      this.origem = origem;
      this.destino = destino;
    }

    public void actionPerformed(ActionEvent evt) {
      origem.copyTo(destino);
    }
  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Trata_Vazio
   */
  static class ActionListenerVazio implements ActionListener {
    Janela janela;
    String nome;
    Library bib;

    public ActionListenerVazio(Janela j, String n, Library b) {
      janela = j;
      nome = n;
      bib = b;
    }

    public void actionPerformed(ActionEvent evt) {
      Desenho d = janela.getSelectedDesenho();
      if(d != null)
        d.processPopUpMenuActions(nome, bib);
    }
  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Trata_Route
   * 
   * class ActionListenerRoute implements ActionListener { Janela janela; String
   * nome; public ActionListenerRoute(Janela j,String n) { janela=j; nome=n; }
   * public void actionPerformed(ActionEvent evt) {
   * janela.canvas.Trata_Route(nome); } }
   */
  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Printing
   */
  static class ActionListenerPrint implements ActionListener {
    Janela janela;
    String nome;

    public ActionListenerPrint(Janela j, String n) {
      janela = j;
      nome = n;
    }

    public void actionPerformed(ActionEvent evt) {
      Desenho d = janela.getSelectedDesenho();
      if(d != null)
        d.Printing(nome);
    }

  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Printing
   */
  static class ActionListenerOpcoes implements ActionListener {
    Janela janela;
    String nome;

    public ActionListenerOpcoes(Janela j, String n) {
      janela = j;
      nome = n;
    }

    public void actionPerformed(ActionEvent evt) {
      janela.processOptionMenu(nome);
    }

  }

  /*****************************************************************************
   * Classe destinada a chamar uma funcao de Desenho - Printing
   */
  static class ActionListenerLanguage implements ActionListener {
    Janela janela;
    String type;
    Locale loc;

    public ActionListenerLanguage(Janela j, String type, Locale loc) {
      janela = j;
      this.type = type;
      this.loc = loc;
    }

    public void actionPerformed(ActionEvent evt) {
      FlowEditorConfig cfg = FlowEditorConfig.loadConfig();
      if(FlowEditorConfig.LOCALE_USE_DEFAULT.equals(type)) {
        cfg.setUseLocale(FlowEditorConfig.LOCALE_USE_DEFAULT);
      } else if(FlowEditorConfig.LOCALE_USE_IFLOW.equals(type)) {
        cfg.setUseLocale(FlowEditorConfig.LOCALE_USE_IFLOW);
      } else if(FlowEditorConfig.LOCALE_USE_SELECTED.equals(type)) {
        cfg.setUseLocale(FlowEditorConfig.LOCALE_USE_SELECTED);
        cfg.setSelectedLocale(loc.toString());
      }
      cfg.saveConfig();
      JOptionPane.showMessageDialog(janela, Messages.getString("Menu.Options.Language.Changed"), Messages.getString("Menu.Options.Language.Title"), JOptionPane.INFORMATION_MESSAGE);
    }

  }
}
