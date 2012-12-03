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
package pt.iflow.chart.resources;

import java.io.InputStream;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.core.RepositoryFile;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.resources.ResourceLoader;

public class RepositoryLoader extends ResourceLoader {

  public InputStream getResource(ChartCtx ctx, String name) {
    InputStream result = null;
    Repository rep = BeanFactory.getRepBean();
    UserInfoInterface userInfo = (UserInfoInterface) ctx.get(Const.USER_INFO);
    try {
      RepositoryFile repFile = rep.getChartFile(userInfo, name);
      result = repFile.getResourceAsStream();
    } catch (Exception e) {
      result = null;
    }
    
    return result;
  }

}
