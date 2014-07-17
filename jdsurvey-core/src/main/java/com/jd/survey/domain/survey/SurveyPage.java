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
package com.jd.survey.domain.survey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.validator.routines.DateValidator;
import org.mvel2.MVEL;

import com.jd.survey.domain.settings.GroupingOperator;
import com.jd.survey.domain.settings.LogicalCondition;
import com.jd.survey.domain.settings.PageLogic;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinitionPage;

/*
@Entity
@NamedQueries({
	@NamedQuery(name = "SurveyPage.findAll", query = "select o from SurveyPage o"),
	@NamedQuery(name = "SurveyPage.findById", query = "select o from SurveyPage o where o.id = ?1"),
	@NamedQuery(name = "SurveyPage.findByPageOrder", query = "select o from SurveyPage o where o.survey.id = ?1 and o.order=?2"),
	@NamedQuery(name = "SurveyPage.getCount", query = "select count(o) from SurveyPage o")
	})
*/

public class SurveyPage implements Comparable <SurveyPage>,  Serializable{
	
		private static final long serialVersionUID = -8982532265326369227L;

		private Survey survey;
	    private Short order;
		private String title;
		private String instructions;
	    private List<QuestionAnswer> questionAnswers = new ArrayList<QuestionAnswer>();
	    //private String visibilityExpression;
	    private boolean randomizeQuestions = false;    
	    private boolean visible = true;
	    
	    public SurveyPage() {
			super();
			// TODO Auto-generated constructor stub
		}
	    
	    
	    public SurveyPage(Survey survey, SurveyDefinitionPage surveyDefinitionPage) {
	    		super();
	    		this.survey=survey;
	    		this.order=surveyDefinitionPage.getOrder();
	    		this.title = surveyDefinitionPage.getTitle();
	    		this.instructions  =  surveyDefinitionPage.getInstructions();
	    		//this.visibilityExpression = surveyDefinitionPage.getVisibilityExpression();
	    		this.randomizeQuestions = surveyDefinitionPage.getRandomizeQuestions();
	    		for (Question question: surveyDefinitionPage.getQuestions()){
	    			this.questionAnswers.add(new QuestionAnswer(question));
	    		}
	    
	    		
	    }
	    
	    /*
	    public void UpdateSettings(SurveyPage surveyPage) {
	    	this.order=surveyPage.getOrder();
    		this.title = surveyPage.getTitle();
    		this.instructions  =  surveyPage.getInstructions();
    		this.visibilityExpression = surveyPage.getVisibilityExpression();
	    	for (int i = 0;  i < this.questionAnswers.size() ;i++){
    			this.questionAnswers.get(i).UpdateSettings(surveyPage.questionAnswers.get(i));
    		}
	    } 
	    */
	    
		public Survey getSurvey() {
			return survey;
		}


		public void setSurvey(Survey survey) {
			this.survey = survey;
		}


