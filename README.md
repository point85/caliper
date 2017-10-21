# Caliper
The Caliper library project manages units of measure and conversions between them.  Caliper is designed to be lightweight and simple to use, yet comprehensive.  It includes a large number of pre-defined units of measure commonly found in science, engineering, technology, finance and the household.  These recognized systems of measurement include the International System of Units (SI), International Customary, United States and British Imperial.  Custom units of measure can also be created in the Caliper unified measurement system.  Custom units are specific to a trade or industry such as industrial packaging where units of can, bottle, case and pallet are typical.  Custom units can be added to the unified system for units that are not pre-defined.  The Caliper library is also available in C# at https://github.com/point85/CaliperSharp.

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

From the two power products, a unit of measure can then be recursively reduced to a map of base units of measure and corresponding exponents along with a scaling factor.  For example, a Newton reduces to (kg, 1), (m, 1), (s, -2) in the SI system.  Multiplying, dividing and converting a unit of measure is accomplished by merging the two maps (i.e. "cancelling out" units) and then computing the overall scaling factor.  The base symbol is obtained directly from the final map.
 
## Code Examples
The singleton unified MeasurementSystem is obtained by calling:
```java
MeasurementSystem sys = MeasurementSystem.getSystem();
```
The Unit.properties file defines the name, symbol, description and UCUM symbol for each of the predefined units in the following code examples.  The Unit.properties file is localizable.  For example, 'metres' can be changed to use the US spelling 'meters' or descriptions can be translated to another language.

The metre scalar UOM is created by the MeasurementSystem as follows:
```java
UnitOfMeasure uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, symbols.getString("m.name"),
	symbols.getString("m.symbol"), symbols.getString("m.desc"));
``` 

The square metre power UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, symbols.getString("m2.name"),
	symbols.getString("m2.symbol"), symbols.getString("m2.desc"), getUOM(Unit.METRE), 2);
```

The metre per second quotient UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SEC, 
	symbols.getString("mps.name"), symbols.getString("mps.symbol"), symbols.getString("mps.desc"),  
	getUOM(Unit.METRE), getSecond());
```

The Newton product UOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, symbols.getString("newton.name"),
	symbols.getString("newton.symbol"), symbols.getString("newton.desc"),
	getUOM(Unit.KILOGRAM), getUOM(Unit.METRE_PER_SECOND_SQUARED));
```

A millisecond is 1/1000th of a second with a defined prefix and created as:

```java
UnitOfMeasure second = sys.getSecond();
UnitOfMeasure msec = sys.getUOM(Prefix.MILLI, second);
```

For a second example, a US gallon = 231 cubic inches:
```java			
UnitOfMeasure uom = createScalarUOM(UnitType.VOLUME, Unit.US_GALLON, symbols.getString("us_gallon.name"),
	symbols.getString("us_gallon.symbol"), symbols.getString("us_gallon.desc"));
uom.setConversion(231d, getUOM(Unit.CUBIC_INCH));
```

When creating the foot unit of measure in the unified measurement system, a bridge conversion to metre is defined (1 foot = 0.3048m):
```java
UnitOfMeasure uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, symbols.getString("foot.name"),
	symbols.getString("foot.symbol"), symbols.getString("foot.desc"));

// bridge to SI
uom.setBridgeConversion(0.3048, getUOM(Unit.METRE), 0);
```

Custom units and conversions can also be created:
```java
// gallons per hour
UnitOfMeasure gph = sys.createQuotientUOM(UnitType.VOLUMETRIC_FLOW, "gph", "gal/hr", "gallons per hour", 
	sys.getUOM(Unit.US_GALLON), sys.getHour());

// 1 16 oz can = 16 fl. oz.
UnitOfMeasure one16ozCan = sys.createScalarUOM(UnitType.VOLUME, "16 oz can", "16ozCan", "16 oz can");
one16ozCan.setConversion(16d, sys.getUOM(Unit.US_FLUID_OUNCE));

// 400 cans = 50 US gallons
Quantity q400 = new Quantity(400d, one16ozCan);
Quantity q50 = q400.convert(sys.getUOM(Unit.US_GALLON));

// 1 12 oz can = 12 fl.oz.
UnitOfMeasure one12ozCan = sys.createScalarUOM(UnitType.VOLUME, "12 oz can", "12ozCan", "12 oz can");
one12ozCan.setConversion(12d, sys.getUOM(Unit.US_FLUID_OUNCE));

