/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
