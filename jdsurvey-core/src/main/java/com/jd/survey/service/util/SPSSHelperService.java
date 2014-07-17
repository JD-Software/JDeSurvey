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
package com.jd.survey.service.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.survey.dao.interfaces.settings.DataSetDAO;
import com.jd.survey.dao.interfaces.settings.SurveyDefinitionDAO;
import com.jd.survey.dao.interfaces.survey.ReportDAO;
import com.jd.survey.domain.settings.DataSet;
import com.jd.survey.domain.settings.DataSetItem;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;


@Service("SPSSHelperService")
public class SPSSHelperService {
	private static final Log log = LogFactory.getLog(SPSSHelperService.class);
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4-strict.xml"; 
	
	@Autowired	private SurveyDefinitionDAO surveyDefinitionDAO;
	@Autowired	private DataSetDAO dataSetDAO;
	@Autowired	private ReportDAO reportDAO;

	
	/**
	 * MATRIX Questions
	 * @return
	 */
	private StringBuffer getSPSSVariableName(Question question, QuestionRowLabel questionRowLabel , QuestionColumnLabel questionColumnLabel) {
		return new StringBuffer().append("P" + question.getPage().getOrder() + "Q" + question.getOrder() + "R" + questionRowLabel.getOrder() + "C" + questionColumnLabel.getOrder() );
	}


	private StringBuffer getSPSSVariableName(Question question) {
		return new StringBuffer().append("P" + question.getPage().getOrder() + "Q" + question.getOrder() );
	}


	/**
	 * returns the variable name for a survey question option
	 * @param option
	 * @return
	 */
	private StringBuffer getSPSSVariableName(QuestionOption option) {
		return new StringBuffer().append("P" + option.getQuestion().getPage().getOrder() + "Q" + option.getQuestion().getOrder()  + "_" + option.getOrder());
	}


	/**
	 * returns the variable label for a survey question 
	 * @return
	 */
	private String getSPSSVariableLabel(Question question) {
		String name = new StringBuffer().append("P"+question.getPage().getOrder().toString().replace("\"", "\"\"") + "-" + question.getQuestionText().replace("\"", "\"\"")).toString();
		if (name.length() > 60) {
			return "\"" + name.substring(0, 60)  + "\"";
		}
		else{
			return "\"" + name + "\"";
		}


	}

	/**
	 * returns the variable label for a survey question option
	 * @param option
	 * @return
	 */
	private String getSPSSVariableLabel(QuestionOption option) {
		String name = new StringBuffer().append("P"+option.getQuestion().getPage().getOrder().toString().replace("\"", "\"\"") + "-" + option.getQuestion().getQuestionText().replace("\"", "\"\"") + "[" + option.getText().replace("\"", "\"\"")  + "]").toString();
		if (name.length() > 60) {
			return "\"" + name.substring(0, 60)  + "\"";
		}
		else{
			return "\"" + name + "\"";
		}
	}


	private String getSPSSVariableLabel(Question question, QuestionRowLabel questionRowLabel , QuestionColumnLabel questionColumnLabel) {
		String name = new StringBuffer().append("P" + questionRowLabel.getQuestion().getPage().getOrder().toString().replace("\"", "\"\"") + 
												"-" + question.getQuestionText().replace("\"", "\"\"") + 
												"-" + questionRowLabel.getLabel().replace("\"", "\"\"") + 
												"-" + questionColumnLabel.getLabel().replace("\"", "\"\"") 
												).toString();
		if (name.length() > 60) {
			return "\"" + name.substring(0, 60)  + "\"";
		}
		else{
			return "\"" + name + "\"";
		}


	}
	

