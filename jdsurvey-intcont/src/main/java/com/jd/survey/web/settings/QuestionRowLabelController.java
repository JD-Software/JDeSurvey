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
import java.util.SortedSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;


@RequestMapping("/settings/questionRows")
@Controller
public class QuestionRowLabelController {
	private static final Log log = LogFactory.getLog(QuestionRowLabelController.class);	
	
	private static final int EMPTY_OPTIONS_COUNT = 10;
	
	@Autowired	private SecurityService securityService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;

	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long questionId, 
							Principal principal,	
							Model uiModel,
							HttpServletRequest httpServletRequest) {
		try{
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			Question question = surveySettingsService.question_findById(questionId);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(question.getPage().getSurveyDefinition().getId(), user) &&
		       !securityService.userBelongsToDepartment(question.getPage().getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			SortedSet<QuestionRowLabel> RowLabels =  question.getRowLabels();
			log.info("initial set size" + RowLabels.size());
			for (int i =1; i<=EMPTY_OPTIONS_COUNT; i++){
				
				log.info("adding to set" + i); 
				RowLabels.add(new QuestionRowLabel(question,(short) (question.getRowLabels().size() + i)));
			}
			question.setRowLabels(RowLabels);
			uiModel.addAttribute("question", question);
			return "settings/questionRows/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(Question question, 
							 BindingResult bindingResult,
							 @RequestParam(value="_proceed", required = false) String proceed,
							 Principal principal,	
							 Model uiModel, 
							 HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {
			
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveySettingsService.question_findById(question.getId()).getPage().getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(surveySettingsService.question_findById(question.getId()).getPage().getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				boolean isValid=true;
				for (int i = 0; i<question.getRowLabelsList().size(); i ++) {
					if (question.getRowLabelsList().get(i).getLabel() != null	&&	question.getRowLabelsList().get(i).getLabel().trim().length() > 0){
							
						if (question.getRowLabelsList().get(i).getLabel().trim().length() ==0	||
								question.getRowLabelsList().get(i).getLabel().trim().length() > 75 ){
							bindingResult.rejectValue("rowLabelsList["+ i +"].label", "invalidEntry");
							isValid= false;
							
						} 
					}
					else{
						//User is trying to save an empty MC form
						if (i==0){
							bindingResult.rejectValue("rowLabelsList["+ i +"].label", "invalidEntry");
							isValid= false;
						}
					}
				}
	
				if (!isValid){
					return "settings/questionRows/update";	
				}
				else{
					question = surveySettingsService.question_updateRowLabels(question);
					return "settings/questionRows/saved";
						
				}
			}
			else{
				question = surveySettingsService.question_updateRowLabels(question);
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(question.getPage().getSurveyDefinition().getId().toString(), httpServletRequest);
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
