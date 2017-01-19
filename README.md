# Caliper
The Caliper project manages units of measure and conversions between them.  Caliper is designed to be lightweight and simple to use, yet comprehensive.  It includes a large number of pre-defined units of measure commonly found in science, engineering, technology and the household.  These recognized systems of measurement include the International System of Units (SI), International Customary, United States and British Imperial.  Custom units of measure can also be created in the Caliper unified measurement system.  Custom units are specific to a trade or industry such as industrial packaging where units of can, bottle, case and pallet are typical.  Custom units can be added to the unified system for units that are not pre-defined. 

A Caliper measurement system is a collection of units of measure where each pair has a linear relationship, i.e. y = ax + b where 'x' is the abscissa unit to be converted, 'y' (the ordinate) is the converted unit, 'a' is the scaling factor and 'b' is the offset.  In the absence of a defined conversion, a unit will always have a conversion to itself.  A bridge unit conversion is defined to convert between the fundamental SI and International customary units of mass (i.e. kilogram to pound mass), length (i.e. metre to foot) and temperature (i.e. Kelvin to Rankine).  These three bridge conversions permit unit of measure conversions between the two systems.  A custom unit can define any bridge conversion such as a bottle to US fluid ounces or litres.
 
## Concepts

The diagram below illustrates these concepts.
![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/CaliperDiagram.png)
 
All units are owned by the unified measurement system. Units 'x' and 'y' belong to a relational system (such as SI or International Customary).  Units 'w' and 'z' belong to a second relational system.  Unit 'y' has a linear conversion to unit 'x'; therefore 'x' must be defined before 'y' can be defined.  Unit 'x' is also related to 'y' by x = (y - b)/a.  Unit 'w' has a conversion to unit 'z'.  Unit 'z' is related to itself by z = z + 0. Unit 'x' has a bridge conversion defined to unit 'z' (for example a foot to a metre).  Note that a bridge conversion from 'z' to 'x' is not necessary since it is the inverse of the conversion from 'x' to 'z'.
 
*Scalar Unit* 

A simple unit, for example a metre, is defined as a scalar UOM.  A special scalar unit of measure is unity or dimensionless "1".  

*Product Unit*

A unit of measure that is the product of two other units is defined as a product UOM.  An example is a Joule which is a Newton·metre.  

*Quotient Unit*  

A unit of measure that is the quotient of two other units is defined as a quotient UOM. An example is a velocity, e.g. metre/second.  

*Power Unit*

A unit of measure that has an exponent on a base unit is defined as a power UOM. An example is area in metre^2. Note that an exponent of 0 is unity, and an exponent of 1 is the base unit itself. An exponent of 2 is a product unit where the multiplier and multiplicand are the base unit.  A power of -1 is a quotient unit of measure where the dividend is 1 and the divisor is the base unit.  

*Type*

Units are classified by type, e.g. length, mass, time and temperature.  Only units of the same type can be converted to one another. Pre-defined units of measure are also enumerated, e.g. kilogram, Newton, metre, etc.  Custom units (e.g. a 1 litre bottle) do not have a pre-defined type or enumeration and are referred to by a unique base symbol.

*Base Symbol*
 
All units have a base symbol that is the most reduced form of the unit.  For example, a Newton is kilogram·metre/second^2.  The base symbol is used in the measurement system to register each unit and to discern the result of arithmetic operations on quantities.  For example, dividing a quantity of Newton·metres by a quantity of metres results in a quantity of Newtons. 

*Quantity*

A quantity is an amount (implemented as a BigDecimal for control of precision and scaling) together with a unit of measure.  When arithmetic operations are performed on quantities, the original units can be transformed.  For example, multiplying a length quantity in metres by a force quantity in Newtons results in a quantity of energy in Joules (or Newton-metres).

*Product of Powers*

A unit of measure is represented internally as a product of two other power units of measure:

![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/PowerProduct.png)

For a simple scalar UOM (e.g. kilogram), both of the UOMs are null with the exponents defaulted to 0.  For a product UOM (e.g. Newton), the first UOM is the multiplier and the second is the multiplicand with both exponents set to 1.  For a quotient UOM (e.g. kilograms/hour), the first UOM is the dividend and the second is the divisor.  The dividend has an exponent of 1 and the divisor an exponent of -1.  For a power UOM (e.g. square metres), the first UOM is the base and the exponent is the power.  In this case, the second UOM is null with the exponent defaulted to 0.

In order to obtain the base symbol, these two units of measure are recursively evaluated until a scalar is reached.  When a power UOM is encountered, that UOM is evaluated n times.
 
## Code Examples
The singleton unified MeasurementSystem is obtained by calling:
```java
MeasurementSystem sys = MeasurementSystem.getSystem();
```
The Unit.properties file defines the name, symbol, description and UCUM symbol for each of the predefined units in the following code examples.  The Unit.properties file is localizable.  For example, 'metres' can be changed to use the US spelling 'meters' or descriptions can be translated to another language.

