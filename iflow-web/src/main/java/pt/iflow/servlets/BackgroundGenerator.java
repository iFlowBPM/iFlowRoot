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
package pt.iflow.servlets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.hivemind.util.Defense;
//import org.apache.tapestry.contrib.services.impl.RoundedCornerService;

/**
 * Class responsible for bulk of java2d manipulation work when used in the {@link RoundedCornerService}. 
 */
public class BackgroundGenerator {

  public static final String TOP_LEFT = "tl";
  public static final String TOP_RIGHT = "tr";
  public static final String BOTTOM_LEFT = "bl";
  public static final String BOTTOM_RIGHT = "br";

  public static final String LEFT = "left";
  public static final String RIGHT = "right";
  public static final String TOP = "top";
  public static final String BOTTOM = "bottom";

  // css2 color spec - http://www.w3.org/TR/REC-CSS2/syndata.html#color-units
  private static final Map<String,Color> _cssSpecMap = new HashMap<String, Color>();

  static {
    _cssSpecMap.put("aqua", new Color(0,255,255));
    _cssSpecMap.put("black", Color.black);
    _cssSpecMap.put("blue", Color.blue);
    _cssSpecMap.put("fuchsia", new Color(255,0,255));
    _cssSpecMap.put("gray", Color.gray);
    _cssSpecMap.put("green", Color.green);
    _cssSpecMap.put("lime", new Color(0,255,0));
    _cssSpecMap.put("maroon", new Color(128,0,0));
    _cssSpecMap.put("navy", new Color(0,0,128));
    _cssSpecMap.put("olive", new Color(128,128,0));
    _cssSpecMap.put("purple", new Color(128,0,128));
    _cssSpecMap.put("red", Color.red);
    _cssSpecMap.put("silver", new Color(192,192,192));
    _cssSpecMap.put("teal", new Color(0,128,128));
    _cssSpecMap.put("white", Color.white);
    _cssSpecMap.put("yellow", Color.yellow);

    ImageIO.setUseCache(false);
  }

  private static Color SHADOW_COLOR = new Color(0x000000);

  private static final float DEFAULT_OPACITY = 0.5f;

  private static final float ANGLE_TOP_LEFT = 90f;
  private static final float ANGLE_TOP_RIGHT = 0f;
  private static final float ANGLE_BOTTOM_LEFT = 180f;
  private static final float ANGLE_BOTTOM_RIGHT = 270f;

  public BufferedImage buildCorner(String color, String backgroundColor, int width, int height,
      String angle, int shadowWidth, float endOpacity)
  throws Exception
  {
    width = width * 2;
    height = height * 2;
    float startAngle = getStartAngle(angle);
    Color bgColor = backgroundColor == null ? null : decodeColor(backgroundColor);

    if (shadowWidth <= 0) {

      BufferedImage arc = drawArc(color, width, height, angle, false, -1);
      BufferedImage ret = arc;

      Arc2D.Float arcArea = new Arc2D.Float(0, 0, width, height, startAngle, 90, Arc2D.PIE);
      if (bgColor != null) {

        ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D)ret.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(bgColor);
        g2.fill(arcArea.getBounds2D());

        g2.drawImage(arc, 0, 0, null);

        g2.dispose();

        ret = convertType(ret, BufferedImage.TYPE_INT_RGB);
      }

      return ret.getSubimage((int)arcArea.getBounds2D().getX(), (int)arcArea.getBounds2D().getY(),
          (int)arcArea.getBounds2D().getWidth(), (int)arcArea.getBounds2D().getHeight());
    }

    BufferedImage mask = drawArc(color, width, height, angle, true, shadowWidth);
    BufferedImage arc = drawArc(color, width, height, angle, false, shadowWidth);

    float startX = 0;
    float startY = 0;
    int shadowSize = shadowWidth * 2;
    float canvasWidth = width + (shadowSize * 2);
    float canvasHeight = height + (shadowSize * 2);

    if (startAngle == ANGLE_BOTTOM_LEFT) {

      startY -= (shadowSize * 2);

    } else if (startAngle == ANGLE_TOP_RIGHT) {

      startX -= shadowSize * 2;

    } else if (startAngle == ANGLE_BOTTOM_RIGHT) {

      startX -= shadowSize * 2;
      startY -= shadowSize * 2;
    }

