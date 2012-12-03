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
