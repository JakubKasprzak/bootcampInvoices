package pl.coderstrust.model;

import java.math.BigDecimal;
import java.util.Objects;

public final class InvoiceEntry {

    private final Long id;
    private final String description;
    private final Long quantity;
    private final BigDecimal price;
    private final BigDecimal netValue;
    private final BigDecimal grossValue;
    private final Vat vatRate;

    public InvoiceEntry(Long id, String description, Long quantity, BigDecimal price, BigDecimal netValue, BigDecimal grossValue, Vat vatRate) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.netValue = netValue;
        this.grossValue = grossValue;
        this.vatRate = vatRate;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Long getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getNetValue() {
        return netValue;
    }

    public BigDecimal getGrossValue() {
        return grossValue;
    }

    public Vat getVatRate() {
        return vatRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceEntry that = (InvoiceEntry) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(description, that.description) &&
                Objects.equals(quantity, that.quantity) &&
                Objects.equals(price, that.price) &&
                Objects.equals(netValue, that.netValue) &&
                Objects.equals(grossValue, that.grossValue) &&
                Objects.equals(vatRate, that.vatRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, quantity, price, netValue, grossValue, vatRate);
    }

    @Override
    public String toString() {
        return "InvoiceEntry{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", netValue=" + netValue +
                ", grossValue=" + grossValue +
                ", vatRate=" + vatRate +
                '}';
    }
}