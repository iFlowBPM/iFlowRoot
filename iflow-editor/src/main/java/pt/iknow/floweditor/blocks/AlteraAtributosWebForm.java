package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar o código bean shell</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mortbay.util.IO;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMEvent;
import org.mozilla.interfaces.nsIDOMEventListener;
import org.mozilla.interfaces.nsIDOMEventTarget;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsISupports;

import pt.iflow.api.blocks.form.Field;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.blocks.form.Tab;
import pt.iflow.api.processtype.DataTypeEnum;
import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.AtributoImpl;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.IDesenho;
import pt.iknow.floweditor.MVCWebServer;
import pt.iknow.floweditor.WebFileType;
import pt.iknow.floweditor.WebFormAction;
import pt.iknow.floweditor.WebFormActionHandler;
import pt.iknow.floweditor.WebFormHandler;
import pt.iknow.floweditor.messages.Messages;
import pt.iknow.floweditor.mozilla.MozillaBrowser;
import pt.iknow.utils.StringUtilities;
import pt.iknow.utils.VelocityUtils;

import com.twolattes.json.Marshaller;

@WebFormActionHandler(ContextPath = "/blockWebForm/")
public class AlteraAtributosWebForm extends AbstractAlteraAtributos implements AlteraAtributosInterface, Runnable {
	private static final long serialVersionUID = 7332941294794991911L;

	private static final int CHUNK_SIZE = 1024;
	private static final String PROPERTY_NAME_PREFIX = "FORM_";

	// events - Keep in sync with JavaScript!!
	private static final String BASE_FORM = "webForm";
	private static final String JS_SAVE_EVT = "scripts/save.js";
	private static final String JS_INIT_EVT = "scripts/init.js";
	private static final String JS_LOAD_EVT = "scripts/load.js";
	private static final String JS_DEBUG_FORM_EDITOR_EVT = "scripts/debug-formeditor.js";
	private static final String JS_OK_EVT = "scripts/ok.js";
	private static final String JS_CANCEL_EVT = "scripts/cancel.js";
	private static final String JS_CATALOG_EVT = "scripts/catalog.js";
	private static final String JS_ADD_CATALOG_EVT = "scripts/addCatalogVar.js";
	private static final String JS_LOAD_MESSAGES = "scripts/loadMessages.js";
	private static final String JS_PREVIEW = "scripts/preview.js";
	private static final String JS_OPEN_FF = "scripts/openFirefox.js";
	private static final String JS_OPEN_OP = "scripts/openOpera.js";
	private static final String JS_OPEN_IE = "scripts/openIExplore.js";
	private static final String HTML_ADD_VAR = "addvar.html";
	private static final String JS_DO_PROMOTE = "scripts/doPromote.js";
	private static final String JS_LIST_TEMPLATES = "scripts/list_templates.json";

	private Shell sShell = null;
	private MozillaBrowser browser = null;
	// private Button buttonOk = null;
	// private Button buttonCancel = null;
	// private Composite composite = null;

	private int exitStatus = EXIT_STATUS_CANCEL;
	private String title = null;
	private WebFormHandler service;
	private IDesenho desenho;
	private String form;
	private Marshaller<Form> formMarshaller = Marshaller.create(Form.class);

	public AlteraAtributosWebForm(FlowEditorAdapter adapter) {
		super(adapter, true);
		this.desenho = adapter.getDesenho();
		setResizable(false);
		adapter.log("Good constructor");
	}

	/**
	 * getExitStatus
	 * 
	 * @return
	 */
	public int getExitStatus() {
		return exitStatus;
	}

