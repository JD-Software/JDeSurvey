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
package com.jd.survey.domain.settings;

public enum QuestionType {
	
	//YES_NO_CHECKBOX("BC"),
	YES_NO_DROPDOWN("BR"),
	SHORT_TEXT_INPUT("ST"),
	LONG_TEXT_INPUT("LT"),
	HUGE_TEXT_INPUT("HT"),
	INTEGER_INPUT("IN"),
	CURRENCY_INPUT("CR"),
	DECIMAL_INPUT("NM"),
	DATE_INPUT("DT"),
	SINGLE_CHOICE_DROP_DOWN("SD"),
	MULTIPLE_CHOICE_CHECKBOXES("MC"),
	DATASET_DROP_DOWN("DD"),
	SINGLE_CHOICE_RADIO_BUTTONS("SR"),
	//YES_NO_CHECKBOX_MATRIX("BCM"),
	YES_NO_DROPDOWN_MATRIX("BRM"),
	SHORT_TEXT_INPUT_MATRIX("STM"),
	INTEGER_INPUT_MATRIX("INM"),
	CURRENCY_INPUT_MATRIX("CRM"),
	DECIMAL_INPUT_MATRIX("NMM"),
	DATE_INPUT_MATRIX("DTM"),	
	
	IMAGE_DISPLAY("IMG"),
	VIDEO_DISPLAY("VID"),
	FILE_UPLOAD("FIL"),
	
	
	STAR_RATING("STR"),
	SMILEY_FACES_RATING("SFR");
	
	private String code;
	 
	private QuestionType(String c) {
	   code = c;
	}
	 
	public String getCode() {
	  return code;
	}
 
	public Boolean getIsDataSet() {
		if (this == QuestionType.DATASET_DROP_DOWN ) {
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public Boolean getIsRating() {
		if (this == QuestionType.STAR_RATING || 
			this == QuestionType.SMILEY_FACES_RATING ) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public Boolean getIsMatrix() {
		if (this == QuestionType.YES_NO_DROPDOWN_MATRIX || 
			this == QuestionType.SHORT_TEXT_INPUT_MATRIX ||
			this == QuestionType.INTEGER_INPUT_MATRIX ||
			this== QuestionType.CURRENCY_INPUT_MATRIX ||
			this == QuestionType.DECIMAL_INPUT_MATRIX ||
			this== QuestionType.DATE_INPUT_MATRIX) {
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public Boolean getRequiresOptions() {
		if (this== QuestionType.MULTIPLE_CHOICE_CHECKBOXES || 
			this == QuestionType.SINGLE_CHOICE_DROP_DOWN ||
			this == QuestionType.SINGLE_CHOICE_RADIO_BUTTONS ||
			this == QuestionType.STAR_RATING ||
			this == QuestionType.SMILEY_FACES_RATING||
			this == QuestionType.DATASET_DROP_DOWN) {
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public Boolean getIsMultipleValue() {
		if (this== QuestionType.MULTIPLE_CHOICE_CHECKBOXES ) {
			return true;
		}
		else{
			return false;
		}
	}
	
	public Boolean getIsTextInput(){
		if (this == QuestionType.HUGE_TEXT_INPUT  ||
			this == QuestionType.LONG_TEXT_INPUT  ||
			this == QuestionType.SHORT_TEXT_INPUT
			){
			
			return true;
		}
		else {

			return false;
		}

	}
	
	
	public static QuestionType fromCode(String code) {
	    if (code != null) {
	      for (QuestionType b : QuestionType.values()) {
	        if (code.equalsIgnoreCase(b.code)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
	
	
	
	
}
