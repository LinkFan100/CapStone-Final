package com.example.capstone.Model;

public class AssociatedPart {

    private int partProductId;
    private int associatedId;
    private String associatedname;
    private double associatedprice;
    private int associatedstock;
    private int associatedmin;
    private int associatedmax;

    public AssociatedPart(int partProductId, int associatedId, String associatedname, double associatedprice, int associatedstock, int associatedmin, int associatedmax) {
        this.partProductId = partProductId;
        this.associatedId = associatedId;
        this.associatedname = associatedname;
        this.associatedprice = associatedprice;
        this.associatedstock = associatedstock;
        this.associatedmin = associatedmin;
        this.associatedmax = associatedmax;
    }

    public int getPartProductId() {
        return partProductId;
    }

    public void setPartProductId(int partProductId) {
        this.partProductId = partProductId;
    }

    public int getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(int associatedId) {
        this.associatedId = associatedId;
    }

    public String getAssociatedname() {
        return associatedname;
    }

    public void setAssociatedname(String associatedname) {
        this.associatedname = associatedname;
    }

    public double getAssociatedprice() {
        return associatedprice;
    }

    public void setAssociatedprice(double associatedprice) {
        this.associatedprice = associatedprice;
    }

    public int getAssociatedstock() {
        return associatedstock;
    }

    public void setAssociatedstock(int associatedstock) {
        this.associatedstock = associatedstock;
    }

    public int getAssociatedmin() {
        return associatedmin;
    }

    public void setAssociatedmin(int associatedmin) {
        this.associatedmin = associatedmin;
    }

    public int getAssociatedmax() {
        return associatedmax;
    }

    public void setAssociatedmax(int associatedmax) {
        this.associatedmax = associatedmax;
    }
}
