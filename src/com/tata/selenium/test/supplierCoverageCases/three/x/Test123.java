package com.tata.selenium.test.supplierCoverageCases.three.x;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.tata.selenium.constants.ApplicationConstants;

public class Test123 {

	public static void main(String[] args) {
		
		occurance("or", "hellohelloworldhello");
	}

	public static void occurance(String Search_Term, String Search_Content) {
		try {
			int SearchTermCount = 0;
			for(int i =0;i<Search_Content.length();i++)
			{
				if(Search_Term.charAt(0) == Search_Content.charAt(i))
				{
					if(i>0)
					{
						if(Search_Content.charAt(i-1) == ' ')
						{
							if(i+Search_Term.length()<Search_Content.length())
							{
								if(Search_Content.charAt(i+Search_Term.length()) != ' ')
								{

								}
							}
						}
					}
					int count = 1;
					for(int j=1 ; j<Search_Term.length();j++){
						i++;
						if(Search_Term.charAt(j) != Search_Content.charAt(i)){
							break;
						}
						else{
							count++;
						}
					}
					if(count == Search_Term.length()){
						SearchTermCount++;
					}

				}
			}
			System.out.println("The Search Term "+ Search_Term + " was repeated :" + SearchTermCount);


		} catch (Exception e) {
			System.out.println("Error Occured");
			System.out.println(e.getMessage());
		}


	}
}