The metre scalar UOM is created by the MeasurementSystem as follows:
```java
UnitOfMeasure uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, symbols.getString("m.name"),
					symbols.getString("m.symbol"), symbols.getString("m.desc"), symbols.getString("m.unified"));
``` 

The square metre power UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, symbols.getString("m2.name"),
					symbols.getString("m2.symbol"), symbols.getString("m2.desc"), symbols.getString("m2.unified"),
					getUOM(Unit.METRE), 2);
```

The metre per second quotient UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SECOND, 
					symbols.getString("mps.name"), symbols.getString("mps.symbol"), 
					symbols.getString("mps.desc"), symbols.getString("mps.unified"), 
					getUOM(Unit.METRE), getSecond());
```

The Newton product UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, symbols.getString("newton.name"),
					symbols.getString("newton.symbol"), symbols.getString("newton.desc"),
					symbols.getString("newton.unified"), getUOM(Unit.KILOGRAM), 
					getUOM(Unit.METRE_PER_SECOND_SQUARED));
```

A millisecond is 1/1000th of a second with a defined prefix and created as:

```java
UnitOfMeasure second = sys.getSecond();
UnitOfMeasure msec = sys.getUOM(Prefix.MILLI, second);
```

For a second example, a US gallon = 231 cubic inches:
```java			
Conversion	conversion = new Conversion(Quantity.createAmount("231"), getUOM(Unit.CUBIC_INCH));
UnitOfMeasure uom = createScalarUOM(UnitType.VOLUME, Unit.US_GALLON, symbols.getString("us_gallon.name"),
					symbols.getString("us_gallon.symbol"), symbols.getString("us_gallon.desc"),
					symbols.getString("us_gallon.unified"));
uom.setConversion(conversion);
```

When creating the foot unit of measure in the unified measurement system, a bridge conversion to metre is defined (1 foot = 0.3048m):
```java
UnitOfMeasure uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, symbols.getString("foot.name"),
					symbols.getString("foot.symbol"), symbols.getString("foot.desc"),
					symbols.getString("foot.unified"));

// bridge to SI
Conversion conversion = new Conversion(Quantity.createAmount("0.3048"), getUOM(Unit.METRE));
uom.setBridge(conversion);
```

Custom units and conversions can also be created:
```java
// gallons per hour
QuotientUOM gph = sys.createQuotientUOM(UnitType.VOLUMETRIC_FLOW, "gph", "gal/hr", "gallons per hour", 
	sys.getUOM(Unit.US_GALLON), sys.getHour());

// 1 16 oz can = 16 fl. oz.
ScalarUOM one16ozCan = sys.createScalarUOM(UnitType.VOLUME, "16 oz can", "16ozCan", "16 oz can");
one16ozCan.setConversion(new Conversion(Quantity.createAmount("16"), sys.getUOM(Unit.US_FLUID_OUNCE)));

// 400 cans = 50 US gallons
Quantity q400 = new Quantity("400", one16ozCan);
Quantity q50 = q400.convert(sys.getUOM(Unit.US_GALLON));

// 1 12 oz can = 12 fl.oz.
ScalarUOM one12ozCan = sys.createScalarUOM(UnitType.VOLUME, "12 oz can", "12ozCan", "12 oz can");
one12ozCan.setConversion(new Conversion(Quantity.createAmount("12"), sys.getUOM(Unit.US_FLUID_OUNCE)));

// 48 12 oz cans = 36 16 oz cans
Quantity q48 = new Quantity("48", one12ozCan);
Quantity q36 = q48.convert(one16ozCan);

// 6 12 oz cans = 1 6-pack of 12 oz cans
Conversion conversion = new Conversion(six, one12ozCan);
ScalarUOM sixPackCan = sys.createScalarUOM(UnitType.VOLUME, "6-pack", "6PCan", "6-pack of 12 oz cans");
sixPackCan.setConversion(conversion);	
```

Quantities can be added, subtracted and converted:
```java
UnitOfMeasure m = sys.getUOM(Unit.METRE);
UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		
Quantity q1 = new Quantity("2", m);
Quantity q2 = new Quantity("2", cm);
		
// add two quantities.  q3 is 2.02 metre
Quantity q3 = q1.add(q2);
		
// q4 is 202 cm
Quantity q4 = q3.convert(cm);
		
// subtract q1 from q3 to get 0.02m
q3 = q3.subtract(q1);
```

as well as multiplied and divided:
```java
BigDecimal bd = Quantity.createAmount("50");
Quantity q1 = new Quantity(bd, cm);
Quantity q2 = new Quantity(bd, cm);
		
// q3 = 2500 cm^2
Quantity q3 = q1.multiply(q2);
		
// q4 = 50 cm
Quantity q4 = q3.divide(q1);
```

and inverted:
```java
UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND); 
Quantity q1 = new Quantity(BigDecimal.TEN, mps);
		
