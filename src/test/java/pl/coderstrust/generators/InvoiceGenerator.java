package pl.coderstrust.generators;

import java.util.ArrayList;
import java.util.List;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceGenerator {

    private static List<InvoiceEntry> generateEntries(int count) {
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }
        return entries;
    }

    private static Invoice generateInvoice(Long id) {
        return Invoice.builder()
            .withId(id)
            .withNumber(WordGenerator.generateRandomWord())
            .withBuyer(CompanyGenerator.generateRandomCompany())
            .withSeller(CompanyGenerator.generateRandomCompany())
            .withDueDate(LocalDateGenerator.generateRandomLocalDate())
            .withIssuedDate(LocalDateGenerator.generateRandomLocalDate())
            .withEntries(generateEntries(5))
            .build();
    }

    public static Invoice getRandomInvoiceWithSpecificId(Long id) {
        return generateInvoice(id);
    }

    public static Invoice generateRandomInvoice() {
        return generateInvoice(IdGenerator.getRandomId());
    }

    public static Invoice generateRandomInvoiceWithNullId() {
        return generateInvoice(null);
    }
}