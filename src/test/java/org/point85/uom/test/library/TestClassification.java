/*
MIT License

Copyright (c) 2018 Kent Randall

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
package org.point85.uom.test.library;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestClassification extends BaseTest {
	@Test
	public void testClassifications() throws Exception {	
		UnitType ut = null;
		
		UnitOfMeasure one = sys.getOne();
		UnitOfMeasure s = sys.getSecond();
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		UnitOfMeasure degC = sys.getUOM(Unit.CELSIUS);
		UnitOfMeasure amp = sys.getUOM(Unit.AMPERE);
		UnitOfMeasure mol = sys.getUOM(Unit.MOLE);
		UnitOfMeasure cd = sys.getUOM(Unit.CANDELA);
		
		
		// base types
		assertTrue(one.classify().getUnitType().equals(UnitType.UNITY));
		assertTrue(s.classify().getUnitType().equals(UnitType.TIME));
		assertTrue(m.classify().getUnitType().equals(UnitType.LENGTH));
		assertTrue(kg.classify().getUnitType().equals(UnitType.MASS));
		assertTrue(degC.classify().getUnitType().equals(UnitType.TEMPERATURE));
		assertTrue(amp.classify().getUnitType().equals(UnitType.ELECTRIC_CURRENT));
		assertTrue(mol.classify().getUnitType().equals(UnitType.SUBSTANCE_AMOUNT));
		assertTrue(cd.classify().getUnitType().equals(UnitType.LUMINOSITY));
		assertTrue(sys.getUOM(Unit.US_DOLLAR).classify().getUnitType().equals(UnitType.CURRENCY));
		assertTrue(sys.getUOM(Unit.BIT).classify().getUnitType().equals(UnitType.COMPUTER_SCIENCE));
		
		// area
		UnitOfMeasure uom = sys.getUOM(Unit.FOOT).power(2);
		assertTrue(uom.classify().getUnitType().equals(UnitType.AREA));
		
		// volume
		assertTrue(m.multiply(m).multiply(m).classify().getUnitType().equals(UnitType.VOLUME));
		
		// density
		assertTrue(kg.divide(m.power(3)).classify().getUnitType().equals(UnitType.DENSITY));
		
		// speed
		assertTrue(m.divide(s).classify().getUnitType().equals(UnitType.VELOCITY));
		
		// volumetric flow
		assertTrue(m.power(3).divide(s).classify().getUnitType().equals(UnitType.VOLUMETRIC_FLOW));
		
		// mass flow
		assertTrue(kg.divide(s).classify().getUnitType().equals(UnitType.MASS_FLOW));
		
		// frequency
		assertTrue(one.divide(s).classify().getUnitType().equals(UnitType.FREQUENCY));
		
		// acceleration
		assertTrue(m.divide(s.power(2)).classify().getUnitType().equals(UnitType.ACCELERATION));
		
		// force
		assertTrue(m.multiply(kg).divide(s.power(2)).classify().getUnitType().equals(UnitType.FORCE));
		
		// pressure
		assertTrue(kg.divide(m).divide(s.power(2)).classify().getUnitType().equals(UnitType.PRESSURE));
		
		// energy
		assertTrue(kg.multiply(m).multiply(m).divide(s.power(2)).classify().getUnitType().equals(UnitType.ENERGY));
		
		// power
		assertTrue(kg.multiply(m).multiply(m).divide(s.power(3)).classify().getUnitType().equals(UnitType.POWER));
		
		// electric charge
		assertTrue(s.multiply(amp).classify().getUnitType().equals(UnitType.ELECTRIC_CHARGE));
		
		// electromotive force
		assertTrue(kg.multiply(m.power(2)).divide(amp).divide(s.power(3)).classify().getUnitType().equals(UnitType.ELECTROMOTIVE_FORCE));
		
		// electric resistance
		assertTrue(kg.multiply(m.power(-3)).multiply(amp.power(2)).multiply(s.power(4)).classify().getUnitType().equals(UnitType.ELECTRIC_RESISTANCE));
		
		// electric capacitance
		assertTrue(s.power(-3).multiply(amp.power(-2)).multiply(m.power(2)).divide(kg).classify().getUnitType().equals(UnitType.ELECTRIC_CAPACITANCE));
		
		// electric permittivity				
		assertTrue(s.power(4).multiply(amp.power(2)).multiply(m.power(-3)).divide(kg).classify().getUnitType().equals(UnitType.ELECTRIC_PERMITTIVITY));
		
		// electric field strength
		assertTrue(amp.divide(m).classify().getUnitType().equals(UnitType.ELECTRIC_FIELD_STRENGTH));
		
		// magnetic flux
		assertTrue(kg.divide(amp).divide(s.power(2)).multiply(m.power(2)).classify().getUnitType().equals(UnitType.MAGNETIC_FLUX));		
		
		// magnetic flux density
		assertTrue(kg.divide(amp).divide(s.power(2)).classify().getUnitType().equals(UnitType.MAGNETIC_FLUX_DENSITY));
		
		// inductance
		assertTrue(kg.multiply(amp.power(-2)).divide(s.power(2)).multiply(m.power(2)).classify().getUnitType().equals(UnitType.ELECTRIC_INDUCTANCE));
		
		// conductance
		assertTrue(kg.power(-1).multiply(amp.power(2)).multiply(s.power(3)).multiply(m.power(-2)).classify().getUnitType().equals(UnitType.ELECTRIC_CONDUCTANCE));
		
		// luminous flux
		ut = cd.multiply(one).classify().getUnitType();
		assertTrue(ut.equals(UnitType.LUMINOUS_FLUX) || ut.equals(UnitType.LUMINOSITY));
		
		// illuminance
		assertTrue(cd.divide(m.power(2)).classify().getUnitType().equals(UnitType.ILLUMINANCE));
		
		// radiation dose absorbed and effective
		ut = m.power(2).divide(s.power(2)).classify().getUnitType();
		assertTrue(ut.equals(UnitType.RADIATION_DOSE_ABSORBED) || ut.equals(UnitType.RADIATION_DOSE_EFFECTIVE));
		
		// radiation dose rate
		assertTrue(m.power(2).divide(s.power(3)).classify().getUnitType().equals(UnitType.RADIATION_DOSE_RATE));
		
		// radioactivity
		ut = s.power(-1).classify().getUnitType();
		assertTrue(ut.equals(UnitType.RADIOACTIVITY) || ut.equals(UnitType.FREQUENCY));
		
		// catalytic activity
		assertTrue(mol.divide(s).classify().getUnitType().equals(UnitType.CATALYTIC_ACTIVITY));
		
		// dynamic viscosity
		assertTrue(kg.divide(s).multiply(m).classify().getUnitType().equals(UnitType.DYNAMIC_VISCOSITY));
		
		// kinematic viscosity
		assertTrue(m.power(2).divide(s).classify().getUnitType().equals(UnitType.KINEMATIC_VISCOSITY));
		
		// reciprocal length
		assertTrue(one.divide(m).classify().getUnitType().equals(UnitType.RECIPROCAL_LENGTH));
		
		// plane angle
		assertTrue(UnitType.PLANE_ANGLE.getTypeMap().isEmpty());
		assertTrue(sys.getUOM(Unit.RADIAN).getBaseUnitsOfMeasure().isEmpty());
		
		// solid angle
		assertTrue(UnitType.SOLID_ANGLE.getTypeMap().isEmpty());
		assertTrue(sys.getUOM(Unit.STERADIAN).getBaseUnitsOfMeasure().isEmpty());
		
		// time squared
		assertTrue(s.power(2).classify().getUnitType().equals(UnitType.TIME_SQUARED));
		
		// molar concentration
		assertTrue(mol.divide(m.power(3)).classify().getUnitType().equals(UnitType.MOLAR_CONCENTRATION));
		
		// irradiance
		assertTrue(kg.divide(s.power(3)).classify().getUnitType().equals(UnitType.IRRADIANCE));
		
	}

}
