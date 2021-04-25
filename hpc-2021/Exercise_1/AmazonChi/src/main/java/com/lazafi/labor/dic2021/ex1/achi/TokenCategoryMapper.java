package com.lazafi.labor.dic2021.ex1.achi;

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

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class TokenCategoryMapper extends Mapper<Object, Text, Text, Text> {
    private static final Logger log = Logger.getLogger("TokenCategoryMapper");
    private final static String DELIMITERS = " \t0123456789.!?,;:()[]{}-_\"'`~#&*%$\\/";

    // token
    private Text outkey = new Text();
    // category
    private Text outval = new Text();

    JSONParser jsonParser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
    FileSystem fs;
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
            String category = review.getAsString("category");
            if (text != null) {
                StringTokenizer st = new StringTokenizer(text, DELIMITERS);
                //String[] strArray = text.split("[0-9\\s\\.\\!\\?\\,\\;\\:\\(\\)\\[\\]{}\\-_\"'`~#&*%$\\/\\\\]+");
                Set<String> words = new HashSet<String>();
                while (st.hasMoreTokens()) {
                    // lowercase
                    String token = st.nextToken().toLowerCase();
                    // filter stopwords and tokens <= 1
                    if (
                            !stopwords.contains(token)
                            && !words.contains(token)
                            && token.length() > 1
                    ) {
                        words.add(token);
                        outkey.set(token);
                        outval.set(category);
                        context.write(outkey, outval);
                    }

                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}