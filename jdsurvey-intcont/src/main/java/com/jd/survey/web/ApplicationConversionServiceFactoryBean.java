  /*Copyright (C) 2014  JD Software, Inc.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of the
    License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
  */
package com.jd.survey.web;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.converter.Converter;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.settings.*;
import com.jd.survey.domain.security.Authority;
import com.jd.survey.domain.security.Group;
import com.jd.survey.domain.security.User;
import com.jd.survey.service.settings.ApplicationSettingsService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.security.UserService;


/**
 * A central place to register survey converters and formatters. 
 */
@Configurable
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean{
	private static final Log log = LogFactory.getLog(ApplicationConversionServiceFactoryBean.class);

	@Autowired	private ApplicationContext applicationContext;
	
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private ApplicationSettingsService applicationSettingsService;
	@Autowired	private UserService userService;
	
	
	
	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register survey converters and formatters
	}
	
	
	public Converter<VelocityTemplate, String> getVelocityTemplateToStringConverter() {
        return new Converter<VelocityTemplate, java.lang.String>() {
            public String convert(VelocityTemplate velocityTemplate) {
            	log.info("converting VelocityTemplateToString");
            	return new StringBuilder().append(velocityTemplate.getName()).toString();
            }
        };
    }
    public Converter<Long, VelocityTemplate> getIdToVelocityTemplateConverter() {
        return new Converter<java.lang.Long,  VelocityTemplate>() {
            public  VelocityTemplate convert(java.lang.Long id) {
            	log.info("converting Long to VelocityTemplate id=" + id + " result" + surveySettingsService.velocityTemplate_findById(id).toString());
                return surveySettingsService.velocityTemplate_findById(id);
            }
        };
    }
    public Converter<String, VelocityTemplate> getStringToVelocityTemplateConverter() {
        return new Converter<java.lang.String, VelocityTemplate>() {
            public VelocityTemplate convert(String id) {
            	log.info("converting String to VelocityTemplate id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), VelocityTemplate.class);
            }
        };
    }
    
	
	public Converter<Department, String> getDepartmentToStringConverter() {
        return new Converter<Department, java.lang.String>() {
            public String convert(Department department) {
            	log.info("converting DepartmentToString");
            	return new StringBuilder().append(department.getName()).toString();
            }
        };
    }
    public Converter<Long, Department> getIdToDepartmentConverter() {
        return new Converter<java.lang.Long,  Department>() {
            public  Department convert(java.lang.Long id) {
            	log.info("converting Long to Department id=" + id + " result" + surveySettingsService.department_findById(id).toString());
                return surveySettingsService.department_findById(id);
            }
        };
    }
    public Converter<String, Department> getStringToDepartmentConverter() {
        return new Converter<java.lang.String, Department>() {
            public Department convert(String id) {
            	log.info("converting String to Department id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Department.class);
            }
        };
    }
	
	
	
	
	
	

	public Converter<SurveyDefinition, String> getSurveyDefinitionToStringConverter() {
        return new Converter<SurveyDefinition, java.lang.String>() {
            public String convert(SurveyDefinition surveyDefinition) {
            	log.info("converting SurveyDefinitionToString");
            	return new StringBuilder().append(surveyDefinition.getName()).toString();
            }
        };
    }
    public Converter<Long, SurveyDefinition> getIdToSurveyDefinitionConverter() {
        return new Converter<java.lang.Long,  SurveyDefinition>() {
            public  SurveyDefinition convert(java.lang.Long id) {
            	log.info("converting Long to SurveyDefinition id=" + id + " result" + surveySettingsService.surveyDefinition_findById(id).toString());
                return surveySettingsService.surveyDefinition_findById(id);
            }
        };
    }
    
    public Converter<String, SurveyDefinition> getStringToSurveyDefinitionConverter() {
        return new Converter<java.lang.String, SurveyDefinition>() {
            public SurveyDefinition convert(String id) {
            	log.info("converting String to SurveyDefinition id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), SurveyDefinition.class);
            }
        };
    }
    
    
    
    
    public Converter<SurveyDefinitionPage, String> getSurveyDefinitionPageToStringConverter() {
        return new Converter<SurveyDefinitionPage, java.lang.String>() {
            public String convert(SurveyDefinitionPage surveyDefinitionPage) {
            	log.info("converting SurveyDefinitionPageToString");
            	return new StringBuilder().append(surveyDefinitionPage.toString()).toString();
            }
        };
    }
    public Converter<Long, SurveyDefinitionPage> getIdToSurveyDefinitionPageConverter() {
        return new Converter<java.lang.Long,  SurveyDefinitionPage>() {
            public  SurveyDefinitionPage convert(java.lang.Long id) {
            	log.info("converting Long to SurveyDefinitionPage id=" + id + " result" + surveySettingsService.surveyDefinitionPage_findById(id).toString());
                return surveySettingsService.surveyDefinitionPage_findById(id);
            }
        };
    }
    public Converter<String, SurveyDefinitionPage> getStringToSurveyDefinitionPageConverter() {
        return new Converter<java.lang.String, SurveyDefinitionPage>() {
            public SurveyDefinitionPage convert(String id) {
            	log.info("converting String to SurveyDefinitionPage id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), SurveyDefinitionPage.class);
            }
        };
    }
    
    
    
    
    
    
    
	public Converter<QuestionOption, String> getQuestionOptionToStringConverter() {
        return new Converter<QuestionOption, java.lang.String>() {
            public String convert(QuestionOption questionOption) {
            	log.info("converting QuestionOptionToString");
            	return new StringBuilder().append(questionOption.getText()).toString();
            }
        };
    }
    public Converter<Long, QuestionOption> getIdToQuestionOptionConverter() {
        return new Converter<java.lang.Long,  QuestionOption>() {
            public  QuestionOption convert(java.lang.Long id) {
            	log.info("converting Long to QuestionOption id=" + id + " result" + surveySettingsService.questionOption_findById(id).toString());
                return surveySettingsService.questionOption_findById(id);
            }
        };
    }
    public Converter<String, QuestionOption> getStringToQuestionOptionConverter() {
        return new Converter<java.lang.String, QuestionOption>() {
            public QuestionOption convert(String id) {
            	log.info("converting String to QuestionOption id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), QuestionOption.class);
            }
        };
    }
    
    
    public Converter<QuestionType, String> getQuestionTypeToStringConverter() {
        return new Converter<QuestionType, java.lang.String>() {
            public String convert(QuestionType questionType) {
            	log.info("converting QuestionTypeToString");
            	return questionType.getCode();
            }
        };
    }
    public Converter<String, QuestionType> getStringToQuestionTypeConverter() {
        return new Converter<java.lang.String, QuestionType>() {
            public QuestionType convert(String id) {
            	log.info("converting String to Question type id=" + id);
                return QuestionType.fromCode(id);
            }
        };
    }
    
    
    public Converter<Question, String> getQuestionToStringConverter() {
        return new Converter<Question, java.lang.String>() {
            public String convert(Question question) {
            	log.info("converting QuestionToString");
            	return new StringBuilder().append(question.toString()).toString();
            }
        };
    }
    public Converter<Long, Question> getIdToQuestionConverter() {
        return new Converter<java.lang.Long,  Question>() {
            public  Question convert(java.lang.Long id) {
            	log.info("converting Long to Question id=" + id + " result" + surveySettingsService.question_findById(id).toString());
                return surveySettingsService.question_findById(id);
            }
        };
    }
    public Converter<String, Question> getStringToQuestionConverter() {
        return new Converter<java.lang.String, Question>() {
            public Question convert(String id) {
            	log.info("converting String to Question id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Question.class);
            }
        };
    }
    
    
    
    
    public Converter<User, String> getUserToStringConverter() {
        return new Converter<User, java.lang.String>() {
            public String convert(User user) {
            	log.info("converting UserToString");
            	return new StringBuilder().append(user.toString()).toString();
            }
        };
    }
    public Converter<Long, User> getIdToUserConverter() {
        return new Converter<java.lang.Long,  User>() {
            public  User convert(java.lang.Long id) {
            	log.info("converting Long to User id=" + id + " result" + userService.user_findById(id).toString());
                return userService.user_findById(id);
            }
        };
    }
    public Converter<String, User> getStringToUserConverter() {
        return new Converter<java.lang.String, User>() {
            public User convert(String id) {
            	log.info("converting String to User id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), User.class);
            }
        };
    }
	
	
	
	
	 public Converter<Group, String> getGroupToStringConverter() {
        return new Converter<Group, java.lang.String>() {
            public String convert(Group group) {
            	log.info("converting GroupToString");
            	return new StringBuilder().append(group.toString()).toString();
            }
        };
    }
    public Converter<Long, Group> getIdToGroupConverter() {
        return new Converter<java.lang.Long,  Group>() {
            public  Group convert(java.lang.Long id) {
            	log.info("converting Long to Group id=" + id + " result" + userService.group_findById(id).toString());
                return userService.group_findById(id);
            }
        };
    }
    public Converter<String, Group> getStringToGroupConverter() {
        return new Converter<java.lang.String, Group>() {
            public Group convert(String id) {
            	log.info("converting String to Group id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Group.class);
            }
        };
    }
	
	 public Converter<Authority, String> getAuthorityToStringConverter() {
        return new Converter<Authority, java.lang.String>() {
            public String convert(Authority authority) {
            	log.info("converting AuthorityToString");
            	return new StringBuilder().append(authority.toString()).toString();
            }
        };
    }
    public Converter<Long, Authority> getIdToAuthorityConverter() {
        return new Converter<java.lang.Long,  Authority>() {
            public  Authority convert(java.lang.Long id) {
            	log.info("converting Long to Authority id=" + id + " result" + userService.authority_findById(id).toString());
                return userService.authority_findById(id);
            }
        };
    }
    public Converter<String, Authority> getStringToAuthorityConverter() {
        return new Converter<java.lang.String, Authority>() {
            public Authority convert(String id) {
            	log.info("converting String to Authority id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Authority.class);
            }
        };
    }
    
    
  
    public Converter<DataSetItem, String> getDataSetItemToStringConverter() {
        return new Converter<DataSetItem, java.lang.String>() {
            public String convert(DataSetItem dataSetItem) {
            	log.info("converting DataSetItemToString");
            	return new StringBuilder().append(dataSetItem.getText()).toString();
            }
        };
    }
    public Converter<Long, DataSetItem> getIdToDataSetItemConverter() {
        return new Converter<java.lang.Long,  DataSetItem>() {
            public  DataSetItem convert(java.lang.Long id) {
            	log.info("converting Long to DataSetItem id=" + id + " result" + surveySettingsService.velocityTemplate_findById(id).toString());
                return surveySettingsService.datasetItem_findById(id);
            }
        };
    }
    public Converter<String, DataSetItem> getStringToDataSetItemConverter() {
        return new Converter<java.lang.String, DataSetItem>() {
            public DataSetItem convert(String id) {
            	log.info("converting String to DataSetItem id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), DataSetItem.class);
            }
        };
    }
    
	
	
    public Converter<DataSet, String> getDataSetToStringConverter() {
        return new Converter<DataSet, java.lang.String>() {
            public String convert(DataSet dataSet) {
            	log.info("converting DataSetToString");
            	return new StringBuilder().append(dataSet.getName()).toString();
            }
        };
    }
    public Converter<Long, DataSet> getIdToDataSetConverter() {
        return new Converter<java.lang.Long,  DataSet>() {
            public  DataSet convert(java.lang.Long id) {
            	log.info("converting Long to DataSet id=" + id + " result" + surveySettingsService.velocityTemplate_findById(id).toString());
                return surveySettingsService.dataSet_findById(id);
            }
        };
    }
    public Converter<String, DataSet> getStringToDataSetConverter() {
        return new Converter<java.lang.String, DataSet>() {
            public DataSet convert(String id) {
            	log.info("converting String to DataSet id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), DataSet.class);
            }
        };
    }
    
	
    public Converter<RegularExpression, String> getRegularExpressionToStringConverter() {
        return new Converter<RegularExpression, java.lang.String>() {
            public String convert(RegularExpression regularExpression) {
            	log.info("converting regularExpressionToString");
            	return new StringBuilder().append(regularExpression.getName()).toString();
            }
        };
    }
    public Converter<Long, RegularExpression> getIdToRegularExpressionConverter() {
        return new Converter<java.lang.Long,  RegularExpression>() {
            public  RegularExpression convert(java.lang.Long id) {
            	log.info("converting Long to RegularExpression id=" + id );
                return surveySettingsService.regularExpression_findById(id);
            }
        };
    }
    public Converter<String, RegularExpression> getStringToRegularExpressionConverter() {
        return new Converter<java.lang.String, RegularExpression>() {
            public RegularExpression convert(String id) {
            	log.info("converting String to RegularExpression id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), RegularExpression.class);
            }
        };
    }
	
	
    public Converter<GroupingOperator, String> geGroupingOperatorToStringConverter() {
        return new Converter<GroupingOperator, java.lang.String>() {
            public String convert(GroupingOperator groupingOperator) {
            	log.info("converting QuestionTypeToString");
            	return groupingOperator.getCode();
            }
        };
    }
    public Converter<String, GroupingOperator> getStringToGroupingOperatorConverter() {
        return new Converter<java.lang.String, GroupingOperator>() {
            public GroupingOperator convert(String id) {
            	log.info("converting String to Question type id=" + id);
                return GroupingOperator.fromCode(id);
            }
        };
    }
    
    public Converter<LogicOperator, String> getLogicOperatorToStringConverter() {
        return new Converter<LogicOperator, java.lang.String>() {
            public String convert(LogicOperator logicOperator) {
            	log.info("converting QuestionTypeToString");
            	return logicOperator.getCode();
            }
        };
    }
    public Converter<String, LogicOperator> getStringLogicOperatorTypeConverter() {
        return new Converter<java.lang.String, LogicOperator>() {
            public LogicOperator convert(String id) {
            	log.info("converting String to Question type id=" + id);
                return LogicOperator.fromCode(id);
            }
        };
    }
    
    
    
    public Converter<SurveyTemplate, String> getSurveyTemplateToStringConverter() {
        return new Converter<SurveyTemplate, java.lang.String>() {
            public String convert(SurveyTemplate surveyTemplate) {
            	log.info("converting SurveyTemplateToString");
            	return new StringBuilder().append(surveyTemplate.getName()).toString();
            }
        };
    }
    public Converter<Long, SurveyTemplate> getIdToSurveyTemplateConverter() {
        return new Converter<java.lang.Long,  SurveyTemplate>() {
            public  SurveyTemplate convert(java.lang.Long id) {
            	log.info("converting Long to SurveyTemplate id=" + id + " result" + surveySettingsService.surveyTemplate_findById(id));
                return surveySettingsService.surveyTemplate_findById(id);
            }
        };
    }
    public Converter<String, SurveyTemplate> getStringToSurveyTemplateConverter() {
        return new Converter<java.lang.String, SurveyTemplate>() {
            public SurveyTemplate convert(String id) {
            	log.info("converting String to SurveyTemplate id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), SurveyTemplate.class);
            }
        };
    }
    public Converter<Sector, String> getSectorToStringConverter() {
        return new Converter<Sector, java.lang.String>() {
            public String convert(Sector sector) {
            	log.info("converting SectorToString");
            	return new StringBuilder().append(sector.getName()).toString();
            }
        };
    }
    public Converter<Long, Sector> getIdToSectorConverter() {
        return new Converter<java.lang.Long,  Sector>() {
            public  Sector convert(java.lang.Long id) {
            	log.info("converting Long to Sector id=" + id + " result" + surveySettingsService.sector_findById(id));
                return surveySettingsService.sector_findById(id);
            }
        };
    }
    public Converter<String, Sector> getStringToSectorConverter() {
        return new Converter<java.lang.String, Sector>() {
            public Sector convert(String id) {
            	log.info("converting String to Sector id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Sector.class);
            }
        };
    }
    
    public Converter<Day, String> getDayToStringConverter() {
        return new Converter<Day, java.lang.String>() {
            public String convert(Day day) {
            	return new StringBuilder().append(day.getDayName()).toString();
            }
        };
    }
    public Converter<Long, Day> getIdToDayConverter() {
        return new Converter<java.lang.Long,  Day>() {
            public  Day convert(java.lang.Long id) {
                return surveySettingsService.day_findById(id);
            }
        };
    }
    public Converter<String, Day> getStringToDayConverter() {
        return new Converter<java.lang.String, Day>() {
            public Day convert(String id) {
                return getObject().convert(getObject().convert(id, Long.class), Day.class);
            }
        };
    }
    

    
    
    public Converter<GlobalSettings, String> getGlobalSettingsToStringConverter() {
        return new Converter<GlobalSettings, java.lang.String>() {
            public String convert(GlobalSettings globalSettings) {
            	log.info("converting DepartmentToString");
            	return new StringBuilder().append(globalSettings.getPasswordEnforcementRegex()).toString();
            }
        };
    }
    public Converter<Long, GlobalSettings> getIdToGlobalSettingsConverter() {
        return new Converter<java.lang.Long,  GlobalSettings>() {
            public  GlobalSettings convert(java.lang.Long id) {
            	log.info("converting Long to globalSettings id=" + id + " result" + applicationSettingsService.globalSettings_findById(id).toString());
                return applicationSettingsService.globalSettings_findById(id);
            }
        };
    }
    public Converter<String, GlobalSettings> getStringToGlobalSettingsConverter() {
        return new Converter<java.lang.String, GlobalSettings>() {
            public GlobalSettings convert(String id) {
            	log.info("converting String to Department id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), GlobalSettings.class);
            }
        };
    }
	
    
    public void installLabelConverters(FormatterRegistry registry) {
    	
    	
    	registry.addConverter(getVelocityTemplateToStringConverter());
        registry.addConverter(getIdToVelocityTemplateConverter());
        registry.addConverter(getStringToVelocityTemplateConverter());
        
    	
    	registry.addConverter(getDepartmentToStringConverter());
        registry.addConverter(getIdToDepartmentConverter());
        registry.addConverter(getStringToDepartmentConverter());
        
        
        registry.addConverter(getSurveyDefinitionToStringConverter());
        registry.addConverter(getIdToSurveyDefinitionConverter());
        registry.addConverter(getStringToSurveyDefinitionConverter());

        registry.addConverter(getQuestionOptionToStringConverter());
        registry.addConverter(getIdToQuestionOptionConverter());
        registry.addConverter(getStringToQuestionOptionConverter());
        

        registry.addConverter(getQuestionTypeToStringConverter());
        registry.addConverter(getStringToQuestionTypeConverter());
        
        registry.addConverter(getQuestionToStringConverter());
        registry.addConverter(getIdToQuestionConverter());
        registry.addConverter(getStringToQuestionConverter());
        
        registry.addConverter(getSurveyDefinitionPageToStringConverter());
        registry.addConverter(getIdToSurveyDefinitionPageConverter());
        registry.addConverter(getStringToSurveyDefinitionPageConverter());
        
        
        
        registry.addConverter(getUserToStringConverter());
        registry.addConverter(getIdToUserConverter());
        registry.addConverter(getStringToUserConverter());
        
        
        registry.addConverter(getGroupToStringConverter());
        registry.addConverter(getIdToGroupConverter());
        registry.addConverter(getStringToGroupConverter());
        
        
        registry.addConverter(getAuthorityToStringConverter());
        registry.addConverter(getIdToAuthorityConverter());
        registry.addConverter(getStringToAuthorityConverter());
        

        registry.addConverter(getDataSetToStringConverter());
        registry.addConverter(getIdToDataSetConverter());
        registry.addConverter(getStringToDataSetConverter());
        
        
        registry.addConverter(getDataSetItemToStringConverter());
        registry.addConverter(getIdToDataSetItemConverter());
        registry.addConverter(getStringToDataSetItemConverter());
        
        
        registry.addConverter(getRegularExpressionToStringConverter());
        registry.addConverter(getIdToRegularExpressionConverter());
        registry.addConverter(getStringToRegularExpressionConverter());
        
    
        //custom DateTimeFormatter
        DateTimeFormatAnnotationFormatterFactory dateTimeFormatAnnotationFormatterFactory = new DateTimeFormatAnnotationFormatterFactory();
		dateTimeFormatAnnotationFormatterFactory.setApplicationContext(applicationContext);
		registry.addFormatterForFieldAnnotation(dateTimeFormatAnnotationFormatterFactory);
    
		registry.addConverter(geGroupingOperatorToStringConverter()); 
		registry.addConverter(getStringToGroupingOperatorConverter()); 
		registry.addConverter(getLogicOperatorToStringConverter()); 
		registry.addConverter(getStringLogicOperatorTypeConverter()); 

		registry.addConverter(getSectorToStringConverter());
        registry.addConverter(getIdToSectorConverter());
        registry.addConverter(getStringToSectorConverter());
		
        registry.addConverter(getSurveyTemplateToStringConverter());
        registry.addConverter(getIdToSurveyTemplateConverter());
        registry.addConverter(getStringToSurveyTemplateConverter());
		
               
        registry.addConverter(getDayToStringConverter());
        registry.addConverter(getIdToDayConverter());
        registry.addConverter(getStringToDayConverter());
        
        registry.addConverter(getGlobalSettingsToStringConverter());
        registry.addConverter(getIdToGlobalSettingsConverter());
        registry.addConverter(getStringToGlobalSettingsConverter());
    }
    
    
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
	
	
    




	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
