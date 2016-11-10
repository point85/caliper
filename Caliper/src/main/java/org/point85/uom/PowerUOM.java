package org.point85.uom;

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * The PowerUOM represents a unit of measure with an exponent, for example
 * square metres.
 * 
 * @author Kent Randall
 *
 */
public class PowerUOM extends AbstractUnitOfMeasure implements Serializable {

	private static final long serialVersionUID = 2233946797577576713L;

	// exponent, e.g. "2"
	private int power = 0;

	PowerUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}

	void setUnits(UnitOfMeasure base, int power) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("base.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		this.setBase(base);
		this.setPower(power);
	}

	/**
	 * Get the exponent
	 * 
	 * @return Exponent
	 */
	public int getPower() {
		return power;
	}

	private void setPower(int power) {
		this.power = power;
	}

	/**
	 * Get the base unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getBase() {
		return uom1;
	}

	private void setBase(UnitOfMeasure base) {
		this.uom1 = base;
	}
}
