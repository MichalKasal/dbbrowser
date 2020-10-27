package cz.kasal.dbbrowser.service;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.mapper.ConnectionMapper;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.repository.ConnectionRepository;

import javax.persistence.EntityNotFoundException;

public class ConnectionsServiceImpl implements ConnectionsService {


    private ConnectionRepository connectionRepository;

    private ConnectionMapper connectionMapper;


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
                .orElseThrow(EntityNotFoundException::new) ;
    }

    @Override
    public void deleteConnection(Long connectionID) {
        connectionRepository.deleteById(connectionID);
    }

    @Override
    public ConnectionDTO updateConnection(ConnectionDTO connectionDTO) {
       // in this demonstration locking is not implemented
        ConnectionEnt connectionEntity = connectionRepository.findById(connectionDTO.getId())
               .map(connectionEnt -> {
            connectionMapper.mapToEntity(connectionDTO);
            return connectionRepository.save(connectionEnt); })
               .orElseGet(() -> connectionRepository.save(connectionMapper.mapToEntity(connectionDTO)));
       return connectionMapper.mapToDto(connectionEntity);
    }


}
