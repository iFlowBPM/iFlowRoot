package pt.iflow.applet;

import java.applet.Applet;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSObject {
  private static final Log log = LogFactory.getLog(JSObject.class);

  private static final Class<?> jsObjectClass;
  private static final Method getWindow;
  private static final Method call;
  private static final Method eval;
  private static final Method getMember;
  private static final Method setMember;
  private static final Method getSlot;
  private static final Method setSlot;
  private static final Method removeMember;

  static {
    Class<?> jsoClass = null;
    Method getWindowM = null, callM = null, evalM = null, getMemberM = null, setMemberM = null, getSlotM = null, setSlotM = null, removeMemberM = null;

    try {
      jsoClass = Class.forName("netscape.javascript.JSObject"); //$NON-NLS-1$

      Method ms[] = jsoClass.getMethods();
      for (int i = 0; i < ms.length; i++) {
        if (ms[i].getName().compareTo("getWindow") == 0) //$NON-NLS-1$
          getWindowM = ms[i];
        else if (ms[i].getName().compareTo("getSlot") == 0) //$NON-NLS-1$
          getSlotM = ms[i];
        else if (ms[i].getName().compareTo("setSlot") == 0) //$NON-NLS-1$
          setSlotM = ms[i];
        else if (ms[i].getName().compareTo("getMember") == 0) //$NON-NLS-1$
          getMemberM = ms[i];
        else if (ms[i].getName().compareTo("setMember") == 0) //$NON-NLS-1$
          setMemberM = ms[i];
        else if (ms[i].getName().compareTo("removeMember") == 0) //$NON-NLS-1$
          removeMemberM = ms[i];
        else if (ms[i].getName().compareTo("call") == 0) //$NON-NLS-1$
          callM = ms[i];
        else if (ms[i].getName().compareTo("eval") == 0) //$NON-NLS-1$
          evalM = ms[i];
      }

    } catch (Throwable t) {
      log.error("Could not load netscape.javascript.JSObject class", t); //$NON-NLS-1$
    }

    jsObjectClass = jsoClass;
    getWindow = getWindowM;
    call = callM;
    eval = evalM;
    getMember = getMemberM;
    setMember = setMemberM;
    getSlot = getSlotM;
    setSlot = setSlotM;
    removeMember = removeMemberM;

  }

  private Object jsObj;
  private JSObject(Object jsObj) {
    if(null == jsObj) throw new IllegalArgumentException("jsObject cannot be null"); //$NON-NLS-1$
    if(!jsObjectClass.isInstance(jsObj))
      throw new IllegalArgumentException("jsObject must be an instance of netscape.javascript.JSObject"); //$NON-NLS-1$
    this.jsObj = jsObj;
  }

  protected Object getJSObject() {
    return jsObj;
  }

  public static JSObject getWindow(Applet applet) {
    if(applet != null && getWindow != null) {
      try {
        return new JSObject(getWindow.invoke(null, applet));
      } catch (Exception e) {
        log.error("Error retrieving window object"); //$NON-NLS-1$
      }
    }
    return null;
  }

  public Object call(String function, Object ... arguments) {
    if(call == null || function == null) return null;

    // prepare and unbox if necessary
    Object[] args = null;
    if(null != arguments) {
      args = new Object[arguments.length];
      for(int i = 0; i < arguments.length; i++) {
        Object obj = arguments[i];
        if(obj != null && obj instanceof JSObject)
          obj = ((JSObject)obj).getJSObject();
        args[i] = obj; 
      }
    }

    Object obj = null;
    try {
      obj = call.invoke(getJSObject(), function, args);
      if(obj != null && jsObjectClass.isInstance(obj))
        obj = new JSObject(obj);
    } catch (Exception e) {
      log.error("Error invoking call", e); //$NON-NLS-1$
    }
    return obj;
  }

  public Object eval(String script) {
    if(eval == null || script == null) return null;
    Object obj = null;
    try {
      obj = eval.invoke(getJSObject(), script);
      if(obj != null && jsObjectClass.isInstance(obj))
        obj = new JSObject(obj);
    } catch (Exception e) {
      log.error("Error invoking eval", e); //$NON-NLS-1$
    }
    return obj;
  }

  public Object getMember (String name){
    if(getMember == null || name == null) return null;
    Object obj = null;
    try {
      obj = getMember.invoke(getJSObject(), name);
      if(obj != null && jsObjectClass.isInstance(obj))
        obj = new JSObject(obj);
    } catch (Exception e) {
      log.error("Error invoking getMember", e); //$NON-NLS-1$
    }
    return obj;
  }

  public void setMember (String name, Object value) {
    if(setMember == null || name == null) return;
    try {
      if(value != null && value instanceof JSObject)
        value = ((JSObject)value).getJSObject();
      setMember.invoke(getJSObject(), name, value);
    } catch (Exception e) {
      log.error("Error invoking setMember", e); //$NON-NLS-1$
    }
  }
  
  public void removeMember (String name){
    if(removeMember == null || name == null) return;
    try {
      removeMember.invoke(getJSObject(), name);
    } catch (Exception e) {
      log.error("Error invoking removeMember", e); //$NON-NLS-1$
    }
  }
  public Object getSlot (int index) {
    if(getSlot == null || index < 0) return null;
    Object obj = null;
    try {
      obj = getSlot.invoke(getJSObject(), index);
      if(obj != null && jsObjectClass.isInstance(obj))
        obj = new JSObject(obj);
    } catch (Exception e) {
      log.error("Error invoking getSlot", e); //$NON-NLS-1$
    }
    return obj;
  }
  
  public void setSlot (int index, Object value) {
    if(setSlot == null || index < 0) return;
    try {
      if(value != null && value instanceof JSObject)
        value = ((JSObject)value).getJSObject();
      setSlot.invoke(getJSObject(), index, value);
    } catch (Exception e) {
      log.error("Error invoking setSlot", e); //$NON-NLS-1$
    }
  }

  public boolean equals (Object obj) {
    if(obj == null) return false;
    if(!(obj instanceof JSObject)) return false;
    return getJSObject().equals(((JSObject)obj).getJSObject());
  };

  public String toString() {
    return getJSObject().toString();
  }
}
