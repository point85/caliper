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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * A UnitOfMeasure can have a linear conversion (y = ax + b) to another unit of
 * measure in the same internationally recognized measurement system of
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
 * conversion to another basic unit in another recognized measurement system.
 * This conversion is defined unidirectionally. For example, an International
 * Customary foot is 0.3048 SI metres. The conversion from metre to foot is just
 * the inverse of this relationship.
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

	// UOM types
	public enum MeasurementType {
		SCALAR, PRODUCT, QUOTIENT, POWER
	}

	// maximum length of the symbol
	private static final int MAX_SYMBOL_LENGTH = 16;

	// multiply, divide and power symbols
	private static final char MULT = (char) 0xB7;
	private static final char DIV = '/';
	private static final char POW = '^';
	private static final char SQ = (char) 0xB2;
	private static final char CUBED = (char) 0xB3;
	private static final char LP = '(';
	private static final char RP = ')';
	private static final char ONE_CHAR = '1';

	// registry of unit conversion factor (not persistent)
	private Map<UnitOfMeasure, Double> conversionRegistry = new ConcurrentHashMap<>();

	// conversion to another Unit of Measure in the same recognized measurement
	// system (y = ax + b)
	// scaling factor (a)
	private double scalingFactor = 1.0d;

	// offset (b)
	private double offset = 0.0d;

	// x-axis unit
	private UnitOfMeasure abscissaUnit;

	// unit enumerations for the various systems of measurement, e.g. KILOGRAM
	private Unit unit;

	// unit type, e.g. MASS
	private UnitType unitType = UnitType.UNCLASSIFIED;

	// conversion to another Unit of Measure in a different measurement system
	private double bridgeScalingFactor;

	// offset (b)
	private double bridgeOffset;

	// x-axis unit
	private UnitOfMeasure bridgeAbscissaUnit;

	// cached base symbol (not persistent)
	private String baseSymbol;

	// user-defined category
	private String category = MeasurementSystem.getUnitString("default.category.text");

	// base UOMs and exponents for a product of two power UOMs follow
	// power base unit, product multiplier or quotient dividend
	private UnitOfMeasure uom1;

	// product multiplicand or quotient divisor
	private UnitOfMeasure uom2;

	// exponent
	private Integer exponent1;

	// second exponent
	private Integer exponent2;

	// database primary key
	private Long primaryKey;

	// optimistic locking version
	private Integer version;

	/**
	 * Construct a default unit of measure
	 */
	public UnitOfMeasure() {
		super();
	}

	UnitOfMeasure(UnitType type, String name, String symbol, String description) {
		super(name, symbol.trim(), description);
		this.unitType = type;
		this.category = MeasurementSystem.getUnitString("default.category.text");
	}

	/**
	 * Check to see if the exponent is valid
	 * 
	 * @param exponent Power exponent
	 * @return True if it is a valid exponent
	 */
	public static boolean isValidExponent(Integer exponent) {
		return (exponent != null);
	}

	/**
	 * Get the database record's primary key
	 * 
	 * @return Key
	 */
	public Long getKey() {
		return primaryKey;
	}

	/**
	 * Set the database record's primary key
	 * 
	 * @param key Key
	 */
	public void setKey(Long key) {
		this.primaryKey = key;
	}

	/**
	 * Get the optimistic locking version
	 * 
	 * @return version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * Set the optimistic locking version
	 * 
	 * @param version Version
	 */
	public void setVersion(Integer version) {
		this.version = version;
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

	/**
	 * Get the measurement type
	 * 
	 * @return {@link MeasurementType}
	 */
	public MeasurementType getMeasurementType() {
		MeasurementType type = MeasurementType.SCALAR;

		if (isValidExponent(getExponent2()) && getExponent2() < 0) {
			type = MeasurementType.QUOTIENT;
		} else if (isValidExponent(getExponent2()) && getExponent2() > 0) {
			type = MeasurementType.PRODUCT;
		} else if (getUOM1() != null && isValidExponent(getExponent1())) {
			type = MeasurementType.POWER;
		}

		return type;
	}

	UnitOfMeasure clonePower(UnitOfMeasure uom) throws Exception {

		UnitOfMeasure newUOM = new UnitOfMeasure();
		newUOM.setUnitType(getUnitType());

		// check if quotient
		int exponent = 1;
		if (isValidExponent(getPowerExponent())) {
			exponent = getPowerExponent();
		}

		UnitOfMeasure one = MeasurementSystem.getSystem().getOne();
		if (getMeasurementType().equals(MeasurementType.QUOTIENT)) {
			if (getDividend().equals(one)) {
				exponent = getExponent2();
			} else if (getDivisor().equals(one)) {
				exponent = getExponent1();
			}
		}
		newUOM.setPowerUnit(uom, exponent);
		String symbol = UnitOfMeasure.generatePowerSymbol(uom, exponent);
		newUOM.setSymbol(symbol);
		newUOM.setName(symbol);

		return newUOM;
	}

	UnitOfMeasure clonePowerProduct(UnitOfMeasure uom1, UnitOfMeasure uom2) throws Exception {
		boolean invert = false;
		UnitOfMeasure one = MeasurementSystem.getSystem().getOne();

		// check if quotient
		if (getMeasurementType().equals(MeasurementType.QUOTIENT)) {
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
	 * @throws Exception Exception
	 */
	public UnitOfMeasure getBaseUOM() throws Exception {
		String base = getBaseSymbol();
		return MeasurementSystem.getSystem().getBaseUOM(base);
	}

	/**
	 * Get the bridge UOM scaling factor
	 * 
	 * @return Scaling factor
	 */
	public double getBridgeScalingFactor() {
		return this.bridgeScalingFactor;
	}

	/**
	 * Get the bridge UOM abscissa UOM
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getBridgeAbscissaUnit() {
		return this.bridgeAbscissaUnit;
	}

	/**
	 * Get the bridge UOM offset
	 * 
	 * @return Offset
	 */
	public double getBridgeOffset() {
		return this.bridgeOffset;
	}

	/**
	 * Set the conversion to another fundamental unit of measure
	 * 
	 * @param scalingFactor Scaling factor
	 * @param abscissaUnit  X-axis unit
	 * @param offset        Offset
	 * @throws Exception Exception
	 */
	public void setBridgeConversion(double scalingFactor, UnitOfMeasure abscissaUnit, double offset) throws Exception {
		this.bridgeScalingFactor = scalingFactor;
		this.bridgeAbscissaUnit = abscissaUnit;
		this.bridgeOffset = offset;
	}

	/**
	 * Compare this unit of measure to another one.
	 * 
	 * @param other unit of measure
	 * @return -1 if less than, 0 if equal and 1 if greater than
	 */
	@Override
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
	 * @param unit {@link Unit}
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
	 * @param unitType {@link UnitType}
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
	 * @param category Category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	private double getBridgeFactor(UnitOfMeasure uom) {
		double factor = 0.0d;

		// check for our bridge
		if (getBridgeAbscissaUnit() != null) {
			factor = getBridgeScalingFactor();
		} else {
			// try other side
			if (uom.getBridgeAbscissaUnit() != null) {
				UnitOfMeasure toUOM = uom.getBridgeAbscissaUnit();

				if (toUOM.equals(this)) {
					factor = 1.0d / uom.getBridgeScalingFactor();
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
		return Objects.hash(MeasurementSystem.getSystem(), getEnumeration(), getSymbol());
	}

	/**
	 * Compare this unit of measure to another
	 * 
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object other) {

		if (!(other instanceof UnitOfMeasure)) {
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
		if (Double.valueOf(getScalingFactor()).compareTo(Double.valueOf(otherUnit.getScalingFactor())) != 0) {
			return false;
		}

		// same offsets
		if (Double.valueOf(getOffset()).compareTo(Double.valueOf(otherUnit.getOffset())) != 0) {
			return false;
		}
		return true;
	}

	private void checkOffset(UnitOfMeasure other) throws Exception {
		if (Double.valueOf(other.getOffset()).compareTo(0.0d) != 0) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("offset.not.supported"), other.toString());
			throw new Exception(msg);
		}
	}

	private UnitOfMeasure multiplyOrDivide(UnitOfMeasure other, boolean invert) throws Exception {
		if (other == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}

		checkOffset(this);
		checkOffset(other);

		// this base symbol map
		Reducer thisReducer = getReducer();
		Map<UnitOfMeasure, Integer> thisMap = thisReducer.getTerms();

		// other base symbol map
		Reducer otherReducer = other.getReducer();
		Map<UnitOfMeasure, Integer> otherMap = otherReducer.getTerms();

		// create a map of the unit of measure powers
		Map<UnitOfMeasure, Integer> resultMap = new HashMap<>();

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
		Reducer resultReducer = new Reducer();
		resultReducer.setTerms(resultMap);

		// product or quotient
		UnitOfMeasure result = new UnitOfMeasure();

		if (!invert) {
			result.setProductUnits(this, other);
		} else {
			result.setQuotientUnits(this, other);
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

		String base = resultReducer.buildBaseString();
		UnitOfMeasure baseUOM = MeasurementSystem.getSystem().getBaseUOM(base);

		if (baseUOM != null) {
			// there is a conversion to the base UOM
			double thisFactor = thisReducer.getScalingFactor();
			double otherFactor = otherReducer.getScalingFactor();

			double resultFactor = 0;
			if (!invert) {
				resultFactor = thisFactor * otherFactor;
			} else {
				resultFactor = thisFactor / otherFactor;
			}
			result.setScalingFactor(resultFactor);
			result.setAbscissaUnit(baseUOM);
			result.setUnitType(baseUOM.getUnitType());
		}

		return result;
	}

	/**
	 * Multiply two units of measure to create a third one.
	 * 
	 * @param multiplicand {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public UnitOfMeasure multiply(UnitOfMeasure multiplicand) throws Exception {
		return multiplyOrDivide(multiplicand, false);
	}

	/**
	 * Divide two units of measure to create a third one.
	 * 
	 * @param divisor {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public UnitOfMeasure divide(UnitOfMeasure divisor) throws Exception {
		return multiplyOrDivide(divisor, true);
	}

	/**
	 * Invert a unit of measure to create a new one
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public UnitOfMeasure invert() throws Exception {
		UnitOfMeasure inverted = null;

		if (isValidExponent(getExponent2()) && getExponent2() < 0) {
			inverted = getDivisor().divide(getDividend());
		} else {
			inverted = MeasurementSystem.getSystem().getOne().divide(this);
		}

		return inverted;
	}

	/**
	 * Get the unit of measure's symbol in the fundamental units for that system.
	 * For example a Newton is a kg.m/s2.
	 * 
	 * @return Base symbol
	 * @throws Exception Exception
	 */
	public synchronized String getBaseSymbol() throws Exception {
		if (baseSymbol == null) {
			Reducer powerMap = getReducer();
			baseSymbol = powerMap.buildBaseString();
		}
		return baseSymbol;
	}

	private final synchronized Reducer getReducer() throws Exception {
		Reducer reducer = new Reducer();
		reducer.explode(this);
		return reducer;
	}

	/**
	 * Define a conversion with the specified scaling factor, abscissa unit of
	 * measure and scaling factor.
	 * 
	 * @param scalingFactor Factor
	 * @param abscissaUnit  {@link UnitOfMeasure}
	 * @param offset        Offset
	 * @throws Exception Exception
	 */
	public void setConversion(double scalingFactor, UnitOfMeasure abscissaUnit, double offset) throws Exception {
		if (abscissaUnit == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}

		// self conversion is special
		if (this.equals(abscissaUnit)) {
			if (Double.valueOf(scalingFactor).compareTo(1.0d) != 0 || Double.valueOf(offset).compareTo(0.0d) != 0) {
				throw new Exception(MeasurementSystem.getMessage("conversion.not.allowed"));
			}
		}

		// unit has been previously cached, so first remove it, then cache again
		MeasurementSystem.getSystem().unregisterUnit(this);
		baseSymbol = null;

		this.scalingFactor = scalingFactor;
		this.abscissaUnit = abscissaUnit;
		this.offset = offset;

		// re-cache
		MeasurementSystem.getSystem().registerUnit(this);

		// remove from conversion registry
		if (conversionRegistry.containsKey(abscissaUnit)) {
			conversionRegistry.remove(abscissaUnit);
		}
	}

	/**
	 * Define a conversion with a scaling factor of 1 and offset of 0 for the
	 * specified abscissa unit of measure.
	 * 
	 * @param abscissaUnit {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public void setConversion(UnitOfMeasure abscissaUnit) throws Exception {
		this.setConversion(1.0d, abscissaUnit, 0.0d);
	}

	/**
	 * Define a conversion with an offset of 0 for the specified scaling factor and
	 * abscissa unit of measure.
	 * 
	 * @param scalingFactor Factor
	 * @param abscissaUnit  {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public void setConversion(double scalingFactor, UnitOfMeasure abscissaUnit) throws Exception {
		this.setConversion(scalingFactor, abscissaUnit, 0.0d);
	}

	/**
	 * Get the unit of measure's 'b' offset (intercept) for the relation y = ax + b.
	 * 
	 * @return Offset
	 */
	public double getOffset() {
		return this.offset;
	}

	/**
	 * Set the unit of measure's 'b' offset (intercept) for the relation y = ax + b.
	 * 
	 * @param offset Offset
	 */
	public void setOffset(double offset) {
		this.offset = offset;
	}

	/**
	 * Get the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @return Factor
	 */
	public double getScalingFactor() {
		return this.scalingFactor;
	}

	/**
	 * Set the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @param scalingFactor Scaling factor
	 */
	public void setScalingFactor(double scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	/**
	 * Get the unit of measure's x-axis unit of measure for the relation y = ax + b.
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getAbscissaUnit() {
		return this.abscissaUnit != null ? this.abscissaUnit : this;
	}

	/**
	 * Set the unit of measure's x-axis unit of measure for the relation y = ax + b.
	 * 
	 * @param abscissaUnit {@link UnitOfMeasure}
	 */
	public void setAbscissaUnit(UnitOfMeasure abscissaUnit) {
		this.abscissaUnit = abscissaUnit;
	}

	private double convertScalarToScalar(UnitOfMeasure targetUOM) {
		UnitOfMeasure thisAbscissa = getAbscissaUnit();
		double thisFactor = getScalingFactor();

		double unitFactor;

		if (thisAbscissa.equals(targetUOM)) {
			// direct conversion
			unitFactor = thisFactor;
		} else {
			// indirect conversion
			unitFactor = convertUnit(targetUOM);
		}
		return unitFactor;
	}

	private double convertUnit(UnitOfMeasure targetUOM) {

		// get path factors in each system
		PathParameters thisParameters = traversePath();
		PathParameters targetParameters = targetUOM.traversePath();

		double thisPathFactor = thisParameters.getPathFactor();
		UnitOfMeasure thisBase = thisParameters.getPathUOM();

		double targetPathFactor = targetParameters.getPathFactor();
		UnitOfMeasure targetBase = targetParameters.getPathUOM();

		// check for a base conversion unit bridge
		double bridgeFactor = thisBase.getBridgeFactor(targetBase);

		if (bridgeFactor != 0.0d) {
			thisPathFactor = thisPathFactor * bridgeFactor;
		}

		// new path amount
		return thisPathFactor / targetPathFactor;
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
	 * @param targetUOM Target {@link UnitOfMeasure}
	 * @return conversion factor
	 * @throws Exception Exception
	 */
	public double getConversionFactor(UnitOfMeasure targetUOM) throws Exception {
		if (targetUOM == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}

		// first check the cache
		Double cachedFactor = conversionRegistry.get(targetUOM);

		if (cachedFactor != null) {
			return cachedFactor;
		}

		checkTypes(this, targetUOM);

		Reducer fromReducer = getReducer();
		Reducer toReducer = targetUOM.getReducer();

		Map<UnitOfMeasure, Integer> fromMap = fromReducer.getTerms();
		Map<UnitOfMeasure, Integer> toMap = toReducer.getTerms();

		if (fromMap.size() != toMap.size()) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("incompatible.units"), this, targetUOM);
			throw new Exception(msg);
		}

		double fromFactor = fromReducer.getScalingFactor();
		double toFactor = toReducer.getScalingFactor();

		double factor = 1.0d;

		// compute map factor
		int matchCount = 0;

		for (Entry<UnitOfMeasure, Integer> fromEntry : fromMap.entrySet()) {
			UnitType fromType = fromEntry.getKey().getUnitType();
			UnitOfMeasure fromUOM = fromEntry.getKey();
			Integer fromPower = fromEntry.getValue();

			for (Entry<UnitOfMeasure, Integer> toEntry : toMap.entrySet()) {
				UnitType toType = toEntry.getKey().getUnitType();

				if (fromType.equals(toType)) {
					matchCount++;
					UnitOfMeasure toUOM = toEntry.getKey();
					double bd = fromUOM.convertScalarToScalar(toUOM);
					bd = Math.pow(bd, fromPower);
					factor = factor * bd;
					break;
				}
			} // to map
		} // from map

		if (matchCount != fromMap.size()) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("incompatible.units"), this, targetUOM);
			throw new Exception(msg);
		}

		double scaling = fromFactor / toFactor;
		cachedFactor = factor * scaling;

		// cache it
		conversionRegistry.put(targetUOM, cachedFactor);

		return cachedFactor;

	}

	private final PathParameters traversePath() {
		UnitOfMeasure pathUOM = this;
		double pathFactor = 1.0d;

		while (true) {
			double unitFactor = pathUOM.getScalingFactor();
			UnitOfMeasure abscissa = pathUOM.getAbscissaUnit();

			pathFactor = pathFactor * unitFactor;

			if (pathUOM.equals(abscissa)) {
				break;
			}

			// next UOM on path
			pathUOM = abscissa;
		}

		return new PathParameters(pathUOM, pathFactor);
	}

	/**
	 * Check to see if this unit of measure has a conversion to another unit of
	 * measure other than itself.
	 * 
	 * @return True if it does not
	 */
	public boolean isTerminal() {
		return this.equals(getAbscissaUnit());
	}

	/**
	 * Create a String representation of this unit of measure
	 * 
	 * @return String representation
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
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
		double factor = getScalingFactor();
		if (Double.valueOf(factor).compareTo(1.0d) != 0) {
			sb.append(Double.toString(factor)).append(MULT);
		}

		// abscissa unit
		UnitOfMeasure abscissa = getAbscissaUnit();
		if (abscissa != null) {
			sb.append(abscissa.getSymbol());
		}

		// offset
		double uomOffset = getOffset();
		if (Double.valueOf(uomOffset).compareTo(0.0d) != 0) {
			sb.append(" + ").append(Double.toString(getOffset()));
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

	/**
	 * Set the base unit of measure and exponent
	 * 
	 * @param base     Base unit of measure
	 * @param exponent Exponent
	 * @throws Exception Exception
	 */
	public void setPowerUnit(UnitOfMeasure base, Integer exponent) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("base.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		// special cases
		if (exponent == -1) {
			setPowerProduct(MeasurementSystem.getSystem().getOne(), 1, base, -1);
		} else {
			setPowerProduct(base, exponent);
		}
	}

	/**
	 * Get the exponent of a power unit
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

	/**
	 * Set the multiplier and multiplicand
	 * 
	 * @param multiplier   Multiplier
	 * @param multiplicand Multiplicand
	 * @throws Exception Exception
	 */
	public void setProductUnits(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
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

	/**
	 * Set the dividend and divisor
	 * 
	 * @param dividend Dividend
	 * @param divisor  Divisor
	 * @throws Exception Exception
	 */
	public void setQuotientUnits(UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
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

	/**
	 * Get the most reduced units of measure
	 * 
	 * @return Map of {@link UnitOfMeasure} and exponent
	 * @throws Exception Exception
	 */
	public Map<UnitOfMeasure, Integer> getBaseUnitsOfMeasure() throws Exception {
		return getReducer().getTerms();
	}

	/**
	 * Create a power unit of measure from this unit of measure
	 * 
	 * @param exponent Power
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public UnitOfMeasure power(int exponent) throws Exception {
		return MeasurementSystem.getSystem().createPowerUOM(this, exponent);
	}

	/**
	 * If the unit of measure is unclassified, from its base unit map find a
	 * matching unit type.
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	public UnitOfMeasure classify() throws Exception {

		if (!getUnitType().equals(UnitType.UNCLASSIFIED)) {
			// already classified
			return this;
		}

		// base unit map
		Map<UnitOfMeasure, Integer> uomBaseMap = getReducer().getTerms();

		// try to find this map in the unit types
		UnitType matchedType = UnitType.UNCLASSIFIED;

		for (UnitType uomType : UnitType.values()) {
			Map<UnitType, Integer> unitTypeMap = uomType.getTypeMap();

			if (unitTypeMap.entrySet().size() != uomBaseMap.size()) {
				// not a match
				continue;
			}

			boolean match = true;

			// same size, now check base unit types and exponents
			for (Entry<UnitOfMeasure, Integer> uomBaseEntry : uomBaseMap.entrySet()) {
				UnitType uomBaseType = uomBaseEntry.getKey().getUnitType();
				Integer unitValue = unitTypeMap.get(uomBaseType);

				if (unitValue == null || !unitValue.equals(uomBaseEntry.getValue())) {
					// not a match
					match = false;
					break;
				}
			}

			if (match) {
				matchedType = uomType;
				break;
			}
		}

		if (!matchedType.equals(UnitType.UNCLASSIFIED)) {
			setUnitType(matchedType);
		}

		return this;
	}

	// UOM, scaling factor and power cumulative along a conversion path
	private class PathParameters {
		private UnitOfMeasure pathUOM;
		private double pathFactor;

		private PathParameters(UnitOfMeasure pathUOM, double pathFactor) {
			this.pathUOM = pathUOM;
			this.pathFactor = pathFactor;
		}

		private UnitOfMeasure getPathUOM() {
			return pathUOM;
		}

		private double getPathFactor() {
			return pathFactor;
		}
	}

	// reduce a unit of measure to its most basic scalar units of measure.
	private class Reducer {
		private static final int MAX_RECURSIONS = 100;

		// starting level
		private static final int STARTING_LEVEL = -1;

		// UOMs and their exponents
		private Map<UnitOfMeasure, Integer> terms = new HashMap<>();

		// the overall scaling factor
		private double mapScalingFactor = 1.0d;

		// list of exponents down a path to the leaf UOM
		private List<Integer> pathExponents = new ArrayList<>();

		// recursion counter
		private int counter = 0;

		private Reducer() {

		}

		@Override
		public String toString() {
			return mapScalingFactor + ", " + terms.toString();
		}

		private double getScalingFactor() {
			return this.mapScalingFactor;
		}

		private Map<UnitOfMeasure, Integer> getTerms() {
			return terms;
		}

		private void setTerms(Map<UnitOfMeasure, Integer> terms) {
			this.terms = terms;
		}

		private void explode(UnitOfMeasure unit) throws Exception {
			explodeRecursively(unit, STARTING_LEVEL);
		}

		private void explodeRecursively(final UnitOfMeasure unit, int level) throws Exception {
			if (++counter > MAX_RECURSIONS) {
				String msg = MessageFormat.format(MeasurementSystem.getMessage("circular.references"),
						unit.getSymbol());
				throw new Exception(msg);
			}

			// down a level
			level++;

			// scaling factor to abscissa unit
			double unitFactor = unit.getScalingFactor();

			// explode the abscissa unit
			UnitOfMeasure uomUnit = unit.getAbscissaUnit();

			UnitOfMeasure uomOne = uomUnit.getUOM1();
			UnitOfMeasure uomTwo = uomUnit.getUOM2();

			Integer exp1 = uomUnit.getExponent1();
			Integer exp2 = uomUnit.getExponent2();

			// scaling
			if (!pathExponents.isEmpty()) {
				int lastExponent = pathExponents.get(pathExponents.size() - 1);

				// compute the overall scaling factor
				double factor = 1.0d;
				for (int i = 0; i < Math.abs(lastExponent); i++) {
					factor = factor * unitFactor;
				}

				if (lastExponent < 0) {
					mapScalingFactor = mapScalingFactor / factor;
				} else {
					mapScalingFactor = mapScalingFactor * factor;
				}
			} else {
				mapScalingFactor = unitFactor;
			}

			if (uomOne == null) {
				if (!uomUnit.isTerminal()) {
					// keep exploding down the conversion path
					double currentMapFactor = mapScalingFactor;
					mapScalingFactor = 1.0d;
					explodeRecursively(uomUnit, STARTING_LEVEL);
					mapScalingFactor = mapScalingFactor * currentMapFactor;
				} else {

					// multiply out all of the exponents down the path
					int pathExponent = 1;

					for (Integer exp : pathExponents) {
						pathExponent = pathExponent * exp;
					}

					boolean invert = pathExponent < 0;

					for (int i = 0; i < Math.abs(pathExponent); i++) {
						addTerm(uomUnit, invert);
					}
				}
			} else {
				// explode UOM #1
				pathExponents.add(exp1);
				explodeRecursively(uomOne, level);
				pathExponents.remove(level);
			}

			if (uomTwo != null) {
				// explode UOM #2
				pathExponents.add(exp2);
				explodeRecursively(uomTwo, level);
				pathExponents.remove(level);
			}
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
			SortedSet<UnitOfMeasure> keys = new TreeSet<>(terms.keySet());

			for (UnitOfMeasure keyUnit : keys) {
				int power = terms.get(keyUnit);

				if (power < 0) {
					// negative, put in denominator
					if (denominator.length() > 0) {
						denominator.append(MULT);
					}

					if (!keyUnit.equals(MeasurementSystem.getSystem().getOne())) {
						denominator.append(keyUnit.getSymbol());
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
				} else if (power >= 1 && !keyUnit.equals(MeasurementSystem.getSystem().getOne())) {
					// positive, put in numerator
					if (numerator.length() > 0) {
						numerator.append(MULT);
					}

					numerator.append(keyUnit.getSymbol());
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
