package pt.iflow.utils.scanner;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;

/**
 * Codigo especifico do scanner
 * @author ombl
 *
 */
public class ScannerAccess implements ScannerListener, FileAppletService {

  private Scanner _scanner;

  private File _currentImage;

  private String _currentFileName;

  public ScannerAccess() {
    _scanner = Scanner.getDevice();
    if (null != this._scanner)
      _scanner.addListener(this);
  }

  public void update(ScannerIOMetadata.Type type, final ScannerIOMetadata metadata) {
    if (type.equals(ScannerIOMetadata.ACQUIRED)) { // acquired BufferedImage
      BufferedImage image=metadata.getImage();
      System.out.println("Have an image now!"); //$NON-NLS-1$
      try{
        File file = File.createTempFile("scan_", ".jpg"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println("Image Path: "+file.getAbsolutePath()); //$NON-NLS-1$
        ImageIO.write(image, "jpg", file); //$NON-NLS-1$
      }catch(Exception e){
        e.printStackTrace();
      }
    } else if (type.equals(ScannerIOMetadata.FILE)) { // acquired image as file (twain only for the time being)
      final File file = metadata.getFile(); // make reference copy here to avoid race condition
      this._currentFileName = file.getName();
      this._currentImage = file;
    } else if (type.equals(ScannerIOMetadata.NEGOTIATE)) {
      negotiate(metadata);
    } else if (type.equals(ScannerIOMetadata.STATECHANGE)) {
      stateChange(metadata);
    } else if (type.equals(ScannerIOMetadata.EXCEPTION)) {
      metadata.getException().printStackTrace();
    }
  }

  private void stateChange(ScannerIOMetadata metadata) {
    System.err.println(metadata.getStateStr());

    // eh mesmo assim??

    /*
    if (metadata instanceof TwainIOMetadata) { // TWAIN only !
      if (metadata.isState(TwainConstants.STATE_TRANSFERREADY)) { // state = 6
        TwainSource source = ((TwainIOMetadata) metadata).getSource();
        try {
          TwainImageInfo imageInfo = new TwainImageInfo(source);
          imageInfo.get();
        } catch (Exception e) {
          e.printStackTrace();
        }
        try {
          TwainImageLayout imageLayout = new TwainImageLayout(source);
          imageLayout.get();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (metadata.isState(TwainConstants.STATE_TRANSFERRING)) { // state = 7

        // In state 4: supportTwainExtImageInfo=source.getCapability(TwainConstants.ICAP_EXTIMAGEINFO).booleanValue();

        TwainSource source = ((TwainIOMetadata) metadata).getSource();

        try {
          int[] tweis = new int[0x1240 - 0x1200];
          for (int i = 0; i < tweis.length; i++) {
            tweis[i] = 0x1200 + i;
          }

          TwainExtImageInfo imageInfo = new TwainExtImageInfo(source, tweis);
          imageInfo.get();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    */
  }
  
  private void negotiate(ScannerIOMetadata metadata) {
    ScannerDevice device=metadata.getDevice();
    try {
      device.setShowUserInterface(true);
      device.setShowProgressBar(true);
    } catch (ScannerIOException e) {
      e.printStackTrace();
    }

    /*
    if (metadata instanceof SaneIOMetadata) { // SANE only !

      try {
        System.out.println("SANE NEGOTIATE mode = ");
        SaneDevice source = (SaneDevice) ((SaneIOMetadata) metadata).getDevice();

      } catch (Exception e) {
        System.out.println("9\b" + e.getMessage());
      }

    } else if (metadata instanceof TwainIOMetadata) {

      ScannerDevice device = metadata.getDevice();
      try {
        TwainSource source = ((TwainIOMetadata) metadata).getSource();
        source.setXferMech(TwainConstants.TWSX_FILE);
        device.setShowUserInterface(true);
        device.setShowProgressBar(true);

      } catch (ScannerIOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }
     */
  }

  public synchronized File scan() {
    if (null == _scanner)
      return null;
    try {
      _scanner.acquire();
      return this._currentImage;
    } catch (ScannerIOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public synchronized String getCurrentFileName() {
    return this._currentFileName;
  }

  public void discard() {
    this._currentImage = null;
  }

  public boolean isActive() {
    return this._scanner != null;
  }

}
