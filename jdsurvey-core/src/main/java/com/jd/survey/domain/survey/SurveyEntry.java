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

import com.jd.survey.domain.settings.Question;

public class SurveyEntry implements  Serializable, Comparable <SurveyEntry>{
	private static final long serialVersionUID = 8681210092236271317L;
	
	private  Long   surveyId;
	private  Long   surveyDefinitionId;
	private  String departmentName;
	private  String surveyName;
	private String createdByIpAddress;
	private String createdByLogin;
	private String createdByFirstName;
	private String createdByMiddleName;
	private String createdByLastName;
	private String createdByEmail;
	
	private Date creationDate;
	private Date lastUpdateDate;
	private Date submissionDate;
	private SurveyStatus status;
	public SurveyEntry() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	
	
	
	public SurveyEntry(Long surveyId, Long surveyDefinitionId,String createdByIpAddress,
			String departmentName, String surveyName, String createdByLogin,
			String createdByFirstName, String createdByMiddleName,
			String createdByLastName, String createdByEmail , Date creationDate,
			Date lastUpdateDate, Date submissionDate, SurveyStatus status) {
		super();
		this.surveyId = surveyId;
		this.surveyDefinitionId = surveyDefinitionId;
		this.createdByIpAddress = createdByIpAddress;
		this.departmentName = departmentName;
		this.surveyName = surveyName;
		this.createdByLogin = createdByLogin;
		this.createdByFirstName = createdByFirstName;
		this.createdByMiddleName = createdByMiddleName;
		this.createdByLastName = createdByLastName;
		this.createdByEmail = createdByEmail;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.submissionDate = submissionDate;
		this.status = status;
	}










	public Long getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(Long surveyId) {
		this.surveyId = surveyId;
	}
	public Long getSurveyDefinitionId() {
		return surveyDefinitionId;
	}
	public void setSurveyDefinitionId(Long surveyDefinitionId) {
		this.surveyDefinitionId = surveyDefinitionId;
	}
	public String getDepartmentName() {
		return departmentName;
	}
	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}
	public String getSurveyName() {
		return surveyName;
	}
	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}
	public String getCreatedByLogin() {
		return createdByLogin;
	}
	public void setCreatedByLogin(String createdByLogin) {
		this.createdByLogin = createdByLogin;
	}
	public String getCreatedByIpAddress() {
		return createdByIpAddress;
	}
	public void setCreatedByIpAddress(String createdByIpAddress) {
		this.createdByIpAddress = createdByIpAddress;
	}
	public String getCreatedByFirstName() {
		return createdByFirstName;
	}
	public void setCreatedByFirstName(String createdByFirstName) {
		this.createdByFirstName = createdByFirstName;
	}
	public String getCreatedByMiddleName() {
		return createdByMiddleName;
	}
	public void setCreatedByMiddleName(String createdByMiddleName) {
		this.createdByMiddleName = createdByMiddleName;
	}
	public String getCreatedByLastName() {
		return createdByLastName;
	}
	public void setCreatedByLastName(String createdByLastName) {
		this.createdByLastName = createdByLastName;
	}

	
	
	public String getCreatedByEmail() {
		return createdByEmail;
	}










	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
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
	
	
	public String getCreatedByFullName() {
		StringBuilder s = new StringBuilder("");
		if (this.createdByFirstName != null) {
			if (this.createdByFirstName != null) {s.append(createdByFirstName).append(" ").append(createdByLastName);} else {s.append(createdByFirstName);}
		}	
		else{
			if (this.createdByLastName != null) {s.append(createdByLastName);}
		} 
		return s.toString();
	}
	
	
	//comparable interface
		@Override
		public int compareTo(SurveyEntry that) {
			final int BEFORE = -1;
			final int AFTER = 1;
			if (that == null) {
				return BEFORE;
			}
			Comparable<Date> thisSurveyEntryCreationDate = this.getCreationDate();
			Comparable<Date> thatSurveyEntryCreationDate = that.getCreationDate();
			if(thisSurveyEntryCreationDate == null) {
				return AFTER;
			} else if(thatSurveyEntryCreationDate == null) {
				return BEFORE;
			} else {
				if  (thisSurveyEntryCreationDate.compareTo(that.getCreationDate())== 0) {
					return this.surveyId.compareTo(that.surveyId);
					} 
				else {
					return thisSurveyEntryCreationDate.compareTo(that.getCreationDate());
				}
			}
		}

	
}
