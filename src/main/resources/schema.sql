CREATE TABLE IF NOT EXISTS User (
    user_id int auto_increment primary key,
    user_name varchar(127) not null,
    user_gender char(1) not null,
    user_email varchar(127) not null,
    phone_number varchar(20) not null,
    user_address varchar(255),
    user_birth char(8) not null,
    user_registered_date timestamp not null,
    user_google_id bigint,
    user_kakao_id bigint,
    user_naver_id bigint,
    simple_password varchar(8) not null
);

CREATE TABLE IF NOT EXISTS History (
    history_id bigint auto_increment primary key,
    user_id int not null,
    history_name varchar(100) not null,
    history_elements JSON,
    activity_date timestamp not null,
    foreign key (user_id) references user(user_id)
);

CREATE TABLE IF NOT EXISTS Shortcut (
    shortcut_id bigint auto_increment primary key,
    user_id int not null,
    shortcut_name varchar(100) not null,
    shorcut_elements JSON,
    is_favorite boolean not null default false,
    foreign key (user_id) references user(user_id)
);

CREATE TABLE IF NOT EXISTS Agent (
    agent_id int auto_increment primary key,
    agent_name varchar(127) not null,
    agent_email varchar(127) not null,
    agent_pw varchar(255)
);

CREATE TABLE IF NOT EXISTS Consultation (
    consultation_id bigint auto_increment primary key,
    user_id int not null,
    agent_id int not null,
    consultation_title varchar(100) not null,
    consultation_content varchar(1000),
    consultation_date timestamp not null,
    foreign key (user_id) references user(user_id),
    foreign key (agent_id) references agent(agent_id)
);

CREATE TABLE IF NOT EXISTS Account (
    account_id bigint auto_increment primary key,
    user_id int not null,
    account_name varchar(127) not null, 
    bank varchar(31) not null,
    account_number varchar(31) not null,
    account_type varchar(31) not null,
    balance bigint not null default 0,
    foreign key (user_id) references user(user_id)
);

CREATE TABLE IF NOT EXISTS Transaction (
    transaction_id bigint auto_increment primary key,
    sender_account_id bigint not null,
    receiver_account_id bigint not null,
    amount bigint not null,
    sender_name varchar(31),
    receiver_name varchar(31),
    transaction_date timestamp not null,
    foreign key (sender_account_id) references account(id),
    foreign key (receiver_account_id) references account(id)
);
