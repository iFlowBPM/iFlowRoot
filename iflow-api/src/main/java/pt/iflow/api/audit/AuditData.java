/*
 *
 * Created on Feb 1, 2007 by iKnow
 *
  */

package pt.iflow.api.audit;


public class AuditData {

 protected String _sName; 
 protected String _sValue; 
 protected String _sDisplayName;
 
 public AuditData() {
   this(null,null,null);
 }
 
 public AuditData(String asName, String asValue) {
   this(asName, asValue, asName);
 }
 
 public AuditData(String asName, String asValue, String asDisplayName) {
   _sName = asName;
   _sValue = asValue;
   _sDisplayName = asDisplayName;
 }
 
 public String getName() {
   return _sName;
 }

 public void setName(String asName) {
   _sName = asName;
 }
 
 public String getValue() {
   return _sValue;
 }
 
 public void setValue(String asValue) {
   _sValue = asValue;
 }
 
 public String getDisplayName() {
   return _sDisplayName;
 }
 
 public void setDisplayName(String asDisplayName) {
   _sDisplayName = asDisplayName;
 }
 
 public String toString() {
   if(this._sDisplayName == null) {
     return this._sName + "=" + this._sValue;      
   } else {
     return "(" + this._sDisplayName + ")" + this._sName + "=" + this._sValue; 
   }
 }
}
