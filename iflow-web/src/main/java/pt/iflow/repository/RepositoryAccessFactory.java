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
 * Created on Jan 12, 2006 by mach
 *
  */

package pt.iflow.repository;

import java.util.Properties;

import javax.sql.DataSource;

import pt.iflow.api.repository.RepositoryAccess;

public class RepositoryAccessFactory {
  
  public static final int TYPE_DEFAULT = 0;
  public static final int TYPE_MYSQL = 1;
  
  private static int getImplType(String typeStr) {
    int type = TYPE_DEFAULT;
    if (null == typeStr) return TYPE_DEFAULT;
    if("MYSQL".equals(typeStr))
      type = TYPE_MYSQL;
    else
      type = TYPE_DEFAULT;

    return type;
  }
  
  
  
  public static RepositoryAccess getRepositoryAccess(String implType,Properties props) {
    RepositoryAccess retObj = null;
    
    int type = getImplType(implType);
    
    switch(type) {
    case TYPE_DEFAULT:
      retObj = new RepositoryAccessOracleImpl(props);
      System.out.print("USING DEFAULT REP ACCESS");
      break;
    case TYPE_MYSQL:
      retObj = new RepositoryAccessMysqlImpl(props);
      System.out.print("USING MYSQL REP ACCESS");
      break;
    default:
      System.out.print("UNKNOWN DB TYPE. USING DEFAULT.");
      retObj = new RepositoryAccessOracleImpl(props);
    }
    
    return retObj;
  }

  public static RepositoryAccess getRepositoryAccess(String implType, DataSource ds) {
    RepositoryAccess retObj = null;
    int type = getImplType(implType);

    switch(type) {
    case TYPE_DEFAULT:
      retObj = new RepositoryAccessOracleImpl(ds);
      System.out.print("USING DEFAULT REP ACCESS");
      break;
    case TYPE_MYSQL:
      retObj = new RepositoryAccessMysqlImpl(ds);
      System.out.print("USING MYSQL REP ACCESS");
      break;
    default:
      System.out.print("UNKNOWN DB TYPE. USING DEFAULT.");
      retObj = new RepositoryAccessOracleImpl(ds);
    }
    
    return retObj;
  }
  
  

}
