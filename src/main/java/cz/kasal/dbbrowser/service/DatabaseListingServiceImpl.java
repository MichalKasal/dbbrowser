package cz.kasal.dbbrowser.service;


import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.repository.ListingRepositoryImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DatabaseListingServiceImpl implements DatabaseListingService {

    private ListingRepositoryImpl listingRepository;


    public DatabaseListingServiceImpl (ListingRepositoryImpl listingRepository){
        this.listingRepository = listingRepository;
    }


    @Override
    public List<SchemaDTO> getAllSchemas() {
        return listingRepository.findAllSchema();
    }

    public List<TableDTO> findAllTables(String schemaName){
        return listingRepository.findAllTables(schemaName);
    }

    public List<ColumnDTO> findAllColumns(String schemaName, String tableName){
        return listingRepository.findAllColumns(schemaName, tableName);
    }

    public List previewTable(String schemaName, String tableName){
        listingRepository.previewTable(schemaName,tableName);
        return null;
    }



}
