package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryStatisticsTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.WordCountTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TokenChiReducer extends Reducer<Text, Text, Text, CategoryStatisticsTuple> {

    private static final String DOCUMENT_COUNT = "document.count.";

    public static void setDocumentCount(Job job, String key, long count) {
        job.getConfiguration().set(DOCUMENT_COUNT + key, Long.toString(count));
    }

    public static long getDocumentCount(Configuration conf, String key) {
        return Long.parseLong(conf.get(DOCUMENT_COUNT + key));
    }

    /**
     *
     * @param key token
     * @param values categories
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void reduce(Text key, Iterable<Text> values, Context context
    ) throws IOException, InterruptedException {
        Map<String, Long> categories = new LinkedHashMap<String, Long>();

        // count categories
        for (Text val : values) {
            if (categories.containsKey(val.toString())) {
                categories.put(val.toString(), categories.get(val.toString()).longValue() + 1L);
            } else {
                categories.put(val.toString(), 1L);
            }
        }

       // long N = getDocumentCount(context.getConfiguration(), "TOTAL");
        long M = categories.values().stream().reduce(0L, Long::sum);

       // for (long val : categories.values()) {
       //     M += val;
       // }


        for (Map.Entry<String, Long> category: categories.entrySet()) {
            long A = category.getValue();
            long P = getDocumentCount(context.getConfiguration(), category.getKey());
           // long P = 0;
            //double chi2 = (N * Math.pow((A*N - M*P), 2))/(P*M*(N-P)*(N-M));
            context.write(key, new CategoryStatisticsTuple(category.getKey(), A, P, M));
        }

        //context.write(key, new Text(categories.toString()));
    }
}

