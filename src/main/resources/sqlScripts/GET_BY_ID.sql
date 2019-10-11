SELECT O.*,
A.account_number as buyer_account_number, A.address as buyer_address,A.email as buyer_email,A.name as buyer_name,A.phone_number as buyer_phone_number,A.tax_id as buyer_tax_id,
B.account_number as seller_account_number,B.address as seller_address,B.email as seller_email,B.name as seller_name,B.phone_number as seller_phone_number,B.tax_id as seller_tax_id,
D.id as entry_id,D.description,D.gross_value,D.net_value,D.price,D.quantity,D.vat_rate
FROM INVOICE O
JOIN COMPANY A ON (O.buyer_id=A.id)
JOIN COMPANY B ON(O.seller_id=B.id)
JOIN INVOICE_ENTRIES C ON (O.id=C.invoice_id)
JOIN INVOICE_ENTRY D ON (D.id=C.entries_id)
WHERE O.id = ? or  null
