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
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (1,'I', 'ROLE_ADMIN', 'الوصول الكامل', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (2,'I', 'ROLE_SURVEY_ADMIN','يسمح هذا الدور لمستخدم بإدارة الدراسات الاستقصائية', 0);
INSERT INTO sec_authority (id,authority_type, name, description,version) VALUES (3,'E', 'ROLE_SURVEY_PARTICIPANT','يسمح هذا الدور لمستخدم لملء استطلاع ', 0);


/*Security Groups*/
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (1,'I','Adminitrators','المستخدمين الذين لديهم وصول أدمينينستاتور',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (2,'I','Survey Adminitrators','المستخدمين الذين قد يتمكنون من الدراسات الاستقصائية',0);
INSERT INTO sec_group (id,group_type,name,description,version) VALUES (3,'E','Survey Participants','المستخدمين الذين يمكن ملء الدراسات الاستقصائية' ,0);

/*Security Group Authorities*/
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (1,1);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (2,2);
INSERT INTO sec_group_authority (group_id,authority_id) VALUES (3,3);


/*Users*/
INSERT INTO sec_user (id,user_type,enabled, login,date_of_birth,		first_name,	middle_name,	last_name,	email,					creation_date,				last_update_date,		version,	password)
VALUES 				 (1, 'I',1,       'admin','1975-01-01',		'admin',	'admin',		'admin',	'me@example.com',	'2012-10-21 14:44:53',		'2012-10-21 14:44:53',	0,			'a601995c56b8c7148e36cf2feb682d308404e262a2dc6eab1a14e158ef6eed49');	


INSERT INTO sec_user_group (user_id,group_id) VALUES(1,1);


SET SQL_SAFE_UPDATES=0;	
delete from velocity_template;
INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (1,'نسيت تسجيل الدخول','<html><body>
<p><strong>كل طلب الخاص بك أدناه هو تسجيل الدخول الخاص بك</strong></p>
<p>${user_login} :تسجيل الدخول</p></body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (2,'نسيت كلمة المرور','<html><body>
<p>وقد أحرز طلب تغيير كلمة مرور للحساب الخاص بك. 
إذا كنت قد قدمت هذا الطلب الرجاء استخدام الرابط أدناه لتغيير كلمة المرور الخاصة بك. إذا لم يكن الرجاء تجاهل هذا البريد الإلكتروني</p>
<p>${reset_password_link}</p>
</body></html>',0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (3,'أسئلة وأجوبة',
'	<html><body>
	<h1 id="main_section">: أسئلة وأجوبة</h1>
	<!-- Survey creation FAQs -->
	<h2>عملية إنشاء الاستبيان</h2>
	<ul>
	<li><a href="#survey_sec1">كيف يمكنني إنشاء استبيانات؟</a></li>
	<li><a href="#survey_sec2">ماذا يعني النموذجان اللذان يظهران تحت خانة الموضوع في النافذة الجديدة للاستبيان المنشأ؟</a></li>
	<li><a href="#survey_sec3">كيف يمكنني أن أصوغ  أسئلة تستوجب جوابا قبل الشروع في الاستبيان؟</a></li>
	<li><a href="#survey_sec4">كيف يمكنني إدخال الشعارات في الاستبيانات؟</a></li>
	<li><a href="#survey_sec5">ماهي الخطوات المتبعة لتعبئة الاستبيانات؟</a></li>
	<li><a href="#survey_sec6">كيف يمكنني أن أغير لون الاستبيانات؟</a></li>
	</ul>
	<!-- Logic FAQs -->
	<h2>الخاصيات المنطقية</h2>
	<ul>
	<li><a href="#logic_sec1">ما هي وظيفة تقوم الأسئلة العشوائية وخصائص الخيارات؟</a></li>
	<li><a href="#logic_sec2">كيف يمكنني وضع تقسيم خاص بالأسئلة والأجوبة؟</a></li>
	<li><a href="#logic_sec3">ما هو تناغم مميزات الأجوبة وكيف يمكن استخدامها؟</a></li>
	</ul>
	<!-- Statistics FAQs -->
	<h2>تجميع البيانات</h2>
	<ul>
	<li><a href="#data_sec1">كيف يمكنني تحميل نسخ من الاستبيانات الجاهزة (المكتملة)؟</a></li>
	<li><a href="#data_sec2">كيف يمكنني أن أشاهد بيانات الاستبيان/(و) الإحصائيات؟</a></li>
	<li><a href="#data_sec3">ما هي الخيارات المتاحة لإرسال إحصائيات الاستبيان؟</a></li>
	</ul>
	<!-- Security FAQs -->
	<h2>الحماية</h2>
	<ul>
	<li><a href="#security_sec1">كيف يمكن إنشاء مستخدمين؟</a></li>
	<li><a href="#security_sec2">أين يوجد خيار إنجاز استبيانات خاصة أو عامة؟</a></li>
	<li><a href="#security_sec3">حينما أحاول تسجيل الدخول أتلقى رسالة تخبرني بأن محاولتي الدخول لم تنجح، المرجو المحاولة مرة أخرى. السبب: عجز المستخدم. لماذا يحدث هذا؟</a></li>
	</ul>
	<!-- Other FAQs -->
	<h2>مميزات أخرى</h2>
	<ul>
	<li><a href="#other_sec1">كيف أرسل وأجلب استبيانات؟</a></li>
	<li><a href="#other_sec2">ما هي قواعد بيانات؟</a></li>
	<li><a href="#other_sec3">كيف يمكن إرسال دعوات استبيان لمجموعة من المشاركين؟</a></li>
	<li><a href="#other_sec4">ما معني القوالب؟</a></li>
	</ul>
	<!-- survey_sec1 -->
	<h2 id="survey_sec1">كيف يمكن إنشاء استبيانات؟</h2>
	<p style="color: black;">:لإنشاء استبيانات ينبغي أن يكون هناك مشرف أو مشرف خاص بالاستبيان. ولإنشاء الاستبيان يرجى اتباع الخطوات التالية</p>
	<ol style="padding: 0em 4em;">
	<li>١- اذهب إلى إعدادات، الموجودة في أعلى القائمة وآنقر على استبيانات</li>
	<li>٢- آنقر في اللوحة اليمنى على إضافة استبيان جديد</li>
	<li>٣-حدد قسما واكتب اسما للاستبيان. ثم املأ باقي الحقول الاختيارية الموجودة في الصفحة وانقر على حفظ</li>
	<li>٤- آنقر علىإضافة صفحة جديدة في اللوحة اليمنى، أو على الزر الموجود في الزاوية اليسرى</li>
	<li>٥- اختر ترتبا، وادخل عنوانا لصفحة الاستبيان ضَمِّن أي تعليمات إضافية، وحدد ما إذا كنت ستختار الأسئلة العشوائية أم لا</li>
	<li>٦- آنقر على زر إضافة سؤال الموجود في الزاوية اليسرى</li>
	<li>٧- اختر ترتيبا موضعيا، نوع السؤال، ثم قم بالتحرير في خانة نص السؤال. املأ الخانات إن لزم الأمر ذلك، ثم آنقر حفظ</li>
	<li>:بحسب السؤال المطروح؛ تتطلب بعض الحقول الإضافية تتطلب أن تكون محددة<ol type="a">
	<li>أ- سيظهر رابط خيارات التحديث في لوحة الاختيار الأوحد، وخانة الاختيار المتعدد، وفي زر إرسال الاختيار الأحادي للأسئلة المحررة</li>
	<li>ب- إن خانات التحديث وروابط تحديث الصفوف/الخانات يظهر مع كل الأسئلة النموذجية</li>
	</ol></li>
	<li>٩- اضف أسئلة أخرى للصفحة ولصفحات الاستبيان إذا اقتضى الأمر ذلك</li>
	<li>١٠- بعد تهييء الاستبيان، آنقر على نشر الاستبيان في اللوحة الموجودة على اليمين للنشر أو إرسال الاستبيان لتحميله</li>
	</ol><!-- survey_sec2 -->
	<h2 id="survey_sec2">ماذا يعني النموذجان اللذان يظهران تحت خانة الموضوع في النافذة الجديدة للاستبيان المنشأ؟</h2>
	<p style="color: black;"> إن النموذج الأول، نموذج رسالة الدعوة، يحتوي على رسالة تعرض على بريد المتلقين كرسالة دعوة للمشاركة في الاستبيان . </p>
	<p>أما النموذج الثاني، نموذج الاستبيان الجاهز (المكتمل)، فهو عبارة عن رسالة تعرض على المشاركين بعد أن يكملوا الاستبيان. والنموذجان معا يمكن تعديلهما برسائل خاصة.
	</p>
	<!-- survey_sec3 -->
	<h2 id="survey_sec3">كيف يمكن صياغة أسئلة تستوجب جوابا قبل الشروع في الاستبيان؟</h2>
	<p style="color: black;">عند إنشاء أو تحديث الأسئلة، هناك خانة اختيار تحت اسم “إلزامي”.  يساعد هذا الخيار المشاركين على عدم الانتقال إلى الصفحة الموالية إلا بعد تقديم جواب</p>
	<!-- survey_sec4 -->
	<h2 id="survey_sec4">كيف يمكنني إلحاق الشعارات بالاستبيانات؟</h2>
	<p style="color: black;">:إن إضافة شعارات خاصة أو للشركة للاستبيانات يمكن أن تتم خلال عملية الإنشاء أو بعد عملية النشر. يرجى اتباع المراحل التالية لإضافة الشعار</p>
	<ol style="padding: 0em 4em;">
	<li>من شريط القائمة اذهب إلى إعدادات وانقر على استبيانات</li>
	<li>حدد استبيانا لإضافة الشعار، وانقر على رابط «مشاهدة الاستبيان» الموجود أسفل خانة الإجراءات.</li>
	<li> انقر على تحديث الشعار الموجود في اللوحة اليمنى</li>
	<li>انقر على زر اختر ملفا، حدد صورة (الأنواع المتاحة pnr، gif، bmp، وjpeg) ثم انقر على تحميل</li>
	</ol><!-- survey_sec5 -->
	<h2 id="survey_sec5">ماهي الخطوات المتبعة لتعبئة الاستبيانات؟</h2>
	<p style="color: black;"> ما أن يتم نشر الاستبيان حتى يصبح من الممكن تعبئته من قبل المشاركين . ففي حال كان الاستبيان موجها للعموم فإنه ليس من اللازم على المشاركين التسجيل </p>
	<p style="color: black;">غير أنه في حال كان الاستبيان خاصا، فآنذاك يستوجب الأمر مشرفا يقوم بتحديد الفئة المستهدفة من المشاركين</p>
	<ol style="padding: 0em 4em;">
	<li>تسجيل الدخول إلى الموقع الخارجي</li>
	<li>تحديد الاستبيان الذي ينبغي تعبئته، ثم انقر على رابط ملء الاستبيان</li>
	<li>في حال تم تعبئة الاستبيان للمرة الأولى فإن استبيانا جديدا سينشأ بصورة آلية. وعليه، انقر على رابط إنشاء استبيان جديد الموجود  في الزاوية اليسرى من الصفحة</li>
	<li>بعد تعبئة الصفحة انقر على ”التالي“ لمباشرة الصفحة الموالية</li>
	<li>راجع الاستبيان في الصفحة الأخيرة ثم انقر علي «إنهاء»/إرسال للانتهاء</li>
	</ol><!-- survey_sec6 -->
	<h2 id="survey_sec6">(theme)كيف يمكنني تغيير المظهر أو لون الاستبيانات؟</h2>
	<p style="color: black;"> "يمكن إدخال تعديلات على اللون ومظهر الاستبيانات من خلال تحديد مختلف المظاهر في ”إنشاء استبيان جديد؛ أو “تحديد نوافذ الاستبيان</p>
	<p style="color: black;">إن النقر على رابط المظهر الموجود في يسار خيارات الألوان سيسمح بمعاينة مسبقة للألوان</p>
	<!-- logic_sec1 -->
	<h2 id="logic_sec1">ما هو دور الأسئلة العشوائية ومميزات الخيارات؟</h2>
	<p style="color: black;"> تقوم لخصائص العشوائية بتشكيل قائمة من الأسئلة والأجوبة وفق ترتيب شوائي وهذا ما يسمح للاستبيان بعرض الأسئلة والخيارات وفق ترتيب يختلف بين الفينة والأخرى </p>
	<p style="color: black;">توجد الخصائص العشوائية في إنشاء/تحديث نافذة الاستبيان و في نافذة إنشاء/تحديث الأسئلة</p>
	<!-- logic_sec2 -->
	<h2 id="logic_sec2">كيف يمكنني أن أطبق التفريعات على الأسئلة والأجوبة؟</h2>
	<p style="color: black;">يشكل التفريع طريقة في تجاوز الأسئلة التي لا علاقة لها بالموضوع، والتي ترتكز على إدراج الأجوبة المفتوحة. حينما
	 يتم تعبئة الاستبيانات يمكن أن تكون هناك اقتراحات، حيث تقتضي الأجوبة الاستثنائية مشاركا للانتقال إلى أسئلة خاصة أو إلى أقسام  داخل 
	الاستبيان. ولإضافة تفريع منطقي للأسئلة يرجى اتباع الخطوات التالية</p>
	<ol style="padding: 0em 4em;">
	<li>اختر صفحة استبيان تحتوي على الأسئلة لإضافة التفريع المنطقي.</li>
	<li>آنقر على تحرير فرع وتجاوز آلأيقونة المنطقية التي توجد في أعلى الصفحة.</li>
	<li>حدد الصفحة للذهاب إلى “الذهاب إلى” قائمة لوحة الاختيار.</li>
	<li>"اختر حالة للتطبيق في خانة “متى.</li>
	<li>تمكين الأسئلة من اتباع منطق.</li>
	<li>ضمن خانة ”القيم“ اختر الإجابات التي ستُفَعِّل القسم.</li>
	<li>آنقر على حفظ.</li>
	</ol><!-- logic_sec3 -->
	<h2 id="logic_sec3">ما هو تناغم مميزات الأجوبة وكيف يمكن استخدامها؟</h2>
	<p style="color: black;">
	تسمح خصائص التناغم المحددة من ضمن قائمة الأسئلة بدمجها مع باقي الأسل
	ة الأخرى. للاستفادة من وظائف التناغم من الواجب إضافة معيار ل
	نص السؤال. يقتضي المعيار رمز الدولار، انفتاح متعرج، رقم الصفحة، رقم السؤال، وإغلاق متعرج، مكتوبة بصورة مرتبة. على سبيل المثال، إذا ما أردنا أن نستعمل الجواب من السؤال الرابع من الصفحة الثان
	ية، ينبغي أن نضيف المعيار التالي لنص السؤال: $ {p2p4}</p>
	<p style="color: black;"><em> 
	:ملحوظة
	خصائص التناغم لا تعمل إلا بإحالة الإجابات  من الصفحات السابقة. إن أي محاولة لاستخدام جواب من الصفحات التالية لن تكون مفلحة </em></p>
	<!-- data_sec1 -->
	<h2 id="data_sec1">كيف يمكنني تحميل نسخ من الاستبيانات المنجزة؟</h2>
	<p style="color: black;">يمكن تحميل الاستبيانات على شكل ملف “بي دي إف” للمشاهدة، والطباعة، ولحفظ نسخ منها. لتحميل الاستبيانات يرجى اتباع الخطوات التالية</p>
	<ol style="padding: 0em 4em;">
	<li>اذهب إلى تسجيل الاستبيان في أعلى شريط القائمة وانقر على تسجيل.</li>
	<li>حدد الاستبيان، ثم انقر على مشاهدة التسجيلات.</li>
	<li>انقر على رابط مشاهدة الاستبيان المترابط مع الاستبيان لكي يتم تحميله.</li>
	<li>(PDF)انقر على اللوحة اليمنى في ”تحويل“ إلي بي دي إف.</li>
	<li>يتم عرض الاستبيان على شكل بي دي إف وبالإمكان سحبه أو حفظه.</li>
	</ol><!-- data_sec2 -->
	<h2 id="data_sec2">كيف يمكنني مشاهدة بيانات الاستبيان/الاحصائيات؟</h2>
	<p style="color: black;"> يمكن للاحصائيات أن تشاهد في البرنامج التطبيقي. والأسئلة، التي تم فصلها إلى صفحات خاصة، يتم عرضها في اللوحة اليمنى عندما تتم مشاهدة البيانات في اللوحة اليسرى. 
	:وبالإمكان مشاهدة إحصائيات الاستبيان بتطبيق ما يلي</p>
	<ol style="padding: 0em 4em;">
	<li>آنقر على ”إحصائيات“ الموجود في أعلى شريط القائمة.</li>
	<li>حدد استبيانا وانقر على مشاهدة الإحصائيات.</li>
	</ol><!-- data_sec3 -->
	<h2 id="data_sec3">ماهي الخيارات المتاحة لإرسال إحصائيات الاستبيان؟</h2>
	<p style="color: black;">للاستبيانات التي تحتوي على أعداد كبرى من البيانات. لإرسال إحصائيات الاستبيان يرجى استكمال الخطوات التالية
	كما تمت الإشارة إلى ذلك في ”الموضوع“، واستخدم comma delimited value أو هيآت SPSS
	ثمة ثلاث صيغ لإرسال إحصائيات الاستبيان لبيانات التحليل. يمكن إرسال إحصائيات الاستبيان  على شكل ملف إكسيل، comma delimited file****، أو ملف SPSS.
	</p>
	<ol style="padding: 0em 4em;">
	<li>"Survey entries" آنقر على رابط إرسال الموجود أسفل</li>
	<li>تحديد الاستبيان الذي سيتم إرساله من القائمة.</li>
	<li>اختر الشكل الذي سيتم إرسال الاستبيان إليه من خلال النقر على أحد الروابط الموجودة أسفل خانة الإجراءات.</li>
	<li>حدد دليلا واسم الملف، ثم انقر على حفظ.</li>
	</ol><!-- security_sec1 -->
	<h2 id="security_sec1">كيف يمكنني إنشاء مستخدمين؟</h2>
	<p style="color: black;">عملية إنشاء المستخدمَين الداخلي والخارجي لا تتم إلا من قبل مشرفين. ولإنشاء مستخدمين جدد يرجى اتباع الخطوات التالية</p>
	<ol style="padding: 0em 4em;">
	<li>التأكد من الدخول كمشرف.</li>
	<li>اذهب في أعلى شريط القائمة إلى لوحة الحماية وآنقر على المستخدمين الداخليين.</li>
	<li>آنقر على إضافة مستخدم داخلي جديد من اللوحة اليمنى.</li>
	<li>آنقر في صفحة مستخدم جديد على الخيار الممكن لتفعيل الحساب أو دع الصفحة فارغة إذا ظل الحساب معطلا.</li>
	<li>املأ باقي الحقوق الضرورية.<br /><em>ملحوظة: إن خانة تاريخ الازدياد تقبل بصيغة ش ش/ي ي/س س س 
	س. وينبغي أن لا تقل كلمة السر على ثمانية أحرف، وتتضمن رقما واحدا على الأقل، وحرفا صغيرا على الأقل وآ
	.[ @#$%^&+=]خر مرتفع، إلى جانب إحدى العلامات التالية </em></li>
	<li>حدد مجموعة لكي ينتمي إليها المستخدم.</li>
	<li>إذا كانت المجموعة المحددة هي ممن يشرفون على الاستبيان، فيجب اختيار قسم أو الأقسام التي سيكون بإمكان المستخدم دخولها.</li>
	</ol>
	<p style="color: black;">إن عملية إنشاء مستخدم خارجي متطابقة بصورة جوهرية، عدا الخيار الأوحد الذي يوجد ضمن فرع المجموع
	ات وهم المشاركون في الاستبيان. وبدلا من أن يكون هناك إمكانية لدخول المستخدمين الخارجيين ثمة إمكانية لدخول الاستبيانات.
	</p>
	<!-- security_sec2 -->
	<h2 id="security_sec2">أين يوجد خيار إنشاء استبيان خاص أو عام؟</h2>
	<p style="color: black;">يوجد خيار إنشاء استبيان خاص أو عام في ”إنشاء استبيان جديد“ و ”تحديث نوافذ الاستبيان“. إن الخانة تحمل اسم ”متاح للعموم“.</p>
	<!-- security_sec3 -->
	<h2 id="security_sec3">حينما أحاول تسجيل الدخول أتلقى رسالة تخبرني بأن محاولتي الدخول لم تنجح، المرجو المحاولة مرة أخرى. السبب: عجز المستخدم. لماذا يحدث هذا؟</h2>
	<p style="color: black;">تظهر هذه الرسالة حينما  يحاول المستخدم تسجيل الدخول في البرنامج التطبيقي من خلال حساب معطل. غير أنه يمكن للمشرف أن يعالج المسألة عبر:</p>
	<ol style="padding: 0em 4em;">
	<li>الذهاب إلى لوحة الحماية الموجودة في أعلى شريط القائمة.</li>
	<li>بحسب حساب المستخدم، انقر على المستخدم الداخلي أو الخارجي.</li>
	<li>ابحث عن الحساب المعطل وانقر على رابط تحديث المستخدم الموجود ضمن خانة الإجراءات.</li>
	<li>تحقق من خانة التمكين وآنقر على حفظ.</li>
	</ol><!-- other_sec1 -->
	<h2 id="other_sec1">كيف يمكنني أن أرسل أو أجلب استبيانات؟</h2>
	<p style="color: black;">: يستوجب إرسال استبيانات بضع خطوات</p>
	<ol style="padding: 0em 4em;">
	<li>الذهاب إلى خانة البدايات والنقر على الاستبيانات.</li>
	<li>البحث عن الاستبيان موضوع الإرسال والنقر على رابط مشاهدة الاستبيان الموجود ضمن خانة الإجراءات.</li>
	<li>انطلاقا من اللوحة اليمنى، آنقر على إرسال الاستبيان.</li>
	<li>اختر دليلا وخصص اسما للملف.</li>
	</ol>
	<p style="color: black;">: لجلب الاستبيانات يرجى اتباع الخطوات التالية</p>
	<ol style="padding: 0em 4em;">
	<li>اذهب إلى إعدادات وانقر على استبيانات.</li>
	<li>آنقر على جلب استبيانات من اللوحة اليمنى.</li>
	<li>حدد قسما، ثم آدخل اسما للاستبيان، وآنقر على اختر ملفا.</li>
	<li>تصفح بصورة مباشرة ملف الاستبيان وافتحه.</li>
	<li>آنقر تحميل.</li>
	</ol>
	<p style="color: black;">: ثمة طريقة بديلة لجلب الاستبيانات المتاحة، ولكنها متاحة للمشرفين فقط</p>
	<ol style="padding: 0em 4em;">
	<li>آذهب إلى خانة الحماية وآنقر على أقسام.</li>
	<li>حدد قسما وآنقر على رابط مشاهدة قسم الموجود ضمن خانة الإجراءات.</li>
	<li>آنقر على جلب استبيانات من اللوحة اليمنى.</li>
	<li>اكتب اسما للاستبيان.</li>
	<li>حدد ملف الاستبيان وانقر على تحميل.</li>
	</ol><!-- other_sec2 -->
	<h2 id="other_sec2">ماهي مجموعة البيانات؟</h2>
	<p style="color: black;">ثمة نموذج لمجموعة البيانات متاح للتحميل وهو موجود في 
	صفحة تحميل البيانات. وجب التنويه إلى أن عملية الإضافة، والتحديث، وحذف البيانات في متناول المشرفين فقط. لإضافة مجموعات البيانات ينبغي القيام بما يلي
	تشكل البيانات مجموعة من البيانات التي يمكن جلبها ومن ثم استخدامها لتموين قائمة الخيارات. والصيغة المقبولة من البيانات هي comma delimited values file
	</p>
	<ol style="padding: 0em 4em;">
	<li>ضمن ”إعدادات“ آنقر على مجموعة البيانات.</li>
	<li>آنقر على إضافة بيانات.</li>
	</ol><!-- other_sec3 -->
	<h2 id="other_sec3">كيف يمكنني أن أرسل  دعوات الاستبيان لمجموعة من المشاركين؟</h2>
	<p style="color: black;">
	comma delimited values file هي الاسم الشخصي، والاسم الأوسط، ثم الاسم العائلي، وعنوان البريد الإلكتروني. يمكن تحميل صفحة نموذجية لدعوة المشاركين.
	يحتوي   comma delimited file على قائمة من المعلوم
	ات الخاصة بالمشاركين في الاستبيان، والتي يمكن استخدامها لإرسال الدعوات للمشاركة في الاستبيانات. والصيغة الموجودة في 
	</p>
	<p style="color: black;"><em>ملحوظة: دعوات الاستبيان لا يمكن أن يقوم بها إلا المشرف.</em></p>
	<p style="color: black;">:وخطوات إرسال عدوات المشاركة في الاستبيان هي</p>
	<ol style="padding: 0em 4em;">
	<li>آنقر على رابط دعوات الموجود في خانة إعدادات.</li>
	<li>آنقر على دعوة مشاركين، الموجودة في اللوحة اليمنى.</li>
	<li>حدد استبيانا واختر ملف الدعوات.</li>
	<li>نقر تحميل.</li>
	</ol>
	<p style="color: black;"></p>
	<!-- other_sec4 -->
	<h2 id="other_sec4">ما هي القوالب؟</h2>
	<p style="color: black;">
	.القوالب هي نماذج نصية ينبغي أن يلتزم بها المشارك عند الإجابة عن السؤال، فحين يطبق قالب على سؤال ما فإن  البرنامج سيلزم المشارك بإدخال النص وفقا للنموذج المعمول به. 
	 وحدهم المشرفون يمكنهم تحديث أو حذف القوالب. وعليه فإن تطبيقها على الأسئلة يتم لحظة إنشاء السؤال أو أثناء عملية التحديث ضمن ”التحقق من المعلومات
	"Validation Information section".
	 </p>
	</body></html>'
,0,'2012-10-21 14:44:53');

INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (4,'دعوة البريد الإلكتروني','<html><body><h2>: دعوات</h2>
<p>${full_name}; مرحبا</p>
<p>:كنت قد دعيت للمشاركة في الدراسة الاستقصائية<br />
 ${survey_name} الدراسة الاستقصائية <br /><br />: للمشاركة يرجى النقر على الرابط التالي</p><p>${survey_link}</p></body></html>',0,'2012-10-21 14:44:53');


INSERT INTO velocity_template (id,`template_name`,template_definition,version,template_timestamp)
VALUES (5,'محتوى الصفحة المكتملة','وقد قدم المسح الخاص بك. شكرا لك على المشاركة',0,'2012-10-21 14:44:53');




















