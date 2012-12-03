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
package pt.iflow.api.core;

import java.util.List;
import java.util.Map;

import pt.iflow.api.processtype.ProcessDataType;

public interface ProcessCatalogue {
  public boolean isList(String var);

  public boolean hasVar(String var);

  public ProcessDataType getDataType(String var);

  public List<String> getSimpleVariableNames();

  public List<String> getListVariableNames();

  public List<String> getBindableVariableNames();

  public boolean hasPublicName(String var);

  public String getPublicName(String var);

  public String getDisplayName(String var);

  public String getDefaultValueExpression(String var);

  public boolean isSearchable(String var);

  public Map<String,Integer> getSearchables();

  public String[] getOrderedSearchableNames();
}
