package com.jd.survey.dao.defaults;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Period;
import org.joda.time.Seconds;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.jd.survey.util.StringHelper;

//@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleTests {

	
	@Test
	public void MyTest() {
		try {
			
		
			String videoUrl = "<iframe src=\"http://player.vimeo.com/video/55637436?title=0&amp;byline=0&amp;portrait=0\" width=\"500\" height=\"279\" frameborder=\"0\" webkitAllowFullScreen mozallowfullscreen allowFullScreen></iframe>";
		
					
			videoUrl = videoUrl.replaceAll("width=\"([^\"]*)\"", "width=\"100%\"");
			videoUrl = videoUrl.replaceAll("width='([^']*)'", "width='100%'");
			
			videoUrl = videoUrl.replaceAll("height=\"([^\"]*)\"", "");
			videoUrl = videoUrl.replaceAll("height='([^']*)'", "");
			
			System.out.println(videoUrl);
			
		
		/*
			QuestionType qtype = QuestionType.CURRENCY_INPUT;
			System.out.println(qtype.getCode());
			switch (qtype) {
			case  DATASET_DROP_DOWN: //Yes No Checkbox
				System.out.println("DATASET_DROP_DOWN");
				break;
			case  CURRENCY_INPUT: //Yes No Checkbox
				System.out.println("CURRENCY_INPUT");
				break;
			}	
			
			
		
		Date date1 =new Date();
		String key = KeyGenerators.string().generateKey();
		String salt = KeyGenerators.string().generateKey();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-yyyy HH:mm:ss"); //please notice the capital M
		System.out.println(formatter.format(date1));
		
		
		
		
		String message = "admieeeen###" + formatter.format(date1);
		System.out.println(message);
		String encryptedMessage;
		String decryptedMessage;
		byte[] encryptedBinaryByteArray;
		byte[] encryptedAsciiByteArray;
		byte[] decryptedAsciiByteArray;
		
		
		
		BytesEncryptor bytesEncryptor;
	
		
		
			bytesEncryptor = Encryptors.standard(key,salt);
			
			// 256 bit AES encryption 
			encryptedBinaryByteArray  = bytesEncryptor.encrypt(message.getBytes("US-ASCII"));
			//BASE 64 encode
			encryptedAsciiByteArray = Base64.encode(encryptedBinaryByteArray);
			//print
			encryptedMessage = new String(encryptedAsciiByteArray);
			System.out.println(encryptedMessage);
			
			
			//get the byte array from the string
			encryptedAsciiByteArray = encryptedMessage.getBytes("US-ASCII");
			//BASE 64 decode the byte array 
			encryptedBinaryByteArray = Base64.decode(encryptedAsciiByteArray);
			
			
			decryptedAsciiByteArray = bytesEncryptor.decrypt(encryptedBinaryByteArray);
			decryptedMessage = new String(decryptedAsciiByteArray, "US-ASCII");
			System.out.println(decryptedMessage);
			String[] strArr= decryptedMessage.split("###");
			System.out.println(strArr[0]);
			System.out.println(strArr[1]);
			date1 = formatter.parse(strArr[1]);
			
			System.out.println(formatter.format(new Date()));
			System.out.println(new Period(new DateTime(date1), new DateTime(new Date())).getHours());

			
			*/
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
	
		
		
	}
}
