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
import java.util.SortedSet;

import com.jd.survey.domain.survey.SurveyEntry;



/**
 */
public interface SurveyEntryDAO {
	public SurveyEntry get(Long surveyId);
	public SortedSet<SurveyEntry> getAll(Long surveyDefinitionId);
	public SortedSet<SurveyEntry> getAll(Long surveyDefinitionId,Integer startResult, Integer maxRows);
	public Long getCount(Long surveyDefinitionId); 
}