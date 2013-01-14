package pt.iknow.iflow;

public interface RepositoryStatusListener {
  public abstract void start(long start, long total);
  public abstract void done(long done);
  public abstract void finish();
}
