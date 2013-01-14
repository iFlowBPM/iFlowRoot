package pt.iknow.floweditor;

import javax.swing.JFrame;

import pt.iflow.api.msg.IMessages;
import pt.iknow.iflow.RepositoryClient;

public interface FlowEditorAdapter {

  public JFrame getParentFrame();
  
  public IDesenho getDesenho();
  
  public IJanela getJanela();
  
  public void log(String msg);

  public void log(String msg, Throwable t);
  
  public RepositoryClient getRepository();
  
  public IMessages getBlockMessages();
  
  public String getString(String key, Object ... objects);
  
  public void asyncExec(Runnable runnable);
  
  public boolean isRepOn();
  
  public Object getRootShell();
  
  public void showError(String msg);
  
  public Atributo newAtributo(String nome, String valor, String descricao);
  
  public String getBlockKey();
}
