package com.lazafi.labor.dic2021.ex1.achi.old;

import com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryStatisticsTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


public class CategoryChiMapper extends Mapper<Text, CategoryStatisticsTuple, Text, TokenChiTuple> {
    private static final Logger log = Logger.getLogger("TokenCategoryMapper");

    // category
    private Text outkey = new Text();
    // token+chi
    private TokenChiTuple outval = new TokenChiTuple();

    private long totalDocuments;

    @Override
    protected void setup(Context context) {
        totalDocuments = Long.parseLong(context.getConfiguration().get(AmazonChiDriver.COUNTERGROUP + AmazonChiDriver.DOCUMENTS.TOTAL.name()));
    }

@Override
public void map(Text key, CategoryStatisticsTuple value, Context context) throws IOException, InterruptedException {
    //String[] fields = value.toString().split("\\s");

    if (key.toString().equals("grandson")) {
        log.info(value.toString());
    }

        outkey.set(value.getCategory());
        outval.setToken(key);
        long N = totalDocuments;
        long A = value.getA().get();
        long P = value.getP().get();
        long M = value.getM().get();
        double chi2 = (N * Math.pow((A*N - M*P), 2))/(P*M*(N-P)*(N-M));
        outval.setChi2(new DoubleWritable(chi2));
        context.write(outkey, outval);
    }
}