package org.anonymous.module;

public class Record {
    private int col_id;
    private String col_name;
    private String col_type;
    private String col_type_val;
    private int col_size;
    private int numberOfObjects;
    private double avg_length;

    public Record(int col_id, String col_name, String col_type,String col_type_val, int col_size, int numberOfObjects, double avg_length){
        this.col_id = col_id;
        this.col_name = col_name;
        this.col_type = col_type;
        this.col_type_val = col_type_val;
        this.col_size = col_size;
        this.numberOfObjects = numberOfObjects;
        this.avg_length = avg_length;
    }

    public int getCol_id() {
        return col_id;
    }

    public String getCol_name() {
        return col_name;
    }

    public String getCol_type() {
        return col_type;
    }

    public String getCol_type_val() {
        return col_type_val;
    }

    public int getCol_size() {
        return col_size;
    }

    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    public double getAvg_length() {
        return avg_length;
    }
}
