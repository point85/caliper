package org.point85.uom;

import java.io.Serializable;

/**
 * The ScalarUOM represents a simple unit of measure, for example a metre.
 * 
 * @author Kent Randall
 *
 */
public class ScalarUOM extends AbstractUnitOfMeasure implements Serializable {

	private static final long serialVersionUID = -2065109288225305453L;

	ScalarUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}
}
