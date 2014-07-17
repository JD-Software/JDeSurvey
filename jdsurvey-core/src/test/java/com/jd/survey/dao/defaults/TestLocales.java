package com.jd.survey.dao.defaults;

import java.text.*;
import java.util.*;

import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

class TestLocales {
	
	
	public void currencyFormat(Locale currentLocale) {
		Double currency = new Double(9843.21);
		NumberFormat currencyFormatter;
		String currencyOut;
		currencyFormatter = NumberFormat.getCurrencyInstance(currentLocale);
		currencyOut = currencyFormatter.format(currency);
		System.out.println(currencyOut + " " + currentLocale.toString());
	}

	public static void main(String args[]) {
		
	
	
		/*
		PasswordEncoder encoder = new ShaPasswordEncoder(512);
		System.out.println(encoder.encodePassword("ddddd","WW"));
		for (int i = 1 ; i<20; i++) {
			String uuid = UUID.randomUUID().toString().replace("-", "");
			System.out.println("uuid = " + uuid);
		}
		*/
		/*
		Integer[][] integerAnswersMatrix; 
		int numberofRows = 3;
		int numberofColumns = 4;
		integerAnswersMatrix = new Integer[numberofRows][numberofColumns];
		
		
		int val = 100;
		for (int i = 0;  i<numberofRows ; i++) {
			for (int j = 0;  j<numberofColumns ; j++) {
				integerAnswersMatrix[i][j]= val;
				val++;
			}
		}
		
		for (int i = 0;  i<numberofRows ; i++) {
			for (int j = 0;  j<numberofColumns ; j++) {
				System.out.println("integerAnswersMatrix[" + i + "][" + j + "]="+ integerAnswersMatrix[i][j]);
				val++;
			}
		}
		*/
		
		
		
		
		/*
		Double currency = new Double("1234567.89");
		String currencyOut;
		Locale locale = new Locale("en", "US");
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
		currencyOut = currencyFormatter.format(currency);
		System.out.println(currencyOut + " " + locale.toString());
		
		
		String valueToValidate = "$123467.89";
		CurrencyValidator currencyValidator = new CurrencyValidator(true, true);
		if (!currencyValidator.isValid(valueToValidate, locale)){
			System.out.println("not Valid");
		}
		
		System.out.println(currencyValidator.format(currencyValidator.validate(valueToValidate, locale),locale));
		
		valueToValidate = "123,467.89";
		BigDecimalValidator bigDecimalValidator = new BigDecimalValidator(true);
		if (!bigDecimalValidator.isValid(valueToValidate, locale)){
			System.out.println("not Valid");
		}
		
		System.out.println(bigDecimalValidator.format(bigDecimalValidator.validate(valueToValidate, locale), locale));
		
		
		
		
		
		
		
		
		Locale[] locales = new Locale[]{new Locale("fr", "FR"),
				new Locale("de", "DE"), new Locale("ca", "CA"),
				new Locale("rs", "RS"),new Locale("en", "IN")
		};

		TestLocales[] formate =	new TestLocales[locales.length];

		for (int i = 0; i < locales.length; i++) {
			formate[i].currencyFormat(locales[i]);
		}
		*/
	}
}
