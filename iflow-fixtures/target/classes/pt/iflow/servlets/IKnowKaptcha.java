package pt.iflow.servlets;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.lang.RandomStringUtils;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.text.WordRenderer;
import com.google.code.kaptcha.util.Helper;

/**
 * @author testvoogd@hotmail.com
 */
public class IKnowKaptcha implements Producer {
  private Properties props = null;
  private boolean bbox = true;
  private Color boxColor = Color.black;
  private int boxThick = 1;
  private WordRenderer wordRenderer = null;
  private GimpyEngine gimpy = null;
  private BackgroundProducer backGroundImp = null;
  private TextProducer textProducer = null;

  public IKnowKaptcha() {
  }

  public void setProperties(Properties props) {
    this.props = props;
    if (this.props != null) {
      // doing some init stuff.
      String box = props.getProperty(Constants.KAPTCHA_BORDER);
      if (box != null && !box.equals("no")) {
        this.bbox = true;
      } else {
        this.bbox = false;
      }

      // this.w = Helper.getIntegerFromString(props, "ikaptcha.width");
      // this.h = Helper.getIntegerFromString(props, "ikaptcha.height");

      if (bbox) {
        boxColor = Helper.getColor(this.props, Constants.KAPTCHA_BORDER_COLOR, Color.black);
        boxThick = Helper.getIntegerFromString(props, Constants.KAPTCHA_BORDER_THICKNESS);
        if (boxThick == 0)
          boxThick = 1;
      }

      this.gimpy = (GimpyEngine) Helper.ThingFactory.loadImpl(Helper.ThingFactory.OBSCURIFICATOR_IMPL, props);
      this.backGroundImp = (BackgroundProducer) Helper.ThingFactory.loadImpl(Helper.ThingFactory.BACKGROUND_IMPL, props);
      this.wordRenderer = (WordRenderer) Helper.ThingFactory.loadImpl(Helper.ThingFactory.WORDRENDERER_IMPL, props);
      this.textProducer = (TextProducer) Helper.ThingFactory.loadImpl(Helper.ThingFactory.TEXTPRODUCER_IMPL, props);
      this.textProducer.getText();
    }
  }

  /**
   * The width image in pixels.
   */
  private int w = 150;

  /**
   * The height image in pixels.
   */
  private int h = 50;

  /**
   * Create an image which have written a distorted text, text given as
   * parameter. The result image is put on the output stream
   * 
   * @param stream
   *          the OutputStrea where the image is written
   * @param text
   *          the distorted characters written on image
   * @throws IOException
   *           if an error occurs during the image written on output stream.
   */
  public void createImage(OutputStream stream, String text) throws IOException {

    Iterator<ImageWriter> iiw = ImageIO.getImageWritersByFormatName("jpg");
    if (iiw.hasNext()) {
      ImageWriter writer = iiw.next();

      // put the text on the image
      BufferedImage bi = wordRenderer.renderWord(text, w, h);

      // create a new distorted (wound version of) the image
      gimpy.setProperties(props);
      bi = gimpy.getDistortedImage(bi);

      // add a background to the image
      bi = this.backGroundImp.addBackground(bi);
      // bi = addBackground(bi);

      // get the graphics of the image
      Graphics2D graphics = bi.createGraphics();

      if (bbox)
        drawBox(graphics);

      // encode the image to jpeg format

      ImageOutputStream ios = ImageIO.createImageOutputStream(stream);
      ImageWriteParam param = writer.getDefaultWriteParam();
      param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      param.setCompressionQuality(1);
      writer.setOutput(ios);
      writer.write(null, new IIOImage(bi, null, null), param);
      ios.flush();
      writer.dispose();
    }
  }

  private void drawBox(Graphics2D graphics) {
    graphics.setColor(this.boxColor);

    if (this.boxThick != 1) {
      BasicStroke stroke = new BasicStroke((float) boxThick);
      graphics.setStroke(stroke);
    }

    Line2D d2 = new Line2D.Double(0, 0, 0, w);
    graphics.draw(d2);

    Line2D d3 = new Line2D.Double(0, 0, w, 0);
    graphics.draw(d3);

    d3 = new Line2D.Double(0, h - 1, w, h - 1);
    graphics.draw(d3);

    d3 = new Line2D.Double(w - 1, h - 1, w - 1, 0);

    graphics.draw(d3);
  }

  public void setBackGroundImageProducer(BackgroundProducer background) {
    this.backGroundImp = background;

  }

  /**
   * @return the properties
   */
  public Properties getProperties() {
    return props;
  }

  /**
   * 
   */
  public void setObscurificator(GimpyEngine engine) {
    this.gimpy = engine;
  }

  /**
   * 
   */
  public void setTextProducer(TextProducer textP) {
    this.textProducer = textP;
  }

  char[] captchars = new char[] { 'i', 'k', 'n', 'o', 'w', 'i', 'f', 'l', 'o', 'w', };

  char[] captcharsx = new char[] { 'a', 'b', 'c', 'd', 'e', '2', '3', '4', '5', '6', '7', '8', 'g', 'f', 'y', 'n', 'm', 'n', 'p',
      'w', 'x' };

  public String createText() {
    return RandomStringUtils.random(4, captchars);
  }

  /**
   * @param renederer
   */
  public void setWordRenderer(WordRenderer renederer) {
    wordRenderer = renederer;
  }

}
