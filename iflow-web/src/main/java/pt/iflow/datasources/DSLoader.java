package pt.iflow.datasources;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.Setup;
import pt.iflow.api.utils.Utils;

public class DSLoader {
  private static final String POOL_ELEM = "pool";
  public static final String POOL_IMPL_CLASS = "poolClassName";
  public static final String POOL_JNDI_NAME = "poolJNDIName";

  private static final String BASE_CONTEXT = Const.iFLOW_DATASOURCE_CTX;

  private static final Pattern DATA_SOURCE_CFG_RE = Pattern.compile("datasource\\.impl\\.(\\w+)\\.(\\w+)");
  private static final String DATA_SOURCE_CFG_CLASS_NAME = "class";
  private static final String DATA_SOURCE_CFG_DRIVER_ATTR = "driverAttr";
  private static final String DATA_SOURCE_CFG_USERNAME_ATTR = "usernameAttr";
  private static final String DATA_SOURCE_CFG_PASSWORD_ATTR = "passwordAttr";
  private static final String DATA_SOURCE_CFG_URL_ATTR = "urlAttr";
  private static final String DATA_SOURCE_CFG_DISABLE_ATTR = "disableAttrs";
  private static final String DATA_SOURCE_CFG_DESCRIPTION = "description";

  private static final Pattern DRIVER_CFG_RE = Pattern.compile("jdbc\\.driver\\.(\\w+)\\.(\\w+)");
  private static final String DRIVER_CFG_CLASS_NAME = "class";
  private static final String DRIVER_CFG_URL = "url";
  private static final String DRIVER_CFG_DESCRIPTION = "description";

  private static DSLoader instance = null;

  public static DSLoader getInstance() {
    if (null == instance)
      instance = new DSLoader();
    return instance;
  }

  private static interface Converter {
    Object convert(String value);
  }

  private static class DataSourceEntryImpl implements DataSourceEntry {
    private String className = null;
    private String driverAttr = null;
    private String urlAttr = null;
    private String description = null;
    private String id = null;
    private Set<String> disabledAttrs = new HashSet<String>();
    private String usernameAttr = null;
    private String passwordAttr = null;

    public String getId() {
      return id;
    }
    
    public String getClassName() {
      return className;
    }

    public Set<String> getDisabledAttrs() {
      return disabledAttrs;
    }

    public String getDriverAttr() {
      return driverAttr;
    }
    
    public String getUrlAttr() {
      return urlAttr;
    }
    
    public String getDescription() {
      return description;
    }
    
    public String toString() {
      return "Class: "+getClassName()+"; Driver Attr: "+getDriverAttr()+"; Disabled atts: "+getDisabledAttrs();
    }
    
    public String getUsernameAttr() {
      return usernameAttr;
    }
    
    //public String getPasswordAttr() {
    //  return passwordAttr;
    //}

  }

  private static class DriverEntryImpl implements DriverEntry {
    private String className = null;
    private String url = null;
    private String description = null;
    private String id = null;

    public String getId() {
      return id;
    }
    
    public String getClassName() {
      return className;
    }

    public String getUrl() {
      return url;
    }

    public String getDescription() {
      return description;
    }
    
    public String toString() {
      return "Driver: "+getClassName()+"; URL: "+getUrl();
    }
  }

