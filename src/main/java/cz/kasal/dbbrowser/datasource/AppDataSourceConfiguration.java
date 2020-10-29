package cz.kasal.dbbrowser.datasource;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.repository.ConnectionRepository;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Optional;


@Configuration
@ComponentScan(basePackages = "cz.kasal.dbbrowser")
public class AppDataSourceConfiguration {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }


    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(primaryDataSource());
    }


    @Lazy
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataSource createdDataSource(ConnectionRepository connectionRepository) {

       RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
       if(requestAttributes != null) {
           Map<Object,String> at = (Map<Object,String>) requestAttributes.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
           if(at != null) {
               Optional<ConnectionEnt> connectionDTOOpt = connectionRepository.findById(Long.parseLong(at.get("connectionId")));
               if (connectionDTOOpt.isPresent()) {
                   ConnectionEnt connection = connectionDTOOpt.get();
                   DataSourceBuilder<?> dsBuilder = DataSourceBuilder.create();
                   StringBuilder stringBuilder = new StringBuilder();
                   stringBuilder.append("jdbc:postgresql://").append(connection.getHostname()).append(":").append(connection.getPort()).append("/").append(connection.getDatabaseName());
                   dsBuilder.driverClassName("org.postgresql.Driver");
                   dsBuilder.url(stringBuilder.toString());
                   dsBuilder.username(connection.getUsername());
                   dsBuilder.password(connection.getPassword());
                   return dsBuilder.build();
               }
           }
        }
           throw new BeanCreationException("Cannot continue processing request");
    }

    @Lazy
    @Qualifier("createdJdbcTemplate")
    @Bean
    public JdbcTemplate createdJdbcTemplate(ConnectionRepository connectionRepository) {
        return new JdbcTemplate(createdDataSource(connectionRepository));
    }

}
