package pl.coderstrust.database;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import pl.coderstrust.database.mapper.InvoiceRowMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

@Repository
@ConditionalOnProperty(name = "pl.coderstrust.database", havingValue = "postgresql")
public class SQLDatabase implements Database {
    private static final String ENCODING = "UTF-8";
    private final JdbcTemplate jdbcTemplate;
    private final String GET_BY_ID = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET_BY_ID.sql"), ENCODING);
    private final String GET_BY_NUMBER = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET_BY_NUMBER.sql"), ENCODING);
    private final String GET_ALL = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/GET_ALL.sql"), ENCODING);
    private final String DELETE_ALL_DATA = FileUtils.readFileToString(new File("src/main/resources/sqlScripts/DELETE_ALL_DATA.sql"), ENCODING);

    @Autowired
    public SQLDatabase(JdbcTemplate jdbcTemplate) throws IOException {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Invoice save(Invoice invoice) throws DatabaseOperationException {
        if (invoice == null) {
            throw new IllegalArgumentException("invoice cannot be null.");
        }
        List<InvoiceEntry> invoiceEntries = new ArrayList<>();
        invoiceEntries = insertAllInvoiceEntries(invoice.getId(), invoice.getEntries());
        if (!companyExists(invoice.getBuyer().getId())) {

            if (!companyExists(invoice.getSeller().getId())) {
                return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), invoice.getSeller(), invoiceEntries);
        }
        if (!companyExists(invoice.getSeller().getId())) {

            if (!companyExists(invoice.getBuyer().getId())) {
                return insertInvoiceTable(invoice, insertCompanyTable(invoice.getBuyer()), insertCompanyTable(invoice.getSeller()), invoiceEntries);
            }
            return insertInvoiceTable(invoice, invoice.getBuyer(), insertCompanyTable(invoice.getSeller()), invoiceEntries);
        }
        return insertInvoiceTable(invoice, invoice.getBuyer(), invoice.getSeller(), invoice.getEntries());
    }

    @Override
    public void delete(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            if(jdbcTemplate.update("DELETE FROM invoice_entries WHERE invoice_id=?", id)==0)
            jdbcTemplate.update("DELETE FROM invoice WHERE id=?", id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseOperationException("An error occured during deleting invoice by Id");
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during deleting invoice by Id");
        }
    }

    @Override
    public Optional<Invoice> getById(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            Optional<Invoice>invoice=jdbcTemplate.query(GET_BY_ID, new Object[] {id}, new InvoiceRowMapper()).stream().findFirst();
            if(invoice.isPresent()){
                return invoice;
            }
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting invoice by Id");
        }
    }

    @Override
    public Optional<Invoice> getByNumber(String number) throws DatabaseOperationException {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null.");
        }
        try {
            Optional<Invoice>invoice=jdbcTemplate.query(GET_BY_NUMBER, new Object[] {number}, new InvoiceRowMapper()).stream().findFirst();
            if(invoice.isPresent()){
                return invoice;
            }
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting invoice by Number");
        }
    }

    @Override
    public Collection<Invoice> getAll() throws DatabaseOperationException {
        try {
            return jdbcTemplate.query(GET_ALL, new InvoiceRowMapper()).stream().distinct().collect(Collectors.toList());
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting all invoices from database");
        }
    }

    @Override
    public void deleteAll() throws DatabaseOperationException {
        try {
            jdbcTemplate.execute(DELETE_ALL_DATA);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during getting all invoices from database");
        }
    }

    @Override
    public boolean exists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT ID FROM invoice WHERE ID=?)", new Object[] {id}, Boolean.class);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during checking if invoice exists");
        }
    }

    @Override
    public long count() throws DatabaseOperationException {
        try {
            return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM INVOICE", Integer.class);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during counting all invoices");
        }
    }

    private boolean companyExists(Long id) throws DatabaseOperationException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null.");
        }
        try {
            return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT ID FROM COMPANY WHERE ID=?)", new Object[] {id}, Boolean.class);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("An error occured during checking if company exists");
        }
    }

    private Company insertCompanyTable(Company company) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO COMPANY(account_number,address,email,name,phone_number,tax_id) VALUES(?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, company.getAccountNumber());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getEmail());
            ps.setString(4, company.getName());
            ps.setString(5, company.getPhoneNumber());
            ps.setString(6, company.getTaxId());
            return ps;
        }, holder);
        return buildCompany(company, Long.valueOf(holder.getKeys().get("id").toString()));
    }

    private InvoiceEntry insertInvoiceEntryTable(InvoiceEntry invoiceEntry) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO invoice_entry(description, gross_value, net_value, price,quantity, vat_rate) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, invoiceEntry.getDescription());
            ps.setBigDecimal(2, invoiceEntry.getGrossValue());
            ps.setBigDecimal(3, invoiceEntry.getNetValue());
            ps.setBigDecimal(4, invoiceEntry.getPrice());
            ps.setLong(5, invoiceEntry.getQuantity());
            ps.setFloat(6, invoiceEntry.getVatRate().getValue());
            return ps;
        }, holder);
        return buildInvoiceEntry(invoiceEntry, Long.valueOf(holder.getKeys().get("id").toString()));
    }

    private void insertInvoiceEntriesTable(Long invoiceId, List<InvoiceEntry>invoiceEntries) {
        for (int i = 0; i <invoiceEntries.size() ; i++) {
            jdbcTemplate.update(
                "INSERT INTO invoice_entries(invoice_id,entries_id) VALUES(?,?)",
                invoiceId, invoiceEntries.get(i).getId());
        }
    }

    private Invoice insertInvoiceTable(Invoice invoice, Company buyer, Company seller, List<InvoiceEntry> invoiceEntries) {
        KeyHolder holder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO INVOICE(due_date, issued_date, number, buyer_id, seller_id) VALUES(? ,?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, Date.valueOf(invoice.getDueDate()));
            ps.setDate(2, Date.valueOf(invoice.getIssuedDate()));
            ps.setString(3, invoice.getNumber());
            ps.setLong(4, buyer.getId());
            ps.setLong(5, seller.getId());
            return ps;
        }, holder);
        insertInvoiceEntriesTable(Long.valueOf(holder.getKeys().get("id").toString()), invoiceEntries);
        return buildInvoice(Long.valueOf(holder.getKeys().get("id").toString()), invoice, buyer, seller, invoiceEntries);
    }

    private List<InvoiceEntry> insertAllInvoiceEntries(Long invoiceId, List<InvoiceEntry> invoiceEntries) {
        List<InvoiceEntry> entriesToAdd = new ArrayList<>();
        for (int i = 0; i < invoiceEntries.size(); i++) {
                InvoiceEntry invoiceEntry = insertInvoiceEntryTable(invoiceEntries.get(i));
                entriesToAdd.add(insertInvoiceEntryTable(invoiceEntries.get(i)));
        }
        return entriesToAdd;
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

    private Company buildCompany(Company company, Long id) {
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

    private InvoiceEntry buildInvoiceEntry(InvoiceEntry invoiceEntry, Long id) {
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

}
