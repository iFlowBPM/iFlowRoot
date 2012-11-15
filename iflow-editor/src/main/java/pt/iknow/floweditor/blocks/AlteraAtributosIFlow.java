package pt.iknow.floweditor.blocks;

/**
 * <p>Title: </p>
 * <p>Description: Diálogo para editar e criar o código bean shell</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: iKnow </p>
 * @author iKnow
 * @version 1.0
 */

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import pt.iknow.floweditor.Atributo;
import pt.iknow.floweditor.FlowEditor;
import pt.iknow.floweditor.FlowEditorAdapter;
import pt.iknow.floweditor.messages.Messages;

public class AlteraAtributosIFlow extends AbstractAlteraAtributos implements AlteraAtributosInterface, Runnable {
  private static final long serialVersionUID = 7332941294794991911L;

  /* OK */
  private static String OK=Messages.getString("Common.ok"); //$NON-NLS-1$

  private int exitStatus = EXIT_STATUS_CANCEL;
  private String title = null;
  private boolean disposed = false;
  private Thread modalThread = null;
  
  public AlteraAtributosIFlow(FlowEditorAdapter janela) {
    super(janela);
  }

  /**
   * getExitStatus
   * @return
   */
  public int getExitStatus() {
    return exitStatus;
  }

  /**
   * getNewAttributes
   * @return
   */
  public String[][] getNewAttributes() {
    return new String[0][2];
  }

  /**
   * setDataIn
   * @param title
   * @param atributos
   */
  public void setDataIn(String title, List<Atributo> atributos) {
    this.modalThread = Thread.currentThread();
    this.title = title;
    FlowEditor.getRootDisplay().asyncExec(this);
    adapter.log("Waiting SWT to finish....");
    while(!disposed) {
      try {
        Thread.sleep(250);
      } catch (InterruptedException e) {
      }
    }
  }

  public void run() {
    disposed = false;
    final Shell shell = new Shell(FlowEditor.getRootShell(), SWT.APPLICATION_MODAL|SWT.CLOSE|SWT.TITLE|SWT.MAX|SWT.RESIZE);

    if(null != title) shell.setText(title);

    shell.addShellListener(new ShellListener(){

      public void shellActivated(ShellEvent e) {
      }

      public void shellClosed(ShellEvent e) {
        System.out.println("shell closed");
        closeWindow(shell);
      }

      public void shellDeactivated(ShellEvent e) {
      }

      public void shellDeiconified(ShellEvent e) {
      }

      public void shellIconified(ShellEvent e) {
      }

    });

    final Browser browser = new Browser(shell, SWT.BORDER);

    shell.setLayout(new GridLayout(1, true));
    browser.setLayoutData(new GridData(GridData.FILL_BOTH));

    browser.addProgressListener(new ProgressListener() {
      public void changed(ProgressEvent event) {
      }

      public void completed(ProgressEvent event) {
        browser.removeProgressListener(this);
        String cookie = adapter.getRepository().getCookie();
        System.out.println(cookie);
        browser.execute("document.cookie='"+cookie+"';window.location='main.jsp';");
      }
    });

    // browser.setUrl(FlowEditor.getActionURL()+service.getContext());
    browser.setUrl(adapter.getRepository().getBaseURL()+"/login.jsp");
    // browser.setText("<html><head><title>redirecting...</title><script type=\"text/javascript\">function delayer(){window.location='http://localhost:8080/iFlow/main.jsp';}</script></head><body onload=\"document.cookie='"+cookie+"';setTimeout('delayer()', 5000)\"><p>redirecting...</p></body></html>");


    Button okButton = new Button(shell, SWT.PUSH|SWT.CENTER);
    okButton.setText(OK);
    okButton.addSelectionListener(new SelectionListener() {
      public void widgetSelected(SelectionEvent arg0) {
        System.out.println("ok selected");
        closeWindow(shell);
      }

      public void widgetDefaultSelected(SelectionEvent arg0) {
      }
    });

    shell.setSize(1024,768);
    shell.open();

  }

  private void closeWindow(Shell shell) {
    shell.dispose();
    disposed = true;
    if(null != this.modalThread)
      this.modalThread.interrupt();
  }

}
