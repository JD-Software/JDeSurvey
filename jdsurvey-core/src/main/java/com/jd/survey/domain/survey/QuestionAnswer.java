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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.jd.survey.domain.settings.Question;



public class QuestionAnswer implements Comparable <QuestionAnswer>,  Serializable{
	private static final long serialVersionUID = 4516421821141384852L;
	private static final int MAX_COLUMNS = 10;
	private static final int MAX_ROWS = 25;
	private static final int MAX_OPTIONS = 250;
	
	
	
    private Question question;
    private Long longAnswerValue;
	private String stringAnswerValue;
	private String otherStringAnswerValue;
	
 	private BigDecimal bigDecimalAnswerValue; 
    private Boolean booleanAnswerValue;
    private Date dateAnswerValue;
    
    private Integer[] integerAnswerValuesArray = new Integer[MAX_OPTIONS];
    
    private Long[][] longAnswerValuesMatrix = new Long[MAX_ROWS][MAX_COLUMNS];
	private String[][] stringAnswerValuesMatrix = new String[MAX_ROWS][MAX_COLUMNS];
 	private BigDecimal[][] bigDecimalAnswerValuesMatrix = new BigDecimal[MAX_ROWS][MAX_COLUMNS]; 
    private Boolean[][] booleanAnswerValuesMatrix= new Boolean[MAX_ROWS][MAX_COLUMNS];
    private Date[][] dateAnswerValuesMatrix= new Date[MAX_ROWS][MAX_COLUMNS];
    
    private  SurveyDocument surveyDocument;
    private  boolean  documentAlreadyUploded = false;

	public QuestionAnswer() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public QuestionAnswer(Question question) {
		super();
		this.question=question;
	}
	
	
	public Long getLongAnswerValue() {
		return longAnswerValue;
	}
	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public void setLongAnswerValue(Long longAnswerValue) {
		this.longAnswerValue = longAnswerValue;
	}
	public String getStringAnswerValue() {
		return stringAnswerValue;
	}
	public void setStringAnswerValue(String stringAnswerValue) {
		this.stringAnswerValue = stringAnswerValue;
	}
	
	
	
	public String getOtherStringAnswerValue() {
		return otherStringAnswerValue;
	}

	public void setOtherStringAnswerValue(String otherStringAnswerValue) {
		this.otherStringAnswerValue = otherStringAnswerValue;
	}

	public Boolean getBooleanAnswerValue() {
		return booleanAnswerValue;
	}
	public void setBooleanAnswerValue(Boolean booleanAnswerValue) {
		this.booleanAnswerValue = booleanAnswerValue;
	}
	public Date getDateAnswerValue() {
		return dateAnswerValue;
	}
	public void setDateAnswerValue(Date dateAnswerValue) {
		this.dateAnswerValue = dateAnswerValue;
	}
	
	public BigDecimal getBigDecimalAnswerValue() {
		return bigDecimalAnswerValue;
	}

	public void setBigDecimalAnswerValue(BigDecimal bigDecimalAnswerValue) {
		this.bigDecimalAnswerValue = bigDecimalAnswerValue;
	}
	
	

	
	
	
	
	

	public Integer[] getIntegerAnswerValuesArray() {
		return integerAnswerValuesArray;
	}

	public void setIntegerAnswerValuesArray(Integer[] integerAnswerValuesArray) {
		this.integerAnswerValuesArray = integerAnswerValuesArray;
	}

	public Long[][] getLongAnswerValuesMatrix() {
		return longAnswerValuesMatrix;
	}
	public void setLongAnswerValuesMatrix(Long[][] longAnswerValuesMatrix) {
		this.longAnswerValuesMatrix = longAnswerValuesMatrix;
	}
	public String[][] getStringAnswerValuesMatrix() {
		return stringAnswerValuesMatrix;
	}
	public void setStringAnswerValuesMatrix(String[][] stringAnswerValuesMatrix) {
		this.stringAnswerValuesMatrix = stringAnswerValuesMatrix;
	}
	public BigDecimal[][] getBigDecimalAnswerValuesMatrix() {
		return bigDecimalAnswerValuesMatrix;
	}
	public void setBigDecimalAnswerValuesMatrix(
			BigDecimal[][] bigDecimalAnswerValuesMatrix) {
		this.bigDecimalAnswerValuesMatrix = bigDecimalAnswerValuesMatrix;
	}
	public Boolean[][] getBooleanAnswerValuesMatrix() {
		return booleanAnswerValuesMatrix;
	}
	public void setBooleanAnswerValuesMatrix(Boolean[][] booleanAnswerValuesMatrix) {
		this.booleanAnswerValuesMatrix = booleanAnswerValuesMatrix;
	}
	public Date[][] getDateAnswerValuesMatrix() {
		return dateAnswerValuesMatrix;
	}
	public void setDateAnswerValuesMatrix(Date[][] dateAnswerValuesMatrix) {
		this.dateAnswerValuesMatrix = dateAnswerValuesMatrix;
	}
	
	
	
	
	
	
	
	

	public boolean getDocumentAlreadyUploded() {
		return documentAlreadyUploded;
	}

	public void setDocumentAlreadyUploded(boolean documentAlreadyUploded) {
		this.documentAlreadyUploded = documentAlreadyUploded;
	}

	public SurveyDocument getSurveyDocument() {
		return surveyDocument;
	}

	public void setSurveyDocument(SurveyDocument surveyDocument) {
		this.surveyDocument = surveyDocument;
	}

	public Short getOrder() {
		return question.getOrder();
	}
	
	
	
	
	
	

	@Override
	public int compareTo(QuestionAnswer that) {
		final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Short> thisQuestionAnswer = this.getOrder();
		Comparable<Short> thatQuestionAnswer = that.getOrder();
		if(thisQuestionAnswer == null) {
			return AFTER;
		} else if(thatQuestionAnswer == null) {
			return BEFORE;
		} else {
			return thisQuestionAnswer.compareTo(that.getOrder());
		}
	}


	@Override
		public String toString() {
		 return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}
	
	  
}
