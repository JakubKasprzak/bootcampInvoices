package pl.coderstrust.database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.RowMapper;
import pl.coderstrust.model.Company;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;
import pl.coderstrust.model.Vat;

public class InvoiceRowMapper implements RowMapper<Invoice> {

    List<InvoiceEntry> entries;
    long invoiceId;

    @Override
    public Invoice mapRow(ResultSet rs, int rowNum) throws SQLException {
        if(invoiceId==0){
            invoiceId=rs.getLong("ID");
        }
        if(invoiceId==rs.getLong("ID")){
            entries.add(getInvoiceEntry(rs));
        }
        if(invoiceId!=rs.getLong("ID")){
            invoiceId=rs.getLong("ID");
            entries=new ArrayList<>();
            entries.add(getInvoiceEntry(rs));
        }
        return Invoice
            .builder()
            .withId(rs.getLong("ID"))
            .withSeller(getCompanySeller(rs))
            .withBuyer(getCompanyBuyer(rs))
            .withNumber(rs.getString("number"))
            .withIssuedDate(rs.getDate("issued_date").toLocalDate())
            .withDueDate(rs.getDate("due_date").toLocalDate())
            .withEntries(entries)
            .build();
    }

    public InvoiceRowMapper() {
        entries = new ArrayList<>();
    }

    private Company getCompanyBuyer(ResultSet rs) throws SQLException {
        return Company.builder()
            .withId(rs.getLong("buyer_id"))
            .withEmail(rs.getString("buyer_email"))
            .withAddress(rs.getString("buyer_address"))
            .withAccountNumber(String.valueOf(rs.getString("buyer_account_number")))
            .withName(rs.getString("buyer_name"))
            .withPhoneNumber(rs.getString("buyer_phone_number"))
            .withTaxId(rs.getString("buyer_tax_id"))
            .build();
    }

    private Company getCompanySeller(ResultSet rs) throws SQLException {
        return Company.builder()
            .withId(rs.getLong("seller_id"))
            .withEmail(rs.getString("seller_email"))
            .withAddress(rs.getString("seller_address"))
            .withAccountNumber(String.valueOf(rs.getString("seller_account_number")))
            .withName(rs.getString("seller_name"))
            .withPhoneNumber(rs.getString("seller_phone_number"))
            .withTaxId(rs.getString("seller_tax_id"))
            .build();
    }

    private InvoiceEntry getInvoiceEntry(ResultSet rs) throws SQLException {
        return InvoiceEntry.builder()
            .withId(rs.getLong("id"))
            .withDescription(rs.getString("description"))
            .withQuantity(rs.getLong("quantity"))
            .withPrice(rs.getBigDecimal("price"))
            .withNetValue(rs.getBigDecimal("net_value"))
            .withGrossValue(rs.getBigDecimal("gross_value"))
            .withVatRate(Vat.getVatType(rs.getFloat("vat_rate")))
            .build();
    }
}
