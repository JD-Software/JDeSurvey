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

import com.jd.survey.domain.settings.QuestionColumnLabel;



public class QuestionAnswerColumnLabel  implements Comparable <QuestionAnswerColumnLabel> ,  Serializable{

	private static final long serialVersionUID = -1476359900884520187L;
		private Long id;
	  	private Short order;
	  	private String label;
	  	
	  	public QuestionAnswerColumnLabel() {
			super();
			// TODO Auto-generated constructor stub
		}

	 	public QuestionAnswerColumnLabel(QuestionColumnLabel questionColumnLabel) {
			super();
			this.id=questionColumnLabel.getId();
			this.order=questionColumnLabel.getOrder();
			this.label=questionColumnLabel.getLabel();
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

		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		

		//comparable
		@Override
		public int compareTo(QuestionAnswerColumnLabel that) {
			final int BEFORE = -1;
			final int AFTER = 1;
			if (that == null) {
				return BEFORE;
			}
			Comparable<Short> thisQuestionAnswerColumnLabel = this.getOrder();
			Comparable<Short> thatQuestionAnswerColumnLabel = that.getOrder();
			if(thisQuestionAnswerColumnLabel == null) {
				return AFTER;
			} else if(thatQuestionAnswerColumnLabel == null) {
				return BEFORE;
			} else {
				return thisQuestionAnswerColumnLabel.compareTo(that.getOrder());
			}
		}
		
		


		@Override
		public String toString() {
			 return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
			}
}
