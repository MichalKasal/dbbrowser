package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;

import java.util.List;

public interface DatabaseListingService {

     List<SchemaDTO> getAllSchemas();

     List<TableDTO> findAllTables(String schemaName);

     List<ColumnDTO> findAllColumns(String schemaName, String tableName);

     List previewTable(String schemaName, String tableName);

     List<ColumnStatisticsDTO> getColumnStatistics(String schemaName, String tableName);

     List<TableStatisticsDTO> getTableStatistics(String schemaName);
}
