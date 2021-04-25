package com.lazafi.labor.dic2021.ex1.achi;


import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.old.CategoryChiMapper;
import com.lazafi.labor.dic2021.ex1.achi.old.CategoryChiReducer;
import com.lazafi.labor.dic2021.ex1.achi.util.HadoopUtils;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.Iterator;

public class AmazonChiDriver extends Configured implements Tool {

    public static final String COUNTERGROUP = "DocumentCounter";

    public enum DOCUMENTS {
        TOTAL
    }

    public int run(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.printf("Usage: %s [generic options] <input> <stage> <output> <stopwords>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        String inputDir = args[0];
        String stageDir = args[1];
        String outputDir = args[2];
        String stopwordpath = args[3];

        Path input = new Path(inputDir);
        Path stage1 = new Path(stageDir + "/1");
        Path stage2 = new Path(stageDir + "/2");
        Path stage3 = new Path(stageDir + "/3");
        Path stage4 = new Path(stageDir + "/4");
        Path output = new Path(outputDir);

        // clean output dirs
        FileSystem fs = FileSystem.get(getConf());
        fs.delete(stage1, true);
        fs.delete(stage2, true);
        fs.delete(stage3, true);
        fs.delete(stage4, true);

        int retcode = 1;

        // stage 0
        // count document occurencies
        // use counters for output (NullOutput)
        Job counterJob = Job.getInstance(getConf(), "Document Counter");
        counterJob.setJarByClass(getClass());
        counterJob.setMapperClass(DocumentCountMapper.class);
        counterJob.setReducerClass(DocumentCountReducer.class);

        counterJob.setOutputKeyClass(Text.class);
        counterJob.setOutputValueClass(LongWritable.class);

        counterJob.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(counterJob, input);

        // no output
        counterJob.setOutputFormatClass(NullOutputFormat.class);

        if (counterJob.waitForCompletion(false)) {
            // stage 1
            // TokenizerJob
            // compute chi2 for all token - category combinations
            // Map -> tokenize review and omit token - category
            // Redurce -> count category occurences for given token and compute chi2

            getConf().set("stopwordfile.path", stopwordpath);
            Job tokenizerJob = Job.getInstance(getConf(), "Review Tokenizer");

            tokenizerJob.setJarByClass(getClass());
            tokenizerJob.setMapperClass(TokenCategoryMapper.class);

            // user document counters from stage 0
            // set document counters in conf
            Iterator<Counter> cnts = counterJob.getCounters().getGroup(COUNTERGROUP).iterator();
            while (cnts.hasNext()) {
                Counter counter = cnts.next();
                TokenChiReducer.setDocumentCount(tokenizerJob, counter.getName(), counter.getValue());
            }
            // set total document counter
            long totalDocuments = counterJob.getCounters().findCounter(COUNTERGROUP, DOCUMENTS.TOTAL.name()).getValue();
            tokenizerJob.getConfiguration().set(COUNTERGROUP + DOCUMENTS.TOTAL.name(), Long.toString(totalDocuments));

            tokenizerJob.setReducerClass(TokenChiReducer2.class);
            tokenizerJob.setMapOutputKeyClass(Text.class);
            tokenizerJob.setMapOutputValueClass(Text.class);

            // use sequenceFileFormat to output Category, Token, CHI2 values as keys, Null as value
            tokenizerJob.setOutputKeyClass(CategoryTokenChiTuple.class);
            tokenizerJob.setOutputValueClass(NullWritable.class);
            tokenizerJob.setOutputFormatClass(SequenceFileOutputFormat.class);

            FileInputFormat.addInputPath(tokenizerJob, input);
            FileOutputFormat.setOutputPath(tokenizerJob, stage1);

            //tokenizerJob.setNumReduceTasks(100);

            if (tokenizerJob.waitForCompletion(false)) {
                // stage 2
                // use secondary sort to sort tokens by chi2 value

                getConf().set("mapreduce.output.textoutputformat.separator", " ");
                Job sortJob = Job.getInstance(getConf(), "Chi Sorter");
                sortJob.setJarByClass(getClass());

                sortJob.setMapperClass(CategoryChiMapper2.class);
                sortJob.setReducerClass(CategoryChiReducer2.class);
                sortJob.setPartitionerClass(CategoryChiPartitioner.class);
                sortJob.setGroupingComparatorClass(CategoryChiGroupingComparator.class);

                sortJob.setMapOutputKeyClass(CategoryChiTuple.class);
                sortJob.setMapOutputValueClass(TokenChiTuple.class);
                sortJob.setOutputKeyClass(Text.class);
                sortJob.setOutputValueClass(Text.class);
                sortJob.setInputFormatClass(SequenceFileInputFormat.class);
                sortJob.setOutputFormatClass(TextOutputFormat.class);

                FileInputFormat.addInputPath(sortJob, stage1);
                FileOutputFormat.setOutputPath(sortJob, stage2);

                //sortJob.setNumReduceTasks(1);

                if (sortJob.waitForCompletion(true)) {
                    // stage 3
                    // collect all tokens and sort them

                    getConf().set("mapreduce.output.textoutputformat.separator", " ");

                    Job dictonaryJob = Job.getInstance(getConf(), "Token Dictionary");

                    dictonaryJob.setJarByClass(getClass());
                    dictonaryJob.setMapperClass(TokenDictionaryMapper.class);
                    dictonaryJob.setReducerClass(TokenDictionaryReducer.class);
                    dictonaryJob.setMapOutputKeyClass(NullWritable.class);
                    dictonaryJob.setMapOutputValueClass(Text.class);

                    dictonaryJob.setOutputKeyClass(Text.class);
                    dictonaryJob.setOutputValueClass(NullWritable.class);

                    dictonaryJob.setInputFormatClass(TextInputFormat.class);
                    dictonaryJob.setOutputFormatClass(TextOutputFormat.class);

                    FileInputFormat.addInputPath(dictonaryJob, stage2);
                    FileOutputFormat.setOutputPath(dictonaryJob, stage3);

                    dictonaryJob.setNumReduceTasks(1);

                    retcode = dictonaryJob.waitForCompletion(true) ? 0 : 1;

                    if (retcode == 0) {
                        // combine output files
                        //HadoopUtils.copyMerge(fs, stage2, FileSystem.getLocal(getConf()), output, false, getConf(), null);
                        //HadoopUtils.copyMerge(fs, stage3, FileSystem.getLocal(getConf()), output, false, getConf(), null);
                    }
                }
            }
        }
        return retcode;
    }


    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new AmazonChiDriver(), args);
        System.exit(exitCode);
    }


}