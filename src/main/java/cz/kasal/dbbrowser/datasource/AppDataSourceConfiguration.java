package cz.kasal.dbbrowser.datasource;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.model.ConnectionDTO;
import cz.kasal.dbbrowser.repository.ConnectionRepository;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.util.Map;


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
               ConnectionEnt connectionDTO = connectionRepository.findById(Long.parseLong(at.get("connectionId"))).get();
               DataSourceBuilder<?> dsBuilder = DataSourceBuilder.create();
               StringBuilder stringBuilder = new StringBuilder();
               stringBuilder.append("jdbc:postgresql://").append(connectionDTO.getHostname()).append(":").append(connectionDTO.getPort()).append("/").append(connectionDTO.getDatabaseName());
               dsBuilder.driverClassName("org.postgresql.Driver");
               dsBuilder.url(stringBuilder.toString());
               dsBuilder.username(connectionDTO.getUsername());
               dsBuilder.password(connectionDTO.getPassword());
               return dsBuilder.build();
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
