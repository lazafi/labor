package com.lazafi.labor.dic2021;

import com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver;
import com.lazafi.labor.dic2021.ex1.achi.old.CategoryChiReducer;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.old.CategoryTokenizerMapper;
import com.lazafi.labor.dic2021.ex1.achi.model.WordCountTuple;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertThat;

public class ChiTest {

    @Ignore
    @Test
    public void processesValidRecord() throws IOException, InterruptedException {
        Text value = new Text("{\"reviewerID\": \"AL2DRD3J2Z4EQ\", \"asin\": \"B0002YV80A\", \"reviewerName\": \"C. Green\", \"helpful\": [2, 2], \"reviewText\": \"garden slope\", \"overall\": 4.0, \"summary\": \"garden sprinklers\", \"unixReviewTime\": 1191110400, \"reviewTime\": \"09 30, 2007\", \"category\": \"Patio_Lawn_and_Garde\"}");

        WordCountTuple result1 = new WordCountTuple("garden", 1);
        WordCountTuple result2 = new WordCountTuple("slope", 1);

        new MapDriver<Object, Text, Text, WordCountTuple>()
                .withMapper(new CategoryTokenizerMapper())
                .withInput(new IntWritable(0), value)
                .withOutput(new Text("Patio_Lawn_and_Garde"), result1)
                .withOutput(new Text("Patio_Lawn_and_Garde"), result2)
                .runTest();
    }

    @Ignore
    @Test
    public void recordTokenizer() throws IOException, InterruptedException {
        Text value = new Text("{\"reviewerID\": \"AL2DRD3J2Z4EQ\", \"asin\": \"B0002YV80A\", \"reviewerName\": \"C. Green\", \"helpful\": [2, 2], \"reviewText\": \"garden slope?and!other{one;two}is.word [next]~ein#zwei&polizei*drei%viel$nochein/bier\\\\her\", \"overall\": 4.0, \"summary\": \"garden sprinklers\", \"unixReviewTime\": 1191110400, \"reviewTime\": \"09 30, 2007\", \"category\": \"Patio_Lawn_and_Garde\"}");

        new MapDriver<Object, Text, Text, WordCountTuple>()
                .withMapper(new CategoryTokenizerMapper())
                .withConfiguration(new Configuration())
                .withInput(new IntWritable(0), value)
                .runTest();
    }


    @Ignore
    @Test
    public void returnCategoryChi() throws IOException, InterruptedException {
        List<TokenChiTuple> countList = new ArrayList<>();
        countList.add(new TokenChiTuple("garden", 1.1));
        countList.add(new TokenChiTuple("garden", 1.2));
        countList.add(new TokenChiTuple("slope", 1.3));

        Map<String, Long> result = new LinkedHashMap<String, Long>();
        result.put("garden", 2L);
        result.put("slope", 1L);

        new ReduceDriver<Text, TokenChiTuple, Text, Text>()
                .withReducer(new CategoryChiReducer())
                .withInput(new Text("Patio_Lawn_and_Garde"), countList)
                .withOutput(new Text("Patio_Lawn_and_Garde"), new Text(result.toString()))
                .runTest();
    }

    //@Ignore
    @Test
    public void test() throws Exception {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "file:///");
        conf.set("mapreduce.framework.name", "local");
        conf.setInt("mapreduce.task.io.sort.mb", 1);
        conf.set("staging1.path", "target/tmp");
        //conf.set("stopwordfile.path", "src/resources/stopwords.txt");
        Path input = new Path("input");
        Path staging = new Path("target/o");
        Path output = new Path("target/out");
        Path stopwords = new Path("src/resources/stopwords.txt");
        FileSystem fs = FileSystem.getLocal(conf);
        fs.delete(staging, true); // delete old output
        AmazonChiDriver driver = new AmazonChiDriver();
        driver.setConf(conf);
        int exitCode = driver.run(new String[]{input.toString(), staging.toString(), output.toString(), stopwords.toString()});
    }



}