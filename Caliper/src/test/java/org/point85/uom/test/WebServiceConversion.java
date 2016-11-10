package org.point85.uom.test;

import com.google.gson.annotations.SerializedName;

public class WebServiceConversion {
	@SerializedName("XML4PharmaServerWebServiceResponse")
	private WebServiceResponse webServiceResponse;

	public WebServiceResponse getWebServiceResponse() {
		return webServiceResponse;
	}

	public void setWebServiceResponse(WebServiceResponse webServiceResponse) {
		this.webServiceResponse = webServiceResponse;
	}
}
