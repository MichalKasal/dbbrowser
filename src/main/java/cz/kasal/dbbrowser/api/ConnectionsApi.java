package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.connection.api.ConnectionsApiDelegate;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.service.ConnectionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Service
public class ConnectionsApi implements ConnectionsApiDelegate {


    private ConnectionsService connectionsService;

    public ConnectionsApi(ConnectionsService connectionsService){
        this.connectionsService = connectionsService;
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

    private void addHypermedia(ConnectionDTO connectionDTO){
        connectionDTO.add(linkTo(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).getConnectionsConnectionID(connectionDTO.getId())).withSelfRel()
               .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).putConnectionsConnectionID( connectionDTO.getId(),null)))
                .andAffordance(afford(methodOn(cz.kasal.dbbrowser.connection.api.ConnectionsApi.class).deleteConnectionsConnectionID(connectionDTO.getId())))
        );
    }

}
