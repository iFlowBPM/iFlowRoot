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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IOUtils {
  private static Log log = LogFactory.getLog(IOUtils.class);

  public static byte[] getResourceAsBytes(String resource) {
    return getResourceAsBytes(resource, null);
  }

  public static byte[] getResourceAsBytes(String resource, ClassLoader cl) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    InputStream in = null;
    try {
      if (null == cl)
        copyStreams(in = IOUtils.class.getResourceAsStream(resource), out);
      else
        copyStreams(in = cl.getResourceAsStream(resource), out);
    } catch (Exception e) {
      log.error("Error reading resource " + resource, e); //$NON-NLS-1$
      return null;
    } finally {
      if (null != in) {
        try {
          in.close();
        } catch (IOException e) {
        }
      }
    }
    return out.toByteArray();
  }

  public static void copyStreams(InputStream in, OutputStream out) throws IOException {
    byte[] b = new byte[8192];
    int r;
    while ((r = in.read(b)) >= 0)
      out.write(b, 0, r);
  }
}
