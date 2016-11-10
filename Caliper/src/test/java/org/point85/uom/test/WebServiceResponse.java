package org.point85.uom.test;

import com.google.gson.annotations.SerializedName;

public class WebServiceResponse {
	@SerializedName("ServerDateTime")
	private String serverDateTime;
	
	@SerializedName("Response")
	private ConversionResponse response;

	@SerializedName("WebServiceRequest")
	private String webServiceRequest;

	public ConversionResponse getResponse() {
		return response;
	}

	public void setResponse(ConversionResponse response) {
		this.response = response;
	}

	public String getWebServiceRequest() {
		return webServiceRequest;
	}

	public void setWebServiceRequest(String webServiceRequest) {
		this.webServiceRequest = webServiceRequest;
	}

}
