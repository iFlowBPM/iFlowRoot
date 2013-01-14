/*
 * <p>Title: DataElement.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 27, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.api.services.types;

public class DataElementSet {
    public DataElement[] result = null;
    
    public DataElementSet() {
      super();
    }

    /**
     * @return Returns the result.
     */
    public DataElement[] getResult() {
        return result;
    }

    /**
     * @param result The result to set.
     */
    public void setResult(DataElement[] result) {
        this.result = result;
    }
}
