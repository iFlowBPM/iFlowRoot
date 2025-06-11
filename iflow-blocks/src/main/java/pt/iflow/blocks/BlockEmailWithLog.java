package pt.iflow.blocks;

import pt.iflow.api.notification.Email;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockEmailWithLog extends BlockEmail {

	public final static String sMESSAGE_ID = "messageId"; //$NON-NLS-1$

	public BlockEmailWithLog(int anFlowId, int id, int subflowblockid, String filename) {
		super(anFlowId, id, subflowblockid, filename);
		// TODO Auto-generated constructor stub
	}


	protected void sendMessage(UserInfoInterface userInfo, ProcessData procData, Email email, StringBuffer logMsg) {

		String varMessageId = getAttribute(sMESSAGE_ID);
		String msgId = "unknown";

		msgId = email.sendMsgWithLog(true);
		procData.set(varMessageId, msgId);


		logMsg.append("Mail sent To: " + email.getTo() + ";");
		Logger.info(userInfo.getUtilizador(),this,"after",procData.getSignature() + "email registered with Msg Id " + msgId);

	}

}
