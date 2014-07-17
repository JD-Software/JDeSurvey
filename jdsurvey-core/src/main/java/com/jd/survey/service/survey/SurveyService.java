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
package com.jd.survey.service.survey;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jd.survey.dao.interfaces.security.UserDAO;
import com.jd.survey.dao.interfaces.settings.DataSetDAO;
import com.jd.survey.dao.interfaces.settings.DataSetItemDAO;
import com.jd.survey.dao.interfaces.settings.QuestionDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionPageDAO;
import com.jd.survey.dao.interfaces.survey.QuestionStatisticDAO;
import com.jd.survey.dao.interfaces.survey.SurveyDAO;
import com.jd.survey.dao.interfaces.survey.SurveyDocumentDAO;
import com.jd.survey.dao.interfaces.survey.SurveyEntryDAO;
import com.jd.survey.dao.interfaces.survey.SurveyStatisticDAO;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.QuestionStatistic;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyDocument;
import com.jd.survey.domain.survey.SurveyEntry;
import com.jd.survey.domain.survey.SurveyPage;
import com.jd.survey.domain.survey.SurveyStatistic;
import com.jd.survey.domain.survey.SurveyStatus;


@Transactional(readOnly = true)
@Service("SurveyService")
public class SurveyService {
	private static final Log log = LogFactory.getLog(SurveyService.class);
	@Autowired	private UserDAO userDAO;
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private SurveyDefinitionPageDAO surveyDefinitionPageDAO;
	@Autowired	private QuestionDAO questionDAO;
	
	@Autowired	private SurveyDAO surveyDAO;
	@Autowired	private SurveyEntryDAO surveyEntryDAO;
	@Autowired	private QuestionStatisticDAO questionStatisticDAO;
	@Autowired	private SurveyStatisticDAO surveyStatisticDAO;
	@Autowired	private SurveyDocumentDAO surveyDocumentDAO;
	@Autowired	private DataSetDAO dataSetDAO;
	@Autowired	private DataSetItemDAO dataSetItemDAO;
	@Autowired  private VelocityEngine velocityEngine;
	
	
	/** 
	 * creates a new survey for the user  
	 * @param surveyDefinitionId
	 * @param login
	 * @return
	 */
	@Transactional(readOnly = false)
	public Survey survey_create(Long surveyDefinitionId, String login, String ipAddress){
			User user=null;
			SurveyDefinition surveyDefinition  = surveyDefinitionDAO.findById(surveyDefinitionId);
			if (login!=null && login.length() >0) {
				user = userDAO.findByLogin(login);
			}
			Survey survey;
			if (user!=null) { survey = new Survey(surveyDefinition, user,ipAddress);} else {survey = new Survey(surveyDefinition, ipAddress);};
			survey =  surveyDAO.merge(survey);
			surveyDAO.initialize(survey,surveyDefinition);
			return survey;
	}

	/**
	 * Updates survey attributes 
	 * @param survey
	 * @return
	 */
	@Transactional(readOnly = false)
	public Survey survey_merge(Survey survey) {
			survey.setLastUpdateDate(new Date());
			return surveyDAO.merge(survey);
	}
	
	/**
	  * Delete a survey 
	  * @param survey
	  */
	@Transactional(readOnly = false)
	public void survey_remove(Survey survey) {
			surveyDAO.remove(survey);
	}
	
	/**
	 * Submit a survey
	 * @param surveyId
	 * @return
	 */
	@Transactional(readOnly = false)
	public Survey survey_submit(Long surveyId) {
		Survey survey = surveyDAO.findById(surveyId);
		survey.setStatus(SurveyStatus.S);
		survey.setSubmissionDate(new Date());
		survey.setLastUpdateDate(new Date());
		return surveyDAO.merge(survey);
	}
		
	
	