// 48 12 oz cans = 36 16 oz cans
Quantity q48 = new Quantity(48d, one12ozCan);
Quantity q36 = q48.convert(one16ozCan);

// 6 12 oz cans = 1 6-pack of 12 oz cans
UnitOfMeasure sixPackCan = sys.createScalarUOM(UnitType.VOLUME, "6-pack", "6PCan", "6-pack of 12 oz cans");
sixPackCan.setConversion(6d, one12ozCan);	

// 1 case = 4 6-packs
UnitOfMeasure fourPackCase = sys.createScalarUOM(UnitType.VOLUME, "6-pack case", "4PCase", "four 6-packs");
fourPackCase.setConversion(4d, sixPackCan);
		
// A beer bottling line is rated at 2000 12 ounce cans/hour (US) at the
// filler. The case packer packs four 6-packs of cans into a case.
// Assuming no losses, what should be the rating of the case packer in
// cases per hour? And, what is the draw-down rate on the holding tank
// in gallons/minute?
UnitOfMeasure canph = sys.createQuotientUOM(one12ozCan, sys.getHour());
UnitOfMeasure caseph = sys.createQuotientUOM(fourPackCase, sys.getHour());
UnitOfMeasure gpm = sys.createQuotientUOM(sys.getUOM(Unit.US_GALLON), sys.getMinute());
		
// filler production rate
Quantity filler = new Quantity(2000d, canph);

// tank draw-down
Quantity draw = filler.convert(gpm);

// case packer production
Quantity packer = filler.convert(caseph);
```

Quantities can be added, subtracted and converted:
```java
UnitOfMeasure m = sys.getUOM(Unit.METRE);
UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		
Quantity q1 = new Quantity(2d, m);
Quantity q2 = new Quantity(2d, cm);
		
// add two quantities.  q3 is 2.02 metre
Quantity q3 = q1.add(q2);
		
// q4 is 202 cm
Quantity q4 = q3.convert(cm);
		
// subtract q1 from q3 to get 0.02 metre
q3 = q3.subtract(q1);
```

as well as multiplied and divided:
```java
Quantity q1 = new Quantity(50d, cm);
Quantity q2 = new Quantity(50d, cm);
		
// q3 = 2500 cm^2
Quantity q3 = q1.multiply(q2);
		
// q4 = 50 cm
Quantity q4 = q3.divide(q1);
```

and inverted:
```java
UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND); 
Quantity q1 = new Quantity(10d, mps);
		
// q2 = 0.1 sec/m
Quantity q2 = q1.invert();
```

To make working with linearly scaled units of measure (with no offset) easier, the MeasurementSystem's getUOM() using a Prefix can be used.  This method accepts a Prefix enum and the unit of measure that it is scaled against.  The resulting unit of measure has a name concatented with the Prefix's name and target unit name.  The symbol is formed similarly.  For example, a centilitre (cL) is created from the pre-defined litre by:
```java
UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
UnitOfMeasure cL = sys.getUOM(Prefix.CENTI, litre);
```
and, a megabyte (MB = 2^20 bytes) is created by:
```java
UnitOfMeasure mB = sys.getUOM(Prefix.MEBI, Unit.BYTE);
```

*Implicit Conversions*

A quantity can be converted to another unit of measure without requiring the target UOM to first be created.  If the quantity has a product or quotient UOM, use the convertToPowerProduct() method.  For example:

```java
// convert 1 newton-metre to pound force-inches
Quantity nmQ = new Quantity(1.0, sys.getUOM(Unit.NEWTON_METRE));
Quantity lbfinQ = nmQ.convertToPowerProduct(sys.getUOM(Unit.POUND_FORCE), sys.getUOM(Unit.INCH));
```

If the quantity has power UOM, use the convertToPower() method.  For example:

```java
// convert 1 square metre to square inches
Quantity m2Q = new Quantity(1.0, sys.getUOM(Unit.SQUARE_METRE));
Quantity in2Q = m2Q.convertToPower(sys.getUOM(Unit.INCH));
```

Other UOMs can be converted using the convert() method.

## Physical Unit Equation Examples

Water boils at 100 degrees Celcius.  What is this temperature in Fahrenheit?
```java
Quantity qC = new Quantity(100.0, Unit.CELSIUS);
Quantity qF = qC.convert(Unit.FAHRENHEIT);
```

One's Body Mass Index (BMI) can be calculated as:
```java
Quantity height = new Quantity(2d, Unit.METRE);
Quantity mass = new Quantity(100d, Unit.KILOGRAM);
Quantity bmi = mass.divide(height.multiply(height));
```

Einstein's famous E = mc^2:
```java
Quantity c = sys.getQuantity(Constant.LIGHT_VELOCITY);
Quantity m = new Quantity(1.0, Unit.KILOGRAM);
Quantity e = m.multiply(c).multiply(c);
```

Ideal Gas Law, PV = nRT.  A cylinder of argon gas contains 50.0 L of Ar at 18.4 atm and 127 °C.  How many moles of argon are in the cylinder?
```java
Quantity p = new Quantity(18.4, Unit.ATMOSPHERE).convert(Unit.PASCAL);
Quantity v = new Quantity(50d, Unit.LITRE).convert(Unit.CUBIC_METRE);
Quantity t = new Quantity(127d, Unit.CELSIUS).convert(Unit.KELVIN);
Quantity n = p.multiply(v).divide(sys.getQuantity(Constant.GAS_CONSTANT).multiply(t));
```

Photon energy using Planck's constant:
```java
// energy of red light photon = Planck's constant times the frequency
Quantity frequency = new Quantity(400d, sys.getUOM(Prefix.TERA, Unit.HERTZ));
Quantity ev = sys.getQuantity(Constant.PLANCK_CONSTANT).multiply(frequency).convert(Unit.ELECTRON_VOLT);

