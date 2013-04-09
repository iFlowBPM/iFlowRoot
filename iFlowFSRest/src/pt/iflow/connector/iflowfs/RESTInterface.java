/*
 * 
 */
package pt.iflow.connector.iflowfs;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.google.gson.Gson;
import com.infosistema.fs.entity.FileStructure;
import com.infosistema.fs.entity.KeyStructure;
import com.infosistema.fs.entity.KeyValueReference;

/**
 * The Class RESTInterface provides the connection with IFSService.
 * 
 * @author Ekaterina Hilário
 */
public class RESTInterface {
	
	static private String proppath = "D:/C/Users/agago/workspace/iFlowFSRest/src/iflowfsrest.properties";	
	
	/**
	 * Gets the content of the key by the given id of this key.
	 *
	 * @param keyId the key id of String type
	 * @return the key of KeyStructure type
	 */
	public KeyStructure getKey(String keyId) {
		
		Properties prop = new Properties(); //A property list 		
		Resty resty = new Resty();			//The entity of Resty Class, provides REST services
		Gson gson = new Gson();				//The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON representation
		JSONResource jsonResult;			//A resource presentation in JSON format
		String link;						//A string to keep the URL obtained from .properties file
		String jsonString;					//A string to keep JSON string 
		KeyStructure key;					//An object of KeyStructure to keep return value
		
		try {
			prop.load(new FileInputStream(proppath));
            link = prop.getProperty("key.url"); // receives the URL value from the .properties file
			jsonResult = resty.json(link + "/" + keyId); //gets the specified key content in a form of JSON string
			jsonString = jsonResult.toObject().toString(); 
			
			key = gson.fromJson(jsonString, KeyStructure.class); // transforms JSON string into the KeyStructure object
			return key;
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets all keys in the file structure.
	 *
	 * @return the keys of KeyStructure type
	 */
	public KeyStructure getKey() {
		
		Properties prop = new Properties(); //A property list 		
		Resty resty = new Resty();			//The entity of Resty Class, provides REST services
		Gson gson = new Gson();				//The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON representation
		JSONResource jsonResult;			//A resource presentation in JSON format
		String link;						//A string to keep the URL obtained from .properties file
		String jsonString;					//A string to keep JSON string 
		KeyStructure keys;					//An object of KeyStructure to keep return value
		
		try {
			prop.load(new FileInputStream(proppath));
            link = prop.getProperty("key.url"); // receives the URL value from the .properties file
			jsonResult = resty.json(link); //gets all keys content in a form of JSON string
			jsonString = jsonResult.toObject().toString();
			
			keys = gson.fromJson(jsonString, KeyStructure.class); // transforms JSON string into the KeyStructure object
			return keys;
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Gets the file characteristics specified by fileId.
	 *
	 * @param fileId the file id of String type
	 * @return the file of FileStructure type
	 */
	public FileStructure getFile(String fileId) {
		
		Properties prop = new Properties(); //A property list 		
		Resty resty = new Resty();			//The entity of Resty Class, provides REST services
		Gson gson = new Gson();				//The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON representation
		JSONResource jsonResult;			//A resource presentation in JSON format
		String link;						//A string to keep the URL obtained from .properties file
		String jsonString;					//A string to keep JSON string 
		FileStructure file;					//An object of FileStructure to keep return value
		
		try {
			prop.load(new FileInputStream(proppath));
            link = prop.getProperty("file.url"); // receives the URL value from the .properties file
			jsonResult = resty.json(link + "/" + fileId); //gets the specified file characteristics in a form of JSON string
			jsonString = jsonResult.toObject().toString(); 
			
			file = gson.fromJson(jsonString, FileStructure.class);  // transforms JSON string into the FileStructure object
			return file;
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Puts the specified file into the IFSService file system.
	 *
	 * @param file the file characteristics of FileStructure type
	 * @return the file of FileStructure type
	 */
	public FileStructure putFile(FileStructure file) {
		
		Properties prop = new Properties(); //A property list 		
		Resty resty = new Resty();			//The entity of Resty Class, provides REST services
		Gson gson = new Gson();				//The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON representation
		JSONResource jsonResult;			//A resource presentation in JSON format
		JSONObject jsonFile;				//An unordered collection of name/value pairs which keep file characteristics
		String link;						//A string to keep the URL obtained from .properties file
		String jsonString;					//A string to keep JSON string 
		
		try {
			prop.load(new FileInputStream(proppath));           
			link = prop.getProperty("file.url"); // receives the URL value from the .properties file			
			
			jsonString = gson.toJson(file); 
			jsonFile = new JSONObject(jsonString); // transforms JSON string into the JSON object to be put via REST
			
			jsonResult = resty.json(link, resty.put(resty.content(jsonFile))); //puts the specified file into the IFSService file system
			jsonString = jsonResult.toObject().toString();						
			file = gson.fromJson(jsonString, FileStructure.class); // transforms JSON string into the FileStructure object
			return file;
		} 
		catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	/**
	 * Deletes the file specified by fileId from the IFSService file system.
	 *
	 * @param fileId the file id of String type
	 * @return the file of FileStructure type
	 */
	public FileStructure deleteFile(String fileId) {
		
		Properties prop = new Properties(); //A property list 		
		Resty resty = new Resty();			//The entity of Resty Class, provides REST services
		Gson gson = new Gson();				//The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON representation
		JSONResource jsonResult;			//A resource presentation in JSON format
		String link;						//A string to keep the URL obtained from .properties file
		String jsonString;					//A string to keep JSON string 
		FileStructure file;					//An object of FileStructure to keep return value
		
		try {
			prop.load(new FileInputStream(proppath));
            link = prop.getProperty("file.url"); // receives the URL value from the .properties file
			
			jsonResult = resty.json(link + "/" + fileId, resty.delete()); //delete the specified file from the IFSService file system
			jsonString = jsonResult.toObject().toString();
			
			file = gson.fromJson(jsonString, FileStructure.class); // transforms JSON string into the FileStructure object
			return file;
		} 
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
