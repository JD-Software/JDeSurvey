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
package com.jd.survey.dao.interfaces.settings;

import java.util.Set;

import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

import com.jd.survey.domain.settings.SurveyDefinition;

/**
 */
public interface SurveyDefinitionDAO extends JpaDao<SurveyDefinition> {
	
	public Set<SurveyDefinition> findAllInternal() throws DataAccessException;
	public Set<SurveyDefinition> findAllInternal(int startResult, int maxRows) throws DataAccessException;
	public Set<SurveyDefinition> findAllInternal(String login) throws DataAccessException;
	public Set<SurveyDefinition> findAllInternal(String login, int startResult, int maxRows) throws DataAccessException;
	

	public Set<SurveyDefinition> findAllCompletedInternal() throws DataAccessException;
	public Set<SurveyDefinition> findAllCompletedInternal(int startResult, int maxRows) throws DataAccessException;
	public Set<SurveyDefinition> findAllCompletedInternal(String login) throws DataAccessException;
	public Set<SurveyDefinition> findAllCompletedInternal(String login,int startResult, int maxRows) throws DataAccessException;
	
	public Set<SurveyDefinition> findAllPublishedInternal() throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedInternal(int startResult, int maxRows) throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedInternal(String login) throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedInternal(String login,int startResult, int maxRows) throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedPublic() throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedPublic(int startResult, int maxRows) throws DataAccessException;
	
	public Set<SurveyDefinition> findAllPublishedExternal(String login) throws DataAccessException;
	public Set<SurveyDefinition> findAllPublishedExternal(String login, int startResult, int maxRows) throws DataAccessException;
	
	public SurveyDefinition findById(Long id) throws DataAccessException;
	public SurveyDefinition findByIdEager(Long id) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public Set<SurveyDefinition> findByName(String name) throws DataAccessException;
	public int deleteByDepartmentId(Long id) throws DataAccessException;
	
	public Set<SurveyDefinition> getSurveyDefinitionUsers(String login, int startResult, int maxRows) throws DataAccessException;
	public Set<SurveyDefinition> getSurveyDefinitionUsers(String login);
	
}