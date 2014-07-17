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
package com.jd.survey.web.statistics;

import java.awt.Color;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
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
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionStatistic;
import com.jd.survey.domain.survey.SurveyStatistic;
import com.jd.survey.service.security.SecurityService;
import com.jd.survey.service.security.UserService;
import com.jd.survey.service.settings.SurveySettingsService;
import com.jd.survey.service.survey.SurveyService;


@RequestMapping("/statistics")
@Controller
public class StatisticsController {
	private static final Log log = LogFactory.getLog(StatisticsController.class);
	
	@Autowired	private UserService userService;
	@Autowired	private SurveyService surveyService;
	@Autowired	private SurveySettingsService surveySettingsService;
	@Autowired	private SecurityService securityService;
	@Autowired	private MessageSource messageSource;
	
	

	
	private static final String DATE_FORMAT = "date_format";
	
	private static final String SURVEY_LABEL = "com.jd.survey.domain.settings.surveydefinition_label_short";
	private static final String TOTAL_LABEL = "com.jd.survey.domain.survey.surveystatistic.totalcount_label";
	private static final String COMPLETED_LABEL = "com.jd.survey.domain.survey.surveystatistic.submittedcount_label";
	private static final String OPTION_FREQUENCY_LABEL = "com.jd.survey.domain.survey.questionstatistic.frequency_label";
	private static final String NO_STATSTISTICS_MESSAGE = "statistics_not_applicable_message";
	private static final String PAGE_LABEL = "com.jd.survey.domain.survey.surveypage_label_short";
	private static final String OPTION_LABEL = "com.jd.survey.domain.settings.questionoption_label_short";
	private static final String MINIMUM_LABEL = "com.jd.survey.domain.survey.questionstatistic.min_label";
	private static final String MAXIMUM_LABEL = "com.jd.survey.domain.survey.questionstatistic.max_label";
	private static final String AVERAGE_LABEL = "com.jd.survey.domain.survey.questionstatistic.average_label";
	private static final String STANDARD_DEVIATION_LABEL = "com.jd.survey.domain.survey.questionstatistic.samplestandarddeviation_label";
	private static final String FALSE_LABEL = "false_message";
	private static final String TRUE_LABEL = "true_message";

	
	
	void populateModel(Model uiModel, Long surveyDefinitionId,Question question,User user) {
		try{
			SurveyDefinition surveyDefinition = surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			Set<SurveyDefinition> surveyDefinitions= surveySettingsService.surveyDefinition_findAllCompletedInternal(user);
			SurveyStatistic surveyStatistic =surveyService.surveyStatistic_get(surveyDefinitionId);
			Long recordCount = surveyStatistic.getSubmittedCount();
			
			uiModel.addAttribute("question",question); 
			uiModel.addAttribute("questionId",question.getId()); 
			uiModel.addAttribute("surveyDefinition", surveyDefinition);
			uiModel.addAttribute("surveyDefinitions", surveyDefinitions);
			uiModel.addAttribute("surveyStatistic", surveyStatistic);
			uiModel.addAttribute("recordCount", recordCount);
			uiModel.addAttribute("questionStatistics" ,surveyService.questionStatistic_getStatistics(question,recordCount));
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}

	}
	
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
			
