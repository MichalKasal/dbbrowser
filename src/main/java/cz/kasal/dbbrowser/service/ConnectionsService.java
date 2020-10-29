package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.model.ConnectionDTO;

/**
 * Provides logic for processing CRUD connection requests
 */
public interface ConnectionsService {

    /**
     * Adds new database connection into DB
     * @param connectionDTO representation of database connection
     * @return saved DB connection
     */
    ConnectionDTO saveConnection(ConnectionDTO connectionDTO);

    /**
     * Returns connection specified by ID
     * @param connectionID  connection ID
     * @return  connection specified by ID
     */
    ConnectionDTO getConnection(Long connectionID);

    /**
     * Deletes connection specified by ID
     * @param connectionID  connection ID
     */
    void deleteConnection(Long connectionID);


    /**
     * Updates existing connection or adds new connection if connection doesn't exist yet
     * @param connectionDTO representation of connection object
     * @return  saved connection
     */
    ConnectionDTO updateConnection(ConnectionDTO connectionDTO, Long connectionId);

}
