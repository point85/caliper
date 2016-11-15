# caliper
Caliper is a project for managing units of measure and the conversions between them.  It is designed to be simple to use, yet comprehensive.  It includes units of measure commonly found
in science, engineering, technology and in the household.  These recognized sytems of measurement include International System of Units (SI), International Customary, United States and British Imperial.  
Custom units of measure can also be created in the Caliper unified measurement system.  Custom units are specific to a trade or industry such as packaging where units of can, bottles, cases and pallets might be in use.

 A Caliper measurement system is a collection of units of measure where each pair has a linear relationship, i.e. y = ax + b where x is the abscissa unit to be converted, y (the ordinate) is the converted unit, 
 a is the factor and b is the offset.  In the absence of a defined conversion, a unit will always have  a conversion to itself.  A special bridge unit conversion is defined to convert between the SI and International customary units 
 of mass (kilogram to pound mass), length (metre to foot) and temperature (Kelvin to Rankine).  A custom unit can define any bridge conversion such as a bottle to US fluid ounces.
 
  ## Concepts
 The diagram below illustrates these concepts.
 ![Caliper Diagram](https://github.com/point85/caliper/blob/master/doc/CaliperDiagram.png)
 
All units are owned by the unified measurement system. A measurement service provides access to the measurement system and the units that it owns.  
Units x and y belong to a relational system (such as SI or International Customary).  So too do units w and z.  Unit y has a linear conversion to unit x, therefore x must be defined before y can be defined.  Unit w has a converison to
unit z.  Unit x has a bridge conversion defined to unit z (for example a foot to a metre).  A bridge conversion from z to x is not necessary since it is the inverse of the conversion from x to z.
 
 A simple unit, for example a metre, is defined as a ScalarUOM.  A special scalar unit of measure is unity or "1".  A unit of measure that is the product of two other units is defined as a ProductUOM.  An example is a Joule which is a Newton·metre.  
 A unit of measure that is the quotient of two other units is defined as a QuotientUOM. An example is a velocity, e.g. metre/second.  A unit of measure that has an exponent on a base unit is defined as a PowerUOM. 
 An example is area in metre\u00B2. Note that an exponent of 0 is unity, and an exponent of 1 is the base  unit itself. An expont of 2 is a product unit where the multiplier and multiplicand are the base unit.  
 A power of -1 is a quotient unit of measure where the dividend is 1 and the divisor is the base unit.  Units are classfied by type, e.g. length, mass, time, temperature, etc..  
 They are also enumerated, e.g. kilogram, Newton, metre, etc.  Custom units (e.g. a 1 litre bottle) do not have a pre-defined type or enumeration.
 
 All units have a base symbol that is the most reduced form of the unit.  For example, a Newton is a kilogram·metre/second\u00B2.  The base symbol is used by the measurement system to register each unit and to recognize the result 
 of arithmetic operations on quantitites.  A quantity is an amount (as a BigDecimal for precision and scaling) along with a unit of measure.  When arithmetic operations are performed on quanties, the original units are transformed.  
 For example, multiplying a length in metres by a force in Newtons results in a quantity of energy in Joules.
 
## Implementation
The UML class diagram below is the java implementation of the conceptual design described above.  

 ![Caliper UML](https://github.com/point85/caliper/blob/master/doc/CaliperUML.png)
 
Class Symbolic is the abstract base class for both AbstractUnitOfMeasure and MeasurementSystem.  Symbolic has attributes of name, symbol and description.  
AbstractUnitOfMeasure implements the UnitOfMeasure interface and thus has most of the functionality for a unit of measure.
It has attributes of the owning MeasurementSystem, base symbol and the Unified Code for Units Of Measurement symbol [UCUM](http://unitsofmeasure.org/ucum.html).  
Four concrete classes extend AbstractUnitOfMeasure:
* ScalarUOM for simple units of measure
* ProductUOM for product units
* QuotientUOM for quotient units
* PowerUOM for power units

Class MeasurementSystem has attributes of the unit of measure symbol cache. It provides methods to instantiate the concrete classes.

Single class MeasurementService provides a method for getting the unified MeasurementSystem.

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