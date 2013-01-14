package pt.iflow.api.db;

import java.util.Collection;

public interface DBQueryEngineInterface {
	
	public String processQuery(String queryName,Collection params);
	

}
