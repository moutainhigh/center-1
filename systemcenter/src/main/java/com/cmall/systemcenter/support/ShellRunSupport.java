package com.cmall.systemcenter.support;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 脚本命令执行
 */
public class ShellRunSupport {

	public RunResult exec(List<String> commands) {
		RunResult result = new RunResult();
		Process proc = null;
		BufferedReader stdInput = null;
		BufferedReader stdError = null;
		
		List<String> inputList = new ArrayList<String>();
		List<String> errorList = new ArrayList<String>();
		
		try {
			proc = new ProcessBuilder(commands).start();
			stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			inputList.addAll(IOUtils.readLines(stdInput));
			inputList.addAll(IOUtils.readLines(stdError));
			
			proc.waitFor();
			
			inputList.addAll(IOUtils.readLines(stdInput));
			inputList.addAll(IOUtils.readLines(stdError));
			
			IOUtils.closeQuietly(stdInput);
			IOUtils.closeQuietly(stdError);
			
			result.setExitValue(proc.exitValue());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(stdInput);
			IOUtils.closeQuietly(stdError);
		}
		
		result.setResult(StringUtils.join(inputList,"\n"));
		result.setError(StringUtils.join(errorList,"\n"));
		
		return result;
	}
	
	public static class RunResult {
		private int exitValue = -1;
		private String result;
		private String error;
		
		public int getExitValue() {
			return exitValue;
		}
		public void setExitValue(int exitValue) {
			this.exitValue = exitValue;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getError() {
			return error;
		}
		public void setError(String error) {
			this.error = error;
		}
	}
}
