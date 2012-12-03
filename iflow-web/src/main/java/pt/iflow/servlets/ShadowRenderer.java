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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import pt.iflow.api.utils.Logger;

/**
 *
 */
public class ShadowRenderer {

    // size of the shadow in pixels (defines the fuzziness)
    private int size = 5;

    // opacity of the shadow
    private float opacity = 0.5f;
    
    // color of the shadow
    private Color color = Color.BLACK;

    /**
     * <p>A shadow renderer needs three properties to generate shadows.
     * These properties are:</p>
     * <ul>
     *   <li><i>size</i>: The size, in pixels, of the shadow. This property also
     *   defines the fuzzyness.</li>
     *   <li><i>opacity</i>: The opacity, between 0.0 and 1.0, of the shadow.</li>
     *   <li><i>color</i>: The color of the shadow. Shadows are not meant to be
     *   black only.</li>
     * </ul>
     * @param size the size of the shadow in pixels. Defines the fuzziness.
     * @param opacity the opacity of the shadow.
     * @param color the color of the shadow.
     */
    public ShadowRenderer(final int size, final float opacity, final Color color) {

        setSize(size);
        setOpacity(opacity);
        setColor(color);
    }

    /**
     * <p>Gets the color used by the renderer to generate shadows.</p>
     * @return this renderer's shadow color
     */
    public Color getColor() {
        return color;
    }

    /**
     * <p>Sets the color used by the renderer to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this color
     * until it is set again.</p>
     * <p>If the color provided is null, the previous color will be retained.</p>
     * @param shadowColor the generated shadows color
     */
    public void setColor(final Color shadowColor) {
        if (shadowColor != null) {
            Color oldColor = this.color;
            this.color = shadowColor;
        }
    }

    /**
     * <p>Gets the opacity used by the renderer to generate shadows.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque.</p>
     * @return this renderer's shadow opacity
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * <p>Sets the opacity used by the renderer to generate shadows.</p>
     * <p>Consecutive calls to {@link #createShadow} will all use this opacity
     * until it is set again.</p>
     * <p>The opacity is comprised between 0.0f and 1.0f; 0.0f being fully
     * transparent and 1.0f fully opaque. If you provide a value out of these
     * boundaries, it will be restrained to the closest boundary.</p>
     * @param shadowOpacity the generated shadows opacity
     */
    public void setOpacity(final float shadowOpacity) {
        float oldOpacity = this.opacity;

        if (shadowOpacity < 0.0) {
            this.opacity = 0.0f;
        } else if (shadowOpacity > 1.0f) {
            this.opacity = 1.0f;
        } else {
            this.opacity = shadowOpacity;
        }
    }

    /**
     * <p>Gets the size in pixel used by the renderer to generate shadows.</p>
     * @return this renderer's shadow size
     */
    public int getSize() {
        return size;
    }

    /**
     * <p>Sets the size, in pixels, used by the renderer to generate shadows.</p>
     * <p>The size defines the blur radius applied to the shadow to create the
     * fuzziness.</p>
     * <p>There is virtually no limit to the size. The size cannot be negative.
     * If you provide a negative value, the size will be 0 instead.</p>
     * @param shadowSize the generated shadows size in pixels (fuzziness)
     */
    public void setSize(final int shadowSize) {
        int oldSize = this.size;

        if (shadowSize < 0) {
            this.size = 0;
        } else {
            this.size = shadowSize;
        }
    }