// q2 = 0.1 sec/m
Quantity q2 = q1.invert();
```

One's Body Mass Index (BMI) can be calculated as:
```java
Quantity height = new Quantity("2", sys.getUOM(Unit.METRE));
Quantity mass = new Quantity("100", sys.getUOM(Unit.KILOGRAM));
Quantity bmi = mass.divide(height.multiply(height));
```

and Einstein's famous E = mc^2:
```java
UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
UnitOfMeasure joule = sys.getUOM(Unit.JOULE);
UnitOfMeasure c2 = sys.createPowerUOM(UnitType.CUSTOM, "c2", "c^2", "speed of light squared", sys.getUOM(Unit.LIGHT_VELOCITY), 2);
Quantity oneC2 = new Quantity(BigDecimal.ONE, c2);
Quantity oneKg = new Quantity(BigDecimal.ONE, kg);
Quantity energy = oneKg.multiply(oneC2);
Quantity joules = energy.convert(joule);
```

and photon energy using Planck's constant:
```java
// low frequency red light
UnitOfMeasure THz = sys.getUOM(Prefix.TERA, sys.getUOM(Unit.HERTZ));
Quantity lightFrequency = new Quantity("400", THz);

// Planck's constant
UnitOfMeasure h = sys.getUOM(Unit.PLANCKS_CONSTANT);
Quantity planck = new Quantity(BigDecimal.ONE, h);

// photon energy (approx. 1.65 eV)
Quantity ev = planck.multiply(lightFrequency).convert(sys.getUOM(Unit.ELECTRON_VOLT));

// wavelength of red light in nanometres (approx 749.48)
Quantity vc = new Quantity(BigDecimal.ONE, sys.getUOM(Unit.LIGHT_VELOCITY));
Quantity wavelength = vc.divide(lightFrequency).convert(sys.getUOM(Prefix.NANO, sys.getUOM(Unit.METRE)));
```

To make working with linearly scaled units of measure (with no offset) easier, the MeasurementSystem's getUOM() using a Prefix can be used.  This method accepts a Prefix enum and the unit of measure that it is scaled against.  The resulting unit of measure has a name concatented with the Prefix's name and target unit name.  The symbol is formed similarly.  For example, a centilitre (cL) is created from the pre-defined litre by:
```java
UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
UnitOfMeasure cL = sys.getUOM(Prefix.CENTI, litre);
```
and, a megabyte (MB = 2^20 bytes) is created by:
```java
UnitOfMeasure mB = sys.getUOM(Prefix.CSMEGA, sys.getUOM(Unit.BYTE));
```
###Caching
A unit of measure once created is registered in two hashmaps, one by its base symbol key and the second one by its enumeration key.  Caching greatly increases performance since the unit of measure is created only once.  Methods are provided to clear the cache of all instances as well as to unregister a particular instance.

The BigDecimal value of a unit of measure conversion is also cached.  This performance optimization eliminates the need to calculate the conversion multiple times if many quantities are being converted at once; for example, operations upon a vector or matrix of quantities all with the same unit of measure.

## Localization
All externally visible text is defined in two resource bundle .properties files.  The Unit.properties file has the name (.name), symbol (.symbol), description (.desc) and UCUM symbol (.unified) for a unit of measure as well as toString() method text.  The Message.properties file has the text for an exception.  A default English file for each is included in the project.  The files can be translated to another language by following the Java locale naming conventions for the properties file, or the English version can be edited, e.g. to change "metre" to "meter".  For example, a metre's text is:

```java
# metre
m.name = metre
m.symbol = m
m.desc = The length of the path travelled by light in vacuum during a time interval of 1/299792458 of a second.
m.unified = m
```

and for an exception:
```java
already.created = The unit of measure with symbol {0} has already been created by {1}.  Did you intend to scale this unit with a linear conversion?
```

## Project Structure
Caliper depends upon Java 6+.  The unit tests depend on JUnit (http://junit.org/junit4/), Hamcrest (http://hamcrest.org/), Gson (https://github.com/google/gson) and HTTP Request (https://github.com/kevinsawicki/http-request). 

Caliper is a Gradle project with the following structure:
 * `/build/docs/javadoc` javadoc files
 * `/build/libs` compiled caliper.jar 
 * `/doc` documentation
 * `/src/main/java` - java source files
 * `/src/main/resources` - localizable Message.properties file to define error messages and localizable Unit.properties file to define the unit's name, symbol, description and UCUM symbol.
 * `/src/test/java` - JUnit test java source files 

## JSR 363
JSR 363 "proposes to establish safe and useful methods for modeling physical quantities" (https://java.net/downloads/unitsofmeasurement/JSR363Specification_EDR.pdf).  Caliper shares many of the underlying aspects of JSR 363.  In particular:
* UnitOfMeasure class is similar to Unit interface
* Quantity class is similar to Quantity interface
* UnitType enum is similar to Dimension interface
* MeasurementSystem class is similar to SystemOfUnits interface, and also incorporates aspects of ServiceProvider

Caliper however does not use Java generics, and there is only one system of units.  Caliper performs math using BigDecimal amounts whereas JSR 363 uses Numbers.