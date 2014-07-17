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
package com.jd.survey.service.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.settings.DepartmentDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.dao.interfaces.survey.SurveyEntryDAO;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Department;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.survey.SurveyEntry;



@Transactional(readOnly = true)
@Service("SecurityService")
public class SecurityService {
	private static final Log log = LogFactory.getLog(UserService.class);	
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private SurveyEntryDAO surveyEntryDAO;
	
	
	//checks if the internal user Belongs to the survey definition department
	public boolean userIsAuthorizedToManageSurvey(Long surveyDefinitionId, User user) {
		try { 
			Boolean isAuthorized = false; 
			SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(surveyDefinitionId);
			//if administator always authorized
			if (user.isAdmin()) {return true;}

			//Make sure the user belongs to the same department as the survey
			if (user.isSurveyAdmin()) {
				for (Department department : user.getDepartments()) {
					if (surveyDefinition.getDepartment().getId().equals(department.getId())) {
						isAuthorized = true;
						break;
					}
				}
			}
			return isAuthorized;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	//checks if the internal user Belongs to the survey definition department
		public boolean userIsAuthorizedToCreateSurvey(Long surveyDefinitionId, User user) {
			try { 
				Boolean isAuthorized = false; 
				SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(surveyDefinitionId);
				//if administator always authorized
				if (user.isAdmin()) {return true;}

				//Make sure the user belongs to the same department as the survey
				if (user.isSurveyAdmin()) {
					for (Department department : user.getDepartments()) {
						if (surveyDefinition.getDepartment().getId().equals(department.getId())) {
							isAuthorized = true;
							break;
						}
					}
				}
				
				//Make sure the user was authorized for the survey
				if (user.isSurveyParticipant()) {
					for (SurveyDefinition surveyDef : user.getSurveyDefinitions()) {
						if (surveyDef.getId().equals(surveyDefinition.getId())) {
							isAuthorized = true;
							break;
						}
					}
				}
				
				return isAuthorized;
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				throw (new RuntimeException(e));
			}
		}

	

	
	
	//checks if the internal user belongs to the department
	public boolean userBelongsToDepartment(Long departmentId, User user) {
		try { 
			Boolean isAuthorized = false;
			//if administator always authorized
			if (user.isAdmin()) {
				return true;
			}
			for (Department department : user.getDepartments()) {
				if (department.getId().equals(departmentId)) {
					isAuthorized = true;
					break;
				}
			}
			return isAuthorized;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	

	//checks if the external user is authorized to create a surveyEntry 
	public boolean userIsAuthorizedToCreateSurveyEntry(Long surveyDefinitionId, User user) {
		try { 
			Boolean isAuthorized = false; 
			//if administrator always authorized
			if (user.isAdmin()) {
				return true;
			}
			
			//if survey administrator check the user departments
			if (user.isSurveyAdmin()) {
				return userIsAuthorizedToManageSurvey(surveyDefinitionId, user);
			}
			
			SurveyDefinition surveyDefinition= surveyDefinitionDAO.findById(surveyDefinitionId);
			
			if (surveyDefinition.getIsPublic()) {
				return true;
			}
			
			//check that the external user account was authorized on the survey
			if (user.isSurveyParticipant()) {
				for (SurveyDefinition userSurveyDefinition : user.getSurveyDefinitions()) {
					if (userSurveyDefinition.getId().equals(surveyDefinitionId)) {
						isAuthorized = true;
						break;
					}
				}
			}
			return isAuthorized;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
		
	}

	
	//checks if the external user is authorized to edit a surveyEntry 
	public boolean userIsAuthorizedToEditSurveyEntry(Long surveyId, User user) {
		try { 
			Boolean isAuthorized = false; 
			//if administator always authorized
			if (user.isAdmin()) {
				return true;
			}
			SurveyEntry surveyEntry = surveyEntryDAO.get(surveyId);
			if (surveyEntry.getCreatedByLogin().equals(user.getLogin())) {
					isAuthorized = true;
			}
			return isAuthorized;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
		
	}
	
	
	
	
	

}