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
package com.jd.survey.dao.survey;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.jd.survey.dao.interfaces.survey.ReportDAO;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyPage;

/** DAO implementation to handle persistence for survey reports
 */
@Repository("ReportDAO")
public class ReportDAOImpl implements ReportDAO  {

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate; 

    
	@Autowired
    public void setBasicDataSource(DataSource basicDataSource) {
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
        this.namedParameterJdbcTemplate = new 	NamedParameterJdbcTemplate(basicDataSource);	
    }
	
	 
    
    public List<Map<String,Object>> getSurveyData(Long surveyDefinitionId) {
    	String selectSql = "select s.creation_date, s.last_update_date, s.first_name, s.last_name, s.middle_name, s.ip_address, s.login, s.status, s.submission_date, s.type_name, d.* from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id)";
    	return  jdbcTemplate.queryForList(selectSql);
    }
    
}