	/**
	 * Returns the SPSS variable value definitions for a survey
	 * @param surveyDefinition
	 * @return
	 */
	private StringBuffer getSurveySPSSVaribaleValues (SurveyDefinition surveyDefinition) {
		try {
			DataSet dataset;
			StringBuffer sb = new StringBuffer();
			for (SurveyDefinitionPage page :surveyDefinition.getPages()) {
				for (Question question :page.getQuestions()) {

					
					Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
					AntiSamy as = new AntiSamy();
					CleanResults cr = as.scan(question.getQuestionText(), policy);
					question.setQuestionText(cr.getCleanHTML());

					switch (question.getType()) {
					case YES_NO_DROPDOWN: //Yes No DropDown
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append(" 1 'Yes' 0 'No'.\n");
						break;
					case SHORT_TEXT_INPUT: //Short Text Input
						break;
					case LONG_TEXT_INPUT: //Long Text Input
						break;
					case HUGE_TEXT_INPUT: //Huge Text Input
						break;
					case INTEGER_INPUT: //Integer Input
						break;
					case CURRENCY_INPUT: //Currency Input
						break;
					case DECIMAL_INPUT: //Decimal Input
						break;
					case DATE_INPUT: //Date Input
						break;
					case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append("\n");
						for (QuestionOption option :question.getOptions()) {	
							sb.append("'").append(option.getValue().replace("'", "").replace("\"", "")).append("' ").append("'").append(option.getText().replace("'", "").replace("\"", "")).append("' ");
						}
						sb.deleteCharAt(sb.length()-1); 
						sb.append(".\n"); 
						break;
					case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
						for (QuestionOption option :question.getOptions()) {
							sb.append("VALUE LABELS ").append(getSPSSVariableName(option)).append(" 1 'Yes' 0 'Not selected'.\n");

						}
						break;	
					/*	
					case DATASET_DROP_DOWN: //DataSet Drop Down
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append("\n");
						dataset = dataSetDAO.findByName(question.getDataSetCode());
						for (DataSetItem dataSetItem :dataset.getItems()) {	
							sb.append("'").append(dataSetItem.getValue().replace("'", "").replace("\"", "")).append("' ").append("'").append(dataSetItem.getText().replace("'", "").replace("\"", "")).append("' ");
						}
						sb.deleteCharAt(sb.length()-1); 
						sb.append(".\n"); 
						break;
					*/	
					case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append("\n");
						for (QuestionOption option :question.getOptions()) {	
							sb.append("'").append(option.getValue().replace("'", "").replace("\"", "")).append("' ").append("'").append(option.getText().replace("'", "").replace("\"", "")).append("' ");
						}
						sb.deleteCharAt(sb.length()-1); 
						sb.append(".\n"); 
						break;	

					case YES_NO_DROPDOWN_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append("VALUE LABELS ").append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" 1 'Yes' 0 'No'.\n");
							}
						}
						break;
						
					case SHORT_TEXT_INPUT_MATRIX:
						break;
					case INTEGER_INPUT_MATRIX:
						break;	
					case CURRENCY_INPUT_MATRIX:
						break;	
					case DECIMAL_INPUT_MATRIX:
						break;	
					case DATE_INPUT_MATRIX:
						break;
					case IMAGE_DISPLAY:
						break;

					case VIDEO_DISPLAY:
						break;

					case FILE_UPLOAD:
						break;
					case STAR_RATING:
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append("\n");
						for (QuestionOption option :question.getOptions()) {	
							sb.append("'").append(option.getValue().replace("'", "").replace("\"", "")).append("' ").append("'").append(option.getText().replace("'", "").replace("\"", "")).append("' ");
						}
						sb.deleteCharAt(sb.length()-1); 
						sb.append(".\n"); 
						break;
					case SMILEY_FACES_RATING:
						sb.append("VALUE LABELS ").append(getSPSSVariableName(question)).append("\n");
						for (QuestionOption option :question.getOptions()) {	
							sb.append("'").append(option.getValue().replace("'", "").replace("\"", "")).append("' ").append("'").append(option.getText().replace("'", "").replace("\"", "")).append("' ");
						}
						sb.deleteCharAt(sb.length()-1); 
						sb.append(".\n"); 
						break;
					}
					
				}
			}
			return sb;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the SPSS variable name definitions for a survey
	 * @param surveyDefinition
	 * @return
	 */
	private StringBuffer getSurveySPSSVaribaleLabels (SurveyDefinition surveyDefinition) {
		try {
			StringBuffer sb = new StringBuffer();
			//For each page
			for (SurveyDefinitionPage page :surveyDefinition.getPages()) {
				//for each question
				for (Question question :page.getQuestions()) {
					//matrix questions
					if (question.getType().getIsMatrix()) {
						//for each row
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							//for each column
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append("VARIABLE LABELS ").append(getSPSSVariableName(question,questionRowLabel,questionColumnLabel )).append(" ").append(getSPSSVariableLabel(question,questionRowLabel,questionColumnLabel)).append(".\n");
							}
						}	
					}
					else {
						//multiple values questions
						if (question.getType().getIsMultipleValue()) {
							for (QuestionOption option :question.getOptions()) {	
								sb.append("VARIABLE LABELS ").append(getSPSSVariableName(option)).append(" ").append(getSPSSVariableLabel(option)).append(".\n");
							}
						}
						//Single value Questions
						else {
							sb.append("VARIABLE LABELS ").append(getSPSSVariableName(question)).append(" ").append(getSPSSVariableLabel(question)).append(".\n");
						}
					}
				}
			}
			return sb;
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the SPSS type definition for a survey
	 * @param question
	 * @return
	 */
	private StringBuffer getSurveySPSSTypeDefinitions(SurveyDefinition surveyDefinition) {
		try {
			StringBuffer sb = new StringBuffer();
			for (SurveyDefinitionPage page :surveyDefinition.getPages()) {
				for (Question question :page.getQuestions()) {
					
					Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
					AntiSamy as = new AntiSamy();
					CleanResults cr = as.scan(question.getQuestionText(), policy);
					question.setQuestionText(cr.getCleanHTML());
					
					switch (question.getType()) {
					case YES_NO_DROPDOWN: //Yes No DropDown
						sb.append(getSPSSVariableName(question)).append(" F1\n");
						break;
					case SHORT_TEXT_INPUT: //Short Text Input
						sb.append(getSPSSVariableName(question)).append(" A75\n");
						break;
					case LONG_TEXT_INPUT: //Long Text Input
						sb.append(getSPSSVariableName(question)).append(" A250\n");
						break;
					case HUGE_TEXT_INPUT: //Huge Text Input
						sb.append(getSPSSVariableName(question)).append(" A2000\n");
						break;
					case INTEGER_INPUT: //Integer Input
						sb.append(getSPSSVariableName(question)).append(" F11\n");
						break;
					case CURRENCY_INPUT: //Currency Input
						sb.append(getSPSSVariableName(question)).append(" DOLLAR\n");
						break;
					case DECIMAL_INPUT: //Decimal Input
						sb.append(getSPSSVariableName(question)).append(" F\n");
						break;
					case DATE_INPUT: //Date Input
						sb.append(getSPSSVariableName(question)).append(" SDATE10\n"); //Format of export should be YYYY/MM/DD
						break;
					case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
						sb.append(getSPSSVariableName(question)).append(" A5\n"); 
						break;
					case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes
						for (QuestionOption option :question.getOptions()) {
							sb.append(getSPSSVariableName(option)).append(" F1\n"); 	 	
						}
						break;	
					case DATASET_DROP_DOWN: //DataSet Drop Down
						sb.append(getSPSSVariableName(question)).append(" A5\n");
						break;
					case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
						sb.append(getSPSSVariableName(question)).append(" A5\n");
						break;	


					case YES_NO_DROPDOWN_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" F1\n");
							}
						}

						break;
					case SHORT_TEXT_INPUT_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" A75\n");
							}
						}
						break;
					case INTEGER_INPUT_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" F11\n");
							}
						}
						break;	
					case CURRENCY_INPUT_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" DOLLAR\n");
							}
						}
						break;	
					case DECIMAL_INPUT_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" F\n");
							}
						}
						break;	
					case DATE_INPUT_MATRIX:
						for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								sb.append(getSPSSVariableName(question, questionRowLabel, questionColumnLabel)).append(" SDATE10\n");
							}
						}
						break;				

					case IMAGE_DISPLAY:
						break;

					case VIDEO_DISPLAY:
						break;

					case FILE_UPLOAD:
						break;
						
						
					case STAR_RATING:
						sb.append(getSPSSVariableName(question)).append(" F11\n");
						break;
					case SMILEY_FACES_RATING:
						sb.append(getSPSSVariableName(question)).append(" F11\n");
						break;
					}
				}
			}
			//Remove the extra line break and append a dot
			sb.deleteCharAt(sb.length()-1); 
			sb.append(".\n"); 
			return sb; 
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}





	public byte[] getSurveyDefinitionSPSSMetadata(Long surveyDefinitionId, String dataFileName) {
		try{
			SurveyDefinition surveyDefinition= surveyDefinitionDAO.findById(surveyDefinitionId);


			//start building the SPSS metadata file 
			StringBuffer metadataStringBuffer = new StringBuffer();
			metadataStringBuffer.append("*Survey Name : " + surveyDefinition.getName() + "\n" +
					"*Export Date : " + new Date().toString() + "\n" +
					"*NOTE: In some cases you may need to modify the data file location below to include the full path on your computer to be able to open the data file in SPSS.\n");
			metadataStringBuffer.append("GET DATA  " 
					+ "/TYPE=TXT\n" 
					+ "/FILE='" + dataFileName + "'\n" 
					+ "/DELCASE=LINE\n" 
					+ "/DELIMITERS=\",\"\n"
					+ "/QUALIFIER='\"'\n"
					+ "/ARRANGEMENT = DELIMITED\n" 
					+ "/FIRSTCASE=1\n" 
					+ "/IMPORTCASE=ALL\n");
			metadataStringBuffer.append("/VARIABLES=\n");
			//Variable Types Declarations
			metadataStringBuffer.append(getSurveySPSSTypeDefinitions(surveyDefinition));

			//add more commands
			metadataStringBuffer.append("CACHE.\n");
			metadataStringBuffer.append("EXECUTE.\n");

			//Variable names
			metadataStringBuffer.append(getSurveySPSSVaribaleLabels(surveyDefinition));


			//Variable values
			metadataStringBuffer.append(getSurveySPSSVaribaleValues(surveyDefinition));

			return metadataStringBuffer.toString().getBytes("UTF-8");

		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}




	public byte[] getSurveyDefinitionSPSSData(Long surveyDefinitionId) {
		try{

			String columnName;
			StringBuffer dataStringBuffer = new StringBuffer();

			SurveyDefinition surveyDefinition= surveyDefinitionDAO.findById(surveyDefinitionId);
			List<Map<String,Object>> surveys = reportDAO.getSurveyData(surveyDefinitionId); 

			for (Map<String,Object> record : surveys){
				//dataStringBuffer.append(record.get("survey_id") == null ? "" : "\"" + record.get("survey_id").toString().replace("\"", "\"\"") +"\"," );
				//dataStringBuffer.append(record.get("type_name") == null ? "" : "\"" + record.get("type_name").toString().replace("\"", "\"\"") +"\"," );
				//dataStringBuffer.append(record.get("login") == null ? "" : "\"" + record.get("login").toString().replace("\"", "\"\"") +"\"," );
				//dataStringBuffer.append(record.get("submission_date") == null ? "" : "\"" + record.get("creation_date").toString().replace("\"", "\"\"") +"\"," );
				//dataStringBuffer.append(record.get("creation_date") == null ? "" : "\"" + record.get("last_update_date").toString().replace("\"", "\"\"") +"\"," );
				//dataStringBuffer.append(record.get("last_update_date") == null ? "" : "\"" + record.get("last_update_date").toString().replace("\"", "\"\"") +"\"," );

				for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
					for(Question question :page.getQuestions()) {
									
						
						switch (question.getType()) {
						case YES_NO_DROPDOWN: //Yes No DropDown
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + ((Boolean) record.get(columnName) ? "1" : "0") +"\"," );
							break;
						case SHORT_TEXT_INPUT: //Short Text Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case LONG_TEXT_INPUT: //Long Text Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case HUGE_TEXT_INPUT: //Huge Text Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case INTEGER_INPUT: //Integer Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case CURRENCY_INPUT: //Currency Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case DECIMAL_INPUT: //Decimal Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case DATE_INPUT: //Date Input
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case SINGLE_CHOICE_DROP_DOWN: //Single choice Drop Down
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," ); 
							break;
						case MULTIPLE_CHOICE_CHECKBOXES: //Multiple Choice Checkboxes

							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							for (QuestionOption option :question.getOptions()) {
								if (record.get(columnName) != null && option.getValue().equals(record.get(columnName).toString())) {
									dataStringBuffer.append("1,");
								}
								else{
									dataStringBuffer.append("0,");
								} 	 	
							}
							break;	
						case DATASET_DROP_DOWN: //DataSet Drop Down
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case SINGLE_CHOICE_RADIO_BUTTONS: //Single Choice Radio Buttons
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;	


						case YES_NO_DROPDOWN_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "0," : "\"" + ((Boolean) record.get(columnName) ? "1" : "0") +"\"," );					
								}
							}
							break;
						case SHORT_TEXT_INPUT_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							break;
						case INTEGER_INPUT_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							break;	
						case CURRENCY_INPUT_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							break;	
						case DECIMAL_INPUT_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							break;	

						case DATE_INPUT_MATRIX:
							for (QuestionRowLabel questionRowLabel : question.getRowLabels()){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" + questionColumnLabel.getOrder()	;
									dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
								}
							}
							break;			


						case IMAGE_DISPLAY:
							break;

						case VIDEO_DISPLAY:
							break;

						case FILE_UPLOAD:
							break;
						case STAR_RATING:
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;
						case SMILEY_FACES_RATING:
							columnName = "p" + page.getOrder()+"q" +question.getOrder();
							dataStringBuffer.append(record.get(columnName) == null ? "," : "\"" + record.get(columnName).toString().replace("\"", "\"\"") +"\"," );
							break;

						}

					}
				}
				dataStringBuffer.deleteCharAt(dataStringBuffer.length()-1); //delete the last comma
				dataStringBuffer.append("\n");
			}


			return dataStringBuffer.toString().getBytes("UTF-8");

		}



		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}



}
