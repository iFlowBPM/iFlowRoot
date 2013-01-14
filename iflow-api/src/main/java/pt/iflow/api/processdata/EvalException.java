package pt.iflow.api.processdata;

/**
 * Excepção lançada quando uma avaliação, parse ou query falha
 * 
 * 
 * @author ombl
 *
 */
public class EvalException extends Exception {

  private static final long serialVersionUID = -6269060258143255291L;

  public EvalException() {
    super();
  }

  public EvalException(String message, Throwable cause) {
    super(message, cause);
  }

  public EvalException(String message) {
    super(message);
  }

  public EvalException(Throwable cause) {
    super(cause);
  }

}
