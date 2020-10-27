package cz.kasal.dbbrowser.api;

import cz.kasal.dbbrowser.connection.api.ConnectionsApiDelegate;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.service.ConnectionsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

public class ConnectionsApi implements ConnectionsApiDelegate {


    public ConnectionsApi(ConnectionsService connectionsService){
        // empty constructor
    }


    @Override
    public ResponseEntity<Void> deleteConnectionsConnectionID(Long connectionID) {
        return null;
    }

    @Override
    public ResponseEntity<ConnectionDTO> getConnectionsConnectionID(Long connectionID) {
        return null;
    }

    @Override
    public ResponseEntity<ConnectionDTO> postConnections(ConnectionDTO connectionDTO) {



        connectionDTO.add(linkTo(methodOn(ConnectionsApi.class).getConnectionsConnectionID(connectionDTO.getId())).withSelfRel());
        URI uri = MvcUriComponentsBuilder.fromController(getClass())
                .path("/{id}")
                .buildAndExpand(connectionDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(connectionDTO);
    }

    @Override
    public ResponseEntity<Void> putConnectionsConnectionID(Long connectionID) {
        return null;
    }
}
