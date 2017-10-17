/**
 * 
 */
package pt.iflow.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import pt.iflow.api.core.InterfacesManager;
import pt.iflow.api.db.DatabaseInterface;
import pt.iflow.api.transition.ProfilesTO;
import pt.iflow.api.utils.Logger;
import pt.iflow.api.utils.UserInfoInterface;
import pt.iflow.api.utils.InterfaceInfo;
import pt.iflow.api.utils.Utils;

/**
 * @author helder
 *
 */
public class InterfacesManagerBean implements InterfacesManager {

  private static InterfacesManagerBean instance = null;

  private InterfacesManagerBean() {}

  public static InterfacesManagerBean getInstance() {
    if(null == instance){
      instance = new InterfacesManagerBean();
      initializeInterfaces();
    }
    return instance;
  }

  private static InterfaceInfo[] interfaces = new InterfaceInfo[0];;
  private static ProfilesTO[] profiles = new ProfilesTO[0];;
  /**
   * metodo que inicializa a lista das interfaces no sistema configuração actual 
   * não contempla, que a listagem das interfaces esteja numa bd
   */
  private static void initializeInterfaces() {
    // TODO Auto-generated method stub
    List <InterfaceInfo> listaInterfaces = new ArrayList <InterfaceInfo>();

    /*1 Painel $field_dashboard
      2 Tarefas $field_tasks
      3 Processos $field_processes
      4 Admin $field_admin
      5 Delegações $field_delegations
      8 Pesquisa $field_myprocesses
      10 Relatórios $field_reports
      
      12 Notificações
     * */
   // InterfaceInfo umaInterface = new InterfaceInfo(1,"Painel","");
   // listaInterfaces.add(umaInterface);

InterfaceInfo  umaInterface = new InterfaceInfo(2,"Tarefas","Lista de tarefas que o utilizador tem activas, podendo utilizar filtros para facilitar a pesquisa");
    listaInterfaces.add(umaInterface);

   // umaInterface = new InterfaceInfo(3,"Processos","");
   // listaInterfaces.add(umaInterface);

   // umaInterface = new InterfaceInfo(4,"Admin","");
   // listaInterfaces.add(umaInterface);

    umaInterface = new InterfaceInfo(5,"Delegações","Interface que permite o controlo de delegações de tarefas entre utilizadores");
    listaInterfaces.add(umaInterface);

    umaInterface = new InterfaceInfo(8,"Pesquisa","Lista de processos disponiveis ao utilizador");
    listaInterfaces.add(umaInterface);

    umaInterface = new InterfaceInfo(10,"Relatórios","Permite acesso a graficos que revelam informações uteis sobre as tarefas");
    listaInterfaces.add(umaInterface);
    
    umaInterface = new InterfaceInfo(12,"Notificações","Interface que apresenta informação relevante ao utilizador");
    listaInterfaces.add(umaInterface);
    
    umaInterface = new InterfaceInfo(11,"Documentos","Permite acesso a processos do tipo Documento");
    listaInterfaces.add(umaInterface);

    interfaces =listaInterfaces.toArray(new InterfaceInfo[listaInterfaces.size()]);
  }

  /* (non-Javadoc)
   * @see pt.iflow.core.InterfacesManager#getAllInterfaces(pt.iflow.api.utils.UserInfoInterface)
   */
  public InterfaceInfo[] getAllInterfaces(){  
    return interfaces; 
  }

