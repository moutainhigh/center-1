package com.cmall.groupcenter;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.cmall.groupcenter.job.JobCensusProductSales;
import com.cmall.groupcenter.job.JobForRefreshTemplateProData;
import com.srnpr.zapcom.topdo.TopTest;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TopTest
{
	
	public void runa(){
		JobCensusProductSales aa = new JobCensusProductSales();
		aa.doExecute(null);
	}
	
}
