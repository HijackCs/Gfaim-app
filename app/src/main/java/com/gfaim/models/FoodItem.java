package com.gfaim.models;

public class FoodItem {
    private String name;
    private int quantity;
    private int count;

    public FoodItem(String name, int quantity, int count) {
        this.name = name;
        this.quantity = quantity;
        this.count = count;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCount() { return count; }

    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", count=" + count +
                '}';
    }
}
