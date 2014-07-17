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
package com.jd.survey.web.reports;

import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jd.survey.dao.interfaces.survey.ReportDAO;
import com.jd.survey.domain.security.User;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.util.SPSSHelperService;


@RequestMapping("/reports")
@Controller
public class ReportController {
	private static final Log log = LogFactory.getLog(ReportController.class);

	@Autowired	private SecurityService securityService;
	@Autowired	private UserService userService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private ReportDAO reportDAO;
	@Autowired	private SPSSHelperService sPSSHelperService;
	@Autowired	private MessageSource messageSource;
	
	
	
	
	private static final String SURVEY_ID = "com.jd.survey.domain.survey_label_short";
	private static final String SURVEY_NAME = "com.jd.survey.domain.settings.surveydefinition.name_label";
	private static final String FIRST_NAME = "com.jd.survey.domain.security.user.firstname_label";
	private static final String MIDDLE_NAME = "com.jd.survey.domain.security.user.middlename_label";
	private static final String LAST_NAME = "com.jd.survey.domain.security.user.lastname_label";
	private static final String SUBMISSION_DATE = "com.jd.survey.domain.survey.submissiondate_label";
	private static final String CREATION_DATE = "com.jd.survey.domain.surveyentry.creationdate_label";
	private static final String LAST_UPDATE = "com.jd.survey.domain.surveyentry.lastupdatedate_label";
	private static final String IP_ADDRESS = "com.jd.survey.domain.surveyentry.createdbyipaddress_label";
	 
	
	

