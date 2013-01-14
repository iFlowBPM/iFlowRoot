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
