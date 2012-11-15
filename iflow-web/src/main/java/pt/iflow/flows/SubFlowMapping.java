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
