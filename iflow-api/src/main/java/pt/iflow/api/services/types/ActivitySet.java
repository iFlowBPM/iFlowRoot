/*
 * <p>Title: DataElement.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) Sep 27, 2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.api.services.types;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import pt.iflow.api.core.Activity;

public class ActivitySet {
    public Activity[] result = null;

    public ActivitySet() {
      super();
    }

    /**
     * @return Returns the result.
     */
    public Activity[] getResult() {
        return result;
    }

    /**
     * @param result The result to set.
     */
    public void setResult(Activity[] result) {
        this.result = result;
    }   
}
