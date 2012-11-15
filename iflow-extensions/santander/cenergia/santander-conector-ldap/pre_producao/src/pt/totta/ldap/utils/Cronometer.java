package pt.totta.ldap.utils;

public class Cronometer
{
  private long startTime;
  private long stopTime;

  public void startCronometer()
  {
    startTime = System.currentTimeMillis();
  }

  public void stopCronometer()
  {
    stopTime = System.currentTimeMillis();
  }

  public void resetCronometer()
  {
    startTime = 0;
    stopTime = 0;
  }

  public long calculateElapsedTime(long timeScale)
  {
    long elapsedTime = 0;
    
    elapsedTime = stopTime - startTime;
    
    if(elapsedTime < 0)
      return 0;
    else
      return elapsedTime/timeScale;
  }
}