package com.lazafi.labor.dic2021;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;


public class CategoryTokenChiTuple implements Writable, WritableComparable<CategoryTokenChiTuple> {
    private Text category;
    private Text token;
    private DoubleWritable chi2;

    public CategoryTokenChiTuple() {
        category = new Text();
        token = new Text();
        chi2 = new DoubleWritable(0.0);
    }

    public CategoryTokenChiTuple(String category, String word, double count) {
        this.category = new Text(category);
        this.token = new Text(word);
        this.chi2 = new DoubleWritable(count);
    }

    public void write(DataOutput out) throws IOException {
        this.category.write(out);
        this.token.write(out);
       this.chi2.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        this.category.readFields(in);
        this.token.readFields(in);
        this.chi2.readFields(in);
    }

    @Override
    public String toString() {
        return category.toString() + ":" + token.toString() + ":" + chi2.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryTokenChiTuple that = (CategoryTokenChiTuple) o;
        return Objects.equals(category, that.category) && Objects.equals(token, that.token) && Objects.equals(chi2, that.chi2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, token, chi2);
    }

    public Text getCategory() {
        return category;
    }

    public void setCategory(Text category) {
        this.category = category;
    }

    public Text getToken() {
        return token;
    }

    public void setToken(Text token) {
        this.token = token;
    }

    public DoubleWritable getChi2() {
        return chi2;
    }

    public void setChi2(DoubleWritable chi2) {
        this.chi2 = chi2;
    }

    @Override
    public int compareTo(CategoryTokenChiTuple other) {
        int v = this.category.compareTo(other.getCategory());

        if (v == 0) {
            v = this.chi2.compareTo(other.getChi2());
        }
        return v*(-1);
    }
}