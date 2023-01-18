CREATE TABLE company
(
    id integer NOT NULL,
    name character varying,
    CONSTRAINT company_pkey PRIMARY KEY (id)
);

CREATE TABLE person
(
    id integer NOT NULL,
    name character varying,
    company_id integer references company(id),
    CONSTRAINT person_pkey PRIMARY KEY (id)
);

insert into company(id,name)
values (1, 'mvideo'),
	(2, 'gazprom'),
	(3, 'sberbank'),
	(4, 'vtb'),
	(5, 'rosseti'),
	(6, 'rzd'),
	(7, 'dns'),
	(8, '1s'),
	(9, 'wildberries'),
	(10, 'lamoda');

insert into person(id,name, company_id)
values (1, 'Ivan', 10),
	(2, 'Sergei', 6),
	(3, 'Jack', 5),
	(4, 'Petr', 4),
	(5, 'Alex', 2),
	(6, 'Alex', 3),
	(7, 'Dmitriy', 9),
	(8, 'Anna', 8),
	(9, 'Ekaterina', 4),
	(10, 'John', 2),
	(11, 'Olesya', 1),
	(12, 'Aliya', 3),
	(13, 'Juilio', 7),
	(14, 'Sergio', 6),
	(15, 'Lionel', 4),
	(16, 'Valdislav', 5),
	(17, 'Evgeniy', 5),
	(18, 'Sergio', 9),
	(19, 'William', 10);

select p.name, c.name
from person p
	join company c on p.company_id = c.id
where p.company_id <> 5;

select c.name, count(p.company_id)
from person p
join company c on p.company_id = c.id
group by c.name
having count(company_id) = (
	select max(maximum)
	from (select company_id, count(company_id) as maximum
	from person
	group by company_id) as q1);
