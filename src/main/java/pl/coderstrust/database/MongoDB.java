package pl.coderstrust.database;

import com.mongodb.MongoClientURI;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import pl.coderstrust.database.mongo.MongoInvoice;
import pl.coderstrust.model.Invoice;

public class MongoDB implements Database {

    MongoOperations invoiceRepository = new MongoTemplate(new SimpleMongoDbFactory(new MongoClientURI("something")));

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {

            MongoInvoice savedInvoice = invoiceRepository.save(modelMapper.mapToMongoInvoice(invoice));
            return modelMapper.mapToInvoice(savedInvoice);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during saving invoice.", e);
        }
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        if (!invoiceRepository.existsById(id)) {
            throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
        }
        try {
            invoiceRepository.deleteById(id);
        } catch (NonTransientDataAccessException | NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during deleting invoice.", e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<MongoInvoice> invoice = invoiceRepository.findById(id);
            if (invoice.isPresent()) {
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            return Optional.empty();
        } catch (NoSuchElementException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by id.", e);
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            Example<MongoInvoice> example = Example.of(modelMapper.mapToMongoInvoice(new Invoice.Builder().withNumber(number).build()));
            Optional<MongoInvoice> invoice = invoiceRepository.findOne(example);
            if (invoice.isPresent()) {
                return Optional.of(modelMapper.mapToInvoice(invoice.get()));
            }
            return Optional.empty();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting invoice by number.", e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToInvoices(invoiceRepository.findAll());
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting all invoices.", e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            invoiceRepository.deleteAll();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during deleting all invoices.", e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return invoiceRepository.existsById(id);
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during checking if invoice exists.", e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return invoiceRepository.count();
        } catch (NonTransientDataAccessException e) {
            throw new DatabaseOperationException("An error occurred during getting number of invoices.", e);
        }
    }
}