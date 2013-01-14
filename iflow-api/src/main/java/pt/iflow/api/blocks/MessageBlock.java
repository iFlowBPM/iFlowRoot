package pt.iflow.api.blocks;

import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public interface MessageBlock {
    
    public final static String MESSAGE_USER = "UserMessage";
    
    public boolean hasMessage();
    public String getMessage(UserInfoInterface userInfo, ProcessData procData);
    public String getMessage(UserInfoInterface userInfo, ProcessData procData, String defaultMessage);
}
