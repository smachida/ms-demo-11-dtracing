set charset utf8;

drop database if exists `review-db`;
create database `review-db` default character set utf8;
use `review-db`;

set character_set_client = utf8;
set character_set_connection = utf8;

create table if not exists reviews (
  id bigint AUTO_INCREMENT NOT NULL PRIMARY KEY,
  version bigint not null,
  product_id bigint not null,
  review_id bigint not null,
  author varchar(100) not null,
  subject varchar(100) not null,
  content varchar(1000)
);

use `review-db`;

set foreign_key_checks = 0;
TRUNCATE table reviews;
set foreign_key_checks = 1;

insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1001, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1001, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1002, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1002, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1003, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1003, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1004, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1004, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1005, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1005, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1006, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1006, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1007, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1007, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1008, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1008, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1009, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1009, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1010, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1010, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1011, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1011, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1012, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1012, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1013, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1013, 2, '山田花子', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1014, 1, '山田太郎', 'レビュー', 'レビュー');
insert into reviews (version, product_id, review_id, author, subject, content) values (0, 1014, 2, '山田花子', 'レビュー', 'レビュー');