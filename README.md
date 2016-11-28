# Caliper
The Caliper project manages units of measure and conversions between them.  Caliper is designed to be simple to use, yet comprehensive.  It includes a large number of pre-defined units of measure commonly found in science, engineering, technology and the household.  These recognized systems of measurement include the International System of Units (SI), International Customary, United States and British Imperial.  Custom units of measure can also be created in the Caliper unified measurement system.  Custom units are specific to a trade or industry such as industrial packaging where units of can, bottle, case and pallet are typical.  

A Caliper measurement system is a collection of units of measure where each pair has a linear relationship, i.e. y = ax + b where x is the abscissa unit to be converted, y (the ordinate) is the converted unit, a is the scaling factor and b is the offset.  In the absence of a defined conversion, a unit will always have a conversion to itself.  A special bridge unit conversion is defined to convert between the fundamental SI and International customary units of mass (i.e. kilogram to pound mass), length (i.e. metre to foot) and temperature (i.e. Kelvin to Rankine).  A custom unit can define any bridge conversion such as a bottle to US fluid ounces.
 
## Concepts

The diagram below illustrates these concepts.
![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/CaliperDiagram.png)
 
All units are owned by the unified measurement system. Units x and y belong to a relational system (such as SI or International Customary).  So too do units w and z.  Unit y has a linear conversion to unit x; therefore x must be defined before y can be defined.  Unit x is also related to y by x = (y - b)/a.  Unit w has a converison to
unit z.  Unit x has a bridge conversion defined to unit z (for example a foot to a metre).  A bridge conversion from z to x is not necessary since it is the inverse of the conversion from x to z.
 
*Scalar Unit* 
A simple unit, for example a metre, is defined as a ScalarUOM.  A special scalar unit of measure is unity or "1".  

*Product Unit*
A unit of measure that is the product of two other units is defined as a ProductUOM.  An example is a Joule which is a Newton·metre.  

*Quotient Unit*
A unit of measure that is the quotient of two other units is defined as a QuotientUOM. An example is a velocity, e.g. metre/second.  

*Power Unit*
A unit of measure that has an exponent on a base unit is defined as a PowerUOM. An example is area in metre^2. Note that an exponent of 0 is unity, and an exponent of 1 is the base unit itself. An exponent of 2 is a product unit where the multiplier and multiplicand are the base unit.  A power of -1 is a quotient unit of measure where the dividend is 1 and the divisor is the base unit.  

Units are classfied by type, e.g. length, mass, time, temperature, etc..  
They are also enumerated, e.g. kilogram, Newton, metre, etc.  Custom units (e.g. a 1 litre bottle) do not have a pre-defined type or enumeration.
 
All units have a base symbol that is the most reduced form of the unit.  For example, a Newton is kilogram·metre/second^2.  The base symbol is used in the measurement system to register each unit and to discern the result of arithmetic operations on quantitites.  For example, dividing a quantity of Newton-metres by a quantity of metres results in a quantity of Newtons. 

A quantity is an amount (implemented as a BigDecimal for precision and scaling) together with a unit of measure.  When arithmetic operations are performed on quantities, the original units are transformed.  
For example, multiplying a length in metres by a force in Newtons results in a quantity of energy in Joules.
 
## Code Examples
The unified MeasurementSystem is obtained by calling:
```java
MeasurementSystem sys = MeasurementSystem.getUnifiedSystem();
```

The metre ScalarUOM is created by the MeasurementSystem as follows:
```java
UnitOfMeasure uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, symbols.getString("m.name"),
					symbols.getString("m.symbol"), symbols.getString("m.desc"), symbols.getString("m.unified"));
``` 

The square metre PowerUOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, symbols.getString("m2.name"),
					symbols.getString("m2.symbol"), symbols.getString("m2.desc"), symbols.getString("m2.unified"),
					getUOM(Unit.METRE), 2);
```

The metre per second QuotientUOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SECOND, symbols.getString("mps.name"),
					symbols.getString("mps.symbol"), symbols.getString("mps.desc"), symbols.getString("mps.unified"),
					getUOM(Unit.METRE), getSecond());
```

