package pt.iflow.vfs.connector;

import java.io.IOException;

import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.google.gson.Gson;
import com.infosistema.fs.entity.FileStructure;
import com.infosistema.fs.entity.KeyStructure;
import com.infosistema.fs.entity.KeyValueStructureList;

public class RestInterface {

  String link; // A string to keep the URL obtained from .properties file

  public RestInterface(String link) {
    super();
    this.link = link;
  }

  /**
   * Gets the content of the key by the given id of this key.
   * 
   * @param keyId
   *          the key id of String type
   * @return the key of KeyStructure type
   */
  public KeyStructure getKey(String keyId) {

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    String jsonString; // A string to keep JSON string
    KeyStructure key; // An object of KeyStructure to keep return value

    try {
      jsonResult = resty.json(link + "/" + keyId); // gets the specified key content in a form of JSON string
      jsonString = jsonResult.object().toString();

      key = gson.fromJson(jsonString, KeyStructure.class); // transforms JSON string into the KeyStructure object
      return key;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
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

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    String jsonString; // A string to keep JSON string
    KeyStructure keys; // An object of KeyStructure to keep return value

    try {
      jsonResult = resty.json(link); // gets all keys content in a form of JSON string
      jsonString = jsonResult.object().toString();

      keys = gson.fromJson(jsonString, KeyStructure.class); // transforms JSON string into the KeyStructure object
      return keys;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Gets the file characteristics specified by fileId.
   * 
   * @param fileId
   *          the file id of String type
   * @return the file of FileStructure type
   */
  public FileStructure getFile(String fileId) {

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    String jsonString; // A string to keep JSON string
    FileStructure file; // An object of FileStructure to keep return value

    try {
      jsonResult = resty.json(link + "/file/" + fileId); // gets the specified file characteristics in a form of JSON string
      jsonString = jsonResult.object().toString();

      file = gson.fromJson(jsonString, FileStructure.class); // transforms JSON string into the FileStructure object
      return file;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Puts the specified file into the IFSService file system.
   * 
   * @param file
   *          the file characteristics of FileStructure type
   * @return the file of FileStructure type
   */
  public FileStructure putFile(FileStructure file) {

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    JSONObject jsonFile; // An unordered collection of name/value pairs which keep file characteristics
    String jsonString; // A string to keep JSON string

    try {
      jsonString = gson.toJson(file);
      jsonFile = new JSONObject(jsonString); // transforms JSON string into the JSON object to be put via REST

      jsonResult = resty.json(link, resty.put(resty.content(jsonFile))); // puts the specified file into the IFSService file system
      jsonString = jsonResult.object().toString();
      file = gson.fromJson(jsonString, FileStructure.class); // transforms JSON string into the FileStructure object
      return file;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return file;
  }

  /**
   * Deletes the file specified by fileId from the IFSService file system.
   * 
   * @param fileId
   *          the file id of String type
   * @return the file of FileStructure type
   */
  public FileStructure deleteFile(String fileId) {

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    String jsonString; // A string to keep JSON string
    FileStructure file; // An object of FileStructure to keep return value

    try {
      jsonResult = resty.json(link + "/" + fileId, resty.delete()); // delete the specified file from the IFSService file system
      jsonString = jsonResult.object().toString();

      file = gson.fromJson(jsonString, FileStructure.class); // transforms JSON string into the FileStructure object
      return file;
    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public KeyValueStructureList getKeyValue() {

    Resty resty = new Resty(); // The entity of Resty Class, provides REST services
    Gson gson = new Gson(); // The entity of Gson Class, Java library that can be used to convert Java Objects into their JSON
    // representation
    JSONResource jsonResult; // A resource presentation in JSON format
    String jsonString; // A string to keep JSON string
    KeyValueStructureList keyValues; // An object of KeyStructure to keep return value

    try {
      jsonResult = resty.json(link + "/rest/keyvalue"); // gets all keys content in a form of JSON string
      Object o = jsonResult.object();
      jsonString = jsonResult.object().toString();

      keyValues = gson.fromJson(jsonString, KeyValueStructureList.class); // transforms JSON string into the KeyStructure object
      return keyValues;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    RestInterface ri = new RestInterface("http://localhost:8080/IFSService");
    // KeyValueStructure[] keyValues = ri.getKeyValue();
  }
}
