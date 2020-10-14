package com.cmall.groupcenter.kjt.response;

import java.util.List;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;

public class RsyncResponseTraceOrder extends RsyncKjtResponseBase {

	private Data Data ;

	
	public Data getData() {
		return Data;
	}


	public void setData(Data data) {
		Data = data;
	}
	
	public static class Data {
		private List<SoOrder> TraceOrderList;

		public List<SoOrder> getTraceOrderList() {
			return TraceOrderList;
		}

		public void setTraceOrderList(List<SoOrder> traceOrderList) {
			TraceOrderList = traceOrderList;
		}
		
	}
	
	public static class SoOrder {
		private int SOID;
		private int SOStatus;
		private List<Solog> Logs;
		public int getSOID() {
			return SOID;
		}
		public void setSOID(int sOID) {
			SOID = sOID;
		}
		public int getSOStatus() {
			return SOStatus;
		}
		public void setSOStatus(int sOStatus) {
			SOStatus = sOStatus;
		}
		public List<Solog> getLogs() {
			return Logs;
		}
		public void setLogs(List<Solog> logs) {
			Logs = logs;
		}
	} 
	
	
	public static class Solog {
		private String OptTime;
		private int OptType;
		private String OptNote;
		public String getOptTime() {
			return OptTime;
		}
		public void setOptTime(String optTime) {
			OptTime = optTime;
		}
		public int getOptType() {
			return OptType;
		}
		public void setOptType(int optType) {
			OptType = optType;
		}
		public String getOptNote() {
			return OptNote;
		}
		public void setOptNote(String optNote) {
			OptNote = optNote;
		}
	}

}

