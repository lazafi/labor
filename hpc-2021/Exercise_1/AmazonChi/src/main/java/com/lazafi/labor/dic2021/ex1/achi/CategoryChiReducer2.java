package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class CategoryChiReducer2 extends Reducer<CategoryTokenChiTuple, TokenChiTuple, Text, Text> {
    private static final Logger log = Logger.getLogger("CategoryChiReducer");

    Text outkey = new Text();
    Text outval = new Text();

    /**
     *
     * @param key category
     * @param values tokens with chi
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void reduce(CategoryTokenChiTuple key, Iterable<TokenChiTuple> values, Context context
    ) throws IOException, InterruptedException {


        int count = 0;
        StringBuilder sb = new StringBuilder();
        Iterator<TokenChiTuple> itr = values.iterator();
        while(itr.hasNext() && count < 150) {
            sb.append(itr.next().toString());
            sb.append(" ");
            count++;
        }

        outkey.set(key.getCategory());
        outval.set(sb.toString());

        context.write(outkey, outval);
    }
}

