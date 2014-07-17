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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
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
import com.jd.survey.domain.settings.DataSet;
import com.jd.survey.domain.settings.LogicalCondition;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;




@RequestMapping("/settings/surveyDefinitionPages")
@Controller
public class SurveyDefinitionPageController {
	private static final Log log = LogFactory.getLog(SurveyDefinitionPageController.class);	

	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private UserService userService;
	@Autowired	private SecurityService securityService;

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", produces = "text/html")
	public String show(@PathVariable("id") Long id, 
					   Principal principal,
					   HttpServletRequest httpServletRequest,
					   Model uiModel) {
		log.info("show(): id=" + id);
		try {
			SurveyDefinitionPage page = surveySettingsService.surveyDefinitionPage_findById(id);
			
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			 //Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(page.getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(page.getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			/*
			for (Question question: page.getQuestions()) {
				if (question.getType()== QuestionType.DATASET_DROP_DOWN){
					DataSet dataset = surveySettingsService.dataset_findByName(question.getDataSetCode());
					uiModel.addAttribute("datasetItems" + question.getOrder(),surveySettingsService.datasetItem_findByDataSetId(dataset.getId(), 0, 10));
				}
				
			}
			*/
			uiModel.addAttribute("page", page);
			return "settings/surveyDefinitionPages/show";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}


	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "create", produces = "text/html")
	public String createGet(@PathVariable("id") Long surveyDefinitionId,
			 				Principal principal,
							Model uiModel,
							HttpServletRequest httpServletRequest) {
		try {
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			SurveyDefinition surveyDefinition =  surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			SurveyDefinitionPage surveyDefinitionPage = new SurveyDefinitionPage(surveyDefinition);
			populateEditForm(uiModel, surveyDefinitionPage,user);
			return "settings/surveyDefinitionPages/create";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String createPost(@RequestParam(value = "_proceed", required = false) String proceed,
							@Valid SurveyDefinitionPage surveyDefinitionPage, 
							BindingResult bindingResult, 
							Principal principal,
							Model uiModel, 
							HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionPage.getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(surveyDefinitionPage.getSurveyDefinition().getDepartment().getId(), user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, surveyDefinitionPage,user);
					return "settings/surveyDefinitionPages/create";
				}
				uiModel.asMap().clear();
				surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_merge(surveyDefinitionPage);
				return "settings/surveyDefinitionPages/saved";

			} 


			else{
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionPage.getSurveyDefinition().getId().toString(), httpServletRequest);

			}
		}
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}

	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "form", produces = "text/html")
	public String updateForm(@PathVariable("id") Long id, 
			 				Principal principal,
							Model uiModel) {
		try{
			User user = userService.user_findByLogin(principal.getName());	
			populateEditForm(uiModel, surveySettingsService.surveyDefinitionPage_findById(id),user);
			return "settings/surveyDefinitionPages/update";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	@RequestMapping(method = RequestMethod.PUT, produces = "text/html")
	public String update(@RequestParam(value = "_proceed", required = false) String proceed,
						 @Valid SurveyDefinitionPage surveyDefinitionPage, 
						 BindingResult bindingResult, 
						 Principal principal,
						 Model uiModel, 
						 HttpServletRequest httpServletRequest) {
		log.info("update(): handles PUT");
		try{
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionPage.getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(surveyDefinitionPage.getSurveyDefinition().getDepartment().getId(), user)	) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			if(proceed != null){
				if (bindingResult.hasErrors()) {
					populateEditForm(uiModel, surveyDefinitionPage,user);
					return "settings/surveyDefinitionPages/update";
				}
				//validate VisibilityExpression
				boolean isValid =  true; 
				ParserContext ctx = new ParserContext();
				ctx.setStrongTyping(true); 
				ctx.addInput("surveyDefinition", SurveyDefinition.class);
				uiModel.asMap().clear();
				surveyDefinitionPage =surveySettingsService.surveyDefinitionPage_merge(surveyDefinitionPage);
				return "settings/surveyDefinitionPages/saved";

			}else{

				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionPage.getSurveyDefinition().getId().toString(), httpServletRequest);


			}

		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}

	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "fork", produces = "text/html")
	public String prepareForkForm(@PathVariable("id") Long id, 
			 				Principal principal,
							Model uiModel) {
		try{
			SurveyDefinitionPage surveyDefinitionPage =surveySettingsService.surveyDefinitionPage_findById(id);
			surveyDefinitionPage.loadFromJson();
			
			uiModel.addAttribute("page", surveyDefinitionPage);
			uiModel.addAttribute("pageCount", surveyDefinitionPage.getSurveyDefinition().getPages().size());
			
			
			
			for (String key : surveyDefinitionPage.getPageLogic().getLogicalConditions().keySet()) {
				uiModel.addAttribute("lc"+key , surveyDefinitionPage.getPageLogic().getLogicalConditions().get(key));
			}
			
			
			
			return "settings/surveyDefinitionPages/fork";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/fork", method = RequestMethod.POST, produces = "text/html")
	public String updateSkipAndBranckLogic (@RequestParam(value = "_proceed", required = false) String proceed,
											@Valid SurveyDefinitionPage surveyDefinitionPage, 
											BindingResult bindingResult, 
											Principal principal,
											Model uiModel, 
											HttpServletRequest httpServletRequest) {
		log.info("create(): handles " + RequestMethod.POST.toString());
		try {
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionPage.getSurveyDefinition().getId(), user) &&
			   !securityService.userBelongsToDepartment(surveyDefinitionPage.getSurveyDefinition().getDepartment().getId(), user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			if(proceed != null){
				uiModel.asMap().clear();
				surveySettingsService.surveyDefinitionPage_updateSkipAndBranckLogic(surveyDefinitionPage);
				return "settings/surveyDefinitionPages/saved";
			} 
			else{
				return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionPage.getSurveyDefinition().getId().toString(), httpServletRequest);

			}
		}
		catch (Exception e) {
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
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(id, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}
			
			
			
			SurveyDefinitionPage surveyDefinitionPage = surveySettingsService.surveyDefinitionPage_findById(id);
			surveySettingsService.surveyDefinitionPage_remove(surveyDefinitionPage);
			uiModel.asMap().clear();
			return "redirect:/settings/surveyDefinitions/" + encodeUrlPathSegment(surveyDefinitionPage.getSurveyDefinition().getId().toString(), httpServletRequest);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}



	void populateEditForm(Model uiModel, SurveyDefinitionPage surveyDefinitionPage, User user) {
		try{
			surveyDefinitionPage.setSurveyDefinition( surveySettingsService.surveyDefinition_findById(surveyDefinitionPage.getSurveyDefinition().getId()));
			uiModel.addAttribute("surveyDefinitionPage", surveyDefinitionPage);
			uiModel.addAttribute("surveyDefinitions", surveySettingsService.surveyDefinition_findAllInternal(user));
			SurveyDefinition surveyDefinition = surveySettingsService.surveyDefinition_findById(surveyDefinitionPage.getSurveyDefinition().getId());
			uiModel.addAttribute("size", surveyDefinition.getPages().size());
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
