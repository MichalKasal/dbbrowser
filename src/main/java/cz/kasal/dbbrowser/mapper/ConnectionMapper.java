package cz.kasal.dbbrowser.mapper;


import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ConnectionMapper {

    public abstract ConnectionEnt mapToEntity (ConnectionDTO connectionDTO);

    public abstract ConnectionDTO mapToDto (ConnectionEnt connectionEnt);

}
