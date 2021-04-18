package com.lazafi.labor.dic2021.ex1.achi.old;

import com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver;
import com.lazafi.labor.dic2021.ex1.achi.model.WordCountTuple;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class CategoryTokenizerMapper extends Mapper<Object, Text, Text, WordCountTuple> {
    private static final Logger log = Logger.getLogger("CategoryTokenizerMapper");

    public static final String RECORDS_COUNTER_NAME = "Records";

    private final static IntWritable ONE = new IntWritable(1);
    private Text category = new Text();
    WordCountTuple result = new WordCountTuple();
    JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    FileSystem fs;
    Path stopwordfile;
    List stopwords = new ArrayList();

    @Override
    protected void setup(Context context) {
        try {
            fs = FileSystem.get(context.getConfiguration());
            log.info(fs.getUri().toString());
            String sfp = context.getConfiguration().get("stopwordfile.path");
            if (sfp != null) {
                stopwords = parseStopwordFile(new Path(sfp));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private List<String> parseStopwordFile(Path file) throws IOException {
        List<String> retval = new ArrayList<String>();
        FSDataInputStream inStream = fs.open(file);
        try {
            String line;
            line = inStream.readLine();
            while (line != null){
                retval.add(line);
                line = inStream.readLine();
            }
            } finally {
                // you should close out the BufferedReader
                inStream.close();
            }
        return retval;
        }


public void map(Object key, Text value, Context context
    ) throws IOException, InterruptedException {
        try {

            JSONObject review = (JSONObject) jsonParser.parse(value.toString());

            String text = review.getAsString("reviewText");
            if (text != null) {
                String[] strArray = text.split("[0-9\\s\\.\\!\\?\\,\\;\\:\\(\\)\\[\\]{}\\-_\"'`~#&*%$\\/\\\\]+");
                Set<String> words = new HashSet<String>();
                for (String w : strArray) {
                    String token = w.toLowerCase();
                    if (
                            !stopwords.contains(token)
                            && !words.contains(token)
                            && token.length() > 1
                    ) {
                        words.add(token);
                    }

                }

                for (String w : words) {
                    result.setWord(new Text(w));
                    result.setCount(new LongWritable(1));
                    category.set(review.getAsString("category"));
                    context.write(category, result);
                }
                context.getCounter(AmazonChiDriver.DOCUMENTS.TOTAL).increment(1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}