    BufferedImage ret = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D)ret.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Arc2D.Float arcArea = new Arc2D.Float(startX, startY, canvasWidth, canvasHeight, startAngle, 90, Arc2D.PIE);

    if (bgColor != null) {

      g2.setColor(bgColor);
      g2.fill(arcArea.getBounds2D());
    }

    BufferedImage shadow = drawArcShadow(mask, color, backgroundColor, width, height, angle, shadowWidth, endOpacity);

    g2.setClip(arcArea);
    g2.drawImage(shadow, 0, 0, null);

    g2.setClip(null);
    g2.drawImage(arc, 0, 0, null);

    return convertType(ret, BufferedImage.TYPE_INT_RGB).getSubimage((int)arcArea.getBounds2D().getX(), (int)arcArea.getBounds2D().getY(),
        (int)arcArea.getBounds2D().getWidth(), (int)arcArea.getBounds2D().getHeight());
  }

  static BufferedImage convertType(BufferedImage image, int type) {
    BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), type);
    Graphics2D g = result.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g.drawRenderedImage(image, null);
    g.dispose();
    return result;
  }

  BufferedImage drawArc(String color, int width, int height, String angle, boolean masking, int shadowWidth)
  {
    Color arcColor = decodeColor(color);
    float startAngle = getStartAngle(angle);

    int canvasWidth = width;
    int canvasHeight = height;
    float startX = 0;
    float startY = 0;
    int shadowSize = 0;

    if (shadowWidth > 0 && !masking) {

      shadowSize = shadowWidth * 2;
      canvasWidth += shadowSize * 2;
      canvasHeight += shadowSize * 2;

      if (startAngle == ANGLE_TOP_LEFT) {

        startX += shadowSize;
        startY += shadowSize;

      } else if (startAngle == ANGLE_BOTTOM_LEFT) {

        startX += shadowSize;
        startY -= shadowSize;

      } else if (startAngle == ANGLE_TOP_RIGHT) {

        startX -= shadowSize;
        startY += shadowSize;

      } else if (startAngle == ANGLE_BOTTOM_RIGHT) {

        startX -= shadowSize;
        startY -= shadowSize;
      }
    }

    BufferedImage img = new BufferedImage( canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) img.createGraphics();

//    float extent = 90;
    if (masking) {

//      extent = 120;
      startAngle -= 20;
    }

//    Arc2D.Float fillArea = new Arc2D.Float(startX, startY, width, height, startAngle, extent, Arc2D.PIE);

    // draw arc
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    g2.setColor(arcColor);
    g2.setComposite(AlphaComposite.Src);


    Rectangle2D.Float rectArea = new Rectangle2D.Float(0,0,30, 30);                
    GradientPaint gp = new GradientPaint(0,0,new Color(0,0,0), width, height, new Color(255,255,255));

    g2.setPaint(gp);
    g2.fill(rectArea);



    //g2.fill(fillArea);

    g2.dispose();

    return img;
  }

  BufferedImage drawArcShadow(BufferedImage mask, String color, String backgroundColor, int width, int height,
      String angle, int shadowWidth, float endOpacity)
  {
    float startAngle = getStartAngle(angle);
    int shadowSize = shadowWidth * 2;
    int sampleY = 0;
    int sampleX = 0;
    int sampleWidth = width + shadowSize;
    int sampleHeight = height + shadowSize;

    if (startAngle == ANGLE_TOP_LEFT) {

    } else if (startAngle == ANGLE_BOTTOM_LEFT) {

      sampleWidth -= shadowSize;
      sampleHeight = height;

      sampleY += shadowSize;

    } else if (startAngle == ANGLE_TOP_RIGHT) {

      sampleWidth -= shadowSize;
      sampleHeight -= shadowSize;

      sampleX += shadowSize;
    } else if (startAngle == ANGLE_BOTTOM_RIGHT) {

      sampleWidth -= shadowSize;
      sampleHeight -= shadowSize;

      sampleX += shadowSize;
      sampleY += shadowSize;
    }

    ShadowRenderer shadowRenderer = new ShadowRenderer(shadowWidth, endOpacity, SHADOW_COLOR);
    BufferedImage dropShadow = shadowRenderer.createShadow(mask);

    // draw shadow arc

    BufferedImage img = new BufferedImage( (width * 4), (height * 4), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) img.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setComposite(AlphaComposite.Src);
    g2.drawImage(dropShadow, 0, 0, null);

    g2.dispose();

    return img.getSubimage(sampleX, sampleY, sampleWidth, sampleHeight);
  }

  public BufferedImage buildShadow(String color, String backgroundColor, int width, int height,
      float arcWidth, float arcHeight,
      int shadowWidth, float endOpacity)
  {
    Color fgColor = color == null ? Color.WHITE : decodeColor(color);
    Color bgColor = backgroundColor == null ? null : decodeColor(backgroundColor);

    BufferedImage mask = new BufferedImage(width, height,  BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = mask.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    RoundRectangle2D.Float fillArea = new RoundRectangle2D.Float(0, 0, width, height, arcHeight, arcWidth);
    g2.setColor(fgColor);
    g2.fill(fillArea);
    g2.dispose();

    // clip shadow

    ShadowRenderer shadowRenderer = new ShadowRenderer(shadowWidth, endOpacity, SHADOW_COLOR);
    BufferedImage dropShadow = shadowRenderer.createShadow(mask);

    BufferedImage clipImg = new BufferedImage( width + (shadowWidth * 2), height + (shadowWidth * 2), BufferedImage.TYPE_INT_ARGB);
    g2 = clipImg.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setComposite(AlphaComposite.Src);

    RoundRectangle2D.Float clip = new RoundRectangle2D.Float(0, 0, width + (shadowWidth * 2), height + (shadowWidth * 2), arcHeight, arcWidth);
    g2.setClip(clip);
    g2.drawImage(dropShadow, 0, 0, null);
    g2.dispose();

    // draw everything

    BufferedImage img = new BufferedImage( width + (shadowWidth * 2), height + (shadowWidth * 2), BufferedImage.TYPE_INT_ARGB);
    g2 = img.createGraphics();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (bgColor != null)
    {
      fillArea = new RoundRectangle2D.Float(0, 0, width + (shadowWidth * 2), height + (shadowWidth * 2), arcHeight, arcWidth);
      g2.setColor(bgColor);
      g2.fill(fillArea.getBounds2D());
    }

    g2.drawImage(clipImg, 0, 0, null);

    if (fgColor != null)
    {
      fillArea = new RoundRectangle2D.Float(0, 0, width, height, arcHeight, arcWidth);
      g2.setColor(fgColor);
      g2.fill(fillArea);
    }

    g2.dispose();

    return convertType(img, BufferedImage.TYPE_INT_RGB);
  }

  public BufferedImage buildSideShadow(String side, int size, float opacity)
  throws Exception
  {
    Defense.notNull(side, "side");

    if (opacity <= 0)
      opacity = DEFAULT_OPACITY;

    int maskWidth = 0;
    int maskHeight = 0;
    int sampleY = 0;
    int sampleX = 0;
    int sampleWidth = 0;
    int sampleHeight = 0;

    if (LEFT.equals(side)) {

      maskWidth = size * 4;
      maskHeight = size * 4;
      sampleY = maskHeight / 2;
      sampleWidth = size * 2;
      sampleHeight = 2;
    } else if (RIGHT.equals(side)) {

      maskWidth = size * 4;
      maskHeight = size * 4;
      sampleY = maskHeight / 2;
      sampleX = maskWidth;
      sampleWidth = size * 2;
      sampleHeight = 2;
    } else if (BOTTOM.equals(side)) {

      maskWidth = size * 4;
      maskHeight = size * 4;
      sampleY = maskHeight;
      sampleX = maskWidth / 2;
      sampleWidth = 2;
      sampleHeight = size * 2;
    } else if (TOP.equals(side)) {

      maskWidth = size * 4;
      maskHeight = size * 4;
      sampleY = 0;
      sampleX = maskWidth / 2;
      sampleWidth = 2;
      sampleHeight = size * 2;
    }

    BufferedImage mask = new BufferedImage( maskWidth, maskHeight, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = (Graphics2D) mask.createGraphics();

    g2.setColor(Color.white);
    g2.fillRect(0, 0, maskWidth, maskHeight);

    g2.dispose();

    ShadowRenderer shadowRenderer = new ShadowRenderer(size, opacity, SHADOW_COLOR);
    BufferedImage dropShadow = shadowRenderer.createShadow(mask);

    BufferedImage render = new BufferedImage(maskWidth * 2, maskHeight * 2, BufferedImage.TYPE_INT_ARGB);
    g2 = (Graphics2D)render.createGraphics();

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Rectangle2D.Float clip = new Rectangle2D.Float(sampleX, sampleY, sampleWidth, sampleHeight);

    g2.setColor(Color.white);
    g2.fill(clip);

    g2.drawImage(dropShadow, 0, 0, null);

    g2.dispose();

    return render.getSubimage(sampleX, sampleY, sampleWidth, sampleHeight);
  }

  /**
   * Matches the incoming string against one of the constants defined; tl, tr, bl, br.
   *
   * @param code The code for the angle of the arc to generate, if no match is found the default is
   *          {@link #TOP_RIGHT} - or 0 degrees.
   * @return The pre-defined 90 degree angle starting degree point.
   */
  public float getStartAngle(String code) {
    if (TOP_LEFT.equalsIgnoreCase(code))
      return ANGLE_TOP_LEFT;
    if (TOP_RIGHT.equalsIgnoreCase(code))
      return ANGLE_TOP_RIGHT;
    if (BOTTOM_LEFT.equalsIgnoreCase(code))
      return ANGLE_BOTTOM_LEFT;
    if (BOTTOM_RIGHT.equalsIgnoreCase(code))
      return ANGLE_BOTTOM_RIGHT;

    return ANGLE_TOP_RIGHT;
  }

  /**
   * Decodes the specified input color string into a compatible awt color object. Valid inputs
   * are any in the css2 color spec or hex strings.
   *
   * @param color The color to match.
   * @return The decoded color object, may be black if decoding fails.
   */
  public Color decodeColor(String color) {
    Color specColor = (Color) _cssSpecMap.get(color);
    if (specColor != null)
      return specColor;

    String hexColor = color.startsWith("0x") ? color : "0x" + color;

    return Color.decode(hexColor);
  }
}
