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
package pt.iflow.api.services.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import pt.iflow.api.blocks.Block;
import pt.iflow.api.blocks.form.Form;
import pt.iflow.api.utils.UserInfoInterface;

public class FlowDataConvert {
	private String BLOCK_PACKAGE;
	private int iOFFSET;
	
	private int _nId;
	private String _sName;
	private String _sFile;
	private Vector<Block> _vFlow;
	private boolean _bOnline;
	private boolean _bDeployed;
	private String _sApplicationId;
	private String _sApplicationName;
	private String _sError;
	private String _organizationId;
	private long _lastModified;
	private long _created;
	private HashMap<String, Integer> _hmIndexVars;
	private int indexPosition;
	private boolean _hasDetail;
	private Block _detailForm;
	private int _seriesId;
	////private ProcessCatalogueConvert _catalogue;
	private Map<String, Form> _hmFormTemplates;
	////private MailStartSettingsConvert _mailStartSettings; 
	private String flowType; //To solve: FlowType does not contain a default constructor, which is a requirement for a bean class
	private int maxBlockId;

	private FlowClassGeneratorConvert flowClassFile = null; ////

	private Hashtable<String, Hashtable<Integer, Object[]>> _htSubFlowEndPorts;
	private Hashtable<Integer, ForkJoinDepConvert> _htForkJoinDepPath; ////
	private HashSet<Set<Integer>> _hsAllStates;
	private Hashtable<String, String> _htSubFlows;

	public FlowDataConvert() {
		super();
	}

	public String getBLOCK_PACKAGE() {
		return BLOCK_PACKAGE;
	}

	public void setBLOCK_PACKAGE(String bLOCKPACKAGE) {
		BLOCK_PACKAGE = bLOCKPACKAGE;
	}

	public int getiOFFSET() {
		return iOFFSET;
	}

	public void setiOFFSET(int iOFFSET) {
		this.iOFFSET = iOFFSET;
	}

	public int get_nId() {
		return _nId;
	}

	public void set_nId(int nId) {
		_nId = nId;
	}

	public String get_sName() {
		return _sName;
	}

	public void set_sName(String sName) {
		_sName = sName;
	}

	public String get_sFile() {
		return _sFile;
	}

	public void set_sFile(String sFile) {
		_sFile = sFile;
	}

	public Vector<Block> get_vFlow() {
		return _vFlow;
	}

	public void set_vFlow(Vector<Block> vFlow) {
		_vFlow = vFlow;
	}

	public boolean is_bOnline() {
		return _bOnline;
	}

	public void set_bOnline(boolean bOnline) {
		_bOnline = bOnline;
	}

	public boolean is_bDeployed() {
		return _bDeployed;
	}

	public void set_bDeployed(boolean bDeployed) {
		_bDeployed = bDeployed;
	}

	public String get_sApplicationId() {
		return _sApplicationId;
	}

	public void set_sApplicationId(String sApplicationId) {
		_sApplicationId = sApplicationId;
	}

	public String get_sApplicationName() {
		return _sApplicationName;
	}

	public void set_sApplicationName(String sApplicationName) {
		_sApplicationName = sApplicationName;
	}

	public String get_sError() {
		return _sError;
	}

	public void set_sError(String sError) {
		_sError = sError;
	}

	public String get_organizationId() {
		return _organizationId;
	}

	public void set_organizationId(String organizationId) {
		_organizationId = organizationId;
	}

	public long get_lastModified() {
		return _lastModified;
	}

	public void set_lastModified(long lastModified) {
		_lastModified = lastModified;
	}

	public long get_created() {
		return _created;
	}

	public void set_created(long created) {
		_created = created;
	}

	public HashMap<String, Integer> get_hmIndexVars() {
		return _hmIndexVars;
	}

	public void set_hmIndexVars(HashMap<String, Integer> hmIndexVars) {
		_hmIndexVars = hmIndexVars;
	}

	public int getIndexPosition() {
		return indexPosition;
	}

	public void setIndexPosition(int indexPosition) {
		this.indexPosition = indexPosition;
	}

	public boolean is_hasDetail() {
		return _hasDetail;
	}

	public void set_hasDetail(boolean hasDetail) {
		_hasDetail = hasDetail;
	}

	public Block get_detailForm() {
		return _detailForm;
	}

	public void set_detailForm(Block detailForm) {
		_detailForm = detailForm;
	}

	public int get_seriesId() {
		return _seriesId;
	}

	public void set_seriesId(int seriesId) {
		_seriesId = seriesId;
	}

	/*public ProcessCatalogueConvert get_catalogue() {
		return _catalogue;
	}

	public void set_catalogue(ProcessCatalogueConvert catalogue) {
		_catalogue = catalogue;
	}*/

	public Map<String, Form> get_hmFormTemplates() {
		return _hmFormTemplates;
	}

	public void set_hmFormTemplates(Map<String, Form> hmFormTemplates) {
		_hmFormTemplates = hmFormTemplates;
	}

	/*public MailStartSettingsConvert get_mailStartSettings() {
		return _mailStartSettings;
	}

	public void set_mailStartSettings(MailStartSettingsConvert mailStartSettings) {
		_mailStartSettings = mailStartSettings;
	}*/

	public String getFlowType() {
		return flowType;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}

	public int getMaxBlockId() {
		return maxBlockId;
	}

	public void setMaxBlockId(int maxBlockId) {
		this.maxBlockId = maxBlockId;
	}

	public FlowClassGeneratorConvert getFlowClassFile() {
		return flowClassFile;
	}

	public void setFlowClassFile(FlowClassGeneratorConvert flowClassFile) {
		this.flowClassFile = flowClassFile;
	}

	public Hashtable<String, Hashtable<Integer, Object[]>> get_htSubFlowEndPorts() {
		return _htSubFlowEndPorts;
	}

	public void set_htSubFlowEndPorts(
			Hashtable<String, Hashtable<Integer, Object[]>> htSubFlowEndPorts) {
		_htSubFlowEndPorts = htSubFlowEndPorts;
	}

	public Hashtable<Integer, ForkJoinDepConvert> get_htForkJoinDepPath() {
		return _htForkJoinDepPath;
	}

	public void set_htForkJoinDepPath(
			Hashtable<Integer, ForkJoinDepConvert> htForkJoinDepPath) {
		_htForkJoinDepPath = htForkJoinDepPath;
	}

	public HashSet<Set<Integer>> get_hsAllStates() {
		return _hsAllStates;
	}

	public void set_hsAllStates(HashSet<Set<Integer>> hsAllStates) {
		_hsAllStates = hsAllStates;
	}

	public Hashtable<String, String> get_htSubFlows() {
		return _htSubFlows;
	}

	public void set_htSubFlows(Hashtable<String, String> htSubFlows) {
		_htSubFlows = htSubFlows;
	}
}
