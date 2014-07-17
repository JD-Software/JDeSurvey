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

import java.text.DecimalFormat;

public class SurveyStatistic {
	private  Long   surveyDefinitionId;
	private  String departmentName;
	private  String surveyName;
	private  Long   icompletedCount;
	private  Long   submittedCount;
	private  Long   deletedCount;
	private  Long   totalCount;
	
	public SurveyStatistic() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SurveyStatistic(Long surveyDefinitionId,
						   String surveyName,
						   String departmentName,
						   Long icompletedCount, 
						   Long submittedCount,
						   Long deletedCount, 
						   Long totalCount) {
		super();
		this.departmentName = departmentName;
		this.surveyDefinitionId = surveyDefinitionId;
		this.surveyName = surveyName;
		this.icompletedCount = icompletedCount;
		this.submittedCount = submittedCount;
		this.deletedCount = deletedCount;
		this.totalCount = totalCount;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Long getSurveyDefinitionId() {
		return surveyDefinitionId;
	}

	public void setSurveyDefinitionId(Long surveyDefinitionId) {
		this.surveyDefinitionId = surveyDefinitionId;
	}

	public String getSurveyName() {
		return surveyName;
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public Long getIcompletedCount() {
		return icompletedCount;
	}

	public void setIcompletedCount(Long icompletedCount) {
		this.icompletedCount = icompletedCount;
	}

	public Long getSubmittedCount() {
		return submittedCount;
	}

	public void setSubmittedCount(Long submittedCount) {
		this.submittedCount = submittedCount;
	}

	public Long getDeletedCount() {
		return deletedCount;
	}

	public void setDeletedCount(Long deletedCount) {
		this.deletedCount = deletedCount;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
	
	
	public double getSubmittedPercentage() {
		double percentage = 0;
		if (totalCount != 0) {percentage = ((double)submittedCount/(double)totalCount);}
		return Double.valueOf(new DecimalFormat("#.##").format(percentage));
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
