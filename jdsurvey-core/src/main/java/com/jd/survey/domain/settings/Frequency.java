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


//Monthly 1
//Weekly 2

public enum Frequency {
	
	MONTHLY("1"), 	
	WEEKLY("2");
	
	private String code;
	
	private  Frequency(String c) {
		code = c;
			}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	
	
	public static Frequency fromCode(String code) {
	    if (code != null) {
	      for (Frequency b : Frequency.values()) {
	        if (code.equalsIgnoreCase(b.code)) {
	          return b;
	        }
	      }
	    }
	    return null;
	  }
	
	
	
	
	
	
	
	}