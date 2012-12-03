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
package pt.iknow.floweditor.blocks;

/*****************************************************
 *
 *  Project FLOW EDITOR
 *
 *  class: AlteraAtributos
 *
 *  desc: dialogo para alterar atributos de um bloco
 *
 ****************************************************/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.wsdl.Operation;
import javax.wsdl.Port;
import javax.wsdl.Service;

import org.apache.axis.utils.XMLUtils;
import org.apache.axis.wsdl.gen.Parser;
import org.xml.sax.InputSource;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.iflow.RepositoryClient;
import pt.iknow.utils.swing.MyColumnEditorModel;
import pt.iknow.utils.swing.MyJTableX;
import pt.iknow.utils.swing.MyTableModel;
import pt.iknow.utils.wsdl.WSDLField;
import pt.iknow.utils.wsdl.WSDLUtils;

public class AlteraAtributosWebServiceSinc extends AbstractAlteraAtributos
		implements AlteraAtributosInterface {
	private static final long serialVersionUID = 134L;

	// private static final int _nPROPS_PANE = 0;
	private static final int _nIN_PANE = 1;
	private static final int _nOUT_PANE = 2;

	// next block is also defined in UniFlow block
	private static final String _sPROPS_PREFIX = "P"; //$NON-NLS-1$
	private static final String _sINPUT_PREFIX = "I"; //$NON-NLS-1$
	private static final String _sOUTPUT_PREFIX = "O"; //$NON-NLS-1$

	private static final int _nWSDL_IDX = 0;
	private static final int _nSERVICE_IDX = 1;
	private static final int _nPORT_IDX = 2;
	private static final int _nOPERATION_IDX = 3;
	private static final int _nTIMEOUT_IDX = 4;
	private static final int _nRETRIES_IDX = 5;
	private static final int _nAUTHENT_IDX = 6; // AUTHENTICATION
	private static final int _nUSERLOGIN_IDX = 7; // AUTHENTICATION
	private static final int _nUSERAUTH_IDX = 8; // AUTHENTICATION
	private static final int _nPASSAUTH_IDX = 9; // AUTHENTICATION
	
	private static final int _PROPS_SIZE = 10;

	// next block is also defined in UniFlow block
	private static final String _sWSDL = "WSDL"; //$NON-NLS-1$
	private static final String _sSERVICE = "SERVICE"; //$NON-NLS-1$
	private static final String _sPORT = "PORT"; //$NON-NLS-1$
	private static final String _sOPERATION = "OPERATION"; //$NON-NLS-1$
	private static final String _sTIMEOUT = "TIMEOUT"; //$NON-NLS-1$
	private static final String _sRETRIES = "RETRIES"; //$NON-NLS-1$
	private static final String _sAUTHENT = "AUTHENTICATION"; //$NON-NLS-1$ //AUTHENTICATION
	private static final String _sUSERLOGIN = "USERLOGIN"; //$NON-NLS-1$ //AUTHENTICATION
	private static final String _sUSERAUTH = "USERAUTH"; //$NON-NLS-1$ //AUTHENTICATION
	private static final String _sPASSAUTH = "PASSAUTH"; //$NON-NLS-1$ //AUTHENTICATION
	
	
	JTabbedPane jtp = new JTabbedPane();
	JPanel panel1 = new JPanel();
	BorderLayout borderLayout1 = new BorderLayout();
	JPanel jPanel1 = new JPanel();
	JPanel jPanel2 = new JPanel();
	JButton jButton1 = new JButton();
	JButton jButton2 = new JButton();
	JPanel jPanel3 = new JPanel();

	// PROPS
	JComboBox _jcbWSDL = null;
	JLabel _jlWSDL = null;
	JTextField _jtfWSDL = null;
	JButton _jbWSDL = null;
	JComboBox _jcbWSDLService = null;
	JComboBox _jcbWSDLPort = null;
	JComboBox _jcbWSDLOperation = null;

	JTextField _jtfTimeout = null;
	JTextField _jtfRetries = null;

	// Authentication
	JCheckBox _jcbAuth = null;
	JCheckBox _jcbUserLogin = null;
	JTextField _jtfUser = null;
	JPasswordField _jtfPass = null;
	JLabel _jLabelUserLogin = null;
	JLabel _jLabelUser = null;
	JLabel _jLabelPass = null;

	// WSDL
	// Objects
	// key: service name | value = service QName object
	private HashMap<String, Service> _hmServices = null;
	// key: port name | value = Port object
	private HashMap<String, Port> _hmPorts = null;
	// key: operation name | value = Operation object
	private HashMap<String, Operation> _hmOperations = null;

	// WSDL manipulation
	private WSDLUtils _wu = null;

	// INPUT
	MyJTableX jtInput = new MyJTableX();

	// OUTPUT
	MyJTableX jtOutput = new MyJTableX();

	JScrollPane jScrollPane1 = new JScrollPane();
	JScrollPane jScrollPane2 = new JScrollPane();
	JScrollPane jScrollPane3 = new JScrollPane();

	private int exitStatus = EXIT_STATUS_CANCEL;

	private String[][] propsData;

	// UniFlow -> SOAP
	// string[n][4] : for each n, 0=uniflow var, 1=input name, 2=input type, 3 =
	// isLeave
	private String[][] inputData;
	// SOAP -> UniFlow
	// string[n][4] : for each n, 0=input name, 1=input type, 2=uniflow var, 3 =
	// isLeave
	private String[][] outputData;

	// key: soap field name | value: uniflow var name
	private HashMap<String, String> hmInMapping = new HashMap<String, String>();

	// key: soap field name | value: uniflow var name
	private HashMap<String, String> hmOutMapping = new HashMap<String, String>();

	private String[] _saWSDL = null;

	private RepositoryClient _rep = null;
	private boolean _bInitialized = false;

	// next block is also defined in UniFlow block
	private final String _ESCOLHA;

	private final String _NEW;
	private final String[] _saInputColumnNames;
	private final String[] _saOutputColumnNames;

	// next block is also defined in UniFlow block
	private static final String _URL_REP_FILE = ".url"; //$NON-NLS-1$
	private static final String _WSDL_REP_FILE = ".wsdl"; //$NON-NLS-1$

	public AlteraAtributosWebServiceSinc(FlowEditorAdapter adapter) {
		super(adapter, "", true); //$NON-NLS-1$

		_rep = adapter.getRepository();

		_ESCOLHA = adapter.getString("AlteraAtributosWebServiceSinc.choose"); //$NON-NLS-1$

		_NEW = adapter.getString("AlteraAtributosWebServiceSinc.new"); //$NON-NLS-1$
		_saInputColumnNames = new String[] { "iFlow", //$NON-NLS-1$
				adapter.getString("AlteraAtributosWebServiceSinc.soapField"), //$NON-NLS-1$
				adapter.getString("AlteraAtributosWebServiceSinc.soapType"), //$NON-NLS-1$
		};
		_saOutputColumnNames = new String[] {
				adapter.getString("AlteraAtributosWebServiceSinc.soapField"), //$NON-NLS-1$
				adapter.getString("AlteraAtributosWebServiceSinc.soapType"), //$NON-NLS-1$
				"iFlow", //$NON-NLS-1$
		};

	}

	public int getExitStatus() {
		return exitStatus;
	}

	public String[][] getNewAttributes() {
		int propsLen = propsData.length;
		int inputLen = inputData.length;
		int outputLen = outputData.length;

		String[][] newAttributes = new String[propsLen + inputLen + outputLen][3];

		int counter = 0;

		for (int i = 0; i < propsLen; i++, counter++) {
			newAttributes[counter][0] = _sPROPS_PREFIX + propsData[i][0];
			newAttributes[counter][1] = propsData[i][1];
			newAttributes[counter][2] = propsData[i][2];
		}

		for (int i = 0; i < inputLen; i++, counter++) {
			newAttributes[counter][0] = _sINPUT_PREFIX + inputData[i][1]; // soap
																			// field
																			// name
			newAttributes[counter][1] = inputData[i][0]; // uniflow var
			newAttributes[counter][2] = inputData[i][2]; // soap field type
		}

		for (int i = 0; i < outputLen; i++, counter++) {
			newAttributes[counter][0] = _sOUTPUT_PREFIX + outputData[i][0]; // soap
																			// field
																			// name
			newAttributes[counter][1] = outputData[i][2]; // uniflow var
			newAttributes[counter][2] = outputData[i][1]; // soap field type
		}

		return newAttributes;
	}

	private void refreshWSDL() {
		String[] sa = new String[0];

		if (_rep != null) {
			sa = _rep.listWebServices();
		}

		if (sa == null || sa.length == 0) {
			_saWSDL = new String[1];
		} else {

			ArrayList<String> al = new ArrayList<String>();
			for (int i = 0; i < sa.length; i++) {
				if (sa[i].endsWith(_URL_REP_FILE)) {
					continue;
				}
				al.add(sa[i]);
			}

			_saWSDL = new String[al.size() + 2];
			_saWSDL[0] = _ESCOLHA;
			for (int i = 0; i < al.size(); i++) {
				_saWSDL[i + 1] = (String) al.get(i);
			}
		}

		_saWSDL[_saWSDL.length - 1] = _NEW;

	}

	private void genWSDLCombo() {

		this.refreshWSDL();

		if (this._jcbWSDL == null) {
			this._jcbWSDL = new JComboBox(_saWSDL);
			this._jcbWSDL.requestFocus();
		} else {
			this._jcbWSDL.removeAllItems();

			for (int i = 0; i < _saWSDL.length; i++) {
				this._jcbWSDL.addItem(_saWSDL[i]);
			}
		}

		this._jcbWSDL.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					String swsdl = (String) evt.getItem();

					if (swsdl.equals(_NEW)) {
						// enable new wsdl import
						enableNewWSDL(true);
						_jcbWSDLService.removeAllItems();
						_jcbWSDLService.setEnabled(false);
						disableCombos();
					} else {
						// disable new wsdl import
						enableNewWSDL(false);

						if (!swsdl.equals(_ESCOLHA)) {
							// enable service selection

							genWSDLServiceCombo(swsdl);

							String url = getWSDLURL(swsdl);
							_jtfWSDL.setText(url);

						} else {
							disableCombos();
						}
					}
				}
			}
		});

		this._jcbWSDL.updateUI();
	}

	private void disableCombos() {
		if (null != _jcbWSDLService) {
			_jcbWSDLService.removeAllItems();
			_jcbWSDLService.setEnabled(false);
		}
		if (null != _jcbWSDLPort) {
			_jcbWSDLPort.removeAllItems();
			_jcbWSDLPort.setEnabled(false);
		}
		if (null != _jcbWSDLOperation) {
			_jcbWSDLOperation.removeAllItems();
			_jcbWSDLOperation.setEnabled(false);
		}
	}

	private void genWSDLServiceCombo(String asWSDL) {

		String[] saServices = new String[0];
		boolean bEnabled = false;

		if (asWSDL != null) {
			try {
				saServices = this.getWSDLServices(asWSDL);

				bEnabled = true;
			} catch (Exception e) {
				adapter.log("GEN WSDL SERVICE COMBO EXCEPTION: ", e); //$NON-NLS-1$
			}
		}

		if (this._jcbWSDLService == null) {
			this._jcbWSDLService = new JComboBox(saServices);
		} else {
			this._jcbWSDLService.removeAllItems();

			for (int i = 0; i < saServices.length; i++) {
				this._jcbWSDLService.addItem(saServices[i]);
			}
		}

		this._jcbWSDLService.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					String sService = (String) evt.getItem();

					try {

						if (!sService.equals(_ESCOLHA)) {
							// enable port selection
							genWSDLPortCombo(sService);
						} else {
							// disable port selection
							_jcbWSDLPort.removeAllItems();
							_jcbWSDLPort.setEnabled(false);
						}

					} catch (Exception e) {
						adapter.log("UNABLE TO CHANGE SERVICE STATE: ", e); //$NON-NLS-1$
					}
				}
			}
		});

		this._jcbWSDLService.setEnabled(bEnabled);
	}

	private void genWSDLPortCombo(String asWSDLService) {

		boolean bEnabled = false;
		String[] saPorts = new String[0];

		if (asWSDLService != null) {
			try {
				saPorts = getWSDLServicePorts(asWSDLService);
				bEnabled = true;
			} catch (Exception e) {
				adapter.log("GEN WSDL PORTS COMBO EXCEPTION: ", e); //$NON-NLS-1$
			}
		}

		if (this._jcbWSDLPort == null) {
			this._jcbWSDLPort = new JComboBox(saPorts);
		} else {
			this._jcbWSDLPort.removeAllItems();

			for (int i = 0; i < saPorts.length; i++) {
				this._jcbWSDLPort.addItem(saPorts[i]);
			}
		}

		this._jcbWSDLPort.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					String sPort = (String) evt.getItem();

					try {

						if (!sPort.equals(_ESCOLHA)) {
							// enable operation selection
							genWSDLOperationCombo(sPort);
						} else {
							_jcbWSDLOperation.removeAllItems();
							_jcbWSDLOperation.setEnabled(false);
						}

					} catch (Exception e) {
						adapter.log("UNABLE TO CHANGE PORT STATE: ", e); //$NON-NLS-1$
					}
				}
			}
		});

		this._jcbWSDLPort.setEnabled(bEnabled);
	}

	private void genWSDLOperationCombo(String asWSDLPort) {

		boolean bEnabled = false;
		String[] saOperations = new String[0];

		if (asWSDLPort != null) {
			try {
				saOperations = getWSDLPortOperations(asWSDLPort);
				bEnabled = true;
			} catch (Exception e) {
				adapter.log("GEN WSDL OPERATION COMBO EXCEPTION: ", e); //$NON-NLS-1$
			}
		}

		if (this._jcbWSDLOperation == null) {
			this._jcbWSDLOperation = new JComboBox(saOperations);
		} else {
			this._jcbWSDLOperation.removeAllItems();

			for (int i = 0; i < saOperations.length; i++) {
				this._jcbWSDLOperation.addItem(saOperations[i]);
			}
		}

		this._jcbWSDLOperation
				.addItemListener(new java.awt.event.ItemListener() {
					public void itemStateChanged(ItemEvent evt) {
						if (evt.getStateChange() == ItemEvent.SELECTED) {
							String sOper = (String) evt.getItem();

							try {
								boolean enable = !sOper.equals(_ESCOLHA);
								enableMapping(enable);
							} catch (Exception e) {
								adapter
										.log(
												"UNABLE TO CHANGE OPERATION STATE: ", e); //$NON-NLS-1$
							}
						}
					}
				});

		this._jcbWSDLOperation.setEnabled(bEnabled);
	}

	public void setDataIn(String title, List<Atributo> atributos) {
		if (null == _rep) {
			String wMsg = adapter.getString("Block.offline.msg");
			String wTitle = adapter.getString("Block.offline.title");
			JOptionPane.showMessageDialog(getParent(), wMsg, wTitle,
					JOptionPane.ERROR_MESSAGE);
			exitStatus = EXIT_STATUS_CANCEL;
			dispose();
			return;
		}

		setTitle(adapter.getString("AlteraAtributosWebServiceSinc.title")); //$NON-NLS-1$

		inputData = new String[0][4];
		outputData = new String[0][4];

		this.hmInMapping = new HashMap<String, String>();
		this.hmOutMapping = new HashMap<String, String>();

		Atributo atr = null;
		String sName = null;
		String sPrefix = null;
		String sVal = null;
		HashMap<String, String> hmPropsData = new HashMap<String, String>();
		HashMap<String, String> hm = null;

		for (int i = 0; i < atributos.size(); i++) {

			atr = (Atributo) atributos.get(i);

			if (atr == null)
				continue;

			sName = atr.getNome();
			sVal = atr.getValor();
			if (sVal == null)
				sVal = ""; //$NON-NLS-1$
			sPrefix = sName.substring(0, 1);
			sName = sName.substring(1);

			if (sPrefix.equals(_sPROPS_PREFIX)) {
				hm = hmPropsData;
			} else if (sPrefix.equals(_sINPUT_PREFIX)) {
				hm = this.hmInMapping;
			} else if (sPrefix.equals(_sOUTPUT_PREFIX)) {
				hm = this.hmOutMapping;
			} else {
				continue;
			}

			hm.put(sName, sVal);
		}

		// PROPS
		propsData = new String[_PROPS_SIZE][3];

		propsData[_nWSDL_IDX][0] = _sWSDL;
		propsData[_nWSDL_IDX][1] = (String) hmPropsData.get(_sWSDL);
		if (propsData[_nWSDL_IDX][1] == null
				|| propsData[_nWSDL_IDX][1].equals(""))propsData[_nWSDL_IDX][1] = null; //$NON-NLS-1$
		propsData[_nSERVICE_IDX][0] = _sSERVICE;
		propsData[_nSERVICE_IDX][1] = (String) hmPropsData.get(_sSERVICE);
		if (propsData[_nSERVICE_IDX][1] == null
				|| propsData[_nSERVICE_IDX][1].equals(""))propsData[_nSERVICE_IDX][1] = _ESCOLHA; //$NON-NLS-1$
		propsData[_nPORT_IDX][0] = _sPORT;
		propsData[_nPORT_IDX][1] = (String) hmPropsData.get(_sPORT);
		if (propsData[_nPORT_IDX][1] == null
				|| propsData[_nPORT_IDX][1].equals(""))propsData[_nPORT_IDX][1] = _ESCOLHA; //$NON-NLS-1$
		propsData[_nOPERATION_IDX][0] = _sOPERATION;
		propsData[_nOPERATION_IDX][1] = (String) hmPropsData.get(_sOPERATION);
		if (propsData[_nOPERATION_IDX][1] == null
				|| propsData[_nOPERATION_IDX][1].equals(""))propsData[_nOPERATION_IDX][1] = _ESCOLHA; //$NON-NLS-1$
		propsData[_nTIMEOUT_IDX][0] = _sTIMEOUT;
		propsData[_nTIMEOUT_IDX][1] = (String) hmPropsData.get(_sTIMEOUT);
		if (propsData[_nTIMEOUT_IDX][1] == null
				|| propsData[_nTIMEOUT_IDX][1].equals(""))propsData[_nTIMEOUT_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$
		propsData[_nRETRIES_IDX][0] = _sRETRIES;
		propsData[_nRETRIES_IDX][1] = (String) hmPropsData.get(_sRETRIES);
		if (propsData[_nRETRIES_IDX][1] == null
				|| propsData[_nRETRIES_IDX][1].equals(""))propsData[_nRETRIES_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$

		// AUTHENTICATION
		propsData[_nAUTHENT_IDX][0] = _sAUTHENT;
	    propsData[_nAUTHENT_IDX][1] = (String)hmPropsData.get(_sAUTHENT);
		if (propsData[_nAUTHENT_IDX][1] == null	|| propsData[_nAUTHENT_IDX][1].equals(""))propsData[_nAUTHENT_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$

		propsData[_nUSERLOGIN_IDX][0] = _sUSERLOGIN;
	    propsData[_nUSERLOGIN_IDX][1] = (String)hmPropsData.get(_sUSERLOGIN);
		if (propsData[_nUSERLOGIN_IDX][1] == null	|| propsData[_nUSERLOGIN_IDX][1].equals(""))propsData[_nUSERLOGIN_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$

		propsData[_nUSERAUTH_IDX][0] = _sUSERAUTH;
	    propsData[_nUSERAUTH_IDX][1] = (String)hmPropsData.get(_sUSERAUTH);
		if (propsData[_nUSERAUTH_IDX][1] == null	|| propsData[_nUSERAUTH_IDX][1].equals(""))propsData[_nUSERAUTH_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$

		propsData[_nPASSAUTH_IDX][0] = _sPASSAUTH;
	    propsData[_nPASSAUTH_IDX][1] = (String)hmPropsData.get(_sPASSAUTH);
		if (propsData[_nPASSAUTH_IDX][1] == null	|| propsData[_nPASSAUTH_IDX][1].equals(""))propsData[_nPASSAUTH_IDX][1] = ""; //$NON-NLS-1$ //$NON-NLS-2$

		
		/* criar botões e arranjar dialogo */
		try {
			jbInit();
			pack();

			this._bInitialized = true;
		} catch (Exception ex) {
			adapter.log("error", ex);
		}

		this.setSize(600, 500);
		setVisible(true);

	}

	// AUTHENTICATION
	private void addListenerAuth() {
		this._jcbAuth.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					_jcbUserLogin.setVisible(true);
					_jtfUser.setVisible(true);
					_jtfPass.setVisible(true);
					_jLabelUserLogin.setVisible(true);
					_jLabelUser.setVisible(true);
					_jLabelPass.setVisible(true);
				} else {
					_jcbUserLogin.setVisible(false);
					_jtfUser.setVisible(false);
					_jtfPass.setVisible(false);
					_jLabelPass.setVisible(false);
					_jLabelUserLogin.setVisible(false);
					_jLabelUser.setVisible(false);
					_jLabelPass.setVisible(false);
				}
			}
		});
	}

	private void addListenerUserLogin() {
		this._jcbUserLogin.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					_jtfUser.setEnabled(false);
					_jtfPass.setEnabled(false);
				} else {
					_jtfUser.setEnabled(true);
					_jtfPass.setEnabled(true);
				}
			}
		});
	}

	/**
	 * Iniciar caixa de diálogo
	 * 
	 * @throws Exception
	 */
	void jbInit() throws Exception {
		// configurar
		panel1.setLayout(borderLayout1);

		this.setSize(600, 500);

		/* resize */
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentResized(java.awt.event.ComponentEvent evt) {
				dialogComponentResized(evt);
			}
		});

		/* botão OK */
		jButton1.setText(OK);

		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton1_actionPerformed(e);
			}
		});

		/* botão cancelar */
		jButton2.setText(Cancelar);
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jButton2_actionPerformed(e);
			}
		});

		/* paineis */
		JPanel aux1 = new JPanel();
		aux1.setSize(100, 1);
		getContentPane().add(aux1, BorderLayout.WEST);
		JPanel aux2 = new JPanel();
		aux2.setSize(100, 1);
		getContentPane().add(aux2, BorderLayout.EAST);

		// MAIN CONTENT

		GridBagLayout sGridbag = new GridBagLayout();
		GridBagConstraints sC = new GridBagConstraints();
		sC.fill = GridBagConstraints.HORIZONTAL;
		JPanel jPanel = new JPanel();
		jPanel.setLayout(sGridbag);

		// separator
		JPanel aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// WSDL
		genWSDLCombo();
		JLabel jLabel = null;
		String stmp = "WSDL"; //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jcbWSDL);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(this._jcbWSDL, sC);
		jPanel.add(this._jcbWSDL);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// WSDL URL and IMPORT BUTTON
		aux = new JPanel();
		aux.setSize(40, 1);
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		this._jtfWSDL = new JTextField(30);
		stmp = "URL"; //$NON-NLS-1$
		this._jlWSDL = new JLabel(stmp);
		this._jlWSDL.setLabelFor(this._jtfWSDL);
		sGridbag.setConstraints(this._jlWSDL, sC);
		jPanel.add(this._jlWSDL);
		aux = new JPanel();
		aux.setSize(30, 1);
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 2;
		sGridbag.setConstraints(this._jtfWSDL, sC);
		jPanel.add(this._jtfWSDL);
		ImageIcon ic = new ImageIcon(adapter.getJanela().createImage(
				"right.gif", false)); //$NON-NLS-1$
		this._jbWSDL = new JButton(ic);
		this._jbWSDL.setToolTipText(adapter
				.getString("AlteraAtributosWebServiceSinc.tooltip.importWSDL")); //$NON-NLS-1$
		this._jbWSDL.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jbWSDL_actionPerformed(e);
			}
		});
		aux = new JPanel();
		aux.setSize(60, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(this._jbWSDL, sC);
		jPanel.add(this._jbWSDL);
		sC.gridwidth = 1;
		// at startup, disable new WSDL area
		enableNewWSDL(false);

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// WSDL SERVICE
		genWSDLServiceCombo(null);
		jLabel = null;
		stmp = adapter.getString("AlteraAtributosWebServiceSinc.label.service"); //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jcbWSDLService);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(this._jcbWSDLService, sC);
		jPanel.add(this._jcbWSDLService);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// WSDL PORT
		genWSDLPortCombo(null);
		jLabel = null;
		stmp = adapter.getString("AlteraAtributosWebServiceSinc.label.port"); //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jcbWSDLPort);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(this._jcbWSDLPort, sC);
		jPanel.add(this._jcbWSDLPort);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// WSDL OPERATION
		genWSDLOperationCombo(null);
		jLabel = null;
		stmp = adapter
				.getString("AlteraAtributosWebServiceSinc.label.operation"); //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jcbWSDLOperation);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(this._jcbWSDLOperation, sC);
		jPanel.add(this._jcbWSDLOperation);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// TIMEOUT
		this._jtfTimeout = new JTextField(10);
		jLabel = null;
		stmp = adapter.getString("AlteraAtributosWebServiceSinc.label.timeout"); //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jtfTimeout);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jtfTimeout, sC);
		jPanel.add(this._jtfTimeout);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// RETRIES
		this._jtfRetries = new JTextField(10);
		jLabel = null;
		stmp = adapter.getString("AlteraAtributosWebServiceSinc.label.tries"); //$NON-NLS-1$
		jLabel = new JLabel(stmp);
		jLabel.setLabelFor(this._jtfRetries);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jtfRetries, sC);
		jPanel.add(this._jtfRetries);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// AUTHENTICATION USE CHECKBOX
		this._jcbAuth = new JCheckBox();
		jLabel = null;
		jLabel = new JLabel(adapter.getString("AlteraAtributosWebServiceSinc.UseAuthent"));
		jLabel.setLabelFor(this._jcbAuth);
		sC.gridwidth = 2;
		sGridbag.setConstraints(jLabel, sC);
		jPanel.add(jLabel);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jcbAuth, sC);
		jPanel.add(this._jcbAuth);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;
		addListenerAuth();

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// AUTHENTICATION LOGIN USER
		this._jcbUserLogin = new JCheckBox();
		this._jcbUserLogin.setVisible(false);
		_jLabelUserLogin = new JLabel(adapter.getString("AlteraAtributosWebServiceSinc.UserLogin"));
		_jLabelUserLogin.setLabelFor(this._jcbUserLogin);
		sC.gridwidth = 2;
		sGridbag.setConstraints(_jLabelUserLogin, sC);
		jPanel.add(_jLabelUserLogin);
		_jLabelUserLogin.setVisible(false);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jcbUserLogin, sC);
		jPanel.add(this._jcbUserLogin);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;
		addListenerUserLogin();

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// AUTHENTICATION USER
		this._jtfUser = new JTextField(10);
		this._jtfUser.setVisible(false);
		_jLabelUser = new JLabel(adapter.getString("AlteraAtributosWebServiceSinc.UserAuth"));
		_jLabelUser.setLabelFor(this._jtfUser);
		sC.gridwidth = 2;
		sGridbag.setConstraints(_jLabelUser, sC);
		jPanel.add(_jLabelUser);
		_jLabelUser.setVisible(false);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jtfUser, sC);
		jPanel.add(this._jtfUser);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// AUTHENTICATION PASS
		this._jtfPass = new JPasswordField(10);
		this._jtfPass.setVisible(false);
		_jLabelPass = new JLabel(adapter.getString("AlteraAtributosWebServiceSinc.PassAuth"));
		_jLabelPass.setLabelFor(this._jtfPass);
		sC.gridwidth = 2;
		sGridbag.setConstraints(_jLabelPass, sC);
		jPanel.add(_jLabelPass);
		_jLabelPass.setVisible(false);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = 1;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sGridbag.setConstraints(this._jtfPass, sC);
		jPanel.add(this._jtfPass);
		aux = new JPanel();
		aux.setSize(30, 1);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		// separator
		aux = new JPanel();
		aux.setSize(1, 20);
		sC.gridwidth = GridBagConstraints.REMAINDER;
		sGridbag.setConstraints(aux, sC);
		jPanel.add(aux);
		sC.gridwidth = 1;

		jScrollPane1.getViewport().add(jPanel, BorderLayout.NORTH);

		// MAIN CONTENT END

		// INPUT CONTENT
		jtInput = new MyJTableX(inputData, _saInputColumnNames);
		genDataTable(true);
		jScrollPane2.getViewport().add(jtInput);

		// OUTPUT CONTENT
		jtOutput = new MyJTableX(outputData, _saOutputColumnNames);
		genDataTable(false);
		jScrollPane3.getViewport().add(jtOutput);

		// WINDOW
		getContentPane().add(jScrollPane1);
		getContentPane().add(jScrollPane2);
		getContentPane().add(jScrollPane3);

		jtp
				.addTab(
						adapter
								.getString("AlteraAtributosWebServiceSinc.label.properties"), jScrollPane1); //$NON-NLS-1$
		jtp
				.addTab(
						adapter
								.getString("AlteraAtributosWebServiceSinc.label.inputMap"), jScrollPane2); //$NON-NLS-1$
		jtp
				.addTab(
						adapter
								.getString("AlteraAtributosWebServiceSinc.label.outputMap"), jScrollPane3); //$NON-NLS-1$

		getContentPane().add(jtp, BorderLayout.CENTER);

		jtp.setEnabledAt(1, false);
		jtp.setEnabledAt(2, false);

		this.getContentPane().add(jPanel2, BorderLayout.SOUTH);
		jPanel2.add(jButton1, null);
		jPanel2.add(jButton2, null);
		this.getContentPane().add(jPanel3, BorderLayout.NORTH);

		if (propsData[_nWSDL_IDX][1] == null) {
			if (_saWSDL.length == 1) {
				propsData[_nWSDL_IDX][1] = _NEW;
				enableNewWSDL(true);
			} else {
				propsData[_nWSDL_IDX][1] = _ESCOLHA;
				enableNewWSDL(false);
			}
			_jcbWSDLService.removeAllItems();
			_jcbWSDLService.setEnabled(false);
		}

		this._jcbWSDL.setSelectedItem(propsData[_nWSDL_IDX][1]);
		this._jcbWSDLService.setSelectedItem(propsData[_nSERVICE_IDX][1]);
		this._jcbWSDLPort.setSelectedItem(propsData[_nPORT_IDX][1]);
		this._jcbWSDLOperation.setSelectedItem(propsData[_nOPERATION_IDX][1]);

		this._jtfTimeout.setText((String) propsData[_nTIMEOUT_IDX][1]);
		this._jtfRetries.setText((String) propsData[_nRETRIES_IDX][1]);

		// AUTHENTICATION
		if (propsData[_nAUTHENT_IDX][1].equals("true"))
			this._jcbAuth.setSelected(true);
		if (propsData[_nUSERLOGIN_IDX][1].equals("true"))
			this._jcbUserLogin.setSelected(true);
		if(propsData[_nUSERAUTH_IDX][1] != null && propsData[_nPASSAUTH_IDX][1] != null){
			this._jtfUser.setText(propsData[_nUSERAUTH_IDX][1]);
			this._jtfPass.setText(propsData[_nPASSAUTH_IDX][1]);
		}
		
		dialogComponentResized(null);
		repaint();
	}

	/* OK */
	void jButton1_actionPerformed(ActionEvent e) {
		jtInput.stopEditing();
		jtOutput.stopEditing();

		// PROPS
		propsData = new String[_PROPS_SIZE][3];

		propsData[_nWSDL_IDX][0] = _sWSDL;
		propsData[_nWSDL_IDX][1] = (String) _jcbWSDL.getSelectedItem();
		propsData[_nWSDL_IDX][2] = ""; //$NON-NLS-1$

		propsData[_nSERVICE_IDX][0] = _sSERVICE;
		propsData[_nSERVICE_IDX][1] = (String) _jcbWSDLService
				.getSelectedItem();
		propsData[_nSERVICE_IDX][2] = ""; //$NON-NLS-1$

		propsData[_nPORT_IDX][0] = _sPORT;
		propsData[_nPORT_IDX][1] = (String) _jcbWSDLPort.getSelectedItem();
		propsData[_nPORT_IDX][2] = ""; //$NON-NLS-1$

		propsData[_nOPERATION_IDX][0] = _sOPERATION;
		propsData[_nOPERATION_IDX][1] = (String) _jcbWSDLOperation
				.getSelectedItem();
		propsData[_nOPERATION_IDX][2] = ""; //$NON-NLS-1$

		propsData[_nTIMEOUT_IDX][0] = _sTIMEOUT;
		propsData[_nTIMEOUT_IDX][1] = (String) _jtfTimeout.getText();
		propsData[_nTIMEOUT_IDX][2] = ""; //$NON-NLS-1$

		propsData[_nRETRIES_IDX][0] = _sRETRIES;
		propsData[_nRETRIES_IDX][1] = (String) _jtfRetries.getText();
		propsData[_nRETRIES_IDX][2] = ""; //$NON-NLS-1$

		// AUTHENTICATION
		propsData[_nAUTHENT_IDX][0] = _sAUTHENT;
		propsData[_nAUTHENT_IDX][1] = "" + _jcbAuth.isSelected();
		propsData[_nAUTHENT_IDX][2] = "";

		propsData[_nUSERLOGIN_IDX][0] = _sUSERLOGIN;
		propsData[_nUSERLOGIN_IDX][1] = "" + _jcbUserLogin.isSelected();
		propsData[_nUSERLOGIN_IDX][2] = "";
		
		propsData[_nUSERAUTH_IDX][0] = _sUSERAUTH;
		propsData[_nUSERAUTH_IDX][1] = "" + _jtfUser.getText();
		propsData[_nUSERAUTH_IDX][2] = "";
		
		propsData[_nPASSAUTH_IDX][0] = _sPASSAUTH;
		propsData[_nPASSAUTH_IDX][1] = "" + _jtfPass.getText();
		propsData[_nPASSAUTH_IDX][2] = "";
		
		exitStatus = EXIT_STATUS_OK;
		dispose();
	}

	/* Cancelar */
	void jButton2_actionPerformed(ActionEvent e) {
		exitStatus = EXIT_STATUS_CANCEL;
		dispose();
	}

	/* WSDL Combo */
	void enableNewWSDL(boolean abStatus) {
		Color cf = Color.black;

		if (!abStatus) {
			cf = Color.gray;
		}

		this._jlWSDL.setForeground(cf);
		this._jtfWSDL.setEnabled(abStatus);
		this._jbWSDL.setEnabled(abStatus);
	}

	/* WSDL Import */
	void jbWSDL_actionPerformed(ActionEvent e) {

		if (_rep == null) {
			adapter
					.showError(adapter
							.getString("AlteraAtributosWebServiceSinc.error.noRepository")); //$NON-NLS-1$
			return;
		}

		String url = this._jtfWSDL.getText();

		// download wsdl
		byte[] wsdl = null;
		try {

			wsdl = downloadWSDL(url);

			if (wsdl == null || wsdl.length == 0) {
				throw new Exception("o wsdl obtido é null"); //$NON-NLS-1$
			}

			org.w3c.dom.Document doc = XMLUtils.newDocument(new InputSource(
					new ByteArrayInputStream(wsdl)));
			adapter.log("XML encoding: " + doc.getXmlEncoding()); //$NON-NLS-1$
			Parser wp = new Parser();
			wp.run(null, doc);
		} catch (Exception exc) {
			adapter
					.showError(adapter
							.getString("AlteraAtributosWebServiceSinc.error.getWSDL") + exc.getMessage()); //$NON-NLS-1$
			return;
		}

		String parsedURL = url;
		try {
			uploadWSDL(url, wsdl);
			parsedURL = parseURL(url)[0];
		} catch (Exception exc) {
			adapter
					.showError(adapter
							.getString("AlteraAtributosWebServiceSinc.error.sendWSDL") + exc.getMessage()); //$NON-NLS-1$
			return;
		}

		this.genWSDLCombo();

		String[] item = parsedURL.split("/");
		this._jcbWSDL.setSelectedItem(item[item.length-1]);

	}

	private void uploadWSDL(String asURL, byte[] asWSDL) throws Exception {

		String[] parsedURL = parseURL(asURL);

		// upload to repository
		_rep.setWebService(parsedURL[0], asWSDL); // get the encoding?
		_rep.setWebService(parsedURL[1], asURL.getBytes("UTF-8")); //$NON-NLS-1$
	}

	// returns:
	// index 0: wsdl file full path relative to repository wsdl dir
	// index 1: url text file full path relative to repository wsdl dir
	private String[] parseURL(String asURL) throws Exception {
		String[] retObj = new String[2];

		int idx = asURL.indexOf("http://"); //$NON-NLS-1$
		String url = asURL;

		if (idx > -1) {
			idx += "http://".length(); //$NON-NLS-1$
			url = asURL.substring(idx) + 1;
		}

		StringTokenizer st = new StringTokenizer(url, "/"); //$NON-NLS-1$

		String sHost = null;
		String sWSDL = null;

		while (st.hasMoreTokens()) {
			if (sHost == null) {
				sHost = (String) st.nextElement();
			} else {
				sWSDL = (String) st.nextElement();
			}
		}

		if (sWSDL == null)
			throw new Exception(
					adapter
							.getString("AlteraAtributosWebServiceSinc.error.noWsdlName")); //$NON-NLS-1$

		sWSDL = sWSDL.split("[\\?=\\.]")[0]; //$NON-NLS-1$

		retObj[0] = sHost + "/" + sWSDL + _WSDL_REP_FILE; //$NON-NLS-1$
		retObj[1] = sHost + "/" + sWSDL + _URL_REP_FILE; //$NON-NLS-1$

		return retObj;
	}

	private String getWSDLURL(String asWSDL) {
		String retObj = null;
		byte[] data = new byte[0];
		if (_rep != null) {

			String url = asWSDL.substring(0, asWSDL.lastIndexOf(".")); //$NON-NLS-1$
			url = url + _URL_REP_FILE;

			data = _rep.getWebService(url);
		}

		if (data == null) {
			retObj = ""; //$NON-NLS-1$
		} else {
			try {
				retObj = new String(data, "UTF-8"); //$NON-NLS-1$
			} catch (UnsupportedEncodingException e) {
				adapter.log("error", e);
			}
		}

		return retObj;
	}

	private String[] getWSDLServices(String asWSDL) throws Exception {
		String[] retObj = null;

		this._hmServices = new HashMap<String, Service>();

		if (_rep != null) {

			byte[] swsdlcontent = _rep.getWebService(asWSDL);

			_wu = new WSDLUtils(new ByteArrayInputStream(swsdlcontent));

			Service[] serva = _wu.getServices();

			ArrayList<String> alServices = new ArrayList<String>();
			for (int i = 0; i < serva.length; i++) {
				Service qServ = serva[i];
				alServices.add(qServ.getQName().getLocalPart());
				_hmServices.put(qServ.getQName().getLocalPart(), qServ);
			}

			if (alServices.size() > 1) {
				alServices.add(0, _ESCOLHA);
			}

			retObj = new String[alServices.size()];
			for (int i = 0; i < alServices.size(); i++) {
				retObj[i] = (String) alServices.get(i);
			}
		}

		return retObj;
	}

	private String[] getWSDLServicePorts(String asWSDLService) throws Exception {
		String[] retObj = null;

		this._hmPorts = new HashMap<String, Port>();

		if (asWSDLService != null && _wu != null && _hmServices != null
				&& _hmServices.containsKey(asWSDLService)) {

			Service sServ = (Service) this._hmServices.get(asWSDLService);

			Port[] porta = _wu.getServicePorts(sServ);

			ArrayList<String> alPorts = new ArrayList<String>();
			for (int i = 0; i < porta.length; i++) {
				Port port = porta[i];
				alPorts.add(port.getName());

				_hmPorts.put(port.getName(), port);
			}

			if (alPorts.size() > 1) {
				alPorts.add(0, _ESCOLHA);
			}

			retObj = new String[alPorts.size()];
			for (int i = 0; i < alPorts.size(); i++) {
				retObj[i] = (String) alPorts.get(i);
			}
		}

		return retObj;
	}

	private String[] getWSDLPortOperations(String asWSDLPort) throws Exception {
		String[] retObj = null;

		this._hmOperations = new HashMap<String, Operation>();

		if (asWSDLPort != null && _hmPorts != null
				&& _hmPorts.containsKey(asWSDLPort)) {

			Port pPort = (Port) this._hmPorts.get(asWSDLPort);

			Operation[] opera = _wu.getPortOperations(pPort);

			ArrayList<String> alOpers = new ArrayList<String>();
			for (int i = 0; i < opera.length; i++) {
				Operation oper = opera[i];
				alOpers.add(oper.getName());

				_hmOperations.put(oper.getName(), oper);
			}

			if (alOpers.size() > 1) {
				alOpers.add(0, _ESCOLHA);
			}

			retObj = new String[alOpers.size()];
			for (int i = 0; i < alOpers.size(); i++) {
				retObj[i] = (String) alOpers.get(i);
			}

		}

		return retObj;
	}

	private void enableMapping(boolean ab) {
		// generate input/output arrays
		// enable input/output mappings

		if (!ab) {
			jtp.setEnabledAt(_nIN_PANE, false);
			jtp.setEnabledAt(_nOUT_PANE, false);

			return;
		}

		if (this._bInitialized) {
			// IF ALREADY INITIALIZED,
			// set defined mapping in mapping maps from table inputs
			for (int i = 0; i < inputData.length; i++) {
				hmInMapping.put(inputData[i][1], inputData[i][0]);
			}
			for (int i = 0; i < outputData.length; i++) {
				hmOutMapping.put(outputData[i][0], outputData[i][2]);
			}
		}

		String sOperation = (String) this._jcbWSDLOperation.getSelectedItem();

		// Operation operation = (Operation)_hmOperations.get(sOperation);

		String sPort = (String) this._jcbWSDLPort.getSelectedItem();
		Port port = (Port) _hmPorts.get(sPort);

		WSDLField[] faInputs = _wu.getOperationFields(port, sOperation, true);
		ArrayList<WSDLField> alInputs = this.getFields(faInputs);
		inputData = new String[alInputs.size()][4];
		for (int i = 0; i < alInputs.size(); i++) {
			WSDLField wf = alInputs.get(i);

			String sFieldName = genFieldName(wf);

			if (hmInMapping.containsKey(sFieldName)) {
				inputData[i][0] = (String) hmInMapping.get(sFieldName);
			} else {
				inputData[i][0] = ""; //$NON-NLS-1$
			}
			inputData[i][1] = sFieldName;
			inputData[i][2] = wf.getType().getQName().getLocalPart()
					+ wf.getDimensions();

			inputData[i][3] = String.valueOf(wf.isLeaf());
			if (!wf.isLeaf()) {
				inputData[i][0] = ""; //$NON-NLS-1$
			}
		}
		genDataTable(true);

		WSDLField[] faOutputs = _wu.getOperationFields(port, sOperation, false);
		ArrayList<WSDLField> alOutputs = this.getFields(faOutputs);
		outputData = new String[alOutputs.size()][4];
		for (int i = 0; i < alOutputs.size(); i++) {
			WSDLField wf = alOutputs.get(i);

			String sFieldName = genFieldName(wf);

			outputData[i][0] = sFieldName;
			outputData[i][1] = wf.getType().getQName().getLocalPart()
					+ wf.getDimensions();
			if (hmOutMapping.containsKey(sFieldName)) {
				outputData[i][2] = (String) hmOutMapping.get(sFieldName);
			} else {
				outputData[i][2] = ""; //$NON-NLS-1$
			}

			outputData[i][3] = String.valueOf(wf.isLeaf());
			if (!wf.isLeaf()) {
				outputData[i][2] = ""; //$NON-NLS-1$
			}
		}
		genDataTable(false);

		jtp.setComponentAt(_nIN_PANE, jScrollPane2);
		jtp.setComponentAt(_nOUT_PANE, jScrollPane3);

		jtp.setEnabledAt(_nIN_PANE, true);
		jtp.setEnabledAt(_nOUT_PANE, true);

	}

	private static String genFieldName(WSDLField awf) {
		return awf.getFullName();
	}

	private ArrayList<WSDLField> getFields(WSDLField[] awfaFields) {
		ArrayList<WSDLField> altmp = new ArrayList<WSDLField>();
		for (int i = 0; awfaFields != null && i < awfaFields.length; i++) {
			altmp.add(awfaFields[i]);
		}
		return getFields(altmp);
	}

	@SuppressWarnings("unchecked")//$NON-NLS-1$
	private ArrayList<WSDLField> getFields(ArrayList<WSDLField> aalFields) {
		ArrayList<WSDLField> retObj = new ArrayList<WSDLField>();

		for (int i = 0; aalFields != null && i < aalFields.size(); i++) {
			WSDLField wf = (WSDLField) aalFields.get(i);

			retObj.add(wf);
			if (!wf.isLeaf()) {
				retObj.addAll(this.getFields(wf.getChilds()));
			}
		}

		return retObj;
	}

	public void dialogComponentResized(java.awt.event.ComponentEvent evt) {
	}

	private byte[] downloadWSDL(String aURL) throws Exception {

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		InputStream is = null;
		URLConnection c = null;

		try {
			String surl = aURL;
			if (surl == null)
				throw new Exception(
						adapter
								.getString("AlteraAtributosWebServiceSinc.error.invalidURL")); //$NON-NLS-1$

			if (!surl.startsWith("http://")) { //$NON-NLS-1$
				surl = "http://" + surl; //$NON-NLS-1$
			}

			URL url = new URL(surl);
			c = url.openConnection();
			c.connect();
			is = c.getInputStream();

			// Read till the connection is closed.
			byte[] b = new byte[8092];
			int len = 0;
			while ((len = is.read(b)) != -1) {
				bout.write(b, 0, len);
			}
		} catch (Exception e) {
			adapter.log("downloadWSDL EXCEPTION:", e); //$NON-NLS-1$
			Exception eRet = new Exception(
					adapter
							.getString("AlteraAtributosWebServiceSinc.error.invalidURL")); //$NON-NLS-1$
			throw eRet;
		} finally {
			is.close();
			// c.close();
		}

		return bout.toByteArray();
	}

	private void genDataTable(boolean abInput) {

		MyJTableX jt = null;

		final Object[][] data;
		final String[] columns;
		final boolean bInput = abInput;
		JScrollPane jsp;

		if (bInput) {
			jt = jtInput;
			data = inputData;
			columns = _saInputColumnNames;
			jsp = jScrollPane2;
		} else {
			jt = jtOutput;
			data = outputData;
			columns = _saOutputColumnNames;
			jsp = jScrollPane3;
		}

		jt = new MyJTableX(data, columns);

		WSTableModel model = new WSTableModel(columns, data, bInput ? 0 : 2);

		jt.setModel(model);

		jt.setRowSelectionAllowed(false);
		jt.setColumnSelectionAllowed(false);

		MyColumnEditorModel rm0 = new MyColumnEditorModel();

		for (int i = 0; i < data.length; i++) {
			String sToFill = null;
			if (bInput) {
				sToFill = (String) data[i][0];
			} else {
				sToFill = (String) data[i][2];
			}
			JTextField jtf = new JTextField(sToFill);
			DefaultCellEditor dce = new DefaultCellEditor(jtf);
			dce.setClickCountToStart(2);
			jtf.setSelectionColor(Color.red);
			jtf.setSelectedTextColor(Color.white);
			rm0.addEditorForCell(i, bInput ? 0 : 2, dce);
		}

		jt.setMyColumnEditorModel(rm0);

		jsp.getViewport().add(jt, null);

		if (bInput) {
			jtInput = jt;
		} else {
			jtOutput = jt;
		}

	}

	// O modelo de edicao das colunas eh demasiado complexo para o MyTableModel.
	// Esta classe vai contornar o problema.
	private static class WSTableModel extends MyTableModel {
		private static final long serialVersionUID = 324324234235345L;

		private int tcol;

		public WSTableModel(String[] cols, Object[][] data, int tcol) {
			super(cols, data);
			this.tcol = tcol;
		}

		public boolean isCellEditable(int row, int col) {
			if ("false".equals(getData()[row][3]))return false; // if not leave, not editable //$NON-NLS-1$

			return (col == tcol);
		}
	}

}
