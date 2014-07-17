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
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.QuestionStatisticDAO;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.survey.QuestionStatistic;


/** DAO implementation to handle persistence for object :Group
 */

@Repository("QuestionStatisticDAO")
@Transactional
public class QuestionStatisticMSSQLDAOImp  implements QuestionStatisticDAO{
	private static final Log log = LogFactory.getLog(QuestionStatisticMSSQLDAOImp.class);

	private JdbcTemplate jdbcTemplate;
    
	@Autowired
    public void setBasicDataSource(DataSource basicDataSource) {
        this.jdbcTemplate = new JdbcTemplate(basicDataSource);
    
	
	
	
	}
	
	
	/**
	 * Computes question answer statistics 
	 */
	@Override
	public List<QuestionStatistic> getStatistics(Question question,Long totalRecordCount) {
		switch (question.getType())
		{
		case YES_NO_DROPDOWN:
			return getFrequencyStatistics(question,totalRecordCount);
		case SHORT_TEXT_INPUT:
			return null;
		case LONG_TEXT_INPUT:
			return null;
		case HUGE_TEXT_INPUT:
			return null;
		case INTEGER_INPUT:
			return getDescriptiveStatistics(question,totalRecordCount);
		case CURRENCY_INPUT:
			return getDescriptiveStatistics(question,totalRecordCount);
		case DECIMAL_INPUT: 
			return getDescriptiveStatistics(question,totalRecordCount);
		case DATE_INPUT:
			return getDateDescriptiveStatistics(question,totalRecordCount);
		case SINGLE_CHOICE_DROP_DOWN:
			return getFrequencyStatistics(question,totalRecordCount);
		case  MULTIPLE_CHOICE_CHECKBOXES:
			return getArrayFrequencyStatistics(question, totalRecordCount);
		case DATASET_DROP_DOWN:
			return getFrequencyStatistics(question,totalRecordCount);	
		case  SINGLE_CHOICE_RADIO_BUTTONS:
			return getFrequencyStatistics(question,totalRecordCount);
			
		
		
		
		
		
		case 	YES_NO_DROPDOWN_MATRIX:
			return getMatrixFrequencyStatistics(question,totalRecordCount);
		case 	SHORT_TEXT_INPUT_MATRIX:
			return null;
		case 	INTEGER_INPUT_MATRIX:
			return getMatrixDescriptiveStatistics(question,totalRecordCount);
		case 	CURRENCY_INPUT_MATRIX:
			return getMatrixDescriptiveStatistics(question,totalRecordCount);
		case 	DECIMAL_INPUT_MATRIX:
			return getMatrixDescriptiveStatistics(question,totalRecordCount);
		case 	DATE_INPUT_MATRIX:
			return getDateMatrixDescriptiveStatistics(question,totalRecordCount);
		case 	IMAGE_DISPLAY:
			return null;
		case 	VIDEO_DISPLAY:
			return null;
		case 	FILE_UPLOAD:
			return null;
			
			
		case 	STAR_RATING:
			return getFrequencyStatistics(question,totalRecordCount);
		case 	SMILEY_FACES_RATING:
			return getFrequencyStatistics(question,totalRecordCount);
		
		default: 
			return null;
		}
		
	}


