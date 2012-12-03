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
package pt.iflow.flows;

import java.sql.Date;

public class SubFlowMapping {
    private Date timestamp;
    private String mainFlowName;
    private String subFlowName;
    private Integer originalBlockId;
    private Integer mappedBlockId;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getOriginalBlockId() {
        return originalBlockId;
    }

    public void setOriginalBlockId(Integer originalBlockId) {
        this.originalBlockId = originalBlockId;
    }

    public Integer getMappedBlockId() {
        return mappedBlockId;
    }

    public void setMappedBlockId(Integer mappedBlockId) {
        this.mappedBlockId = mappedBlockId;
    }

    public SubFlowMapping() {
        super();
        // TODO Auto-generated constructor stub
    }

    public String getMainFlowName() {
        return mainFlowName;
    }

    public void setMainFlowName(String mainFlowName) {
        this.mainFlowName = mainFlowName;
    }

    public String getSubFlowName() {
        return subFlowName;
    }

    public void setSubFlowName(String subFlowName) {
        this.subFlowName = subFlowName;
    }

    public SubFlowMapping(Date timestamp, String mainFlowName, String subFlowName, Integer originalBlockId,
            Integer mappedBlockId) {
        super();
        this.timestamp = timestamp;
        this.mainFlowName = mainFlowName;
        this.subFlowName = subFlowName;
        this.originalBlockId = originalBlockId;
        this.mappedBlockId = mappedBlockId;
    }

    public SubFlowMapping(String mainFlowName, String subFlowName, Integer originalBlockId, Integer mappedBlockId) {
        super();
        this.mainFlowName = mainFlowName;
        this.subFlowName = subFlowName;
        this.originalBlockId = originalBlockId;
        this.mappedBlockId = mappedBlockId;
    }    
}
