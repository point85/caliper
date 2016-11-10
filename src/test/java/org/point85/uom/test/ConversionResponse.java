package org.point85.uom.test;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class ConversionResponse {
	@SerializedName("SourceQuantity")
	private BigDecimal sourceQuantity;
	
	@SerializedName("ResultQuantity")
	private BigDecimal resultQuantity;
	
	@SerializedName("SourceUnit")
	private String sourceUnit;
	
	@SerializedName("TargetUnit")
	private String targetUnit;
	

	public BigDecimal getSourceQuantity() {
		return sourceQuantity;
	}

	public void setSourceQuantity(BigDecimal sourceQuantity) {
		this.sourceQuantity = sourceQuantity;
	}

	public BigDecimal getResultQuantity() {
		return resultQuantity;
	}

	public void setResultQuantity(BigDecimal resultQuantity) {
		this.resultQuantity = resultQuantity;
	}

	public String getSourceUnit() {
		return sourceUnit;
	}

	public void setSourceUnit(String sourceUnit) {
		this.sourceUnit = sourceUnit;
	}

	public String getTargetUnit() {
		return targetUnit;
	}

	public void setTargetUnit(String targetUnit) {
		this.targetUnit = targetUnit;
	}

}
