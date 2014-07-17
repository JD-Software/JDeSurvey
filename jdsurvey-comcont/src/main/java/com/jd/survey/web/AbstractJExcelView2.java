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
package com.jd.survey.web;

import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.WorkbookParser;
import jxl.write.WritableWorkbook;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.LocalizedResourceHelper;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractView;

/**+
 * A modified AbstractJExcelView class to handle an error in reading user name in linux environment. Only method modidied is renderMergedOutputModel  
 *  * @author JD Software 
 **/
public abstract class AbstractJExcelView2 extends AbstractView {

	/** The content type for an Excel response */
	private static final String CONTENT_TYPE = "application/vnd.ms-excel";

	/** The extension to look for existing templates */
	private static final String EXTENSION = ".xls";


	/** The url at which the template to use is located */
	private String url;


	/**
	 * Default Constructor.
	 * Sets the content type of the view to "application/vnd.ms-excel".
	 */
	public AbstractJExcelView2() {
		setContentType(CONTENT_TYPE);
	}

	/**
	 * Set the URL of the Excel workbook source, without localization part nor extension.
	 */
	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	protected boolean generatesDownloadContent() {
		return true;
	}

	/**
	 * Renders the Excel view, given the specified model.
	 */
	@Override
	protected final void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// Set the content type and get the output stream.
		response.setContentType(getContentType());
		OutputStream outputStream = response.getOutputStream();

		WritableWorkbook writableWorkbook;
		if (this.url != null) {

			Workbook workbook = getTemplateSource(this.url, request);
			
			//this was modified from the standard AbstractJExcelView2 spring library to handle an error parsing out the
			//username in linux environments
			WorkbookSettings workbookSettings  = ((WorkbookParser) workbook).getSettings();
			workbookSettings.setWriteAccess("BSAS");
			writableWorkbook = Workbook.createWorkbook(outputStream, workbook,workbookSettings);
		} 
		else {
			logger.debug("Creating Excel Workbook from scratch");
			writableWorkbook = Workbook.createWorkbook(outputStream);
		}

		buildExcelDocument(model, writableWorkbook, request, response);

		// Should we set the content length here?
		// response.setContentLength(workbook.getBytes().length);

		writableWorkbook.write();
		outputStream.flush();
		writableWorkbook.close();
	}

	/**
	 * Create the workbook from an existing XLS document.
	 * @param url the URL of the Excel template without localization part nor extension
	 * @param request current HTTP request
	 * @return the template workbook
	 * @throws Exception in case of failure
	 */
	protected Workbook getTemplateSource(String url, HttpServletRequest request) throws Exception {
		LocalizedResourceHelper helper = new LocalizedResourceHelper(getApplicationContext());
		Locale userLocale = RequestContextUtils.getLocale(request);
		Resource inputFile = helper.findLocalizedResource(url, EXTENSION, userLocale);

		// Create the Excel document from the source.
		if (logger.isDebugEnabled()) {
			logger.debug("Loading Excel workbook from " + inputFile);
		}
		return Workbook.getWorkbook(inputFile.getInputStream());
	}

















	/**
	 * Subclasses must implement this method to create an Excel Workbook
	 * document, given the model.
	 * @param model the model Map
	 * @param workbook the Excel workbook to complete
	 * @param request in case we need locale etc. Shouldn't look at attributes.
	 * @param response in case we need to set cookies. Shouldn't write to it.
	 * @throws Exception in case of failure
	 */
	protected abstract void buildExcelDocument(Map<String, Object> model, WritableWorkbook workbook,
			HttpServletRequest request, HttpServletResponse response) throws Exception;

}
