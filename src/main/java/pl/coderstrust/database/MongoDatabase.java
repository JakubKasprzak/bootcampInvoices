package pl.coderstrust.database;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.mongo.MongoInvoice;
import pl.coderstrust.database.mongo.MongoModelMapper;
import pl.coderstrust.model.Invoice;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "mongo")
public class MongoDatabase implements Database {
    private final MongoTemplate mongoTemplate;
    private final MongoModelMapper modelMapper;
    private AtomicLong lastId;

    private static Logger log = LoggerFactory.getLogger(HibernateDatabase.class);

    public MongoDatabase(MongoTemplate mongoTemplate, MongoModelMapper modelMapper) {
        if (mongoTemplate == null) {
            log.error("Database is empty.");
            throw new IllegalArgumentException("Mongo template cannot be null.");
        }
        if (modelMapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null.");
        }
        this.mongoTemplate = mongoTemplate;
        this.modelMapper = modelMapper;
        init();
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            log.error("Attempt to add null invoice to database.");
            throw new IllegalArgumentException("Invoice cannot be null.");
        }
        try {
            MongoInvoice invoiceInDatabase = getInvoiceById(invoice.getId());
            if (invoiceInDatabase == null) {
                log.debug("Invoice has been successfully added to database.");
                return insertInvoice(invoice);
            }
            log.debug("Invoice has been successfully updated.");
            return updateInvoice(invoice, invoiceInDatabase.getMongoId());
        } catch (Exception e) {
            String message = "An error occurred during saving invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private Invoice insertInvoice(Invoice invoice) {
        Invoice invoiceToBeInserted = Invoice.builder()
            .withInvoice(invoice)
            .withId(lastId.incrementAndGet())
            .build();
        return modelMapper.mapToInvoice(mongoTemplate.insert(modelMapper.mapToMongoInvoice(invoiceToBeInserted)));
    }

    private Invoice updateInvoice(Invoice invoice, String mongoId) {
        MongoInvoice updatedInvoice = MongoInvoice.builder()
            .withMongoId(mongoId)
            .withId(invoice.getId())
            .withNumber(invoice.getNumber())
            .withBuyer(invoice.getBuyer())
            .withSeller(invoice.getSeller())
            .withDueDate(invoice.getDueDate())
            .withIssuedDate(invoice.getIssuedDate())
            .withEntries(invoice.getEntries())
            .build();
        return modelMapper.mapToInvoice(mongoTemplate.save(updatedInvoice));
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to delete invoice by null id.");
            throw new IllegalArgumentException("Invoice id cannot be null.");
        }
        try {
            MongoInvoice invoice = mongoTemplate.findAndRemove(Query.query(Criteria.where("id").is(id)), MongoInvoice.class);
            if (invoice == null) {
                log.error("Attempt to delete not existing invoice.");
                throw new DatabaseOperationException(String.format("There is no invoice with id: %s", id));
            }
        } catch (Exception e) {
            String message = "An error occurred during deleting invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to get invoice by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            MongoInvoice invoice = getInvoiceById(id);
            if (invoice != null) {
                return Optional.of(modelMapper.mapToInvoice(invoice));
            }
            log.debug("There is no invoice with id {}.", id);
            return Optional.empty();
        } catch (Exception e) {
            String message = "An error occurred during getting invoice by id.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private MongoInvoice getInvoiceById(Long id) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), MongoInvoice.class);
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            log.error("Attempt to get invoice by null number.");
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            MongoInvoice invoice = mongoTemplate.findOne(Query.query(Criteria.where("number").is(number)), MongoInvoice.class);
            if (invoice != null) {
                return Optional.of(modelMapper.mapToInvoice(invoice));
            }
            log.debug("There is no invoice with number {}.", number);
            return Optional.empty();
        } catch (Exception e) {
            String message = "An error occurred during getting invoice by number.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return modelMapper.mapToInvoices(mongoTemplate.findAll(MongoInvoice.class));
        } catch (Exception e) {
            String message = "An error occurred during getting all invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            mongoTemplate.dropCollection(MongoInvoice.class);
        } catch (Exception e) {
            String message = "An error occurred during deleting all invoice.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            log.error("Attempt to check if invoice exists by null id.");
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return mongoTemplate.exists(Query.query(Criteria.where("id").is(id)), MongoInvoice.class);
        } catch (Exception e) {
            String message = "An error occurred during checking if invoice exists.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return mongoTemplate.count(new Query(), MongoInvoice.class);
        } catch (Exception e) {
            String message = "An error occurred during counting invoices.";
            log.error(message, e);
            throw new DatabaseOperationException(message, e);
        }
    }

    private void init() {
        Query query = new Query();
        query.with(new Sort(Sort.Direction.DESC, "id"));
        query.limit(1);
        MongoInvoice invoice = mongoTemplate.findOne(query, MongoInvoice.class);
        if (invoice == null) {
            lastId = new AtomicLong(0);
            return;
        }
        lastId = new AtomicLong(invoice.getId());
    }
}