  private static final Map<Class<?>, Converter> types = new HashMap<Class<?>, Converter>();
  static {

    types.put(String.class, new Converter() {
      public Object convert(String value) {
        return value;
      }
    });

    Converter conv = null;
    types.put(Byte.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Byte(value);
      }
    });
    types.put(byte.class, conv);

    types.put(Character.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Character(value.charAt(0));
      }
    });
    types.put(byte.class, conv);

    types.put(Short.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Short(value);
      }
    });
    types.put(short.class, conv);

    types.put(Integer.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Integer(value);
      }
    });
    types.put(int.class, conv);

    types.put(Long.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Long(value);
      }
    });
    types.put(long.class, conv);

    types.put(Float.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Float(value);
      }
    });
    types.put(float.class, conv);

    types.put(Double.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Double(value);
      }
    });
    types.put(double.class, conv);

    types.put(Boolean.class, conv = new Converter() {
      public Object convert(String value) {
        return StringUtils.isBlank(value) ? null : new Boolean(value);
      }
    });
    types.put(boolean.class, conv);

  }

  private static boolean isTypeSupported(Class<?> type) {
    return types.containsKey(type);
  }

  private Map<String, List<Properties>> dataSources = new HashMap<String, List<Properties>>();

  private DSLoader() {
    try {
      parseXML();
    } catch (Exception e) {
      Logger.warning(null, this, "<init>", "Error loading datasource configuration", e);
    }
  }

  public Map<String, DataSourceEntry> getRegisteredDataSources() {
    Map<String, DataSourceEntry> dsImplementations = new HashMap<String, DataSourceEntry>();
    Properties propsPoolImpl = new Properties();
    InputStream is = null;
    try {
        is = Setup.getResource("pool_config.properties");
        propsPoolImpl.load(is);
      } catch (IOException e) {
  	} finally {
  		if( is != null) Utils.safeClose(is);
  	}    

    Set<String> toRemove = new HashSet<String>();

    for (Object key : propsPoolImpl.keySet()) {
      String prop = (String) key;
      Matcher m = DATA_SOURCE_CFG_RE.matcher(prop);
      if (!m.matches())
        continue;
      String dsId = m.group(1);
      String dsProp = m.group(2);

      DataSourceEntryImpl cfg = (DataSourceEntryImpl) dsImplementations.get(dsId);
      if (null == cfg) {
        cfg = new DataSourceEntryImpl();
        cfg.id = dsId;
        dsImplementations.put(dsId, cfg);
        toRemove.add(dsId);
      }

      if (DATA_SOURCE_CFG_CLASS_NAME.equals(dsProp)) {
        String className = propsPoolImpl.getProperty(prop);
        if (StringUtils.isNotBlank(className)) {
          cfg.className = className;
          toRemove.remove(dsId);
        }
      } else if (DATA_SOURCE_CFG_DRIVER_ATTR.equals(dsProp)) {
        cfg.driverAttr = propsPoolImpl.getProperty(prop);
      } else if (DATA_SOURCE_CFG_USERNAME_ATTR.equals(dsProp)) {
        cfg.usernameAttr = propsPoolImpl.getProperty(prop);
      } else if (DATA_SOURCE_CFG_PASSWORD_ATTR.equals(dsProp)) {
        cfg.passwordAttr = propsPoolImpl.getProperty(prop);
      } else if (DATA_SOURCE_CFG_URL_ATTR.equals(dsProp)) {
        cfg.urlAttr = propsPoolImpl.getProperty(prop);
      } else if (DATA_SOURCE_CFG_DESCRIPTION.equals(dsProp)) {
        cfg.description = propsPoolImpl.getProperty(prop);
      } else if (DATA_SOURCE_CFG_DISABLE_ATTR.equals(dsProp)) {
        String disabledAttrs = propsPoolImpl.getProperty(prop);
        if (StringUtils.isNotBlank(disabledAttrs)) {
          String[] disabled = disabledAttrs.split("\\s*,\\s*");
          for (String d : disabled)
            cfg.disabledAttrs.add(d);
        }
      }
    }

    // remove entries without driver
    for (String key : toRemove)
      dsImplementations.remove(key);

    // Reindex map by class name
    Set<String> keys = new HashSet<String>(dsImplementations.keySet());
    for(String key : keys) {
      DataSourceEntry value = dsImplementations.get(key);
      dsImplementations.put(value.getClassName(), value);
      dsImplementations.remove(key);
    }
      
    return dsImplementations;
  }

  public Map<String, DriverEntry> getRegisteredJdbcDrivers() {
    Map<String, DriverEntry> registeredDrivers = new HashMap<String, DriverEntry>();
    Properties propsPoolImpl = new Properties();
    InputStream is = null;
    try {
      is = Setup.getResource("pool_config.properties");
      propsPoolImpl.load(is);
    } catch (IOException e) {
	} finally {
		if( is != null) Utils.safeClose(is);
	}    

    Set<String> toRemove = new HashSet<String>();

    for (Object key : propsPoolImpl.keySet()) {
      String prop = (String) key;
      Matcher m = DRIVER_CFG_RE.matcher(prop);
      if (!m.matches())
        continue;
      String dsId = m.group(1);
      String dsProp = m.group(2);

      DriverEntryImpl cfg = (DriverEntryImpl) registeredDrivers.get(dsId);
      if (null == cfg) {
        cfg = new DriverEntryImpl();
        cfg.id = dsId;
        registeredDrivers.put(dsId, cfg);
        toRemove.add(dsId);
      }

      if (DRIVER_CFG_CLASS_NAME.equals(dsProp)) {
        String className = propsPoolImpl.getProperty(prop);
        if (StringUtils.isNotBlank(className)) {
          cfg.className = className;
          toRemove.remove(dsId);
        }
      } else if (DRIVER_CFG_URL.equals(dsProp)) {
        cfg.url = propsPoolImpl.getProperty(prop);
      } else if (DRIVER_CFG_DESCRIPTION.equals(dsProp)) {
        cfg.description = propsPoolImpl.getProperty(prop);
      }
    }

    // remove entries without driver
    for (String key : toRemove)
      registeredDrivers.remove(key);

    // Reindex map by class name
    Set<String> keys = new HashSet<String>(registeredDrivers.keySet());
    for(String key : keys) {
      DriverEntry value = registeredDrivers.get(key);
      registeredDrivers.put(value.getClassName(), value);
      registeredDrivers.remove(key);
    }

    return registeredDrivers;
  }

  private class DSLoaderHandler extends DefaultHandler {
    List<Properties> config = new ArrayList<Properties>();
    Properties props = null;
    StringBuilder sb = null;
    int depth = 0;
    int pooldepth = 0;
    String propName = null;

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      depth++;
      if (POOL_ELEM.equalsIgnoreCase(name)) {
        // got one!
        props = new Properties();
        int size = attributes.getLength();
        for (int i = 0; i < size; i++) {
          String propName = attributes.getQName(i);
          if (null == propName)
            continue;
          String propValue = attributes.getValue(i);
          if (null == propValue)
            continue;
          props.put(propName, propValue);
        }
        pooldepth = depth;
      } else if (depth == pooldepth + 1) {
        sb = new StringBuilder();
        propName = name;
      } else {
        // ignorar tags dentro de tags
        sb = null;
        propName = null;
      }
    }

    public void endElement(String uri, String localName, String name) throws SAXException {
      if (POOL_ELEM.equalsIgnoreCase(name)) {
        pooldepth = 0;
        config.add(props);
        props = null;
      } else if (depth == pooldepth + 1) {
        if (sb != null && propName != null && props != null) {
          props.setProperty(propName, sb.toString());
        }
      }

      sb = null;
      propName = null;
      depth--;
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
      if (sb != null)
        sb.append(ch, start, length);
    }

  }

  private void parseXML() throws IOException, ParserConfigurationException, SAXException {

    InputStream in = Setup.getResource("ds.xml");
    if (null == in)
      return; // no pool configured

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(false);
    factory.setValidating(false);
    factory.setXIncludeAware(false);

    SAXParser parser = factory.newSAXParser();
    DSLoaderHandler dh = new DSLoaderHandler();

    try {
		parser.parse(in, dh);
	} finally {
		if( in != null) Utils.safeClose(in);
	}    

    dataSources.put(Const.SYSTEM_ORGANIZATION, dh.config);

  }

  private void writeXML() {
    StringBuilder sb = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ds>\n");

    for (String orgid : dataSources.keySet()) {
      List<Properties> pools = dataSources.get(orgid);
      sb.append("\t<pools organization=\"").append(StringEscapeUtils.escapeXml(orgid)).append("\">\n");

      for (Properties pool : pools) {
        sb.append("\t\t<pool");

        for (Object key : pool.keySet()) {
          String name = (String) key;
          if (null == name)
            continue;
          String value = pool.getProperty(name);
          if (null == value)
            continue;
          sb.append(" ").append(name).append("=\"").append(StringEscapeUtils.escapeXml(value)).append("\"");
        }
        sb.append(" />\n");
      }

      sb.append("\t</pools>\n");
    }
    sb.append("</ds>\n");

    byte[] data = null;
    try {
      data = sb.toString().getBytes("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("There is no UTF-8?", e);
    }
    Setup.storeResource("ds3.xml", new ByteArrayInputStream(data));
  }

  private void loadPools(Context jdbcCtx) throws DSLoaderException {
    List<Properties> pools = dataSources.get("1");

    for (Properties pool : pools) {
      try {
        String sImplClass = pool.getProperty(POOL_IMPL_CLASS);
        String sJNDIName = pool.getProperty(POOL_JNDI_NAME);
        if (StringUtils.isBlank(sImplClass)) {
          // issue an error to the log and continue
          continue;
        }

        if (StringUtils.isBlank(sJNDIName)) {
          // issue an error to the log and continue
          continue;
        }

        NameParser nParser = jdbcCtx.getNameParser("");
        Name name = nParser.parse(sJNDIName);
        int nameSize = name.size();
        if(nameSize>1) {
          // criar os subcontextos consoante for necessario
          Context subCtx = jdbcCtx;
          for(int i = 0; i < nameSize-1; i++) {
            try {
              subCtx = (Context) subCtx.lookup(name.get(i));
            } catch(NameNotFoundException e) {
              subCtx = subCtx.createSubcontext(name.get(i));
            }
          }
        }
        
        if(null != jdbcCtx) {
        try {
          jdbcCtx.lookup(sJNDIName);
          throw new DSLoaderException("JNDI name is in use.");
        } catch (NameNotFoundException e) {
        }

        Class<?> poolImpl = loadDataSourceClass(sImplClass);

        Object instance = poolImpl.newInstance();

        loadObjectProperties(instance, pool);

        jdbcCtx.bind(sJNDIName, new IFlowDataSource(instance));
        }
      } catch (NamingException e) {
        throw new DSLoaderException("Error registering pool", e);
      } catch (ClassNotFoundException e) {
        throw new DSLoaderException("Pool implementation not found", e);
      } catch (InstantiationException e) {
        throw new DSLoaderException("Could not instantiate pool", e);
      } catch (IllegalAccessException e) {
        throw new DSLoaderException("Cannot access pool implementation", e);
      } catch (ClassCastException e) {
        throw new DSLoaderException("Pool is not instance of DataSource?", e);
      }
    }
  }

  private void loadObjectProperties(Object bean, Properties pool) {
    for (Object key : pool.keySet()) {
      String name = (String) key;
      try {
        String value = pool.getProperty(name);
        Class<?> typeClass = PropertyUtils.getPropertyType(bean, name);
        if (isTypeSupported(typeClass))
          PropertyUtils.setProperty(bean, name, types.get(typeClass).convert(value));
      } catch (Exception e) {
      }
    }
  }

  private void load(Context ctx) throws DSLoaderException {
    try {
      DSLoader loader = new DSLoader();
      loader.parseXML();
      loader.loadPools(ctx);
    } catch (Exception e) {
      throw new DSLoaderException(e);
    }
  }

  private Class<?> loadDataSourceClass(String className) throws ClassNotFoundException {
    Repository rep = BeanFactory.getRepBean();
    Class<?> cl = null;
    try {
      cl = rep.loadClass(Const.SYSTEM_ORGANIZATION, className);
    } catch (ClassNotFoundException e) {
      cl = Thread.currentThread().getContextClassLoader().loadClass(className);
    }
    return cl;
  }
  /*
   *        at pt.iflow.datasources.DSLoader.unbindContext(DSLoader.java:583)
       at pt.iflow.datasources.DSLoader.unbindContext(DSLoader.java:587)
   */

  public void start() {
    if(Const.DISABLE_DATASOURCE_MANAGEMENT) return;
    try {
      InitialContext ic = new InitialContext();
      Context ctx = null;
      try {
        ctx = (Context) ic.lookup(BASE_CONTEXT);
      } catch (NamingException e) {
        ctx = ic.createSubcontext(BASE_CONTEXT);
      }
      load(ctx);

    } catch (Exception e) {
      Logger.error(null, this, "start", "Error deploying managed datasources", e);
    }
  }

  private void unbindContext(Context ctx) {
    if (null == ctx)
      return;
    try {
      Map<String,Context> subCtxs = new HashMap<String,Context>();
      Set<String> subObjs = new HashSet<String>();
      NamingEnumeration<Binding> ctxNames = ctx.listBindings("");
      while (ctxNames.hasMore()) {
        Binding binding = ctxNames.next();
        String name = binding.getName();
        Object obj = binding.getObject();
        if (obj instanceof Context) {
          subCtxs.put(name,(Context)obj);
        } else {
          subObjs.add(name);
        }

      }
      
      // kill objects
      for(String name : subObjs) {
        try {
          ctx.unbind(name);
        } catch (NamingException ex) {
          Logger.warning(null, this, "unbindContext", "Error unbinding object '"+name+"'", ex);
        }
      }
      
      for(Map.Entry<String,Context> entry : subCtxs.entrySet()) {
        unbindContext(entry.getValue());
        try {
          ctx.destroySubcontext(entry.getKey());
        } catch (NamingException e) {
          Logger.warning(null, this, "unbindContext", "Error destroying context '"+entry.getKey()+"'", e);
        }
      }
      subCtxs = null;
      subObjs = null;
    } catch (NamingException e) {
      Logger.error(null, this, "unbindContext", "Error listing context entries", e);
    }
  }

  public void stop() {

    if(Const.DISABLE_DATASOURCE_MANAGEMENT) return;

    try {
      InitialContext ic = new InitialContext();

      try {
        Context ctx = (Context) ic.lookup(Const.iFLOW_DATASOURCE_CTX);
        unbindContext(ctx);
      } catch (NameNotFoundException e) {
        // ignore
      } catch (NamingException e) {
        Logger.warning(null, this, "stop", "Exception during JNDI cleanup", e);
      }
      try {
        ic.destroySubcontext(Const.iFLOW_DATASOURCE_CTX);
      } catch (NamingException e) {
        Logger.warning(null, this, "stop", "Exception removing iFlow JNDI context", e);
      }
    } catch (Exception e) {
      Logger.error(null, this, "stop", "Exception during JNDI cleanup", e);
    }

  }
  
  public List<Properties> getPoolsByOrganization(String organization) {
    if(StringUtils.isBlank(organization)) organization = Const.SYSTEM_ORGANIZATION;
    
    ArrayList<Properties> allDs = new ArrayList<Properties>(dataSources.get(organization));
    for(int i = 0; i < allDs.size(); i++) {
      Properties pool = new Properties();
      pool.putAll(allDs.get(i));
      allDs.set(i, pool);
    }
    
    return Collections.unmodifiableList(allDs);
  }

  public void setPoolByOrganization(String organization, Properties pool) {
    // return Collections.unmodifiableList(dataSources.get(organization));
    // TODO implement
    
    writeXML();
  }

  public Map<String, Class<?>> getDataSourceProperties(String dsClassName) throws DSLoaderException {

    DataSourceEntry cfg = getRegisteredDataSources().get(dsClassName);
    if (null == cfg)
      throw new DSLoaderException("DataSource implementation not registered.");

    Class<?> cc;
    try {
      cc = loadDataSourceClass(dsClassName);
    } catch (ClassNotFoundException e) {
      throw new DSLoaderException("Class not available: " + dsClassName, e);
    }

    if (!DataSource.class.isAssignableFrom(cc))
      throw new DSLoaderException("Class " + dsClassName + " is not an instance of DataSource");

    Map<String, Class<?>> results = new TreeMap<String, Class<?>>();

    PropertyDescriptor[] descrs = PropertyUtils.getPropertyDescriptors(cc);

    for (PropertyDescriptor d : descrs) {
      Method w = d.getWriteMethod();
      Method r = d.getReadMethod();
      if (null == r || null == w) {
      } else if (isTypeSupported(d.getPropertyType()) && !cfg.getDisabledAttrs().contains(d.getName())) {
        results.put(d.getName(), d.getPropertyType());
      }
    }

    return results;
  }

  // TODO agarrar na template de URL dos drivers e criar um wizard para configurar a ligação à BD
  
}