	/**
	 * Exports survey data to an Excel file
	 * @param surveyDefinitionId
	 * @param principal
	 * @param uiModel
	 * @param httpServletRequest
	 * @return
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "excel", produces = "text/html")
	public ModelAndView surveyExcelExport(@PathVariable("id") Long surveyDefinitionId,
			Principal principal,
			Model uiModel,
			HttpServletResponse response,
			HttpServletRequest httpServletRequest)  {
		try{
			SurveyDefinition surveyDefinition= surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			List<Map<String,Object>> surveys = reportDAO.getSurveyData(surveyDefinitionId);
			
			Map<String,String> messages =  new TreeMap<String,String>();
			
			messages.put("surveyId", messageSource.getMessage(SURVEY_ID, null, LocaleContextHolder.getLocale()));
			messages.put("surveyName",messageSource.getMessage(SURVEY_NAME, null, LocaleContextHolder.getLocale()) );
			messages.put("firstname", messageSource.getMessage(FIRST_NAME, null, LocaleContextHolder.getLocale()));
			messages.put("middlename", messageSource.getMessage(MIDDLE_NAME, null, LocaleContextHolder.getLocale()));
			messages.put("lastname", messageSource.getMessage(LAST_NAME, null, LocaleContextHolder.getLocale()));
			messages.put("submissionDate", messageSource.getMessage(SUBMISSION_DATE, null, LocaleContextHolder.getLocale()));
			messages.put("creationDate", messageSource.getMessage(CREATION_DATE, null, LocaleContextHolder.getLocale()));
			messages.put("lastUpdateDate", messageSource.getMessage(LAST_UPDATE, null, LocaleContextHolder.getLocale()));
			messages.put("ipaddress", messageSource.getMessage(IP_ADDRESS, null, LocaleContextHolder.getLocale()));
			
			String login = principal.getName();
			User user = userService.user_findByLogin(login);
			//Check if the user is authorized
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				response.sendRedirect("../accessDenied");
				
			}
			
			ModelAndView modelAndView =new ModelAndView("surveyList");
			modelAndView.addObject("surveyDefinition",surveyDefinition);
			modelAndView.addObject("surveys",surveys);
			modelAndView.addObject("messages",messages);
			return modelAndView;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Exports survey data to a comma delimited values file
	 * @param surveyDefinitionId
	 * @param principal
	 * @param response
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "csv", produces = "text/html")
	public void surveyCSVExport(@PathVariable("id") Long surveyDefinitionId,
								Principal principal,	
								HttpServletRequest httpServletRequest,
								HttpServletResponse response)  {
		try{

			
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				response.sendRedirect("../accessDenied");
				//throw new AccessDeniedException("Unauthorized access attempt");
			}
			
			String columnName;
			SurveyDefinition surveyDefinition= surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			List<Map<String,Object>> surveys = reportDAO.getSurveyData(surveyDefinitionId);

			StringBuilder stringBuilder = new StringBuilder();

			stringBuilder.append("\"id\",\"Survey Name\",\"User Login\",\"Submission Date\",\"Creation Date\",\"Last Update Date\",");
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				for(Question question :page.getQuestions()) {
					if (question.getType().getIsMatrix()){
						for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								stringBuilder.append("\" p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + 
										"c" +	questionColumnLabel.getOrder() + "\",")	;
							}
						}
						continue;
					}

					if (question.getType().getIsMultipleValue() ){
						for( QuestionOption questionOption: question.getOptions()){
							stringBuilder.append("\" p" + page.getOrder()+"q" +question.getOrder() + "o" + questionOption.getOrder() + "\",");

						}
						continue;
					}
					stringBuilder.append("\"p" + page.getOrder()+"q" +question.getOrder() + "\",");
				}
			}

			
			stringBuilder.deleteCharAt(stringBuilder.length()-1); //delete the last comma
			stringBuilder.append("\n");

			for (Map<String,Object> record : surveys){
				stringBuilder.append(record.get("survey_id") == null ? "" : "\"" + record.get("survey_id").toString().replace("\"", "\"\"") +"\"," );
				stringBuilder.append(record.get("type_name") == null ? "" : "\"" + record.get("type_name").toString().replace("\"", "\"\"") +"\"," );
				stringBuilder.append(record.get("login") == null ? "" : "\"" + record.get("login").toString().replace("\"", "\"\"") +"\"," );
				stringBuilder.append(record.get("submission_date") == null ? "" : "\"" + record.get("creation_date").toString().replace("\"", "\"\"") +"\"," );
				stringBuilder.append(record.get("creation_date") == null ? "" : "\"" + record.get("last_update_date").toString().replace("\"", "\"\"") +"\"," );
				stringBuilder.append(record.get("last_update_date") == null ? "" : "\"" + record.get("last_update_date").toString().replace("\"", "\"\"") +"\"," );

				for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
					for(Question question :page.getQuestions()) {
						if (question.getType().getIsMatrix()){
							for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() +  "r" + questionRowLabel.getOrder() + "c" +	questionColumnLabel.getOrder();
									stringBuilder.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							continue;
						}
						if (question.getType().getIsMultipleValue() ){
							for( QuestionOption questionOption: question.getOptions()){
								columnName = "p" + page.getOrder()+"q" +question.getOrder() + "o" +questionOption.getOrder();
								stringBuilder.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							}
							continue;
						}
						columnName = "p" + page.getOrder()+"q" +question.getOrder();
						stringBuilder.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );

					}	
				}
				stringBuilder.deleteCharAt(stringBuilder.length()-1); //delete the last comma
				stringBuilder.append("\n");
			}

			//Zip file manipulations Code
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ZipEntry zipentry; 
			ZipOutputStream zipfile = new ZipOutputStream(bos);  
			zipentry = new ZipEntry("survey" + surveyDefinition.getId()  +".csv");    
			zipfile.putNextEntry(zipentry);  
			zipfile.write(stringBuilder.toString().getBytes("UTF-8"));  
			zipfile.close();


			//response.setContentType("text/html; charset=utf-8");
			response.setContentType("application/octet-stream");
			// Set standard HTTP/1.1 no-cache headers.
			response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			// Set standard HTTP/1.0 no-cache header.
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=survey" + surveyDefinition.getId()  +".zip");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			//servletOutputStream.write(stringBuilder.toString().getBytes("UTF-8"));
			servletOutputStream.write(bos.toByteArray());
			servletOutputStream.flush();

		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value = "/{id}", params = "spss", produces = "text/html")
	public void surveySPSSExport(@PathVariable("id") Long surveyDefinitionId,
			Principal principal,
			HttpServletRequest httpServletRequest,
			HttpServletResponse response)  {
		try{
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				response.sendRedirect("../accessDenied");
				//throw new AccessDeniedException("Unauthorized access attempt");
			}
			
			String metadataFileName ="survey" + surveyDefinitionId + ".sps";
			String dataFileName = "survey" + surveyDefinitionId + ".dat";

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zipfile = new ZipOutputStream(baos);  

			//metadata    
			zipfile.putNextEntry(new ZipEntry(metadataFileName));  
			zipfile.write(sPSSHelperService.getSurveyDefinitionSPSSMetadata(surveyDefinitionId, dataFileName));
			//data
			zipfile.putNextEntry(new ZipEntry(dataFileName));  
			zipfile.write(sPSSHelperService.getSurveyDefinitionSPSSData(surveyDefinitionId));
			zipfile.close();

			//response.setContentType("text/html; charset=utf-8");
			response.setContentType("application/octet-stream");
			// Set standard HTTP/1.1 no-cache headers.
			response.setHeader("Cache-Control", "no-store, no-cache,must-revalidate");
			// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			// Set standard HTTP/1.0 no-cache header.
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Content-Disposition", "inline;filename=survey" + surveyDefinitionId + "_spss.zip");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			//servletOutputStream.write(stringBuilder.toString().getBytes("UTF-8"));
			servletOutputStream.write(baos.toByteArray());
			servletOutputStream.flush();

		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}



	@ExceptionHandler(RuntimeException.class)
	public String handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
		log.error(ex);
		log.error("redirect to /uncaughtException");
		return "redirect:/uncaughtException";
	}


}
