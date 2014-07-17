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

import com.jd.survey.domain.settings.SurveyDefinitionPage;

/**
 */
public interface SurveyDefinitionPageDAO extends JpaDao<SurveyDefinitionPage> {
	public Set<SurveyDefinitionPage> findAll() throws DataAccessException;
	public Set<SurveyDefinitionPage> findAll(int startResult, int maxRows) throws DataAccessException;
	public SurveyDefinitionPage findById(Long id) throws DataAccessException;
	public SurveyDefinitionPage findByOrder(Long surveyDefintionId,Short pageOrder) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public int deleteBySurveyDefinitionId(Long id) throws DataAccessException;
	public Set<SurveyDefinitionPage> findByTitle(String title) throws DataAccessException;
}