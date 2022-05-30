-- liquibase formatted SQL

-- changeset joao.moreira:add_categories dbms:mysql
insert into product_category(name)
values ('Shoes'),
       ('Handbags'),
       ('Furniture'),
       ('Clothes')
-- ROLLBACK delete from product_category;

-- changeset joao.moreira:add_customers dbms:mysql
insert into address(street, number, district, city, state, country, zip_code, creation_date)
values ('Street 1', '1', 'District 1', 'City 1', 'State 1', 'Country 1', '11111111', now());

insert into customer(email, shipping_address_id, billing_address_id)
values ('user1@so5.com', 1, 1),
       ('user2@so5.com', null, null);

insert into credit_card_data(customer_id, holder, number, verification_code, expiration_date, creation_date)
values (1, 'USER 1', '1111111111111111', '222', now(), now())
-- ROLLBACK delete from credit_card_data;
-- ROLLBACK delete from customer;
-- ROLLBACK delete from address;