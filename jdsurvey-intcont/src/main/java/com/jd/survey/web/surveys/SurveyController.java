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
package com.jd.survey.web.surveys;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.DataSet;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.Survey;
import com.jd.survey.domain.survey.SurveyDocument;
import com.jd.survey.domain.survey.SurveyEntry;
import com.jd.survey.domain.survey.SurveyPage;
import com.jd.survey.domain.survey.SurveyStatistic;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.survey.SurveyService;


@RequestMapping("/surveys")
@Controller
public class SurveyController {
	private static final Log log = LogFactory.getLog(SurveyController.class);
	private static final String DATE_FORMAT = "date_format";
	
	@Autowired	private MessageSource messageSource;
	@Autowired	private UserService userService;
	@Autowired	private SurveyService surveyService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private SecurityService securityService;

	
	
	
	/**
	 * Shows a list of Survey Definitions 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(produces = "text/html",method = RequestMethod.GET)
	public String listSurveys(Model uiModel, Principal principal) {
		try{
			User user = userService.user_findByLogin(principal.getName());
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			return "surveys/entries";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	/**
	 * Shows a list of Survey Entries for a Survey Definition, Supports Paging 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/list", produces = "text/html",method = RequestMethod.GET)
	public String listSurveyEntries(@RequestParam(value = "id", required = true) Long surveyDefinitionId,
									@RequestParam(value = "page", required = false) Integer page, 
					  				@RequestParam(value = "size", required = false) Integer size, 
									Model uiModel,
									Principal principal,
									HttpServletRequest httpServletRequest) {
		try{
			User user = userService.user_findByLogin(principal.getName());
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}				
				
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			uiModel.addAttribute("surveyDefinition", surveySettingsService.surveyDefinition_findById(surveyDefinitionId));
			uiModel.addAttribute("surveyStatistic", surveyService.surveyStatistic_get(surveyDefinitionId));
			int sizeNo = size == null ? 25 : size.intValue();
			final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
			Set<SurveyEntry> surveyEntries= surveyService.surveyEntry_getAll(surveyDefinitionId,firstResult,sizeNo);
			float nrOfPages = (float) surveyService.surveyEntry_getCount(surveyDefinitionId) / sizeNo;
			int maxPages = (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages);
			uiModel.addAttribute("maxPages", maxPages);
			uiModel.addAttribute("surveyEntries", surveyEntries);
			return "surveys/entries";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	
	/**
	 * Shows a single Survey 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/{id}",params = "show", produces = "text/html")
	public String showSurvey(@PathVariable("id") Long surveyId,
							 Principal principal,
					   		 Model uiModel,
					   		 HttpServletRequest httpServletRequest) {
		log.info("showSurvey surveyId=" + surveyId + " no pageOrder");
		try{
			
			//Survey survey =surveyService.Survey_findById(surveyId);
			User user = userService.user_findByLogin(principal.getName());
			SurveyEntry surveyEntry= surveyService.surveyEntry_get(surveyId);
			
			if(!securityService.userIsAuthorizedToManageSurvey(surveyEntry.getSurveyDefinitionId(), user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}	
			List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			uiModel.addAttribute("surveyEntry", surveyEntry);
			uiModel.addAttribute("surveyPages", surveyPages);
			return "surveys/survey";
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	/**
	 * export the single Survey to a PDF 
	 * @param surveyId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/{id}",params = "pdf", produces = "text/html")
	public ModelAndView  exportSurveyToPdf(@PathVariable("id") Long surveyId,
										 	Principal principal,
										 	HttpServletRequest httpServletRequest,
										 	HttpServletResponse response) {
		log.info("showSurvey surveyId=" + surveyId + " no pageOrder");
		try{
			
			SurveyEntry surveyEntry= surveyService.surveyEntry_get(surveyId);
			//SurveyDefinition surveyDefinition= surveySettingsService.surveyDefinition_findById(surveyEntry.getSurveyDefinitionId());
			List<SurveyPage> surveyPages = surveyService.surveyPage_getAll(surveyId,messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			
			ModelAndView modelAndView =new ModelAndView("surveyPdf");
			modelAndView.addObject("surveyEntry",surveyEntry);
			//modelAndView.addObject("surveyDefinition",surveyDefinition);
			modelAndView.addObject("surveyPages",surveyPages);
			
			modelAndView.addObject("falseMessage",messageSource.getMessage("false_message", null, LocaleContextHolder.getLocale()));  
			modelAndView.addObject("trueMessage",messageSource.getMessage("true_message", null, LocaleContextHolder.getLocale()));
			modelAndView.addObject("dateFormat",messageSource.getMessage("date_format", null, LocaleContextHolder.getLocale()));
			
			Map<String,String> surveyEntryLabels = new HashMap<String,String>();
			surveyEntryLabels.put("surveyEntryLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("surveyNameLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.surveyname_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("surveyIdLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.surveyid_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("createdByLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.createdbyfullname_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("creationDateLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.creationdate_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("lastUpdateDateLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.lastupdatedate_label", null, LocaleContextHolder.getLocale()));
			surveyEntryLabels.put("submissionDateLabel", messageSource.getMessage("com.jd.survey.domain.surveyentry.submissiondate_label", null, LocaleContextHolder.getLocale()));
			modelAndView.addObject("surveyEntryLabels",surveyEntryLabels);
			
			return modelAndView;
			
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	/**
	 * Shows a list of surveys for export to different formats
	 * @param uiModel
	 * @param principal
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/report" , produces = "text/html",method = RequestMethod.GET)
	public String listSurveysForExport(Model uiModel, Principal principal) {
		try{
			User user = userService.user_findByLogin(principal.getName());
			List<SurveyStatistic> surveyStatistics = surveyService.surveyStatistic_getAll(user);
			uiModel.addAttribute("surveyStatistics", surveyStatistics);
			return "surveys/surveys";
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	
	
	
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/doc/{Id}", produces = "text/html")
	public void getQuestionDocument(@PathVariable("Id") Long surveyDocumentId,
									Principal principal,				
									HttpServletResponse response) {
		try {
			SurveyDocument surveyDocument =  surveyService.surveyDocumentDAO_findById(surveyDocumentId);
			
			response.setContentType(surveyDocument.getContentType());
			// Set standard HTTP/1.1 no-cache headers.
			response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			// Set standard HTTP/1.0 no-cache header.
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=" + surveyDocument.getFileName());
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(surveyDocument.getContent());
			servletOutputStream.flush();
		} 
		catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
