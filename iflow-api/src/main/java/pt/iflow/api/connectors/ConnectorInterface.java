package pt.iflow.api.connectors;

import java.util.List;

import pt.iflow.api.connectors.handlers.SelectItem;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface ConnectorInterface {

  static final String SIGNATURE = "ConnectorInterface";
  
  List<SelectItem> dispatch(UserInfoInterface userInfo, ProcessData procData, Object... args);

  String getDescription(UserInfoInterface userInfo);

  String getArgsInfo();
}
