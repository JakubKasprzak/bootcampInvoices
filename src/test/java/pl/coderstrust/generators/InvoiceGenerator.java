package pl.coderstrust.generators;

import java.util.ArrayList;
import java.util.List;
import pl.coderstrust.model.Invoice;
import pl.coderstrust.model.InvoiceEntry;

public class InvoiceGenerator {

    private static List<InvoiceEntry> generateEntries() {
        List<InvoiceEntry> entries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            entries.add(InvoiceEntryGenerator.getRandomEntry());
        }
        return entries;
    }

    private static Invoice buildInvoice(Long id) {
        return Invoice.builder()
            .withId(id)
            .withNumber(RandomWordGenerator.generateRandomWord())
            .withBuyer(CompanyGenerator.generateRandomCompany())
            .withSeller(CompanyGenerator.generateRandomCompany())
            .withDueDate(RandomLocalDateGenerator.generateRandomLocalDate())
            .withIssuedDate(RandomLocalDateGenerator.generateRandomLocalDate())
            .withEntries(generateEntries())
            .build();
    }

    public static Invoice generateRandomInvoice(Long id) {
        return buildInvoice(id);
    }
}
