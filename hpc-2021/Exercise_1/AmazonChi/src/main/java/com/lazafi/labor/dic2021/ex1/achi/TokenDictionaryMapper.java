package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Logger;


public class TokenDictionaryMapper extends Mapper<Object, Text, NullWritable, Text> {
    private static final Logger log = Logger.getLogger("TokenDictionaryMapper");

    // category
    private Text outval = new Text();
    // token+chi

@Override
public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

      StringTokenizer stringTokenizer = new StringTokenizer(value.toString());

      String cat = stringTokenizer.nextToken();

       while (stringTokenizer.hasMoreTokens()) {
           String token = stringTokenizer.nextToken();
           String[] comps = token.split(":");
           outval.set(comps[0]);
           context.write(NullWritable.get(), outval);
       }
   }
}