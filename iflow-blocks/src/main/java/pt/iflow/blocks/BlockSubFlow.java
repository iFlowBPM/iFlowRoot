package pt.iflow.blocks;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.Port;
import pt.iflow.api.processdata.ProcessData;
import pt.iflow.api.utils.UserInfoInterface;

public class BlockSubFlow extends Block {

	public Port portIn, portOut, portOutThread, portError;

	public BlockSubFlow(int anFlowId, int id, int subflowblockid, String filename) {
		super(anFlowId,id, subflowblockid, filename);
		hasInteraction = false;
		bProcInDBRequired = true;
		saveFlowState = false;
		canRunInPopupBlock = false;
		throw new RuntimeException("Not migrated.");
	}

	@Override
	public Port getEventPort() {
		return null;
	}

	@Override
	public String before(UserInfoInterface userInfo, ProcessData procData) {
		return  "";
	}

	@Override
	public boolean canProceed(UserInfoInterface userInfo, ProcessData procData) {
		return false;
	}

	@Override
	public Port after(UserInfoInterface userInfo, ProcessData procData) {
		return null;
	}

	@Override
	public String getDescription(UserInfoInterface userInfo,
			ProcessData procData) {
		return this.getDesc(userInfo, procData, true, "Sa&iacute; SubFluxo " + this.getSubFlowFilename());
	}

	@Override
	public String getResult(UserInfoInterface userInfo, ProcessData procData) {
		return this.getDesc(userInfo, procData, false, "Sa&iacute; SubFluxo " + this.getSubFlowFilename() + " Conclu&iacute;da.");
	}

	@Override
	public String getUrl(UserInfoInterface userInfo, ProcessData procData) {
		return Block.getDefaultUrl(userInfo, procData);
	}

	@Override
	public Port[] getOutPorts(UserInfoInterface userInfo) {
		Port[] retObj = new Port[2];
		retObj[0] = portOut;
		retObj[1] = portError;
		return retObj;
	}

	@Override
	public Port[] getInPorts(UserInfoInterface userInfo) {
		Port[] retObj = new Port[2];
		retObj[0] = portIn;
		return retObj;
	}


}
