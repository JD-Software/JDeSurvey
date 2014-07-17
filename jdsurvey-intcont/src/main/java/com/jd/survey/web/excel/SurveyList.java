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
package com.jd.survey.web.excel;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.web.AbstractJExcelView2;


public class SurveyList extends AbstractJExcelView2 {
	private static final Log log = LogFactory.getLog(SurveyList.class);
	
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4-strict.xml";
	
	@Override
	protected void buildExcelDocument(Map<String, Object> model,
			WritableWorkbook wb, 
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		try{

			
			int col = 1;
			int row = 1;
			String columnName="";
			SurveyDefinition surveyDefinition= (SurveyDefinition) model.get("surveyDefinition");
			List<Map<String,Object>> surveys = (List<Map<String,Object>>) model.get("surveys");
			Map<String,String> messages = (Map<String,String>) model.get("messages");
			Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
			AntiSamy as = new AntiSamy();
			
			//populate the metadata
			WritableSheet sheet = wb.getSheet("metadata");
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				for(Question question :page.getQuestions()) {
					
					CleanResults cr = as.scan(question.getQuestionText(), policy);
					question.setQuestionText(cr.getCleanHTML());
					
					if (question.getType().getIsMatrix() ){
						for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + "c" +	questionColumnLabel.getOrder()	;
								sheet.addCell(new Label(0, row, columnName,sheet.getCell(0,1).getCellFormat()));
								sheet.addCell(new Label(1, row, page.getOrder().toString(),sheet.getCell(1,1).getCellFormat()));
								sheet.addCell(new Label(2, row, question.getOrder().toString(),sheet.getCell(2,1).getCellFormat()));
								sheet.addCell(new Label(3, row, question.getQuestionText() ,sheet.getCell(3,1).getCellFormat()));
								sheet.addCell(new Label(4, row, "[row:" +  questionRowLabel.getLabel() + ", column:" + questionColumnLabel.getLabel() + "]",sheet.getCell(3,1).getCellFormat()));
								row++;
							}
						}
						continue;
					}


					if (question.getType().getIsMultipleValue() ){
						for(QuestionOption questionOption : question.getOptions()){
							columnName = "p" + page.getOrder()+"q" +question.getOrder() + "o" +questionOption.getOrder();
							sheet.addCell(new Label(0, row, columnName,sheet.getCell(0,1).getCellFormat()));
							sheet.addCell(new Label(1, row, page.getOrder().toString(),sheet.getCell(1,1).getCellFormat()));
							sheet.addCell(new Label(2, row, question.getOrder().toString(),sheet.getCell(2,1).getCellFormat()));
							sheet.addCell(new Label(3, row, question.getQuestionText(),sheet.getCell(3,1).getCellFormat()));
							sheet.addCell(new Label(4, row, "[option:"+ questionOption.getText() + "]",sheet.getCell(3,1).getCellFormat()));
							
							row++;
						}
						continue;
					}

					columnName = "p" + page.getOrder()+"q" +question.getOrder();
					sheet.addCell(new Label(0, row, columnName,sheet.getCell(0,1).getCellFormat()));
					sheet.addCell(new Label(1, row, page.getOrder().toString(),sheet.getCell(1,1).getCellFormat()));
					sheet.addCell(new Label(2, row, question.getOrder().toString(),sheet.getCell(2,1).getCellFormat()));
					sheet.addCell(new Label(3, row, question.getQuestionText(),sheet.getCell(3,1).getCellFormat()));
					sheet.addCell(new Label(4, row,  "",sheet.getCell(3,1).getCellFormat()));
					row++;



				}
			}


			//populate the Excel spreadsheet 
			sheet= wb.getSheet("data");
			//Put column Name in the first row
			CellFormat headerCellFormat = sheet.getCell(0,0).getCellFormat();
			
			
			sheet.addCell(new Label(0, 0, messages.get("surveyId"),headerCellFormat));
			sheet.addCell(new Label(1, 0, messages.get("surveyName"),headerCellFormat));
			sheet.addCell(new Label(2, 0, messages.get("firstname"),headerCellFormat));
			sheet.addCell(new Label(3, 0, messages.get("middlename"),headerCellFormat));
			sheet.addCell(new Label(4, 0, messages.get("lastname"),headerCellFormat));
			sheet.addCell(new Label(5, 0, messages.get("ipaddress"),headerCellFormat));
			sheet.addCell(new Label(6, 0, messages.get("submissionDate"),headerCellFormat));
			sheet.addCell(new Label(7, 0, messages.get("creationDate"),headerCellFormat));
			sheet.addCell(new Label(8, 0, messages.get("lastUpdateDate"),headerCellFormat));
			


			col =9; 
			for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
				for(Question question :page.getQuestions()) {
					// Check if the Question is Of Type Matrix
					if (question.getType().getIsMatrix()){
						for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
							for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
								columnName = question.getQuestionText() + " (row:" + questionRowLabel.getLabel() + 
										" column:" +	questionColumnLabel.getLabel() + ")";
								sheet.addCell(new Label(col, 0, columnName,headerCellFormat));
								col++;
							}
						}
						continue;
					}
					if (question.getType().getIsMultipleValue() ){
						for( QuestionOption questionOption : question.getOptions()){

							columnName = question.getQuestionText() + " (" +  questionOption.getText() + " )";
							sheet.addCell(new Label(col, 0, columnName,headerCellFormat));
							col++;
						}
						continue;
					}
					columnName = question.getQuestionText();
					sheet.addCell(new Label(col, 0, columnName,headerCellFormat)) ;
					col++;
				}
			}



			//put the Data
			Boolean value;
			row=1;
			CellFormat  dataCellFormat = sheet.getCell(0,1).getCellFormat();

			for (Map<String,Object> record : surveys){
				sheet.addCell(new Label(0, row, record.get("survey_id") == null ? "" : record.get("survey_id").toString(),dataCellFormat));
				sheet.addCell(new Label(1, row, record.get("type_name") == null ? "" : record.get("type_name").toString(),dataCellFormat));
				sheet.addCell(new Label(2, row, record.get("first_name") == null ? "" : record.get("first_name").toString(),dataCellFormat));
				sheet.addCell(new Label(3, row, record.get("middle_name") == null ? "" : record.get("middle_name").toString(),dataCellFormat));
				sheet.addCell(new Label(4, row, record.get("last_name") == null ? "" : record.get("last_name").toString(),dataCellFormat));
				sheet.addCell(new Label(5, row, record.get("ip_address") == null ? "" : record.get("ip_address").toString(),dataCellFormat));
				sheet.addCell(new Label(6, row, record.get("submission_date") == null ? "" : record.get("submission_date").toString(),dataCellFormat));
				sheet.addCell(new Label(7, row, record.get("creation_date") == null ? "" : record.get("creation_date").toString(),dataCellFormat));
				sheet.addCell(new Label(8, row, record.get("last_update_date") == null ? "" : record.get("last_update_date").toString(),dataCellFormat));


				col =9; 
				for(SurveyDefinitionPage page :surveyDefinition.getPages()) {
					for(Question question :page.getQuestions()) {
						columnName = "p" + page.getOrder()+"q" +question.getOrder();
						// Check if the Question is Of Type Matrix
						if (question.getType().getIsMatrix()){
							for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
								for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
									columnName = "p" + page.getOrder()+"q" +question.getOrder() + "r" + questionRowLabel.getOrder() + 
											"c" +	questionColumnLabel.getOrder()	;
									sheet.addCell(new Label(col, row, record.get(columnName) == null ? "" : record.get(columnName).toString() ,dataCellFormat));
									col++;
								}
							}
							continue;
						}

						if (question.getType().getIsMultipleValue() ){
							for( QuestionOption questionOption : question.getOptions()){
								columnName = "p" + page.getOrder()+"q" +question.getOrder() + "o" +questionOption.getOrder();
								sheet.addCell(new Label(col, row, record.get(columnName) == null ? "" : record.get(columnName).toString() ,dataCellFormat));
								col++;
							}
							continue;
						}
						columnName = "p" + page.getOrder()+"q" +question.getOrder();
						sheet.addCell(new Label(col, row, record.get(columnName) == null ? "" : record.get(columnName).toString() ,dataCellFormat));
						col++;
						
						//Program Name	
					}
				}
				row=row+1;
			}
			String fileName = "surveys";
			response.setHeader("Content-Disposition", "attachment; filename=\"" +fileName+ ".xls\"");

		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}


}