  /* (non-Javadoc)
   * @see pt.iflow.core.InterfacesManager#getProfilesForInterface(pt.iflow.api.utils.UserInfoInterface, String)
   */
  public String[] getProfilesForInterface(UserInfoInterface userInfo, String idInterface) {
    // TODO Auto-generated method stub
    String [] result = new String[0];

    Logger.debug(userInfo .getUtilizador(), this, "getUserProfiles", "Listing all profiles");

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "getUserProfiles", "not administrator, exiting");
      return new String[0];
    }

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("SELECT profileid FROM profiles_tabs WHERE tabid="+idInterface);

    int size = coll.size();
    int i = 0;
    result = new String[size];
    Iterator<Map<String,String>> iter = coll.iterator();
    while(iter.hasNext()) {
      Map<String,String> mapping = iter.next();
      result[i] = (String) mapping.get("profileid");
      i++;
    }

    return result;
  }

  public boolean addProfileToInterface(UserInfoInterface userInfo, String interfaceId, String profileId) {
    // TODO Auto-generated method stub
    boolean result = false;
    String organizationId = userInfo.getCompanyID();
   
    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "addProfileToInterface", "Adding profile to interface. user:" + userInfo.getUserId() + "interface "+interfaceId+" to profile " + profileId);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "addProfileToInterface", "not administrator, exiting");
      return false;
    }

    try {
      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      //verification if it's not the default profile
      if (profileId.equals("0")){
        pst = db.prepareStatement("insert into organizations_tabs (organizationid,tabid) values (?,?)");
        pst.setString(2, interfaceId);
        pst.setString(1, organizationId);
      }else{
        pst = db.prepareStatement("insert into profiles_tabs (profileid,tabid) values (?,?)");
        pst.setString(2, interfaceId);
        pst.setString(1, profileId);       
      }
      pst.executeUpdate();
      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "addinterfaceprofiles", "Interface-Profile mapping not inserted!", e);
    }
    finally {
      DatabaseInterface.closeResources(db, pst);
    }

    return result;
  }

  public boolean removeProfileFromInterface(UserInfoInterface userInfo, String interfaceId, String profileId) {
    // TODO Auto-generated method stub
    boolean result = false;
    String organizationId = userInfo.getCompanyID();

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;

    Logger.debug(userInfo.getUtilizador(), this, "removeProfileFromInterface", "Removing profile "+profileId+" from interface " + interfaceId);

    if (!userInfo.isOrgAdmin()) {
      Logger.debug(userInfo.getUtilizador(), this, "removeProfileFromInterface", "not administrator, exiting");
      return false;
    }

    try {

      ds = Utils.getDataSource();
      db = ds.getConnection();
      db.setAutoCommit(false);
      if (profileId.equals("0")){
        pst = db.prepareStatement("delete from organizations_tabs where tabid = ? and organizationid = ?");
        pst.setString(1, interfaceId);
        pst.setString(2, organizationId);
      }else{
        pst = db.prepareStatement("delete from profiles_tabs where profileid = ? and tabid = ?");
        pst.setString(1, profileId);
        pst.setString(2, interfaceId);
      }
      pst.executeUpdate();

      db.commit();
      result = true;

    }
    catch (SQLException e) {
      result = false;
      Logger.warning(userInfo.getUtilizador(), this, "removeProfileFromInterface", "mapeamento não desfeito!", e);
    }
    finally {
      DatabaseInterface.closeResources(db, pst);
    }

    return result;
  }

  public boolean isInterfaceDisabledByDefault(UserInfoInterface userInfo, String interfaceId) {
    // TODO Auto-generated method stub
    boolean result = false;

    String organizationId = userInfo.getCompanyID();

    DataSource ds = null;
    Connection db = null;

    PreparedStatement pst = null;
    Logger.debug(userInfo.getUtilizador(), this, "isInterfaceDisabledByDefault", "query is interface disabled by default: organization"+organizationId+" interface " + interfaceId);

    Collection<Map<String,String>> coll = DatabaseInterface.executeQuery("SELECT organizationid FROM organizations_tabs WHERE organizationid = "+organizationId+" and tabid ="+interfaceId);
    
    if(coll.size() == 1){
      result = true;
    }  
    return result;
  }


//------------------------------------------ TIRAR DA BD ARRAY IDs TABS SEM PERMISSAO
public int[] tabsRejeitadas( UserInfoInterface userInfo , String perfil)
{   
  int [] ids = new int [0];
  int i = 0;
  int tam = 0;
  DataSource ds = Utils.getDataSource();
  Connection db = null;
  Statement st = null;
  ResultSet rs = null;
  try {
    db = ds.getConnection();
    st = db.createStatement();
    
    //Tirar vector com nao permissoes da organizaçao e dos perfis
    rs = st.executeQuery("SELECT tabid FROM profiles_tabs" 
                        + "where profileid = "+ perfil
                        + " union select tabid from organizations_tabs where organizationid = "+userInfo.getOrganization());

 //Inicializar array com numero de tabs
 while (rs.next()) tam++;
 rs.beforeFirst();  
 ids = new int[tam];
  
    //Preencher array com IDs das tabs
    while (rs.next()){
    ids[i] = rs.getInt(1);
    i++;
    }     
    rs.close();
  }
  catch (SQLException sqle) {
    sqle.printStackTrace();
  }
  finally {
    DatabaseInterface.closeResources(db, st, rs);
  }   
  return ids;
}
//----------------------------------------------------------  FIM
}





