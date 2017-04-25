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
package org.point85.uom;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A UnitOfMeasure can have a linear {@link Conversion} (y = ax + b) to another
 * unit of measure in the same internationally recognized measurement system of
 * International Customary, SI, US or British Imperial. Or, the unit of measure
 * can have a conversion to another custom unit of measure. It is owned by the
 * unified {@link MeasurementSystem} defined by this project.
 * </p>
 * 
 * <p>
 * A unit of measure is categorized by scalar (simple unit), quotient (divisor
 * and dividend units), product (multiplier and multiplicand units) or power
 * (unit with an integral exponent). More than one representation of a unit of
 * measure is possible. For example, a unit of "per second" could be a quotient
 * of "1/s" (e.g. an inverted second) or a power of s^-1.
 * </p>
 * 
 * <p>
 * A unit of measure also has an enumerated {@link UnitType} (for example LENGTH
 * or MASS) and a unique {@link Unit} discriminator (for example METRE). <br>
 * A basic unit (a.k.a fundamental unit in the SI system) can have a bridge
 * {@link Conversion} to another basic unit in another recognized measurement
 * system. This conversion is defined unidirectionally. For example, an
 * International Customary foot is 0.3048 SI metres. The conversion from metre
 * to foot is just the inverse of this relationship.
 * </p>
 * 
 * <p>
 * A unit of measure has a base symbol, for example 'm' for metre. A base symbol
 * is one that consists only of the symbols for the base units of measure. In
 * the SI system, the base units are well-defined. The derived units such as
 * Newton all have base symbols expressed in the fundamental units of length
 * (metre), mass (kilogram), time (second), temperature (Kelvin), plane angle
 * (radian), electric charge (Coulomb) and luminous intensity (candela). In the
 * US and British systems, base units are not defined. Caliper uses foot for
 * length, pound mass for mass and Rankine for temperature. This base symbol is
 * used in unit of measure conversions to uniquely identify the target unit.
 * </p>
 * <p>
 * The SI system has defined prefixes (e.g. "centi") for 1/100th of another unit
 * (e.g. metre). Instead of defining all the possible unit of measure
 * combinations, the {@link MeasurementSystem} is able to create units by
 * specifying the {@link Prefix} and target unit of measure. Similarly, computer
 * science has defined prefixes for bytes (e.g. "mega").
 * 
 * @author Kent Randall
 *
 */
public class UnitOfMeasure extends Symbolic implements Comparable<UnitOfMeasure> {
	// BigDecimal math. A MathContext object with a precision setting matching
	// the IEEE 754R Decimal64 format, 16 digits, and a rounding mode of
	// HALF_EVEN, the IEEE 754R default.
	public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

	// maximum length of the symbol
	private static final int MAX_SYMBOL_LENGTH = 16;

	// multiply, divide and power symbols
	private static final char MULT = 0xB7;
	private static final char DIV = '/';
	private static final char POW = '^';
	private static final char SQ = 0xB2;
	private static final char CUBED = 0xB3;
	private static final char LP = '(';
	private static final char RP = ')';
	private static final char ONE_CHAR = '1';

	// registry of unit conversion factor
	private Map<UnitOfMeasure, BigDecimal> conversionRegistry = new ConcurrentHashMap<UnitOfMeasure, BigDecimal>();

	// conversion to another Unit of Measure in the same recognized measurement
	// system (y = ax + b)
	private Conversion conversion;

	// unit enumerations for the various systems of measurement, e.g. KILOGRAM
	private Unit unit;

	// unit type, e.g. MASS
	private UnitType unitType = UnitType.UNCLASSIFIED;

	// conversion to another Unit of Measure in a different measurement system
	private Conversion bridgeConversion;

	// cached base symbol
	private String baseSymbol;

	// user-defined category
	private String category;

	// base UOMs and exponents for a product of two power UOMs follow
	// power base unit, product multiplier or quotient dividend
	private UnitOfMeasure uom1;

	// product multiplicand or quotient divisor
	private UnitOfMeasure uom2;

	// exponent
	private Integer exponent1;

	// second exponent
	private Integer exponent2;

	/**
	 * Construct a default unit of measure
	 */
	public UnitOfMeasure() {
		super();
		initialize();
	}

	UnitOfMeasure(UnitType type, String name, String symbol, String description) {
		super(name, symbol, description);
		this.unitType = type;
		initialize();
	}

	private void initialize() {
		// a unit can always be converted to itself
		try {
			this.conversion = new Conversion(this);
		} catch (Exception e) {
			// should not happen
		}
	}

	private void setPowerProduct(UnitOfMeasure uom1, Integer exponent1) {
		this.uom1 = uom1;
		this.exponent1 = exponent1;
	}