			return "statistics/statistics";
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
	public String listSurveyEntries(@RequestParam(value = "sid", required = true) Long surveyDefinitionId,
									@RequestParam(value = "qid", required = false) Long questionId,
									Model uiModel,
									Principal principal,
									HttpServletRequest httpServletRequest) {
		
		try{
			
			Question question; 
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				return "accessDenied";	
			}	
			
			//get the first question
			if (questionId == null) {
				question  = surveySettingsService.question_findByOrder(surveyDefinitionId,(short) 1,(short) 1);
			}
			else{
				question  = surveySettingsService.question_findById(questionId);
			}
				populateModel(uiModel,surveyDefinitionId,question, user);
			return "statistics/statistics";
			
			
			
			
			
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
	@RequestMapping(value="/pdf/{id}", produces = "text/html")
	public ModelAndView  exportSurveyToPdf(@PathVariable("id") Long surveyDefinitionId,
										 	Principal principal,
										 	HttpServletRequest httpServletRequest,
										 	HttpServletResponse response) {
		
		try{
			
			User user = userService.user_findByLogin(principal.getName());
			if(!securityService.userIsAuthorizedToManageSurvey(surveyDefinitionId, user)) {
				log.warn("Unauthorized access to url path " + httpServletRequest.getPathInfo() + " attempted by user login:" + principal.getName() + "from IP:" + httpServletRequest.getLocalAddr());
				throw(new RuntimeException("Unauthorized access"));	
			}
			ModelAndView modelAndView =new ModelAndView("statisticsPdf");
			
			Map<String,String> messages =  new TreeMap<String,String>();
			
			messages.put("surveyLabel", messageSource.getMessage(SURVEY_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("totalLabel",messageSource.getMessage(TOTAL_LABEL, null, LocaleContextHolder.getLocale()) );
			messages.put("completedLabel", messageSource.getMessage(COMPLETED_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("optionFrequencyLabel", messageSource.getMessage(OPTION_FREQUENCY_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("noStatstisticsMessage", messageSource.getMessage(NO_STATSTISTICS_MESSAGE, null, LocaleContextHolder.getLocale()));
			
			messages.put("pageLabel", messageSource.getMessage(PAGE_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("optionLabel", messageSource.getMessage(OPTION_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("minimumLabel", messageSource.getMessage(MINIMUM_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("maximumLabel", messageSource.getMessage(MAXIMUM_LABEL, null, LocaleContextHolder.getLocale()));
			
			messages.put("averageLabel", messageSource.getMessage(AVERAGE_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("standardDeviationLabel", messageSource.getMessage(STANDARD_DEVIATION_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("date_format", messageSource.getMessage(DATE_FORMAT, null, LocaleContextHolder.getLocale()));
			messages.put("falseLabel", messageSource.getMessage(FALSE_LABEL, null, LocaleContextHolder.getLocale()));
			messages.put("trueLabel", messageSource.getMessage(TRUE_LABEL, null, LocaleContextHolder.getLocale()));
			
			
			
			

			
			SurveyDefinition surveyDefinition = surveySettingsService.surveyDefinition_findById(surveyDefinitionId);
			SurveyStatistic surveyStatistic =surveyService.surveyStatistic_get(surveyDefinitionId);
			Long recordCount = surveyStatistic.getSubmittedCount();
			modelAndView.addObject("surveyDefinition", surveyDefinition);
			modelAndView.addObject("surveyStatistic", surveyStatistic);
			modelAndView.addObject("messages", messages);
			Map<String,List<QuestionStatistic>> allquestionStatistics =  new TreeMap<String,List<QuestionStatistic>>();
			
			for (SurveyDefinitionPage  page :surveyDefinition.getPages()) {
				for (Question  question :page.getQuestions()) { 
					List<QuestionStatistic> questionStatistics = surveyService.questionStatistic_getStatistics(question,recordCount);
					allquestionStatistics.put("q" + question.getId().toString(),questionStatistics);
				}
			}
			modelAndView.addObject("allquestionStatistics", allquestionStatistics);
			return modelAndView;
			
			
			
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	

	
	
	
	
	


	
	
	/**
	 * Controller that handles the chart generation for a question statistics
	 * @param surveyDefinitionId
	 * @param pageOrder
	 * @param questionOrder
	 * @param recordCount
	 * @param response
	 */
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	@RequestMapping(value="/chart/{surveyDefinitionId}/{questionId}")
	public void generatePieChart(@PathVariable("surveyDefinitionId")  Long surveyDefinitionId,
								 @PathVariable("questionId")  Long questionId, 
								 HttpServletResponse response) {
		try {
			response.setContentType("image/png");
			long recordCount  = surveyService.surveyStatistic_get(surveyDefinitionId).getSubmittedCount();
			PieDataset pieDataSet= createDataset(questionId,recordCount);
			JFreeChart chart = createChart(pieDataSet, "");
			ChartUtilities.writeChartAsPNG(response.getOutputStream(), chart, 340 ,200);
			response.getOutputStream().close();
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}	
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	private PieDataset createDataset(Long questionId, Long recordCount)	{
		try{
			String columnValue=""; 
			Question question = surveySettingsService.question_findById(questionId);
			List<QuestionStatistic> questionStatistics =surveyService.questionStatistic_getStatistics(question,recordCount);
			DefaultPieDataset defaultPieDataset = new  DefaultPieDataset ();
			for (QuestionStatistic questionStatistic: questionStatistics) {
				//double percentage = ((double)questionStatistic.getCount()/(double)recordCount) * 100;
				//percentage = Double.valueOf(new DecimalFormat("#.##").format(percentage));
				defaultPieDataset.setValue(questionStatistic.getEntry(), questionStatistic.getFrequency() *100);
			}
			return defaultPieDataset;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw (new RuntimeException(e));
		}
	}
	
	
	@Secured({"ROLE_ADMIN","ROLE_SURVEY_ADMIN"})
	private JFreeChart createChart(PieDataset pieDataset, String title) {
	try{
		JFreeChart chart = ChartFactory.createPieChart(title, pieDataset, false,true,false);

		chart.setBackgroundPaint(null);//this line necessary for transparency of background
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setOpaque(false); //this line necessary for transparency of background
        chartPanel.setBackground(new Color(0, 0, 0, 0)); //this line necessary for transparency of background
        
		PiePlot plot = (PiePlot) chart.getPlot();
		
		//Color[] colors = {new Color(170, 195, 217, 255),new Color(246, 140, 31, 255),new Color(204, 204, 204, 255),new Color(231, 238, 144, 255),new Color(51, 51, 51, 255),new Color(101, 125, 151, 255),new Color(0, 102, 255, 255)}; 
		//PieRenderer renderer = new PieRenderer(colors); 
		//renderer.setColor(plot, pieDataset);
		
		
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator("{0}:{1}%"); 
		plot.setLabelGenerator(generator);
		
		plot.setStartAngle(270);
		plot.setDirection(Rotation.CLOCKWISE);
		
		return chart;
	} catch (Exception e) {
		log.error(e.getMessage(),e);
		throw (new RuntimeException(e));
	}
	}
	
	
	
	/**
	 * Not Used
	 * @author Admin
	 *
	 */
	static class PieRenderer 
    { 
        private Color[] color; 

        public PieRenderer(Color[] color) 
        { 
            this.color = color; 
        }        

        public void setColor(PiePlot plot, PieDataset dataset) 
        { 
            List <Comparable> keys = dataset.getKeys(); 
            int aInt; 

            for (int i = 0; i < keys.size(); i++) 
            { 
                aInt = i % this.color.length; 
                plot.setSectionPaint(keys.get(i), this.color[aInt]); 
            } 
        } 
    } 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
