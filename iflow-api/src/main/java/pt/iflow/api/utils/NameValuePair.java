package pt.iflow.api.utils;

public class NameValuePair<N, V> {
  private N name;
  private V value;

  public NameValuePair() {
  }
  
  public NameValuePair(N name) {
    this();
    this.name = name;
  }
  
  public NameValuePair(N name, V value) {
    this(name);
    this.value = value;
  }

  public N getName() {
    return this.name;
  }

  public V getValue() {
    return this.value;
  }

  public NameValuePair<N, V> setName(N name) {
    this.name = name;
    return this;
  }

  public NameValuePair<N, V> setValue(V value) {
    this.value = value;
    return this;
  }

  public NameValuePair<N, V> setPair(NameValuePair<N, V> nvp) {
    return set(nvp.getName(), nvp.getValue());
  }

  public NameValuePair<N, V> set(N name, V value) {
    this.name = name;
    this.value = value;
    return this;
  }
  
  @Override
  public String toString() {
    String retObj = "";
    if (this.name instanceof String) {
      retObj += (String) this.name;
    }
    if (this.value instanceof String) {
      if (this.name instanceof String) {
        retObj += "=";
      }
      retObj += (String) this.value;
    }
    return retObj;
  }
}
