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
