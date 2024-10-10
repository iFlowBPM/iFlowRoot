/*
 * <p>Title: ProfileData.java</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 18/Abr/2005</p>
 * <p>Company: iKnow</p>
 * @author Pedro Monteiro
 * @version 1.0
 */

package pt.iflow.presentation;

public class ProfileData {
    
    String _profile = null;
    String _theme = null;
    int _weight = -1;

    public ProfileData(String profile, String theme, int weight) {
        _profile = profile;
        _theme = theme;
        _weight = weight;
    }

    public String getProfile() {
        return _profile;
    }
    

    public String getTheme() {
        return _theme;
    }
    

    public int getWeight() {
        return _weight;
    }
}
