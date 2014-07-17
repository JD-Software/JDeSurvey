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
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.format.Alignment;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.DateValidator;
import org.jfree.util.Log;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.jd.survey.domain.settings.DataSetItem;
import com.jd.survey.domain.settings.Question;
import com.jd.survey.domain.settings.QuestionColumnLabel;
import com.jd.survey.domain.settings.QuestionOption;
import com.jd.survey.domain.settings.QuestionRowLabel;
import com.jd.survey.domain.settings.QuestionType;
import com.jd.survey.domain.settings.SurveyDefinition;
import com.jd.survey.domain.settings.SurveyDefinitionPage;
import com.jd.survey.domain.survey.QuestionStatistic;
import com.jd.survey.domain.survey.SurveyStatistic;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BarcodeEAN;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component
public class StatisticsPdf  extends AbstractPdfView{
	private static final String  POLICY_FILE_LOCATION="/antisamy-tinymce-1-4-4-strict.xml"; 
	
	private static final Font titleFont = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(0,68,136));
	private static final Font subTitleFont = new Font(Font.HELVETICA, 10, Font.BOLD, new Color(0,68,136));
	private static final Font boldedFont = new Font(Font.HELVETICA, 9, Font.BOLD);
	private static final Font normalFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
	
	
	@Override
	protected void buildPdfDocument(Map model, Document document,
									PdfWriter writer, 
									HttpServletRequest request,
									HttpServletResponse response) throws Exception {

	
		
	
		
		

		Map<String,String> messages = (Map<String,String>) model.get("messages");
		SurveyDefinition surveyDefinition= (SurveyDefinition) model.get("surveyDefinition");
		SurveyStatistic surveyStatistic = (SurveyStatistic) model.get("surveyStatistic");
		Map<String,List<QuestionStatistic>> allQuestionStatistics = (Map<String,List<QuestionStatistic>>) model.get("allquestionStatistics");
		List<QuestionStatistic> questionStatistics; 
		
	
		String surveyLabel= messages.get("surveyLabel");
		String totalLabel= messages.get("totalLabel");
		String completedLabel= messages.get("completedLabel");
		String noStatstisticsMessage= messages.get("noStatstisticsMessage");
		String pageLabel= messages.get("pageLabel");
		String optionLabel= messages.get("optionLabel");
		String optionFrequencyLabel= messages.get("optionFrequencyLabel");
		String minimumLabel= messages.get("minimumLabel");
		String maximumLabel= messages.get("maximumLabel");
		String averageLabel= messages.get("averageLabel");
		String standardDeviationLabel= messages.get("standardDeviationLabel");
		String date_format=messages.get("date_format");
		String falseLabel = messages.get("falseLabel");
		String trueLabel = messages.get("trueLabel");
		
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		
		Paragraph titleParagraph;
		
		//Render Survey statistics
		writeTitle(document,surveyLabel + ": " +surveyDefinition.getName());
		writeEntry(document,totalLabel ,surveyStatistic.getTotalCount().toString());
		writeEntry(document,completedLabel ,surveyStatistic.getSubmittedCount().toString() + " (" + percentFormat.format(surveyStatistic.getSubmittedPercentage()) + ")");
		
		
		//Render Question statistics
		for(SurveyDefinitionPage page :surveyDefinition.getPages()) { //loop on the pages
			writeTitle(document,pageLabel + " " + page.getTwoDigitPageOrder() + ": " +page.getTitle());
			for(Question question :page.getQuestions()) {
				Policy policy = Policy.getInstance(this.getClass().getResource(POLICY_FILE_LOCATION));
				AntiSamy as = new AntiSamy();
				CleanResults cr = as.scan(question.getQuestionText(), policy);
				question.setQuestionText(cr.getCleanHTML());
				
				writeSubTitle(document,question.getTwoDigitPageOrder() + "- " +question.getQuestionText());
				questionStatistics = (List<QuestionStatistic>) allQuestionStatistics.get("q" +question.getId().toString());
					
				switch (question.getType())
				{
				case YES_NO_DROPDOWN:
					writeBooleanQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel,trueLabel,falseLabel);
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case SHORT_TEXT_INPUT:
					writeEntry(document,noStatstisticsMessage);
					break;
				case LONG_TEXT_INPUT:
					writeEntry(document,noStatstisticsMessage);
					break;
				case HUGE_TEXT_INPUT:
					writeEntry(document,noStatstisticsMessage);
					break;
				case INTEGER_INPUT:
					writeEntry(document, minimumLabel, questionStatistics.get(0).getMin());
					writeEntry(document, maximumLabel, questionStatistics.get(0).getMax()); 
					writeEntry(document, averageLabel, questionStatistics.get(0).getAverage());
					writeEntry(document, standardDeviationLabel, questionStatistics.get(0).getSampleStandardDeviation());
					break;
				case CURRENCY_INPUT:
					writeCurrencyEntry(document, minimumLabel, questionStatistics.get(0).getMin());
					writeCurrencyEntry(document, maximumLabel, questionStatistics.get(0).getMax()); 
					writeCurrencyEntry(document, averageLabel, questionStatistics.get(0).getAverage());
					writeCurrencyEntry(document, standardDeviationLabel, questionStatistics.get(0).getSampleStandardDeviation());
					break;
				case DECIMAL_INPUT:
					writeEntry(document, minimumLabel, questionStatistics.get(0).getMin());
					writeEntry(document, maximumLabel, questionStatistics.get(0).getMax()); 
					writeEntry(document, averageLabel, questionStatistics.get(0).getAverage());
					writeEntry(document, standardDeviationLabel, questionStatistics.get(0).getSampleStandardDeviation());
					break;
				case DATE_INPUT:
					writeEntry(document, minimumLabel, questionStatistics.get(0).getMinDate(),date_format);
					writeEntry(document, maximumLabel, questionStatistics.get(0).getMaxDate(),date_format);
					break;
				case SINGLE_CHOICE_DROP_DOWN:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case MULTIPLE_CHOICE_CHECKBOXES:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case DATASET_DROP_DOWN:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case SINGLE_CHOICE_RADIO_BUTTONS:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case YES_NO_DROPDOWN_MATRIX:
					writeBooleanMatrixQuestionStatistics(document,question,questionStatistics,trueLabel,falseLabel);
					break;
				case SHORT_TEXT_INPUT_MATRIX:
					writeEntry(document,noStatstisticsMessage);
					break;
				case INTEGER_INPUT_MATRIX:
					writeNumericMatrixQuestionStatistics(document,question,questionStatistics,minimumLabel,maximumLabel,averageLabel,standardDeviationLabel);
					break;
				case CURRENCY_INPUT_MATRIX:
					writeCurrencyMatrixQuestionStatistics(document,question,questionStatistics,minimumLabel,maximumLabel,averageLabel,standardDeviationLabel);
					break;
				case DECIMAL_INPUT_MATRIX:
					writeNumericMatrixQuestionStatistics(document,question,questionStatistics,minimumLabel,maximumLabel,averageLabel,standardDeviationLabel);
					break;
				case DATE_INPUT_MATRIX:
					writeDateMatrixQuestionStatistics(document,question,questionStatistics,minimumLabel,maximumLabel,date_format);
					break;
				case IMAGE_DISPLAY:
					writeEntry(document,noStatstisticsMessage);
					break;
				case VIDEO_DISPLAY:
					writeEntry(document,noStatstisticsMessage);
					break;
				case FILE_UPLOAD:
					writeEntry(document,noStatstisticsMessage);
					break;
				case STAR_RATING:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				case SMILEY_FACES_RATING:
					writeOptionsQuestionStatistics(document,question,questionStatistics,optionLabel,optionFrequencyLabel);
					break;
				}
			}
		}
	}

	private void writeTitle(Document document,String title) throws Exception{
		Paragraph titleParagraph = new Paragraph(title,titleFont);
		titleParagraph.setAlignment(Element.ALIGN_LEFT);
		titleParagraph.setLeading(30);
		titleParagraph.setSpacingAfter(2);
		document.add(titleParagraph);
	}
	
	private void writeSubTitle(Document document,String title) throws Exception{
		Paragraph titleParagraph = new Paragraph(title,subTitleFont);
		titleParagraph.setAlignment(Element.ALIGN_LEFT);
		titleParagraph.setLeading(20);
		titleParagraph.setSpacingAfter(2);
		document.add(titleParagraph);
	}
	
	private void writeEntry(Document document,String label , String value) throws Exception{
		Paragraph questionParagraph = new Paragraph();
		questionParagraph.setLeading(14, 0);
		questionParagraph.setIndentationLeft(18);
		questionParagraph.add(new Chunk(label.trim() + ": ",boldedFont)); 
	    questionParagraph.add(new Chunk(value == null ? "": value.trim() ,normalFont));  
		document.add(questionParagraph);
	}
	
	private void writeEntry(Document document,String label) throws Exception{
		Paragraph questionParagraph = new Paragraph();
		questionParagraph.setLeading(14, 0);
		questionParagraph.setIndentationLeft(18);
		questionParagraph.add(new Chunk(label.trim()  ,boldedFont)); 
	    document.add(questionParagraph);
	}
	
	private void writeEntry(Document document,String label , double value) throws Exception{
		Paragraph questionParagraph = new Paragraph();
		questionParagraph.setLeading(14, 0);
		questionParagraph.setIndentationLeft(18);
		questionParagraph.add(new Chunk(label.trim() + ": ",boldedFont)); 
	    questionParagraph.add(new Chunk(BigDecimalValidator.getInstance().format(value , LocaleContextHolder.getLocale()) , normalFont));  
		document.add(questionParagraph);
	}
	
	private void writeEntry(Document document,String label , Date value, String dateFormat) throws Exception{
		Paragraph questionParagraph = new Paragraph();
		questionParagraph.setLeading(14, 0);
		questionParagraph.setIndentationLeft(18);
		questionParagraph.add(new Chunk(label.trim() + ": ",boldedFont)); 
	    questionParagraph.add(new Chunk(DateValidator.getInstance().format(value ,dateFormat) , normalFont));  
		document.add(questionParagraph);
	}
	
	private void writeCurrencyEntry(Document document,String label , double value) throws Exception{
		Paragraph questionParagraph = new Paragraph();
		questionParagraph.setLeading(14, 0);
		questionParagraph.setIndentationLeft(18);
		questionParagraph.add(new Chunk(label.trim() + ": ",boldedFont)); 
	    questionParagraph.add(new Chunk(CurrencyValidator.getInstance().format(value , LocaleContextHolder.getLocale()) , normalFont));  
		document.add(questionParagraph);
	}
	
	
	
	private Table createOptionsQuestionStatisticsTableHeader(String optionLabel,
															 String optionFrequencyLabel)throws Exception{
		
		Table  statsTable;
		Cell cell;
		
		statsTable = new Table (7);
		statsTable.setWidth(94);
		statsTable.setBorder(0);
		statsTable.setOffset(5);
		statsTable.setPadding(2);
		statsTable.setDefaultCellBorder(0);
		
		statsTable.setWidths(new int[]{4, 2, 1,1,1,1,1});
		
		cell =new Cell(new Paragraph(optionLabel,boldedFont));
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph(optionFrequencyLabel,boldedFont));
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph("20%",boldedFont));
		cell.setBorder(Cell.BOTTOM);
		cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph("40%",boldedFont));
		cell.setBorder(Cell.BOTTOM);
		cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph("60%",boldedFont));
		cell.setBorder(Cell.BOTTOM);
		cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph("80%",boldedFont));
		cell.setBorder(Cell.BOTTOM);
		cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
		statsTable.addCell(cell);
		
		cell =new Cell(new Paragraph("100%",boldedFont));
		cell.setBorder(Cell.BOTTOM);
		cell.setHorizontalAlignment(cell.ALIGN_RIGHT);
		statsTable.addCell(cell);
		return statsTable;
	}
	private void writeBooleanQuestionStatistics(Document document,
												Question question ,
												List<QuestionStatistic> questionStatistics,
												String optionLabel,
												String optionFrequencyLabel,
												String trueLabel,
												String falseLabel) 
												throws Exception{
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		
		Table statsTable =  createOptionsQuestionStatisticsTableHeader(optionLabel,optionFrequencyLabel);
		Cell cell;
		Boolean foundOption = false;
		
		cell =new Cell(new Paragraph(trueLabel,normalFont));
		statsTable.addCell(cell);
		if (questionStatistics!=null && questionStatistics.size() > 0) {
			for (QuestionStatistic questionStatistic :questionStatistics) {
				if (questionStatistic.getEntry().equals("1")) {
					foundOption = true;
					cell =new Cell(new Paragraph(percentFormat.format(questionStatistic.getFrequency()),normalFont));
					statsTable.addCell(cell);

					cell =new Cell();
					Image img = Image.getInstance(this.getClass().getResource("/chartbar.png"));
					cell.setColspan(5);
					img.scaleAbsolute((float) (questionStatistic.getFrequency() * 210),10f);
					cell.addElement(img);
					cell.setVerticalAlignment(Cell.ALIGN_BOTTOM);
					statsTable.addCell(cell);
					break;
				}
			}
		}
		if (!foundOption) {
			cell =new Cell(new Paragraph(percentFormat.format(0) , normalFont));
			statsTable.addCell(cell);
		
			cell =new Cell();
			cell.setColspan(5);
			statsTable.addCell(cell);
		}
		
		foundOption = false;
		cell =new Cell(new Paragraph(falseLabel,normalFont));
		statsTable.addCell(cell);
		if (questionStatistics!=null && questionStatistics.size() > 0) {
			for (QuestionStatistic questionStatistic :questionStatistics) {
				if (questionStatistic.getEntry().equals("0")) {
					foundOption = true;
					cell =new Cell(new Paragraph(percentFormat.format(questionStatistic.getFrequency()),normalFont));
					statsTable.addCell(cell);

					cell =new Cell();
					Image img = Image.getInstance(this.getClass().getResource("/chartbar.png"));
					cell.setColspan(5);
					img.scaleAbsolute((float) (questionStatistic.getFrequency() * 210),10f);
					cell.addElement(img);
					cell.setVerticalAlignment(Cell.ALIGN_BOTTOM);
					statsTable.addCell(cell);
					break;
				}
			}
		}
		if (!foundOption) {
			cell =new Cell(new Paragraph(percentFormat.format(0) , normalFont));
			statsTable.addCell(cell);
		
			cell =new Cell();
			cell.setColspan(5);
			statsTable.addCell(cell);
		}
		document.add(statsTable);
		
	}
	
	private void writeOptionsQuestionStatistics(Document document,
												Question question ,
												List<QuestionStatistic> questionStatistics,
												String optionLabel,
												String optionFrequencyLabel) 
												throws Exception{
		
		
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		
		Table statsTable =  createOptionsQuestionStatisticsTableHeader(optionLabel,optionFrequencyLabel);
		Cell cell;
		
		int rowIndex = 0;
		for (QuestionOption option : question.getOptions()){
			Boolean foundOption = false;
			cell =new Cell(new Paragraph(option.getText(),normalFont));
			//if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
			statsTable.addCell(cell);
			
			if (questionStatistics!=null && questionStatistics.size() > 0) {
				for (QuestionStatistic questionStatistic :questionStatistics) {
						if (question.getType().getIsMultipleValue()) {
							//multiple value question (checkboxes) match on order
							if (questionStatistic.getOptionOrder().equals(option.getOrder())){	
								foundOption = true;
	
								cell =new Cell(new Paragraph(percentFormat.format(questionStatistic.getFrequency()),normalFont));
								statsTable.addCell(cell);
	
								cell =new Cell();
								Image img = Image.getInstance(this.getClass().getResource("/chartbar.png"));
								cell.setColspan(5);
								img.scaleAbsolute((float) (questionStatistic.getFrequency() * 210),10f);
								cell.addElement(img);
								cell.setVerticalAlignment(Cell.ALIGN_BOTTOM);
								statsTable.addCell(cell);
								break;
							}
						}
						else {
							//single value question match on value
							if (questionStatistic.getEntry()!=null && questionStatistic.getEntry().equals(option.getValue())){
								foundOption = true;
								cell =new Cell(new Paragraph(percentFormat.format(questionStatistic.getFrequency()),normalFont));
								//if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
								statsTable.addCell(cell);
	
								cell =new Cell();
								//if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
								Image img = Image.getInstance(this.getClass().getResource("/chartbar.png"));
								cell.setColspan(5);
								img.scaleAbsolute((float) (questionStatistic.getFrequency() * 210),10f);
								cell.addElement(img);
								cell.setVerticalAlignment(Cell.ALIGN_BOTTOM);
								statsTable.addCell(cell);
								break;
							}
							
							
							
						}
				}
			} 
 
			if (!foundOption) {
				
				cell =new Cell(new Paragraph(percentFormat.format(0) , normalFont));
				//if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				statsTable.addCell(cell);
				
				cell =new Cell();
				//if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				cell.setColspan(5);
				statsTable.addCell(cell);
			}
			rowIndex++;	
		}
		document.add(statsTable);
		
	}
	
	
	
	
	
	private void writeBooleanMatrixQuestionStatistics(Document document,
													  Question question ,
													  List<QuestionStatistic> questionStatistics,
													  String trueLabel,
													  String falseLabel) 
					throws Exception{

		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);
		
		Table  statsTable;
		Cell cell;
		
		statsTable = new Table (question.getColumnLabels().size() +1);
		statsTable.setWidth(94);
		statsTable.setBorder(0);
		statsTable.setOffset(5);
		statsTable.setPadding(2);
		statsTable.setDefaultCellBorder(0);
		
		//header
		cell = new Cell();
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
			cell = new Cell(new Paragraph(columnLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.BOTTOM);
			statsTable.addCell(cell);
		}
		int rowIndex = 1;
		for (QuestionRowLabel rowLabel:question.getRowLabels()) {
			cell =new Cell(new Paragraph(rowLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.RIGHT);
			if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
			statsTable.addCell(cell);
			for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
				boolean found = false; 
				cell = new Cell();
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				for (QuestionStatistic questionStatistic :questionStatistics) {
					if (questionStatistic.getRowOrder().equals(rowLabel.getOrder()) &&
						questionStatistic.getColumnOrder().equals(columnLabel.getOrder()) &&
						questionStatistic.getEntry().equals("1")){
						cell.add(new Paragraph(trueLabel + ": "+ percentFormat.format(questionStatistic.getFrequency()) ,normalFont));
						found = true; 
						break;
					}
				}
				if (!found) {cell.add(new Paragraph(trueLabel + ": "+ percentFormat.format(0) ,normalFont));}
				
				found = false; 
				for (QuestionStatistic questionStatistic :questionStatistics) {
					if (questionStatistic.getRowOrder().equals(rowLabel.getOrder()) &&
						questionStatistic.getColumnOrder().equals(columnLabel.getOrder()) &&
						questionStatistic.getEntry().equals("0")){
						cell.add(new Paragraph(falseLabel + ": "+ percentFormat.format(questionStatistic.getFrequency()) ,normalFont));
						found = true;
						break;
					}
				}
				if (!found) {cell.add(new Paragraph(falseLabel + ": "+ percentFormat.format(0) ,normalFont));}
				
				statsTable.addCell(cell);
			}
			rowIndex++;	
		}
		
		document.add(statsTable);

	}

	private void writeNumericMatrixQuestionStatistics(Document document,
													  Question question ,
													  List<QuestionStatistic> questionStatistics,
													  String minimumLabel,
													  String maximumLabel,
													  String averageLabel,
													  String standardDeviationLabel) 
					throws Exception{

		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);

		Table  statsTable;
		Cell cell;

		statsTable = new Table (question.getColumnLabels().size() +1);
		statsTable.setWidth(94);
		statsTable.setBorder(0);
		statsTable.setOffset(5);
		statsTable.setPadding(2);
		statsTable.setDefaultCellBorder(0);

		//header
		cell = new Cell();
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
			cell = new Cell(new Paragraph(columnLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.BOTTOM);
			statsTable.addCell(cell);
		}
		int rowIndex = 1;
		for (QuestionRowLabel rowLabel:question.getRowLabels()) {
			cell =new Cell(new Paragraph(rowLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.RIGHT);
			if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
			statsTable.addCell(cell);
			for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
				boolean found = false; 
				cell = new Cell();
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				for (QuestionStatistic questionStatistic :questionStatistics) {
					if (questionStatistic.getRowOrder().equals(rowLabel.getOrder()) &&
						questionStatistic.getColumnOrder().equals(columnLabel.getOrder())){
						cell.add(new Paragraph(minimumLabel + ": "+ BigDecimalValidator.getInstance().format(questionStatistic.getMin() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(maximumLabel + ": "+ BigDecimalValidator.getInstance().format(questionStatistic.getMax() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(averageLabel + ": "+ BigDecimalValidator.getInstance().format(questionStatistic.getAverage() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(standardDeviationLabel + ": "+ BigDecimalValidator.getInstance().format(questionStatistic.getSampleStandardDeviation() , LocaleContextHolder.getLocale()) ,normalFont));
						
						
						break;
					}
				}
				if (!found) {}

				

				statsTable.addCell(cell);
			}
			rowIndex++;	
		}

		document.add(statsTable);

	}


	private void writeCurrencyMatrixQuestionStatistics(Document document,
			Question question ,
			List<QuestionStatistic> questionStatistics,
			String minimumLabel,
			String maximumLabel,
			String averageLabel,
			String standardDeviationLabel) 
					throws Exception{

		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);

		Table  statsTable;
		Cell cell;

		statsTable = new Table (question.getColumnLabels().size() +1);
		statsTable.setWidth(94);
		statsTable.setBorder(0);
		statsTable.setOffset(5);
		statsTable.setPadding(2);
		statsTable.setDefaultCellBorder(0);

		//header
		cell = new Cell();
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
			cell = new Cell(new Paragraph(columnLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.BOTTOM);
			statsTable.addCell(cell);
		}
		int rowIndex = 1;
		for (QuestionRowLabel rowLabel:question.getRowLabels()) {
			cell =new Cell(new Paragraph(rowLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.RIGHT);
			if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
			statsTable.addCell(cell);
			for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
				boolean found = false; 
				cell = new Cell();
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				for (QuestionStatistic questionStatistic :questionStatistics) {
					if (questionStatistic.getRowOrder().equals(rowLabel.getOrder()) &&
							questionStatistic.getColumnOrder().equals(columnLabel.getOrder())){
						cell.add(new Paragraph(minimumLabel + ": "+ CurrencyValidator.getInstance().format(questionStatistic.getMin() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(maximumLabel + ": "+ CurrencyValidator.getInstance().format(questionStatistic.getMax() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(averageLabel + ": "+ CurrencyValidator.getInstance().format(questionStatistic.getAverage() , LocaleContextHolder.getLocale()) ,normalFont));
						cell.add(new Paragraph(standardDeviationLabel + ": "+ CurrencyValidator.getInstance().format(questionStatistic.getSampleStandardDeviation() , LocaleContextHolder.getLocale()) ,normalFont));


						break;
					}
				}
				if (!found) {}



				statsTable.addCell(cell);
			}
			rowIndex++;	
		}

		document.add(statsTable);

	}




	private void writeDateMatrixQuestionStatistics(Document document,
			Question question ,
			List<QuestionStatistic> questionStatistics,
			String minimumLabel,
			String maximumLabel,
			String dateFormat) 
					throws Exception{

		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMaximumFractionDigits(1);

		Table  statsTable;
		Cell cell;

		statsTable = new Table (question.getColumnLabels().size() +1);
		statsTable.setWidth(94);
		statsTable.setBorder(0);
		statsTable.setOffset(5);
		statsTable.setPadding(2);
		statsTable.setDefaultCellBorder(0);

		//header
		cell = new Cell();
		cell.setBorder(Cell.BOTTOM);
		statsTable.addCell(cell);
		for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
			cell = new Cell(new Paragraph(columnLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.BOTTOM);
			statsTable.addCell(cell);
		}
		int rowIndex = 1;
		for (QuestionRowLabel rowLabel:question.getRowLabels()) {
			cell =new Cell(new Paragraph(rowLabel.getLabel(),boldedFont));
			cell.setBorder(Cell.RIGHT);
			if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
			statsTable.addCell(cell);
			for (QuestionColumnLabel columnLabel:question.getColumnLabels()) {
				boolean found = false; 
				cell = new Cell();
				if ((rowIndex % 2) == 1) {cell.setBackgroundColor(new Color(244,244,244));}
				for (QuestionStatistic questionStatistic :questionStatistics) {
					if (questionStatistic.getRowOrder().equals(rowLabel.getOrder()) &&
						questionStatistic.getColumnOrder().equals(columnLabel.getOrder())){
						cell.add(new Paragraph(minimumLabel + ": "+ DateValidator.getInstance().format(questionStatistic.getMinDate() , dateFormat) ,normalFont));
						cell.add(new Paragraph(maximumLabel + ": "+ DateValidator.getInstance().format(questionStatistic.getMaxDate() , dateFormat) ,normalFont));
						break;
					}
				}
				if (!found) {}



				statsTable.addCell(cell);
			}
			rowIndex++;	
		}

		document.add(statsTable);

	}























}