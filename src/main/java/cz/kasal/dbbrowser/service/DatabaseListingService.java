package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;

import java.util.List;

public interface DatabaseListingService {

     List<SchemaDTO> getAllSchemas();

     List<TableDTO> findAllTables(String schemaName);

     List<ColumnDTO> findAllColumns(String schemaName, String tableName);

}
