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

@Data
@Entity
@Table(name = "CONNECTION")
public class ConnectionEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", unique = true)
    private String name;

    @Column(name = "HOSTNAME")
    private String hostname;

    @Min(1)
    @Max(65535)
    @Column(name = "PORT")
    private Integer port;

    @Column(name = "DATABASE_NAME", length = 63)
    private String databaseName;

    @Column(name = "USERNAME", length = 63)
    private String username;

    @Column(name  = "PASSWORD", length = 63)
    private String password;





}
