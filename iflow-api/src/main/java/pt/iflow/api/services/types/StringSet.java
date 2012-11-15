/*
 * <p>Title: StringSet.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 30, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.api.services.types;

public class StringSet {
    public String[] result = null;

    public StringSet() {
      super();
    }

    /**
     * @return Returns the result.
     */
    public String[] getResult() {
        return result;
    }

    /**
     * @param result The result to set.
     */
    public void setResult(String[] result) {
        this.result = result;
    }
}
