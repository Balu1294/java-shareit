delete from requests cascade;
delete from comments cascade;
delete from bookings cascade;
delete from items cascade;
delete from users cascade;

insert into users (id, name, email)
values (1, 'Jon Bon', 'mail@mail.ru'),
       (2, 'Bon Jon', 'google@mail.com'),
       (3, 'Don Jon', 'boogle@mail.com');

insert into items (id, name, owner_id, description, available, request_id)
values (1, 'Отвертка', 1, 'Простая отвертка', 'true', 1),
       (2, 'Топор', 1, 'Простой топор', 'true', 1),
       (3, 'Пила', 1, 'Бензопила', 'false', 2);

insert into bookings (id, booker_id, item_id, start_date, end_date, status)
values (1, 2, 1, '2023-07-08 20:20:20', '2023-07-08 21:20:20', 'APPROVED'),
       (2, 2, 1, '2030-07-13 20:20:20', '2030-07-13 21:20:20', 'APPROVED'),
       (3, 2, 1, '2023-07-11 20:20:20', '2029-07-14 21:20:20', 'APPROVED'),
       (4, 2, 1, '2030-07-14 22:20:20', '2030-07-15 21:20:20', 'CANCELED');

insert into comments (id, author_id, item_id, rating, text, time_of_creation)
values (2, 2, 1, 1, 'comment text', '2023-07-11 12:20:20');

insert into requests(id, description, requestor_id, time_of_creation, response_items)
values (1, 'Отвертка', 2, '2023-07-10 20:20:20', null),
       (2, 'Пила', 2, '2023-07-11 10:20:20', null),
       (3, 'Стремянка', 2, '2023-07-12 10:20:20', null);