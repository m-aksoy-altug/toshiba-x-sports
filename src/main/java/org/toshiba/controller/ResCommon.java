package org.toshiba.controller;

public class ResCommon {
	private String ErrMessage;
	private int Status;

	public int getStatus() {
		return this.Status;
	}

	public void setStatus(int status) {
		this.Status = status;
	}

	public String getErrMessage() {
		return this.ErrMessage;
	}

	public void setErrMessage(String errMessage) {
		this.ErrMessage = errMessage;
	}
}
