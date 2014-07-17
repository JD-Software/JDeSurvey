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
import org.springframework.core.convert.converter.Converter;

import com.jd.survey.domain.survey.Survey;
import com.jd.survey.service.survey.SurveyService;


/**
 * A central place to register survey converters and formatters. 
 */
@Configurable
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean{
	private static final Log log = LogFactory.getLog(ApplicationConversionServiceFactoryBean.class);

	@Autowired
	private SurveyService surveyService;
	
	
	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register survey converters and formatters
	}
	

	public Converter<Survey, String> getSurveyToStringConverter() {
        return new Converter<Survey, java.lang.String>() {
            public String convert(Survey surveyDefinition) {
            	log.info("converting SurveyToString");
            	return new StringBuilder().append(surveyDefinition.getTypeName()).toString();
            }
        };
    }
    public Converter<Long, Survey> getIdToSurveyConverter() {
        return new Converter<java.lang.Long,  Survey>() {
            public  Survey convert(java.lang.Long id) {
            	log.info("converting Long to Survey id=" + id + " result" + surveyService.survey_findById(id).toString());
                return surveyService.survey_findById(id);
            }
        };
    }
    public Converter<String, Survey> getStringToSurveyConverter() {
        return new Converter<java.lang.String, Survey>() {
            public Survey convert(String id) {
            	log.info("converting String to Survey id=" + id);
                return getObject().convert(getObject().convert(id, Long.class), Survey.class);
            }
        };
    }
    
    
    
    
    
    
    
    
	
	
	
    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(getSurveyToStringConverter());
        registry.addConverter(getIdToSurveyConverter());
        registry.addConverter(getStringToSurveyConverter());
        
    }
    
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
