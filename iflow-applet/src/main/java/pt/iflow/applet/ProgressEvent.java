package pt.iflow.applet;

public class ProgressEvent {
  private final int progress;
  private final String note;
  
  public ProgressEvent(int progress, String note) {
    this.progress = progress;
    this.note = note;
  }

  public int getProgress() {
    return progress;
  }

  public String getNote() {
    return note;
  }
  
}
