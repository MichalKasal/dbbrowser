package cz.kasal.dbbrowser.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "CONNECTION")
public class ConnectionEnt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "HOSTNAME")
    private String hostname;

    @Column(name = "PORT")
    private Integer port;

    @Column(name = "DATABASE_NAME")
    private String databaseName;

    @Column(name = "USERNAME")
    private String username;

    @Column(name  = "PASSWORD")
    private String password;





}
