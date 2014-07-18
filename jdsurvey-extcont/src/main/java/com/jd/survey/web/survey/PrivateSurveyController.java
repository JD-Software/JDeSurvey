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
package com.jd.survey.web.survey;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.GlobalSettings;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyDocument;
import com.jd.survey.domain.survey.SurveyPage;
import com.jd.survey.domain.survey.SurveyStatus;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.ApplicationSettingsService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.survey.SurveyService;



@RequestMapping({"/private"})
@Controller
public class PrivateSurveyController {
	private static final Log log = LogFactory.getLog(PrivateSurveyController.class);	
	private static final String DATE_FORMAT = "date_format";
	
	private static final String REQUEST_PATH_WARNING_MESSAGE = " request path:";
	private static final String FROM_IP_WARNING_MESSAGE = " from IP Address:";
	private static final String FROM_USER_LOGIN_WARNING_MESSAGE = " attempted with user login:";
	
	private static final String UNAUTHORIZED_ATTEMPT_TO_CREATE_SURVEY_WARNING_MESSAGE = "Unauthorized Attempt to access survey definition id:";
	private static final String UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE = "Unauthorized Attempt to access survey id:";
	private static final String UNAUTHORIZED_ATTEMPT_TO_EDIT_SUBMITTED_SURVEY_WARNING_MESSAGE = "Unauthorized Attempt to edit a submitted survey:";
	
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4.xml";
	
