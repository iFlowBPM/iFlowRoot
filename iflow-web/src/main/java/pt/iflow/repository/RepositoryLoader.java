/*
 *
 * Created on Jan 18, 2006 by mach
 *
  */

package pt.iflow.repository;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import pt.iflow.api.repository.RepositoryAccess;
import pt.iflow.api.utils.Utils;
import pt.iknow.utils.Convert;

/**
 * 
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright (c) 2006 mach</p>
 * 
 * @author mach
 */

public class RepositoryLoader {

  private static final int NUM_PARAMS = 5;
  
  private static final int DB_HOST_PARAM = 0;
  private static final int DB_NAME_PARAM = 1;
  private static final int DB_USER_PARAM = 2;
  private static final int DB_PASS_PARAM = 3;
  
  private static final String FILE_PATH_KEY = "FILE_PATH";
  private static final String REP_PATH_KEY = "REP_PATH";
  private static final String IS_ZIP_FLAG_KEY = "IS_ZIP_FLAG";
  
  private RepositoryAccess _access;

  public RepositoryLoader(String accessImpl,Properties props) {
    this._access = RepositoryAccessFactory.getRepositoryAccess(accessImpl, props);
  }

  public RepositoryLoader(Properties props) {
    this(null, props);
  }

  private static String usage() {
    StringBuffer result = new StringBuffer();
    result.append("Usage: ");
    result.append("<repository db host> ");
    result.append("<repository db> ");
    result.append("<repository db user> ");
    result.append("<repository db pass> ");
    result.append("<file>#<repository file path>#<is zip ? Y/N> [<file>#<repository file path>#<is zip ? Y/N> ...]");
    return result.toString();
  }
  
  private static Collection parseFiles(String[] args, int start) {
    ArrayList retObj = new ArrayList();
    
    for(int i=start;i<args.length;i++) {
      System.out.println("Parsing file info : " + args[i]);
      
      String[] tmp = StringUtils.split(args[i],'#');
      
      String filePath = tmp[0];
      String repPath = tmp[1];
      String isZipFlag = tmp[2];
      
      if(!isZipFlag.equalsIgnoreCase("Y") && !isZipFlag.equalsIgnoreCase("N") ) {
        System.out.println("Invalid repository dir flag : " + isZipFlag + " - Skiping file " + filePath );
        continue;
      }
      
      HashMap fileData = new HashMap();
      
      fileData.put(FILE_PATH_KEY, filePath);
      fileData.put(REP_PATH_KEY, repPath);
      fileData.put(IS_ZIP_FLAG_KEY, isZipFlag);
      
      retObj.add(fileData);
    }
    
    return retObj;
  }
  
  private void uploadFile(String filePath, String repPath, boolean isZip) throws IOException {
    System.out.print("Uploading '" + filePath + "' to '" + repPath);
    if(isZip) {
      System.out.println("' as dir.");
    } else {
      System.out.println("'.");
    }
    
    FileInputStream fis = null;
    
    try {
    	fis = new FileInputStream(filePath);

    	byte[] buffer = new byte[8096];
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	int read = -1;
    	while ((read = fis.read(buffer)) != -1) {
    		bos.write(buffer, 0, read);
    	}
    	bos.close();
    	byte[] data = bos.toByteArray();


    	// TODO obter a organizacao
    	if(isZip)
    		this._access.setZipFile(repPath, data);
    	else
    		this._access.setFile(repPath, data);
    } finally {
    	if( fis != null) Utils.safeClose(fis);
    }    
  }
  
  /**
   * 
   * TODO Add comment for method main on RepositoryLoader
   * 
   * @param args
   */
  public static void main(String[] args) {
    if(args.length < NUM_PARAMS) {
      System.out.println("Invalid number of arguments (" + args.length+ "):");
      System.out.println(usage());
      return;
    }
    
    Properties props = new Properties();

    props.setProperty("REP_DB_HOST", args[DB_HOST_PARAM]);
    props.setProperty("REP_DB_PORT", "1521");
    props.setProperty("REP_DB_NAME", args[DB_NAME_PARAM]);
    props.setProperty("REP_DB_USER", args[DB_USER_PARAM]);
    props.setProperty("REP_DB_PASS", args[DB_PASS_PARAM]);

    
    System.out.println("Properties : \n----\n" + props.toString() + "\n----");
    
    RepositoryLoader loader = new RepositoryLoader(props);
    
    Collection dataToUpload = parseFiles(args,    NUM_PARAMS-1);
    
    Iterator iter = dataToUpload.iterator();
    
    while(iter.hasNext()) {
      Map fileInfo = (Map) iter.next();
      String filePath = (String) fileInfo.get(FILE_PATH_KEY);
      String repPath = (String) fileInfo.get(REP_PATH_KEY);
      boolean isZip = Convert.toBool((String)fileInfo.get(IS_ZIP_FLAG_KEY));
      try {
        loader.uploadFile(filePath,repPath,isZip);
      }
      catch (IOException e) {
        System.out.println("Error uploading file " + filePath);
        e.printStackTrace();
      }
    }
  }



}
