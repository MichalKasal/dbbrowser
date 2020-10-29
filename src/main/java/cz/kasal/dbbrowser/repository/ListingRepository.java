package cz.kasal.dbbrowser.repository;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;

import java.util.List;
import java.util.Map;

/**
 * Listing repository providing methods for querying
 * information about schemas, tables, columns and statistics
 */
public interface ListingRepository {

    /**
     * Finds all schemas in database specified in request
     *
     * @return list of all database schemas
     */
    List<SchemaDTO> findAllSchema();

    /**
     * List of all tables in given schema in database specified in request
     *
     * @param schemaName name of database schema
     * @return list of all tables in schema
     */
    List<TableDTO> findAllTables(String schemaName);

    /**
     * List of all columns in given table in database specified in request
     *
     * @param schemaName name of database schema
     * @param tableName  name of database table
     * @return list of all columns in table
     */
    List<ColumnDTO> findAllColumns(String schemaName, String tableName);

    /**
     * Previews data from certain table in database specified in request
     *
     * @param schemaName name of database schema
     * @param tableName  name of database table
     * @return data preview
     */
    List<Map<String, Object>> previewTable(String schemaName, String tableName);

    /**
     * Calculates min, max, avg, median about each acceptable column in table
     *
     * @param schemaName name of database schema
     * @param tableName  name of database table
     * @return statistics about each column, NaN is used where value is not acceptable
     */
    List<ColumnStatisticsDTO> getColumnsStatistics(String schemaName, String tableName);

    /**
     * Returns number of rows and attributes of all tables
     *
     * @param schemaName name of database schema
     * @return name, number of rows and number of attributes
     */
    List<TableStatisticsDTO> getTableStatistics(String schemaName);
}
