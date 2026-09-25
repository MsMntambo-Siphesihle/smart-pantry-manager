package com.richfield.smartpantry.logic;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts quantities into a common base unit so that "1 kg of flour" in the
 * pantry correctly satisfies a recipe that asks for "200 g of flour".
 *
 * Three families are supported: MASS (base gram), VOLUME (base millilitre)
 * and COUNT (base piece). Quantities are only ever compared inside the same
 * family.
 */
public final class UnitConverter {

    public static final String MASS = "MASS";
    public static final String VOLUME = "VOLUME";
    public static final String COUNT = "COUNT";

    private static final Map<String, String> FAMILY = new HashMap<>();
    private static final Map<String, Double> FACTOR = new HashMap<>();

    static {
        // Mass - base unit is the gram
        putUnit("mg", MASS, 0.001);
        putUnit("g", MASS, 1.0);
        putUnit("gram", MASS, 1.0);
        putUnit("grams", MASS, 1.0);
        putUnit("kg", MASS, 1000.0);
        putUnit("oz", MASS, 28.35);
        putUnit("lb", MASS, 453.59);

        // Volume - base unit is the millilitre
        putUnit("ml", VOLUME, 1.0);
        putUnit("l", VOLUME, 1000.0);
        putUnit("litre", VOLUME, 1000.0);
        putUnit("tsp", VOLUME, 5.0);
        putUnit("tbsp", VOLUME, 15.0);
        putUnit("cup", VOLUME, 250.0);

        // Count - base unit is one item
        putUnit("piece", COUNT, 1.0);
        putUnit("pieces", COUNT, 1.0);
        putUnit("pcs", COUNT, 1.0);
        putUnit("unit", COUNT, 1.0);
        putUnit("item", COUNT, 1.0);
        putUnit("", COUNT, 1.0);
    }

    private static void putUnit(String unit, String family, double factor) {
        FAMILY.put(unit, family);
        FACTOR.put(unit, factor);
    }

    private UnitConverter() {
    }

    private static String clean(String unit) {
        return unit == null ? "" : unit.trim().toLowerCase();
    }

    /** Returns MASS, VOLUME or COUNT. Unknown units are treated as COUNT. */
    public static String familyOf(String unit) {
        String family = FAMILY.get(clean(unit));
        return family == null ? COUNT : family;
    }

    /** Converts a quantity to its family's base unit (g, ml or piece). */
    public static double toBase(double quantity, String unit) {
        Double factor = FACTOR.get(clean(unit));
        return factor == null ? quantity : quantity * factor;
    }

    /** True when two units can be meaningfully compared, e.g. g and kg. */
    public static boolean comparable(String unitA, String unitB) {
        return familyOf(unitA).equals(familyOf(unitB));
    }
}
