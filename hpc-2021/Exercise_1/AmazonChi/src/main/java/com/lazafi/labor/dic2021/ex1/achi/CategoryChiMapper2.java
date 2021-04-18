package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryStatisticsTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.logging.Logger;


public class CategoryChiMapper2 extends Mapper<CategoryTokenChiTuple, NullWritable, CategoryTokenChiTuple, TokenChiTuple> {
    private static final Logger log = Logger.getLogger("TokenCategoryMapper");

    // category
    private CategoryTokenChiTuple outkey = new CategoryTokenChiTuple();
    // token+chi
    private TokenChiTuple outval = new TokenChiTuple();

@Override
public void map(CategoryTokenChiTuple key, NullWritable value, Context context) throws IOException, InterruptedException {

        outkey.setCategory(key.getCategory());
        outkey.setToken(key.getToken());
        outkey.setChi2(key.getChi2());

        outval.setToken(key.getToken());
        outval.setChi2(key.getChi2());

        context.write(outkey, outval);
    }
}