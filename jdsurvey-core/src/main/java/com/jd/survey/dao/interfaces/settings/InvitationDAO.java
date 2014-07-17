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

import com.jd.survey.domain.settings.Invitation;
import java.lang.Long;
import java.util.Set;
import java.util.SortedSet;

import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 */
public interface InvitationDAO extends JpaDao<Invitation> {
	public SortedSet<Invitation> findSurveyAll(Long surveyDefinitionId) throws DataAccessException;
	public SortedSet<Invitation> findSurveyAll(Long surveyDefinitionId, int startResult, int maxRows) throws DataAccessException;

	public Invitation findById(Long id) throws DataAccessException;
	public Invitation findByUuid(String uuid) throws DataAccessException;
	
	public Long getSurveyCount(Long surveyDefinitionId) throws DataAccessException;
	public Long getSurveyOpenedCount(Long surveyDefinitionId) throws DataAccessException;
	
	public SortedSet<Invitation> searchByFirstName(String firstName) throws DataAccessException;
	public SortedSet<Invitation> searchByLastName(String lastName) throws DataAccessException;
	public SortedSet<Invitation> searchByFirstNameAndLastName(String firstName , String lastName) throws DataAccessException;
	public SortedSet<Invitation> searchByEmail(String email) throws DataAccessException;
	
}