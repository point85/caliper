package org.point85.uom;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * The QuotientUOM represents a unit of measure that is the ratio of two other
 * units of measure, for example metres per second.
 * 
 * @author Kent Randall
 *
 */
public class QuotientUOM extends AbstractUnitOfMeasure implements Serializable {

	private static final long serialVersionUID = -4338453032032082553L;

	QuotientUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}

	String generateSymbol() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDividend().getSymbol()).append('/').append(getDivisor().getSymbol());
		return sb.toString();
	}

	void setUnits(UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
		if (dividend == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("dividend.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		if (divisor == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("divisor.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		this.setDividend(dividend);
		this.setDivisor(divisor);
	}

	/**
	 * Get the dividend unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDividend() {
		return uom1;
	}

	private void setDividend(UnitOfMeasure dividend) {
		this.uom1 = dividend;
	}

	/**
	 * Get the divisor unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDivisor() {
		return uom2;
	}

	private void setDivisor(UnitOfMeasure divisor) {
		this.uom2 = divisor;
	}
}
