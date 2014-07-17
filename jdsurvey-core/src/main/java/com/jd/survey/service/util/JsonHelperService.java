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
package com.jd.survey.service.util;

import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.stereotype.Service;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.util.HibernateProxySerializer;

@Service("JsonHelperService")
public class JsonHelperService {
	private static final Log log = LogFactory.getLog(JsonHelperService.class);

	
	
	public String serializeSurveyDefinition(SurveyDefinition surveyDefinition){
		try{
			GsonBuilder gsonBuilder = new GsonBuilder();
			//set up the fields to skip in the serialization
			gsonBuilder = gsonBuilder.setExclusionStrategies(new ExclusionStrategy() {
		        						public boolean shouldSkipClass(Class<?> clazz) {
		        							return false;
		        						}
		        						@Override
		        						public boolean shouldSkipField(FieldAttributes f) {
		        						boolean skip = (f.getDeclaringClass() == SurveyDefinition.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == SurveyDefinition.class && f.getName().equals("version"))||
		        									   (f.getDeclaringClass() == SurveyDefinition.class && f.getName().equals("department"))||
		        									   (f.getDeclaringClass() == SurveyDefinition.class && f.getName().equals("users"))||
		        									   (f.getDeclaringClass() == SurveyDefinitionPage.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == SurveyDefinitionPage.class && f.getName().equals("surveyDefinition"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("version"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("page"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("optionsList"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("rowLabelsList"))||
		        									   (f.getDeclaringClass() == Question.class && f.getName().equals("columnLabelsList"))||
		        									   (f.getDeclaringClass() == QuestionOption.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == QuestionOption.class && f.getName().equals("version"))||
		        						               (f.getDeclaringClass() == QuestionOption.class && f.getName().equals("question")) ||
		        						               (f.getDeclaringClass() == QuestionRowLabel.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == QuestionRowLabel.class && f.getName().equals("version"))||
		        						               (f.getDeclaringClass() == QuestionRowLabel.class && f.getName().equals("question")) ||
		        						               (f.getDeclaringClass() == QuestionColumnLabel.class && f.getName().equals("id"))||
		        									   (f.getDeclaringClass() == QuestionColumnLabel.class && f.getName().equals("version"))||
		        									   (f.getDeclaringClass() == QuestionColumnLabel.class && f.getName().equals("question"));
		        						return skip;
		        						}

		     });
			
			//de-proxy the object
			gsonBuilder.registerTypeHierarchyAdapter(HibernateProxy.class, new HibernateProxySerializer());
			Hibernate.initialize(surveyDefinition);
			if (surveyDefinition instanceof HibernateProxy)  {
			  surveyDefinition = (SurveyDefinition) ((HibernateProxy)surveyDefinition).getHibernateLazyInitializer().getImplementation();
			}
			Gson gson =  gsonBuilder.serializeNulls().create();
			return gson.toJson(surveyDefinition);
		
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
		
	}
	
	
	
	
	public SurveyDefinition deSerializeSurveyDefinition(String jsonString){
		try{
			Gson gson = new Gson();
			SurveyDefinition surveyDefinition =  (SurveyDefinition) gson.fromJson(jsonString,SurveyDefinition.class);
			return surveyDefinition;
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
		
	}
	
	
	
	
	
	
	

}
