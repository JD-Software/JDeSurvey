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
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (1,'I', 'ROLE_ADMIN','Este es el nivel más alto de acceso sin restricciones', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (2,'I', 'ROLE_SURVEY_ADMIN','Esta función permite al usuario administrar Encuestas', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (3,'E', 'ROLE_SURVEY_PARTICIPANT','Este rol permite a un usuario para llenar una Encuesta', 0);


/*Security Groups*/
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (1,'I','Adminitrators','Usuarios con acceso Admininstator',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (2,'I','Survey Adminitrators','Usuarios que pueden manipular encuestas',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (3,'E','Survey Participants','Usuarios que pueden llenar encuestas' ,0);

/*Security Group Authorities*/
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (1,1);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (2,2);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (3,3);


/*Users*/
INSERT INTO sec_user (id,user_type,enabled, login,date_of_birth,		first_name,	middle_name,	last_name,	email,					creation_date,				last_update_date,		version,	password)
VALUES 				 (1, 'I',1,       'admin','1975-01-01',		'admin',	'admin',		'admin',	'me@example.com',	'2012-10-21 14:44:53',		'2012-10-21 14:44:53',	0,			'a601995c56b8c7148e36cf2feb682d308404e262a2dc6eab1a14e158ef6eed49');	



INSERT INTO sec_user_group (user_id,group_id) VALUES(1,1);

	
delete from velocity_template;
INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (1,'ForgotLogin','<html><body><h1>Bienvenido a Open eSurvey ....</h1><p>Este es su Login: ${userLogin}</p></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (2,'ForgotPassword','<p>Haga un click en este Link para resetear su password <a href="../../public/rpass?key=${key}">Reiniciar password</a></p>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (3,'InternalHelpContent','<h2><span id="result_box" lang="es"><span class="hps">Instrucciones Generales</span></span></h2><p><span id="result_box" lang="es"><span class="hps">El sistema Open eSurvey, </span><span class="hps">se utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p><h2><span id="result_box" lang="es"><span class="hps">&iquest;C&oacute;mo</span> <span class="hps alt-edited">Hacer</span></span>?:</h2><p><span id="result_box" lang="es"><span class="hps">Esta aplicaci&oacute;n</span> <span class="hps">se utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p><h2><span id="result_box" lang="es"><span class="hps alt-edited">Preguntas M&aacute;s Frecuentes</span></span>:</h2><p><span id="result_box" lang="es"><span class="hps">Aqu&iacute; est&aacute; una lista</span> <span class="hps">de</span> <span class="hps">algunas preguntas y respuestas</span></span> ....</p><h2><span id="result_box" lang="es"><span class="hps">Soporte T&eacute;cnico</span></span>:</h2><p><span id="result_box" lang="es"><span class="hps">Si</span> <span class="hps">necesita ayuda para usar</span> <span class="hps">este</span> <span class="hps">sistema, por favor</span> <span class="hps">p&oacute;ngase en contacto con</span><br /><br /><span id="result_box" lang="es"><span class="hps">El sistema Open eSurvey</span></span> se <span class="hps">utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (4,'ExternalHelpContent','<h2><span id="result_box" lang="es"><span class="hps">Instrucciones Generales</span></span></h2><p><span id="result_box" lang="es"><span class="hps">El sistema Open eSurvey, </span><span class="hps">se utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p><h2><span id="result_box" lang="es"><span class="hps">&iquest;C&oacute;mo</span> <span class="hps alt-edited">Hacer</span></span>?:</h2><p><span id="result_box" lang="es"><span class="hps">Esta aplicaci&oacute;n</span> <span class="hps">se utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p><h2><span id="result_box" lang="es"><span class="hps alt-edited">Preguntas M&aacute;s Frecuentes</span></span>:</h2><p><span id="result_box" lang="es"><span class="hps">Aqu&iacute; est&aacute; una lista</span> <span class="hps">de</span> <span class="hps">algunas preguntas y respuestas</span></span> ....</p><h2><span id="result_box" lang="es"><span class="hps">Soporte T&eacute;cnico</span></span>:</h2><p><span id="result_box" lang="es"><span class="hps">Si</span> <span class="hps">necesita ayuda para usar</span> <span class="hps">este</span> <span class="hps">sistema, por favor</span> <span class="hps">p&oacute;ngase en contacto con</span><br /><br /><span id="result_box" lang="es"><span class="hps">El sistema Open eSurvey</span></span> se <span class="hps">utiliza para gestionar</span> <span class="hps">licencias que</span> <span class="hps">incluye un sistema de</span> <span class="hps">gesti&oacute;n de documentos,</span> <span class="hps">sistema de notificaci&oacute;n</span> <span class="hps">Intergarted</span> <span class="hps">.....</span></span></p>',0,'2012-10-21 14:44:53');



INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (5,'InternalHomePageContent','<p><span id="result_box" lang="es"><span class="hps">Bienvenido</span> </span>${user.firstName} ${user.lastName} al Sistema Open eSurvey. <span id="result_box" lang="es"><span class="hps">El sistema proporciona un</span> <span class="hps">portal</span> <span class="hps">para la recogida de</span> <span class="hps">datos de la encuesta</span><span>.</span> <span class="hps">Haga clic</span> <span class="hps atn">en la pesta&ntilde;a ''Encuestas''</span> <span class="hps">en</span> <span class="hps">el men&uacute; superior para</span> <span class="hps">empezar.</span></span></p>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (6,'ExternalHomePageContent','<p><span id="result_box" lang="es"><span class="hps">Bienvenido</span> </span>${user.firstName} ${user.lastName} al Sistema Open eSurvey. <span id="result_box" lang="es"><span class="hps">El sistema proporciona un</span> <span class="hps">portal</span> <span class="hps">para la recogida de</span> <span class="hps">datos de la encuesta</span><span>.</span> <span class="hps">Haga clic</span> <span class="hps atn">en la pesta&ntilde;a ''Encuestas''</span> <span class="hps">en</span> <span class="hps">el men&uacute; superior para</span> <span class="hps">empezar.</span></span></p>',0,'2012-10-21 14:44:53');






