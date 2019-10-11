package pl.coderstrust.configuration;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "postgresql")
@PropertySource(value = {"classpath:application-postgresql.properties"})
public class SQLConfiguration {

    @Value("${spring.datasource.url}")
    private String defaultDb;

    @Value("${spring.datasource.username}")
    private String login;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource postgresqlDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(defaultDb);
        dataSource.setUsername(login);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.setResultsMapCaseInsensitive(true);
        return jdbcTemplate;
    }
}
