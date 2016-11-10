package org.point85.uom;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * The ProductUOM represents a unit of measure that is the product of two other
 * units of measure, for example Newton-metres.
 * 
 * @author Kent Randall
 *
 */
public class ProductUOM extends AbstractUnitOfMeasure implements Serializable {

	private static final long serialVersionUID = -2557375717703721882L;

	// left of "x"
	private UnitOfMeasure multiplier;

	// right of "x"
	private UnitOfMeasure multiplicand;

	ProductUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}

	String generateSymbol() {
		final char MULT = 0xB7;
		StringBuilder sb = new StringBuilder();

		if (getMultiplier().equals(getMultiplicand())) {
			sb.append(getMultiplier().getSymbol()).append("^2");
		} else {
			sb.append(getMultiplier().getSymbol()).append(MULT).append(getMultiplicand().getSymbol());
		}
		return sb.toString();
	}

	void setUnits(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		if (multiplier == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("multiplier.cannot.be.null"),
					getSymbol());
			throw new Exception(msg);
		}

		if (multiplicand == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("multiplicand.cannot.be.null"),
					getSymbol());
			throw new Exception(msg);
		}

		this.setMultiplier(multiplier);
		this.setMultiplicand(multiplicand);
	}

	/**
	 * Get the multiplier
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplier() {
		return multiplier;
	}

	private void setMultiplier(UnitOfMeasure multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * Get the multiplicand
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplicand() {
		return multiplicand;
	}

	private void setMultiplicand(UnitOfMeasure multiplicand) {
		this.multiplicand = multiplicand;
	}
}
