package com.lazafi.labor.dic2021.ex1.achi.model;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class WordCountTuple implements Writable {
    private Text word;
    private LongWritable count;

    public WordCountTuple() {
        word = new Text();
        count = new LongWritable(0);
    }

    public WordCountTuple(String word, long count) {
        this.word = new Text(word);
        this.count = new LongWritable(count);
    }

    public void write(DataOutput out) throws IOException {
       this.word.write(out);
       this.count.write(out);
    }

    public void readFields(DataInput in) throws IOException {
        this.word.readFields(in);
        this.count.readFields(in);
    }

    @Override
    public String toString() {
        return word.toString() + ":" + count.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordCountTuple that = (WordCountTuple) o;
        return count.get() == that.count.get() && word.toString().equals(that.word.toString());
    }

    public Text getWord() {
        return word;
    }

    public void setWord(Text word) {
        this.word = word;
    }

    public LongWritable getCount() {
        return count;
    }

    public void setCount(LongWritable count) {
        this.count = count;
    }

}