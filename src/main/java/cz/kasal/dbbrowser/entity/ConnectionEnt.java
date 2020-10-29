package cz.kasal.dbbrowser.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Represents CONNECTION table in DB
 */
@Data
@Entity
@Table(name = "CONNECTION")
public class ConnectionEnt {

    /**
     * PK of connection
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * Unique name of connection used to identify connection by user
     */
    @Column(name = "NAME", unique = true)
    private String name;

    /**
     * Hostname of database
     */
    @Column(name = "HOSTNAME")
    private String hostname;

    /**
     * Number of port where database accepts connections
     */
    @Min(1)
    @Max(65535)
    @Column(name = "PORT")
    private Integer port;

    /**
     * Name of database to connect
     */
    @Column(name = "DATABASE_NAME", length = 63)
    private String databaseName;

    /**
     * Username used for login to database
     */
    @Column(name = "USERNAME", length = 63)
    private String username;

    /**
     * Password used for login to database
     */
    @Column(name = "PASSWORD", length = 63)
    private String password;


}
