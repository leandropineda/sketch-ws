package com.lpineda.dsketch.api;

public class SketchConfig {
    private Integer rows;
    private Integer cols;
    private Integer prime;

    public void setRows(Integer rows) { this.rows = rows; }
    public Integer getRows() { return rows; }

    public void setCols(Integer cols) { this.cols = cols; }
    public Integer getCols() { return cols; }

    public void setPrime(Integer prime) { this.prime = prime; }
    public Integer getPrime() { return prime; }
}
