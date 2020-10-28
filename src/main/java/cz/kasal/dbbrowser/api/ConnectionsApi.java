package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.connection.api.ConnectionsApiDelegate;
import cz.kasal.dbbrowser.model.ColumnDTO;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.model.SchemaDTO;
import cz.kasal.dbbrowser.model.TableDTO;
import cz.kasal.dbbrowser.service.ConnectionsService;
import cz.kasal.dbbrowser.service.DatabaseListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.xml.validation.Schema;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class ConnectionsApi implements ConnectionsApiDelegate {


    private ConnectionsService connectionsService;

    private DatabaseListingService databaseListingService;

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
        addHypermedia(connectionDTO);
        return ResponseEntity.ok(connectionDTO);
    }

    @Override
    public ResponseEntity<ConnectionDTO> postConnections(ConnectionDTO connectionDTO) {
        connectionDTO = connectionsService.saveConnection(connectionDTO);
        addHypermedia(connectionDTO);
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(connectionDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(connectionDTO);
    }

    @Override
    public ResponseEntity<ConnectionDTO> putConnectionsConnectionID(Long connectionID, ConnectionDTO connectionDTO) {
        connectionDTO = connectionsService.updateConnection(connectionDTO, connectionID);
        addHypermedia(connectionDTO);
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
    public ResponseEntity<List<ColumnDTO>> getColumns(String schemaName, String tableName, String connectionId) {
        return null;
    }

    @Override
    public ResponseEntity<List<Object>> getPreview(String connectionId, String schemaName, String tableName) {
        return null;
    }

    @Override
    public ResponseEntity<List<SchemaDTO>> getSchemas(String connectionId) {
        List<SchemaDTO> schemaDTOs = databaseListingService.getAllSchemas();
       return ResponseEntity.ok(schemaDTOs);
    }



    @Override
    public ResponseEntity<List<TableDTO>> getTables(String schemaName, String connectionId) {
        List<TableDTO> tableDTOs = databaseListingService.findAllTables(schemaName);
        return ResponseEntity.ok(tableDTOs);
    }






    private void addHypermedia(ConnectionDTO connectionDTO){
        connectionDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getConnectionsConnectionID(connectionDTO.getId())).withSelfRel()
               .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).putConnectionsConnectionID( connectionDTO.getId(),null)))
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).deleteConnectionsConnectionID(connectionDTO.getId())))
        );
    }

}