	private void setPowerProduct(UnitOfMeasure uom1, Integer exponent1, UnitOfMeasure uom2, Integer exponent2) {
		this.uom1 = uom1;
		this.exponent1 = exponent1;
		this.uom2 = uom2;
		this.exponent2 = exponent2;
	}

	private Integer getExponent1() {
		return exponent1;
	}

	private Integer getExponent2() {
		return exponent2;
	}

	private UnitOfMeasure getUOM1() {
		return this.uom1;
	}

	private UnitOfMeasure getUOM2() {
		return this.uom2;
	}

	boolean isQuotient() {
		return (getExponent2() != null && getExponent2() < 0) ? true : false;
	}

	UnitOfMeasure clonePower(UnitOfMeasure uom) throws Exception {

		UnitOfMeasure newUOM = new UnitOfMeasure();
		newUOM.setUnitType(getUnitType());

		// check if quotient
		int exponent = 1;
		if (getPowerExponent() != null) {
			exponent = getPowerExponent();
		}

		UnitOfMeasure one = MeasurementSystem.getSystem().getOne();
		if (isQuotient()) {
			if (getDividend().equals(one)) {
				exponent = getExponent2();
			} else if (getDivisor().equals(one)) {
				exponent = getExponent1();
			}
		}
		newUOM.setPowerUnits(uom, exponent);
		String symbol = UnitOfMeasure.generatePowerSymbol(uom, exponent);
		newUOM.setSymbol(symbol);
		newUOM.setName(symbol);

		return newUOM;
	}

