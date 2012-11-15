package pt.iflow.api.blocks;


/**
 * <p>Title: Attribute</p>
 * <p>Description: This class represents an attribute, with setters and getters for name and value </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: iKnow </p>
 * @author João Valentim
 * @version 1.0
 */

public class Attribute {

   private String name;
   private String value;

   public Attribute() {
   }

   public Attribute(String nm, String vl) {
      name  = nm;
      value = vl;
   }

   /**
    * Sets the attribute name.
    * @param nm the attribute name.
    */
   public void setName(String nm) {
      name = nm;
   }

   /**
    * Gets the attribute name.
    * @return the attribute name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the attribute value.
    * @param nm the attribute value.
    */
   public void setValue(String vl) {
      value = vl;
   }

   /**
    * Gets the attribute value.
    * @return the attribute value.
    */
   public String getValue() {
      return value;
   }
}

