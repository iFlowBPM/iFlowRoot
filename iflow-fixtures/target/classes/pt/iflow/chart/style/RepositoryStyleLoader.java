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
