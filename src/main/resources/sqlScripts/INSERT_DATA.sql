--------COMPANY INSERT
INSERT INTO company(account_number, address, email, name, phone_number, tax_id) VALUES('111','address1','email1','name1','111','tax_id_1') RETURNING id;
INSERT INTO company(account_number, address, email, name, phone_number, tax_id) VALUES('222','address2','email2','name2','222','tax_id_2');
INSERT INTO company(account_number, address, email, name, phone_number, tax_id) VALUES('333','address3','email3','name3','333','tax_id_3');
INSERT INTO company(account_number, address, email, name, phone_number, tax_id) VALUES('444','address4','email4','name4','4444','tax_id_4');
INSERT INTO company(account_number, address, email, name, phone_number, tax_id) VALUES('555','address5','email5','name5','5555','tax_id_5');

SELECT  *FROM  company;
----- INVOICE INSERT------------------
INSERT INTO invoice(due_date, issued_date, number, buyer_id, seller_id) VALUES(current_date,current_date,'number1',2,3);
INSERT INTO invoice(due_date, issued_date, number, buyer_id, seller_id) VALUES(current_date,current_date,'number2',2,3);
INSERT INTO invoice(due_date, issued_date, number, buyer_id, seller_id) VALUES(current_date,current_date,'number3',3,1);
INSERT INTO invoice(due_date, issued_date, number, buyer_id, seller_id) VALUES(current_date,current_date,'number4',3,2);
INSERT INTO invoice(due_date, issued_date, number, buyer_id, seller_id) VALUES(current_date,current_date,'number5',1,3);
--- invoice_entry-------
INSERT INTO invoice_entry(description, gross_value, net_value, price, quantity, vat_rate) VALUES('description1','1000','7500','1750','100','0.23');
INSERT INTO invoice_entry(description, gross_value, net_value, price, quantity, vat_rate) VALUES('description2','1000','7500','1750','100','0.23');
INSERT INTO invoice_entry(description, gross_value, net_value, price, quantity, vat_rate) VALUES('description3','1000','7500','1750','100','0.23');
INSERT INTO invoice_entry(description, gross_value, net_value, price, quantity, vat_rate) VALUES('description4','1000','7500','1750','100','0.23');
INSERT INTO invoice_entry(description, gross_value, net_value, price, quantity, vat_rate) VALUES('description5','1000','7500','1750','100','0.23');
----invoice_entries-----------------
INSERT INTO invoice_entries(invoice_id, entries_id) VALUES(1,2);
INSERT INTO invoice_entries(invoice_id, entries_id) VALUES(2,3);
INSERT INTO invoice_entries(invoice_id, entries_id) VALUES(3,1);
INSERT INTO invoice_entries(invoice_id, entries_id) VALUES(1,3);
INSERT INTO invoice_entries(invoice_id, entries_id) VALUES(3,2);

SELECT *FROM company;