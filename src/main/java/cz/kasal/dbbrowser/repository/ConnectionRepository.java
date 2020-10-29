package cz.kasal.dbbrowser.repository;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA Repository providing CRUD operations over Connection
 */
@Repository
public interface ConnectionRepository extends JpaRepository<ConnectionEnt, Long> {

}
