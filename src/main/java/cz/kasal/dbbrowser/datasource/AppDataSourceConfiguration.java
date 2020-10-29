package cz.kasal.dbbrowser.datasource;

import cz.kasal.dbbrowser.entity.ConnectionEnt;
import cz.kasal.dbbrowser.repository.ConnectionRepository;
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


/**
 * Configures datasources used in application.
 * Primary datasource is used for manipulating internal data of Connection objects,
 * createdDataSource is used for querying databases specified by Connection object.
 */
@Configuration
@ComponentScan(basePackages = "cz.kasal.dbbrowser")
public class AppDataSourceConfiguration {

    /**
     * Primary datasource is used for manipulating internal data of Connection objects.
     * Cofigured in application.properties
     *
     * @return datasource properties from application.properties
     */
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }


    /**
     * Primary datasource
     *
     * @return configured DataSource
     */
    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    /**
     * Primary jdbcTemplate
     *
     * @return configured JdbcTemplate
     */
    @Bean
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(primaryDataSource());
    }


    /**
     * Creates datasource used in ListingRepository for querying databases
     * specified by user request (using connection ID)
     *
     * @param connectionRepository connectionRepository is used for obtaining data
     *                             about DB connection parameters
     * @return configured datasource
     */
    @Lazy
    @Bean
    @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public DataSource createdDataSource(ConnectionRepository connectionRepository) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            Map<Object, String> at = (Map<Object, String>) requestAttributes.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (at != null) {
                Optional<ConnectionEnt> connectionDTOOpt = connectionRepository.findById(Long.parseLong(at.get("connectionId")));
                if (connectionDTOOpt.isPresent()) {
                    System.out.println(connectionDTOOpt.toString());
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
                throw new IllegalStateException("Cannot continue processing request, connection not found for " + at.get("connectionId"));
            }
            throw new IllegalStateException("Cannot continue processing request, bad request format");
        }
        throw new IllegalStateException("Cannot continue processing request");
    }

    /**
     * Configured JDBCTemplate for querying user specified databases
     *
     * @param connectionRepository connectionRepository is used for obtaining data
     *                             about DB connection parameters
     * @return configured jdbc template
     */
    @Lazy
    @Qualifier("clientJdbcTemplate")
    @Bean
    public JdbcTemplate clientJdbcTemplate(ConnectionRepository connectionRepository) {
        return new JdbcTemplate(createdDataSource(connectionRepository));
    }

}
