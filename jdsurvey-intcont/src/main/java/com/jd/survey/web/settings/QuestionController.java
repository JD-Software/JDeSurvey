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
package com.jd.survey.web.settings;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.SurveyDefinitionStatus;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/settings/questions")
@Controller
public class QuestionController {
	private static final Log log = LogFactory.getLog(QuestionController.class);	
	private static final String EXTREMELY_UNSATISFIED_LABEL = "extremely_unsatisfied_label";
	private static final String UNSATISFIED_LABEL = "unsatisfied_label";
	private static final String NEUTRAL_LABEL = "neutral_label";
	private static final String SATISFIED_LABEL = "satisfied_label";
	private static final String EXTREMELY_SATISFIED_LABEL = "extremely_satisfied_label";
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4.xml";
	
	private static short size;

		
	@Autowired	private MessageSource messageSource;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	@Autowired	private SecurityService securityService;

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "create", produces = "text/html")
	public String createQuestion(@PathVariable("id") Long surveyDefinitionPageId, 
								Principal principal,	
								Model uiModel,
								HttpServletRequest httpServletRequest) {
		log.info("createForm(): handles param form");
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); 
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionPage.getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(surveyDefinitionPage.getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			//User user = userService.user_findByLogin(principal.getName());
			//SurveyDefinitionPage surveyDefinitionPage =  surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId);
			Question question = new Question(surveyDefinitionPage);
			size = (short) question.getPage().getQuestions().size();
			populateEditForm(uiModel, question, user);

			return "settings/questions/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid Question question, 
						BindingResult bindingResult, 
						Principal principal,	
						Model uiModel, 
						HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); 
			//Check if the user is authorized
			
			if(!securityService.userIsAuthorizedToManageSurvey(question.getPage().getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(question.getPage().getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
			return "accessDenied";	
		}
			//User user = userService.user_findByLogin(principal.getName());
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, question, user);
					return "settings/questions/create";
				}
				
				if (!surveySettingsService.question_ValidateDateRange(question)){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("dateMinimum", "date_format_validation_range");
					return "settings/questions/create";	
				}	
				//validate Double min max	
				if (!surveySettingsService.question_ValidateMinMaxDoubleValues(question)){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("decimalMinimum", "field_min_invalid");
					return "settings/questions/create";	
				}	
				//validate Integer min max	
				if (!surveySettingsService.question_ValidateMinMaxValues(question)){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("integerMinimum", "field_min_invalid");
					return "settings/questions/create";	
				}	
				if (question.getType().getIsRating()) {
					SortedSet<QuestionOption> options = new TreeSet<QuestionOption>();
					options.add(new  QuestionOption(question, (short)1 ,"1",messageSource.getMessage(EXTREMELY_UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
					options.add(new  QuestionOption(question, (short)2 ,"2",messageSource.getMessage(UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
					options.add(new  QuestionOption(question, (short)3 ,"3",messageSource.getMessage(NEUTRAL_LABEL, null, LocaleContextHolder.getLocale())));
					options.add(new  QuestionOption(question, (short)4 ,"4",messageSource.getMessage(SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
					options.add(new  QuestionOption(question, (short)5 ,"5",messageSource.getMessage(EXTREMELY_SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
					question =surveySettingsService.question_merge(question,options);	
				}
				
//				if (question.getPublishToSocrata().equals(true)){
//					bindingResult.rejectValue("socrataColumnName", "field_min_invalid");
//					return "settings/questions/create";	
//					}
				
				else {
					
					Policy questionTextPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
					AntiSamy emailAs = new AntiSamy();
					CleanResults crQuestionText = emailAs.scan(question.getQuestionText(), questionTextPolicy);
					question.setQuestionText(crQuestionText.getCleanHTML());
					
					Policy questionTipPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
					AntiSamy completedSurveyAs = new AntiSamy();
					CleanResults crQuestionTip = completedSurveyAs.scan(question.getTip(), questionTipPolicy);
					question.setTip(crQuestionTip.getCleanHTML());
					
					question =surveySettingsService.question_merge(question);
				
				} 	
				uiModel.asMap().clear();
				return "settings/questions/saved";
			}

			else {
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(question.getPage().getSurveyDefinition().getId().toString(), httpServletRequest);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id,
						HttpServletRequest httpServletRequest,
						Principal principal,	
						Model uiModel) {
		log.info("show(): id=" + id);
		try {
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(id); 
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(id, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			uiModel.addAttribute("question", surveySettingsService.question_findById(id));
			uiModel.addAttribute("itemId", id);
			return "settings/questions/show";   
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/lookup", produces = "text/html")
	public String lookup(Principal principal,
			 Question question,
			 Model uiModel, 
			 HttpServletRequest httpServletRequest) {
		uiModel.addAttribute("question",question);
		return "settings/questions/lookup";
		
	}
	
	
	

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						@Valid Question question,
						BindingResult bindingResult,
						Principal principal,	
						Model uiModel, 
						HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			//User user = userService.user_findByLogin(principal.getName());
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			
			//SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); surveySettingsService.question_findById(question.getId()).getPage().getSurveyDefinition().getId()
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(question.getPage().getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(question.getPage().getSurveyDefinition().getDepartment().getId(), user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			if(proceed != null ){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, question,user);
					log.info("-------------------------------------------" +bindingResult.getFieldErrors().toString());
					return "settings/questions/update";
				}
				if (!surveySettingsService.question_ValidateDateRange(question)){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("dateMinimum", "date_format_validation_range");
					return "settings/questions/update";	
				}	
				if (!surveySettingsService.question_ValidateMinMaxDoubleValues(question) ){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("decimalMinimum", "field_min_invalid");
					return "settings/questions/update";	
				}	
				if (!surveySettingsService.question_ValidateMinMaxValues(question) ){
					populateEditForm(uiModel, question,user);
					bindingResult.rejectValue("integerMinimum", "field_min_invalid");
					return "settings/questions/update";	
				}
				if (question.getSuportsOptions()){
					//If user wants to modify and existent question without options to Rating type, then use the default values
					int NumberOfQuestionOptions = 0;
					Set<QuestionOption> qOpts = surveySettingsService.questionOption_findByQuestionId(question.getId());
					for (QuestionOption q : qOpts){
						NumberOfQuestionOptions++;
					}
					if ((question.getType().toString()=="SMILEY_FACES_RATING" || question.getType().toString()=="STAR_RATING") && NumberOfQuestionOptions != 5){
						log.info("Removing Question Options since the amount of Questions Options for Rating Type cannot be longer than 5 Qoptions");
						surveySettingsService.questionOption_removeQuestionOptionsByQuestionId(question.getId());
						SortedSet<QuestionOption> options = new TreeSet<QuestionOption>();
						options.add(new  QuestionOption(question, (short)1 ,"1",messageSource.getMessage(EXTREMELY_UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
						options.add(new  QuestionOption(question, (short)2 ,"2",messageSource.getMessage(UNSATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
						options.add(new  QuestionOption(question, (short)3 ,"3",messageSource.getMessage(NEUTRAL_LABEL, null, LocaleContextHolder.getLocale())));
						options.add(new  QuestionOption(question, (short)4 ,"4",messageSource.getMessage(SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
						options.add(new  QuestionOption(question, (short)5 ,"5",messageSource.getMessage(EXTREMELY_SATISFIED_LABEL, null, LocaleContextHolder.getLocale())));
						//Adding default values to Rating Type Question
						log.info("Adding default values to Rating Type Question");
						question =surveySettingsService.question_merge(question,options);
						uiModel.asMap().clear();
						return "settings/questions/saved";
					}
					else{
						Policy questionTextPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
						AntiSamy emailAs = new AntiSamy();
						CleanResults crQuestionText = emailAs.scan(question.getQuestionText(), questionTextPolicy);
						question.setQuestionText(crQuestionText.getCleanHTML());
						
						Policy questionTipPolicy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
						AntiSamy completedSurveyAs = new AntiSamy();
						CleanResults crQuestionTip = completedSurveyAs.scan(question.getTip(), questionTipPolicy);
						question.setTip(crQuestionTip.getCleanHTML());
						
						question =surveySettingsService.question_merge(question);
						uiModel.asMap().clear();
						return "settings/questions/saved";
					}
				}
				
				question =surveySettingsService.question_merge(question);
				uiModel.asMap().clear();
				return "settings/questions/saved";
			
			}else{
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(question.getPage().getSurveyDefinition().getId().toString(), httpServletRequest);
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long id,
							Principal principal,	
							Model uiModel) {
		log.info("updateForm(): id=" + id);
		try{
			User user = userService.user_findByLogin(principal.getName());
			populateEditForm(uiModel, surveySettingsService.question_findById(id),user);
			return "settings/questions/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
	public String delete(@PathVariable("id") Long id, 
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("delete(): id=" + id);
		try {
			Question question = surveySettingsService.question_findById(id);
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(surveyDefinitionPageId); 
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(question.getPage().getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(question.getPage().getSurveyDefinition().getDepartment().getId(), user)		) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			surveySettingsService.question_remove(id);
			uiModel.asMap().clear();
			return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(question.getPage().getSurveyDefinition().getId().toString(), httpServletRequest);

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	void populateEditForm(Model uiModel, Question question, User user) {
		log.info("populateEditForm()");
		try{
			short i = (short)question.getPage().getQuestions().size();
			uiModel.addAttribute("question", question);
			uiModel.addAttribute("regularExpressions", surveySettingsService.regularExpression_findAll());
			uiModel.addAttribute("questionOptions", question.getType());
			uiModel.addAttribute("datasets", surveySettingsService.dataSet_findAll());
			uiModel.addAttribute("surveyDefinitionPages", surveySettingsService.surveyDefinitionPage_findAll());
			if (i!= 0){
				uiModel.addAttribute("size", i);
			}
			else {
				uiModel.addAttribute("size", size);
			}

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
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}

}
