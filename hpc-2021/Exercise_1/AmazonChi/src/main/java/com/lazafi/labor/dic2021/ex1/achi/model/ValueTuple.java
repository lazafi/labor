package com.lazafi.labor.dic2021.ex1.achi.model;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;


public class ValueTuple<A extends Writable,B extends Writable> implements Writable {
    private A a;
    private B b;

    public ValueTuple() {
        a = (A) new Writable() {
            @Override
            public void write(DataOutput dataOutput) throws IOException {
                this.write(dataOutput);
            }

            @Override
            public void readFields(DataInput dataInput) throws IOException {
                this.readFields(dataInput);
            }
        };
        b = (B) new Writable() {
            @Override
            public void write(DataOutput dataOutput) throws IOException {
                this.write(dataOutput);
            }

            @Override
            public void readFields(DataInput dataInput) throws IOException {
                this.readFields(dataInput);
            }
        };
    }


    public void write(DataOutput out) throws IOException {
       this.a.write(out);
       this.b.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        this.a.readFields(in);
        this.b.readFields(in);
    }

    @Override
    public String toString() {
        return a.toString() + ":" + b.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueTuple that = (ValueTuple) o;
        return Objects.equals(a, that.a) && Objects.equals(b, that.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    public A getA() {
        return a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return b;
    }

    public void setB(B b) {
        this.b = b;
    }
}