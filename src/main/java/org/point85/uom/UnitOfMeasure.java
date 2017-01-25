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
import java.util.HashMap;
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
 * A unit of measure has a unique base symbol, for example 'm' for metre. In the
 * SI system, the derived units such as Newton all have base symbols expressed
 * in the fundamental units of length (metre), mass (kilogram), time (second),
 * temperature (Kelvin), plane angle (radian), electric charge (Coulomb) and
 * luminous intensity (candela). This base symbol is used in unit of measure
 * conversions to uniquely identify the target unit. A unit of measure can also
 * have a unique symbol assigned by the Unified Code for Units of Measure (UCUM)
 * specification (http://unitsofmeasure.org/ucum.html).
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
public class UnitOfMeasure implements Comparable<UnitOfMeasure> {

	// category of the UOM
	private enum Category {
		SCALAR, PRODUCT, QUOTIENT, POWER;
	}

	// BigDecimal math. A MathContext object with a precision setting matching
	// the IEEE 754R Decimal64 format, 16 digits, and a rounding mode of
	// HALF_EVEN, the IEEE 754R default.
	public static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

	// multiply, divide and power symbols
	private static final char MULT = 0xB7;
	private static final char DIV = '/';
	private static final char POW = '^';
	private static final char SQ = 0xB2;
	private static final char CUBED = 0xB3;

	// registry of unit conversion factor
	private Map<UnitOfMeasure, BigDecimal> conversionRegistry = new ConcurrentHashMap<UnitOfMeasure, BigDecimal>();

	// name, for example "kilogram"
	private String name;

	// symbol or abbreviation, e.g. "kg"
	private String symbol;

	// description
	private String description;

	// conversion to another Unit of Measure in the same recognized measurement
	// system (y = ax + b)
	private Conversion conversion;

	// unit enumerations for the various systems of measurement, e.g. KILOGRAM
	private Unit unit;

	// unit type, e.g. MASS
	private UnitType unitType;

	// conversion to another Unit of Measure in a different measurement system
	private Conversion bridge;

	// this class holds the base UOMs and exponents for a product of two power
	// UOMs
	private PowerProduct powerProduct;

	// cached base symbol
	private String baseSymbol;

	UnitOfMeasure() {
		initialize();
	}

	UnitOfMeasure(UnitType type, String name, String symbol, String description) {
		this.name = name;
		this.symbol = symbol;
		this.description = description;
		this.unitType = type;
		initialize();
	}

	private void initialize() {
		// a unit can always be converted to itself
		this.conversion = new Conversion(this);
		this.powerProduct = new PowerProduct();
	}

	/**
	 * Remove all cached conversions
	 */
	public void clearCache() {
		conversionRegistry.clear();
	}

	private Category getCategory() throws Exception {
		return powerProduct.getCategory();
	}

	/**
	 * Get the unit of measure's symbol
	 * 
	 * @return Symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	private void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Get the name of the unit of measure.
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the description of the unit of measure.
	 * 
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the unit of measure corresponding to the base symbol
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getBaseUOM() throws Exception {
		UnitOfMeasure base = null;

		String baseSymbol = getBaseSymbol();

		if (baseSymbol != null) {
			base = MeasurementSystem.getSystem().getUOM(baseSymbol);
		}
		return base;
	}

	/**
	 * Get the conversion between the international customary and corresponding
	 * SI unit. For example feet to metres.
	 * 
	 * @return {@link Conversion}
	 */
	public Conversion getBridge() {
		return bridge;
	}

	/**
	 * Set the conversion between the international customary and corresponding
	 * SI unit. For example feet to metres.
	 * 
	 * @param conversion
	 *            {@link Conversion}
	 */
	public void setBridge(Conversion conversion) {
		this.bridge = conversion;
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

	void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	private BigDecimal getBridgeFactor(UnitOfMeasure uom) {
		// common units have a factor of 1
		if (getSymbol().equals(uom.getSymbol()) && getEnumeration().equals(uom.getEnumeration())) {
			return BigDecimal.ONE;
		}

		BigDecimal factor = null;

		// check for our bridge
		if (getBridge() != null) {
			factor = getBridge().getScalingFactor();
		} else {
			// try other side
			if (uom.getBridge() != null) {
				UnitOfMeasure toUOM = uom.getBridge().getAbscissaUnit();

				if (toUOM.equals(this)) {
					factor = decimalDivide(BigDecimal.ONE, uom.getBridge().getScalingFactor());
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
		UnitOfMeasure baseUOM = MeasurementSystem.getSystem().getUOM(baseSymbol);

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
			result.setSymbol(result.generateProductSymbol());
		} else {
			result.setSymbol(result.generateQuotientSymbol());
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
		if (this.getCategory().equals(Category.QUOTIENT)) {
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
		Reducer powerMap = new Reducer();
		powerMap.explode(this);
		return powerMap;
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
	 */
	public void setConversion(Conversion conversion) {
		this.conversion = conversion;
		this.baseSymbol = conversion.getAbscissaUnit().getSymbol();
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

	void setAbscissaUnit(UnitOfMeasure target) {
		conversion.setAbscissaUnit(target);
	}

	private BigDecimal convertScalarToScalar(UnitOfMeasure targetUOM) throws Exception {
		UnitOfMeasure thisUOM = this;

		UnitOfMeasure thisAbscissa = thisUOM.getAbscissaUnit();
		BigDecimal thisFactor = thisUOM.getScalingFactor();

		UnitOfMeasure targetAbscissa = targetUOM.getAbscissaUnit();
		BigDecimal targetFactor = targetUOM.getScalingFactor();

		BigDecimal scalingFactor = null;

		if (thisAbscissa.equals(targetUOM)) {
			scalingFactor = thisFactor;
		} else if (thisUOM.equals(targetAbscissa)) {
			scalingFactor = decimalDivide(BigDecimal.ONE, targetFactor);
		} else if (thisAbscissa.equals(targetAbscissa)) {
			scalingFactor = decimalDivide(getScalingFactor(), targetUOM.getScalingFactor());
		} else {
			scalingFactor = convertUnit(targetUOM);
		}
		return scalingFactor;
	}

	private BigDecimal convertUnit(UnitOfMeasure targetUOM) throws Exception {

		// get path factors in each system
		PathParameters thisParameters = traversePath();
		PathParameters targetParameters = targetUOM.traversePath();

		BigDecimal thisPathFactor = thisParameters.getPathFactor();
		BigDecimal targetPathFactor = targetParameters.getPathFactor();

		if (getBridge() != null || targetUOM.getBridge() != null) {
			// check for a base conversion unit bridge
			UnitOfMeasure thisBase = thisParameters.getPathUOM();
			UnitOfMeasure targetBase = targetParameters.getPathUOM();
			BigDecimal bridgeFactor = thisBase.getBridgeFactor(targetBase);

			if (bridgeFactor == null) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("no.factor"), this.toString(),
						targetUOM.toString());
				throw new Exception(msg);
			}
			thisPathFactor = decimalMultiply(thisPathFactor, bridgeFactor);
		}

		// new path amount
		BigDecimal scalingFactor = decimalDivide(thisPathFactor, targetPathFactor);

		return scalingFactor;
	}

	private static void checkTypes(UnitOfMeasure uom1, UnitOfMeasure uom2) throws Exception {
		UnitType thisType = uom1.getUnitType();
		UnitType targetType = uom2.getUnitType();

		if (thisType != null && targetType != null && !thisType.equals(UnitType.UNITY)
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

		// both maps must be same size
		if (fromMap.size() != toMap.size()) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("maps.not.equal"), fromMap.size(),
					toMap.size());
			throw new Exception(msg);
		}

		BigDecimal fromFactor = fromPowerMap.getScalingFactor();
		BigDecimal toFactor = toPowerMap.getScalingFactor();

		BigDecimal factor = BigDecimal.ONE;

		for (Entry<UnitOfMeasure, Integer> fromEntry : fromMap.entrySet()) {
			UnitType fromType = fromEntry.getKey().getUnitType();
			UnitOfMeasure fromUOM = fromEntry.getKey();
			int fromPower = fromEntry.getValue();

			for (Entry<UnitOfMeasure, Integer> toEntry : toMap.entrySet()) {
				UnitType toType = toEntry.getKey().getUnitType();

				if (fromType.equals(toType)) {

					UnitOfMeasure toUOM = toEntry.getKey();
					int toPower = toEntry.getValue();

					// from and to powers must be equal
					if (fromPower != toPower) {
						String msg = MessageFormat.format(MeasurementSystem.getMessage("powers.not.equal"), fromPower,
								toPower);
						throw new Exception(msg);
					}

					// both from and to are scalars at this point
					if (!(fromUOM.getCategory().equals(Category.SCALAR))) {
						String msg = MessageFormat.format(MeasurementSystem.getMessage("must.be.scalar"), fromUOM);
						throw new Exception(msg);
					}

					if (!(toUOM.getCategory().equals(Category.SCALAR))) {
						String msg = MessageFormat.format(MeasurementSystem.getMessage("must.be.scalar"), toUOM);
						throw new Exception(msg);
					}

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

	final PathParameters traversePath() throws Exception {
		UnitOfMeasure pathUOM = this;
		BigDecimal pathFactor = BigDecimal.ONE;

		int count = 0;

		while (true) {
			count++;
			if (count > 10) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("conversion.depth.exceeded"),
						pathUOM.toString());
				throw new Exception(msg);
			}
			BigDecimal scalingFactor = pathUOM.getScalingFactor();
			UnitOfMeasure abscissa = pathUOM.getAbscissaUnit();

			pathFactor = decimalMultiply(pathFactor, scalingFactor);

			if (pathUOM.equals(abscissa)) {
				if (pathUOM.getCategory().equals(Category.SCALAR)) {
					break;
				}
			}

			// next UOM on path
			pathUOM = abscissa;

			// must be a scalar at this point
			if (!(pathUOM.getCategory().equals(Category.SCALAR))) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("must.be.scalar"), pathUOM);
				throw new Exception(msg);
			}
		}

		return new PathParameters(pathUOM, pathFactor);
	}

	/**
	 * Create a String representation of this unit of measure
	 * 
	 * @return String representation
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ResourceBundle symbolBundle = MeasurementSystem.getSystem().getSymbols();

		// unit enumeration
		Unit enumeration = getEnumeration();
		if (enumeration != null) {
			sb.append(symbolBundle.getString("enum.text")).append(' ').append(enumeration.toString()).append(", ");
		}

		// symbol
		String symbol = getSymbol();
		if (symbol != null) {
			sb.append(symbolBundle.getString("symbol.text")).append(' ').append(symbol);
		}
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

	void setPowerUnits(UnitOfMeasure base, int exponent) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("base.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		this.powerProduct = new PowerProduct(base, exponent, null, 0);
	}

	/**
	 * Get the exponent
	 * 
	 * @return Exponent
	 */
	public Integer getPowerExponent() {
		return powerProduct.getExponent1();
	}

	/**
	 * Get the base unit of measure for the power
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getPowerBase() {
		return powerProduct.getUOM1();
	}

	private String generateProductSymbol() {
		StringBuilder sb = new StringBuilder();

		if (getMultiplier().equals(getMultiplicand())) {
			sb.append(getMultiplier().getSymbol()).append(SQ);
		} else {
			sb.append(getMultiplier().getSymbol()).append(MULT).append(getMultiplicand().getSymbol());
		}
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

		this.powerProduct = new PowerProduct(multiplier, 1, multiplicand, 1);
	}

	/**
	 * Get the multiplier
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplier() {
		return this.powerProduct.getUOM1();
	}

	/**
	 * Get the multiplicand
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplicand() {
		return this.powerProduct.getUOM2();
	}

	String generateQuotientSymbol() {
		StringBuilder sb = new StringBuilder();
		sb.append(getDividend().getSymbol()).append('/').append(getDivisor().getSymbol());
		return sb.toString();
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

		this.powerProduct = new PowerProduct(dividend, 1, divisor, -1);
	}

	/**
	 * Get the dividend unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDividend() {
		return powerProduct.getUOM1();
	}

	/**
	 * Get the divisor unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getDivisor() {
		return this.powerProduct.getUOM2();
	}

	// this method is for optimization of BigDecimal multiplication
	private BigDecimal decimalMultiply(BigDecimal a, BigDecimal b) {
		return a.multiply(b, MATH_CONTEXT);
	}

	// this method is for optimization of BigDecimal division
	private BigDecimal decimalDivide(BigDecimal a, BigDecimal b) {
		return a.divide(b, MATH_CONTEXT);
	}

	// this method is for optimization of BigDecimal exponentiation
	private BigDecimal decimalPower(BigDecimal base, int exponent) {
		return base.pow(exponent, MATH_CONTEXT);
	}

	// this class holds the base UOMs and exponents for a product of two power
	// UOMs
	private class PowerProduct {
		// power base unit, product multiplier or quotient dividend
		private UnitOfMeasure uom1;

		// product multiplicand or quotient divisor
		private UnitOfMeasure uom2;

		// exponent
		private int exponent1 = 0;

		// second exponent
		private int exponent2 = 0;

		private PowerProduct() {

		}

		private PowerProduct(UnitOfMeasure uom1, int exponent1, UnitOfMeasure uom2, int exponent2) {
			this.uom1 = uom1;
			this.exponent1 = exponent1;
			this.uom2 = uom2;
			this.exponent2 = exponent2;
		}

		private Category getCategory() throws Exception {

			Category cat = null;

			if (uom2 == null) {
				if (uom1 == null) {
					cat = Category.SCALAR;
				} else {
					cat = Category.POWER;
				}
			} else {
				if (exponent2 < 0) {
					cat = Category.QUOTIENT;
				} else {
					cat = Category.PRODUCT;
				}
			}
			return cat;
		}

		private int getExponent1() {
			return exponent1;
		}

		private UnitOfMeasure getUOM1() {
			return this.uom1;
		}

		private UnitOfMeasure getUOM2() {
			return this.uom2;
		}
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
		private static final char ONE = '1';

		private static final int MAX_RECURSIONS = 10;

		private Map<UnitOfMeasure, Integer> terms = new HashMap<UnitOfMeasure, Integer>();

		private BigDecimal mapScalingFactor = BigDecimal.ONE;

		private int mapExponent = 1;

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

		private void explode(UnitOfMeasure unit) throws Exception {
			int counter = 0;

			boolean invert = false;

			// invert negative exponent
			if (unit.getPowerExponent() < 0) {
				invert = true;
			}

			explodeRecursively(unit, invert, counter);
		}

		private void explodeRecursively(UnitOfMeasure unit, boolean invert, int counter) throws Exception {
			if (++counter > MAX_RECURSIONS) {
				terms.clear();
				return;
			}

			BigDecimal scaling = unit.getScalingFactor();

			if (!invert) {
				mapScalingFactor = decimalMultiply(mapScalingFactor, scaling);
			} else {
				mapScalingFactor = decimalDivide(mapScalingFactor, scaling);
			}

			// explode the abscissa unit
			UnitOfMeasure abscissaUnit = unit.getAbscissaUnit();

			// check for quotient UOM
			if (abscissaUnit.getCategory().equals(Category.QUOTIENT)) {
				// numerator
				UnitOfMeasure dividend = abscissaUnit.getDividend();

				// denominator
				UnitOfMeasure divisor = abscissaUnit.getDivisor();

				// do not invert the dividend
				explodeRecursively(dividend, invert, counter);

				// invert divisor
				explodeRecursively(divisor, !invert, counter);
			} // end Quotient UOM

			// check for product UOM
			else if (abscissaUnit.getCategory().equals(Category.PRODUCT)) {
				// explode this to scalar units
				UnitOfMeasure multiplier = abscissaUnit.getMultiplier();
				explodeRecursively(multiplier, invert, counter);

				// explode other UOM
				UnitOfMeasure multiplicand = abscissaUnit.getMultiplicand();
				explodeRecursively(multiplicand, invert, counter);
			} // end Product UOM

			// check for power UOM
			else if (abscissaUnit.getCategory().equals(Category.POWER)) {
				UnitOfMeasure powerBase = abscissaUnit.getPowerBase();
				UnitOfMeasure baseUOM = powerBase.getBaseUOM();

				BigDecimal factor = powerBase.getScalingFactor();
				int power = abscissaUnit.getPowerExponent();

				BigDecimal powerScale = decimalPower(factor, power);

				// calculate overall scaling factor
				mapScalingFactor = decimalMultiply(mapScalingFactor, powerScale);

				// if down to a scalar, add them
				if (baseUOM.getCategory().equals(Category.SCALAR)) {
					int exponent = mapExponent * power;
					for (int i = 0; i < Math.abs(exponent); i++) {
						addTerm(baseUOM, invert);
					}
				} else if (baseUOM.getCategory().equals(Category.PRODUCT)) {
					// raise multiplier and multiplicand to nth power
					UnitOfMeasure multiplier = baseUOM.getMultiplier();

					// add multiplier terms
					if (multiplier.getCategory().equals(Category.SCALAR)) {
						for (int i = 0; i < Math.abs(power); i++) {
							addTerm(multiplier, invert);
						}
					} else {
						explodeRecursively(multiplier, invert, counter);
					}

					UnitOfMeasure multiplicand = baseUOM.getMultiplicand();

					// add multiplicand terms
					if (multiplicand.getCategory().equals(Category.SCALAR)) {
						for (int i = 0; i < Math.abs(power); i++) {
							addTerm(multiplicand, invert);
						}
					} else {
						explodeRecursively(multiplicand, invert, counter);
					}

				} else if (baseUOM.getCategory().equals(Category.QUOTIENT)) {
					UnitOfMeasure dividend = baseUOM.getDividend();

					// add dividend terms
					if (dividend.getCategory().equals(Category.SCALAR)) {
						for (int i = 0; i < Math.abs(power); i++) {
							addTerm(dividend, invert);
						}
					} else {
						explodeRecursively(dividend, invert, counter);
					}

					// add divisor terms
					UnitOfMeasure divisor = baseUOM.getDivisor();

					if (divisor.getCategory().equals(Category.SCALAR)) {
						for (int i = 0; i < Math.abs(power); i++) {
							addTerm(divisor, !invert);
						}
					} else {
						explodeRecursively(divisor, invert, counter);
					}
				} else if (baseUOM.getCategory().equals(Category.POWER)) {
					UnitOfMeasure base = baseUOM.getPowerBase();
					int basePower = baseUOM.getPowerExponent();
					mapExponent *= basePower * power;
					explodeRecursively(base, invert, counter);
				}
			} // end Power UOM

			// scalar UOM
			else if (abscissaUnit.getCategory().equals(Category.SCALAR)) {
				UnitOfMeasure uom = abscissaUnit.getAbscissaUnit();

				if (uom.equals(abscissaUnit)) {
					for (int i = 0; i < Math.abs(mapExponent); i++) {
						addTerm(abscissaUnit, invert);
					}
				} else {
					// keep on going
					explodeRecursively(abscissaUnit, invert, counter);
				}
			} // end Scalar UOM
		}

		private void addTerm(UnitOfMeasure uom, boolean invert) throws Exception {
			int unitPower = 1;
			int power = 0;

			if (!(uom.getCategory().equals(Category.SCALAR))) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("must.be.scalar"), uom.getSymbol());
				throw new Exception(msg);
			}

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

					denominator.append(unit.getSymbol());
					if (power < -1) {
						if (power == -2) {
							denominator.append(SQ);
						} else if (power == -3) {
							denominator.append(CUBED);
						} else {
							denominator.append(POW).append(Math.abs(power));
						}
					}
					denominatorCount++;

				} else if (power >= 1 && !unit.equals(MeasurementSystem.getSystem().getOne())) {
					// positive, put in numerator
					if (numerator.length() > 0) {
						numerator.append(MULT);
					}

					numerator.append(unit.getSymbol());

					if (power > 1) {
						if (power == 2) {
							numerator.append(SQ);
						} else if (power == 3) {
							numerator.append(CUBED);
						} else {
							numerator.append(POW).append(power);
						}
					}
					numeratorCount++;

				} else {
					// unary, don't add a '1'
				}
			}

			if (numeratorCount == 0) {
				numerator.append(ONE);
			}

			String result = null;

			if (denominatorCount == 0) {
				result = numerator.toString();
			} else {
				result = numerator.append(DIV).append(denominator).toString();
			}

			return result;
		} // end unit of measure iteration

		@Override
		public String toString() {
			return "Scaling: " + mapScalingFactor + ", Exponent: " + mapExponent + ", Terms: " + terms;
		}
	}
}
