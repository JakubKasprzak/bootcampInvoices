package pl.coderstrust.model;

import java.util.Objects;

public final class Company {

    private final Long id;
    private final String name;
    private final String address;
    private final String taxId;
    private final String accountNumber;
    private final String phoneNumber;
    private final String email;

    private Company(Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
    }

    public static Company.Builder builder() {
        return new Company.Builder();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTaxId() {
        return taxId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Company company = (Company) o;
        return Objects.equals(id, company.id)
            && Objects.equals(name, company.name)
            && Objects.equals(address, company.address)
            && Objects.equals(taxId, company.taxId)
            && Objects.equals(accountNumber, company.accountNumber)
            && Objects.equals(phoneNumber, company.phoneNumber)
            && Objects.equals(email, company.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "Company{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", address='" + address + '\''
            + ", taxId='" + taxId + '\''
            + ", accountNumber='" + accountNumber + '\''
            + ", phoneNumber='" + phoneNumber + '\''
            + ", email='" + email + '\''
            + '}';
    }

    public static class Builder {

        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }

    public static class Builder {

        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Company build() {
            return new Company(this);
        }
    }
}
