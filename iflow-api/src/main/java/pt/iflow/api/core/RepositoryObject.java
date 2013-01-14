package pt.iflow.api.core;

import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>Title: Attribute</p>
 * <p>Description: This class represents a info of a repository object, with setters and getters for id, parentid, name, value, data, modification, isfile, fullpath and propvalue </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: iKnow </p>
 * @author Campa
 * @version 1.0
 */

public class RepositoryObject {
	
	   private int id;
	   private int parentId;
	   private String sName;
	   private byte[] data;
	   private Timestamp modification;
	   private boolean isFile;
	   private String sFullPath;
	   private String sPropValue;

	   public RepositoryObject() {
	   }

	   public RepositoryObject(int aid, int aparentId, String asName, byte[] adata, 
			   Timestamp amodification, boolean aisFile, String asFullPath, String asPropValue) {
		   
		   id  = aid;
		   parentId = aparentId;
		   sName = asName;
		   data = adata;
		   modification = amodification;
		   isFile = aisFile;
		   sFullPath = asFullPath;
		   sPropValue = asPropValue;
	   }

	   /**
	    * Sets the attribute id.
	    * @param aid the attribute id.
	    */
	   public void setId(int aid) {
		   id  = aid;
	   }

	   /**
	    * Gets the attribute id.
	    * @return the attribute id.
	    */
	   public int getId() {
	      return id;
	   }
	   
	   /**
	    * Sets the attribute parentId.
	    * @param aparentId the attribute parentId.
	    */
	   public void setParentId(int aparentId) {
		   parentId  = aparentId;
	   }

	   /**
	    * Gets the attribute parentId.
	    * @return the attribute parentId.
	    */
	   public int getParentId() {
	      return parentId;
	   }

	   /**
	    * Sets the attribute sName.
	    * @param asName the attribute sName.
	    */
	   public void setName(String asName) {
		   sName = asName;
	   }

	   /**
	    * Gets the attribute sName.
	    * @return the attribute sName.
	    */
	   public String getName() {
	      return sName;
	   }
	   
	   /**
	    * Sets the attribute data.
	    * @param adata the attribute data.
	    */
	   public void setData(byte[] adata) {
		   data = adata;
	   }

	   /**
	    * Gets the attribute data.
	    * @return the attribute data.
	    */
	   public byte[] getData() {
	      return data;
	   }
	   
	   /**
	    * Sets the attribute modification.
	    * @param amodification the attribute modification.
	    */
	   public void setModification(Timestamp amodification) {
		   modification = amodification;
	   }

	   /**
	    * Gets the attribute modification.
	    * @return the attribute modification.
	    */
	   public Date getModification() {
	      return modification;
	   }

	   /**
	    * Sets the attribute isFile.
	    * @param aisFile the attribute isFile.
	    */
	   public void setIsFile(boolean aisFile) {
		   isFile = aisFile;
	   }

	   /**
	    * Gets the attribute isFile.
	    * @return the attribute isFile.
	    */
	   public boolean getIsFile() {
	      return isFile;
	   }
	   
	   /**
	    * Sets the attribute sFullPath.
	    * @param asFullPath the attribute sFullPath.
	    */
	   public void setFullPath(String asFullPath) {
		   sFullPath = asFullPath;
	   }

	   /**
	    * Gets the attribute sFullPath.
	    * @return the attribute sFullPath.
	    */
	   public String getFullPath() {
	      return sFullPath;
	   }
	   
	   /**
	    * Sets the attribute sPropValue.
	    * @param asPropValue the attribute sPropValue.
	    */
	   public void setPropValue(String asPropValue) {
		   sPropValue = asPropValue;
	   }

	   /**
	    * Gets the attribute sPropValue.
	    * @return the attribute sPropValue.
	    */
	   public String getPropValue() {
	      return sPropValue;
	   }
	   
	   public String toString() {
	     return getName();
	   }
	   
}
