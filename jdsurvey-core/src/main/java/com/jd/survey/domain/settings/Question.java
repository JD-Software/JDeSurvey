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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.jd.survey.util.SortedSetUpdater;


import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@NamedQueries({
	@NamedQuery(name = "Question.findAll", query = "select o from Question o"),
	@NamedQuery(name = "Question.findById", query = "select o from Question o where o.id = ?1"),
	@NamedQuery(name = "Question.findByOrder", query = "select q from Question q inner join q.page p inner join p.surveyDefinition sd  where sd.id = ?1 and p.order = ?2 and q.order=?3"),
	@NamedQuery(name = "Question.getCount", query = "select count(o) from Question o"),
	@NamedQuery(name = "Question.deleteBySurveyDefinitionPageId", query = "delete from Question o where o.page.id=?1")
})
public class Question extends SortedSetUpdater<QuestionOption>
implements Comparable <Question>,  Serializable, SortedSetUpdater.InrementableCompartator	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 693573304272858465L;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Version
	@Column(name = "version")
	private Integer version;

	@NotNull
	@Column(name = "QUESTION_ORDER")
	private Short order;

	@NotNull
	@NotEmpty
	@Size(max = 2000)
	@Column(length = 2000, nullable= false)
	private String questionText;
	
	// Used to stored question text piped content 
	@Transient
	private String questionLabel;

	@Size(max = 750)
	@Column(length = 750, nullable= true)
	private String multimediaLink;
	
	private Integer height;
	private Integer width;
	
	private Boolean allowOtherTextBox;

	private Boolean required;
	private Boolean visible;

	private Integer integerMinimum;
	private Integer integerMaximum;

	private BigDecimal decimalMinimum;
	private BigDecimal decimalMaximum;
	
	private Boolean randomizeOptions = false;

	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	private Date dateMinimum;

	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	private Date dateMaximum;


	@Size(max = 2000)
	@Column(length = 2000, nullable= true)
	private String tip;

	
	@Size(max = 250)
	@Column(length = 250, nullable= true)
	private String regularExpression;
	

	private Long dataSetId;	
	
	
	@Enumerated(EnumType.STRING)
    private QuestionType type = QuestionType.SHORT_TEXT_INPUT;
	
	@Enumerated(EnumType.STRING)
    private QuestionDirection direction = QuestionDirection.VERTICAL;	
	
 
   
    
    

	@NotNull
	@ManyToOne
	@JoinColumn(name = "SURVEY_DEFINITION_PAGE_ID")
	private SurveyDefinitionPage page;

	
	@OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
	//@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
	@Sort(type = SortType.NATURAL)
	private SortedSet<QuestionOption> options = new TreeSet<QuestionOption>();


	@OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
	//@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
	@Sort(type = SortType.NATURAL)
	private SortedSet<QuestionRowLabel> rowLabels = new TreeSet<QuestionRowLabel>();
	
	
	@OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.EAGER, mappedBy="question")
	//@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER, mappedBy="question")
	@Sort(type = SortType.NATURAL)
	private SortedSet<QuestionColumnLabel> columnLabels = new TreeSet<QuestionColumnLabel>();
	
	
	
	


    @Transient
    @Valid
    private List<QuestionOption> optionsList = new ArrayList<QuestionOption>();
    
    /**
     * Spring is not using the setter on the optionsList field when binding the optionsList on the post. This field is a  work arround below is a fix to that!!!
     * Not pretty and needs to be revisited to see if there is a better way to do this
     */
    @Transient
    @Valid
    private List<QuestionOption> optionsList2 = new ArrayList<QuestionOption>();
    
    @Transient
    @Valid
    private List<QuestionRowLabel> rowLabelsList = new ArrayList<QuestionRowLabel>();
    
    @Transient
    @Valid
    private List<QuestionColumnLabel> columnLabelsList = new ArrayList<QuestionColumnLabel>();

    

    @Transient
    private Set<DataSetItem> dataSetItems;


	public Question() {
		super();
		this.visible=true;
		// TODO Auto-generated constructor stub
	}


	public Question(SurveyDefinitionPage page) {
		super();
		this.visible=true;
		this.page = page;
		this.order = (short) (page.getQuestions().size() + 1);
		
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
	public Short getOrder() {
		return order;
	}
	public void setOrder(Short order) {
		this.order = order;
	}


	public String getQuestionText() {
		if (questionText== null) {return null;} 
		else {return questionText.trim();}
	}
	public void setQuestionText(String questionText) {
		if (questionText== null) {this.questionText= null;} 
		else
		{this.questionText = questionText.trim();}
	}
	
	public String getQuestionLabel() {
		if (questionLabel!= null) {
			return questionLabel;
		}
		else{
			return questionText;	
		}
	}


	public void setQuestionLabel(String questionLabel) {
		this.questionLabel = questionLabel;
	}


	public Boolean getAllowOtherTextBox() {
		return allowOtherTextBox;
	}
	public void setAllowOtherTextBox(Boolean allowOtherTextBox) {
		this.allowOtherTextBox = allowOtherTextBox;
	}
	public Boolean getVisible() {
		return visible;
	}
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public Long getDataSetId() {
		return dataSetId;
	}


	public void setDataSetId(Long dataSetId) {
		this.dataSetId = dataSetId;
	}


	public Boolean getRequired() {
		return required;
	}


	public void setRequired(Boolean required) {
		this.required = required;
	}



	public Integer getIntegerMinimum() {
		return integerMinimum;
	}


	public void setIntegerMinimum(Integer integerMinimum) {
		this.integerMinimum = integerMinimum;
	}


	public Integer getIntegerMaximum() {
		return integerMaximum;
	}


	public void setIntegerMaximum(Integer integerMaximum) {
		this.integerMaximum = integerMaximum;
	}



	public BigDecimal getDecimalMinimum() {
		return decimalMinimum;
	}


	public void setDecimalMinimum(BigDecimal decimalMinimum) {
		this.decimalMinimum = decimalMinimum;
	}


	public BigDecimal getDecimalMaximum() {
		return decimalMaximum;
	}


	public void setDecimalMaximum(BigDecimal decimalMaximum) {
		this.decimalMaximum = decimalMaximum;
	}


	public Date getDateMinimum() {
		return dateMinimum;
	}


	public void setDateMinimum(Date dateMinimum) {
		this.dateMinimum = dateMinimum;
	}


	public Date getDateMaximum() {
		return dateMaximum;
	}


	public void setDateMaximum(Date dateMaximum) {
		this.dateMaximum = dateMaximum;
	}


	public String getTip() {
		if (tip == null) {return null;}
		else{return tip.trim();}
			
		}
		
		public void setTip(String tip) {
		if (tip == null) {this.tip= null;}
		else {this.tip = tip.trim();}
		}

	
	

	public String getMultimediaLink() {
		return multimediaLink;
	}


	public void setMultimediaLink(String multimediaLink) {
		//multimediaLink = multimediaLink.replaceAll("width=\"([^\"]*)\"", "");
		//multimediaLink = multimediaLink.replaceAll("width='([^']*)'", "");
		
		//multimediaLink = multimediaLink.replaceAll("height=\"([^\"]*)\"", "");
		//multimediaLink = multimediaLink.replaceAll("height='([^']*)'", "");
		
		this.multimediaLink = multimediaLink;
	}


	public Integer getHeight() {
		return height;
	}


	public void setHeight(Integer height) {
		this.height = height;
	}


	public Integer getWidth() {
		return width;
	}


	public void setWidth(Integer width) {
		this.width = width;
	}


	
	public QuestionType getType() {
		return type;
	}
	public void setType(QuestionType type) {
		this.type = type;
	}

	public String getTwoDigitPageOrder() {
		if (this.order <10) {return "0" + this.order.toString() ;}else{return this.order.toString();} 
	}
	

	public QuestionDirection getDirection() {
		return direction;
	}


	public void setDirection(QuestionDirection direction) {
		this.direction = direction;
	}


	public SurveyDefinitionPage getPage() {
		return page;
	}
	public void setPage(SurveyDefinitionPage page) {
		this.page = page;
	}


	public SortedSet<QuestionOption> getOptions() {
		return options;
	}
	public void setOptions(SortedSet<QuestionOption> options) {
		this.options = options;
		if (options !=null){
		short i= 1;
		Iterator<QuestionOption> it;
		it = options.iterator();
		while (it.hasNext()) {
			QuestionOption questionOption  = it.next();
			questionOption.setOrder(i);
			optionsList.add(questionOption);
			optionsList2.add(questionOption);
			i++;
		}
		}
		
	}


	public SortedSet<QuestionRowLabel> getRowLabels() {
		return rowLabels;
	}
	public void setRowLabels(SortedSet<QuestionRowLabel> rowLabels) {
		this.rowLabels = rowLabels;
		if (rowLabels !=null){
		short i= 1;
		Iterator<QuestionRowLabel> it;
		it = rowLabels.iterator();
		while (it.hasNext()) {
			QuestionRowLabel questionRowLabel  = it.next();
			questionRowLabel.setOrder(i);
			rowLabelsList.add(questionRowLabel);
			i++;
		}
		}
		
	}

	public SortedSet<QuestionColumnLabel> getColumnLabels() {
		return columnLabels;
	}
	public void setColumnLabels(SortedSet<QuestionColumnLabel> columnLabels) {
		this.columnLabels = columnLabels;
		if (columnLabels !=null){
		short i= 1;
		Iterator<QuestionColumnLabel> it;
		it = columnLabels.iterator();
		while (it.hasNext()) {
			QuestionColumnLabel questionColumnLabel  = it.next();
			questionColumnLabel.setOrder(i);
			columnLabelsList.add(questionColumnLabel);
			i++;
		}
		}
		
	}
	
	
	public List<QuestionOption> getOptionsList() {
		if (optionsList != null && optionsList.size()>0) {
			return optionsList;	
		} else {
			return new ArrayList<QuestionOption>(this.options);
		}
	}
	public void setOptionsList(List<QuestionOption> optionsList) {
		this.optionsList = optionsList;
	}
	
	public List<QuestionRowLabel> getRowLabelsList() {
		return rowLabelsList;
	}
	public void setRowLabelsList(List<QuestionRowLabel> rowLabelsList) {
		this.rowLabelsList = rowLabelsList;
	}

	public List<QuestionColumnLabel> getColumnLabelsList() {
		return columnLabelsList;
	}
	public void setColumnLabelsList(List<QuestionColumnLabel> columnLabelsList) {
		this.columnLabelsList = columnLabelsList;
	}

	
	
	

	


	public Set<DataSetItem> getDataSetItems() {
		return dataSetItems;
	}


	public void setDataSetItems(Set<DataSetItem> dataSetItems) {
		this.dataSetItems = dataSetItems;
	}


	public boolean getSuportsOptions() {
		if (this.type == QuestionType.SINGLE_CHOICE_DROP_DOWN || 
			this.type == QuestionType.MULTIPLE_CHOICE_CHECKBOXES ||
			this.type == QuestionType.STAR_RATING ||
			this.type == QuestionType.SMILEY_FACES_RATING ||
			this.type == QuestionType.SINGLE_CHOICE_RADIO_BUTTONS){
			return true;
		}
		else{ 
			return false;
		}
	}


	public String toString() {
		return "question:" + this.order + " " + this.questionText;
	}




	

	
	
	
	


	//comparable interface
	@Override
	public int compareTo(Question that) {
		final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Short> thisQuestionePage = this.getOrder();
		Comparable<Short> thatQuestionPage = that.getOrder();
		if(thisQuestionePage == null) {
			return AFTER;
		} else if(thatQuestionPage == null) {
			return BEFORE;
		} else {
			return thisQuestionePage.compareTo(that.getOrder());
		}
	}


	

	public String getRegularExpression() {
		return regularExpression;
	}


	public void setRegularExpression(String regularExpression) {
		this.regularExpression = regularExpression;
	}


	public Boolean getRandomizeOptions() {
		return randomizeOptions;
	}


	public void setRandomizeOptions(Boolean randomizeOptions) {
		this.randomizeOptions = randomizeOptions;
	}


	public List<QuestionOption> getOptionsList2() {
		return optionsList2;
	}


	public void setOptionsList2(List<QuestionOption> optionsList2) {
		this.optionsList2 = optionsList2;
	}


}
