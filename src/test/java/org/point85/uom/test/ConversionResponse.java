/*
MIT License

Copyright (c) 2016 Kent Randall

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
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