	UnitOfMeasure clonePowerProduct(UnitOfMeasure uom1, UnitOfMeasure uom2) throws Exception {
		boolean invert = false;
		UnitOfMeasure one = MeasurementSystem.getSystem().getOne();

		// check if quotient
		if (isQuotient()) {
			if (uom2.equals(one)) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("incompatible.units"), this, one);
				throw new Exception(msg);
			}
			invert = true;
		} else {
			if (uom1.equals(one) || uom2.equals(one)) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("incompatible.units"), this, one);
				throw new Exception(msg);
			}
		}

		UnitOfMeasure newUOM = uom1.multiplyOrDivide(uom2, invert);
		newUOM.setUnitType(getUnitType());

		return newUOM;
	}

	/**
	 * Remove all cached conversions
	 */
	public void clearCache() {
		conversionRegistry.clear();
	}

	/**
	 * Get the unit of measure corresponding to the base symbol
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getBaseUOM() throws Exception {
		String baseSymbol = getBaseSymbol();
		return MeasurementSystem.getSystem().getBaseUOM(baseSymbol);
	}

	/**
	 * Get the conversion between the international customary and corresponding
	 * SI unit. For example feet to metres.
	 * 
	 * @return {@link Conversion}
	 */
	public Conversion getBridgeConversion() {
		return bridgeConversion;
	}

	/**
	 * Set the conversion to another fundamental unit of measure
	 * 
	 * @param scalingFactor
	 *            Scaling factor
	 * @param abscissaUnit
	 *            X-axis unit
	 * @throws Exception
	 *             Exception
	 */
	public void setBridgeConversion(BigDecimal scalingFactor, UnitOfMeasure abscissaUnit) throws Exception {
		Conversion conversion = new Conversion(scalingFactor, abscissaUnit, BigDecimal.ZERO);
		this.bridgeConversion = conversion;
	}

	/**
	 * Compare this unit of measure to another one.
	 * 
	 * @param other
	 *            unit of measure
	 * @return -1 if less than, 0 if equal and 1 if greater than
	 */
	public int compareTo(UnitOfMeasure other) {
		return getSymbol().compareTo(other.getSymbol());
	}

	/**
	 * Get the unit's enumerated type
	 * 
	 * @return {@link Unit}
	 */
	public Unit getEnumeration() {
		return unit;
	}

	/**
	 * Set the unit's enumerated type
	 * 
	 * @param unit
	 *            {@link Unit}
	 */
	public void setEnumeration(Unit unit) {
		this.unit = unit;
	}

	/**
	 * Get the type of the unit.
	 * 
	 * @return {@link UnitType}
	 */
	public UnitType getUnitType() {
		return unitType;
	}

	/**
	 * Set the type of the unit.
	 * 
	 * @param unitType
	 *            {@link UnitType}
	 */
	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	/**
	 * Get the category
	 * 
	 * @return Category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Set the category
	 * 
	 * @param category
	 *            Category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	private BigDecimal getBridgeFactor(UnitOfMeasure uom) {
		BigDecimal factor = null;

		// check for our bridge
		if (getBridgeConversion() != null) {
			factor = getBridgeConversion().getScalingFactor();
		} else {
			// try other side
			if (uom.getBridgeConversion() != null) {
				UnitOfMeasure toUOM = uom.getBridgeConversion().getAbscissaUnit();

				if (toUOM.equals(this)) {
					factor = decimalDivide(BigDecimal.ONE, uom.getBridgeConversion().getScalingFactor());
				}
			}
		}

		return factor;
	}

	/**
	 * Get the hash code
	 * 
	 * @return hash code
	 */
	public int hashCode() {
		return MeasurementSystem.getSystem().hashCode() ^ (getEnumeration() == null ? 17 : getEnumeration().hashCode())
				^ getSymbol().hashCode();
	}

	/**
	 * Compare this unit of measure to another
	 * 
	 * @return true if equal
	 */
	public boolean equals(Object other) {

		if (other == null || !(other instanceof UnitOfMeasure)) {
			return false;
		}
		UnitOfMeasure otherUnit = (UnitOfMeasure) other;

		// same enumerations
		Unit thisEnumeration = getEnumeration();
		Unit otherEnumeration = otherUnit.getEnumeration();

		if (thisEnumeration != null && otherEnumeration != null && !thisEnumeration.equals(otherEnumeration)) {
			return false;
		}

		// same abscissa unit symbols
		String thisSymbol = getAbscissaUnit().getSymbol();
		String otherSymbol = otherUnit.getAbscissaUnit().getSymbol();

		if (!thisSymbol.equals(otherSymbol)) {
			return false;
		}

		// same factors
		if (getScalingFactor().compareTo(otherUnit.getScalingFactor()) != 0) {
			return false;
		}

		// same offsets
		if (getOffset().compareTo(otherUnit.getOffset()) != 0) {
			return false;
		}

		return true;
	}

	private void checkOffset(UnitOfMeasure other) throws Exception {
		if (other.getOffset().compareTo(BigDecimal.ZERO) != 0) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("offset.not.supported"), other.toString());
			throw new Exception(msg);
		}
	}

	private UnitOfMeasure multiplyOrDivide(UnitOfMeasure other, boolean invert) throws Exception {
		if (other == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}

		if (other.equals(MeasurementSystem.getSystem().getOne())) {
			return this;
		}

		checkOffset(this);
		checkOffset(other);

		// product or quotient
		UnitOfMeasure result = new UnitOfMeasure();

		if (!invert) {
			result.setProductUnits(this, other);
		} else {
			result.setQuotientUnits(this, other);
		}

		// this base symbol map
		Reducer thisPowerMap = getBaseMap();
		Map<UnitOfMeasure, Integer> thisMap = thisPowerMap.getTerms();
		BigDecimal thisFactor = thisPowerMap.getScalingFactor();

		// other base symbol map
		Reducer otherPowerMap = other.getBaseMap();
		Map<UnitOfMeasure, Integer> otherMap = otherPowerMap.getTerms();
		BigDecimal otherFactor = otherPowerMap.getScalingFactor();

		// create a map of the unit of measure powers
		Map<UnitOfMeasure, Integer> resultMap = new HashMap<UnitOfMeasure, Integer>();

		// iterate over the multiplier's unit map
		for (Entry<UnitOfMeasure, Integer> thisEntry : thisMap.entrySet()) {
			UnitOfMeasure thisUOM = thisEntry.getKey();
			Integer thisPower = thisEntry.getValue();

			Integer otherPower = otherMap.get(thisUOM);

			if (otherPower != null) {
				if (!invert) {
					// add to multiplier's power
					thisPower += otherPower;
				} else {
					// subtract from dividend's power
					thisPower -= otherPower;
				}

				// remove multiplicand or divisor UOM
				otherMap.remove(thisUOM);
			}

			if (thisPower != 0) {
				resultMap.put(thisUOM, thisPower);
			}
		}

		// add any remaining multiplicand terms and invert any remaining divisor
		// terms
		for (Entry<UnitOfMeasure, Integer> otherEntry : otherMap.entrySet()) {
			UnitOfMeasure otherUOM = otherEntry.getKey();
			Integer otherPower = otherEntry.getValue();

			if (!invert) {
				resultMap.put(otherUOM, otherPower);
			} else {
				resultMap.put(otherUOM, -otherPower);
			}
		}

		// get the base symbol and possibly base UOM
		Reducer resultPowerMap = new Reducer();
		resultPowerMap.setTerms(resultMap);

		String baseSymbol = resultPowerMap.buildBaseString();
		UnitOfMeasure baseUOM = MeasurementSystem.getSystem().getBaseUOM(baseSymbol);

		if (baseUOM != null) {
			// there is a conversion to the base UOM
			BigDecimal resultFactor = null;
			if (!invert) {
				resultFactor = decimalMultiply(thisFactor, otherFactor);
			} else {
				resultFactor = decimalDivide(thisFactor, otherFactor);
			}
			result.setScalingFactor(resultFactor);
			result.setAbscissaUnit(baseUOM);
			result.setUnitType(baseUOM.getUnitType());
		}

		if (!invert) {
			result.setSymbol(generateProductSymbol(result.getMultiplier(), result.getMultiplicand()));
		} else {
			result.setSymbol(generateQuotientSymbol(result.getDividend(), result.getDivisor()));
		}

		// constrain to a maximum length
		if (result.getSymbol().length() > MAX_SYMBOL_LENGTH) {
			result.setSymbol(generateIntermediateSymbol());
		}

		return result;
	}

	/**
	 * Multiply two units of measure to create a third one.
	 * 
	 * @param multiplicand
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure multiply(UnitOfMeasure multiplicand) throws Exception {
		return multiplyOrDivide(multiplicand, false);
	}

	/**
	 * Divide two units of measure to create a third one.
	 * 
	 * @param divisor
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure divide(UnitOfMeasure divisor) throws Exception {
		return multiplyOrDivide(divisor, true);
	}

	/**
	 * Invert a unit of measure to create a new one
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure invert() throws Exception {
		UnitOfMeasure inverted = null;

		if (getExponent2() != null && getExponent2() < 0) {
			inverted = getDivisor().divide(getDividend());
		} else {
			inverted = MeasurementSystem.getSystem().getOne().divide(this);
		}

		return inverted;
	}

	/**
	 * Get the unit of measure's symbol in the fundamental units for that
	 * system. For example a Newton is a kg.m/s2.
	 * 
	 * @return Base symbol
	 * @throws Exception
	 *             Exception
	 */
	public synchronized String getBaseSymbol() throws Exception {
		if (baseSymbol == null) {
			Reducer powerMap = getBaseMap();
			baseSymbol = powerMap.buildBaseString();
		}
		return baseSymbol;
	}

	private final synchronized Reducer getBaseMap() throws Exception {
		Reducer reducer = new Reducer();
		reducer.explode(this);
		return reducer;
	}

	/**
	 * Get the conversion to another unit of measure
	 * 
	 * @return {@link Conversion}
	 */
	public Conversion getConversion() {
		return this.conversion;
	}

	/**
	 * Set the conversion to another unit of measure
	 * 
	 * @param conversion
	 *            {@link Conversion}
	 * @throws Exception
	 *             Exception
	 */
	public void setConversion(Conversion conversion) throws Exception {
		// unit has been previously cached, so first remove it, then cache again
		MeasurementSystem.getSystem().unregisterUnit(this);
		baseSymbol = null;
		this.conversion = conversion;

		// re-cache
		MeasurementSystem.getSystem().cacheUnit(this);
	}

	/**
	 * Define a conversion with the specified scaling factor, abscissa unit of
	 * measure and scaling factor.
	 * 
	 * @param scalingFactor
	 *            Factor
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @param offset
	 *            Offset
	 * @throws Exception
	 *             Exception
	 */
	public void setConversion(BigDecimal scalingFactor, UnitOfMeasure abscissaUnit, BigDecimal offset)
			throws Exception {
		Conversion conversion = new Conversion(scalingFactor, abscissaUnit, offset);
		this.setConversion(conversion);
	}

	/**
	 * Define a conversion with a scaling factor of 1 and offset of 0 for the
	 * specified abscissa unit of measure.
	 * 
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public void setConversion(UnitOfMeasure abscissaUnit) throws Exception {
		this.setConversion(BigDecimal.ONE, abscissaUnit, BigDecimal.ZERO);
	}

	/**
	 * Define a conversion with an offset of 0 for the specified scaling factor
	 * and abscissa unit of measure.
	 * 
	 * @param scalingFactor
	 *            Factor
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public void setConversion(BigDecimal scalingFactor, UnitOfMeasure abscissaUnit) throws Exception {
		this.setConversion(scalingFactor, abscissaUnit, BigDecimal.ZERO);
	}

	/**
	 * Construct a conversion with an offset of 0 for the specified scaling
	 * factor and abscissa unit of measure.
	 * 
	 * @param scalingFactor
	 *            Factor
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public void setConversion(String scalingFactor, UnitOfMeasure abscissaUnit) throws Exception {
		this.setConversion(Quantity.createAmount(scalingFactor), abscissaUnit);
	}

	/**
	 * Get the unit of measure's 'b' offset (intercept) for the relation y = ax
	 * + b.
	 * 
	 * @return Offset
	 */
	public BigDecimal getOffset() {
		return conversion.getOffset();
	}

	/**
	 * Set the unit of measure's 'b' offset (intercept) for the relation y = ax
	 * + b.
	 * 
	 * @param offset
	 *            Offset
	 */
	public void setOffset(BigDecimal offset) {
		conversion.setOffset(offset);
	}

	/**
	 * Get the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @return Factor
	 */
	public BigDecimal getScalingFactor() {
		return conversion.getScalingFactor();
	}

	/**
	 * Set the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @param factor
	 *            Scaling factor
	 */
	public void setScalingFactor(BigDecimal factor) {
		conversion.setScalingFactor(factor);
	}

	/**
	 * Get the unit of measure's x-axis unit of measure for the relation y = ax
	 * + b.
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getAbscissaUnit() {
		return conversion.getAbscissaUnit();
	}

	/**
	 * Set the unit of measure's x-axis unit of measure for the relation y = ax
	 * + b.
	 * 
	 * @param target
	 *            {@link UnitOfMeasure}
	 */
	public void setAbscissaUnit(UnitOfMeasure target) {
		conversion.setAbscissaUnit(target);
	}

	private BigDecimal convertScalarToScalar(UnitOfMeasure targetUOM) throws Exception {
		UnitOfMeasure thisAbscissa = getAbscissaUnit();
		BigDecimal thisFactor = getScalingFactor();

		BigDecimal scalingFactor = null;

		if (thisAbscissa.equals(targetUOM)) {
			// direct conversion
			scalingFactor = thisFactor;
		} else {
			// indirect conversion
			scalingFactor = convertUnit(targetUOM);
		}
		return scalingFactor;
	}

	private BigDecimal convertUnit(UnitOfMeasure targetUOM) throws Exception {

		// get path factors in each system
		PathParameters thisParameters = traversePath();
		PathParameters targetParameters = targetUOM.traversePath();

		BigDecimal thisPathFactor = thisParameters.getPathFactor();
		UnitOfMeasure thisBase = thisParameters.getPathUOM();

		BigDecimal targetPathFactor = targetParameters.getPathFactor();
		UnitOfMeasure targetBase = targetParameters.getPathUOM();

		// check for a base conversion unit bridge
		BigDecimal bridgeFactor = thisBase.getBridgeFactor(targetBase);

		if (bridgeFactor != null) {
			thisPathFactor = decimalMultiply(thisPathFactor, bridgeFactor);
		}

		// new path amount
		BigDecimal scalingFactor = decimalDivide(thisPathFactor, targetPathFactor);

		return scalingFactor;
	}

	private static void checkTypes(UnitOfMeasure uom1, UnitOfMeasure uom2) throws Exception {
		UnitType thisType = uom1.getUnitType();
		UnitType targetType = uom2.getUnitType();

		if (thisType != UnitType.UNCLASSIFIED && targetType != UnitType.UNCLASSIFIED && !thisType.equals(UnitType.UNITY)
				&& !targetType.equals(UnitType.UNITY) && !thisType.equals(targetType)) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("must.be.same.as"), uom1, uom1.getUnitType(),
					uom2, uom2.getUnitType());
			throw new Exception(msg);
		}
	}

	/**
	 * Get the factor to convert to the unit of measure
	 * 
	 * @param targetUOM
	 *            Target {@link UnitOfMeasure}
	 * @return conversion factor
	 * @throws Exception
	 *             Exception
	 */
	public BigDecimal getConversionFactor(UnitOfMeasure targetUOM) throws Exception {
		if (targetUOM == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}

		// first check the cache
		BigDecimal cachedFactor = conversionRegistry.get(targetUOM);

		if (cachedFactor != null) {
			return cachedFactor;
		}

		checkTypes(this, targetUOM);

		Reducer fromPowerMap = getBaseMap();
		Reducer toPowerMap = targetUOM.getBaseMap();

		Map<UnitOfMeasure, Integer> fromMap = fromPowerMap.getTerms();
		Map<UnitOfMeasure, Integer> toMap = toPowerMap.getTerms();

		if (fromMap.size() != toMap.size()) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("incompatible.units"), this, targetUOM);
			throw new Exception(msg);
		}

		BigDecimal fromFactor = fromPowerMap.getScalingFactor();
		BigDecimal toFactor = toPowerMap.getScalingFactor();

		BigDecimal factor = BigDecimal.ONE;

		for (Entry<UnitOfMeasure, Integer> fromEntry : fromMap.entrySet()) {
			UnitType fromType = fromEntry.getKey().getUnitType();
			UnitOfMeasure fromUOM = fromEntry.getKey();
			Integer fromPower = fromEntry.getValue();

			for (Entry<UnitOfMeasure, Integer> toEntry : toMap.entrySet()) {
				UnitType toType = toEntry.getKey().getUnitType();

				if (fromType.equals(toType)) {
					UnitOfMeasure toUOM = toEntry.getKey();
					BigDecimal bd = fromUOM.convertScalarToScalar(toUOM);
					bd = decimalPower(bd, fromPower);
					factor = decimalMultiply(factor, bd);

					break;
				}
			} // to map
		} // from map

		BigDecimal scaling = decimalDivide(fromFactor, toFactor);
		cachedFactor = decimalMultiply(factor, scaling);

		// cache it
		conversionRegistry.put(targetUOM, cachedFactor);

		return cachedFactor;

	}

	private final PathParameters traversePath() throws Exception {
		UnitOfMeasure pathUOM = this;
		BigDecimal pathFactor = BigDecimal.ONE;

		while (true) {
			BigDecimal scalingFactor = pathUOM.getScalingFactor();
			UnitOfMeasure abscissa = pathUOM.getAbscissaUnit();

			pathFactor = decimalMultiply(pathFactor, scalingFactor);

			if (pathUOM.equals(abscissa)) {
				break;
			}

			// next UOM on path
			pathUOM = abscissa;
		}

		return new PathParameters(pathUOM, pathFactor);
	}

	private boolean isTerminal() {
		return this.equals(getAbscissaUnit()) ? true : false;
	}

	/**
	 * Check to see if this UOM has a conversion to another UOM
	 * 
	 * @return true if it does
	 */
	public boolean hasConversion() {
		return !isTerminal();
	}

	/**
	 * Create a String representation of this unit of measure
	 * 
	 * @return String representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ResourceBundle symbolBundle = MeasurementSystem.getSystem().getSymbols();

		// type
		UnitType type = getUnitType();
		sb.append(symbolBundle.getString("unit.type.text")).append(' ').append(type.toString()).append(", ");

		// unit enumeration
		Unit enumeration = getEnumeration();
		if (enumeration != null) {
			sb.append(symbolBundle.getString("enum.text")).append(' ').append(enumeration.toString()).append(", ");
		}

		// symbol
		String symbol = getSymbol();
		sb.append(symbolBundle.getString("symbol.text")).append(' ').append(symbol);
		sb.append(", ").append(symbolBundle.getString("conversion.text")).append(' ');

		// scaling factor
		BigDecimal factor = getScalingFactor();
		if (factor != null && (factor.compareTo(BigDecimal.ONE) != 0)) {
			sb.append(factor.toString()).append(MULT);
		}

		// abscissa unit
		UnitOfMeasure abscissa = getAbscissaUnit();
		if (abscissa != null) {
			sb.append(abscissa.getSymbol());
		}

		// offset
		BigDecimal offset = getOffset();
		if (offset != null && (offset.compareTo(BigDecimal.ZERO) != 0)) {
			sb.append(" + ").append(getOffset().toString());
		}

		sb.append(", ").append(symbolBundle.getString("base.text")).append(' ');

		// base symbol
		try {
			sb.append(getBaseSymbol());
		} catch (Exception e) {
			// ignore
		}

		return sb.toString();
	}

	void setPowerUnits(UnitOfMeasure base, Integer exponent) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("base.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		setPowerProduct(base, exponent);
	}

	/**
	 * Get the exponent
	 * 
	 * @return Exponent
	 */
	public Integer getPowerExponent() {
		return getExponent1();
	}

	/**
	 * Get the base unit of measure for the power
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getPowerBase() {
		return getUOM1();
	}

	// generate a symbol for units of measure created as the result of
	// intermediate multiplication and division operations. These symbols are
	// not cached.
	private static String generateIntermediateSymbol() {
		return Long.toHexString(System.currentTimeMillis());
	}

	static String generatePowerSymbol(UnitOfMeasure base, Integer exponent) {
		StringBuilder sb = new StringBuilder();
		sb.append(base.getSymbol()).append(POW).append(exponent);
		return sb.toString();
	}

	static String generateProductSymbol(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) {
		StringBuilder sb = new StringBuilder();

		if (multiplier.equals(multiplicand)) {
			sb.append(multiplier.getSymbol()).append(SQ);
		} else {
			sb.append(multiplier.getSymbol()).append(MULT).append(multiplicand.getSymbol());
		}
		return sb.toString();
	}

	static String generateQuotientSymbol(UnitOfMeasure dividend, UnitOfMeasure divisor) {
		StringBuilder sb = new StringBuilder();
		sb.append(dividend.getSymbol()).append(DIV).append(divisor.getSymbol());
		return sb.toString();
	}

	void setProductUnits(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		if (multiplier == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplier.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		if (multiplicand == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplicand.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		setPowerProduct(multiplier, 1, multiplicand, 1);
	}

	/**
	 * Get the multiplier
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplier() {
		return getUOM1();
	}

	/**
	 * Get the multiplicand
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplicand() {
		return getUOM2();
	}

	void setQuotientUnits(UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
		if (dividend == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("dividend.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		if (divisor == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("divisor.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		setPowerProduct(dividend, 1, divisor, -1);
	}

	/**
	 * Get the dividend unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDividend() {
		return getUOM1();
	}

	/**
	 * Get the divisor unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDivisor() {
		return getUOM2();
	}

	// this method is for optimization of BigDecimal addition
	static BigDecimal decimalAdd(BigDecimal a, BigDecimal b) {
		BigDecimal value = null;

		if (b.compareTo(BigDecimal.ZERO) == 0) {
			value = a;
		} else {
			value = a.add(b, MATH_CONTEXT);
		}

		return value;
	}

	// this method is for optimization of BigDecimal subtraction
	static BigDecimal decimalSubtract(BigDecimal a, BigDecimal b) {
		BigDecimal value = null;

		if (b.compareTo(BigDecimal.ZERO) == 0) {
			value = a;
		} else {
			value = a.subtract(b, MATH_CONTEXT);
		}

		return value;
	}

	// this method is for optimization of BigDecimal multiplication
	static BigDecimal decimalMultiply(BigDecimal a, BigDecimal b) {
		BigDecimal value = null;

		if (b.compareTo(BigDecimal.ONE) == 0) {
			value = a;
		} else {
			value = a.multiply(b, MATH_CONTEXT);
		}
		return value;
	}

	// this method is for optimization of BigDecimal division
	static BigDecimal decimalDivide(BigDecimal a, BigDecimal b) {
		BigDecimal value = null;

		if (b.compareTo(BigDecimal.ONE) == 0) {
			value = a;
		} else {
			value = a.divide(b, MATH_CONTEXT);
		}
		return value;
	}

	// this method is for optimization of BigDecimal exponentiation
	static BigDecimal decimalPower(BigDecimal base, int exponent) {
		BigDecimal value = null;

		if (exponent == 1) {
			value = base;
		} else if (exponent == 0) {
			value = BigDecimal.ONE;
		} else {
			value = base.pow(exponent, MATH_CONTEXT);
		}
		return value;
	}

	// UOM, scaling factor and power cumulative along a conversion path
	private class PathParameters {
		private UnitOfMeasure pathUOM;
		private BigDecimal pathFactor;

		private PathParameters(UnitOfMeasure pathUOM, BigDecimal pathFactor) {
			this.pathUOM = pathUOM;
			this.pathFactor = pathFactor;
		}

		private UnitOfMeasure getPathUOM() {
			return pathUOM;
		}

		private BigDecimal getPathFactor() {
			return pathFactor;
		}
	}

	// reduce a unit of measure to its most basic scalar units of measure.
	private class Reducer {
		// starting level
		private static final int STARTING_LEVEL = -1;

		// UOMs and their exponents
		private Map<UnitOfMeasure, Integer> terms = new HashMap<UnitOfMeasure, Integer>();

		// the overall scaling factor
		private BigDecimal mapScalingFactor = BigDecimal.ONE;

		// list of exponents down a path to the leaf UOM
		private List<Integer> pathExponents = new ArrayList<>();

		private Reducer() {

		}

		private BigDecimal getScalingFactor() {
			return this.mapScalingFactor;
		}

		private Map<UnitOfMeasure, Integer> getTerms() {
			return terms;
		}

		private void setTerms(Map<UnitOfMeasure, Integer> terms) {
			this.terms = terms;
		}

		public void explode(UnitOfMeasure unit) throws Exception {
			explodeRecursively(unit, STARTING_LEVEL);
		}

		private void explodeRecursively(UnitOfMeasure unit, int level) throws Exception {
			// down a level
			level++;

			// scaling factor to abscissa unit
			BigDecimal scalingFactor = unit.getScalingFactor();

			// explode the abscissa unit
			UnitOfMeasure abscissaUnit = unit.getAbscissaUnit();

			UnitOfMeasure uom1 = abscissaUnit.getUOM1();
			UnitOfMeasure uom2 = abscissaUnit.getUOM2();

			Integer exp1 = abscissaUnit.getExponent1();
			Integer exp2 = abscissaUnit.getExponent2();

			// scaling
			if (pathExponents.size() > 0) {
				int lastExponent = pathExponents.get(pathExponents.size() - 1);

				// compute the overall scaling factor
				BigDecimal factor = BigDecimal.ONE;
				for (int i = 0; i < Math.abs(lastExponent); i++) {
					factor = decimalMultiply(factor, scalingFactor);
				}

				if (lastExponent < 0) {
					mapScalingFactor = decimalDivide(mapScalingFactor, factor);
				} else {
					mapScalingFactor = decimalMultiply(mapScalingFactor, factor);
				}
			} else {
				mapScalingFactor = scalingFactor;
			}

			if (uom1 == null) {
				if (!abscissaUnit.isTerminal()) {
					// keep exploding down the conversion path
					BigDecimal currentMapFactor = mapScalingFactor;
					mapScalingFactor = BigDecimal.ONE;
					explodeRecursively(abscissaUnit, STARTING_LEVEL);
					mapScalingFactor = decimalMultiply(mapScalingFactor, currentMapFactor);
				} else {

					// multiply out all of the exponents down the path
					int pathExponent = 1;

					for (Integer exp : pathExponents) {
						pathExponent = pathExponent * exp;
					}

					boolean invert = pathExponent < 0 ? true : false;

					for (int i = 0; i < Math.abs(pathExponent); i++) {
						addTerm(abscissaUnit, invert);
					}
				}
			} else {
				// explode UOM #1
				pathExponents.add(exp1);
				explodeRecursively(uom1, level);
				pathExponents.remove(level);
			}

			if (uom2 != null) {
				// explode UOM #2
				pathExponents.add(exp2);
				explodeRecursively(uom2, level);
				pathExponents.remove(level);
			}

			// up a level
			level--;
		}

		// add a UOM and exponent pair to the map of reduced terms
		private void addTerm(UnitOfMeasure uom, boolean invert) throws Exception {
			int unitPower = 1;
			int power = 0;

			if (!invert) {
				// get existing power
				if (!terms.containsKey(uom)) {
					// add first time
					power = unitPower;
				} else {
					// increment existing power
					if (!uom.equals(MeasurementSystem.getSystem().getOne())) {
						power = terms.get(uom).intValue() + unitPower;
					}
				}
			} else {
				// denominator with negative powers
				if (!terms.containsKey(uom)) {
					// add first time
					power = -unitPower;
				} else {
					// decrement existing power
					if (!uom.equals(MeasurementSystem.getSystem().getOne())) {
						power = terms.get(uom).intValue() - unitPower;
					}
				}
			}

			if (power == 0) {
				terms.remove(uom);
			} else {
				if (!uom.equals(MeasurementSystem.getSystem().getOne())) {
					terms.put(uom, power);
				}
			}
		}

		// compose the base symbol
		private String buildBaseString() throws Exception {
			StringBuilder numerator = new StringBuilder();
			StringBuilder denominator = new StringBuilder();

			int numeratorCount = 0;
			int denominatorCount = 0;

			// sort units by symbol (ascending)
			SortedSet<UnitOfMeasure> keys = new TreeSet<UnitOfMeasure>(terms.keySet());

			for (UnitOfMeasure unit : keys) {
				int power = terms.get(unit);

				if (power < 0) {
					// negative, put in denominator
					if (denominator.length() > 0) {
						denominator.append(MULT);
					}

					if (!unit.equals(MeasurementSystem.getSystem().getOne())) {
						denominator.append(unit.getSymbol());
						denominatorCount++;
					}

					if (power < -1) {
						if (power == -2) {
							denominator.append(SQ);
						} else if (power == -3) {
							denominator.append(CUBED);
						} else {
							denominator.append(POW).append(Math.abs(power));
						}
					}
				} else if (power >= 1 && !unit.equals(MeasurementSystem.getSystem().getOne())) {
					// positive, put in numerator
					if (numerator.length() > 0) {
						numerator.append(MULT);
					}

					numerator.append(unit.getSymbol());
					numeratorCount++;

					if (power > 1) {
						if (power == 2) {
							numerator.append(SQ);
						} else if (power == 3) {
							numerator.append(CUBED);
						} else {
							numerator.append(POW).append(power);
						}
					}
				} else {
					// unary, don't add a '1'
				}
			}

			if (numeratorCount == 0) {
				numerator.append(ONE_CHAR);
			}

			String result = null;

			if (denominatorCount == 0) {
				result = numerator.toString();
			} else {
				if (denominatorCount == 1) {
					result = numerator.append(DIV).append(denominator).toString();
				} else {
					result = numerator.append(DIV).append(LP).append(denominator).append(RP).toString();
				}
			}

			return result;
		} // end unit of measure iteration
	}
}
