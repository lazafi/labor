package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.logging.Logger;


public class CategoryChiMapper2 extends Mapper<CategoryTokenChiTuple, NullWritable, CategoryChiTuple, TokenChiTuple> {
    private static final Logger log = Logger.getLogger("TokenCategoryMapper");

    // category
    private CategoryChiTuple outkey = new CategoryChiTuple();
    // token+chi
    private TokenChiTuple outval = new TokenChiTuple();

@Override
public void map(CategoryTokenChiTuple key, NullWritable value, Context context) throws IOException, InterruptedException {

        outkey.setToken(key.getCategory());
        outkey.setChi2(key.getChi2());

        outval.setToken(key.getToken());
        outval.setChi2(key.getChi2());

        context.write(outkey, outval);
    }
}