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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.jd.survey.domain.settings.QuestionOption;


public class QuestionAnswerOption  implements Comparable <QuestionAnswerOption> ,  Serializable{


		private static final long serialVersionUID = 5115286246064759082L;
		private Long id;
	  	private Short order;
	  	private String value;
	  	private String text;
	  	
	      	
	  	
	  	
	  	
	  	
	  	public QuestionAnswerOption() {
			super();
			// TODO Auto-generated constructor stub
		}

	 	public QuestionAnswerOption(QuestionOption questionOption) {
			super();
			this.id=questionOption.getId();
			this.order=questionOption.getOrder();
			this.value=questionOption.getValue();
			this.text=questionOption.getText();
			
		}


		public Long getId() {
			return id;
		}




		public void setId(Long id) {
			this.id = id;
		}




		public Short getOrder() {
			return order;
		}




		public void setOrder(Short order) {
			this.order = order;
		}




		public String getValue() {
			return value;
		}




		public void setValue(String value) {
			this.value = value;
		}




		public String getText() {
			return text;
		}




		public void setText(String text) {
			this.text = text;
		}


		
		
		
		
		
		
		
		
		
		
		
		//comparable
		@Override
		public int compareTo(QuestionAnswerOption that) {
			final int BEFORE = -1;
			final int AFTER = 1;
			if (that == null) {
				return BEFORE;
			}
			Comparable<Short> thisQuestionAnswerOption = this.getOrder();
			Comparable<Short> thatQuestionAnswerOption = that.getOrder();
			if(thisQuestionAnswerOption == null) {
				return AFTER;
			} else if(thatQuestionAnswerOption == null) {
				return BEFORE;
			} else {
				return thisQuestionAnswerOption.compareTo(that.getOrder());
			}
		}
		
		


		@Override
		public String toString() {
			 return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
			}
}
