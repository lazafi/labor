package com.lazafi.labor.dic2021.ex1.achi.model;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;


public class CategoryStatisticsTuple implements Writable {
    private Text category;
    private LongWritable A;
    private LongWritable P;
    private LongWritable M;

    public CategoryStatisticsTuple() {
        this.category = new Text();
        A = new LongWritable(0L);
        P = new LongWritable(0L);
        M =  new LongWritable(0L);
    }
    public CategoryStatisticsTuple(String key) {
        super();
        category = new Text(key);
    }

    public CategoryStatisticsTuple(String category, long A, long P, long M) {
        this.category = new Text(category);
        this.A = new LongWritable(A);
        this.P = new LongWritable(P);
        this.M = new LongWritable(M);
    }

    public void write(DataOutput out) throws IOException {
       this.category.write(out);
        this.A.write(out);
        this.P.write(out);
        this.M.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        this.category.readFields(in);
        this.A.readFields(in);
        this.P.readFields(in);
        this.M.readFields(in);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryStatisticsTuple that = (CategoryStatisticsTuple) o;
        return Objects.equals(category, that.category) && Objects.equals(A, that.A) && Objects.equals(P, that.P) && Objects.equals(M, that.M);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, A, P, M);
    }

    @Override
    public String toString() {
        return category.toString() + ":" + A.get() + ":" + P.get() + ":" + M.get();
    }

    public Text getCategory() {
        return category;
    }

    public void setCategory(Text category) {
        this.category = category;
    }

    public LongWritable getA() {
        return A;
    }

    public void setA(LongWritable a) {
        this.A = a;
    }

    public LongWritable getP() {
        return P;
    }

    public void setP(LongWritable p) {
        P = p;
    }

    public LongWritable getM() {
        return M;
    }

    public void setM(LongWritable m) {
        M = m;
    }
}