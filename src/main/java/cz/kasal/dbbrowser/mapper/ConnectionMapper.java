package cz.kasal.dbbrowser.mapper;


import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import org.mapstruct.Mapper;

/**
 * Mapstruct mapper maps Connection
 */
@Mapper(componentModel = "spring")
public abstract class ConnectionMapper {

    /**
     * Maps ConnectionDTO to Connection database entity
     *
     * @param connectionDTO dto object
     * @return connection entity
     */
    public abstract ConnectionEnt mapToEntity(ConnectionDTO connectionDTO);

    /**
     * Maps Connection entity to dto
     *
     * @param connectionEnt connection entity
     * @return dto
     */
    public abstract ConnectionDTO mapToDto(ConnectionEnt connectionEnt);

}
