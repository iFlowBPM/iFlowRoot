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
package pt.iflow.api.repository;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.UserInfoInterface;

public class RepositoryURIResolver implements URIResolver {

  private URIResolver parent;
  private UserInfoInterface userInfo;

  public RepositoryURIResolver(UserInfoInterface userInfo) {
    this(userInfo, null);
  }

  public RepositoryURIResolver(UserInfoInterface userInfo, URIResolver parent) {
    this.parent = parent;
    this.userInfo = userInfo;
  }

  /**
   * Called by the processor through {@link FOUserAgent} when it encounters an
   * uri in an external-graphic element. (see also
   * {@link javax.xml.transform.URIResolver#resolve(String, String)} This
   * resolver will allow URLs without a scheme, i.e. it assumes 'repos:' as the
   * default scheme. If the base URL is null a 'repos:' URL referencing the
   * template directory is used as the base URL. If the method is successful it
   * will return a Source of type
   * {@link javax.xml.transform.stream.StreamSource} with its SystemID set to
   * the resolved URL used to open the underlying InputStream.
   * 
   * @param href
   *          An href attribute, which may be relative or absolute.
   * @param base
   *          The base URI against which the first argument will be made
   *          absolute if the absolute URI is required.
   * @return A {@link javax.xml.transform.Source} object, or null if the href
   *         cannot be resolved.
   * @throws javax.xml.transform.TransformerException
   *           Never thrown by this implementation.
   * @see javax.xml.transform.URIResolver#resolve(String, String)
   */
  public Source resolve(String href, String base) throws TransformerException {
    Source source = resolveRepository(href, base);
    if (null == source && parent != null)
      source = parent.resolve(href, base);
    return source;
  }

  protected Source resolveRepository(String href, String base) throws javax.xml.transform.TransformerException {
    Repository repos = BeanFactory.getRepBean();

    RepositoryFile file = null;
    file = repos.getPrintTemplate(userInfo, href);
    
    if (null == file || !file.exists()) {
      file = repos.getPrintTemplate(userInfo, base + "/" + href);
    }

    if (null == file || !file.exists()) {
      file = repos.getWebFile(userInfo, href);
    }
    
    if (null == file || !file.exists()) {
      file = repos.getWebFile(userInfo, base + "/" + href);
    }
    
    if (null != file && file.exists()) {
      return new StreamSource(file.getResourceAsStream(), href);
    }

    return null;
  }

}
