SET SQL_SAFE_UPDATES=0;

/*Question types
Not needed any more
delete from question_type;

INSERT INTO question_type (id, type_name, type_text, version)  VALUES (1,'BC','Yes No Checkbox',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (2,'BR','Yes No DropDown',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (3,'ST','Short Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (4,'LT','Long Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (5,'HT','Huge Text Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (6,'IN','Integer Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (7,'CR','Currency Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (8,'NM','Decimal Input',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (9,'DT','Date Input',0); 
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (10,'SD','Single choice Drop Down',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (11,'MC','Multiple Choice Checkboxes',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (12,'DD','DataSet Drop Down',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (13,'SR','Single Choice Radio Buttons',0);


INSERT INTO question_type (id, type_name, type_text, version)  VALUES (14,'BCM','Yes No Checkbox Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (15,'BRM','Yes No DropDown Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (16,'STM','Short Text Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (17,'INM','Integer Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (18,'CRM','Currency Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (19,'NMM','Decimal Input Matrix',0);
INSERT INTO question_type (id, type_name, type_text, version)  VALUES (20,'DTM','Date Input Matrix',0); 
*/






delete from sec_user_group;
delete from sec_group_authority;
delete from sec_group;
delete from sec_authority;
delete from sec_user;
delete from regular_expression;




/*Regular Expressions */
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (1,'First Name Validation', 'First Name','^[0-9a-zA-Z\\.\\-\\, ]{0,75}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (2,'Last Name Validation', 'Last Name','^[0-9a-zA-Z\\.\\-\\, ]{0,75}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (3,'Email Validation', 'Email','^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{0,9})$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (4,'Address Validation', 'Address','^[0-9a-zA-Z\\.\\-,# ]{0,100}$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (5,'Zip Code Validation', 'Zip Code','^\\d{5}$|^$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (6,'US Phone Number Validation', 'US Phone Number','^\\d{3}-\\d{3}-\\d{4}$|^$', 0);
INSERT INTO regular_expression (id,description, name,regex ,version) VALUES (7,'Social Security Number Validation', 'Social Security Number','^(\\d{3}-\\d{2}-\\d{4}){1,12}$|^$', 0);

/*Security Authorities*/
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (1,'I', 'ROLE_ADMIN', 'Highest level access withoyut anuy restrictions', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (2,'I', 'ROLE_SURVEY_ADMIN','This role allows a user to manage surveys.', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (3,'E', 'ROLE_SURVEY_PARTICIPANT','This role allows a user to fill a survey ', 0);


/*Security Groups*/
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (1,'I','Adminitrators','Users with admininstator access',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (2,'I','Survey Adminitrators','Users who may manage Surveys',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (3,'E','Survey Participants','Users who can fill surveys' ,0);

/*Security Group Authorities*/
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (1,1);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (2,2);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (3,3);


/*Users*/
INSERT INTO sec_user (id,user_type,enabled, login,date_of_birth,		first_name,	middle_name,	last_name,	email,					creation_date,				last_update_date,		version,	password)
VALUES 				 (1, 'I',1,       'admin','1975-01-01',		'admin',	'admin',		'admin',	'loubala@jd-soft.com',	'2012-10-21 14:44:53',		'2012-10-21 14:44:53',	0,			'a601995c56b8c7148e36cf2feb682d308404e262a2dc6eab1a14e158ef6eed49');	


INSERT INTO sec_user_group (user_id,group_id) VALUES(1,1);


delete from velocity_template;
INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (1,'Nom d''utilisateur oublié ?' ,'<html><body><h1>Bienvenue sur JD Open-e-Survey ....</h1><p>Voici votre pseudo: ${userLogin}</p></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (2,'ForgotPassword','<html><body>Click on this Link to reset your password <a href="http://localhost:8080/internal/public/rpass?key=${key}"> Reset password</a></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (3,'Aide Internal','<h2>Instructions Générales</h2><p>Open-E-Survey est un logiciel de collection de données, le system peut etre utuliser comme questionaire <p><h2>How Tos\:</h2><p></p><h2>FAQs\:</h2><p><p>Voici une liste de questions et réponses</p><h2>Soutien Technique\:</h2><p>Si vous avez besoin d''aide pour utiliser ce système s''il vous plaît contactez</p>',0,'2012-10-21 14:44:53');



INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (6,'ExternalHomePageContent','Welcome ${user.firstName} ${user.lastName} to the JD Survey System. Le système fournit un portail pour collection de données. S''il vous plaît cliquer sur le ''Questionnaires'' onglet au haut du menu pour commencer.',0,'2012-10-21 14:44:53');






