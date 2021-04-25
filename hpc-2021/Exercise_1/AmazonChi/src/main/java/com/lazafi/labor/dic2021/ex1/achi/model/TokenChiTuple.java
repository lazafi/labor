package com.lazafi.labor.dic2021.ex1.achi.model;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;


public class TokenChiTuple implements WritableComparable<TokenChiTuple> {
    private Text token;
    private DoubleWritable chi2;

    public TokenChiTuple() {
        token = new Text();
        chi2 = new DoubleWritable(0.0);
    }

    public TokenChiTuple(String word, double count) {
        this.token = new Text(word);
        this.chi2 = new DoubleWritable(count);
    }

    public void write(DataOutput out) throws IOException {
       this.token.write(out);
       this.chi2.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        this.token.readFields(in);
        this.chi2.readFields(in);
    }

    @Override
    public String toString() {
        return token.toString() + ":" + chi2.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenChiTuple that = (TokenChiTuple) o;
        return Objects.equals(token, that.token) && Objects.equals(chi2, that.chi2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, chi2);
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
    public int compareTo(TokenChiTuple other) {
        int v = this.token.compareTo(other.getToken());
        if (v == 0) {
            v = this.chi2.compareTo(other.getChi2());
        }
        return v*(-1);
    }

}