package com.richfield.smartpantry.model;

/**
 * A single ingredient the user currently has at home.
 * Mirrors one row of the "pantry_items" table in the SQLite database.
 */
public class PantryItem {

    private long id;
    private String name;            // what the user typed, e.g. "Tomatoes"
    private String normalisedName;  // matching key, e.g. "tomato"
    private double quantity;
    private String unit;            // g, kg, ml, l, tsp, tbsp, cup, piece
    private String expiryDate;      // ISO yyyy-MM-dd, may be null

    public PantryItem() {
    }

    public PantryItem(long id, String name, String normalisedName,
                      double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.normalisedName = normalisedName;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNormalisedName() { return normalisedName; }
    public void setNormalisedName(String normalisedName) { this.normalisedName = normalisedName; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    /** Human readable amount used by the list adapter, e.g. "2.0 kg". */
    public String getDisplayQuantity() {
        String qty = (quantity == Math.floor(quantity))
                ? String.valueOf((long) quantity)
                : String.valueOf(quantity);
        return qty + " " + unit;
    }
}
