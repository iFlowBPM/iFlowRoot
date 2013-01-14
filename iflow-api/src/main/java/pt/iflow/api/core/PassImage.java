package pt.iflow.api.core;

import pt.iflow.api.utils.UserInfoInterface;

public interface PassImage {
    

    public byte[] getImage(UserInfoInterface userInfo);
    
    public byte[] getRubricImage(UserInfoInterface userInfo);
       
    public void saveImage(UserInfoInterface userInfo, byte[] img);

    public void saveRubrica(UserInfoInterface userInfo, byte[] img);
    
    public int getNumAss(UserInfoInterface userInfo, int docid);

    public void updateNumAss(UserInfoInterface userInfo, int docid, int numass);
    
    public void teste(UserInfoInterface userInfo);
   
    public byte[] getImageUser(String userid);
    
    public byte[] getImageIconRepFromImage(byte[] image);
}
