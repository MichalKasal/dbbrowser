package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.connection.api.ConnectionsApiDelegate;
import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.TableStatisticsDTO;
import cz.kasal.dbbrowser.service.ConnectionsService;
import cz.kasal.dbbrowser.service.DatabaseListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class ConnectionsApi implements ConnectionsApiDelegate {

    private ConnectionsService connectionsService;

    private DatabaseListingService databaseListingService;

    @Autowired
    public ConnectionsApi(ConnectionsService connectionsService, DatabaseListingService databaseListingService){
        this.connectionsService = connectionsService;
        this.databaseListingService = databaseListingService;
    }


    @Override
    public ResponseEntity<Void> deleteConnectionsConnectionID(Long connectionID) {
        connectionsService.deleteConnection(connectionID);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ConnectionDTO> getConnectionsConnectionID(Long connectionID) {
        ConnectionDTO connectionDTO = connectionsService.getConnection(connectionID);
        addHypermediaConnection(connectionDTO);
        return ResponseEntity.ok(connectionDTO);
    }

    @Override
    public ResponseEntity<ConnectionDTO> postConnections(ConnectionDTO connectionDTO) {
        connectionDTO = connectionsService.saveConnection(connectionDTO);
        addHypermediaConnection(connectionDTO);
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(connectionDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(connectionDTO);
    }

    @Override
    public ResponseEntity<ConnectionDTO> putConnectionsConnectionID(Long connectionID, ConnectionDTO connectionDTO) {
        connectionDTO = connectionsService.updateConnection(connectionDTO, connectionID);
        addHypermediaConnection(connectionDTO);
        if(connectionID.compareTo(connectionDTO.getId()) == 0){
            return ResponseEntity.ok(connectionDTO);
        }else{
            URI uri = MvcUriComponentsBuilder.fromController(getClass())
                    .path("/{id}")
                    .buildAndExpand(connectionDTO.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(connectionDTO);
        }
    }

    @Override
    public ResponseEntity<List<ColumnDTO>> getColumns(String schemaName, String tableName, Long connectionId) {
        List<ColumnDTO> columnDTOs = databaseListingService.findAllColumns(schemaName, tableName);
        return ResponseEntity.ok(columnDTOs);
    }

    @Override
    public ResponseEntity<List<Object>> getPreview(Long connectionId, String schemaName, String tableName) {
        List<Object> preview = databaseListingService.previewTable(schemaName, tableName);
        return ResponseEntity.ok(preview);
    }

    @Override
    public ResponseEntity<List<SchemaDTO>> getSchemas(Long connectionId) {
        List<SchemaDTO> schemaDTOs = databaseListingService.getAllSchemas().stream().map(schemaDTO -> addHypermediaSchema(schemaDTO, connectionId)).collect(Collectors.toList());
        return ResponseEntity.ok(schemaDTOs);
    }


    @Override
    public ResponseEntity<List<TableDTO>> getTables(String schemaName, Long connectionId) {
        List<TableDTO> tableDTOs = databaseListingService.findAllTables(schemaName).stream().map(tableDTO -> addHypermediaTable(tableDTO, schemaName, connectionId)).collect(Collectors.toList());
        return ResponseEntity.ok(tableDTOs);
    }

    @Override
    public ResponseEntity<List<TableStatisticsDTO>> getTableStatistics(Long connectionId, String schemaName) {
        List<TableStatisticsDTO> tableStatisticsDTOs = databaseListingService.getTableStatistics(schemaName);
        return ResponseEntity.ok(tableStatisticsDTOs);
    }

    @Override
    public ResponseEntity<List<ColumnStatisticsDTO>> getTableColumnStatistics(Long connectionId, String schemaName, String tableName) {
        List<ColumnStatisticsDTO> columnStatisticsDTOS = databaseListingService.getColumnStatistics(schemaName, tableName);
        return ResponseEntity.ok(columnStatisticsDTOS);
    }

    private void addHypermediaConnection(ConnectionDTO connectionDTO) {
        connectionDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getConnectionsConnectionID(connectionDTO.getId())).withSelfRel()
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).putConnectionsConnectionID(connectionDTO.getId(), null)))
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).deleteConnectionsConnectionID(connectionDTO.getId())))
        ).add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getSchemas(connectionDTO.getId())).withRel("schemas"));
    }

    private SchemaDTO addHypermediaSchema(SchemaDTO schemaDTO, Long connectionId) {
        schemaDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTables(schemaDTO.getSchemaName(), connectionId)).withRel("tables"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTableStatistics(connectionId, schemaDTO.getSchemaName())).withRel("statistics"));
        return schemaDTO;
    }

    private TableDTO addHypermediaTable(TableDTO tableDTO, String schemaName, Long connectionId) {
        tableDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getColumns(schemaName, tableDTO.getTableName(), connectionId)).withRel("columns"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getPreview(connectionId, schemaName, tableDTO.getTableName())).withRel("data"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTableColumnStatistics(connectionId, schemaName, tableDTO.getTableName())).withRel("statistics"));
        return tableDTO;
    }


}
