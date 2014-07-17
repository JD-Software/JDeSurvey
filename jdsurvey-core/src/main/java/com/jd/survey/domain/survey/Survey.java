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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.SurveyDefinition;




@Entity
@NamedQueries({
	@NamedQuery(name = "Survey.findAll", query = "select o from Survey o"),
	@NamedQuery(name = "Survey.findById", query = "select o from Survey o where o.id = ?1"),
	@NamedQuery(name = "Survey.getCount", query = "select count(o) from Survey o"),
	@NamedQuery(name = "Survey.findAllByTypeId", query = "select o from Survey o where o.typeId=?1 order by submissionDate desc, creationDate desc"),
	@NamedQuery(name = "Survey.findAllIncompleteByTypeId", query = "select o from Survey o where o.typeId=?1 and (status='I' or status='R')  order by submissionDate desc, creationDate desc"),
	@NamedQuery(name = "Survey.findAllSubmittedByTypeId", query = "select o from Survey o where o.typeId=?1 and status='S' order by submissionDate desc, creationDate desc"),
	@NamedQuery(name = "Survey.findAllDeletedByTypeId", query = "select o from Survey o where o.typeId=?1 and status='D' order by submissionDate desc, creationDate desc"),
	@NamedQuery(name = "Survey.findUserEntriesByTypeIdAndLogin", query = "select o from Survey o where o.typeId=?1 and o.login=?2 and status<>'D' order by submissionDate desc, creationDate desc"),
	@NamedQuery(name = "Survey.findUserEntriesByTypeIdAndIpAddress", query = "select o from Survey o where o.typeId=?1 and o.ipAddress=?2 and status<>'D' order by submissionDate desc, creationDate desc")
	})
public class Survey implements Serializable{
	
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 4047038776942375913L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
	
	@Version
    @Column(name = "version")
    private Integer version;
	
	@Column(name = "SURVEY_DEFINITION_ID")
	Long typeId;
	

	@Column(length = 100, nullable= true)
	private String login;
	
	
	@Column(length = 75, nullable= true)
	private String firstName;

	@Column(length = 75,nullable= true)
	private String middleName;

	@Column(length = 75, nullable= true)
	private String lastName;

	
	@Column(length = 100, nullable= true)
	private String email;
	
	
	
	
	@Column(length = 45, nullable= true)
	private String ipAddress;
	
	
	
	@NotNull
	@Size(max = 75)
    private String typeName;
	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= false)
	private Date creationDate;
	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= true)
	private Date lastUpdateDate;
				
	
	@DateTimeFormat(pattern="#{messages['date_format']}")
	@Column(nullable= true)
	private Date submissionDate;
	
	
	@Enumerated(EnumType.STRING)
    private SurveyStatus status = SurveyStatus.I;

    public Survey() {
		super();
		this.creationDate= new Date();
		this.lastUpdateDate= new Date();
		this.status= SurveyStatus.I;
		// TODO Auto-generated constructor stub
	}

    public Survey(SurveyDefinition surveyDefinition, User user, String ipAddress) {
		super();
		this.typeId = surveyDefinition.getId();
		this.typeName = surveyDefinition.getName();
		this.creationDate= new Date();
		this.lastUpdateDate= new Date();
		this.login= user.getLogin();
		this.firstName= user.getFirstName();
		this.middleName = user.getMiddleName();
		this.lastName = user.getLastName();
		this.email= user.getEmail();
		this.ipAddress = ipAddress;
		this.status= SurveyStatus.I;
	}
    
    public Survey(SurveyDefinition surveyDefinition,String ipAddress) {
		super();
		this.typeId = surveyDefinition.getId();
		this.typeName = surveyDefinition.getName();
		this.creationDate= new Date();
		this.lastUpdateDate= new Date();
		this.ipAddress = ipAddress;
		this.status= SurveyStatus.I;
	}
    
           
	@Override
	public String toString() {
		 return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
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

	
	
	
	

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	
	
	
	
	
	
	
	
	
	
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	
	
	
	
	
	
	
	
	
	
	
	public Date getSubmissionDate() {
		return submissionDate;
	}

	public void setSubmissionDate(Date submissionDate) {
		this.submissionDate = submissionDate;
	}

	public SurveyStatus getStatus() {
		return status;
	}

	public void setStatus(SurveyStatus status) {
		this.status = status;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	

}
