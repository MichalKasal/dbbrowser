package cz.kasal.dbbrowser.repository;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;

@Lazy
@Repository
public class ListingRepositoryImpl implements ListingRepository {

    private static final String FIND_ALL_SCHEMAS = "select * from information_schema.schemata";

    private static final String FIND_ALL_TABLES = "SELECT tab.*, pgt.hasindexes, pgt.hasrules, pgt.hastriggers, pgt.rowsecurity, pgt.tablespace FROM information_schema.tables tab " +
            "join " +
            "pg_tables pgt ON tab.table_name = pgt.tablename " +
            "                     and tab.table_schema = pgt.schemaname " +
            "                     and tab.table_catalog = pgt.tableowner " +
            "                     and tab.table_schema = ?";


    private static final String FIND_ALL_COLUMNS = "SELECT * FROM" +
            " information_schema.columns " +
            " WHERE table_schema = '?'" +
            " AND table_name = '?'";


    private static final String EXISTS_TABLE = "select 1 from information_schema.tables where table_schema = '?' and table_name = '?'";

    private static final String PREVIEW_DATA = "SELECT * FROM %s.%s";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ListingRepositoryImpl(@Qualifier("createdJdbcTemplate") JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<SchemaDTO> findAllSchema(){
        return this.jdbcTemplate.query(FIND_ALL_SCHEMAS, new BeanPropertyRowMapper<>(SchemaDTO.class));
    }

    @Override
    public List<TableDTO> findAllTables(String schemaName){
        return this.jdbcTemplate.query(FIND_ALL_TABLES, new BeanPropertyRowMapper<>(TableDTO.class),schemaName);
    }

    @Override
    public List<ColumnDTO> findAllColumns(String schemaName, String tableName){
        return this.jdbcTemplate.query(FIND_ALL_COLUMNS, new String[]{schemaName,tableName}, new BeanPropertyRowMapper<>(ColumnDTO.class));
    }

    @Override
    public List<Map<String, Object>> previewTable(String schemaName, String tableName){
        if(this.jdbcTemplate.queryForList(EXISTS_TABLE, new String[]{schemaName, tableName}).isEmpty()){
            throw new EntityNotFoundException(String.format("Table with table name %s and schema %s doesn't exist", schemaName, tableName));
        }else{
            String query = String.format(PREVIEW_DATA, schemaName, tableName);
            return this.jdbcTemplate.queryForList(query);
        }
    }
}