// and wavelength of red light in nanometres (approx 749.48)
Quantity wavelength = sys.getQuantity(Constant.LIGHT_VELOCITY).divide(frequency).convert(sys.getUOM(Prefix.NANO, Unit.METRE));
```

Newton's second law of motion (F = ma). Weight of 1 kg in lbf:
```java
Quantity mkg = new Quantity(1d, Unit.KILOGRAM);
Quantity f = mkg.multiply(sys.getQuantity(Constant.GRAVITY)).convert(Unit.POUND_FORCE);
```
Units per volume of solution, C = A x (m/V)
```java
// create the "A" unit of measure
UnitOfMeasure activityUnit = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "activity", "act",
	"activity of material", sys.getUOM(Unit.UNIT), sys.getUOM(Prefix.MILLI, Unit.GRAM));

// calculate concentration
Quantity activity = new Quantity(1d, activityUnit);
Quantity grams = new Quantity(1d, Unit.GRAM).convert(Prefix.MILLI, Unit.GRAM);
Quantity volume = new Quantity(1d, sys.getUOM(Prefix.MILLI, Unit.LITRE));
Quantity concentration = activity.multiply(grams.divide(volume));
Quantity katals = concentration.multiply(new Quantity(1d, Unit.LITRE)).convert(Unit.KATAL);
```
Black body radiation:

```java
// The Stefan-Boltzmann law states that the power emitted per unit area
// of the surface of a black body is directly proportional to the fourth
// power of its absolute temperature: sigma * T^4
// calculate at 1000 Kelvin
Quantity temp = new Quantity(1000.0, Unit.KELVIN);
Quantity intensity = sys.getQuantity(Constant.STEFAN_BOLTZMANN).multiply(temp.power(4));
```

Expansion of the universe:

```java
// Hubble's law, v = H0 x D. Let D = 10 Mpc
Quantity d = new Quantity(10d, sys.getUOM(Prefix.MEGA, sys.getUOM(Unit.PARSEC)));
Quantity h0 = sys.getQuantity(Constant.HUBBLE_CONSTANT);
Quantity velocity = h0.multiply(d);
```

Device Characteristic Life

```java
// A device has an activation energy of 0.5 and a characteristic life of
// 2,750 hours at an accelerated temperature of 150 degrees Celsius.
// Calculate the characteristic life at an expected use temperature of
// 85 degrees Celsius.

