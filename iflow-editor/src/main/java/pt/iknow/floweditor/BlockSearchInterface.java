package pt.iknow.floweditor;

public interface BlockSearchInterface {

  public abstract InstanciaComponente[] search(String text);
  public abstract void gotoBlock(InstanciaComponente ic);
}
