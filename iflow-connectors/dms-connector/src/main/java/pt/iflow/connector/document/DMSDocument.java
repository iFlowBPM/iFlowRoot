package pt.iflow.connector.document;

import org.apache.commons.collections15.OrderedMap;

public interface DMSDocument extends Document {

  public String getScheme();

  public void setScheme(String Scheme);

  public String getAddress();

  public void setAddress(String address);

  public String getUuid();

  public void setUuid(String uuid);

  public String getPath();

  public void setPath(String path);

  public void addComments(OrderedMap<String, String> comments);

  public void addComment(String name, String value);

  public OrderedMap<String, String> getComments();

}
