package pt.iflow.applet;

/**
 * Utilitários de sistema operativo.
 * <br>
 * Referência: <a href="http://lopica.sourceforge.net/os.html">http://lopica.sourceforge.net/os.html</a>
 * @author ombl
 *
 */
public class OSUtils {

  private static enum OSType {
    UNSUPPORTED,
    WINDOWS,
    LINUX,
    MAC_OS_X
  };

  private static final OSType OS;

  static {
    String os = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
    if(os.indexOf("win") >= 0) //$NON-NLS-1$
      OS = OSType.WINDOWS;
    else if(os.indexOf("mac") >= 0) //$NON-NLS-1$
      OS = OSType.MAC_OS_X;
    else if(os.indexOf("linux") >= 0) //$NON-NLS-1$
      OS = OSType.LINUX;
    else
      OS = OSType.UNSUPPORTED;
  }


  public static boolean isWindows() {
    return OS.equals(OSType.WINDOWS);
  }

  public static boolean isLinux() {
    return OS.equals(OSType.LINUX);
  }

  public static boolean isMacOSX() {
    return OS.equals(OSType.MAC_OS_X);
  }

  public static boolean isUnsupported() {
    return OS.equals(OSType.UNSUPPORTED);
  }

}