		public Short getOrder() {
			return order;
		}
		public void setOrder(Short order) {
			this.order = order;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		
		
		
		public String getInstructions() {
			return instructions;
		}


		public void setInstructions(String instructions) {
			this.instructions = instructions;
		}


	
	  public Boolean getSatisfiesConditions(PageLogic pageLogic,String dateFormat) {
			try {
				Map map = new HashMap();
				map.put("page", this);
				String mvelExpression = computeMvelExpression(pageLogic,dateFormat);
				System.out.println("---------------------------");
				System.out.println(mvelExpression);
				System.out.println("---------------------------");
				Boolean satisfied = (Boolean) MVEL.eval(mvelExpression, map);
				if (satisfied !=null) {
					return satisfied;
				}
				return false;
			} catch (Exception e) {
				throw (new RuntimeException(e));
			}
		}
		
		
	  
	  
	  private String computeMvelExpressionForSignleValueQuestion(QuestionAnswer questionAnswer , LogicalCondition condition, String groupingOperator){
		  if (condition.getStringValues()!= null && condition.getStringValues().size() > 0) {
			  StringBuilder stringBuilder  = new StringBuilder();
			  stringBuilder.append("(");
			  for (String selectedValue : condition.getStringValues()) {
				  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].stringAnswerValue=='" + selectedValue +"' ||" );
			  }  
			  stringBuilder.setLength(stringBuilder.length() - 2);	//remove the extra ||
			  stringBuilder.append(") ");	
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString(); 
		  }
		  else { 
			  return "";
		  }
	  }

	  
	  private String computeMvelExpressionForMultipleValueQuestion(QuestionAnswer questionAnswer , LogicalCondition condition, String groupingOperator){
		  if (condition.getIntegerValues()!= null && condition.getIntegerValues().size() > 0 &&
			  questionAnswer.getIntegerAnswerValuesArray()!= null && questionAnswer.getIntegerAnswerValuesArray().length > 0) {
			  StringBuilder stringBuilder  = new StringBuilder();
			  stringBuilder.append("(");
			  for (Integer selectedValue : condition.getIntegerValues()) {
				  int idx = 0;
				  for (Integer answerValue : questionAnswer.getIntegerAnswerValuesArray()) {
					  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].integerAnswerValuesArray["+ idx + "]==" + selectedValue +" ||" );  
					  idx++;
				  }
			  }  
			  stringBuilder.setLength(stringBuilder.length() - 2);	//remove the extra ||
			  stringBuilder.append(") ");	
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();

		  }
		  else { 
			  return "";
		  } 
	  }
	  
	  private String computeMvelExpressionForIntegerAnswerQuestion(QuestionAnswer questionAnswer , LogicalCondition condition, String groupingOperator){
		  StringBuilder stringBuilder  = new StringBuilder();
		  if (condition.getLongMin() != null && condition.getLongMax() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].longAnswerValue >=" + condition.getLongMin());
			  stringBuilder.append(" && ");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].longAnswerValue <=" + condition.getLongMax());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString(); 
		  }	
		  if (condition.getLongMin() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].longAnswerValue >=" + condition.getLongMin());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  if (condition.getLongMax() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].longAnswerValue <=" + condition.getLongMax());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  return "";
	  }
	  
	  
	  private String computeMvelExpressionForDecimalAnswerQuestion(QuestionAnswer questionAnswer , LogicalCondition condition, String groupingOperator){
		  StringBuilder stringBuilder  = new StringBuilder();
		  if (condition.getBigDecimalMin() != null && condition.getBigDecimalMax() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].bigDecimalAnswerValue >=" + condition.getBigDecimalMin());
			  stringBuilder.append(" && ");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].bigDecimalAnswerValue <=" + condition.getBigDecimalMax());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString(); 
		  }	
		  if (condition.getBigDecimalMin() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].bigDecimalAnswerValue >=" + condition.getBigDecimalMin());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  if (condition.getBigDecimalMax() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].bigDecimalAnswerValue <=" + condition.getBigDecimalMax());
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  return "";
	  }
	  
	  private String computeMvelExpressionForDateAnswerQuestion(QuestionAnswer questionAnswer , LogicalCondition condition, String groupingOperator,String dateFormat){
		  StringBuilder stringBuilder  = new StringBuilder();
		  if (condition.getDateMin() != null && condition.getDateMax() != null) {
			  stringBuilder.append("(");
			  
			   
			  
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].dateAnswerValue >=" + "org.apache.commons.validator.routines.DateValidator.getInstance().validate('" + DateValidator.getInstance().format(condition.getDateMin(), dateFormat) +"','" + dateFormat +  "')"); 
			  stringBuilder.append(" && ");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].dateAnswerValue <=" + "org.apache.commons.validator.routines.DateValidator.getInstance().validate('" + DateValidator.getInstance().format(condition.getDateMax(), dateFormat) +"','" + dateFormat +  "')");
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString(); 
		  }	
		  if (condition.getDateMin() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].dateAnswerValue >=" + "org.apache.commons.validator.routines.DateValidator.getInstance().validate('" + DateValidator.getInstance().format(condition.getDateMin(), dateFormat) +"','" + dateFormat +  "')");
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  if (condition.getDateMax() != null) {
			  stringBuilder.append("(");
			  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].dateAnswerValue <=" + "org.apache.commons.validator.routines.DateValidator.getInstance().validate('" + DateValidator.getInstance().format(condition.getDateMax(), dateFormat) +"','" + dateFormat +  "')");
			  stringBuilder.append(")");
			  stringBuilder.append(groupingOperator);
			  return stringBuilder.toString();
		  }
		  return "";
	  }
	  
	  
	  
	  private String computeMvelExpression(PageLogic pageLogic,String dateFormat) {
		  try {
			 
			
			 
			  StringBuilder stringBuilder  = new StringBuilder();
			  stringBuilder.append("e=(");

			  String groupingOperator;
			  if (pageLogic.getGroupingOperator() ==GroupingOperator.AND) {groupingOperator="&&";}	else {groupingOperator="||";}; 

			  for (QuestionAnswer questionAnswer: this.questionAnswers) {
				  String key = questionAnswer.getOrder().toString();
				  if (pageLogic.getLogicalConditions().containsKey(key) && pageLogic.getLogicalConditions().get(key).getEnabled()) {

					  LogicalCondition condition  = pageLogic.getLogicalConditions().get(key);
					 
					  

					  switch (questionAnswer.getQuestion().getType()) {
					  case YES_NO_DROPDOWN:
						  stringBuilder.append("(");
						  stringBuilder.append("page.questionAnswers[" + (questionAnswer.getOrder() - 1) +"].booleanAnswerValue==" + condition.getBooleanValue());
						  stringBuilder.append(")");
						  stringBuilder.append(groupingOperator);
						  break;
					  case DATE_INPUT:
						  stringBuilder.append(computeMvelExpressionForDateAnswerQuestion(questionAnswer,condition,groupingOperator,dateFormat));	
						  break;
					  case INTEGER_INPUT:
						  stringBuilder.append(computeMvelExpressionForIntegerAnswerQuestion(questionAnswer,condition,groupingOperator));	
						  break;
					  case CURRENCY_INPUT:
						  stringBuilder.append(computeMvelExpressionForDecimalAnswerQuestion(questionAnswer,condition,groupingOperator));	
						  break;
					  case DECIMAL_INPUT:
						  stringBuilder.append(computeMvelExpressionForDecimalAnswerQuestion(questionAnswer,condition,groupingOperator));	
						  break;
					  case SINGLE_CHOICE_DROP_DOWN:
						  stringBuilder.append(computeMvelExpressionForSignleValueQuestion(questionAnswer,condition,groupingOperator));	
						  break;
					  case SINGLE_CHOICE_RADIO_BUTTONS:
						  stringBuilder.append(computeMvelExpressionForSignleValueQuestion(questionAnswer,condition,groupingOperator));
						  break;
					  case STAR_RATING:
						  stringBuilder.append(computeMvelExpressionForSignleValueQuestion(questionAnswer,condition,groupingOperator));
						  break;
					  case SMILEY_FACES_RATING:
						  stringBuilder.append(computeMvelExpressionForSignleValueQuestion(questionAnswer,condition,groupingOperator));
						  break;
					  case MULTIPLE_CHOICE_CHECKBOXES:
						  stringBuilder.append(computeMvelExpressionForMultipleValueQuestion(questionAnswer,condition,groupingOperator));
						  break;
							  
					  } 
				  }//if key match
			  } //for statement
			  stringBuilder.setLength(stringBuilder.length() - 2);	//remove the groupingOperator
			  stringBuilder.append(");");
			  
			  
			  if (stringBuilder.toString().trim().length() > 6) {
				  return "e=false;" + stringBuilder.toString() + "e==null? false:e;";
			  } 
			  else {
				//no valid conditions
				  return "false"; 
			  }
			  
			  
			  
		  }
		  catch (Exception e) {
			  throw (new RuntimeException(e));
		  }
	  }
	  
	  
	  
			
		
		public boolean getVisible() {
			return visible;
		}


		public void setVisible(boolean visible) {
			this.visible = visible;
		}

		

		public List<QuestionAnswer> getQuestionAnswers() {
			return this.questionAnswers;

		}

		

		public void setQuestionAnswers(List<QuestionAnswer> questionAnswers) {
			this.questionAnswers = questionAnswers;

		}

			

		public boolean getRandomizeQuestions() {
			return randomizeQuestions;
		}


		public void setRandomizeQuestions(boolean randomizeQuestions) {
			this.randomizeQuestions = randomizeQuestions;
		}


		@Override
		public String toString() {
			 return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
		

		
		
		
		//comparable interface
		@Override
		public int compareTo(SurveyPage that) {
			final int BEFORE = -1;
			final int AFTER = 1;
			if (that == null) {
				return BEFORE;
			}
			Comparable<Short> thisSurveyPage = this.getOrder();
			Comparable<Short> thatSurveyPage = that.getOrder();
			if(thisSurveyPage == null) {
				return AFTER;
			} else if(thatSurveyPage == null) {
				return BEFORE;
			} else {
				return thisSurveyPage.compareTo(that.getOrder());
			}
		}
		
		
		
		
		
}
