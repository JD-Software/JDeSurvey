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
import java.util.List;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.SurveyStatisticDAO;
import com.jd.survey.domain.survey.SurveyStatistic;

/** DAO implementation to handle persistence for object :Group
 */
@Repository("SurveyStatisticDAO")
@Transactional
public class SurveyStatisticDAOImp  implements SurveyStatisticDAO{

	private JdbcTemplate jdbcTemplate;
    
	@Autowired
    public void setBasicDataSource(DataSource basicDataSource) {
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    }

	public SurveyStatistic get(Long surveyDefinitionId) {
		SurveyStatistic surveyStatistic = this.jdbcTemplate.queryForObject(
				"select d.id as id, d.name as department_name, sd.name as survey_name, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status in ('I','R')) as icompleted_count," +  
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'S') as submitted_count," +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'D') as deleted_count, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id)  as total_count " +
						"from  	survey_definition sd "+
						"inner join department d on (sd.department_id = d.id) " +
						"where sd.id = ?",
						new Object[]{surveyDefinitionId},
						new RowMapper<SurveyStatistic>() {
					public SurveyStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
						SurveyStatistic surveyStatistic = new SurveyStatistic();
						surveyStatistic.setSurveyDefinitionId(rs.getLong("id"));
						surveyStatistic.setSurveyName(rs.getString("survey_name"));
						surveyStatistic.setDepartmentName(rs.getString("department_name"));
						surveyStatistic.setIcompletedCount(rs.getLong("icompleted_count"));
						surveyStatistic.setSubmittedCount(rs.getLong("submitted_count"));
						surveyStatistic.setDeletedCount(rs.getLong("deleted_count"));
						surveyStatistic.setTotalCount(rs.getLong("total_count"));
						return surveyStatistic;
					}
				});
		return surveyStatistic;
	}

	
	public List<SurveyStatistic> getAll() {
		List<SurveyStatistic> surveyStatistics = this.jdbcTemplate.query(
				"select sd.id as id, d.name as department_name, sd.name as survey_name, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status in ('I','R')) as icompleted_count," +  
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'S') as submitted_count," +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'D') as deleted_count, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id)  as total_count " +
						"from  	survey_definition sd "+
						"inner join department d on (sd.department_id = d.id) " +
						"where sd.status <> 'I' " +
						"order by d.name,sd.name",
						new RowMapper<SurveyStatistic>() {
					public SurveyStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
						SurveyStatistic surveyStatistic = new SurveyStatistic();
						surveyStatistic.setSurveyDefinitionId(rs.getLong("id"));
						surveyStatistic.setSurveyName(rs.getString("survey_name"));
						surveyStatistic.setDepartmentName(rs.getString("department_name"));
						surveyStatistic.setIcompletedCount(rs.getLong("icompleted_count"));
						surveyStatistic.setSubmittedCount(rs.getLong("submitted_count"));
						surveyStatistic.setDeletedCount(rs.getLong("deleted_count"));
						surveyStatistic.setTotalCount(rs.getLong("total_count"));
						return surveyStatistic;
					}
				});
		
		return surveyStatistics;
	}
	
	
	public List<SurveyStatistic> getAll(String login) {
		List<SurveyStatistic> surveyStatistics = this.jdbcTemplate.query(
				"select sd.id as id, d.name as department_name, sd.name as survey_name, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status in ('I','R')) as icompleted_count," +  
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'S') as submitted_count," +
						"(select count(*) from survey s where s.survey_definition_id = sd.id and s.status = 'D') as deleted_count, " +
						"(select count(*) from survey s where s.survey_definition_id = sd.id)  as total_count " +
						"from  	survey_definition sd "+
						"inner join department d on (sd.department_id = d.id) " +
						"inner join sec_user_department ud on (ud.department_id=d.id) " +
						"inner join sec_user u on (u.id=ud.user_id) " +
						"where sd.status <> 'I' " +
						"and u.login = ? " + 
						"order by d.name,sd.name ",
						new Object[]{login.toLowerCase()},
						new RowMapper<SurveyStatistic>() {
					public SurveyStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
						SurveyStatistic surveyStatistic = new SurveyStatistic();
						surveyStatistic.setSurveyDefinitionId(rs.getLong("id"));
						surveyStatistic.setSurveyName(rs.getString("survey_name"));
						surveyStatistic.setDepartmentName(rs.getString("department_name"));
						surveyStatistic.setIcompletedCount(rs.getLong("icompleted_count"));
						surveyStatistic.setSubmittedCount(rs.getLong("submitted_count"));
						surveyStatistic.setDeletedCount(rs.getLong("deleted_count"));
						surveyStatistic.setTotalCount(rs.getLong("total_count"));
						return surveyStatistic;
					}
				});
		
		return surveyStatistics;
	}
	
}
	
