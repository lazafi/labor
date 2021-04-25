package com.lazafi.labor.dic2021.ex1.achi.old;

import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CategoryChiReducer extends Reducer<Text, TokenChiTuple, Text, Text> {
    private static final Logger log = Logger.getLogger("CategoryChiReducer");

    private static final String DOCUMENT_COUNT = "document.count.";

    Text outkey = new Text();
    Text outval = new Text();

    public static void setDocumentCount(Job job, String key, long count) {
        job.getConfiguration().set(DOCUMENT_COUNT + key, Long.toString(count));
    }

    public static long getDocumentCount(Configuration conf, String key) {
        return Long.parseLong(conf.get(DOCUMENT_COUNT + key));
    }

    /**
     *
     * @param key category
     * @param values tokens with chi
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void reduce(Text key, Iterable<TokenChiTuple> values, Context context
    ) throws IOException, InterruptedException {


        TokenChiTuple last = new TokenChiTuple();

       // String tokens = StreamSupport.stream(values.spliterator(), true)
       //         .map(Object::toString)
       //         .collect(Collectors.joining(" "));
       // List list = new ArrayList();
       // Stream<TokenChiTuple> sorted = StreamSupport.stream(
       //         values.spliterator(), false)
       //         .sorted();

        StringBuilder sb = new StringBuilder();
        values.forEach(v -> {
            sb.append(v.toString());
            sb.append(" ");
        });

        outval.set(sb.toString());

        context.write(key, outval);
    }
}