	/**
	 * getNewAttributes
	 * 
	 * @return
	 */
	public String[][] getNewAttributes() {
		if (form == null)
			return null;

		// split string into chunks. The max size is about 1024 per attribute
		int mod = form.length() % CHUNK_SIZE == 0 ? 0 : 1;
		int div = form.length() / CHUNK_SIZE;
		int size = div + mod;
		String[][] newAttributes = new String[size][2];
		for (int i = 0; i < div; i++) {
			newAttributes[i][0] = PROPERTY_NAME_PREFIX + i;
			newAttributes[i][1] = form.substring(i * CHUNK_SIZE, i * CHUNK_SIZE + CHUNK_SIZE);
		}
		if (mod != 0) { // last part
			newAttributes[div][0] = PROPERTY_NAME_PREFIX + div;
			newAttributes[div][1] = form.substring(div * CHUNK_SIZE);
		}

		return newAttributes;
	}

	private String extractDataFromAttr(List<Atributo> atributos) {
		Set<FormChunk> newData = new TreeSet<FormChunk>();
		if (atributos != null && !atributos.isEmpty()) {
			for (Atributo a : atributos)
				newData.add(new FormChunk(a.getNome(), a.getValor()));
		}
		return StringUtils.join(newData, null);
	}

	/**
	 * setDataIn
	 * 
	 * @param title
	 * @param atributos
	 */
	public void setDataIn(String title, List<Atributo> atributos) {
		this.title = title;
		setTitle(title);

		String data = extractDataFromAttr(atributos);
		if (StringUtilities.isNotEmpty(data)) {
			try {
				this.form = new JSONObject(data).toString();
			} catch (JSONException e) {
				adapter.log("Error unmarshalling existing data", e);
			}
		}
		System.out.println("The loaded form is: \"" + this.form + "\"");
		adapter.log("Waiting SWT to finish....");
		getContentPane().setLayout(new BorderLayout());
		JLabel lbl = new JLabel();
		lbl.setIcon(new ImageIcon(adapter.getJanela().createImage("warning.png", false)));
		lbl.setText("A aguardar que o browser termine...");
		lbl.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		getContentPane().add(lbl, BorderLayout.CENTER);
		pack();
		addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				if (JOptionPane.showConfirmDialog(AlteraAtributosWebForm.this, "Quer terminar o bloco?", "Terminar",
						JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
					return;
				adapter.asyncExec(new Runnable() {
					public void run() {
						closeWindow(EXIT_STATUS_CANCEL);
					}
				});
			}

			public void windowDeactivated(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
				adapter.asyncExec(AlteraAtributosWebForm.this);
			}

		});
		setVisible(true);
	}

	/**
	 * This method initializes sShell
	 */
	private void createSShell() {
		sShell = new Shell((Shell) adapter.getRootShell(),
				SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE);
		sShell.setText(title);
		sShell.setSize(1024, 768);
		sShell.setLayout(new FillLayout());
		browser = new MozillaBrowser(sShell, SWT.BORDER);
		browser.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent evt) {
				if (evt.keyCode == SWT.F1) {
					evt.doit = false;
					browser.fireEvent("helpWindowLink", "click");
					return;
				}
				if (evt.keyCode == SWT.F5) {
					evt.doit = false;
					browser.refresh();
					return;
				}
				if (evt.keyCode == SWT.F6) {
					evt.doit = false;
					browser.openFirefox();
					return;
				}
				if (evt.keyCode == SWT.F7) {
					evt.doit = false;
					browser.openOpera();
					return;
				}
				if (evt.keyCode == SWT.F8) {
					evt.doit = false;
					browser.openInternetExplorer();
					return;
				}
				if (evt.keyCode == SWT.F9) {
					evt.doit = false;
					browser.fireEvent("previewFormLink", "click");
					return;
				}
				if (evt.keyCode == 's' && evt.stateMask == SWT.CTRL) {
					evt.doit = false;
					browser.fireEvent("saveFormLink", "click");
					return;
				}
				if (evt.keyCode == 'w' && evt.stateMask == SWT.CTRL) {
					evt.doit = false;
					browser.fireEvent("closeEditorLink", "click");
					return;
				}
				if (evt.keyCode == SWT.ESC || (evt.keyCode == 'q' && evt.stateMask == SWT.CTRL)) {
					evt.doit = false;
					browser.fireEvent("cancelEditorLink", "click");
					return;
				}
			}

			public void keyReleased(KeyEvent arg0) {
			}
		});

		browser.addProgressListener(new ProgressListener() {
			public void changed(ProgressEvent event) {
			}

			public void completed(ProgressEvent event) {
				injectDebugger();
			}

		});

		createComposite();
	}

	/**
	 * This method initializes composite
	 * 
	 */
	private void createComposite() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.CENTER;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = false;
		gridData3.horizontalIndent = 2;
		gridData3.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.horizontalAlignment = GridData.FILL;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.horizontalIndent = 0;
		gridData1.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = true;
		/*
		 * composite = new Composite(sShell, SWT.NONE);
		 * composite.setLayout(gridLayout1); composite.setLayoutData(gridData3);
		 * buttonOk = new Button(composite, SWT.NONE); buttonOk.setText(OK);
		 * buttonOk.setLayoutData(gridData1); buttonCancel = new
		 * Button(composite, SWT.NONE); buttonCancel.setText(CANCEL);
		 * buttonCancel.setLayoutData(gridData2);
		 */
	}

	public void run() {
		createSShell();
		service = MVCWebServer.getInstance().createWebFormHandler(this);
		// setup events
		sShell.addShellListener(new ShellListener() {

			public void shellActivated(ShellEvent e) {
				browser.forceFocus();
			}

			public void shellClosed(ShellEvent e) {
				closeWindow(EXIT_STATUS_CANCEL);
			}

			public void shellDeactivated(ShellEvent e) {
			}

			public void shellDeiconified(ShellEvent e) {
				browser.forceFocus();
			}

			public void shellIconified(ShellEvent e) {
			}

		});

		String sURL = service.getURL(BASE_FORM, null);
		adapter.log("Request URL: " + sURL);

		/*
		 * browser.addCloseWindowListener(new CloseWindowListener() { public
		 * void close(org.eclipse.swt.browser.WindowEvent event) { adapter.log(
		 * "Close requested"); closeWindow(exitStatus); } });
		 */
		browser.setUrl(sURL);

		// open window
		sShell.setActive();
		sShell.setFocus();
		sShell.open();
	}

	private void closeWindow(int exitStatus) {
		this.exitStatus = exitStatus;
		sShell.dispose();

		MVCWebServer.getInstance().releaseWebFormHandler(service);
		service = null;
		setVisible(false);
		dispose();
	}

	// register web events
	@WebFormAction(EventName = BASE_FORM, ActionType = WebFileType.HTML)
	public void executeBaseForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + BASE_FORM);
		InputStream in = MVCWebServer.class.getResourceAsStream("/web/blockWebForm/form.html");
		OutputStream out = response.getOutputStream();
		try {
			IO.copy(in, out);
		} finally {
			out.close();
			in.close();
		}
	}

	@WebFormAction(EventName = JS_CATALOG_EVT, ActionType = WebFileType.JAVASCRIPT)
	public void executeGetCatalog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_CATALOG_EVT);
		PrintWriter pw = response.getWriter();
		adapter.log("Injecting catalog");
		pw.println("/* Auto generated by FlowEditor */");
		pw.println("/* 1. Catalog */");
		Collection<Atributo> catalog = desenho.getCatalogue();
		for (Atributo variable : catalog) {
			StringBuilder catBuilder = new StringBuilder("new CatalogWidget({");
			String name = variable.getNome();
			String desc = StringUtilities.isEmpty(variable.getPublicName()) ? name : variable.getPublicName();

			DataTypeEnum dataTypeEnum = DataTypeEnum.getDataType(variable.getDataType());
			String type = dataTypeEnum.name();
			boolean list = dataTypeEnum.isList();
			boolean single = dataTypeEnum.isSingle();

			catBuilder.append("name:'").append(StringEscapeUtils.escapeJavaScript(name)).append("',");
			catBuilder.append("desc:'").append(StringEscapeUtils.escapeJavaScript(desc)).append("',");
			catBuilder.append("type:'").append(StringEscapeUtils.escapeJavaScript(type)).append("',");
			catBuilder.append("single:").append(single).append(",");
			catBuilder.append("list:").append(list).append("");
			catBuilder.append("});");
			adapter.log(catBuilder.toString());
			pw.println(catBuilder);
		}

		// Inject stylesheets
		adapter.log("Injecting stylesheets");
		pw.println("/* 2. StyleSheets */");
		String[] styleSheets = adapter.getRepository().listStyleSheets();
		for (int i = 0; i < styleSheets.length; i++) {
			StringBuilder sb = new StringBuilder();
			String str = StringEscapeUtils.escapeJavaScript(styleSheets[i]);
			sb.append("DragNSort.FormStyleSheets.push({description:'").append(str).append("',value:'").append(str)
					.append("'});");
			adapter.log(sb.toString());
			pw.println(sb);
		}

		styleSheets = adapter.getRepository().listPrintTemplates();
		for (int i = 0; i < styleSheets.length; i++) {
			StringBuilder sb = new StringBuilder();
			String str = StringEscapeUtils.escapeJavaScript(styleSheets[i]);
			sb.append("DragNSort.PrintStyleSheets.push({description:'").append(str).append("',value:'").append(str)
					.append("'});");
			adapter.log(sb.toString());
			pw.println(sb);
		}

		pw.close();
	}

	// Intercept dnd.js and append init debug stuff
	@WebFormAction(EventName = JS_DEBUG_FORM_EDITOR_EVT, ActionType = WebFileType.JAVASCRIPT)
	public void executeGetDND(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing debug form editor.js");

		PrintWriter pw = response.getWriter();
		pw.println("/* Auto generated by FlowEditor */");
		// append debug initialization
		pw.println("window.addEvent('domready', FormEditor.initDebugPage);");
		pw.close();
	}

	@WebFormAction(EventName = JS_LOAD_EVT, ActionType = WebFileType.JSON)
	public void executeLoad(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_LOAD_EVT);
		PrintWriter pw = response.getWriter();
		pw.println("{\"ok\":false,\"error\":\"not implemented\"}");
		pw.close();
	}

	@WebFormAction(EventName = JS_OK_EVT, ActionType = WebFileType.JSON)
	public void executeOkButton(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_OK_EVT);

		executeSave(request, response);

		adapter.asyncExec(new Runnable() {
			public void run() {
				closeWindow(EXIT_STATUS_OK);
			}
		});

	}

	@WebFormAction(EventName = JS_SAVE_EVT, ActionType = WebFileType.JSON)
	public void executeSave(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_SAVE_EVT);
		String sValues = request.getParameter("values");

		System.out.println(sValues);

		boolean ok = false;
		try {
			JSONObject obj = new JSONObject(sValues);
			System.out.println(obj);
			formMarshaller.unmarshall(obj); // unmarshall to check...
			ok = true;
			this.form = obj.toString();
		} catch (Throwable t) {
			adapter.log("Error unmarshalling Form string", t);
		}

		PrintWriter pw = response.getWriter();
		pw.println("{\"ok\":" + ok + "}");
		pw.close();
	}

	@WebFormAction(EventName = JS_INIT_EVT, ActionType = WebFileType.JSON)
	public void executeInit(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_INIT_EVT);
		PrintWriter pw = response.getWriter();
		pw.println("{\"ok\":true,\"form\":" + this.form + "}");
		pw.close();
	}

	@WebFormAction(EventName = JS_CANCEL_EVT, ActionType = WebFileType.JSON)
	public void executeCancel(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_CANCEL_EVT);
		PrintWriter pw = response.getWriter();
		pw.println("{\"ok\":true}");
		pw.close();

		adapter.asyncExec(new Runnable() {
			public void run() {
				closeWindow(EXIT_STATUS_CANCEL);
			}
		});
	}

	@WebFormAction(EventName = JS_ADD_CATALOG_EVT, ActionType = WebFileType.JSON)
	public void executeAddCatalog(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_ADD_CATALOG_EVT);

		String sValues = request.getParameter("values");

		System.out.println(sValues);

		String result = "";
		try {
			JSONObject obj = new JSONObject(sValues);
			Atributo variable = new AtributoImpl();
			String name = obj.getString("name");
			String desc = obj.getString("description");
			String type = obj.getString("type");
			String defaultValue = obj.getString("defaultValue");
			String searchable = obj.getString("searchable");
			String format = obj.getString("defaultFormat");
			variable.setNome(name);
			variable.setPublicName(desc);
			variable.setSearchable(StringUtils.equalsIgnoreCase("on", searchable));
			variable.setInitValue(defaultValue);
			variable.setFormat(format);

			DataTypeEnum dataTypeEnum = DataTypeEnum.getDataType(type);
			String dataType = dataTypeEnum.name();

			adapter.log("Type  => ");

			variable.setDataType(dataType);

			if (this.desenho.addCatalogVariable(variable, true)) {
				obj.put("ok", true);
				obj.put("list", dataTypeEnum.isList());
				obj.put("single", dataTypeEnum.isSingle());
				result = obj.toString();
			} else {
				result = "{\"ok\":false,\"error\":\"Variable exists\"}";
			}
		} catch (Throwable t) {
			adapter.log("Error unmarshalling Catalog string", t);
			result = "{\"ok\":false,\"error\":\"Could not parse request\"}";
		}

		PrintWriter pw = response.getWriter();
		
		
		escapeHtml(result);
		pw.println(result);
		pw.close();
	}

	@WebFormAction(EventName = JS_OPEN_FF, ActionType = WebFileType.JSON)
	public void openFirefox(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing openFirefox");
		browser.openFirefox();
		String success = "{\"ok\":true}";
		PrintWriter pw = response.getWriter();
		pw.println(success);
		pw.close();
	}

	@WebFormAction(EventName = JS_OPEN_OP, ActionType = WebFileType.JSON)
	public void openOpera(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing openOpera");
		browser.openOpera();
		String success = "{\"ok\":true}";
		PrintWriter pw = response.getWriter();
		pw.println(success);
		pw.close();
	}

	@WebFormAction(EventName = JS_OPEN_IE, ActionType = WebFileType.JSON)
	public void openIExplore(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing openIExplore");
		browser.openInternetExplorer();
		String success = "{\"ok\":true}";
		PrintWriter pw = response.getWriter();
		pw.println(success);
		pw.close();
	}

	@WebFormAction(EventName = JS_LOAD_MESSAGES, ActionType = WebFileType.JAVASCRIPT)
	public void executeLoadMessages(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing ");
		final String prefix = "AlteraAtributosWebForm.html.";
		final int len = prefix.length();

		PrintWriter pw = response.getWriter();
		pw.println("/* Auto generated by FlowEditor */");
		pw.println("if(!window['FormEditorMessages']) window.FormEditorMessages={};");
		pw.println("window.FormEditorMessages.messages={};");

		Enumeration<String> keys = Messages.getSubKeys(prefix);
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String msg = Messages.getString(key);
			pw.println("FormEditorMessages.messages['" + StringEscapeUtils.escapeJavaScript(key.substring(len)) + "']='"
					+ StringEscapeUtils.escapeJavaScript(msg) + "';");
		}
		pw.println("FormEditorMessages.updateMessages = function updateMessages(elemFrom) {");
		pw.println("  var children;");
		pw.println("  if(elemFrom) children = $(elemFrom).getElements('.i18n0');");
		pw.println("  else children = $$('.i18n0');");
		pw.println("  $$('.i18n').each(function updateElemMessage(e){");
		pw.println("    if($type(e.get('id'))=='string' && FormEditorMessages.messages[e.id])");
		pw.println("      e.set('text',FormEditorMessages.messages[e.id]);");
		pw.println("  });");
		pw.println("};");
		pw.println("window.addEvent('domready',FormEditorMessages.updateMessages);");

		pw.close();
	}

	private String getCatalogJSON() {
		// GET iFlow URL
		// GET Catalog representation
		Collection<Atributo> catalog = desenho.getCatalogue();
		JSONArray catalogJSON = new JSONArray();
		for (Atributo variable : catalog) {
			JSONObject var = new JSONObject();
			String name = variable.getNome();
			String desc = StringUtilities.isEmpty(variable.getPublicName()) ? name : variable.getPublicName();
			String type = variable.getDataType();
			String value = variable.getInitValue();
			try {
				var.put("name", name);
				var.put("desc", desc);
				var.put("type", type);
				var.put("value", value);
				//adapter.log(var.toString());
				catalogJSON.put(var);
			} catch (JSONException e) {
				adapter.log("Error creating JSON representation of variable ", e);
			}
		}

		return catalogJSON.toString();
	}

	private String generateHtmlPost(String url, String form, String catalog) {
		if (null == form || null == catalog)
			return null;
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("form", StringEscapeUtils.escapeHtml(form));
		ht.put("catalog", StringEscapeUtils.escapeHtml(catalog));
		ht.put("url", url);
		InputStream in = MVCWebServer.class.getResourceAsStream("/web/blockWebForm/poster.vm");
		return VelocityUtils.processTemplate(ht, new InputStreamReader(in));
	}

	private String generateHtmlOffline() {
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht.put("oflinemsg", "O editor está offline.");
		InputStream in = MVCWebServer.class.getResourceAsStream("/web/blockWebForm/offline.vm");
		return VelocityUtils.processTemplate(ht, new InputStreamReader(in));
	}

	@WebFormAction(EventName = JS_PREVIEW, ActionType = WebFileType.JSON)
	public void executePreview(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_PREVIEW);
		final String sValues = request.getParameter("values");

		System.out.println(sValues);

		boolean ok = false;
		try {
			JSONObject obj = new JSONObject(sValues);
			System.out.println(obj);
			formMarshaller.unmarshall(obj); // unmarshall to check...
			ok = true;
		} catch (Throwable t) {
			adapter.log("Error unmarshalling Form string", t);
		}

		if (ok) {
			Runnable r = new Runnable() {
				public void run() {
					Shell shell = new Shell(sShell, SWT.CLOSE | SWT.TITLE | SWT.MAX | SWT.RESIZE);
					shell.setText("Form Preview");
					shell.setLayout(new FillLayout());
					MozillaBrowser browser = new MozillaBrowser(shell);
					if (adapter.isRepOn()) {
						String iflowUrl = adapter.getRepository().getBaseURL() + "/WebForm/preview.jsp";
						browser.importCookies(adapter.getRepository().getCookies());
						// send post
						browser.setText(generateHtmlPost(iflowUrl, sValues, getCatalogJSON()));
					} else {
						browser.setText(generateHtmlOffline());
					}
					shell.setSize(1024, 768);
					shell.setActive();
					shell.setFocus();
					shell.open();
				}
			};

			adapter.asyncExec(r);
		}
		String success = "{\"ok\":" + ok + "}";
		PrintWriter pw = response.getWriter();
		pw.println(success);
		pw.close();
	}

	private static class FormChunk implements Comparable<FormChunk> {
		private int pos;
		private String chunk;

		public FormChunk(String name, String value) {
			pos = new Integer(name.substring(PROPERTY_NAME_PREFIX.length()));
			chunk = value;
		}

		public String toString() {
			return chunk;
		}

		public int compareTo(FormChunk o) {
			return pos - o.pos;
		}
	}

	static final String LOG_EVT_NAME = "feLogEvt";
	static final String LOG_TAG_NAME = "DIV";
	static final String LOG_ATTR_TYPE_NAME = "class";

	private void injectDebugger() {
		nsIDOMWindow domWindow = (nsIDOMWindow) browser.getBrowser().getContentDOMWindow();
		nsIDOMDocument doc = domWindow.getDocument();

		nsIDOMEventTarget evtTarget = (nsIDOMEventTarget) doc.queryInterface(nsIDOMEventTarget.NS_IDOMEVENTTARGET_IID);
		nsIDOMEventListener listener = new nsIDOMEventListener() {
			public nsISupports queryInterface(String arg0) {
				return arg0.equals(nsIDOMEventListener.NS_IDOMEVENTLISTENER_IID) ? this : null;
			}

			public void handleEvent(nsIDOMEvent evt) {
				evt.stopPropagation();
				nsIDOMEventTarget target = evt.getTarget();
				if (null == target)
					return;
				nsIDOMNode node = (nsIDOMNode) target.queryInterface(nsIDOMNode.NS_IDOMNODE_IID);
				String msg = node.getFirstChild().getNodeValue();
				String type = node.getAttributes().getNamedItem(LOG_ATTR_TYPE_NAME).getNodeValue();
				adapter.log("" + type + ": " + msg);
			}
		};
		evtTarget.addEventListener(LOG_EVT_NAME, listener, true);

		// prepare a javascript injection
		StringBuilder scriptInjection = new StringBuilder("if(!window['FormEditor']) window.FormEditor={};\r\n");
		scriptInjection.append("window.FormEditor.sendLogEvent=true;\r\n");
		scriptInjection.append("window.FormEditor.logEventTag='").append(LOG_TAG_NAME).append("';\r\n");
		scriptInjection.append("window.FormEditor.logEventTypeAttr='").append(LOG_ATTR_TYPE_NAME).append("';\r\n");
		scriptInjection.append("window.FormEditor.logEventName='").append(LOG_EVT_NAME).append("';\r\n");

		nsIDOMElement scriptElem = doc.createElement("SCRIPT");
		nsIDOMNode txtNode = doc.createTextNode(scriptInjection.toString());
		scriptElem.appendChild(txtNode);
		doc.getElementsByTagName("HEAD").item(0).appendChild(scriptElem);

		adapter.log("Event logging should be working now");
	}

	@WebFormAction(EventName = HTML_ADD_VAR, ActionType = WebFileType.HTML)
	public void executeAddVariableForm(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + HTML_ADD_VAR);
		PrintWriter pw = response.getWriter();

		Hashtable<String, Object> ahtSubst = new Hashtable<String, Object>();
		ahtSubst.put("dataTypes", DataTypeEnum.values());
		InputStream in = MVCWebServer.class.getResourceAsStream("/web/blockWebForm/addvar.vm");
		Reader reader = new InputStreamReader(in, "UTF-8");
		pw.print(VelocityUtils.processTemplate(ahtSubst, reader));
		reader.close();
		pw.close();
	}

	/* Vulnerabilidades Escape XSS */

	public static final HashMap m = new HashMap();
	static {
		m.put(34, "&quot;"); // < - less-than
		m.put(60, "&lt;"); // < - less-than
		m.put(62, "&gt;"); // > - greater-than

	}

	public static String escapeHtml(String result) {

		StringWriter writer = new StringWriter((int) (result.length() * 1.5));
		try {
			escape(writer, result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return writer.toString();

	}

	public static void escape(Writer writer, String str) throws IOException 
	{
	  	int len = str.length();
	  	for (int i = 0; i < len; i++) 
	  	{
	  		char c = str.charAt(i);
	  		int ascii = (int) c;
	  		String entityName = (String) m.get(ascii);
	  		if (entityName == null) 
	  		{
	  			if (c > 0x7F) 
	  			{
	  			  writer.write("&#");
	  			  writer.write(Integer.toString(c, 10));
	  			  writer.write(';');
	  			} 
	  			else 
	  			{
	  				writer.write(c); 
	  			}		
	  		}
	  		   
	  		else
	  	    {	  				 
	  		    writer.write(entityName);
  		    }}
   }


	@WebFormAction(EventName = JS_DO_PROMOTE, ActionType = WebFileType.JSON)
	public void executeDoPromote(HttpServletRequest request, HttpServletResponse response) throws Exception {
		adapter.log("Executing " + JS_DO_PROMOTE);

		String sValues = request.getParameter("values");

		System.out.println(sValues);

		String result = "{\"ok\":false}";
		JSONObject responseObj = new JSONObject();
		boolean exists = false;
		boolean invalid = false;
		try {
			JSONObject obj = new JSONObject(sValues);
			String name = obj.getString("name");
			boolean force = obj.getBoolean("force");

			if (StringUtils.isBlank(name)) {
				invalid = true;
			}

			if (desenho.getFormTemplate(name) != null)
				exists = true;
			if (force)
				exists = false;

			responseObj.put("exists", exists);
			responseObj.put("invalid", invalid);
			responseObj.put("ok", true);
			responseObj.put("name", name);
			responseObj.put("force", force);
			if (exists || invalid) {
				escapeHtml(result);
				result = responseObj.toString();
				throw new Exception("invalid template client notification exception");
			}
			JSONObject jsonForm = obj.getJSONObject("form");

			Form form = formMarshaller.unmarshall(jsonForm);

			Form formTemplate = (Form) form.duplicate();
			// TODO converter tab actual para template em vez da primeira tab.
			int selectedTab = 0;

			// remove all but first tab from formTemplate
			Tab tab = formTemplate.getTabs().get(selectedTab);
			formTemplate.getTabs().clear();
			formTemplate.getTabs().add(tab);
			formTemplate.getProperties().setTemplate("true");

			// remove all fields from first tab and insert template widget
			Map<String, String> properties = new HashMap<String, String>();
			tab = form.getTabs().get(selectedTab);
			tab.getFields().clear();
			Field templateWidget = new Field();
			templateWidget.setId("templateReconv_" + selectedTab);
			templateWidget.setType("w_template");
			properties.put("template", name);
			properties.put("disabled", "false");
			properties.put("readonly", "false");
			templateWidget.setProperties(properties);
			tab.getFields().add(templateWidget);

			this.form = formMarshaller.marshall(form).toString();
			desenho.setFormTemplate(name, formMarshaller.marshall(formTemplate).toString());
			result = responseObj.toString();
		} catch (Throwable t) {
			adapter.log("Error Converting form to template", t);
		}
		PrintWriter pw = response.getWriter();
		escapeHtml(result);
		pw.print(result);
		pw.close();
	}

	// scripts/list_templates.json
	@WebFormAction(EventName = JS_LIST_TEMPLATES, ActionType = WebFileType.JSON)
	public void executeListTemplates(HttpServletRequest request, HttpServletResponse response) throws Exception {
		JSONObject fieldDefJSON = new JSONObject();
		Map<String, String> templates = desenho.getFormTemplates();
		JSONArray valList = new JSONArray();
		JSONObject obj = new JSONObject();
		obj.put("value", "-1");
		obj.put("description", "Escolha");
		valList.put(obj);

		for (String tplname : templates.keySet()) {
			obj = new JSONObject();
			obj.put("value", tplname);
			obj.put("description", tplname);
			valList.put(obj);
		}

		fieldDefJSON.put("values", valList);
		fieldDefJSON.put("default", "-1");
		fieldDefJSON.put("type", "values");

		adapter.log(fieldDefJSON.toString());

		JSONObject jsonResp = new JSONObject();
		jsonResp.put("values", fieldDefJSON);
		jsonResp.put("ok", "true");

		PrintWriter writer = response.getWriter();
		writer.println(jsonResp);
		writer.close();
	}
}
