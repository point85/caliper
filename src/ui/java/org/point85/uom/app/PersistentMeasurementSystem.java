/*
MIT License

Copyright (c) 2017 Kent Randall

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

package org.point85.uom.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.point85.uom.MeasurementSystem;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitOfMeasure.MeasurementType;
import org.point85.uom.UnitType;

// class to manage saving UOMs to a database using JPA
public class PersistentMeasurementSystem extends MeasurementSystem {
	// JPA persistence unit name
	private static final String PERSISTENCE_UNIT = "UOM";

	// named queries
	private static final String NQ_UOM_CATEGORIES = "UOM.Categories";
	private static final String NQ_UOM_SYMBOLS = "UOM.Symbols";
	private static final String NQ_UOM_SYMBOLS_IN_CATEGORY = "UOM.SymbolsInCategory";
	private static final String NQ_UOM_BY_SYMBOL = "UOM.BySymbol";
	private static final String NQ_UOM_BY_UNIT = "UOM.ByUnit";
	private static final String NQ_UOM_BY_KEY = "UOM.ByKey";
	private static final String NQ_UOM_CROSS_REF = "UOM.CrossRef";

	// standard unified system
	private static PersistentMeasurementSystem persistentSystem = new PersistentMeasurementSystem();

	// entity manager factory
	private EntityManagerFactory emf;

	// entity manager
	private EntityManager em;

	private PersistentMeasurementSystem() {
		super();
	}

	// get the EntityManager, create if necessary
	private EntityManager getEntityManager() {
		if (em == null) {
			emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
			em = emf.createEntityManager();
		}
		return em;
	}

	/**
	 * Singleton
	 * 
	 * @return PersistentMeasurementSystem
	 */
	public static PersistentMeasurementSystem getSystem() {
		return persistentSystem;
	}

	// query for UOM based on its unique symbol
	public UnitOfMeasure fetchUOMBySymbol(String symbol, boolean cascade) throws Exception {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("symbol", symbol);

		// query based on symbol
		UnitOfMeasure uom = fetchUnitOfMeasure(PersistentMeasurementSystem.NQ_UOM_BY_SYMBOL, parameters);

		if (uom != null && cascade) {
			// bring referenced UOMs into the persistence context
			fetchReferencedUnits(uom);
		}

		return uom;
	}

	// fetch recursively all referenced units to make them managed in the
	// persistence unit
	public void fetchReferencedUnits(UnitOfMeasure uom) throws Exception {
		String id = null;
		UnitOfMeasure referenced = null;
		UnitOfMeasure fetched = null;

		// abscissa unit
		referenced = uom.getAbscissaUnit();
		if (referenced != null && !uom.isTerminal()) {
			id = referenced.getSymbol();
			fetched = fetchUOMBySymbol(id, true);

			if (fetched != null) {
				// already in database
				uom.setAbscissaUnit(fetched);
			}

			// units referenced by the abscissa
			fetchReferencedUnits(referenced);
		}

		// bridge abscissa unit
		referenced = uom.getBridgeAbscissaUnit();
		if (referenced != null) {
			id = referenced.getSymbol();
			fetched = fetchUOMBySymbol(id, true);

			if (fetched != null) {
				// already in database
				uom.setBridgeConversion(uom.getBridgeScalingFactor(), fetched, uom.getBridgeOffset());
			}
		}

		// UOM1 and UOM2
		if (uom.getMeasurementType().equals(MeasurementType.PRODUCT)) {
			// multiplier
			UnitOfMeasure uom1 = uom.getMultiplier();
			id = uom1.getSymbol();
			fetched = fetchUOMBySymbol(id, true);

			if (fetched != null) {
				uom1 = fetched;
			}

			// multiplicand
			UnitOfMeasure uom2 = uom.getMultiplicand();
			id = uom2.getSymbol();
			UnitOfMeasure fetched2 = fetchUOMBySymbol(id, true);

			if (fetched2 != null) {
				uom2 = fetched2;
			}

			uom.setProductUnits(uom1, uom2);

			// units referenced by UOM1 & 2
			fetchReferencedUnits(uom1);
			fetchReferencedUnits(uom2);

		} else if (uom.getMeasurementType().equals(MeasurementType.QUOTIENT)) {
			// dividend
			UnitOfMeasure uom1 = uom.getDividend();
			id = uom1.getSymbol();
			fetched = fetchUOMBySymbol(id, true);

			if (fetched != null) {
				uom1 = fetched;
			}

			// divisor
			UnitOfMeasure uom2 = uom.getDivisor();
			id = uom2.getSymbol();
			UnitOfMeasure fetched2 = fetchUOMBySymbol(id, true);

			if (fetched2 != null) {
				uom2 = fetched2;
			}

			uom.setQuotientUnits(uom1, uom2);

			// units referenced by UOM1 & 2
			fetchReferencedUnits(uom1);
			fetchReferencedUnits(uom2);

		} else if (uom.getMeasurementType().equals(MeasurementType.POWER)) {
			referenced = uom.getPowerBase();
			id = referenced.getSymbol();
			fetched = fetchUOMBySymbol(id, true);

			if (fetched != null) {
				// already in database
				uom.setPowerUnit(fetched, uom.getPowerExponent());
			}

			// units referenced by the power base
			fetchReferencedUnits(referenced);
		}
	}

	// fetch UOM by its enumeration
	public UnitOfMeasure fetchUOMByUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = null;

		if (unit == null) {
			return uom;
		}

		// check in the database
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("unit", unit);

		// fetch by Unit enum
		Object result = fetchUnitOfMeasure(NQ_UOM_BY_UNIT, parameters);

		if (result != null) {
			uom = (UnitOfMeasure) result;
			fetchReferencedUnits(uom);

		} else {
			// not in db, get from pre-defined units
			uom = getUOM(unit);

			// fetch units that it references
			fetchReferencedUnits(uom);
		}

		return uom;
	}

	// fetch UOM by its primary key
	UnitOfMeasure fetchUOMByKey(Integer key) throws Exception {

		Map<String, Object> parameters = new HashMap<>();
		parameters.put("key", key);

		UnitOfMeasure result = fetchUnitOfMeasure(NQ_UOM_BY_KEY, parameters);

		return result;
	}

	// execute the named query
	List<?> executeNamedQuery(String queryName, Map<String, Object> parameters) {
		Query query = getEntityManager().createNamedQuery(queryName);

		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query.getResultList();
	}

	// fetch UOM by a named query
	UnitOfMeasure fetchUnitOfMeasure(String queryName, Map<String, Object> parameters) throws Exception {
		UnitOfMeasure uom = null;

		Query query = getEntityManager().createNamedQuery(queryName);

		if (parameters != null) {
			for (Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}

		// the UOM might not be in the database
		try {
			uom = (UnitOfMeasure) query.getSingleResult();
		} catch (Exception e) {
			// ignore
		}
		return uom;
	}

	// remove the UOM from the persistence context
	void evictUOM(UnitOfMeasure entity) {
		getEntityManager().detach(entity);
	}

	// save the UOM to the database
	public void saveUOM(UnitOfMeasure entity) throws Exception {
		try {
			// start transaction
			getEntityManager().getTransaction().begin();

			// merge this entity into the PU
			getEntityManager().merge(entity);

			// commit transaction
			getEntityManager().getTransaction().commit();
		} catch (Throwable t) {
			// roll back transaction
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().rollback();
			}
			throw new Exception(t.getMessage());
		}
	}

	// delete the UOM from the database
	public void deleteUOM(UnitOfMeasure entity) throws Exception {
		if (entity == null) {
			return;
		}

		// check for cross references
		List<UnitOfMeasure> uoms = getCrossReferences(entity);

		if (uoms.size() != 0) {
			List<String> displayStrings = new ArrayList<>(uoms.size());

			for (UnitOfMeasure uom : uoms) {
				displayStrings.add(BaseController.toDisplayString(uom));
			}

			throw new Exception("Unit of measure " + entity.getName()
					+ " cannot be deleted.  It is being referenced by UOM(s) " + displayStrings + ".");
		}

		try {
			// start transaction
			getEntityManager().getTransaction().begin();

			// delete
			getEntityManager().remove(getEntityManager().merge(entity));

			// commit transaction
			getEntityManager().getTransaction().commit();
		} catch (Throwable t) {
			// roll back transaction
			if (getEntityManager().getTransaction().isActive()) {
				getEntityManager().getTransaction().rollback();
			}
			throw new Exception(t.getMessage());
		}
	}

	// get any foreign UOM references
	List<UnitOfMeasure> getCrossReferences(UnitOfMeasure uom) throws Exception {
		Integer key = uom.getKey();

		Query query = getEntityManager().createNamedQuery(NQ_UOM_CROSS_REF);
		query.setParameter(1, key);

		@SuppressWarnings("unchecked")
		List<Integer> keys = (List<Integer>) query.getResultList();

		List<UnitOfMeasure> referencedUOMs = new ArrayList<>(keys.size());

		// get the referenced UOMs
		for (Integer primaryKey : keys) {
			UnitOfMeasure referenced = fetchUOMByKey(primaryKey);
			referencedUOMs.add(referenced);
		}

		return referencedUOMs;
	}

	// fetch all defined categories
	List<String> fetchCategories() {
		@SuppressWarnings("unchecked")
		List<String> categories = (List<String>) executeNamedQuery(PersistentMeasurementSystem.NQ_UOM_CATEGORIES, null);
		Collections.sort(categories);
		return categories;
	}

	// fetch symbols and their names for this UOM type
	List<Object[]> fetchSymbolsAndNames(UnitType unitType) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("type", unitType);

		@SuppressWarnings("unchecked")
		List<Object[]> values = (List<Object[]>) executeNamedQuery(PersistentMeasurementSystem.NQ_UOM_SYMBOLS,
				parameters);

		return values;
	}

	// get symbols and names in this category
	List<Object[]> getSymbolsAndNames(String category) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("category", category);

		@SuppressWarnings("unchecked")
		List<Object[]> values = (List<Object[]>) executeNamedQuery(
				PersistentMeasurementSystem.NQ_UOM_SYMBOLS_IN_CATEGORY, parameters);

		return values;
	}

	@Override
	public UnitOfMeasure getSecond() throws Exception {
		// first look in the database
		UnitOfMeasure uom = fetchUOMByUnit(Unit.SECOND);

		if (uom == null) {
			uom = super.getSecond();
		}
		return uom;
	}
}
