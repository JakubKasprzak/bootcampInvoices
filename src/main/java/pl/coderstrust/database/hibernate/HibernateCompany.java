package pl.coderstrust.database.hibernate;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "company")
public class HibernateCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Long id;

    private final String name;

    private final String address;

    private final String taxId;

    private final String accountNumber;

    private final String phoneNumber;

    private final String email;

    private HibernateCompany() {
        id = null;
        name = null;
        address = null;
        taxId = null;
        accountNumber = null;
        phoneNumber = null;
        email = null;
    }

    private HibernateCompany(HibernateCompany.Builder builder) {
        id = builder.id;
        name = builder.name;
        address = builder.address;
        taxId = builder.taxId;
        accountNumber = builder.accountNumber;
        phoneNumber = builder.phoneNumber;
        email = builder.email;
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
        HibernateCompany company = (HibernateCompany) o;
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
        return "HibernateCompany{"
            + "id=" + id
            + ", name='" + name + '\''
            + ", address='" + address + '\''
            + ", taxId='" + taxId + '\''
            + ", accountNumber='" + accountNumber + '\''
            + ", phoneNumber='" + phoneNumber + '\''
            + ", email='" + email + '\''
            + '}';
    }

    public static HibernateCompany.Builder builder() {
        return new HibernateCompany.Builder();
    }

    public static class Builder {

        private Long id;
        private String name;
        private String address;
        private String taxId;
        private String accountNumber;
        private String phoneNumber;
        private String email;

        public HibernateCompany.Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public HibernateCompany.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public HibernateCompany.Builder withAddress(String address) {
            this.address = address;
            return this;
        }

        public HibernateCompany.Builder withTaxId(String taxId) {
            this.taxId = taxId;
            return this;
        }

        public HibernateCompany.Builder withAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public HibernateCompany.Builder withPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public HibernateCompany.Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public HibernateCompany build() {
            return new HibernateCompany(this);
        }
    }
}
