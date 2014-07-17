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
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotBlank;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jd.survey.util.SortedSetUpdater;


@Entity
@NamedQueries({
	@NamedQuery(name = "SurveyDefinitionPage.findAll", query = "select o from SurveyDefinitionPage o"),
	@NamedQuery(name = "SurveyDefinitionPage.findById", query = "select o from SurveyDefinitionPage o inner join o.surveyDefinition left join o.questions  where o.id = ?1"),
	@NamedQuery(name = "SurveyDefinitionPage.findByOrder", query = "select o from SurveyDefinitionPage o inner join o.surveyDefinition left join o.questions  where o.surveyDefinition.id = ?1 and o.order=?2"),
	@NamedQuery(name = "SurveyDefinitionPage.getCount", query = "select count(o) from SurveyDefinitionPage o"),
	@NamedQuery(name = "SurveyDefinitionPage.deleteBySurveyDefinitionId", query = "delete from SurveyDefinitionPage o where o.surveyDefinition.id=?1"),
	@NamedQuery(name = "SurveyDefinitionPage.findByTitle", query = "select o from SurveyDefinitionPage o where o.title= ?1")
	})
public class SurveyDefinitionPage  extends SortedSetUpdater<Question> 
								  implements Comparable <SurveyDefinitionPage>,Serializable, SortedSetUpdater.InrementableCompartator {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9034412277428599707L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

	@Version
    @Column(name = "version")
    private Integer version;


    @NotNull
    @Column(name = "PAGE_ORDER")
    private Short order;
    
    
    @NotBlank
    @Size(max = 250)
    @Column(length = 250, nullable= false)
    private String title;
    
    @NotNull
    @Size(max = 2000)
    @Column(length = 2000, nullable= false)
    private String instructions;

    
    @NotNull
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "SURVEY_DEFINITION_ID")
    private SurveyDefinition surveyDefinition;
	
    //@IndexColumn(name="PAGE_ORDER")
    @OneToMany(cascade = CascadeType.REMOVE,fetch=FetchType.LAZY,mappedBy="page")
    //@OneToMany(orphanRemoval=true,fetch=FetchType.EAGER,mappedBy="page")
    @Sort(type = SortType.NATURAL)
    private SortedSet<Question> questions = new TreeSet<Question>();

    
    @Lob
	private byte[] pageLogicJSON;
	
    @Transient
	private PageLogic pageLogic =new PageLogic();
	
    
    
 
    
    
    
    
    private Boolean randomizeQuestions = false;
    
    
    
    public SurveyDefinitionPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SurveyDefinitionPage(SurveyDefinition surveyDefinition) {
		super();
		this.surveyDefinition = surveyDefinition;
		this.order = (short) (surveyDefinition.getPages().size() + 1 );
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

	
	
	public SurveyDefinition getSurveyDefinition() {
		return surveyDefinition;
	}

	public void setSurveyDefinition(SurveyDefinition surveyDefinition) {
		this.surveyDefinition = surveyDefinition;
	}


	public SortedSet<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(SortedSet<Question> questions) {
		this.questions = questions;
	}

	

	
	public String getTwoDigitPageOrder() {
		if (this.order <10) {return "0" + this.order.toString() ;}else{return this.order.toString();} 
	}
	
	
	
	
	
	
	public Boolean getRandomizeQuestions() {
		return randomizeQuestions;
	}

	public void setRandomizeQuestions(Boolean randomizeQuestions) {
		this.randomizeQuestions = randomizeQuestions;
	}

	
	public String toString() {
		return this.title;  
	}

	
	
	//Comparable interface
    @Override
	public int compareTo(SurveyDefinitionPage that) {

    	final int BEFORE = -1;
		final int AFTER = 1;
		if (that == null) {
			return BEFORE;
		}
		Comparable<Short> thisSurveyDefinitionPage = this.getOrder();
		Comparable<Short> thatSurveyDefinitionPage = that.getOrder();
		if(thisSurveyDefinitionPage == null) {
			return AFTER;
		} else if(thatSurveyDefinitionPage == null) {
			return BEFORE;
		} else {
			return thisSurveyDefinitionPage.compareTo(that.getOrder());
		}
    
    }
	
  

    
    
    
    
    
    
    
    
    
    
    public byte[] getPageLogicJSON() {
		return pageLogicJSON;
	}

	public void setPageLogicJSON(byte[] pageLogicJSON) {
		this.pageLogicJSON = pageLogicJSON;
		//this.loadFromJson();
	}

	public PageLogic getPageLogic() {
		return pageLogic;
	}

	public void setPageLogic(PageLogic pageLogic) {
		this.pageLogic = pageLogic;
		//this.updateJson();
	}
	
	
	/**
	 * Checks that at least one logical condition was enabled
	 * @return
	 */
	public boolean hasLogic() {
		if (this.pageLogic.getLogicalConditions()!=null &&		
			this.pageLogic.getLogicalConditions().size() > 0) {
			for (String key: pageLogic.getLogicalConditions().keySet()) {
				if (pageLogic.getLogicalConditions().get(key).getEnabled()) {return true;}
			}
		}
		return false;
	}
	

	public void updateJson() {
		try {
			Type pageLogic = new TypeToken<PageLogic>() {}.getType();
			Gson gson = new Gson();
			String json= gson.toJson(this.pageLogic,pageLogic);
			this.pageLogicJSON = json.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw( new RuntimeException(e));
		}

	}
	
	
    public void loadFromJson() {
		try {
			if (this.pageLogicJSON!=null && this.pageLogicJSON.length > 0) {
				Type pageLogic = new TypeToken<PageLogic>() {}.getType();
				Gson gson = new Gson();
				this.pageLogic =  gson.fromJson(new String(this.pageLogicJSON, "UTF8") ,pageLogic);
			}
				
		} catch (UnsupportedEncodingException e) {
			throw( new RuntimeException(e));
		}

	}
	
	
	
}