// Convert the Boltzman constant from J/K to eV/K for the Arrhenius equation
Quantity j = new Quantity(1d, Unit.JOULE);
Quantity eV = j.convert(Unit.ELECTRON_VOLT);
// Boltzmann constant
Quantity Kb = sys.getQuantity(Constant.BOLTZMANN_CONSTANT).multiply(eV.getAmount());
// accelerated temperature
Quantity Ta = new Quantity(150d, Unit.CELSIUS);
// expected use temperature
Quantity Tu = new Quantity(85d, Unit.CELSIUS);
// calculate the acceleration factor
Quantity factor1 = Tu.convert(Unit.KELVIN).invert().subtract(Ta.convert(Unit.KELVIN).invert());
Quantity factor2 = Kb.invert().multiply(0.5);
Quantity factor3 = factor1.multiply(factor2);
double AF = Math.exp(factor3.getAmount());
// calculate longer life at expected use temperature
Quantity life85 = new Quantity(2750d, Unit.HOUR);
Quantity life150 = life85.multiply(AF);
```

## Financial Examples

Value of a stock portfolio:

```java
// John has 100 shares of Alphabet Class A stock. How much is his
// portfolio worth in euros when the last trade was $838.96 and a US
// dollar is worth 0.94 euros?
UnitOfMeasure euro = sys.getUOM(Unit.EURO);
UnitOfMeasure usd = sys.getUOM(Unit.US_DOLLAR);
usd.setConversion(0.94, euro);

UnitOfMeasure googl = sys.createScalarUOM(UnitType.CURRENCY, "Alphabet A", "GOOGL",
	"Alphabet (formerly Google) Class A shares");
googl.setConversion(838.96, usd);
Quantity portfolio = new Quantity(100, googl);
Quantity value = portfolio.convert(euro);
```

## Medical Examples

```java
// convert Unit to nanokatal
UnitOfMeasure u = sys.getUOM(Unit.UNIT);
UnitOfMeasure katal = sys.getUOM(Unit.KATAL);
Quantity q1 = new Quantity(1.0, u);
Quantity q2 = q1.convert(sys.getUOM(Prefix.NANO, katal));

// test result Equivalent
UnitOfMeasure eq = sys.getUOM(Unit.EQUIVALENT);
UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
UnitOfMeasure mEqPerL = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "milliNormal", "mEq/L",
	"solute per litre of solvent ", sys.getUOM(Prefix.MILLI, eq), litre);
Quantity testResult = new Quantity(5.0, mEqPerL);

// blood cell count test results
UnitOfMeasure k = sys.getUOM(Prefix.KILO, sys.getOne());
UnitOfMeasure uL = sys.getUOM(Prefix.MICRO, Unit.LITRE);
UnitOfMeasure kul = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "K/uL", "K/uL",
	"thousands per microlitre", k, uL);
testResult = new Quantity(7.0, kul);

UnitOfMeasure fL = sys.getUOM(Prefix.FEMTO, Unit.LITRE);
testResult = new Quantity(90d, fL);

// TSH test result
UnitOfMeasure uIU = sys.getUOM(Prefix.MICRO, Unit.INTERNATIONAL_UNIT);
UnitOfMeasure mL = sys.getUOM(Prefix.MILLI, Unit.LITRE);
UnitOfMeasure uiuPerml = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "uIU/mL", "uIU/mL",
	"micro IU per millilitre", uIU, mL);
