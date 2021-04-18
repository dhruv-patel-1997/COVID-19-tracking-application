use dhruvp;

create table TEST_RESULTS(
	test_list varchar(1000),
    test_date int,
    test_result boolean
);

create table CONTACT_LIST(
	person1_key varchar(1000),
    person2_key varchar(1000),
    contact_date int,
    contact_duration int,
    person1_contact_reported boolean default 0,
    person2_contact_reported boolean default 0
);

create table POSITIVE_COVID_LIST(
	person_key varchar(1000),
    test_key varchar(1000),
    test_date int
);

alter table CONTACT_LIST add primary key (person1_key,person2_key,contact_date); 
alter table TEST_RESULTS add primary key (test_list);
alter table POSITIVE_COVID_LIST add primary key (person_key,test_key);
