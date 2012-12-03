/*****************************************************************************************
    Infosistema iFlow - workflow and BPM platform
    Copyright(C) 2002-2012 Infosistema, S.A.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

    www.infosistema.com
    iflow@infosistema.com
    Av. Jose Gomes Ferreira, 11 3rd floor, s.34
    Miraflores
    1495-139 Alges Portugal
****************************************************************************************/
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