testResult = new Quantity(2.0, uiuPerml);
```

### Caching
A unit of measure once created is registered in two hashmaps, one by its base symbol key and the second one by its enumeration key.  Caching greatly increases performance since the unit of measure is created only once.  Methods are provided to clear the cache of all instances as well as to unregister a particular instance.

The double value of a unit of measure conversion is also cached.  This performance optimization eliminates the need to calculate the conversion multiple times if many quantities are being converted at once; for example, operations upon a vector or matrix of quantities all with the same unit of measure.

## Localization
All externally visible text is defined in two resource bundle .properties files.  The Unit.properties file has the name (.name), symbol (.symbol) and description (.desc) for a unit of measure as well as toString() method text.  The Message.properties file has the text for an exception.  A default English file for each is included in the project.  The files can be translated to another language by following the Java locale naming conventions for the properties file, or the English version can be edited, e.g. to change "metre" to "meter".  For example, a metre's text is:

```java
# metre
m.name = metre
m.symbol = m
m.desc = The length of the path travelled by light in vacuum during a time interval of 1/299792458 of a second.
```

and for an exception:
```java
already.created = The unit of measure with symbol {0} has already been created by {1}.  Did you intend to scale this unit with a linear conversion?
```

## Unit of Measure Application
An example Unit of Measure converter and editor desktop application has been built to demonstrate fundamental capabilities of the library.  The user interface is implemented in JavaFX 8 and database persistency is provided by JPA (Java Persistence API) with FXML descriptors.  EclipseLink is the JPA implementation for a Microsoft SQL Server database.

The editor allows new units of measure to be created and saved to the database as well as updated and deleted.  All of the units of measure pre-defined in the library are available for use in the editor or in the converter. 

The screen capture below shows the converter:
![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/UOM_Converter.png)

The "Editor ..." button launches the editor (see below).  To convert a unit of measure follow these steps:
*  Select the unit type in the drop-down, e.g. LENGTH.  
*  Enter the amount to convert from, e.g. 1
*  Select the from prefix if desired.  For example, "kilo" is 1000 of the units.
*  Select the from unit of measure, e.g. "m (metre)" in the drop-down.
*  Select the to prefix if desired.  
*  Select the to unit of measure, e.g. "mi (mile)" in the drop-down.
*  Click the "Convert" button.  The converted amount will be displayed below the from amount, e.g. 0.621371192.

The screen capture below shows the unit of measure editor:
![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/UOM_Editor.png) 

To create a unit of measure, click the "New" button and follow these steps:
*  Enter a name, symbol, category (or choose one already defined) and description.
*  Choose the type from the drop-down.  For custom units, choose "UNCLASSIFIED".  Only units of the same type can be converted.
*  If the unit of measure is related to another unit of measure via a conversion, enter the scaling factor (a), abscissa (x) and offset (b).  A prefix (e.g. kilo) may be chosen for the scaling factor.  The conversion will default to the unit of measure itself.
*  For a simple scalar unit, no additional properties are required.
*  For a product or quotient unit of measure, the multiplier/multiplicand or dividend/divisor properties must be entered.  First select the respective unit type (e.g. VOLUME) then the unit of measure.  Click the respective radio button to indicate whether this is product or quotient.
*  For a power unit, the base unit of measure and exponent must be entered.  First select the unit type, then the base unit of measure.  Enter the exponent.
*  Click the "Save" button.  The new unit of measure will appear in the tree view on the left under its category.


To edit a unit of measure, select it in the tree view.  It's properties will be displayed on the right.  Change properties as required, then click the "Save" button.

To refresh the state of the unit that is selected in the tree view from the database, click the "Refresh" button.

To delete a unit of measure, select it in the tree view then click the "Delete" button.


## Project Structure
The Caliper library depends on Java 6+.  The unit tests depend on JUnit (http://junit.org/junit4/), Hamcrest (http://hamcrest.org/), Gson (https://github.com/google/gson) and HTTP Request (https://github.com/kevinsawicki/http-request).  The example application depends on Java 8+ and a JPA implementation (e.g. EclipseLink http://www.eclipse.org/eclipselink/#jpa).

The Caliper library and application, when built with Gradle, has the following structure:
 * `/build/docs/javadoc` - javadoc files for the library
 * `/build/libs` - compiled caliper.jar library
 * `/doc` - documentation
 * `/src/main/java` - java library source files
 * `/src/main/resources` - localizable Message.properties file to define error messages and localizable Unit.properties file to define the unit's name, symbol and description.
 * `/src/test/java` - JUnit test java source files for the library
 * `/src/ui/java` - java source files for JPA persistency and JavaFX 8 user interface for the application
 * `/src/ui/resources` - images and XML files for for JPA persistency
 * `/database` - SQL script files for table and index generation
 
When Caliper is built with Maven, the javadoc and jar files for the base library are in the 'target' folder.

## JSR 363
JSR 363 "proposes to establish safe and useful methods for modeling physical quantities" (https://java.net/downloads/unitsofmeasurement/JSR363Specification_EDR.pdf).  Caliper shares many of the underlying aspects of JSR 363.  In particular:
* UnitOfMeasure class is similar to Unit interface
* Quantity class is similar to Quantity interface
* UnitType enum is similar to Dimension interface
* MeasurementSystem class is similar to SystemOfUnits interface, and also incorporates aspects of ServiceProvider

Caliper however does not use Java generics, and there is only one system of units.  Caliper performs math using BigDecimal amounts whereas JSR 363 uses Numbers.