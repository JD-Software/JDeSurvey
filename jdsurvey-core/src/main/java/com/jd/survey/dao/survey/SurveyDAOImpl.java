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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.survey.SurveyDAO;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyPage;


/** 
 * DAO implementation to handle persistence for object :Survey
 * Uses Both JPA and JDBCTemplate
 */
@Repository("SurveyDAO")
@Transactional
public class SurveyDAOImpl extends AbstractJpaDao<Survey> implements	SurveyDAO {
	private static final Log log = LogFactory.getLog(SurveyDAOImpl.class);
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(Arrays.asList(new Class<?>[] { Survey.class }));

	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate; 


	@Autowired
	public void setBasicDataSource(DataSource basicDataSource) {
		this.jdbcTemplate = new JdbcTemplate(basicDataSource);
		this.namedParameterJdbcTemplate = new 	NamedParameterJdbcTemplate(basicDataSource);	
	}


	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	public SurveyDAOImpl() {
		super();
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	@Transactional
	public Set<Survey> findAll() throws DataAccessException {
		return findAll(-1, -1);
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findAll(int startResult, int maxRows)	throws DataAccessException {
		Query query = createNamedQuery("Survey.findAll", startResult,maxRows);
		return new LinkedHashSet<Survey>(query.getResultList());
	}



	@Transactional
	public Survey findById(Long id) throws DataAccessException {
		try {
			Query query = createNamedQuery("Survey.findById", -1, -1, id);
			return (Survey) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

	}

	@Transactional
	public Long getCount() throws DataAccessException {
		try {
			Query query = createNamedQuery("Survey.getCount",-1,-1);
			return  (Long) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public boolean canBeMerged(Survey entity) {
		return true;
	}




	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findAllByTypeId(Long surveyDefinitionId) throws DataAccessException{
		Query query = createNamedQuery("Survey.findAllByTypeId", -1, -1, surveyDefinitionId);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findAllIncompleteByTypeId(Long surveyDefinitionId) throws DataAccessException {
		Query query = createNamedQuery("Survey.findAllByTypeId", -1, -1, surveyDefinitionId);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findAllSubmittedByTypeId(Long surveyDefinitionId) throws DataAccessException {
		Query query = createNamedQuery("Survey.findAllByTypeId", -1, -1, surveyDefinitionId);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findAllDeletedByTypeId(Long surveyDefinitionId)	throws DataAccessException {
		Query query = createNamedQuery("Survey.findAllByTypeId", -1, -1, surveyDefinitionId);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findUserEntriesByTypeIdAndLogin(Long surveyDefinitionId,String login) throws DataAccessException {
		Query query = createNamedQuery("Survey.findUserEntriesByTypeIdAndLogin", -1, -1, surveyDefinitionId,login);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<Survey> findUserEntriesByTypeIdAndIpAddress(Long surveyDefinitionId,String ipAddress) throws DataAccessException {
		Query query = createNamedQuery("Survey.findUserEntriesByTypeIdAndIpAddress", -1, -1, surveyDefinitionId,ipAddress);
		return new LinkedHashSet<Survey>(query.getResultList());
	}

	





	/**
	 * Publishes a survey definitions. This will create the table where the survey data will be stored 
	 */
	@Override
	public void publish(SurveyDefinition surveyDefinition) {
		try{
			StringBuilder stringBuilder  = new StringBuilder();
			stringBuilder.append("create table survey_data_" + surveyDefinition.getId());
			stringBuilder.append(" (");
			stringBuilder.append("survey_id BIGINT , ");
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				stringBuilder.append(" p" + page.getOrder()+"v BIT,");
				for(Question question :page.getQuestions()) {
					switch (question.getType()) {
					case YES_NO_DROPDOWN: //Yes No DropDown
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " BIT,");
						break;
					case SHORT_TEXT_INPUT: //Short Text Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(75),");
						break;
					case LONG_TEXT_INPUT: //Long Text Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(250),");
						break;
					case HUGE_TEXT_INPUT: //Huge Text Input
						//stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(2000),");
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " TEXT,");
						break;
					case INTEGER_INPUT: //Integer Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " BIGINT,");
						break;
					case CURRENCY_INPUT: //Currency Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "  decimal(19,4),");
						break;
					case DECIMAL_INPUT: //Decimal Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " decimal(19,4),");
						break;
					case DATE_INPUT: //Date Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " DATE,");
						break;
					case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(75),");					
						break;
					case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
						for(QuestionOption option :question.getOptions()) {
							stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "o" + option.getOrder() +  " BIT,");
						}
						//other Support
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "text"+ " varchar(255),");
						break;
					case DATASET_DROP_DOWN:
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(75),");
						break;
					case SINGLE_CHOICE_RADIO_BUTTONS:
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " varchar(5),");
						//other Support
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "text"+ " varchar(255),");
						break;	
					case YES_NO_DROPDOWN_MATRIX://Yes No DropDown Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " BIT,");
							}
						}
						break;
					case SHORT_TEXT_INPUT_MATRIX://Short Text Input Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " varchar(75),");
							}
						}
						break;
					case INTEGER_INPUT_MATRIX://Integer Input Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " BIGINT,");
							}
						}
						break;
					case CURRENCY_INPUT_MATRIX://Currency Input Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " decimal(19,4),");
							}
						}
						break;
					case DECIMAL_INPUT_MATRIX://Decimal Input Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " decimal(19,4),");
							}
						}
						break;
					case DATE_INPUT_MATRIX://Date Input Matrix
						for(QuestionRowLabel row :question.getRowLabels()) {
							for(QuestionColumnLabel column :question.getColumnLabels()) { 
								stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + "r" + row.getOrder() + "c" + column.getOrder() +  " DATE,");
							}
						}
						break;
					case STAR_RATING: //Integer Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " BIGINT,");
						break;
					case SMILEY_FACES_RATING: //Integer Input
						stringBuilder.append(" p" + page.getOrder()+"q" +question.getOrder() + " BIGINT,");
						break;	
					} 
				}
			}
			stringBuilder.append("PRIMARY KEY (survey_id))");
			String sqlStatement = stringBuilder.toString();
			//log.info(sqlStatement);
			jdbcTemplate.execute(sqlStatement);	
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}		
	}


	/**
	 * inserts an empty row in the survey data table
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void initialize(Survey survey,SurveyDefinition surveyDefinition) {
		try{
			StringBuilder stringBuilder  = new StringBuilder();
			//insert a null record if it is brand new survey
			stringBuilder.append("insert into  survey_data_" + surveyDefinition.getId());
			stringBuilder.append(" (survey_id,");
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				stringBuilder.append(" p" + page.getOrder()+"v,");
			}
			stringBuilder.setLength(stringBuilder.length() - 1);
			stringBuilder.append(") values (:survey_id,");
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				stringBuilder.append(" :p" + page.getOrder()+"v,");
			}
			stringBuilder.setLength(stringBuilder.length() - 1);
			stringBuilder.append(") ");
			
			Map namedParameters = new HashMap();
			namedParameters.put("survey_id", survey.getId());
			
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				namedParameters.put("p" + page.getOrder()+"v" , true);
			}
			String sqlStatement = stringBuilder.toString();
			log.info(sqlStatement);
			namedParameterJdbcTemplate.update(sqlStatement, namedParameters);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}
	}


	/**
	 * Reads a single survey page from the survey data table 
	 */
	@Override
	public SurveyPage getPage(final Survey survey,final SurveyDefinitionPage surveyDefinitionPage, final String dateFormat) {
		try{
			boolean hasDatabaseQuestions = false;
			StringBuilder stringBuilder  = new StringBuilder();
			String pageVisibilityColumn = "p" + surveyDefinitionPage.getOrder()+ "v";
			stringBuilder.append("select survey_id, " + pageVisibilityColumn + ", "); 
			for(Question question :surveyDefinitionPage.getQuestions()) {
				int optionsCount = question.getOptions().size();
				int rowCount = question.getRowLabels().size();
				int columnCount = question.getColumnLabels().size();
				switch (question.getType()) {
				case YES_NO_DROPDOWN: //Yes No DropDown
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case SHORT_TEXT_INPUT: //Short Text Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case LONG_TEXT_INPUT: //Long Text Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case HUGE_TEXT_INPUT: //Huge Text Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case INTEGER_INPUT: //Integer Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case CURRENCY_INPUT: //Currency Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case DECIMAL_INPUT: //Decimal Input
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case DATE_INPUT: //Date Input 
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					hasDatabaseQuestions = true;
					break;
				case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
					hasDatabaseQuestions = true;
					for(int o = 1; o <=optionsCount ;o++) {
						stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"o"+ o + ",");
					}
					//other support
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"text"+ ",");
					break;
				case DATASET_DROP_DOWN: //DataSet Drop Down
					hasDatabaseQuestions = true;
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					break;
				case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
					hasDatabaseQuestions = true;
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					//other support
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"text"+ ",");
					break;
				case YES_NO_DROPDOWN_MATRIX://Yes No DropDown Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				case SHORT_TEXT_INPUT_MATRIX://Short Text Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				case INTEGER_INPUT_MATRIX://Integer Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				case CURRENCY_INPUT_MATRIX://Currency Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				case DECIMAL_INPUT_MATRIX://Decimal Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				case DATE_INPUT_MATRIX://Date Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()  +"r"+ r + "c" + c + ",");
						}
					}
					break;
				
				case STAR_RATING: //Integer Input
					hasDatabaseQuestions = true;
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					break;
				case SMILEY_FACES_RATING: //Integer Input
					hasDatabaseQuestions = true;
					stringBuilder.append(" p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + ",");
					break;
				}
			}
			stringBuilder.setLength(stringBuilder.length() - 1);
			if (!hasDatabaseQuestions)	{ stringBuilder.setLength(stringBuilder.length() - 1);}
			
			stringBuilder.append(" from survey_data_" + surveyDefinitionPage.getSurveyDefinition().getId());
			stringBuilder.append(" where survey_id = ?");
			SurveyPage surveyPage = this.jdbcTemplate.queryForObject(stringBuilder.toString(),
					new Object[]{survey.getId()},
					new RowMapper<SurveyPage>() {
				public SurveyPage mapRow(ResultSet rs, int rowNum) throws SQLException {
					int optionsCount;	
					int rowCount;
					int columnCount;
					
					Integer[] integerAnswerValuesArray;
					
					Long[][] longAnswerValuesMatrix;
					String[][] stringAnswerValuesMatrix;
					BigDecimal[][] bigDecimalAnswerValuesMatrix; 
					Boolean[][] booleanAnswerValuesMatrix;
					Date[][] dateAnswerValuesMatrix;

					SurveyPage page = new SurveyPage(survey,surveyDefinitionPage);
					page.setVisible((rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"v")));
					QuestionAnswer questionAnswer;
					List<QuestionAnswer> questionAnswers = new ArrayList<QuestionAnswer>();
					for(Question question :surveyDefinitionPage.getQuestions()) {
						questionAnswer = new QuestionAnswer(question);
						optionsCount = question.getOptions().size();
						rowCount = question.getRowLabels().size();
						columnCount = question.getColumnLabels().size();

						switch (question.getType()) {
						case YES_NO_DROPDOWN: //Yes No DropDown
							questionAnswer.setBooleanAnswerValue((rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case SHORT_TEXT_INPUT: //Short Text Input
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case LONG_TEXT_INPUT: //Long Text Input
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case HUGE_TEXT_INPUT: //Huge Text Input
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case INTEGER_INPUT: //Integer Input
							questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							if (rs.wasNull()) questionAnswer.setLongAnswerValue(null);  
							questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" : questionAnswer.getLongAnswerValue().toString());
							break;
						case CURRENCY_INPUT: //Currency Input
							questionAnswer.setBigDecimalAnswerValue((rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							questionAnswer.setStringAnswerValue(questionAnswer.getBigDecimalAnswerValue() == null ?	"" :
																CurrencyValidator.getInstance().format(questionAnswer.getBigDecimalAnswerValue(),LocaleContextHolder.getLocale()));
							break;
						case DECIMAL_INPUT: //Decimal Input
							questionAnswer.setBigDecimalAnswerValue((rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							questionAnswer.setStringAnswerValue(questionAnswer.getBigDecimalAnswerValue() == null ?	"" :
								BigDecimalValidator.getInstance().format(questionAnswer.getBigDecimalAnswerValue(), LocaleContextHolder.getLocale()));
							break;
						case DATE_INPUT: //Date Input 
							questionAnswer.setDateAnswerValue((rs.getDate("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							questionAnswer.setStringAnswerValue(questionAnswer.getDateAnswerValue() == null ?	"" :
								DateValidator.getInstance().format(questionAnswer.getDateAnswerValue(), dateFormat));
							break;
						case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
							integerAnswerValuesArray= new Integer[optionsCount];
							int index = 0;
							for(int o = 1; o <=optionsCount ;o++) {
								if(rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"o"+ o)) {
									integerAnswerValuesArray[index] = o;
									index++;
								}
							}
							questionAnswer.setIntegerAnswerValuesArray(integerAnswerValuesArray);
							//other text data
							questionAnswer.setOtherStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + "text")));
							break;
						case DATASET_DROP_DOWN: //DataSet Drop Down
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							break;
						case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
							questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							//other text data
							questionAnswer.setOtherStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + "text")));
							break;
						case YES_NO_DROPDOWN_MATRIX://Yes No DropDown Matrix
							booleanAnswerValuesMatrix = new Boolean[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									booleanAnswerValuesMatrix[r-1][c-1]= rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
								}
							}
							questionAnswer.setBooleanAnswerValuesMatrix(booleanAnswerValuesMatrix);
							break;
						case SHORT_TEXT_INPUT_MATRIX://Short Text Input Matrix
							stringAnswerValuesMatrix = new String[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									stringAnswerValuesMatrix[r-1][c-1]= rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
								}
							}
							questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
							break;
						case INTEGER_INPUT_MATRIX://Integer Input Matrix
							stringAnswerValuesMatrix = new String[rowCount][columnCount];
							longAnswerValuesMatrix = new Long[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									longAnswerValuesMatrix[r-1][c-1] = rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
									if (rs.wasNull()) longAnswerValuesMatrix[r-1][c-1] =null;  
									stringAnswerValuesMatrix[r-1][c-1]= longAnswerValuesMatrix[r-1][c-1] == null ?	"" : longAnswerValuesMatrix[r-1][c-1].toString();	
								}
							}
							questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
							questionAnswer.setLongAnswerValuesMatrix(longAnswerValuesMatrix);
							break;
						case CURRENCY_INPUT_MATRIX://Currency Input Matrix
							stringAnswerValuesMatrix = new String[rowCount][columnCount];
							bigDecimalAnswerValuesMatrix = new BigDecimal[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									bigDecimalAnswerValuesMatrix[r-1][c-1] = rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
									stringAnswerValuesMatrix[r-1][c-1]= bigDecimalAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
										CurrencyValidator.getInstance().format(bigDecimalAnswerValuesMatrix[r-1][c-1],LocaleContextHolder.getLocale());	
								}
							}
							questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
							questionAnswer.setBigDecimalAnswerValuesMatrix(bigDecimalAnswerValuesMatrix);
							break;
						case DECIMAL_INPUT_MATRIX://Decimal Input Matrix
							stringAnswerValuesMatrix = new String[rowCount][columnCount];
							bigDecimalAnswerValuesMatrix = new BigDecimal[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									bigDecimalAnswerValuesMatrix[r-1][c-1] = rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
									stringAnswerValuesMatrix[r-1][c-1]= bigDecimalAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
										BigDecimalValidator.getInstance().format(bigDecimalAnswerValuesMatrix[r-1][c-1],LocaleContextHolder.getLocale());	
								}
							}
							questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
							questionAnswer.setBigDecimalAnswerValuesMatrix(bigDecimalAnswerValuesMatrix);
							break;
						case DATE_INPUT_MATRIX://Date Input Matrix
							stringAnswerValuesMatrix = new String[rowCount][columnCount];
							dateAnswerValuesMatrix = new Date[rowCount][columnCount];
							for(int r = 1; r <=rowCount ;r++) {
								for(int c = 1; c <=columnCount ;c++) { 
									dateAnswerValuesMatrix[r-1][c-1] = rs.getDate("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
									stringAnswerValuesMatrix[r-1][c-1]= dateAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
										DateValidator.getInstance().format(dateAnswerValuesMatrix[r-1][c-1], dateFormat);	
								}
							}
							questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
							questionAnswer.setDateAnswerValuesMatrix(dateAnswerValuesMatrix);
							break;
						case STAR_RATING: //Integer Input
							questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							if (rs.wasNull()) questionAnswer.setLongAnswerValue(null);  
							questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" : questionAnswer.getLongAnswerValue().toString());
							break;
						case SMILEY_FACES_RATING: //Integer Input
							questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
							if (rs.wasNull()) questionAnswer.setLongAnswerValue(null);  
							questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" : questionAnswer.getLongAnswerValue().toString());
							break;
							
						}
						questionAnswers.add(questionAnswer);
					}
					page.setQuestionAnswers(questionAnswers);
					return page;
				}
			});
			return surveyPage;
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}
	}



	/**
	 * Reads a single survey page from the survey data table 
	 */
	@Override
	public List<SurveyPage> getPages(final Survey survey,final SurveyDefinition surveyDefinition, final String dateFormat) {
		try{
			StringBuilder stringBuilder  = new StringBuilder();
			stringBuilder.append("select * "); 
			stringBuilder.setLength(stringBuilder.length() - 1);
			stringBuilder.append(" from survey_data_" + surveyDefinition.getId());
			stringBuilder.append(" where survey_id = ?");
			List<SurveyPage> surveyPages = this.jdbcTemplate.queryForObject(stringBuilder.toString(),
					new Object[]{survey.getId()},
					new RowMapper<List<SurveyPage>>() {
				public List<SurveyPage> mapRow(ResultSet rs, int rowNum) throws SQLException {
					int optionsCount;	
					int rowCount;
					int columnCount;
					Integer[] integerAnswerValuesArray;
					Long[][] longAnswerValuesMatrix;
					String[][] stringAnswerValuesMatrix;
					BigDecimal[][] bigDecimalAnswerValuesMatrix; 
					Boolean[][] booleanAnswerValuesMatrix;
					Date[][] dateAnswerValuesMatrix;

					List<SurveyPage> pages = new ArrayList<SurveyPage>();
					for (SurveyDefinitionPage surveyDefinitionPage : surveyDefinition.getPages()) {
						SurveyPage page = new SurveyPage(survey,surveyDefinitionPage);
						page.setVisible((rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"v")));
						QuestionAnswer questionAnswer;
						List<QuestionAnswer> questionAnswers = new ArrayList<QuestionAnswer>();
						for(Question question :surveyDefinitionPage.getQuestions()) {
							questionAnswer = new QuestionAnswer(question);
							optionsCount = question.getOptions().size();
							rowCount = question.getRowLabels().size();
							columnCount = question.getColumnLabels().size();
							switch (question.getType()) {
								case YES_NO_DROPDOWN: //Yes No DropDown
									questionAnswer.setBooleanAnswerValue((rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case SHORT_TEXT_INPUT: //Short Text Input
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case LONG_TEXT_INPUT: //Long Text Input
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case HUGE_TEXT_INPUT: //Huge Text Input
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case INTEGER_INPUT: //Integer Input
									questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" :
										questionAnswer.getLongAnswerValue().toString());
									break;
								case CURRENCY_INPUT: //Currency Input
									questionAnswer.setBigDecimalAnswerValue((rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									questionAnswer.setStringAnswerValue(questionAnswer.getBigDecimalAnswerValue() == null ?	"" :
																		CurrencyValidator.getInstance().format(questionAnswer.getBigDecimalAnswerValue(),LocaleContextHolder.getLocale()));
									break;
								case DECIMAL_INPUT: //Decimal Input
									questionAnswer.setBigDecimalAnswerValue((rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									questionAnswer.setStringAnswerValue(questionAnswer.getBigDecimalAnswerValue() == null ?	"" :
										BigDecimalValidator.getInstance().format(questionAnswer.getBigDecimalAnswerValue(), LocaleContextHolder.getLocale()));
									break;
								case DATE_INPUT: //Date Input 
									questionAnswer.setDateAnswerValue((rs.getDate("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									questionAnswer.setStringAnswerValue(questionAnswer.getDateAnswerValue() == null ?	"" :
										DateValidator.getInstance().format(questionAnswer.getDateAnswerValue(), dateFormat));
									break;
								case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
									integerAnswerValuesArray= new Integer[optionsCount];
									int index = 0;
									for(int o = 1; o <=optionsCount ;o++) {
										if(rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"o"+ o)) {
											integerAnswerValuesArray[index] = o;
											index++;
										}
									}
									questionAnswer.setIntegerAnswerValuesArray(integerAnswerValuesArray);
									//other text data
									questionAnswer.setOtherStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + "text")));
									break;
								case DATASET_DROP_DOWN: //DataSet Drop Down
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									break;
								case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
									questionAnswer.setStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									//other text data
									questionAnswer.setOtherStringAnswerValue((rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder() + "text")));
									break;
								case YES_NO_DROPDOWN_MATRIX://Yes No DropDown Matrix
									booleanAnswerValuesMatrix = new Boolean[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											booleanAnswerValuesMatrix[r-1][c-1]= rs.getBoolean("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
										}
									}
									questionAnswer.setBooleanAnswerValuesMatrix(booleanAnswerValuesMatrix);
									break;
								case SHORT_TEXT_INPUT_MATRIX://Short Text Input Matrix
									stringAnswerValuesMatrix = new String[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											stringAnswerValuesMatrix[r-1][c-1]= rs.getString("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
										}
									}
									questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
									break;
								case INTEGER_INPUT_MATRIX://Integer Input Matrix
									stringAnswerValuesMatrix = new String[rowCount][columnCount];
									longAnswerValuesMatrix = new Long[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											longAnswerValuesMatrix[r-1][c-1] = rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
											stringAnswerValuesMatrix[r-1][c-1]= longAnswerValuesMatrix[r-1][c-1] == null ?	"" : longAnswerValuesMatrix[r-1][c-1].toString();	
										}
									}
									questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
									questionAnswer.setLongAnswerValuesMatrix(longAnswerValuesMatrix);
									break;
								case CURRENCY_INPUT_MATRIX://Currency Input Matrix
									stringAnswerValuesMatrix = new String[rowCount][columnCount];
									bigDecimalAnswerValuesMatrix = new BigDecimal[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											bigDecimalAnswerValuesMatrix[r-1][c-1] = rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
											stringAnswerValuesMatrix[r-1][c-1]= bigDecimalAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
												CurrencyValidator.getInstance().format(bigDecimalAnswerValuesMatrix[r-1][c-1],LocaleContextHolder.getLocale());	
										}
									}
									questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
									questionAnswer.setBigDecimalAnswerValuesMatrix(bigDecimalAnswerValuesMatrix);
									break;
								case DECIMAL_INPUT_MATRIX://Decimal Input Matrix
									stringAnswerValuesMatrix = new String[rowCount][columnCount];
									bigDecimalAnswerValuesMatrix = new BigDecimal[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											bigDecimalAnswerValuesMatrix[r-1][c-1] = rs.getBigDecimal("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
											stringAnswerValuesMatrix[r-1][c-1]= bigDecimalAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
												BigDecimalValidator.getInstance().format(bigDecimalAnswerValuesMatrix[r-1][c-1],LocaleContextHolder.getLocale());	
										}
									}
									questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
									questionAnswer.setBigDecimalAnswerValuesMatrix(bigDecimalAnswerValuesMatrix);
									break;
								case DATE_INPUT_MATRIX://Date Input Matrix
									stringAnswerValuesMatrix = new String[rowCount][columnCount];
									dateAnswerValuesMatrix = new Date[rowCount][columnCount];
									for(int r = 1; r <=rowCount ;r++) {
										for(int c = 1; c <=columnCount ;c++) { 
											dateAnswerValuesMatrix[r-1][c-1] = rs.getDate("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder()+"r"+ r +"c"+ c);
											stringAnswerValuesMatrix[r-1][c-1]= dateAnswerValuesMatrix[r-1][c-1] == null ?	"" : 
												DateValidator.getInstance().format(dateAnswerValuesMatrix[r-1][c-1], dateFormat);	
										}
									}
									questionAnswer.setStringAnswerValuesMatrix(stringAnswerValuesMatrix);
									questionAnswer.setDateAnswerValuesMatrix(dateAnswerValuesMatrix);
									break;
								case STAR_RATING: //Integer Input
									questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									if (rs.wasNull()) questionAnswer.setLongAnswerValue(null);  
									questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" : questionAnswer.getLongAnswerValue().toString());
									break;
								case SMILEY_FACES_RATING: //Integer Input
									questionAnswer.setLongAnswerValue((rs.getLong("p" + surveyDefinitionPage.getOrder()+"q" +question.getOrder())));
									if (rs.wasNull()) questionAnswer.setLongAnswerValue(null);  
									questionAnswer.setStringAnswerValue(questionAnswer.getLongAnswerValue() == null ?	"" : questionAnswer.getLongAnswerValue().toString());
									break;	
							}
							questionAnswers.add(questionAnswer);
						}
						page.setQuestionAnswers(questionAnswers);
						pages.add(page);	
					}
					return pages;
				}
			});
			return surveyPages;
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}
	}

	/**
	 * Updates a survey page visibility in the database
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void updatePageVisibility(Long surveyDefinitionId, Long surveyId, Short pageOrder, Boolean visibility) {
		try{
			Map namedParameters = new HashMap();
			StringBuilder stringBuilder  = new StringBuilder();
			stringBuilder.append("update survey_data_" + surveyDefinitionId);
			stringBuilder.append(" set ");
			stringBuilder.append(" p" + pageOrder+"v=:visibility ");
			stringBuilder.append(" where survey_id =:survey_id");
			String sqlStatement = stringBuilder.toString();
			namedParameters.put("survey_id", surveyId);
			namedParameters.put("visibility", visibility);
			namedParameterJdbcTemplate.update(sqlStatement, namedParameters);
		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}
	}


	/**
	 * Updates a survey page in the survey data table.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void updatePage(SurveyPage surveyPage) {
		try{
			boolean hasDatabaseQuestions = false;
			int optionsCount;	
			int rowCount;
			int columnCount;
			String columnName;
			StringBuilder stringBuilder  = new StringBuilder();

			Map namedParameters = new HashMap();
			namedParameters.put("survey_id", surveyPage.getSurvey().getId());

			stringBuilder.append("update survey_data_" + surveyPage.getSurvey().getTypeId());
			stringBuilder.append(" set ");
			
			for(QuestionAnswer questionAnswer :surveyPage.getQuestionAnswers()) {
				optionsCount = questionAnswer.getQuestion().getOptions().size();
				rowCount = questionAnswer.getQuestion().getRowLabels().size();
				columnCount = questionAnswer.getQuestion().getColumnLabels().size();	
				switch (questionAnswer.getQuestion().getType()) {
				case YES_NO_DROPDOWN: //Yes No DropDown
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getBooleanAnswerValue());
					break;
				case SHORT_TEXT_INPUT: //Short Text Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case LONG_TEXT_INPUT: //Long Text Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case HUGE_TEXT_INPUT: //Huge Text Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case INTEGER_INPUT: //Integer Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getLongAnswerValue());
					break;
				case CURRENCY_INPUT: //Currency Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getBigDecimalAnswerValue());
					break;
				case DECIMAL_INPUT: //Decimal Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getBigDecimalAnswerValue());
					break;
				case DATE_INPUT: //Date Input
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getDateAnswerValue());
					break;
				case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
					hasDatabaseQuestions = true;
					if (questionAnswer.getIntegerAnswerValuesArray() != null) {
						for(int o = 1; o <=optionsCount ;o++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"o"+ o;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							for (int i= 0; i<questionAnswer.getIntegerAnswerValuesArray().length ; i++) {
								if (questionAnswer.getIntegerAnswerValuesArray()[i] != null &&
									questionAnswer.getIntegerAnswerValuesArray()[i]	== o) {
									namedParameters.put(columnName, true);
									break;
								}
								 namedParameters.put(columnName, false);
							}
						}
					}
					//other text data
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() + "text";
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getOtherStringAnswerValue());
					break;
				case DATASET_DROP_DOWN: //DataSet Drop Down
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					//other text data
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() + "text";
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getOtherStringAnswerValue());
					
					break;
				case YES_NO_DROPDOWN_MATRIX://Yes No DropDown Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getBooleanAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case SHORT_TEXT_INPUT_MATRIX://Short Text Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getStringAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case INTEGER_INPUT_MATRIX://Integer Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getLongAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case CURRENCY_INPUT_MATRIX://Currency Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getBigDecimalAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case DECIMAL_INPUT_MATRIX://Decimal Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getBigDecimalAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case DATE_INPUT_MATRIX://Date Input Matrix
					hasDatabaseQuestions = true;
					for(int r = 1; r <=rowCount ;r++) {
						for(int c = 1; c <=columnCount ;c++) {
							columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder() +"r"+ r +"c"+ c;
							stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
							namedParameters.put(columnName, questionAnswer.getDateAnswerValuesMatrix()[r-1][c-1]);
						}
					}
					break;
				case STAR_RATING : //Star Rating
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;
				case SMILEY_FACES_RATING: //Smiley Faces Rating
					hasDatabaseQuestions = true;
					columnName = "p" + surveyPage.getOrder()+"q" +questionAnswer.getOrder();
					stringBuilder.append(" " + columnName + "=:"+ columnName+ ",");
					namedParameters.put(columnName, questionAnswer.getStringAnswerValue());
					break;	
				}

			}
			if (hasDatabaseQuestions) {
				stringBuilder.setLength(stringBuilder.length() - 1);
				stringBuilder.append(" where survey_id =:survey_id");
				String sqlStatement = stringBuilder.toString();
				System.out.println(sqlStatement);
				namedParameterJdbcTemplate.update(sqlStatement, namedParameters);
			}

		}
		catch (Exception e)
		{
			log.error(e.getMessage(),e);
			throw(new RuntimeException(e));
		}
	}











































}
