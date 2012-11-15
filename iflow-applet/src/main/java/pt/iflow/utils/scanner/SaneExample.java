package pt.iflow.utils.scanner;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class SaneExample implements ScannerListener{

  static SaneExample app;

  Scanner scanner;

  public SaneExample(String[] argv){
    scanner=Scanner.getDevice();
    scanner.addListener(this);
    try {
      scanner.acquire();
    } catch (ScannerIOException e) {
      e.printStackTrace();
    }
  }

  public void update(ScannerIOMetadata.Type type, ScannerIOMetadata metadata){
    if(type.equals(ScannerIOMetadata.ACQUIRED)){
      BufferedImage image=metadata.getImage();
      System.out.println("Have an image now!"); //$NON-NLS-1$
      try{
        ImageIO.write(image, "bmp", new File("mmsc_image.bmp")); //$NON-NLS-1$ //$NON-NLS-2$
      }catch(Exception e){
        e.printStackTrace();
      }
    }else if(type.equals(ScannerIOMetadata.STATECHANGE)){
      System.err.println(metadata.getStateStr());
      if(metadata.isFinished()){
        System.exit(0);
      }
    }else if(type.equals(ScannerIOMetadata.NEGOTIATE)){
      ScannerDevice device=metadata.getDevice();
      try{
        device.setShowUserInterface(true);   // default: true
        device.setShowProgressBar(true);     // default: true
        //        device.setResolution(200);
      }catch(Exception e){
        e.printStackTrace();
      }
    }else if(type.equals(ScannerIOMetadata.EXCEPTION)){
      metadata.getException().printStackTrace();
    }
  }

  public static void main(String[] argv){
    try{
      app=new SaneExample(argv);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}


