package pt.iflow.api.blocks;


/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class Port {

   private String name;
   private int connectedBlockId;
   private String connectedPortName;

   public Port() {
   }

   /**
    * Sets the port name.
    * @param nm the port name.
    */
   public void setName(String nm) {
      name = nm;
   }

   /**
    * Gets the port name.
    * @return the port name.
    */
   public String getName() {
      return name;
   }

   /**
    * Sets the connected block id.
    * @param id the connected block id.
    */
   public void setConnectedBlockId(int id) {
      connectedBlockId = id;
   }

   /**
    * Gets the connected block id.
    * @return the connected block id.
    */
   public int getConnectedBlockId() {
      return connectedBlockId;
   }

   /**
    * Sets the connected port name.
    * @param name the connected port name.
    */
   public void setConnectedPortName(String name) {
      connectedPortName = name;
   }

   /**
    * Gets the connected port name.
    * @return the connected port name.
    */
   public String getConnectedPortName() {
      return connectedPortName;
   }

}

