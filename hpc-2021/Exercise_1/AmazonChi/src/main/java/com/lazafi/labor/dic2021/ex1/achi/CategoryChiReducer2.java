package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

public class CategoryChiReducer2 extends Reducer<CategoryChiTuple, TokenChiTuple, Text, Text> {
    private static final Logger log = Logger.getLogger("CategoryChiReducer");

    private final int MAXTOKENS = 150;

    Text outkey = new Text();
    Text outval = new Text();

    /**
     * write category wth all tokens and chi values
     * @param key category
     * @param values tokens with chi
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    public void reduce(CategoryChiTuple key, Iterable<TokenChiTuple> values, Context context
    ) throws IOException, InterruptedException {


        int count = 0;
        StringBuilder sb = new StringBuilder();
        Iterator<TokenChiTuple> itr = values.iterator();
        while(itr.hasNext() && count < MAXTOKENS) {
            sb.append(itr.next().toString());
            sb.append(" ");
            count++;
        }

        outkey.set(key.getToken());
        outval.set(sb.toString());

        context.write(outkey, outval);
    }
}

