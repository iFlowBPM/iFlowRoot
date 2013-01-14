package pt.iknow.floweditor;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: Login
 *
 *  desc: janela de login
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import pt.iknow.floweditor.messages.Messages;
import pt.iknow.iflow.RepositoryWebClient;
import pt.iknow.utils.StringUtilities;


public class Login {

  private static Color cBORDER_COLOR = new Color(0x00,0x00,0x00); // ao mudar funciona
//  public static Color cBG_COLOR = new Color(0xE9,0xF1,0xFA); // ao mudar funciona
//  public static Color cFG_COLOR = new Color(0xAB,0xCE,0x38);
  private static Color cFG_COLOR = new Color(0x00,0x00,0x00);
//  public static Color cYELLOW = new Color(0x47,0x9F,0xD0);
//  public static Color cBLUE = new Color(0xDD,0xDD,0xDD); // botoes

  private Frame _fParent = null;
  private RepositoryWebClient _rRep = null;

  private JDialog _jdLogin = null;
  private JComboBox _jtfRepHost = null;
  private JTextField _jtfUserName = null;
  private JPasswordField _jpfUserPass = null;
  private JLabel _capsLabel = null;

  private String _siFlowURL = null;
  private String _sUserName = null;
  private String _sUserPass = null;
  private boolean _bLogged = false;

  private static int _nLoginCounter = 0;
  private static int nLOGIN_ATTEMPTS = 3;

  private final static int PANEL_WIDTH = 400; // largura do login
  private final static int PANEL_HEIGHT = 200; // altura do login  

  //public final static int MAIN_FONT_SIZE = 12; // e muda !!
  //public final static int TITLE_FONT_INCREMENT = 4;
  //public final static int TITLE_SMALL_FONT_INCREMENT = 2;
  private final static int FONT_INCREMENT = 0;
  
  private FlowEditorConfig feConfig = null;

  public Login(Frame afParent, FlowEditorConfig cfg) {
    this._fParent = afParent;
    this.feConfig = cfg;
    this.init();
  }

  public void init() {

    int nDefaultIndex = 0;
    //vectorURL.add("");
    if (feConfig != null) {
      nDefaultIndex = feConfig.getDefaultUrlIndex().intValue();
      if (nDefaultIndex < 0) {
        nDefaultIndex = 0;
      }
    }
    
    _jdLogin = new JDialog(this._fParent,Messages.getString("Login.title"),true); //$NON-NLS-1$

    _jdLogin.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    _jdLogin.setResizable(false);

    Font fVerdana = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    Font[] fa = ge.getAllFonts();
    for (int i=0; i < fa.length; i++) {
      if (fa[i].getName().equalsIgnoreCase("Verdana")) { //$NON-NLS-1$
        fVerdana = fa[i];
        break;
      }
    }

    Font fMainFont = _jdLogin.getFont();

    Font fFont = null;

    if (fVerdana != null) {
      fFont = fVerdana.deriveFont(Font.BOLD,fMainFont.getSize()+FONT_INCREMENT);
    }
    else {
      fFont = fMainFont.deriveFont(Font.BOLD,fMainFont.getSize()+FONT_INCREMENT);
    }


    GridBagConstraints sC = new GridBagConstraints();
    sC.fill = GridBagConstraints.HORIZONTAL;
    JPanel jPanel = new JPanel();
    jPanel.setLayout(new GridBagLayout());
    //jPanel.setBackground(cBG_COLOR);

    JLabel jLabel = null;

    Insets lInsets = new Insets(1,0,2,0);
    Insets rInsets = new Insets(1,20,2,0);

    String stmp = Messages.getString("Login.server"); //$NON-NLS-1$
    jLabel = new JLabel(stmp);
    jLabel.setFont(fFont);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    //jLabel.setBackground(cBG_COLOR);
    jLabel.setForeground(cFG_COLOR);
    jLabel.setLabelFor(this._jtfRepHost);
    sC.gridx = 0;
    sC.gridy = 0;
    sC.insets = new Insets(1,0,10,0);
    jPanel.add(jLabel,sC);

    KeyListener keyListener = new KeyAdapter() {

      public void keyPressed(KeyEvent e) {
        if(null != _capsLabel)
          _capsLabel.setVisible(e.getComponent().getToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK));
      }

    };
    
    
    Vector<String> urls = new Vector<String>(feConfig.getListFlowURL().size());
    for (FlowRepUrl repUrl : feConfig.getListFlowURL()) {
      urls.add(repUrl.getUrl());
    }

