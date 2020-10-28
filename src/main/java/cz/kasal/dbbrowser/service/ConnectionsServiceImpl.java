package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.mapper.ConnectionMapper;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.repository.ConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
public class ConnectionsServiceImpl implements ConnectionsService {


    private ConnectionRepository connectionRepository;

    private ConnectionMapper connectionMapper;

    @Autowired
    public ConnectionsServiceImpl(ConnectionRepository connectionRepository, ConnectionMapper connectionMapper){
        this.connectionRepository = connectionRepository;
        this.connectionMapper = connectionMapper;
    }


    public ConnectionDTO saveConnection(ConnectionDTO connectionDTO){
        return connectionMapper.mapToDto(connectionRepository.save(connectionMapper.mapToEntity(connectionDTO)));
    }

    @Override
    public ConnectionDTO getConnection(Long connectionID) {
        return connectionRepository.findById(connectionID).map(connectionEnt -> connectionMapper.mapToDto(connectionEnt))
                .orElseThrow(() -> new EntityNotFoundException(String.format("Connection with ID: %d not found", connectionID))) ;
    }

    @Override
    public void deleteConnection(Long connectionID) {
        connectionRepository.deleteById(connectionID);
    }

    @Override
    public ConnectionDTO updateConnection(ConnectionDTO connectionDTO, Long connectionId) {
       // in this demonstration locking is not implemented
        ConnectionEnt connectionEntity = connectionRepository.findById(connectionId)
               .map(connectionEnt -> {
            connectionMapper.mapToEntity(connectionDTO);
            return connectionRepository.save(connectionEnt); })
               .orElseGet(() -> connectionRepository.save(connectionMapper.mapToEntity(connectionDTO)));
       return connectionMapper.mapToDto(connectionEntity);
    }


}
