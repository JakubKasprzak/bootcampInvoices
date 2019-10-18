package pl.coderstrust.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import pl.coderstrust.database.hibernate.InvoiceRepository;

@Configuration
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "hibernate")
@EnableJpaRepositories(basePackageClasses = {InvoiceRepository.class})
@Import({DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
public class HibernateConfiguration {
}
