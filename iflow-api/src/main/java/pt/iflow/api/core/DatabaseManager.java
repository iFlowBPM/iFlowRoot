package pt.iflow.api.core;

import pt.iflow.api.db.DBTable;

/**
 * DatabaseManager.
 * 
 * @author Luis Cabral
 * @since 22.01.2010
 * @version 22.01.2010
 */
public interface DatabaseManager {

  /**
   * Retrieve table's description from DB.
   * 
   * @param dbPool
   *          DB pool name.
   * @param table
   *          Table name.
   * @return Table's description.
   * @see #getTableDescription(String, String, String)
   */
  DBTable getTableDescription(String dbPool, String table);

  /**
   * Retrieve table's description from DB.
   * 
   * @param dbPool
   *          DB pool name.
   * @param schema
   *          Table schema.
   * @param table
   *          Table name.
   * @return Table's description.
   * @see #getTableDescription(String, String)
   */
  DBTable getTableDescription(String dbPool, String schema, String table);

}
