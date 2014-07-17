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
package com.jd.survey.web.pdf;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.DateValidator;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.jd.survey.domain.settings.DataSetItem;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.survey.QuestionAnswer;
import com.jd.survey.domain.survey.SurveyEntry;
import com.jd.survey.domain.survey.SurveyPage;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class SurveyPdf  extends AbstractPdfView{
	
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4-strict.xml"; 
	
	private static final Font titleFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0,68,136));
	private static final Font boldedFont = new Font(Font.HELVETICA, 9, Font.BOLD);
	private static final Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
	
	@Override
	protected void buildPdfDocument(Map model, Document document,
									PdfWriter writer, 
									HttpServletRequest request,
									HttpServletResponse response) throws Exception {

		
		Paragraph titleParagraph;
		
		SurveyEntry surveyEntry= (SurveyEntry) model.get("surveyEntry");
		List<SurveyPage> surveyPages = (List<SurveyPage>) model.get("surveyPages");

		String falseMessage = (String)  model.get("falseMessage");
		String trueMessage = (String)  model.get("trueMessage");
		String dateFormat = (String)  model.get("dateFormat");
		
		
		Map<String,String> surveyEntryLabels = (Map<String,String>)  model.get("surveyEntryLabels");
		//display Survey Entry Information
		writeTitle(document,surveyEntryLabels.get("surveyEntryLabel"));
		writeAnswer(document,surveyEntryLabels.get("surveyIdLabel"), surveyEntry.getSurveyId().toString());	
		writeAnswer(document,surveyEntryLabels.get("surveyNameLabel"), surveyEntry.getSurveyName());
		writeAnswer(document,surveyEntryLabels.get("createdByLabel"), surveyEntry.getCreatedByFullName());
		writeAnswer(document,surveyEntryLabels.get("creationDateLabel"), surveyEntry.getCreationDate() == null ?	"" :
												   DateValidator.getInstance().format(surveyEntry.getCreationDate(), dateFormat));
		writeAnswer(document,surveyEntryLabels.get("lastUpdateDateLabel"), surveyEntry.getLastUpdateDate() == null ?	"" :
			 									   DateValidator.getInstance().format(surveyEntry.getLastUpdateDate(), dateFormat));
		writeAnswer(document,surveyEntryLabels.get("submissionDateLabel"), surveyEntry.getSubmissionDate() == null ?	"" :
			 									   DateValidator.getInstance().format(surveyEntry.getSubmissionDate(), dateFormat));
		
		for(SurveyPage surveyPage :surveyPages) {
			//display page title
			writeTitle(document,surveyPage.getTitle());

			for(QuestionAnswer questionAnswer :surveyPage.getQuestionAnswers()) {
				
				
				Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy as = new AntiSamy();
				CleanResults cr = as.scan(questionAnswer.getQuestion().getQuestionText(), policy);
				questionAnswer.getQuestion().setQuestionText(cr.getCleanHTML());
				

				switch (questionAnswer.getQuestion().getType())
				{
				case YES_NO_DROPDOWN:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getBooleanAnswerValue(),
								falseMessage,trueMessage);							
					break;
				case SHORT_TEXT_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());						
					break;
				case LONG_TEXT_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;
				case HUGE_TEXT_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;
				case INTEGER_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;
				case CURRENCY_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;
				case DECIMAL_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;
				case DATE_INPUT:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), questionAnswer.getStringAnswerValue());							
					break;



				case SINGLE_CHOICE_DROP_DOWN:
					//verify that the value exists in the options
					for (QuestionOption option: questionAnswer.getQuestion().getOptions()){
						if (option.getValue().equalsIgnoreCase(questionAnswer.getStringAnswerValue())) {
							writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), option.getText());	
							break;
						}
					}
					break;
				case MULTIPLE_CHOICE_CHECKBOXES:
					String answers ="";
					for (int i= 0; i < questionAnswer.getIntegerAnswerValuesArray().length; i++) {
						int j= 0;
						for (QuestionOption option: questionAnswer.getQuestion().getOptions()){
							if ((i == j) && questionAnswer.getIntegerAnswerValuesArray()[i] != null) {answers = answers + option.getText() +", ";}
							j++;
						}
					}
					if (!answers.isEmpty()) {answers = answers.substring(0, answers.length()-2);} //remove the last comma
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), answers);	
					break;
				case DATASET_DROP_DOWN:
					for (DataSetItem dataSetItem: questionAnswer.getQuestion().getDataSetItems()){
						if (dataSetItem.getValue().equalsIgnoreCase(questionAnswer.getStringAnswerValue())) {
							writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), dataSetItem.getText());	
							break;
						}
					}
					break;
				case SINGLE_CHOICE_RADIO_BUTTONS:
					//verify that the value exists in the options
					for (QuestionOption option: questionAnswer.getQuestion().getOptions()){
						if (option.getValue().equalsIgnoreCase(questionAnswer.getStringAnswerValue())) {
							writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), option.getText());	
							break;
						}
					}
					break;
				case YES_NO_DROPDOWN_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getBooleanAnswerValuesMatrix(),
										falseMessage,trueMessage);
					break;
				case SHORT_TEXT_INPUT_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getStringAnswerValuesMatrix());
					break;
				case INTEGER_INPUT_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getStringAnswerValuesMatrix());
					break;
				case CURRENCY_INPUT_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getStringAnswerValuesMatrix());
					break;
				case DECIMAL_INPUT_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getStringAnswerValuesMatrix());
					break;
				case DATE_INPUT_MATRIX:
					writeAnswersMatrix(document,questionAnswer.getQuestion(),questionAnswer.getStringAnswerValuesMatrix());
					break;

				case IMAGE_DISPLAY:
					break;
				case VIDEO_DISPLAY:
					break;
				case FILE_UPLOAD:
					writeAnswer(document,questionAnswer.getQuestion().getQuestionText(),
								questionAnswer.getSurveyDocument() != null ?questionAnswer.getSurveyDocument().getFileName(): "");
					
					break;

				case STAR_RATING:
					//verify that the value exists in the options
					for (QuestionOption option: questionAnswer.getQuestion().getOptions()){
						if (option.getValue().equalsIgnoreCase(questionAnswer.getStringAnswerValue())) {
							writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), option.getText());	
							break;
						}
					}
					break;
				case SMILEY_FACES_RATING:
					//verify that the value exists in the options
					for (QuestionOption option: questionAnswer.getQuestion().getOptions()){
						if (option.getValue().equalsIgnoreCase(questionAnswer.getStringAnswerValue())) {
							writeAnswer(document,questionAnswer.getQuestion().getQuestionText(), option.getText());	
							break;
						}
					}
					break;
				}






			}

		}



	}

	
	//display page title

		
		private void writeTitle(Document document,String title) throws Exception{
			Paragraph titleParagraph = new Paragraph(title,titleFont);
			titleParagraph.setAlignment(Element.ALIGN_LEFT);
			titleParagraph.setLeading(30);
			titleParagraph.setSpacingAfter(2);
			document.add(titleParagraph);
		}

	
		private void writeAnswer(Document document,String questionText , boolean answerValue , 
								 String falseMessage,String trueMessage) throws Exception{
			
			
			//String falseString ="False";  
			//String trueString = "True";
			
			Paragraph questionParagraph = new Paragraph();
			questionParagraph.setLeading(14, 0);
			questionParagraph.add(new Chunk(questionText.trim() + ": ",boldedFont)); 
		    questionParagraph.add(new Chunk(answerValue ? trueMessage : falseMessage ,normalFont));  
			document.add(questionParagraph);
		}
		
		private void writeAnswer(Document document,String questionText , String answerValue) throws Exception{
			Paragraph questionParagraph = new Paragraph();
			questionParagraph.setLeading(14, 0);
			questionParagraph.add(new Chunk(questionText.trim() + ": ",boldedFont)); 
		    questionParagraph.add(new Chunk(answerValue == null ? "": answerValue.trim() ,normalFont));  
			document.add(questionParagraph);
		}
		
		
		
		private void writeAnswersMatrix(Document document,Question question,Boolean[][] answerValuesMatrix,
										String falseMessage,String trueMessage) throws Exception{
			Table matrixTable;
			Cell cell;
			
			Paragraph questionParagraph = new Paragraph();
			questionParagraph.add(new Chunk(question.getQuestionText().trim() + ": ",boldedFont));
			document.add(questionParagraph);						
			
			matrixTable = new Table(question.getColumnLabels().size() + 1);
			matrixTable.setPadding(2);
			matrixTable.setWidth(100);
			matrixTable.setDefaultCellBorder(0);
			matrixTable.setBorder(0);
			
			matrixTable.setOffset(4);
			
			
			matrixTable.setAutoFillEmptyCells(true);
			matrixTable.setCellsFitPage(true);
			
			matrixTable.setTableFitsPage(true);
			cell =new Cell("");
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			matrixTable.addCell(cell);
			
			for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
				cell =new Cell(new Paragraph(questionColumnLabel.getLabel(),boldedFont));
				cell.setBackgroundColor(Color.LIGHT_GRAY);
				matrixTable.addCell(cell);
			}
			int rowIndex = 0;
			for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
				int columnIndex = 0;
				questionParagraph = new Paragraph(questionRowLabel.getLabel(),boldedFont);
				questionParagraph.setLeading(12, 0);
				cell = new Cell(questionParagraph);
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(231,238,244));}
				matrixTable.addCell(cell);
				for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
					questionParagraph =new Paragraph(answerValuesMatrix[rowIndex][columnIndex] ? trueMessage: falseMessage, normalFont);
					questionParagraph.setLeading(12, 0);
					cell = new Cell(questionParagraph);
					if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(231,238,244));}
					matrixTable.addCell(cell);
					columnIndex++;
				}
				rowIndex++;
			}
			document.add(matrixTable);
		} 
		
		
		private void writeAnswersMatrix(Document document,Question question,String[][] answerValuesMatrix ) throws Exception{
			Table matrixTable;
			Cell cell;
			
			Paragraph questionParagraph = new Paragraph();
			questionParagraph.add(new Chunk(question.getQuestionText().trim() + ": ",boldedFont)); 
		    document.add(questionParagraph);						
			
			matrixTable = new Table(question.getColumnLabels().size() + 1);
			matrixTable.setPadding(2);
			matrixTable.setWidth(100);
			matrixTable.setDefaultCellBorder(0);
			matrixTable.setBorder(0);
			matrixTable.setOffset(4);
			matrixTable.setAutoFillEmptyCells(true);
			matrixTable.setCellsFitPage(true);
			
			matrixTable.setTableFitsPage(true);
			cell =new Cell("");
			cell.setBackgroundColor(Color.LIGHT_GRAY);
			matrixTable.addCell(cell);
			
			for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
				cell =new Cell(new Paragraph(questionColumnLabel.getLabel(),boldedFont));
				cell.setBackgroundColor(Color.LIGHT_GRAY);
				matrixTable.addCell(cell);
			}
			int rowIndex = 0;
			for (QuestionRowLabel questionRowLabel : question.getRowLabels() ){
				int columnIndex = 0;
				questionParagraph = new Paragraph(questionRowLabel.getLabel(),boldedFont);
				questionParagraph.setLeading(12, 0);
				cell = new Cell(questionParagraph);
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(231,238,244));}
				matrixTable.addCell(cell);
				for (QuestionColumnLabel questionColumnLabel : question.getColumnLabels()){
					questionParagraph =new Paragraph(answerValuesMatrix[rowIndex][columnIndex] == null ? "":
													 answerValuesMatrix[rowIndex][columnIndex],normalFont);
					questionParagraph.setLeading(12, 0);
					cell = new Cell(questionParagraph);
					if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(231,238,244));}
					matrixTable.addCell(cell);
					columnIndex++;
				}
				rowIndex++;
			}
			document.add(matrixTable);
		} 
		
		
		
		
	}