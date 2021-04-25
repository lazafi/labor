package com.lazafi.labor.dic2021.ex1.achi;

import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.logging.Logger;

public class TokenDictionaryReducer extends Reducer<NullWritable, Text, Text, NullWritable> {
    private static final Logger log = Logger.getLogger("TokenCategoryMapper");

     @Override
     public void reduce(NullWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
         java.util.Set<String> tokens = new TreeSet<>();

         int count = 0;
         for(Text value : values) {
             tokens.add(value.toString());
             count++;
         }
         log.info(String.valueOf(count));

         StringBuilder sb = new StringBuilder();


         context.write(new Text(String.join(" ", tokens)), NullWritable.get());
    }
}

