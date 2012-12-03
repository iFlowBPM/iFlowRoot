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
package pt.iflow.chart.style;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import pt.iflow.api.core.BeanFactory;
import pt.iflow.api.core.Repository;
import pt.iflow.api.utils.Const;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iknow.chart.ChartCtx;
import pt.iknow.chart.style.ProxyLoader;
import pt.iknow.chart.style.StyleLoader;

public class RepositoryStyleLoader extends ProxyLoader {

  protected void load(ChartCtx ctx, Properties props) {
    String fileName = props.getProperty("chart.style.name");
    if(null == fileName) return;
    Repository repos = BeanFactory.getRepBean();
    UserInfoInterface userInfo = (UserInfoInterface) ctx.get(Const.USER_INFO);
    StyleLoader loader = dummyDefault;
    
    InputStream input = repos.getChartFile(userInfo, fileName+".style").getResourceAsStream();
    if(null == input) input = repos.getChartFile(userInfo, fileName).getResourceAsStream();
    if(null == input) return;

    boolean loadOk = false;
    Properties fileProps = new Properties();
    try {
      fileProps.load(input);
      loadOk = true;
    } catch(Exception e) {
    } finally {
      try {
        input.close();
      } catch (IOException e) {
      }
    }
    if(loadOk) loader = StyleLoader.getStyleLoader(ctx, fileProps);
    else loader = dummyDefault;
    
    setStyleLoader(loader);
  }
  
}
