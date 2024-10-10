package pt.iflow.core;

import java.io.InvalidObjectException;
import java.io.ObjectInputValidation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import pt.iflow.api.utils.Logger;

public class SessionObject implements Serializable, ObjectInputValidation
{
	
	  private static List<String> keys = new ArrayList<String>();
	  private static String FILTERLABEL = "filterlabel";
	  private static String FILTERDAYS = "filterdays";
	  private static String FILTERFOLDER = "filterfolder";
	  private static String FILTRO_SCROLL = "filtro_scroll";
	  private static String FILTRO_PID = "filtro_pid";
	  private static String FILTRO_SUBPID = "filtro_subpid";
	  private static String FILTRO_SHOWFLOWID = "filtro_showflowid";
	  private static String FILTRO_PNUMBER = "filtro_pnumber";
	  private static String FILTRO_ORDER = "filtro_order";
	  private static String FILTRO_NITEMS = "filtro_nItems";
	  private static String FILTRO_STARTINDEX = "filtro_startindex";
	  private static String FILTRO_NEXTSTARTINDEX = "filtro_nextstartindex";
	  private static String FILTRO_DTBEFORE = "filtro_dtBefore";
	  private static String FILTRO_DTAFTER = "filtro_dtAfter";
	  private static String ACTIVITY_CONFIG = "ACTIVITY_CONFIG";
	  private static String ACTIVITY_INDEX = "ACTIVITY_INDEX";
	  private static String ACTIVITY_BATCH = "ACTIVITY_BATCH";
	  
	  static {
	    keys.add(FILTERLABEL);
	    keys.add(FILTERDAYS);
	    keys.add(FILTERFOLDER);
	    keys.add(FILTRO_SCROLL);
	    keys.add(FILTRO_PID);
	    keys.add(FILTRO_SUBPID);
	    keys.add(FILTRO_SHOWFLOWID);
	    keys.add(FILTRO_PNUMBER);
	    keys.add(FILTRO_ORDER);
	    keys.add(FILTRO_NITEMS);
	    keys.add(FILTRO_STARTINDEX);
	    keys.add(FILTRO_NEXTSTARTINDEX);
	    keys.add(FILTRO_DTBEFORE);
	    keys.add(FILTRO_DTAFTER);
	    keys.add(ACTIVITY_CONFIG);
	    keys.add(ACTIVITY_INDEX);
	    keys.add(ACTIVITY_BATCH);
	  }
	  
	  
	private Object[][] valores;
	
	public SessionObject( HttpSession session )
	{
	    Object[][] valores = new Object[keys.size()][2];
	    int i = 0;

	    for (String key : keys) {
	      valores[i][0] = key;
	      valores[i++][1] = session.getAttribute(key);
	    }
	    
	    this.valores = valores;
	}

	@Override
	public void validateObject() throws InvalidObjectException 
	{
		Logger.trace(this, "SessionObject.validateObject", "validating SessionObject");
	}

	public Object[][] getValores() {
		return valores;
	}

	public void setValores(Object[][] valores) {
		this.valores = valores;
	}
	
	

}
