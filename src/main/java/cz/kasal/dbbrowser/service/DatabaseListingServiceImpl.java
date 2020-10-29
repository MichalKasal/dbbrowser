package cz.kasal.dbbrowser.service;


import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;
import cz.kasal.dbbrowser.repository.ListingRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseListingServiceImpl implements DatabaseListingService {

    private ListingRepositoryImpl listingRepository;

    @Autowired
    public DatabaseListingServiceImpl (ListingRepositoryImpl listingRepository){
        this.listingRepository = listingRepository;
    }


    @Override
    public List<SchemaDTO> getAllSchemas() {
        return listingRepository.findAllSchema();
    }

    @Override
    public List<TableDTO> findAllTables(String schemaName) {
        return listingRepository.findAllTables(schemaName);
    }

    @Override
    public List<ColumnDTO> findAllColumns(String schemaName, String tableName) {
        return listingRepository.findAllColumns(schemaName, tableName);
    }

    @Override
    public List<Map<String, Object>> previewTable(String schemaName, String tableName) {
        return listingRepository.previewTable(schemaName, tableName);
    }

    @Override
    public List<ColumnStatisticsDTO> getColumnStatistics(String schemaName, String tableName) {
        return listingRepository.getColumnsStatistics(schemaName, tableName);
    }

    @Override
    public List<TableStatisticsDTO> getTableStatistics(String schemaName) {
        return listingRepository.getTableStatistics(schemaName);
    }


}
