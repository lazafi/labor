package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TokenChiReducer2 extends Reducer<Text, Text, CategoryTokenChiTuple, NullWritable> {

    private static final String DOCUMENT_COUNT = "document.count.";

    public static void setDocumentCount(Job job, String key, long count) {
        job.getConfiguration().set(DOCUMENT_COUNT + key, Long.toString(count));
    }

    public static long getDocumentCount(Configuration conf, String key) {
        return Long.parseLong(conf.get(DOCUMENT_COUNT + key));
    }

    private long totalDocuments;

    @Override
    protected void setup(Reducer.Context context) {
        totalDocuments = Long.parseLong(context.getConfiguration().get(AmazonChiDriver.COUNTERGROUP + AmazonChiDriver.DOCUMENTS.TOTAL.name()));
    }

    /**
     * count occurencies of the token in all categories
     * calculate chi2 for the token and all categories
     * emit document, token and chi2 value as key
     * @param key token
     * @param values categories
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void reduce(Text key, Iterable<Text> values, Context context
    ) throws IOException, InterruptedException {

        // count categories in which this token occurs and store values in a hashmap
        Map<String, Long> categories = new LinkedHashMap<String, Long>();

        for (Text val : values) {
            if (categories.containsKey(val.toString())) {
                categories.put(val.toString(), categories.get(val.toString()).longValue() + 1L);
            } else {
                categories.put(val.toString(), 1L);
            }
        }


        // total occurences
        long M = categories.values().stream().reduce(0L, Long::sum);

        for (Map.Entry<String, Long> category: categories.entrySet()) {
            long A = category.getValue();
            //long B = M - A;
            // P = A + C
            long P = getDocumentCount(context.getConfiguration(), category.getKey());
            long N = totalDocuments;
            double chi2 = (N * Math.pow((A*N - M*P), 2))/(P*M*(N-P)*(N-M));
            context.write(new CategoryTokenChiTuple(category.getKey(), key.toString(), chi2), NullWritable.get());
        }
    }
}