The Newton ProductUOM is created by the MeasurementSystem as follows: 
```java
UnitOfMeasure uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, symbols.getString("newton.name"),
					symbols.getString("newton.symbol"), symbols.getString("newton.desc"),
					symbols.getString("newton.unified"), getUOM(Unit.KILOGRAM), getUOM(Unit.METRE_PER_SECOND_SQUARED));
```

A millisecond is 1/1000th of a second.  In this case, the unit is defined as:

```java
public static final BigDecimal MILLI = Quantity.createAmount("0.001");
		Conversion conversion = new Conversion(MILLI, getUOM(Unit.SECOND));
		UnitOfMeasure	uom = createScalarUOM(UnitType.TIME, Unit.MILLISECOND, symbols.getString("ms.name"),
					symbols.getString("ms.symbol"), symbols.getString("ms.desc"), symbols.getString("ms.unified"));
			uom.setConversion(conversion);
```

For a second example, a US gallon = 231 cubic inches:
```java			
		Conversion	conversion = new Conversion(Quantity.createAmount("231"), getUOM(Unit.CUBIC_INCH));
		UnitOfMeasure	uom = createScalarUOM(UnitType.VOLUME, Unit.US_GALLON, symbols.getString("us_gallon.name"),
					symbols.getString("us_gallon.symbol"), symbols.getString("us_gallon.desc"),
					symbols.getString("us_gallon.unified"));
			uom.setConversion(conversion);
```

When creating the foot unit of measure in the unified measurement system, a bridge conversion to metre is defined (1 foot = 0.3048m):
```java
UnitOfMeasure 	uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, symbols.getString("foot.name"),
					symbols.getString("foot.symbol"), symbols.getString("foot.desc"),
					symbols.getString("foot.unified"));

			// bridge to SI
			conversion = new Conversion(Quantity.createAmount("0.3048"), getUOM(Unit.METRE));
			uom.setBridge(conversion);
```

Custom units and conversions can also be created:
```java

MeasurementSystem sys = MeasurementSystem.getUnifiedSystem();

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

Quantities can be added and subtracted:
```java
MeasurementSystem sys = MeasurementSystem.getUnifiedSystem();

		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Unit.CENTIMETRE);
		
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

# Header 1
## Header 2 in bold
**_Header 2 Italic_**
### Header 3

*italic text* 
**bold text**

unordered list
* Item 1
* Item 2
  * Item 2a
  * Item 2b

ordered list
1. Item 1
2. Item 2
3. Item 3
   * Item 3a
   * Item 3b

Images 

If you want to embed images, this is how you do it:


![GitHub Logo](/images/logo.png)
Format: ![Alt Text](url)
   
[GitHub](http://github.com)

Inline code
I think you should use an `<addr>` element here instead.
 * `/core` – Fully functional HTTP(s) server consisting of one (1) Java file, ready to be customized/inherited for your own project.


```java
    package com.example;
    
    import java.io.IOException;
    import java.util.Map;
```

or indent four spaces
    function fancyAlert(arg) {
      if(arg) {
        $.facebox({div:'#foo'})
      }
    }

Task lists
- [x] @mentions, #refs, [links](), **formatting**, and <del>tags</del> supported
- [x] list syntax required (any unordered or ordered list supported)
- [x] this is a complete item
- [ ] this is an incomplete item

Table

First Header | Second Header
------------ | -------------
Content from cell 1 | Content from cell 2
Content in the first column | Content in the second column

It is being developed at Github and uses Apache Maven for builds & unit testing:

 * Build status: [![Build Status](https://api.travis-ci.org/NanoHttpd/nanohttpd.png)](https://travis-ci.org/NanoHttpd/nanohttpd)
 * Coverage Status: [![Coverage Status](https://coveralls.io/repos/NanoHttpd/nanohttpd/badge.svg)](https://coveralls.io/r/NanoHttpd/nanohttpd)
 * Current central released version: [![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nanohttpd/nanohttpd/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nanohttpd/nanohttpd)

 horizontal line
-----