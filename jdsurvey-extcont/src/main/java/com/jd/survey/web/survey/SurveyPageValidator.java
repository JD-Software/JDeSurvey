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
package com.jd.survey.web.survey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.jd.survey.domain.survey.SurveyPage;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.QuestionAnswerValidator;

public class SurveyPageValidator implements Validator{
	private static final Log log = LogFactory.getLog(SurveyPageValidator.class);
	

	private String dateFormat;
	private String validcontentTypes;
	private String validImageTypes;
	private Integer maximunFileSize;
	private String invalidContentMessage;
	private String invalidFileSizeMessage;

	public SurveyPageValidator(String dateFormat,
									String 	validcontentTypes, 
									String validImageTypes,
									Integer maximunFileSize,
									String invalidContentMessage,
									String invalidFileSizeMessage) {
		super();
		this.dateFormat=dateFormat; 
		this.validcontentTypes=validcontentTypes.toLowerCase();
		this.validImageTypes=validImageTypes.toLowerCase();
		this.maximunFileSize=maximunFileSize;
		this.invalidContentMessage=invalidContentMessage;
		this.invalidFileSizeMessage=invalidFileSizeMessage;
	}


	@Override
	public boolean supports(Class clazz) {
        return SurveyPage.class.equals(clazz);
    }
	@Override
	public void validate(Object obj, Errors errors) {
		SurveyPage surveyPage= (SurveyPage) obj;
		
		QuestionAnswerValidator questionAnswerValidator;
		int i = 0;
		for (QuestionAnswer questionAnswer : surveyPage.getQuestionAnswers()) {
			log.info("Validating question answer" +  questionAnswer.getQuestion().getQuestionText());
			errors.pushNestedPath("questionAnswers[" + i +"]");
			questionAnswerValidator = new QuestionAnswerValidator(dateFormat,
																	validcontentTypes,
																	validImageTypes,
																	maximunFileSize,
																	invalidContentMessage,
																	invalidFileSizeMessage);
			ValidationUtils.invokeValidator(questionAnswerValidator, questionAnswer, errors);
			errors.popNestedPath();
			i++;
		}
	}
}