    /**
     * <p>Generates the shadow for a given picture and the current properties
     * of the renderer.</p>
     * <p>The generated image dimensions are computed as following:</p>
     * <pre>
     * width  = imageWidth  + 2 * shadowSize
     * height = imageHeight + 2 * shadowSize
     * </pre>
     * @param image the picture from which the shadow must be cast
     * @return the picture containing the shadow of <code>image</code>
     */
    public BufferedImage createShadow(final BufferedImage image) {
        
        // Written by Sesbastien Petrucci
        int shadowSize = size * 2;

        int srcWidth = image.getWidth();
        int srcHeight = image.getHeight();

        int dstWidth = srcWidth + shadowSize;
        int dstHeight = srcHeight + shadowSize;

        int left = size;
        int right = shadowSize - left;

        int yStop = dstHeight - right;

        int shadowRgb = color.getRGB() & 0x00FFFFFF;
        int[] aHistory = new int[shadowSize];
        int historyIdx;

        int aSum;

        int[] dstBuffer = new int[dstWidth * dstHeight];
        int[] srcBuffer = new int[srcWidth * srcHeight];

        getPixels(image, 0, 0, srcWidth, srcHeight, srcBuffer);

        int lastPixelOffset = right * dstWidth;
        float hSumDivider = 1.0f / shadowSize;
        float vSumDivider = opacity / shadowSize;

        int[] hSumLookup = new int[256 * shadowSize];
        for (int i = 0; i < hSumLookup.length; i++) {
            hSumLookup[i] = (int) (i * hSumDivider);
        }

        int[] vSumLookup = new int[256 * shadowSize];
        for (int i = 0; i < vSumLookup.length; i++) {
            vSumLookup[i] = (int) (i * vSumDivider);
        }

        int srcOffset;

        // horizontal pass : extract the alpha mask from the source picture and
        // blur it into the destination picture
        for (int srcY = 0, dstOffset = left * dstWidth; srcY < srcHeight; srcY++) {

            // first pixels are empty
            for (historyIdx = 0; historyIdx < shadowSize; ) {
                aHistory[historyIdx++] = 0;
            }

            aSum = 0;
            historyIdx = 0;
            srcOffset = srcY * srcWidth;

            // compute the blur average with pixels from the source image
            for (int srcX = 0; srcX < srcWidth; srcX++) {

                int a = hSumLookup[aSum];
                dstBuffer[dstOffset++] = a << 24;   // store the alpha value only
                                                    // the shadow color will be added in the next pass

                aSum -= aHistory[historyIdx]; // substract the oldest pixel from the sum

                // extract the new pixel ...
                a = srcBuffer[srcOffset + srcX] >>> 24;
                aHistory[historyIdx] = a;   // ... and store its value into history
                aSum += a;                  // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the row - no new pixels to grab
            for (int i = 0; i < shadowSize; i++) {

                int a = hSumLookup[aSum];
                dstBuffer[dstOffset++] = a << 24;

                // substract the oldest pixel from the sum ... and nothing new to add !
                aSum -= aHistory[historyIdx];

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        // vertical pass
        for (int x = 0, bufferOffset = 0; x < dstWidth; x++, bufferOffset = x) {

            aSum = 0;

            // first pixels are empty
            for (historyIdx = 0; historyIdx < left;) {
                aHistory[historyIdx++] = 0;
            }

            // and then they come from the dstBuffer
            for (int y = 0; y < right; y++, bufferOffset += dstWidth) {
                int a = dstBuffer[bufferOffset] >>> 24;         // extract alpha
                aHistory[historyIdx++] = a;                     // store into history
                aSum += a;                                      // and add to sum
            }

            bufferOffset = x;
            historyIdx = 0;

            // compute the blur avera`ge with pixels from the previous pass
            for (int y = 0; y < yStop; y++, bufferOffset += dstWidth) {

                int a = vSumLookup[aSum];
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;  // store alpha value + shadow color

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                a = dstBuffer[bufferOffset + lastPixelOffset] >>> 24;   // extract the new pixel ...
                aHistory[historyIdx] = a;                               // ... and store its value into history
                aSum += a;                                              // ... and add its value to the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }

            // blur the end of the column - no pixels to grab anymore
            for (int y = yStop; y < dstHeight; y++, bufferOffset += dstWidth) {

                int a = vSumLookup[aSum];
                dstBuffer[bufferOffset] = a << 24 | shadowRgb;

                aSum -= aHistory[historyIdx];   // substract the oldest pixel from the sum

                if (++historyIdx >= shadowSize) {
                    historyIdx -= shadowSize;
                }
            }
        }

        BufferedImage dst = null; 
        try {
          dst = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_ARGB);
        }catch(OutOfMemoryError e1) {
          if(Logger.isDebugEnabled()) {
            Logger.warning("", this, "createShadow", "Attempting to allocate free memory...");
          }
          aHistory = null;
          dstBuffer = null;
          hSumLookup = null;
          srcBuffer = null;
          vSumLookup = null;
          try {
            dst = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_ARGB);
          }catch(OutOfMemoryError e2) {
            if(Logger.isDebugEnabled()) {
              Logger.warning("", this, "createShadow", "Failed to free memory space for image!");
            }
            dst = new BufferedImage(0, 0, BufferedImage.TYPE_INT_ARGB);
          }
        }
        
        setPixels(dst, 0, 0, dstWidth, dstHeight, dstBuffer);
        return dst;
    }

    /**
     * <p>Returns an array of pixels, stored as integers, from a
     * <code>BufferedImage</code>. The pixels are grabbed from a rectangular
     * area defined by a location and two dimensions. Calling this method on
     * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
     * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
     *
     * @param img the source image
     * @param x the x location at which to start grabbing pixels
     * @param y the y location at which to start grabbing pixels
     * @param w the width of the rectangle of pixels to grab
     * @param h the height of the rectangle of pixels to grab
     * @param pixels a pre-allocated array of pixels of size w*h; can be null
     * @return <code>pixels</code> if non-null, a new array of integers
     *   otherwise
     * @throws IllegalArgumentException is <code>pixels</code> is non-null and
     *   of length &lt; w*h
     */
    public static int[] getPixels(BufferedImage img,
                                  int x, int y, int w, int h, int[] pixels) {
        if (w == 0 || h == 0) {
            return new int[0];
        }

        if (pixels == null) {
            pixels = new int[w * h];
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length" +
                                               " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
            imageType == BufferedImage.TYPE_INT_RGB) {
            Raster raster = img.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        // Unmanages the image
        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    /**
     * <p>Writes a rectangular area of pixels in the destination
     * <code>BufferedImage</code>. Calling this method on
     * an image of type different from <code>BufferedImage.TYPE_INT_ARGB</code>
     * and <code>BufferedImage.TYPE_INT_RGB</code> will unmanage the image.</p>
     *
     * @param img the destination image
     * @param x the x location at which to start storing pixels
     * @param y the y location at which to start storing pixels
     * @param w the width of the rectangle of pixels to store
     * @param h the height of the rectangle of pixels to store
     * @param pixels an array of pixels, stored as integers
     * @throws IllegalArgumentException is <code>pixels</code> is non-null and
     *   of length &lt; w*h
     */
    public static void setPixels(BufferedImage img,
                                 int x, int y, int w, int h, int[] pixels) {
        if (pixels == null || w == 0 || h == 0) {
            return;
        } else if (pixels.length < w * h) {
            throw new IllegalArgumentException("pixels array must have a length" +
                                               " >= w*h");
        }

        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
            imageType == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = img.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        } else {
            // Unmanages the image
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }
}
