package pt.iflow.datasources;

import java.util.Set;

public interface DataSourceEntry {
  String getId();
  
  String getClassName();

  String getDriverAttr();
  
  String getUsernameAttr();

  //String getPasswordAttr();

  String getUrlAttr();
  
  String getDescription();

  Set<String> getDisabledAttrs();
}
