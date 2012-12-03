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
package pt.iflow.utils.scanner;
//package uk.co.mmscomputing.device.twain;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import uk.co.mmscomputing.device.scanner.Scanner;
import uk.co.mmscomputing.device.scanner.ScannerDevice;
import uk.co.mmscomputing.device.scanner.ScannerIOException;
import uk.co.mmscomputing.device.scanner.ScannerIOMetadata;
import uk.co.mmscomputing.device.scanner.ScannerListener;

public class TwainExample implements ScannerListener{

  static TwainExample app;  

  Scanner scanner;

  public TwainExample(String[] argv){
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
        ImageIO.write(image, "png", new File("mmsc_image.png")); //$NON-NLS-1$ //$NON-NLS-2$
      }catch(Exception e){
        e.printStackTrace();
      }
    }else if(type.equals(ScannerIOMetadata.NEGOTIATE)){
      ScannerDevice device=metadata.getDevice();
      try{
        device.setShowUserInterface(true);
        device.setShowProgressBar(true);
        //        device.setResolution(100);
      }catch(Exception e){
        e.printStackTrace();
      }
    }else if(type.equals(ScannerIOMetadata.STATECHANGE)){
      System.err.println(metadata.getStateStr());
      if(metadata.isFinished()){
        System.exit(0);
      }
    }else if(type.equals(ScannerIOMetadata.EXCEPTION)){
      metadata.getException().printStackTrace();
    }
  }

  public static void main(String[] argv){
    try{
      app=new TwainExample(argv);
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}


