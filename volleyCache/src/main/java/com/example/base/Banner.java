package com.example.base;

import java.util.ArrayList;
import java.util.List;

public class Banner {
	String message;
	String errorcode;
	List<ImageUrl> data = new ArrayList<ImageUrl>();
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getErrorcode() {
		return errorcode;
	}
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}
	public List<ImageUrl> getData() {
		return data;
	}
	public void setData(List<ImageUrl> data) {
		this.data = data;
	}
}