    this._jtfRepHost = new JComboBox(urls);
    this._jtfRepHost.setEditable(true);
    this._jtfRepHost.addKeyListener(keyListener);
    this._jtfRepHost.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int pos = _jtfRepHost.getSelectedIndex();
        String text = ""; //$NON-NLS-1$
        if(pos >= 0) {
          text = feConfig.getListFlowURL().get(pos).getUser();
        }
        _jtfUserName.setText(text);
        updateFocusElement();
      }
    });

    sC.gridx = 1;
    sC.gridy = 0;
    sC.insets = new Insets(1,20,10,0);
    jPanel.add(this._jtfRepHost,sC);
    sC.gridwidth = 1;


    // USER
    String sUsernameLabel = Messages.getString("Login.user"); //$NON-NLS-1$
    this._jtfUserName = new JTextField(15);
    this._jtfUserName.setBorder(BorderFactory.createLineBorder(cBORDER_COLOR));    
    this._jtfUserName.addKeyListener(keyListener);
    this._jtfUserName.requestFocus();
    
    jLabel = null;
    jLabel = new JLabel(sUsernameLabel);
    jLabel.setFont(fFont);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setForeground(cFG_COLOR);
    jLabel.setLabelFor(this._jtfUserName);
    sC.gridx = 0;
    sC.gridy = 1;
    sC.insets = lInsets;
    jPanel.add(jLabel,sC);
    sC.gridx = 1;
    sC.gridy = 1;
    sC.insets = rInsets;
    jPanel.add(this._jtfUserName,sC);
    sC.gridwidth = 1;


    // PASSWORD
    String sPasswordLabel = Messages.getString("Login.userpassword"); //$NON-NLS-1$
    this._jpfUserPass = new JPasswordField(15);
    this._jpfUserPass.addKeyListener(keyListener);
    this._jpfUserPass.setBorder(BorderFactory.createLineBorder(cBORDER_COLOR, 1));

    jLabel = null;
    jLabel = new JLabel(sPasswordLabel);
    jLabel.setFont(fFont);
    jLabel.setHorizontalAlignment(JLabel.LEFT);
    jLabel.setForeground(cFG_COLOR);
    jLabel.setLabelFor(this._jpfUserPass);
    sC.gridx = 0;
    sC.gridy = 2;
    sC.insets = lInsets;
    jPanel.add(jLabel,sC);
    sC.gridwidth = 1;
    sC.gridx = 1;
    sC.gridy = 2;
    sC.insets = rInsets;
    jPanel.add(this._jpfUserPass,sC);
    
    // set default username if there is one - check above
    if (nDefaultIndex >= 0) {
      this._jtfRepHost.setSelectedIndex(nDefaultIndex); // The _jtfRepHost action event will change the value automatically
    }
    
    try {
      boolean capsEnabled = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
      _capsLabel = new JLabel("Caps ON");
      _capsLabel.setForeground(Color.RED);
      _capsLabel.setVisible(capsEnabled);
      sC.gridx = 1;
      sC.gridy = 3;
      jPanel.add(this._capsLabel,sC);
      sC.gridwidth = 1;
    } catch(UnsupportedOperationException e) {
      _capsLabel = null;
      FlowEditor.log("Can't check CAPS LOCK key status programatically.");
    }

    // OK BUTTON
    JButton jButton = new JButton();
    jButton.setText(Mesg.OK);
    jButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        doLogin(evt);
      }
    });
    jButton.setToolTipText(Messages.getString("Login.tooltip.login"));     //$NON-NLS-1$
    // EXIT BUTTON
    JButton jButton2 = new JButton();
    jButton2.setText(Messages.getString("Login.exit")); //$NON-NLS-1$
    jButton2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
      }
    });
    jButton2.setToolTipText(Messages.getString("Login.tooltip.exit"));     //$NON-NLS-1$

    // Offline BUTTON
    JButton jButton3 = new JButton();
    jButton3.setText(Messages.getString("Login.offline")); //$NON-NLS-1$
    jButton3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        continueOffline();
      }
    });
    jButton3.setToolTipText(Messages.getString("Login.tooltip.offline"));     //$NON-NLS-1$
    
    // PROXY BUTTON
    JButton jButton4 = new JButton();
    jButton4.setText(Messages.getString("Login.proxy")); //$NON-NLS-1$
    jButton4.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        configureProxy();
      }
    });
    jButton4.setToolTipText(Messages.getString("Login.tooltip.proxy"));     //$NON-NLS-1$
    
    JPanel jpButton = new JPanel();
    jpButton.add(jButton,null);
    jpButton.add(jButton2,null);
    jpButton.add(jButton3,null);
    jpButton.add(jButton4,null);

    JRootPane jpMain = _jdLogin.getRootPane();
    jpMain.setLayout(new BorderLayout());
    jpMain.add(jpButton, BorderLayout.SOUTH);
    jpMain.add(jPanel,BorderLayout.CENTER);

    jpMain.setDefaultButton(jButton);

    _jdLogin.setSize(PANEL_WIDTH,PANEL_HEIGHT);
    _jdLogin.setLocationRelativeTo(_jdLogin.getParent());
    //_jdLogin.setLocation(100, 100); 

    _jdLogin.addWindowListener(new WindowAdapter() {
    	public void windowOpened(WindowEvent e){
    		updateFocusElement();
    	}
    });
    
    //_jdLogin.pack();  // fica feio.
    _jdLogin.setVisible(true);

  }

  private void updateFocusElement() {
	  _jtfUserName.requestFocusInWindow();
	  if (StringUtilities.isNotEmpty(_jtfUserName.getText())) {
		  _jpfUserPass.requestFocusInWindow();
	  }
  }
  
  private void configureProxy() {
    ProxyDialog.open(_jdLogin, feConfig);
  }

  private void continueOffline() {
    // no need to login since repository not required and repository not specified.
    // ask user if user wants to continue without repository
    Object [] options = new Object[]{ Mesg.Sim, Mesg.Nao };
    int n = JOptionPane.showOptionDialog(this._jdLogin,
        Messages.getString("Login.continue_no_rep"), //$NON-NLS-1$
        Messages.getString("Login.title_choose"), //$NON-NLS-1$
        JOptionPane.YES_NO_OPTION,
        JOptionPane.QUESTION_MESSAGE,
        (Icon) null,
        options,
        options[1]);
    if (n == JOptionPane.OK_OPTION) {
      // reset repository
      this._sUserName = _jtfUserName.getText();
      this._sUserPass = new String(_jpfUserPass.getPassword());
      this._siFlowURL = (String) _jtfRepHost.getSelectedItem();
      this._rRep = new RepositoryWebClient(this._siFlowURL, this._sUserName, this._sUserPass, Thread.currentThread().getContextClassLoader(), feConfig, true);
      updateConfig(this._rRep.getIFlowURL());
      this._bLogged = true;

      this._jdLogin.dispose();
    }
  }
  
  private void updateConfig(FlowRepUrl url) {
    // search selected index
    int pos = -1;
    Vector<FlowRepUrl> urls = feConfig.getListFlowURL();
    for(int i = 0; i < urls.size(); i++) {
      FlowRepUrl u = urls.get(i);
      if(url.getUrl().equals(u.getUrl())) {
        pos = i;
        u.setUser(url.getUser());
        break;
      }
    }
    
    if(pos == -1) {
      pos = urls.size();
      urls.add(url);
    }
    
    feConfig.setDefaultUrlIndex(pos);
    feConfig.saveConfig();
  }
  
  private void doLogin(ActionEvent evt) {

    if (this._jtfRepHost == null || this._jtfUserName == null || this._jpfUserPass == null) {
      FlowEditor.log("null url, username or password"); //$NON-NLS-1$
      return;
    }

    this._sUserName = _jtfUserName.getText();
    this._sUserPass = new String(_jpfUserPass.getPassword());

    this._siFlowURL = (String) _jtfRepHost.getSelectedItem();

    if (StringUtilities.isNotEmpty(this._siFlowURL)) {

      try {
        new URL(this._siFlowURL);
      } catch (MalformedURLException me) {
        JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.invalid_url"), //$NON-NLS-1$
            Messages.getString("Login.title_warning"), //$NON-NLS-1$
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      this._rRep = new RepositoryWebClient(this._siFlowURL, this._sUserName, this._sUserPass, Thread.currentThread().getContextClassLoader(), feConfig, false);
      if (!this._rRep.checkConnection()) {
        this._rRep = null;
        JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.rep_unavailable"), //$NON-NLS-1$
            Messages.getString("Login.title_warning"), //$NON-NLS-1$
            JOptionPane.WARNING_MESSAGE);
        return;
      }
    } else {
      JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.fill_url"), //$NON-NLS-1$
          Messages.getString("Login.title_error"), //$NON-NLS-1$
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (StringUtilities.isEmpty(this._sUserName)) {
      JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.fill_name"), //$NON-NLS-1$
          Messages.getString("Login.title_warning"), //$NON-NLS-1$
          JOptionPane.WARNING_MESSAGE);

      return;
    }

    if (StringUtilities.isEmpty(this._sUserPass)) {
      JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.fill_password"), //$NON-NLS-1$
          Messages.getString("Login.title_warning"), //$NON-NLS-1$
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    if (this._rRep != null) {
      if (this._rRep.login(this._sUserName, this._sUserPass)) {
        FlowEditor.log(Messages.getString("Login.login_ok")); //$NON-NLS-1$
        this._bLogged = true;

        updateConfig(this._rRep.getIFlowURL());

        this._jdLogin.dispose();
        return;
      } else {
        // HERE
        _nLoginCounter++;
        FlowEditor.log(Messages.getString("Login.login_fail")); //$NON-NLS-1$
      }
    }

    if (_nLoginCounter == nLOGIN_ATTEMPTS) {
      // OOPS.. display message and exit

      JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.max_tries_error"), //$NON-NLS-1$
          Messages.getString("Login.title_error"), //$NON-NLS-1$
          JOptionPane.ERROR_MESSAGE);

      System.exit(-1);
    } else {
      JOptionPane.showMessageDialog(this._jdLogin, Messages.getString("Login.invalid_user_pass"), //$NON-NLS-1$
          Messages.getString("Login.title_error"), //$NON-NLS-1$
          JOptionPane.ERROR_MESSAGE);

      _jtfUserName.setText(""); //$NON-NLS-1$
      _jpfUserPass.setText(""); //$NON-NLS-1$
      this._sUserName = ""; //$NON-NLS-1$
      this._sUserPass = ""; //$NON-NLS-1$
    }
  }

  public String getRepURL() {
    return this._siFlowURL;
  }

  public String getUserName() {
    return this._sUserName;
  }

  public String getUserPass() {
    return this._sUserPass;
  }

  public boolean isLogged() {
    return this._bLogged;
  }

  public RepositoryWebClient getRepository() {
    return this._rRep;
  }
  
} // class
