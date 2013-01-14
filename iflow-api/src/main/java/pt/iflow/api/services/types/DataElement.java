/*
 * <p>Title: DataElement.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 27, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.api.services.types;

public class DataElement {
    public String name = "";
    public String value = "";
    public String type = "";
    
    public DataElement() {
      super();
    }
    
    public DataElement(String key, String value, String type) {
      this();
        this.name = key;
        this.value = value;
        this.type = type;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return Returns the type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return Returns the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param type The type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param value The value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
