create database school_system_project;

drop user 'root'@'localhost';
flush privileges;
create user 'root'@'localhost' identified by 'r00t';

grant all on school_system_project.* to 'root'@'localhost';

select * from users;
select * from admins;
select * from teachers;
select * from parents;
select * from students;
select * from subjects;
select * from lectures;
select * from sessions;

select u.id, u.username, t.first_name, t.last_name
from users u, teachers t
where u.id = t.id;

delete from parents where id = 12;
delete from users where id = 1;

UPDATE users set password = '$2a$10$f.G5JtGpu16X9p/bCPU9Ju8sKWFbJRcyCVnqJF1shFyAMpQI/GKE6' where id=12;

select * from grade_cards;
select * from grades;

select * from student_parent;

select * from classes;
delete from users where id = 3;
delete from admins where id = 3;
delete from classes where 1=1;

INSERT INTO users (`user_type`, `id`, `password`, `role`, `username`, `version`) 
VALUES ('AdminEntity', '0', '$2a$10$uQq98jgUdYQXZzLE1AVTLuBQTbIcOLyI8hN.MM87V/PNN9w60ygwK', 'ADMIN', 'admin', '0');
INSERT INTO admins (`id`) VALUES ('0');