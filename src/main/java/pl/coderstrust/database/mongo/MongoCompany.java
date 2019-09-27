package pl.coderstrust.database.mongo;

import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class MongoCompany {

    @Id
    private final String mongoId;

    private final String name;

    private final String address;

    private final String taxId;

    private final String accountNumber;

    private final String phoneNumber;

    private final String email;

    @PersistenceConstructor
    public MongoCompany(String mongoId, String name, String address, String taxId, String accountNumber, String phoneNumber, String email) {
        this.mongoId = mongoId;
        this.name = name;
        this.address = address;
        this.taxId = taxId;
        this.accountNumber = accountNumber;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    private MongoCompany() {
        mongoId = null;
        name = null;
        address = null;
        taxId = null;
        accountNumber = null;
        phoneNumber = null;
        email = null;
    }

    private MongoCompany(MongoCompany.Builder builder) {
        mongoId = builder.mongoId;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
    }

    public String getMongoId() {
        return mongoId;
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
        MongoCompany company = (MongoCompany) o;
        return Objects.equals(mongoId, company.mongoId)
            && Objects.equals(name, company.name)
            && Objects.equals(address, company.address)
            && Objects.equals(taxId, company.taxId)
            && Objects.equals(accountNumber, company.accountNumber)
            && Objects.equals(phoneNumber, company.phoneNumber)
            && Objects.equals(email, company.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mongoId, name, address, taxId, accountNumber, phoneNumber, email);
    }

    @Override
    public String toString() {
        return "MongoCompany{"
            + "mongoId=" + mongoId
            + ", name='" + name + '\''
            + ", address='" + address + '\''
            + ", taxId='" + taxId + '\''
            + ", accountNumber='" + accountNumber + '\''
            + ", phoneNumber='" + phoneNumber + '\''
            + ", email='" + email + '\''
            + '}';
    }

    public static MongoCompany.Builder builder() {
        return new MongoCompany.Builder();
    }

    public static class Builder {

        private String mongoId;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public MongoCompany.Builder withMongoId(String mongoId) {
            this.mongoId = mongoId;
            return this;
        }

        public MongoCompany.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public MongoCompany.Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public MongoCompany.Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public MongoCompany.Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public MongoCompany.Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public MongoCompany.Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public MongoCompany build() {
            return new MongoCompany(this);
        }
    }
}

