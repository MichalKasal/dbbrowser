package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;

import java.util.List;
import java.util.Map;

/**
 * Provides logic for processing Database Listing requests
 */
public interface DatabaseListingService {

     /**
      * Uses repository to query all database schemas
      *
      * @return all database schemas
      */
     List<SchemaDTO> getAllSchemas();

     /**
      * Uses repository to query all tables in given database schema
      *
      * @param schemaName name of database schema
      * @return all tables in schema
      */
     List<TableDTO> findAllTables(String schemaName);

     /**
      * Uses repository to query all columns in given database schema and table
      *
      * @param schemaName database schema
      * @param tableName  database table
      * @return all columns in table
      */
     List<ColumnDTO> findAllColumns(String schemaName, String tableName);

     /**
      * Previes data in given table
      *
      * @param schemaName database schema
      * @param tableName  database table
      * @return raw preview of data in given table
      */
     List<Map<String, Object>> previewTable(String schemaName, String tableName);

     /**
      * Computes min, max, avg, median for given table columns
      *
      * @param schemaName database schema
      * @param tableName  database table
      * @return return statistics about each table column in given table
      */
     List<ColumnStatisticsDTO> getColumnStatistics(String schemaName, String tableName);

     /**
      * Provides information about number of rows and attributes for all tables in database schema
      *
      * @param schemaName database schema
      * @return statistics about each table
      */
     List<TableStatisticsDTO> getTableStatistics(String schemaName);
}
