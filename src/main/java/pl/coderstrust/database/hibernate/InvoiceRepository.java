package pl.coderstrust.database.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface InvoiceRepository extends JpaRepository<HibernateInvoice, Long> {
}
