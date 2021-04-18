package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.WordCountTuple;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentCountMapper extends Mapper<Object, Text, Text, LongWritable> {

    private final static LongWritable ONE = new LongWritable(1);
    private Text outkey = new Text();
    JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        try {
            JSONObject review = (JSONObject) jsonParser.parse(value.toString());
            String category = review.getAsString("category");
            outkey.set(category);
            context.write(outkey, ONE);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }
}