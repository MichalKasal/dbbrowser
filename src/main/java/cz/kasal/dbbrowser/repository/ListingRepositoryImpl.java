package cz.kasal.dbbrowser.repository;

import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Lazy
@Repository
public class ListingRepositoryImpl implements ListingRepository {

    private static final List<String> NUMERIC_DATATYPES = Arrays.asList("smallint", "integer", "bigint", "real", "numeric", "interval");


    private static final String FIND_ALL_SCHEMAS = "select * from information_schema.schemata";

    private static final String FIND_ALL_TABLES = "SELECT tab.*, pgt.hasindexes, pgt.hasrules, pgt.hastriggers, pgt.rowsecurity, pgt.tablespace FROM information_schema.tables tab " +
            "join " +
            "pg_tables pgt ON tab.table_name = pgt.tablename " +
            "                     and tab.table_schema = pgt.schemaname " +
            "                     and tab.table_catalog = pgt.tableowner " +
            "                     and tab.table_schema = ?";


    private static final String FIND_ALL_COLUMNS = "SELECT * FROM" +
            " information_schema.columns " +
            " WHERE table_schema = ?" +
            " AND table_name = ?";


    private static final String EXISTS_TABLE = "select 1 from information_schema.tables where table_schema = ? and table_name = ?";

    private static final String PREVIEW_DATA = "SELECT * FROM %s.%s LIMIT 10";

    private static final String TABLE_STATISTICS = "select " +
            "       (select '{0}' as table_name), " +
            "       (select count(con.*) as number_of_rows from {0} con), " +
            "       (select count(inf.*) as number_of_attributes from information_schema.columns inf " +
            "                                           where inf.table_name = '{0}' " +
            "                                           and inf.table_schema = '{1}') union all ";


    private JdbcTemplate jdbcTemplate;

    @Autowired
    public ListingRepositoryImpl(@Qualifier("createdJdbcTemplate") JdbcTemplate jdbcTemplate) {
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
        return this.jdbcTemplate.query(FIND_ALL_COLUMNS, new BeanPropertyRowMapper<>(ColumnDTO.class), schemaName, tableName);
    }

    @Override
    public List<Map<String, Object>> previewTable(String schemaName, String tableName) {
        if (checkIfValidTableName(schemaName, tableName)) {
            String query = String.format(PREVIEW_DATA, schemaName, tableName);
            return this.jdbcTemplate.queryForList(query);
        } else {
            throw new EntityNotFoundException(String.format("Table with table name %s and schema %s doesn't exist", schemaName, tableName));
        }
    }

    @Override
    public List<TableStatisticsDTO> getTableStatistics(String schemaName) {
        List<TableDTO> tables = findAllTables(schemaName);
        StringBuilder sb = new StringBuilder();
        tables.forEach(tableDTO -> sb.append(TABLE_STATISTICS.replace("{0}", tableDTO.getTableName())));
        sb.delete(sb.length() - " union all ".length(), sb.length());
        String query = sb.toString().replace("{1}", schemaName);
        return this.jdbcTemplate.query(query, new BeanPropertyRowMapper<>(TableStatisticsDTO.class));
    }

    @Override
    public List<ColumnStatisticsDTO> getColumnsStatistics(String schemaName, String tableName) {
        List<ColumnDTO> columnDTOs = findAllColumns(schemaName, tableName);
        StringBuilder sb = new StringBuilder();
        StringBuilder sbNon = new StringBuilder();
        columnDTOs.forEach(columnDTO -> {
            if (NUMERIC_DATATYPES.contains(columnDTO.getDataType())) {
                sb.append("select '{0}' as column, avg({0}), min({0}), max({0}), percentile_disc(0.5) within group ( order by {0} ) as median from {1}.{2} union all "
                        .replace("{0}", columnDTO.getColumnName()));
            } else {
                sbNon.append("select '{0}' as column, 'NaN' as avg, min({0}), max({0}), 'NaN' as median from {1}.{2} union all "
                        .replace("{0}", columnDTO.getColumnName()));
            }
        });
        List<ColumnStatisticsDTO> statisticsDTOs = new ArrayList<>();
        getPartOfStatistics(statisticsDTOs, sb, tableName, schemaName);
        getPartOfStatistics(statisticsDTOs, sbNon, tableName, schemaName);
        return statisticsDTOs;
    }


    private void getPartOfStatistics(List<ColumnStatisticsDTO> statisticsDTOs, StringBuilder sb, String tableName, String schemaName) {
        sb.delete(sb.length() - " union all ".length(), sb.length());
        if (sb.length() > 0) {
            String queryStatistics = sb.toString()
                    .replace("{2}", tableName)
                    .replace("{1}", schemaName);
            statisticsDTOs.addAll(this.jdbcTemplate.query(queryStatistics, new BeanPropertyRowMapper<>(ColumnStatisticsDTO.class)));
        }
    }


    private boolean checkIfValidTableName(String schemaName, String tableName) {
        return !this.jdbcTemplate.queryForList(EXISTS_TABLE, schemaName, tableName).isEmpty();
    }


}
