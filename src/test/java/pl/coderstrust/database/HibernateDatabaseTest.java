package pl.coderstrust.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderstrust.database.hibernate.InvoiceRepository;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.model.Invoice;

@ExtendWith(MockitoExtension.class)
public class HibernateDatabaseTest {

    private HibernateModelMapper hibernateModelMapper;

    @Mock
    InvoiceRepository invoiceRepository;

    @InjectMocks
    HibernateDatabase database;

    @BeforeEach
    void setup() {
        database = new HibernateDatabase(invoiceRepository, hibernateModelMapper);
    }

    @Test
    void constructorClassShouldThrowExceptionForNullInvoiceRepository() {
        assertThrows(IllegalArgumentException.class, () -> new HibernateDatabase(null, hibernateModelMapper));
    }

    @Test
    void shouldSaveInvoice() throws DatabaseOperationException {
        //given
        Invoice invoiceToSave = InvoiceGenerator.generateRandomInvoice();

        //when
        Invoice result = database.save(invoiceToSave);

        //then
        assertNotNull(result.getId());
        assertEquals(invoiceToSave, database.getById(result.getId()));
    }

    //
//    @Test
//    void shouldAddInvoiceWithNullId() {
//        Invoice addedInvoice = database.save(InvoiceGenerator.generateRandomInvoiceWithNullId());
//
//        assertNotNull(addedInvoice.getId());
//        assertEquals(1L, (long) addedInvoice.getId());
//        assertEquals(storage.get(1L), addedInvoice);
//    }
//
//    @Test
//    void shouldUpdateInvoice() {
//        Invoice invoiceInDatabase = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoiceToUpdate = InvoiceGenerator.getRandomInvoiceWithSpecificId(invoiceInDatabase.getId());
//        storage.put(invoiceInDatabase.getId(), invoiceInDatabase);
//
//        Invoice updatedInvoice = database.save(invoiceToUpdate);
//
//        assertEquals(storage.get(invoiceInDatabase.getId()), updatedInvoice);
//    }
//
//    @Test
//    void shouldDeleteInvoice() throws DatabaseOperationException {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), invoice1);
//        storage.put(invoice2.getId(), invoice2);
//        Map<Long, Invoice> expected = Map.of(invoice1.getId(), invoice1);
//
//        database.delete(invoice2.getId());
//
//        assertEquals(expected, storage);
//    }
//
    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.delete(null));
    }

    @Test
    void deleteMethodShouldThrowExceptionForDeletingNotExistingInvoice() {
        assertThrows(DatabaseOperationException.class, () -> database.delete(10L));
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        assertThrows(IllegalArgumentException.class, () -> database.save(null));
    }

    //
//    @Test
//    void shouldReturnInvoiceById() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), invoice1);
//        storage.put(invoice2.getId(), invoice2);
//
//        Optional<Invoice> optionalInvoice = database.getById(invoice1.getId());
//
//        assertTrue(optionalInvoice.isPresent());
//        assertEquals(invoice1, optionalInvoice.get());
//    }
//
//    @Test
//    void shouldReturnInvoiceByNumber() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), invoice1);
//        storage.put(invoice2.getId(), invoice1);
//
//        Optional<Invoice> optionalInvoice = database.getByNumber(invoice1.getNumber());
//
//        assertTrue(optionalInvoice.isPresent());
//        assertEquals(invoice1, optionalInvoice.get());
//    }
//
//    @Test
//    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceById() {
//        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice.getId(), invoice);
//
//        Optional<Invoice> optionalInvoice = database.getById(invoice.getId() + 1L);
//
//        assertTrue(optionalInvoice.isEmpty());
//    }
//
//    @Test
//    void shouldReturnEmptyOptionalWhileGettingNonExistingInvoiceByNumber() {
//        Invoice invoice = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice.getId(), invoice);
//
//        Optional<Invoice> optionalInvoice = database.getByNumber(invoice.getNumber() + 1L);
//
//        assertTrue(optionalInvoice.isEmpty());
//    }
//
    @Test
    void getByIdShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.getById(null));
    }

    @Test
    void getByNumberShouldThrowExceptionForNullNumber() {
        assertThrows(IllegalArgumentException.class, () -> database.getByNumber(null));
    }

    //
//    @Test
//    void shouldReturnAllInvoices() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertEquals(storage.values(), database.getAll());
//    }
//
//    @Test
//    void shouldDeleteAllInvoices() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        database.deleteAll();
//
//        assertEquals(new HashMap<>(), storage);
//    }
//
//    @Test
//    void shouldReturnTrueForExistingInvoice() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertTrue(database.exists(invoice1.getId()));
//    }
//
//    @Test
//    void shouldReturnFalseForNonExistingInvoice() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertFalse(database.exists(666L));
//    }
//
    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        assertThrows(IllegalArgumentException.class, () -> database.exists(null));
    }
//
//    @Test
//    void shouldReturnNumberOfInvoices() {
//        Invoice invoice1 = InvoiceGenerator.generateRandomInvoice();
//        Invoice invoice2 = InvoiceGenerator.generateRandomInvoice();
//        storage.put(invoice1.getId(), InvoiceGenerator.generateRandomInvoice());
//        storage.put(invoice2.getId(), InvoiceGenerator.generateRandomInvoice());
//
//        assertEquals(2, database.count());
//    }
}