	@Autowired	private MessageSource messageSource;
	@Autowired	private SurveyService surveyService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	@Autowired	private SecurityService securityService;
	@Autowired	private ApplicationSettingsService applicationSettingsService;
	
	
	
	
	/**
	 * Returns the survey logo image binary  
	 * @param departmentId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value = "/logo/{id}", produces = "text/html")
	public void getSurveyLogo(@PathVariable("id") Long surveyDefinitionId, 
								 Model uiModel, 
								 Principal principal,
								 HttpServletRequest httpServletRequest,
								 HttpServletResponse response) {
		try {
			uiModel.asMap().clear();
			User user = userService.user_findByLogin(principal.getName());
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToCreateSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				throw (new RuntimeException("Unauthorized access to logo"));
			}
			else {
					SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
					//response.setContentType("image/png");
					ServletOutputStream servletOutputStream = response.getOutputStream();
					servletOutputStream.write(surveyDefinition.getLogo());
					servletOutputStream.flush();	
			}
			
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	
	/**
	 * Lists all the available survey types
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(produces = "text/html",method = RequestMethod.GET)
	public String listSurveys(Model uiModel,
					   		  Principal principal,
					   		  HttpServletRequest httpServletRequest) {
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			Set<SurveyDefinition> surveyDefinitions =surveySettingsService.surveyDefinition_findAllPublishedExternal(user);
			uiModel.addAttribute("survey_base_path", "private");
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			return "surveys/surveys";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	/**
	 * Shows the survey submissions page if not entries found it will create a new one. if only one entry found will redirect to the survey show page  
	 * @param surveyDefinitionId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value = "/{id}", params = "list", produces = "text/html")
	public String listSurveyEntries(@PathVariable("id") Long surveyDefinitionId, 
									Model uiModel,
							 		Principal principal,
							 		HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToCreateSurvey(surveyDefinitionId, user)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_CREATE_SURVEY_WARNING_MESSAGE + surveyDefinitionId  + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			SurveyDefinition surveyDefinition =surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			Set<Survey> userSurveyEntries= surveyService.survey_findUserEntriesByTypeIdAndLogin(surveyDefinitionId,login);

			if (userSurveyEntries == null || userSurveyEntries.size() == 0) {
				//No User entries for this survey, create a new one
				Survey survey =surveyService.survey_create(surveyDefinitionId,login,httpServletRequest.getRemoteAddr());
				return "redirect:/private/" + encodeUrlPathSegment(survey.getId().toString(), httpServletRequest) +"/1";
			} 
			//entries found
			else { 
				if (userSurveyEntries.size() ==1) {
					//only on entry found
					Iterator<Survey> it = userSurveyEntries.iterator();
					Survey survey =it.next(); // get the first and only element in the set
					if (survey.getStatus() == SurveyStatus.I || survey.getStatus() == SurveyStatus.R) {
						//survey is incomplete or reopened
						return "redirect:/private/" + encodeUrlPathSegment(survey.getId().toString(), httpServletRequest) +"/1";	
					}
					else{
						//List all entries
						uiModel.addAttribute("survey_base_path", "private");
						uiModel.addAttribute("surveyDefinition", surveyDefinition);
						uiModel.addAttribute("userSurveyEntries", userSurveyEntries);
						return "surveys/entries";
					}
				}
				else {
					//multiple entries found
					uiModel.addAttribute("survey_base_path", "private");
					uiModel.addAttribute("surveyDefinition", surveyDefinition);
					uiModel.addAttribute("userSurveyEntries", userSurveyEntries);
					return "surveys/entries";
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	
	/**
	 * Creates a new survey based on the passed surveyDefintionId 
	 * @param surveyDefinitionId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value = "/{id}", params = "create", produces = "text/html")
	public String createSurvey(@PathVariable("id") Long surveyDefinitionId, 
						 		Model uiModel,
						 		Principal principal,
						 		HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToCreateSurvey(surveyDefinitionId, user)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_CREATE_SURVEY_WARNING_MESSAGE + surveyDefinitionId  + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			Survey survey =surveyService.survey_create(surveyDefinitionId,login, httpServletRequest.getRemoteAddr());
			return "redirect:/private/" + encodeUrlPathSegment(survey.getId().toString(), httpServletRequest) +"/1";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	/**
	 * Shows the submit page
	 * @param surveyId
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value = "submit/{id}", produces = "text/html")
	public String preparesubmitSurvey(@PathVariable("id") Long surveyId, 
							   Model uiModel,
							   Principal principal,
							   HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			Survey survey =surveyService.survey_findById(surveyId);
			//Check if the user is authorized
			if(!survey.getLogin().equals(login)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			//Check that the survey was not submitted
			if(! (survey.getStatus().equals(SurveyStatus.I)||survey.getStatus().equals(SurveyStatus.R) )) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_EDIT_SUBMITTED_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			uiModel.addAttribute("survey_base_path", "private");
			uiModel.addAttribute("survey", survey);
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(survey.getTypeId()));
			uiModel.addAttribute("surveyPages", surveyPages);
			uiModel.addAttribute("order", surveyPages.size() +1);
			return "surveys/submit";
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	/**
	 * Handles the post from the submit page
	 * @param proceed
	 * @param survey
	 * @param bindingResult
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value = "/submit", method = RequestMethod.POST,produces = "text/html")
	public String submitSurvey(@RequestParam(value = "id", required = true) Long surveyId,
			  				   @RequestParam(value = "_submit", required = false) String proceedAction,
							   Principal principal,
			 				   Model uiModel,
							   HttpServletRequest httpServletRequest){

		log.info("submitPost(): id= " + surveyId);
		
		try {
			
			String login = principal.getName();
			//Check if the user is authorized
			if(!surveyService.survey_findById(surveyId).getLogin().equals(login)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			//Check that the survey was not submitted
			Survey dbSurvey = surveyService.survey_findById(surveyId);
			if(! (dbSurvey.getStatus().equals(SurveyStatus.I)||dbSurvey.getStatus().equals(SurveyStatus.R) )) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_EDIT_SUBMITTED_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			if(proceedAction!=null){ //submit button
				uiModel.asMap().clear();
				Survey survey = surveyService.survey_submit(surveyId);
				return "redirect:/private/" + encodeUrlPathSegment(survey.getTypeId().toString(), httpServletRequest) + "?list"; 
			}
			else
			{
				uiModel.asMap().clear();
				Survey survey =surveyService.survey_findById(surveyId);
				List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
				Short order  = (short) surveyPages.size();
				return "redirect:/private/" + encodeUrlPathSegment(survey.getId().toString(), httpServletRequest) +"/" + encodeUrlPathSegment(order.toString(), httpServletRequest);
			}
				


		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}


	}

	
	/**
	 * Shows a previously submitted survey as read only
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value="/{id}", produces = "text/html")
	public String showSurvey(@PathVariable("id") Long surveyId,
							 Principal principal,
					   		 Model uiModel,
					   		 HttpServletRequest httpServletRequest) {
		log.info("showSurvey surveyId=" + surveyId + " no pageOrder");
		try{
			Survey survey =surveyService.survey_findById(surveyId);
			String login = principal.getName();
			//Check if the user is authorized
			if(!survey.getLogin().equals(login)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			if (survey.getStatus() == SurveyStatus.I) {
				return "redirect:/private/" + encodeUrlPathSegment(surveyId.toString(), httpServletRequest) +"/1";
			}
			else
			{
				uiModel.addAttribute("survey_base_path", "private");	
				uiModel.addAttribute("survey", survey);
				uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(survey.getTypeId()));
				uiModel.addAttribute("surveyPages", surveyPages);
				return "surveys/survey";
			}
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	/**
	 * Prepares to edit a survey page 
	 * @param surveyId
	 * @param order
	 * @param uiModel
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(value="/{id}/{po}", produces = "text/html")
	public String editSurveyPage(@PathVariable("id") Long surveyId, 
					   			 @PathVariable("po") Short order,
					   			 Model uiModel,
					   			 Principal principal,
						   		 HttpServletRequest httpServletRequest) {
		log.info("editSurveyPage surveyId=" + surveyId + " pageOrder" + order);
		try{
			String login = principal.getName();
			Survey survey =surveyService.survey_findById(surveyId);
			//Check if the user is authorized
			if(!survey.getLogin().equals(login)) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			//Check that the survey was not submitted
			if(! (survey.getStatus().equals(SurveyStatus.I)||survey.getStatus().equals(SurveyStatus.R) )) {
				log.warn(UNAUTHORIZED_ATTEMPT_TO_EDIT_SUBMITTED_SURVEY_WARNING_MESSAGE + surveyId + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
				return "accessDenied";	
				
			}
			
			
			SurveyPage surveyPage  = surveyService.surveyPage_get(surveyId, order,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			
			//randomize the questions order
			if (surveyPage.getRandomizeQuestions()) { Collections.shuffle(surveyPage.getQuestionAnswers(), new Random(System.nanoTime()));}
			
			//randomize the questions options orders
			for (QuestionAnswer questionAnswer : surveyPage.getQuestionAnswers()) {
				questionAnswer.getQuestion().setOptionsList(new ArrayList<QuestionOption>(questionAnswer.getQuestion().getOptions()));
				if (questionAnswer.getQuestion().getRandomizeOptions()) {
					Collections.shuffle(questionAnswer.getQuestion().getOptionsList(), new Random(System.nanoTime()));
				}
			}
			
			List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			uiModel.addAttribute("survey_base_path", "private");
			uiModel.addAttribute("survey", surveyPage.getSurvey());
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyPage.getSurvey().getTypeId()));
			
			uiModel.addAttribute("surveyPage", surveyPage);
			uiModel.addAttribute("surveyPages", surveyPages);
			return "surveys/page";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	/**
	 * Updates a survey page 
	 * @param surveyPage
	 * @param backAction
	 * @param proceedAction
	 * @param bindingResult
	 * @param uiModel
	 * @param principal
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN", "ROLE_SURVEY_PARTICIPANT"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String updateSurveyPage(@Valid SurveyPage surveyPage, 
			@RequestParam(value = "_back", required = false) String backAction,
			@RequestParam(value = "_proceed", required = false) String proceedAction,
			BindingResult bindingResult,
			Model uiModel,
			Principal principal,
			HttpServletRequest httpServletRequest) {
	try {
		String login = principal.getName();
			Short order = surveyPage.getOrder();
			if(proceedAction!=null){ //next button
				Survey survey  = surveyService.survey_findById(surveyPage.getSurvey().getId());
				//Check if the user is authorized
				if(!survey.getLogin().equals(login)) {
					log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + survey.getId() + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
					return "accessDenied";	
					
				}
				
				//Check that the survey was not submitted
				if(! (survey.getStatus().equals(SurveyStatus.I)||survey.getStatus().equals(SurveyStatus.R) )) {
					log.warn(UNAUTHORIZED_ATTEMPT_TO_EDIT_SUBMITTED_SURVEY_WARNING_MESSAGE + survey.getId() + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
					return "accessDenied";	
					
				}
				
				List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyPage.getSurvey().getId(),messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
				surveyPage.setSurvey(survey);
				surveyPage = surveyService.surveyPage_updateSettings(surveyPage);
				
				//populate the uploaded files
				MultipartHttpServletRequest multiPartRequest = (MultipartHttpServletRequest) httpServletRequest;
				Iterator<String>  fileNames = multiPartRequest.getFileNames();
				while (fileNames.hasNext()) {
					String fileName= fileNames.next();
					Long questionId = Long.parseLong(fileName.toUpperCase().replace("FILE", ""));
					for (QuestionAnswer questionAnswer: surveyPage.getQuestionAnswers()) {
						if (questionAnswer.getQuestion().getId().equals(questionId) &&
							multiPartRequest.getFile(fileName).getBytes().length > 0) {
							questionAnswer.setSurveyDocument(new SurveyDocument (survey.getId(),
																			   questionId,
																			   multiPartRequest.getFile(fileName).getName(), 
																			   multiPartRequest.getFile(fileName).getContentType(),
																			   multiPartRequest.getFile(fileName).getBytes()));
						}
					}
				}
				
				Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy as = new AntiSamy();
				for (QuestionAnswer questionAnswer : surveyPage.getQuestionAnswers()){
					if (questionAnswer.getQuestion().getType().getIsTextInput()){
						CleanResults cr = as.scan(questionAnswer.getStringAnswerValue(), policy);
						questionAnswer.setStringAnswerValue(cr.getCleanHTML());
					}
				}
				
				GlobalSettings globalSettings = applicationSettingsService.getSettings();
				
				SurveyPageValidator validator = new SurveyPageValidator(messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()),
																		globalSettings.getValidContentTypes(),
																		globalSettings.getValidImageTypes(),
																		globalSettings.getMaximunFileSize(),
																		globalSettings.getInvalidContentMessage(),
																		globalSettings.getInvalidFileSizeMessage()
																		);
				validator.validate(surveyPage, bindingResult);
				if (bindingResult.hasErrors()) {
					/*
					for (ObjectError err :bindingResult.getAllErrors()) {
						log.info("getObjectName:" + err.getObjectName() + " getCode:" + err.getCode() + " getDefaultMessage:" + err.getDefaultMessage());
						log.info("toString:" + err.toString());
					} 
					*/
					uiModel.addAttribute("survey_base_path", "private");
					uiModel.addAttribute("survey", survey);
					uiModel.addAttribute("surveyPage", surveyPage);
					uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyPage.getSurvey().getTypeId()));
					uiModel.addAttribute("surveyPages", surveyPages);
					return "surveys/page";
				}  
				
				
			
				surveyService.surveyPage_update(surveyPage, messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
				//get the survey pages from the database again, prvious call updates visibility when there is  branching logic 
				surveyPages = surveyService.surveyPage_getAll(surveyPage.getSurvey().getId(),messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
				order = getNextPageOrder(surveyPages,order);

				if (order.equals((short)0)){
					//Submit page
					uiModel.asMap().clear();
					return "redirect:/private/submit/" + encodeUrlPathSegment(surveyPage.getSurvey().getId().toString(), httpServletRequest);	
				}
				else{
					//go to the next page
					uiModel.asMap().clear();
					return "redirect:/private/" + encodeUrlPathSegment(surveyPage.getSurvey().getId().toString(), httpServletRequest) +"/" + encodeUrlPathSegment(order.toString(), httpServletRequest);	
				}


			}
			else {//back button
				Survey survey =surveyService.survey_findById(surveyPage.getSurvey().getId());
				//Check if the user is authorized
				if(!survey.getLogin().equals(login)) {
					log.warn(UNAUTHORIZED_ATTEMPT_TO_ACCESS_SURVEY_WARNING_MESSAGE + survey.getId() + REQUEST_PATH_WARNING_MESSAGE + httpServletRequest.getPathInfo() + FROM_USER_LOGIN_WARNING_MESSAGE + principal.getName() + FROM_IP_WARNING_MESSAGE + httpServletRequest.getLocalAddr());
					return "accessDenied";	
					
				}
				List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyPage.getSurvey().getId(),messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
				order = getPreviousPageOrder(surveyPages,order);
				if (order.equals((short)0)){
					//Go to the surveyEntries page
					uiModel.asMap().clear();
					return "redirect:/private/" + encodeUrlPathSegment(survey.getTypeId().toString(), httpServletRequest) +"?list";
				}
				else{
					//go to previous page
					uiModel.asMap().clear();
					return "redirect:/private/" + encodeUrlPathSegment(survey.getId().toString(), httpServletRequest) +"/" + encodeUrlPathSegment(order.toString(), httpServletRequest);

				}
			}		

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Helper method that computes the next page
	 * @param survey
	 * @param thisPageOrder
	 * @return
	 */
	private Short getNextPageOrder(List<SurveyPage> surveyPages,Short thisPageOrder) {
		try {
			
			if (thisPageOrder.equals((short) surveyPages.size())) {
				return 0; // go to the submit page
			}
			//The next page is the first visible page after this order
			for (SurveyPage surveyPage : surveyPages){
				if (surveyPage.getOrder() > thisPageOrder)
				{
					if (surveyPage.getVisible()) {
						log.info("getNextPageOrder=" + surveyPage.getOrder());
						return surveyPage.getOrder();
					}
				}
			}
			return 0; // go to the submit page
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	/**
	 * Helper method that computes the previous page
	 * @param survey
	 * @param thisPageOrder
	 * @return
	 */
	private Short getPreviousPageOrder(List<SurveyPage> surveyPages, Short thisPageOrder) {
		try {
			if (thisPageOrder.equals((short) 1)) {
				return 0;
			}
			//The previous page is the first visible page before this order
			for (int i= thisPageOrder-1 ; i>=0;  i--){
				SurveyPage surveyPage = surveyPages.get(i);
				if (surveyPage.getOrder() < thisPageOrder)
				{
					if (surveyPage.getVisible()) {
						log.info("getPreviousPageOrder=" + surveyPage.getOrder());
						return surveyPage.getOrder();
					}
				}
			}
			
			return null;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	

	String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
		log.info("encodeUrlPathSegment()");
		try{
			String enc = httpServletRequest.getCharacterEncoding();
			if (enc == null) {
				enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
			}
			try {
				pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
			} catch (UnsupportedEncodingException uee) {log.error(uee);}
			return pathSegment;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException e, HttpServletRequest request) {
		if (e.getClass() == AccessDeniedException.class) {
			return "accessDenied";
		} else {
			return "uncaughtException";
		}
		
	}




}
