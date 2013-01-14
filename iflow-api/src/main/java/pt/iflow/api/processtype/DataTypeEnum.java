package pt.iflow.api.processtype;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import pt.iflow.api.utils.Logger;

public enum DataTypeEnum {
  Text(TextDataType.class,Features.Single),
  Integer(IntegerDataType.class,Features.Single),
  Float(FloatDataType.class,Features.Single),
  Date(DateDataType.class,Features.Single),
  TextArray(TextDataType.class,Features.List),
  IntegerArray(IntegerDataType.class,Features.List),
  FloatArray(FloatDataType.class,Features.List),
  DateArray(DateDataType.class,Features.List),
  Document(DocumentDataType.class,Features.List);

  private final Class<? extends ProcessDataType> type;
  private final Features feature;
  private DataTypeEnum(Class<? extends ProcessDataType> type, Features feature) {
    this.type = type;
    this.feature = feature;
  }

  public Class<? extends ProcessDataType> getTypeClass() {
    return this.type;
  }

  public boolean isList() {
    return this.feature.isList;
  }

  public boolean isSingle() {
    return this.feature.isSingle;
  }

  public ProcessDataType newDataTypeInstance() {
    ProcessDataType instance = null;
    try {
      instance = this.type.newInstance();
    } catch (InstantiationException e) {
      // TODO Handle this
    } catch (IllegalAccessException e) {
      // TODO Handle this
    }
    return instance;
  }

  public ProcessDataType newDataTypeInstance(Object format) {
    ProcessDataType retObj = null;
    if(format != null) {
      Class<? extends Object> fmtClass = null;
      try {
        fmtClass = ClassLoader.getSystemClassLoader().loadClass("" + format);
      } catch (ClassNotFoundException e1) {
        fmtClass = format.getClass();
      }
      try {
        // check if there is a constructor for this argument class
        retObj = this.type.getConstructor(fmtClass).newInstance(format);
      } catch (Exception e) {
      }
      if(retObj == null) {
        // search for suitable constructor
        Constructor<?>[] constructors = this.type.getConstructors();
        for(Constructor<?> constructor: constructors) {
          Class<? extends Object>[] paramTypes = constructor.getParameterTypes();
          if(paramTypes.length != 1) {
            continue;
          }
          Class<? extends Object> paramType = paramTypes[0];
          if(paramType.isAssignableFrom(fmtClass)) {
            try {
              retObj = (ProcessDataType) constructor.newInstance(format);
              break;
            } catch (Exception e) {
              continue;
            }
          }
        }
      }
    }
    if(retObj == null) {
      retObj = newDataTypeInstance();
      Logger.warning("", this, "newDataTypeInstance", "Error: Unable to process format '" + format + "', defaulted to '" + retObj + "'.");
    }
    return retObj;
  }

  private final static Map<String,DataTypeEnum> deprecatedDataTypes = new HashMap<String,DataTypeEnum>();
  static {
    // From DataSet....
    deprecatedDataTypes.put("java.lang.String", DataTypeEnum.Text);
    deprecatedDataTypes.put("double", DataTypeEnum.Float);
    deprecatedDataTypes.put(String[].class.toString(), DataTypeEnum.TextArray);
    deprecatedDataTypes.put(double[].class.toString(), DataTypeEnum.FloatArray);
    deprecatedDataTypes.put(String[].class.getName(), DataTypeEnum.TextArray);
    deprecatedDataTypes.put(double[].class.getName(), DataTypeEnum.FloatArray);
  }


  /**
   * Retrive the corresponding enum type. If enum name is not found, try old catalog types. If none found, return Text.
   * 
   * @see valueOf
   * @param name
   * @return
   */
  public static DataTypeEnum getDataType(String name) {
    try {
      return valueOf(name);
    } catch(IllegalArgumentException actual) {
      DataTypeEnum type = deprecatedDataTypes.get(name);
      if(null == type) type = Text;
      return type;
    }
  }


  private static enum Features {
    Single(true,false),
    List(false,true),
    @SuppressWarnings("unused")
    Mixed(true,true);

    private final boolean isSingle;
    private final boolean isList;

    private Features(boolean isSingle, boolean isList) {
      this.isSingle = isSingle;
      this.isList = isList;
    }
  }

}
