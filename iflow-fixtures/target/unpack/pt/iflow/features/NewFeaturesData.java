/*
 * <p>Title: NewFeaturesData.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 26/Jul/2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.features;

import java.sql.Timestamp;

public class NewFeaturesData {
    
    int _id = -1;
    String _version = null;
    String _feature = null;
    String _description = null;
    Timestamp _created = null;

    public NewFeaturesData(int id, String version, String feature, String description, Timestamp created) {
        _id = id;
        _version = version;
        _feature = feature;
        _description = description;
        _created = created;
    }
    public NewFeaturesData(String version, String feature, String description) {
        _version = version;
        _feature = feature;
        _description = description;
    }
    public NewFeaturesData(int id, String version, String feature, String description) {
        _id = id;
        _version = version;
        _feature = feature;
        _description = description;
    }
    public int getId() {
        return _id;
    }

    public String getVersion() {
        return _version;
    }

    public String getFeature() {
        return _feature;
    }

    public Timestamp getCreated() {
        return _created;
    }

    public String getDescription() {
        return _description;
    }
}
