package cz.kasal.dbbrowser.repository;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;

import java.util.List;
import java.util.Map;

public interface ListingRepository {
    List<SchemaDTO> findAllSchema();

    List<TableDTO> findAllTables(String schemaName);

    List<ColumnDTO> findAllColumns(String schemaName, String tableName);

    List<Map<String, Object>> previewTable(String schemaName, String tableName);
}
