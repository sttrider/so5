-- liquibase formatted SQL

-- changeset joao.moreira:remove_image_not_null dbms:mysql
ALTER TABLE product MODIFY image VARCHAR(200);
-- ROLLBACK ALTER TABLE product MODIFY image VARCHAR(200) NOT NULL;