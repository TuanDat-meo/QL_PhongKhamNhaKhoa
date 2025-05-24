package model;

/**
 * A utility class for storing ID-value pairs for use in combo boxes
 */
public class ComboItem {
    private int id;
    private String value;
    
    /**
     * Constructor for creating a new ComboItem
     * 
     * @param id The ID of the item (typically database ID)
     * @param value The display value for the item (shown in the combo box)
     */
    public ComboItem(int id, String value) {
        this.id = id;
        this.value = value;
    }
    
    /**
     * Get the ID of the item
     * 
     * @return The ID value
     */
    public int getId() {
        return id;
    }
    
    /**
     * Set the ID of the item
     * 
     * @param id The ID value to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Get the display value of the item
     * 
     * @return The display value
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Set the display value of the item
     * 
     * @param value The display value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
    
    /**
     * Override toString method to display the value in the combo box
     * 
     * @return The display value
     */
    @Override
    public String toString() {
        return value;
    }
}