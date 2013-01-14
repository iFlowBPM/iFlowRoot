package pt.iflow.applet;

// XXX Integrar com SwingTask e remover esta classe
public class TaskStatus {

  public static final int WORKING = 0;
  public static final int COMPLETE = 1;
  public static final int ERROR = 2;

  private final String taskId;
  private int status;
  private String result;

  public TaskStatus(String taskId) {
    this.taskId = taskId;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public String getTaskId() {
    return taskId;
  }

  public int getStatus() {
    return status;
  }

  public String getResult() {
    return result;
  }

  
  public String toString() {
    return getJSON();
  }

  public String getJSON() {
    String stat = "working"; //$NON-NLS-1$
    switch (getStatus()) {
    case COMPLETE:
      stat = "complete"; //$NON-NLS-1$
      break;
    case ERROR:
      stat = "error"; //$NON-NLS-1$
      break;
    case WORKING:
    default:
      stat = "working"; //$NON-NLS-1$
      break;
    }
    return "{\"status\":\"" + stat + "\",\"result\":" + getResult() + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
  
  public static String getInvalidStatus() {
    return "{\"status\":\"invalid\"}"; //$NON-NLS-1$
  }
}