	private void processPipedText (SurveyPage surveyPage,List<SurveyPage>  surveyPages) {
		for (QuestionAnswer questionAnswer  : surveyPage.getQuestionAnswers()) {
			if (questionAnswer.getQuestion().getQuestionText().contains("${")) {
				processPipedText(questionAnswer.getQuestion(), surveyPages);
			}
		}
	}
	
	
	private void processPipedText (Question question,List<SurveyPage>  surveyPages) {
		int questionPageOrder= question.getPage().getOrder();
		String questionText = question.getQuestionText();
		for (SurveyPage surveyPage :surveyPages) {
			if (surveyPage.getOrder() < questionPageOrder) {
				for (QuestionAnswer questionAnswer :surveyPage.getQuestionAnswers()) {
					switch (questionAnswer.getQuestion().getType())	{
					case YES_NO_DROPDOWN:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case SHORT_TEXT_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case LONG_TEXT_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case HUGE_TEXT_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case INTEGER_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case CURRENCY_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case DECIMAL_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;
					case DATE_INPUT:
						questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (questionAnswer.getStringAnswerValue() == null ? "": questionAnswer.getStringAnswerValue()));
						break;

					case SINGLE_CHOICE_DROP_DOWN:
						for (QuestionOption option :questionAnswer.getQuestion().getOptions()){
							if (option.getValue().equals(questionAnswer.getStringAnswerValue())) {
								questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (option.getText()==null? "":option.getText()) );
								break;	
							}
						}
						break;
					case DATASET_DROP_DOWN:
						for (QuestionOption option :questionAnswer.getQuestion().getOptions()){
							if (option.getValue().equals(questionAnswer.getStringAnswerValue())) {
								questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (option.getText()==null? "":option.getText()));
								break;	
							}
						}
						break;
					case SINGLE_CHOICE_RADIO_BUTTONS:
						for (QuestionOption option :questionAnswer.getQuestion().getOptions()){
							if (option.getValue().equals(questionAnswer.getStringAnswerValue())) {
								questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (option.getText()==null? "":option.getText()));
								break;	
							}
						}
						break;
					case	STAR_RATING:
						for (QuestionOption option :questionAnswer.getQuestion().getOptions()){
							if (option.getValue().equals(questionAnswer.getStringAnswerValue())) {
								questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (option.getText()==null? "":option.getText()));
								break;	
							}
						}
						break;
					case	SMILEY_FACES_RATING:
						for (QuestionOption option :questionAnswer.getQuestion().getOptions()){
							if (option.getValue().equals(questionAnswer.getStringAnswerValue())) {
								questionText = questionText.replace("${p"+ surveyPage.getOrder() + "q" + questionAnswer.getQuestion().getOrder() +"}", (option.getText()==null? "":option.getText()));
								break;	
							}
						}
						break;
					}

				}
			}
		}
		question.setQuestionLabel(questionText);
	}
	
	
	
	
	/**
	 * Updates the answers for a survey definition page
	*/
	public SurveyPage surveyPage_get(Long surveyId, Short pageOrder, final String dateFormat){
		Survey survey = surveyDAO.findById(surveyId);
		SurveyDefinitionPage surveyDefinitionPage = surveyDefinitionPageDAO.findByOrder(survey.getTypeId(),pageOrder);
		SurveyPage surveyPage  = surveyDAO.getPage(survey, surveyDefinitionPage,dateFormat);

		
		//populate the uploaded files

		for (QuestionAnswer questionAnswer: surveyPage.getQuestionAnswers()) {
			/*
			if (questionAnswer.getQuestion().getType() == QuestionType.DATASET_DROP_DOWN){
				DataSet dataset = dataSetDAO.findByName(questionAnswer.getQuestion().getDataSetCode());
				questionAnswer.getQuestion().setDataSetItems(dataSetItemDAO.findByDataSetId(dataset.getId()));
				continue;
			}
			*/
			if (questionAnswer.getQuestion().getType() == QuestionType.FILE_UPLOAD){
				SurveyDocument surveyDocument  = surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyId, questionAnswer.getQuestion().getId());
				if (surveyDocument!= null)	{
					questionAnswer.setSurveyDocument(surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyId, questionAnswer.getQuestion().getId()));
				}
			}
			
		}
		//Process piped text
		processPipedText(surveyPage, surveyPage_getAll(surveyId,dateFormat));
		return  surveyPage;
	}
	
	

	/**
	 * Updates the answers for a survey definition page
	 */
	public List<SurveyPage> surveyPage_getAll(Long surveyId, final String dateFormat){
		Survey survey = surveyDAO.findById(surveyId);
		SurveyDefinition surveyDefinition = surveyDefinitionDAO.findById(survey.getTypeId());
		List<SurveyPage> surveyPages =  surveyDAO.getPages(survey, surveyDefinition,dateFormat);
		//populate the datasets
		for (SurveyPage surveyPage :surveyPages){
			for (QuestionAnswer questionAnswer: surveyPage.getQuestionAnswers()) {
				/*
				if (questionAnswer.getQuestion().getType() == QuestionType.DATASET_DROP_DOWN){
					DataSet dataset = dataSetDAO.findByName(questionAnswer.getQuestion().getDataSetCode());
					questionAnswer.getQuestion().setDataSetItems(dataSetItemDAO.findByDataSetId(dataset.getId()));
					continue;
				}
				*/
				if (questionAnswer.getQuestion().getType() == QuestionType.FILE_UPLOAD){
					SurveyDocument surveyDocument  = surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyId, questionAnswer.getQuestion().getId());
					if (surveyDocument!= null)	{
						questionAnswer.setSurveyDocument(surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyId, questionAnswer.getQuestion().getId()));
					}
				}

			}
			
			//Process piped text
			processPipedText(surveyPage, surveyPages);
		}
		//Populate uploaded files
		return surveyPages;
	}
	
	

	/**
	 * Updates the database with the survey page answers
	 * @param surveyPage
	 */
	@Transactional(readOnly = false)
	public void surveyPage_update(SurveyPage surveyPage, String dateFormat){
		for (QuestionAnswer questionAnswer: surveyPage.getQuestionAnswers()) {
			if (questionAnswer.getQuestion().getType() == QuestionType.FILE_UPLOAD){
				if (questionAnswer.getSurveyDocument() != null && questionAnswer.getSurveyDocument().getContent().length > 0 ) {
					//delete any question documents
					surveyDocumentDAO.deleteBySurveyIdAndQuestionId(surveyPage.getSurvey().getId(),questionAnswer.getQuestion().getId());
					//save uploaded document
					surveyDocumentDAO.merge(questionAnswer.getSurveyDocument());
				}
			}
		}
		//save the question answers
		surveyDAO.updatePage(surveyPage);
		Boolean visibility =true;
		//checks if there is any branching logic on this page and update the visibility of following pages if necessary.
		SurveyDefinitionPage surveyDefinitionPage= surveyDefinitionPageDAO.findByOrder(surveyPage.getSurvey().getTypeId(), surveyPage.getOrder());
		surveyDefinitionPage.loadFromJson();

		if (surveyDefinitionPage.hasLogic()) {
			if (surveyPage.getSatisfiesConditions(surveyDefinitionPage.getPageLogic(),dateFormat)) {
				visibility =false;
			}
			if (surveyDefinitionPage.getPageLogic().getJumpToPageOrder() == 0) {
				for(Short i = (short) (surveyPage.getOrder() +  1); i <= surveyDefinitionPage.getSurveyDefinition().getPages().size() ; i++ ) {
					log.info("!!!!!!!!!!!!! updating to " + visibility +  " page order" + i);
					surveyDAO.updatePageVisibility(surveyDefinitionPage.getSurveyDefinition().getId(), surveyPage.getSurvey().getId(), i, visibility);
				}
			}
			else {
				for(Short i = (short) (surveyPage.getOrder() +  1); i < surveyDefinitionPage.getPageLogic().getJumpToPageOrder() ; i++ ) {
					log.info("!!!!!!!!!!!!! updating to " + visibility +  " page order" + i);
					surveyDAO.updatePageVisibility(surveyDefinitionPage.getSurveyDefinition().getId(), surveyPage.getSurvey().getId(), i, visibility);
				}
			}
		}
	}

	
	
	
	

	
	
	
	/**
	 * Updates a survey page with the question definitions from the database 
	*/
	public SurveyPage surveyPage_updateSettings(SurveyPage surveyPage){
	
		//updates the Question definitions on the answers
		for (QuestionAnswer questionAnswer: surveyPage.getQuestionAnswers()) {
			questionAnswer.setQuestion(questionDAO.findByOrder(surveyPage.getSurvey().getTypeId(), 
															   surveyPage.getOrder(), 
															   questionAnswer.getOrder()));
			
			//If upload file question mark if file already loaded
			if (questionAnswer.getQuestion().getType() == QuestionType.FILE_UPLOAD){
				SurveyDocument surveyDocument  = surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyPage.getSurvey().getId(), questionAnswer.getQuestion().getId());
				if (surveyDocument!= null)	{
					questionAnswer.setDocumentAlreadyUploded(true);
				}
			}
		}
		return surveyPage;
	}
	
	

	
	
	
	public Set<Survey> surveyDefinition_findAll()
	throws DataAccessException {
			return surveyDAO.findAll();
	}
	
	
	
	public Set<Survey> survey_findAllByTypeId(Long surveyDefinitionId){
		return surveyDAO.findAllByTypeId(surveyDefinitionId);
	}
	
	
	
	public Set<Survey> survey_findAllIncompleteByTypeId(Long surveyDefinitionId){
		return surveyDAO.findAllIncompleteByTypeId(surveyDefinitionId);
	}

	
	
	public Set<Survey> survey_findAllSubmittedByTypeId(Long surveyDefinitionId) {
		return surveyDAO.findAllSubmittedByTypeId(surveyDefinitionId);
	}

	
	
	public Set<Survey> survey_findAllDeletedByTypeId(Long surveyDefinitionId)	{
		return surveyDAO.findAllDeletedByTypeId(surveyDefinitionId);
	}


	
	public Set<Survey> survey_findUserEntriesByTypeIdAndLogin(Long surveyDefinitionId,String login){
		return surveyDAO.findUserEntriesByTypeIdAndLogin(surveyDefinitionId,login);
	}
	
	public Set<Survey> survey_findUserEntriesByTypeIdAndIpAddress(Long surveyDefinitionId,String ipAddress){
		return surveyDAO.findUserEntriesByTypeIdAndIpAddress(surveyDefinitionId,ipAddress);
	}
	
	
	
	public Survey survey_findById(Long id) {
		return surveyDAO.findById(id);
	}
	
	
	
	
	
	public SurveyEntry surveyEntry_get(Long surveyId){
		return surveyEntryDAO.get(surveyId);
	}
	
	public SortedSet<SurveyEntry> surveyEntry_getAll(Long surveyDefinitionId){
		return surveyEntryDAO.getAll(surveyDefinitionId);
	}
	
	
	public SortedSet<SurveyEntry> surveyEntry_getAll(Long surveyDefinitionId, Integer firstResult, Integer maxResults){
		return surveyEntryDAO.getAll(surveyDefinitionId,firstResult,maxResults);
	}
	
	public Long surveyEntry_getCount(Long surveyDefinitionId) {
		return surveyEntryDAO.getCount(surveyDefinitionId);
	}
	
	
	
	
	
	public List<QuestionStatistic> questionStatistic_getStatistics(Question question, Long totalRecordCount) {
		return questionStatisticDAO.getStatistics(question,totalRecordCount);
	}
	
	
	
	public SurveyStatistic surveyStatistic_get(Long surveyDefinitionId) {
		return surveyStatisticDAO.get(surveyDefinitionId);
	}
	
	
	public List<SurveyStatistic> surveyStatistic_getAll(User user) {
		if (user.isAdmin()){
			return surveyStatisticDAO.getAll();
		}
		else{
			return surveyStatisticDAO.getAll(user.getLogin());
		}
	}
	
	
	
	
	
	
	
	
	public SurveyDocument surveyDocumentDAO_findById(Long id) {
		return surveyDocumentDAO.findById(id);
	}
	public SurveyDocument surveyDocumentDAO_findBySurveyIdAndQuestionId(Long surveyId, Long questionId)  {
		return surveyDocumentDAO.findBySurveyIdAndQuestionId(surveyId,questionId);
	}
	@Transactional(readOnly = false)
	public SurveyDocument surveyDocumentDAO_merge(SurveyDocument surveyDocument) {
		
		
		return surveyDocumentDAO.merge(surveyDocument);
	}
	@Transactional(readOnly = false)
	public void surveyDocumentDAO_remove(Long id) {
		SurveyDocument surveyDocument = surveyDocumentDAO.findById(id);
		surveyDocumentDAO.remove(surveyDocument);
	}
	
	
	
}
