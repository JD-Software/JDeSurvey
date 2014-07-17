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
package com.jd.survey.domain.settings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.jd.survey.util.SortedSetUpdater;

@Entity
@NamedQueries({
	@NamedQuery(name = "QuestionColumnLabel.findAll", query = "select o from QuestionColumnLabel o"),
	@NamedQuery(name = "QuestionColumnLabel.findById", query = "select o from QuestionColumnLabel o where o.id = ?1"),
	@NamedQuery(name = "QuestionColumnLabel.getCount", query = "select count(o) from QuestionColumnLabel o"),
	@NamedQuery(name = "QuestionColumnLabel.deleteByQuestionId", query = "delete from QuestionColumnLabel o where o.question.id=?1"),
	@NamedQuery(name = "QuestionColumnLabel.findByQuestionId", query = "select o from QuestionColumnLabel o where o.question.id=?1")
	})
public class QuestionColumnLabel implements Comparable <QuestionColumnLabel>,  Serializable, SortedSetUpdater.InrementableCompartator{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5689804369411211023L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;
	
	
    @NotNull
    @NotEmpty
    @Size(max = 75)
    @Column(name = "LABEL",length = 75, nullable= false)
    private String label;

    @NotNull
    @Column(name = "COLUMN_LABEL_ORDER")
    private Short order;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
    
    
    
    
    
    
	public QuestionColumnLabel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public QuestionColumnLabel(Question question) {
		super();
		this.question = question;
		this.order = (short) (question.getOptions().size() +1);
	
	}

	public QuestionColumnLabel(Question question, Short order) {
		super();
		this.question = question;
		this.order = order;
	}
	
	public QuestionColumnLabel(Question question, Short order, String label) {
		super();
		this.question = question;
		this.order = order;
		this.label= label;
	}

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public Short getOrder() {
		return order;
	}
	public void setOrder(Short order) {
		this.order = order;
	}
	
	
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	
	public String toString() {
        return this.label;
    }
	
	
	
	
	//comparable interface
	@Override
	public int compareTo(QuestionColumnLabel that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Short> thisQuestionColumnLabelPage = this.getOrder();
		Comparable<Short> thatQuestionColumnLabelPage = that.getOrder();
		if(thisQuestionColumnLabelPage == null) {
			return AFTER;
		} else if(thatQuestionColumnLabelPage == null) {
			return BEFORE;
		} else {
			return thisQuestionColumnLabelPage.compareTo(that.getOrder());
		}
	}


	
    
   
	
}
