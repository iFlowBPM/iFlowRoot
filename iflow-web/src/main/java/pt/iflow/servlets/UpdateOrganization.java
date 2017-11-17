/**
 * PublicFiles.java
 *
 * Description:
 *
 * History: 01/15/02 - jpms - created.
 * $Id: PublicFiles.java 248 2007-08-01 13:54:31 +0000 (Qua, 01 Ago 2007) uid=mach,ou=Users,dc=iknow,dc=pt $
 */

package pt.iflow.servlets;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import net.sf.image4j.codec.bmp.BMPDecoder;
import net.sf.image4j.codec.ico.ICODecoder;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.UserManager;
import pt.iflow.api.presentation.OrganizationTheme;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.utils.html.FormData;
import pt.iknow.utils.html.FormFile;
import pt.iknow.utils.html.FormUtils;

/**
 * 
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright (c) 2005 iKnow
 * </p>
 * 
 * @author iKnow
 * 
 * @web.servlet name="UpdateOrg"
 * 
 * @web.servlet-mapping url-pattern="/UpdateOrg"
 */
public class UpdateOrganization extends HttpServlet {

  // colocar isto noutro lugar
  private static int MAX_WIDTH = 130;
  private static int MAX_HEIGHT = 55;

  /**
   * 
   */
  private static final long serialVersionUID = -9101755201777404343L;

  public UpdateOrganization() {
  }

  // actions: change logo, change theme, change organization name
  private void changeLogo(UserInfoInterface userInfo, FormFile file) {
    if (null == file)
      return; // logo se ve

    byte[] data = file.getData();

    String name = file.getFileName().toLowerCase(Locale.ENGLISH);

    if (null == data || data.length == 0)
      return;
    try {

      Image imagem = null;
      if (name.endsWith(".bmp")) {
        imagem = BMPDecoder.read(new ByteArrayInputStream(data));
      } else if (name.endsWith(".ico")) {
        List<BufferedImage> imgList = ICODecoder.read(new ByteArrayInputStream(data));
        if (null != imgList && imgList.size() > 0)
          imagem = imgList.get(0);
      } else {
        // Read image
        ImageIcon imageIcon = new ImageIcon(data);
        imagem = imageIcon.getImage();
      }

      if (null == imagem)
        return;

      // manipulate image
      double w = imagem.getWidth(null);
      double h = imagem.getHeight(null);
      if (w == -1 || h == -1)
        return; // ignore...

      // ensure that the image is not bigger than 130x55
      if (w > MAX_WIDTH || h > MAX_HEIGHT) {
        // scale image.... If any of the coords is -1, then image will be scaled
        // with keep ratio
        int nw = -1;
        int nh = -1;
        if (w > MAX_WIDTH) {
          nw = MAX_WIDTH;
        } else {
          nh = MAX_HEIGHT;
        }

        imagem = new ImageIcon(imagem.getScaledInstance(nw, nh, Image.SCALE_SMOOTH)).getImage();
        w = imagem.getWidth(null);
        h = imagem.getHeight(null);
      }

      int width = MAX_WIDTH;
      int height = MAX_HEIGHT;
      BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      // A imagem gerada estara centrada no resultado final.
      Graphics2D g = (Graphics2D) bi.getGraphics();
      int posx = (int) (width / 2 - w / 2);
      int posy = (int) (height / 2 - h / 2);
      g.drawImage(imagem, posx, posy, null);

      // Write image (get bytes)
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      ImageIO.write(bi, "png", bout);
      data = bout.toByteArray();

    } catch (Exception e) {
      return;
    }

    if (null != data && data.length > 0) {
      Repository rep = BeanFactory.getRepBean();
      rep.setLogo(userInfo, data);
      rep.getLogo(userInfo); // force reload of image to prevent cache
    }
  }

  private void changeTheme(UserInfoInterface userInfo, String newTheme, String menuLocation, String menuStyle, boolean procMenuVisible) {
    if (null == newTheme || newTheme.length() == 0)
      return;

    OrganizationTheme mng = BeanFactory.getOrganizationThemeBean();
    mng.updateOrganizationData(userInfo, newTheme, null, null, menuLocation, menuStyle, procMenuVisible);
  }

  private void changeOrgName(UserInfoInterface userInfo, String newName) {
    if (null == newName || newName.length() == 0)
      return;
    UserManager mng = BeanFactory.getUserManagerBean();
    mng.modifyOrganization(userInfo, userInfo.getCompanyID(), userInfo.getCompanyName(), newName);
  }

  private boolean changeLocale(UserInfoInterface userInfo, String newLang, String timezone) {
    if (StringUtils.isEmpty(newLang))
      return false;
  
    boolean reload = false;
    Locale loc = BeanFactory.getSettingsBean().getOrganizationLocale(userInfo);
    TimeZone tz = BeanFactory.getSettingsBean().getOrganizationTimeZone(userInfo);
    String oldLang = loc.getLanguage() + "_" + loc.getCountry();
    if (!StringUtils.equals(oldLang, newLang) || !StringUtils.equals(tz.getID(), timezone)) {
      String[] parts = newLang.split("_");
      BeanFactory.getSettingsBean().updateOrganizationSettings(userInfo, parts[0], parts[1], timezone);
      userInfo.reloadUserSettings();
      reload = true;
    }
    return reload;
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    UserInfoInterface userInfo = (UserInfoInterface) request.getSession().getAttribute(Const.USER_INFO);
    if (null == userInfo || (!userInfo.isOrgAdmin() && !userInfo.isSysAdmin())) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Operation not permitted");
      return;
    }
    
    FormFile file = null;
    String newName = null;
    String newTheme = null;
    String organizationLang = null;
    String organizationTimeZone = null;
    String menuLocation = null;
    String menuStyle = null;
    boolean procMenuVisible = true;
    
    try {
      FormData formData = FormUtils.parseRequest(request, Const.nUPLOAD_THRESHOLD_SIZE, Const.nUPLOAD_MAX_SIZE, Const.fUPLOAD_TEMP_DIR);
      file = formData.getFileParameter("logo");
      newName = formData.getParameter("companyName");
      newTheme = formData.getParameter("style");
      organizationLang = formData.getParameter("organization_lang");
      organizationTimeZone = formData.getParameter("organization_timezone");
      menuLocation = formData.getParameter("menuLocation");
      menuStyle = formData.getParameter("menuStyle");
      procMenuVisible = StringUtils.equals(String.valueOf(true), formData.getParameter("procMenuVisible"));
      
    } catch (Exception e) {
      e.printStackTrace();
      Logger.error(userInfo.getUtilizador(), this, "service", "Error parsing request");
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
      return;
    }
    
    changeLogo(userInfo, file);
    changeTheme(userInfo, newTheme, menuLocation, menuStyle, procMenuVisible);
    changeOrgName(userInfo, newName);
    boolean reload = changeLocale(userInfo, organizationLang, organizationTimeZone);
    reload = false;
    // changeOrgName(userInfo, newName);
    PrintWriter w = response.getWriter();
    if (reload)
      w.println("reload");
    else
      w.println("Done!");
  }

}
