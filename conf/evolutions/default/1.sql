
# Notes: 
# - `Ups` and `Downs` comments are important to Play.

# --- !Ups

#
# USERS
# -----
#
create table users (
  id int unsigned not null auto_increment,
  username varchar(100) not null,
  password varchar(100) not null,
  primary key (id),
  constraint unique index idx_username_unique (username asc)
) engine = InnoDB;

insert into users(username, password) values ('alvin', 'alvin');
insert into users(username, password) values ('kim', 'kim');

#
# PROJECTS
# --------
# status is 'a' (active) or 'r' (retired)
#
create table projects (
  id int unsigned auto_increment not null,
  user_id int unsigned not null,
  name varchar(255) not null,
  status varchar(1) not null default 'a',
  display_order int not null default 1,
  date_created timestamp not null default now(),
  primary key (id),
  foreign key (user_id) references users(id) on delete cascade,
  constraint unique index idx_projects_unique (user_id, name)
) engine = InnoDB;

insert into projects (user_id, name, display_order) values ((select id from users where username='alvin'), 'Focus', 1);
insert into projects (user_id, name, display_order) values ((select id from users where username='alvin'), 'Finance', 2);
insert into projects (user_id, name, display_order) values ((select id from users where username='alvin'), 'aa.com', 3);
insert into projects (user_id, name, display_order) values ((select id from users where username='alvin'), 'Personal', 4);

insert into projects (user_id, name, display_order) values ((select id from users where username='kim'), 'Personal', 1);
insert into projects (user_id, name, display_order) values ((select id from users where username='kim'), 'Work', 2);

#
# TASKS
# -----
# status: ('c' = created, 'f' = finished)
# parent_id lets me make one task a child of another task
#
create table tasks (
  id int unsigned auto_increment not null,
  user_id int unsigned not null,
  project_id int unsigned not null,
  parent_id int unsigned not null default 0,
  description varchar(255) not null,
  status varchar(1) not null default 'c',
  date_created timestamp not null default now(),
  primary key (id),
  foreign key (user_id) references users(id) on delete cascade,
  foreign key (project_id) references projects(id) on delete cascade
) engine = InnoDB;

insert into tasks (user_id, project_id, description)
    values ((select id from users where username='alvin'), 
            (select id from projects where name='Focus'), 'Get tabs working');
insert into tasks (user_id, project_id, description)
    values ((select id from users where username='alvin'), 
            (select id from projects where name='Focus'), 'Get display-tasks working');
insert into tasks (user_id, project_id, description)
    values ((select id from users where username='alvin'), 
            (select id from projects where name='Focus'), 'Get logout working');
insert into tasks (user_id, project_id, description)
    values ((select id from users where username='alvin'), 
            (select id from projects where name='Focus'), 'Get task to go away when checkbox is clicked');
insert into tasks (user_id, project_id, description)
    values ((select id from users where username='alvin'), 
            (select id from projects where name='aa.com'), 'Update jwarehouse');

insert into tasks (user_id, project_id, description)
    values ((select id from users where username='kim'), 
            (select id from projects where name='Work'), 'Send out invoices');
insert into tasks (user_id, project_id, description)
    values ((select id from users where username='kim'), 
            (select id from projects where name='Work'), 'Write paychecks');



#
# Focus: get tabs wkg; get 'display tasks' wkg; get menu showing; handle menu actions in ctrl; get logout wkg;
#        get task to go away when checkbox clicked; show 'now working window' when hyperlink clicked; 
#        get 'new project' working; how do i stay logged in?
# Finance: 
# aa.com: update jwarehouse, 
# marketing: update play recipes; fix/release WikiReader
#

# --- !Downs

SET FOREIGN_KEY_CHECKS = 0;
drop table if exists users;
drop table if exists projects;
drop table if exists tasks;
SET FOREIGN_KEY_CHECKS = 1;










