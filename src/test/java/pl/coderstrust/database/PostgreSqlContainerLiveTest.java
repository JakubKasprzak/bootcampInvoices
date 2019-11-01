package pl.coderstrust.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.CollectionUtils;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import pl.coderstrust.generators.InvoiceGenerator;
import pl.coderstrust.generators.NumberGenerator;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.config.TestDataBaseConfiguration;

@SpringBootTest(classes = TestDataBaseConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({SpringExtension.class})
public class PostgreSqlContainerLiveTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SQLDatabase sqlDatabase;

    Random random;
    Invoice testedInvoice;
    long numberOfGeneratedInvoices;

    private static final String ENCODING = "UTF-8";
    private final String CREATE_TABLE =FileUtils.readFileToString(new File("src/main/resources/sqlScripts/CREATE_TABLES.sql"),ENCODING);
    private final String DROP_TABLE =FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DROP_ALL_TABLES.sql"),ENCODING);

    @Autowired
    PostgreSQLContainer postgresContainer;

    Collection<Invoice> listOfInvoicesAddedToDatabase;

    public PostgreSqlContainerLiveTest() throws IOException {
    }

    @BeforeEach
    void prepareDatabaseForTest() throws IOException {

        random=new Random();

        listOfInvoicesAddedToDatabase =new ArrayList<>();
        List<InvoiceEntry> listOfInvoiceEntries=new ArrayList<>();

        jdbcTemplate.execute(CREATE_TABLE);
        SimpleJdbcInsert simpleJdbcInsertCompany=new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("company")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoice=new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("invoice")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoiceEntry=new SimpleJdbcInsert(jdbcTemplate.getDataSource())
            .withTableName("invoice_entry")
            .usingGeneratedKeyColumns("id");
        SimpleJdbcInsert simpleJdbcInsertInvoiceEntries=new SimpleJdbcInsert(jdbcTemplate.getDataSource()).
            withTableName("invoice_entries");

        numberOfGeneratedInvoices=NumberGenerator.generateRandomNumber(1);
        for (int i = 0; i < numberOfGeneratedInvoices; i++) {
            Invoice generatedInvoice=InvoiceGenerator.generateRandomInvoice();
            Number sellerKey=simpleJdbcInsertCompany.executeAndReturnKey(mapCompany(generatedInvoice.getSeller()));
            Number buyerKey=simpleJdbcInsertCompany.executeAndReturnKey(mapCompany(generatedInvoice.getBuyer()));
            Number invoiceKey = simpleJdbcInsertInvoice.executeAndReturnKey(mapInvoice(generatedInvoice,buyerKey,sellerKey));
            for (int j = 0; j < generatedInvoice.getEntries().size(); j++) {
                Number invoiceKeyEntry=simpleJdbcInsertInvoiceEntry.executeAndReturnKey(mapInvoiceEntry(generatedInvoice.getEntries().get(j)));
                simpleJdbcInsertInvoiceEntries.execute(mapInvoiceEntries(invoiceKeyEntry,invoiceKey));
                listOfInvoiceEntries.add(buildInvoiceEntry(invoiceKeyEntry.longValue(),generatedInvoice.getEntries().get(j)));
            }
            listOfInvoicesAddedToDatabase.add(buildInvoice(invoiceKey.longValue(),generatedInvoice,buildCompany(buyerKey.longValue(),generatedInvoice.getBuyer()),buildCompany(sellerKey.longValue(),generatedInvoice.getSeller()),List.copyOf(listOfInvoiceEntries)));
            listOfInvoiceEntries.clear();
        }
        testedInvoice =  listOfInvoicesAddedToDatabase.stream().skip(random.nextInt(listOfInvoicesAddedToDatabase.size())).findFirst().get();
   }

    @AfterEach
    void finish() throws IOException {
        jdbcTemplate.execute(DROP_TABLE);
    }

    @Test
    void setTestedInvoice(){

    }

    @Test
    void saveMethodShouldReturnAddedInvoice() throws DatabaseOperationException {
        //Given
        Invoice givenInvoice=InvoiceGenerator.generateRandomInvoice();
        //When
        Invoice expectedInvoice=sqlDatabase.save(givenInvoice);
        //Then
        assertEquals(jdbcTemplate.queryForObject("SELECT max(id) FROM INVOICE",Long.class),expectedInvoice.getId());
    }

    @Test
    void saveMethodShouldThrowExceptionForNullInvoice() {
        Exception e =assertThrows(Exception.class, () -> sqlDatabase.save(null));

        assertEquals(IllegalArgumentException.class,e.getCause().getClass());
    }

    @Test
    void deleteMethodShouldDeleteInvoice() throws DatabaseOperationException {
        //When
        sqlDatabase.delete(testedInvoice.getId());

        //Then
        assertFalse(jdbcTemplate.queryForObject("SELECT EXISTS(SELECT ID FROM invoice WHERE ID=?)", new Object[] {testedInvoice.getId()}, Boolean.class));
    }

    @Test
    void deleteMethodShouldThrowExceptionForNullId() {
        Exception e =assertThrows(Exception.class, () -> sqlDatabase.delete(null));

        assertEquals(IllegalArgumentException.class,e.getCause().getClass());
    }

    @Test()
    void deleteMethodShouldThrowExceptionForDeletingNotExistingInvoice() throws DatabaseOperationException {
        assertThrows(DatabaseOperationException.class, () -> sqlDatabase.delete(listOfInvoicesAddedToDatabase.size()+1L));
    }

    @Test
    void getByIdMethodShouldReturnInvoiceById() throws DatabaseOperationException {
        //When
        Optional<Invoice> expectedInvoice=sqlDatabase.getById(testedInvoice.getId());

        //Then
        assertEquals(expectedInvoice.get(),testedInvoice);
    }

    @Test
    void getByIdMethodShouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotById() throws DatabaseOperationException {
    }

    @Test
    void getByIdMethodShouldThrowExceptionForNullId() {
        Exception e =assertThrows(Exception.class, () -> sqlDatabase.getById(null));

        assertEquals(IllegalArgumentException.class,e.getCause().getClass());
    }

    @Test
    void getByNumberMethodShouldReturnInvoiceByNumber() throws DatabaseOperationException {
        //When
        Optional<Invoice> expectedInvoice=sqlDatabase.getByNumber(testedInvoice.getNumber());

        //Then
        assertEquals(expectedInvoice.get(),testedInvoice);
    }

    @Test
    void getByNumberMethodShouldReturnEmptyOptionalWhenNonExistingInvoiceIsGotByNumber() throws DatabaseOperationException {
        //When
        Optional<Invoice>expected=sqlDatabase.getByNumber("No existent number");

        //Then
        assertEquals(Optional.empty(),expected);
    }
    @Test
    void getAllMethodShouldReturnAllInvoices() throws DatabaseOperationException {
        //Given
        Collection<Invoice>givenListOfInvoices=listOfInvoicesAddedToDatabase;
        //When
        Collection<Invoice> expectedListOfInvoices=sqlDatabase.getAll();
        //Then
        assertEquals(givenListOfInvoices,expectedListOfInvoices);
    }

    @Test
    void deleteAllMethodShouldDeleteAllInvoices() throws DatabaseOperationException {
        //When
        sqlDatabase.deleteAll();

        //Then
        assertEquals(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM INVOICE",Object.class),0L);
    }

    @Test
    void existsMethodShouldReturnTrueForExistingInvoice() throws DatabaseOperationException {
        //When
        boolean expected = sqlDatabase.exists(testedInvoice.getId());
        //Then
        assertTrue(expected);
    }

    @Test
    void existsMethodShouldReturnFalseForNotExistingInvoice() throws DatabaseOperationException {
        //When
        boolean expected = sqlDatabase.exists(100L);
        //Then
        assertFalse(expected);
    }

    @Test
    void existsMethodShouldThrowExceptionForNullId() {
        Exception e =assertThrows(Exception.class, () -> sqlDatabase.exists(null));

        assertEquals(IllegalArgumentException.class,e.getCause().getClass());
    }

    @Test
    void countMethodShouldReturnNumberOfInvoices() throws DatabaseOperationException {
        //Given
        int numberOfInvoices=listOfInvoicesAddedToDatabase.size();
        //When
        long expectedNumberofInvoices= sqlDatabase.count();
        //Then
        assertEquals(numberOfInvoices,expectedNumberofInvoices);
    }

    private Invoice buildInvoice(long invoiceId, Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        return Invoice.builder()
            .withId(invoiceId)
            .withEntries(invoiceEntries)
            .withNumber(invoice.getNumber())
            .withBuyer(buyer)
            .withSeller(seller)
            .withDueDate(invoice.getDueDate())
            .withIssuedDate(invoice.getIssuedDate())
            .build();
    }

    private Company buildCompany(long id, Company company) {
        return Company.builder()
            .withId(id)
            .withAccountNumber(company.getAccountNumber())
            .withAddress(company.getAddress())
            .withEmail(company.getEmail())
            .withName(company.getName())
            .withPhoneNumber(company.getPhoneNumber())
            .withTaxId(company.getTaxId())
            .build();
    }

    private InvoiceEntry buildInvoiceEntry(Long id, InvoiceEntry invoiceEntry) {
        return InvoiceEntry.builder()
            .withId(id)
            .withDescription(invoiceEntry.getDescription())
            .withGrossValue(invoiceEntry.getGrossValue())
            .withNetValue(invoiceEntry.getNetValue())
            .withPrice(invoiceEntry.getPrice())
            .withQuantity(invoiceEntry.getQuantity())
            .withVatRate(invoiceEntry.getVatRate())
            .build();
    }

    private Map mapCompany(Company company) {
        return  Map.of(
            "account_number",company.getAccountNumber(),
            "address",company.getAddress(),
            "email",company.getEmail(),
            "name",company.getName(),
            "phone_number",company.getPhoneNumber(),
            "tax_id",company.getTaxId());
    }

    private Map mapInvoice(Invoice invoice,Number buyerKey,Number sellerKey) {
        return Map.of(
            "due_date",invoice.getDueDate(),
            "issued_date",invoice.getIssuedDate(),
            "number",invoice.getNumber(),
            "buyer_id",buyerKey,
            "seller_id",sellerKey);
    }

    private Map mapInvoiceEntry(InvoiceEntry invoiceEntry) {
        return Map.of(
            "description",invoiceEntry.getDescription(),
            "gross_value",invoiceEntry.getGrossValue(),
            "net_value",invoiceEntry.getNetValue(),
            "price",invoiceEntry.getPrice(),
            "quantity",invoiceEntry.getQuantity(),
            "vat_rate",invoiceEntry.getVatRate().getValue());
    }
    private Map mapInvoiceEntries(Number invoiceKeyEntry,Number invoiceKey) {
        return Map.of("invoice_id",invoiceKey,
            "entries_id",invoiceKeyEntry);
    }
}