	/**
	 * Returns frequency statistics for a choice question' answers (Value, Count) 
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getFrequencyStatistics(Question question,final Long totalRecordCount) {
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		final String columnName = "p" + pageOrder+"q" +questionOrder ;		
		
		StringBuilder stringBuilder  = new StringBuilder();
		stringBuilder.append("select d." + columnName + " as col, count(*) as total "); 
		stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')" );
		stringBuilder.append(" group by d." + columnName);
		String selectSQLStatement =stringBuilder.toString();
		
		List<QuestionStatistic> questionStatistics = this.jdbcTemplate.query(selectSQLStatement,
				new RowMapper<QuestionStatistic>() {
				public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
					QuestionStatistic questionStatistic = new QuestionStatistic();
					questionStatistic.setEntry(rs.getString("col"));
					questionStatistic.setCount(rs.getLong("total"));
					questionStatistic.setTotalCount(totalRecordCount);
					return questionStatistic;
				}
			});
			return questionStatistics;
		
	}
	
	
	/**
	 * Returns frequency statistics for a choice matrix question' answers (Value, Count) 
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getMatrixFrequencyStatistics(Question question,final Long totalRecordCount) {
		List<QuestionStatistic> questionStatistics = new ArrayList<QuestionStatistic>();
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		for(QuestionRowLabel row :question.getRowLabels()) {
			for(QuestionColumnLabel column :question.getColumnLabels()) { 
				final Short columnOrder = column.getOrder();
				final Short rowOrder = row.getOrder();
				final String columnName = "p" + pageOrder+"q" +questionOrder +"r" + rowOrder + "c" + columnOrder;	
				StringBuilder stringBuilder  = new StringBuilder();
				stringBuilder.append("select d." + columnName + " as col, count(*) as total "); 
				stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')" );
				stringBuilder.append(" group by d." + columnName);
				String selectSQLStatement =stringBuilder.toString();
				List<QuestionStatistic> questionCellStatistics = this.jdbcTemplate.query(selectSQLStatement,
						new RowMapper<QuestionStatistic>() {
						public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
							QuestionStatistic questionStatistic = new QuestionStatistic();
							questionStatistic.setColumnOrder(columnOrder);
							questionStatistic.setRowOrder(rowOrder);
							questionStatistic.setEntry(rs.getString("col"));
							questionStatistic.setCount(rs.getLong("total"));
							questionStatistic.setTotalCount(totalRecordCount);
							return questionStatistic;
						}
					});
				questionStatistics.addAll(questionCellStatistics);
			}//loop on columns
		}//loop on rows
		return questionStatistics;
	}
	
	private List<QuestionStatistic>  getArrayFrequencyStatistics(Question question,final Long totalRecordCount) {
		List<QuestionStatistic> questionStatistics = new ArrayList<QuestionStatistic>();
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		for(QuestionOption questionOption :question.getOptions()) {
				final Short optionOrder = questionOption.getOrder();
				final String columnName = "p" + pageOrder+"q" +questionOrder +"o" + optionOrder ;	
				StringBuilder stringBuilder  = new StringBuilder();
				stringBuilder.append("select d." + columnName + " as col, count(*) as total "); 
				stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S') where "+ columnName +"=1");
				stringBuilder.append(" group by d." + columnName);
				String selectSQLStatement =stringBuilder.toString();
				List<QuestionStatistic> questionCellStatistics = this.jdbcTemplate.query(selectSQLStatement,
						new RowMapper<QuestionStatistic>() {
						public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
							QuestionStatistic questionStatistic = new QuestionStatistic();
							questionStatistic.setOptionOrder(optionOrder);
							questionStatistic.setEntry(rs.getString("col"));
							questionStatistic.setCount(rs.getLong("total"));
							questionStatistic.setTotalCount(totalRecordCount);
							return questionStatistic;
						}
					});
				questionStatistics.addAll(questionCellStatistics);
			
		}//loop on options
		return questionStatistics;
	}
	
	
	
	/**
	 * Returns descriptive statistics for numeric date answers (minimum, maximum)
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getDateDescriptiveStatistics(Question question,final Long totalRecordCount) {
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		final String columnName = "p" + pageOrder+"q" +questionOrder ;		
		StringBuilder stringBuilder  = new StringBuilder();
		stringBuilder.append("select MIN(d." + columnName + ") as min ,MAX(d." + columnName + ") as max "); 
		stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')");
		String selectSQLStatement =stringBuilder.toString();
		
		
		List<QuestionStatistic> questionStatistics = this.jdbcTemplate.query(selectSQLStatement,
				new RowMapper<QuestionStatistic>() {
				public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
					QuestionStatistic questionStatistic = new QuestionStatistic();
					questionStatistic.setMinDate(rs.getDate("min"));
					questionStatistic.setMaxDate(rs.getDate("max"));
					questionStatistic.setTotalCount(totalRecordCount);
					return questionStatistic;
				}
			});
			return questionStatistics;
		
	}
	
	/**
	 * Returns descriptive statistics for numeric question' answers (minimum, maximum, average, standard deviation)
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getDescriptiveStatistics(Question question,final Long totalRecordCount) {
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		final String columnName = "p" + pageOrder+"q" +questionOrder ;		
		StringBuilder stringBuilder  = new StringBuilder();
		stringBuilder.append("select MIN(d." + columnName + ") as min ,MAX(d." + columnName + ") as max ,AVG(d." + columnName + ") as avg ,STDEV(d." + columnName + ") as std "); 
		stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')");
		String selectSQLStatement =stringBuilder.toString();
		
		
		List<QuestionStatistic> questionStatistics = this.jdbcTemplate.query(selectSQLStatement,
				new RowMapper<QuestionStatistic>() {
				public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
					QuestionStatistic questionStatistic = new QuestionStatistic();
					questionStatistic.setMin(rs.getDouble("min"));
					questionStatistic.setMax(rs.getDouble("max"));
					questionStatistic.setAverage(rs.getDouble("avg"));
					questionStatistic.setSampleStandardDeviation(rs.getDouble("std"));
					questionStatistic.setTotalCount(totalRecordCount);
					return questionStatistic;
				}
			});
			return questionStatistics;
		
	}
	
	
	/**
	 * Returns descriptive statistics for numeric matrix question' answers (minimum, maximum, average, standard deviation)
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getDateMatrixDescriptiveStatistics(Question question,final Long totalRecordCount) {
		List<QuestionStatistic> questionStatistics = new ArrayList<QuestionStatistic>();
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		
		for(QuestionRowLabel row :question.getRowLabels()) {
			for(QuestionColumnLabel column :question.getColumnLabels()) { 
				final Short columnOrder = column.getOrder();
				final Short rowOrder = row.getOrder();
				final String columnName = "p" + pageOrder+"q" +questionOrder +"r" + rowOrder + "c" + columnOrder;	
				StringBuilder stringBuilder  = new StringBuilder();
				stringBuilder.append("select MIN(d." + columnName + ") as min ,MAX(d." + columnName + ") as max "); 
				stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')");
				String selectSQLStatement =stringBuilder.toString();
				List<QuestionStatistic> questionCellStatistics = this.jdbcTemplate.query(selectSQLStatement,
						new RowMapper<QuestionStatistic>() {
						public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
							QuestionStatistic questionStatistic = new QuestionStatistic();
							questionStatistic.setColumnOrder(columnOrder);
							questionStatistic.setRowOrder(rowOrder);
							questionStatistic.setMinDate(rs.getDate("min"));
							questionStatistic.setMaxDate(rs.getDate("max"));
							questionStatistic.setTotalCount(totalRecordCount);
							return questionStatistic;
						}
					});
				questionStatistics.addAll(questionCellStatistics);
			}//loop on columns
		}//loop on rows
		return questionStatistics;
	}
		
	
	/**
	 * Returns descriptive statistics for numeric matrix question' answers (minimum, maximum, average, standard deviation)
	 * @param question
	 * @return
	 */
	private List<QuestionStatistic>  getMatrixDescriptiveStatistics(Question question,final Long totalRecordCount) {
		List<QuestionStatistic> questionStatistics = new ArrayList<QuestionStatistic>();
		Long surveyDefinitionId =question.getPage().getSurveyDefinition().getId();
		Short pageOrder =question.getPage().getOrder();
		Short questionOrder =question.getOrder();
		
		for(QuestionRowLabel row :question.getRowLabels()) {
			for(QuestionColumnLabel column :question.getColumnLabels()) { 
				final Short columnOrder = column.getOrder();
				final Short rowOrder = row.getOrder();
				final String columnName = "p" + pageOrder+"q" +questionOrder +"r" + rowOrder + "c" + columnOrder;	
				StringBuilder stringBuilder  = new StringBuilder();
				stringBuilder.append("select MIN(d." + columnName + ") as min ,MAX(d." + columnName + ") as max ,AVG(d." + columnName + ") as avg ,STDEV(d." + columnName + ") as std "); 
				stringBuilder.append(" from survey_data_" + surveyDefinitionId + " d inner join survey s on (s.id=d.survey_id and s.status='S')");
				String selectSQLStatement =stringBuilder.toString();
				List<QuestionStatistic> questionCellStatistics = this.jdbcTemplate.query(selectSQLStatement,
						new RowMapper<QuestionStatistic>() {
						public QuestionStatistic mapRow(ResultSet rs, int rowNum) throws SQLException {
							QuestionStatistic questionStatistic = new QuestionStatistic();
							questionStatistic.setColumnOrder(columnOrder);
							questionStatistic.setRowOrder(rowOrder);
							questionStatistic.setMin(rs.getDouble("min"));
							questionStatistic.setMax(rs.getDouble("max"));
							questionStatistic.setAverage(rs.getDouble("avg"));
							questionStatistic.setSampleStandardDeviation(rs.getDouble("std"));
							questionStatistic.setTotalCount(totalRecordCount);
							return questionStatistic;
						}
					});
				questionStatistics.addAll(questionCellStatistics);
			}//loop on columns
		}//loop on rows
		return questionStatistics;
	}
		

	
	
	
}
	
