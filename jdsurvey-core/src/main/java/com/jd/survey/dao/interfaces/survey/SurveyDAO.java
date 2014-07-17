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
package  com.jd.survey.dao.interfaces.survey;

import java.util.List;
import java.util.Set;

import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyPage;

/**
 */
public interface SurveyDAO extends JpaDao<Survey> {
	public Set<Survey> findAll() throws DataAccessException;
	public Set<Survey> findAll(int startResult, int maxRows) throws DataAccessException;
	public Survey findById(Long id) throws DataAccessException;
	public Long getCount() throws DataAccessException;
	public Set<Survey> findAllByTypeId(Long surveyDefinitionId) throws DataAccessException;
	public Set<Survey> findAllIncompleteByTypeId(Long surveyDefinitionId) throws DataAccessException;
	public Set<Survey> findAllSubmittedByTypeId(Long surveyDefinitionId) throws DataAccessException;
	public Set<Survey> findAllDeletedByTypeId(Long surveyDefinitionId) throws DataAccessException;
	public Set<Survey> findUserEntriesByTypeIdAndLogin(Long surveyDefinitionId, String login) throws DataAccessException;
	public Set<Survey> findUserEntriesByTypeIdAndIpAddress(Long surveyDefinitionId, String ipAddress) throws DataAccessException;
	
	
	public void publish(SurveyDefinition surveyDefinition);
	public void initialize(Survey survey,SurveyDefinition surveyDefinition);
	public SurveyPage getPage(Survey survey, SurveyDefinitionPage surveyDefinitionPage, final String dateFormat);
	public List<SurveyPage> getPages(final Survey survey,final SurveyDefinition surveyDefinition, final String dateFormat);
	public void updatePage(SurveyPage surveyPage);
	public void updatePageVisibility(Long surveyDefinitionId, Long surveyId, Short pageOrder, Boolean visibility); 
}