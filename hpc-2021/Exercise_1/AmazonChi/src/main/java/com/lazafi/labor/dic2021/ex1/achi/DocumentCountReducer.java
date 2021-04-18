package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.WordCountTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DocumentCountReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

     public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {

         int sum = 0;
         for (LongWritable val : values) {
             sum += val.get();
         }

         context.getCounter(AmazonChiDriver.COUNTERGROUP, key.toString()).setValue(sum);
         context.getCounter(AmazonChiDriver.COUNTERGROUP, AmazonChiDriver.DOCUMENTS.TOTAL.name()).increment(sum);

         //context.write(key, NullWritable.get());
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {

    }
}

