package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.connection.api.ConnectionsApiDelegate;
import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ColumnStatisticsDTO;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Implements ConnectionApiDelegate using delegate pattern and serves as entrypoint for processing user request
 * in this demo application.
 * <p>
 * For documentation see generated swagged docs
 */
@Service
public class ConnectionsApi implements ConnectionsApiDelegate {

    private final ConnectionsService connectionsService;

    private final DatabaseListingService databaseListingService;

    /**
     * Constructor injecting all services containing business logic needed for request processing in this class
     * @param connectionsService     service contains logic of processing DB connection requests
     * @param databaseListingService service contains logic of processing DB listing requests
     */
    @Autowired
    public ConnectionsApi(ConnectionsService connectionsService, DatabaseListingService databaseListingService) {
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
        List preview = databaseListingService.previewTable(schemaName, tableName);
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

    /**
     * Adds HATEOAS metadata to ConnectionDTO objects
     *
     * @param connectionDTO input object
     */
    private void addHypermediaConnection(ConnectionDTO connectionDTO) {
        connectionDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getConnectionsConnectionID(connectionDTO.getId())).withSelfRel()
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).putConnectionsConnectionID(connectionDTO.getId(), null)))
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).deleteConnectionsConnectionID(connectionDTO.getId())))
        ).add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getSchemas(connectionDTO.getId())).withRel("schemas"));
    }

    /**
     * Adds HATEOAS metadata to SchemaDTO objects
     *
     * @param schemaDTO    input object
     * @param connectionId ID of connection used in request
     * @return SchemaDTO containing HATEOAS metadata
     */
    private SchemaDTO addHypermediaSchema(SchemaDTO schemaDTO, Long connectionId) {
        schemaDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTables(schemaDTO.getSchemaName(), connectionId)).withRel("tables"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTableStatistics(connectionId, schemaDTO.getSchemaName())).withRel("statistics"));
        return schemaDTO;
    }

    /**
     * Adds HATEOAS metadata to TableDTO objects
     *
     * @param tableDTO     input object
     * @param schemaName   name of schema used in request
     * @param connectionId connection id used in request
     * @return TableDTO containing HATEOAS metadata
     */
    private TableDTO addHypermediaTable(TableDTO tableDTO, String schemaName, Long connectionId) {
        tableDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getColumns(schemaName, tableDTO.getTableName(), connectionId)).withRel("columns"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getPreview(connectionId, schemaName, tableDTO.getTableName())).withRel("data"))
                .add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getTableColumnStatistics(connectionId, schemaName, tableDTO.getTableName())).withRel("statistics"));
        return tableDTO;
    }


}
