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

import java.awt.Toolkit;
import java.util.Map;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.swingworker.SwingWorker;
import org.json.JSONException;
import org.json.JSONObject;

import pt.iflow.applet.cipher.CipherException;
import pt.iflow.applet.cipher.FileCipher;
import pt.iflow.applet.signer.FileSigner;
import pt.iflow.applet.signer.SignerException;



public class SwingTask extends SwingWorker<Object, Object> implements AppletDocParameters {

  private static Log log = LogFactory.getLog(SwingTask.class);
  public static final String START_TASK = "StartTask"; //$NON-NLS-1$
  public static final String PROGRESS_CHANGE = "ProgressChange"; //$NON-NLS-1$
  public static final String ERROR_OCCURED = "ErrorOccured"; //$NON-NLS-1$
  public static final String COMPLETE = "Complete"; //$NON-NLS-1$
  
  private String result;
  private DynamicDialog dialog;
  
  protected void done() {
    System.gc();
    log.debug("Swing task done!"); //$NON-NLS-1$
    Toolkit.getDefaultToolkit().beep();
    super.done();
  }
  
  public SwingTask(DynamicDialog dialog) {
    this.dialog = dialog;
  }
  
  public String getResult() {
    return result;
  }
  
  protected Object doInBackground() throws Exception {
    final WebClient webClient = dialog.getWebClient();
    final FileSigner signer = dialog.getFileSigner();
    final FileCipher cipher = dialog.getFileCipher();
    final IVFileProvider fileProvider = dialog.getFileProvider();
    firePropertyChange(START_TASK, false, true);
    String replaceFile = String.valueOf(fileProvider.replaceFile());
    
    // 1. Obter o ficheiro
    IVFile file = fileProvider.getFile(dialog);
    if(null == file) {
      if(fileProvider.isErrorSet()) {
        firePropertyChange(ERROR_OCCURED, null, Messages.getString("PopUpFileApplet.118")); //$NON-NLS-1$
        return null;
      } else {
        firePropertyChange(COMPLETE, DynamicDialog.OK, DynamicDialog.CANCEL);
        return null;
      }
       
    }
    
    // 2. Assinar o ficheiro
    IVFile signedFile = null;
    if(DynamicDialog.haveCert){
    	
    if(DynamicDialog.toLoad) LoadImageAction.setNumAss(0); //Reset ao numero de assinaturas se o Doc foi inserido agora
    	
    firePropertyChange(PROGRESS_CHANGE, null, new ProgressEvent(33,Messages.getString("PopUpFileApplet.1"))); //$NON-NLS-1$
    try {
      signedFile = signer.sign(file);
    } catch (SignerException e) {
      firePropertyChange(ERROR_OCCURED, e, e.getMessage());
      return null;
    }
    if(null == signedFile) {
      firePropertyChange(ERROR_OCCURED, null, Messages.getString("PopUpFileApplet.55")); //$NON-NLS-1$
      return null;
    }
    }
    // 3. Encriptar o ficheiro
    firePropertyChange(PROGRESS_CHANGE, null, new ProgressEvent(33,Messages.getString("PopUpFileApplet.1"))); //$NON-NLS-1$
    IVFile encryptedFile = null;
    try {
    	
    	if(DynamicDialog.toLoad && !DynamicDialog.haveCert){
    		encryptedFile = cipher.encrypt(file);
    		LoadImageAction.setNumAss(-1); //Caso nao tenha sido assinado reset para zero (como)
    	}
    	else{
    		encryptedFile = cipher.encrypt(signedFile);
    	}
    	
    } catch (CipherException e) {
      firePropertyChange(ERROR_OCCURED, e, e.getMessage());
      return null;
    }
    if(null == encryptedFile) {
      firePropertyChange(ERROR_OCCURED, null, Messages.getString("PopUpFileApplet.55")); //$NON-NLS-1$
      return null;
    }
    
    if (LoadImageAction.getFlagGravar()){    
        webClient.postImage(LoadImageAction.getImageBD(), dialog);
      }
    
    // 4. Upload do ficheiro resultante
    firePropertyChange(PROGRESS_CHANGE, null, new ProgressEvent(66,Messages.getString("PopUpFileApplet.56"))); //$NON-NLS-1$
    Map<String,String> params = webClient.getParameters();
    params.put(WebClient.UPDATE_PARAM, replaceFile);
    String fileId = webClient.postFile(params, encryptedFile, dialog);
    
    log.debug("Got this response from server: "+fileId); //$NON-NLS-1$

    try {
      new Integer(fileId.trim());
    } catch(Exception e) {
      firePropertyChange(ERROR_OCCURED, null, Messages.getString("PopUpFileApplet.60")); //$NON-NLS-1$
      return null;
    }
    
    try {
      JSONObject objResult = new JSONObject();
      objResult.put("id", fileId); //$NON-NLS-1$
      objResult.put("name", file.getName()); //$NON-NLS-1$
      objResult.put("varname", params.get(VARIABLE_PARAM)); //$NON-NLS-1$
      result = objResult.toString();
    } catch(JSONException e) {
      log.error("Error building response", e); //$NON-NLS-1$
    }

    // 5. Fim!
    firePropertyChange(PROGRESS_CHANGE, null, new ProgressEvent(100,Messages.getString("PopUpFileApplet.65"))); //$NON-NLS-1$

    firePropertyChange(COMPLETE, DynamicDialog.CANCEL, DynamicDialog.OK);

    return fileId;
  }

  public ImageIconRep getImageFromDB() throws Exception {
    final WebClient webClient = dialog.getWebClient();
    ImageIconRep image = webClient.getImage(dialog);
	return image;
  }
  
  public ImageIconRep getRubricImageFromDB() throws Exception {
	    final WebClient webClient = dialog.getWebClient();
	    ImageIconRep image = webClient.getRubricImage(dialog);
		return image;
	  }
  
}
