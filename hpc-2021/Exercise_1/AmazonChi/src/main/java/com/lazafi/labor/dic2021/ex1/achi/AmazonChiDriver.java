package com.lazafi.labor.dic2021.ex1.achi;


import com.lazafi.labor.dic2021.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryStatisticsTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.ValueTuple;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
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
        if (args.length != 3) {
            System.err.printf("Usage: %s [generic options] <input> <output> <tmp>\n", getClass().getSimpleName());
            ToolRunner.printGenericCommandUsage(System.err);
            return -1;
        }

        String inputDir = args[0];
        String outputDir = args[1];
        String tmpDir = args[2];


        int retcode = 1;

        Job counterJob = Job.getInstance(getConf(), "Document Counter");
        counterJob.setJarByClass(getClass());
        counterJob.setMapperClass(DocumentCountMapper.class);
        //countingJob.setCombinerClass(.class);
        counterJob.setReducerClass(DocumentCountReducer.class);

        counterJob.setOutputKeyClass(Text.class);
        counterJob.setOutputValueClass(LongWritable.class);

        counterJob.setInputFormatClass(TextInputFormat.class);

        FileInputFormat.addInputPath(counterJob, new Path(inputDir));

        counterJob.setOutputFormatClass(NullOutputFormat.class);
        //TextOutputFormat.setOutputPath(counterJob, new Path(tmpDir));
        //FileOutputFormat.setOutputPath(countingJob, new Path(args[1]));

        if (counterJob.waitForCompletion(false)) {
            Job tokenizerJob = Job.getInstance(getConf(), "Review Tokenizer");

            tokenizerJob.setJarByClass(getClass());
            //job.setMapperClass(CategoryTokenizerMapper.class);
            tokenizerJob.setMapperClass(TokenCategoryMapper.class);

            // set document counters
            Iterator<Counter> cnts = counterJob.getCounters().getGroup(COUNTERGROUP).iterator();
            while (cnts.hasNext()) {
                Counter counter = cnts.next();
                //CategoryChiReducer.setDocumentCount(job, counter.getName(), counter.getValue());
                TokenChiReducer.setDocumentCount(tokenizerJob, counter.getName(), counter.getValue());
            }
            // set total document counter
            long totalDocuments = counterJob.getCounters().findCounter(COUNTERGROUP, DOCUMENTS.TOTAL.name()).getValue();
            tokenizerJob.getConfiguration().set(COUNTERGROUP + DOCUMENTS.TOTAL.name(), Long.toString(totalDocuments));

            //job.setReducerClass(CategoryChiReducer.class);
            tokenizerJob.setReducerClass(TokenChiReducer2.class);
            tokenizerJob.setMapOutputKeyClass(Text.class);
            //job.setMapOutputValueClass(WordCountTuple.class);
            tokenizerJob.setMapOutputValueClass(Text.class);

            tokenizerJob.setOutputKeyClass(CategoryTokenChiTuple.class);
            //tokenizerJob.setOutputValueClass(CategoryStatisticsTuple.class);
            tokenizerJob.setOutputValueClass(NullWritable.class);
            tokenizerJob.setOutputFormatClass(SequenceFileOutputFormat.class);
            //SequenceFileOutputFormat.setCompressOutput(tokenizerJob, true);
            //SequenceFileOutputFormat.setOutputCompressorClass(tokenizerJob, GzipCodec.class);
            //SequenceFileOutputFormat.setOutputCompressionType(tokenizerJob, SequenceFile.CompressionType.BLOCK);
            //tokenizerJob.setOutputFormatClass(KeyValueTextOutputFormat.class);

            FileInputFormat.addInputPath(tokenizerJob, new Path(inputDir));
            FileOutputFormat.setOutputPath(tokenizerJob, new Path(tmpDir));

            if (tokenizerJob.waitForCompletion(false)) {
                Job sortJob = Job.getInstance(getConf(), "Chi Sorter");
                sortJob.setJarByClass(getClass());

                sortJob.setMapperClass(CategoryChiMapper2.class);
                sortJob.setReducerClass(CategoryChiReducer2.class);
                sortJob.setPartitionerClass(CategoryChiPartitioner.class);
                sortJob.setGroupingComparatorClass(CategoryChiGroupingComparator.class);

                sortJob.setMapOutputKeyClass(CategoryTokenChiTuple.class);
                sortJob.setMapOutputValueClass(TokenChiTuple.class);
                sortJob.setOutputKeyClass(Text.class);
                sortJob.setOutputValueClass(Text.class);
                sortJob.setInputFormatClass(SequenceFileInputFormat.class);
                sortJob.setOutputFormatClass(TextOutputFormat.class);

                FileInputFormat.addInputPath(sortJob, new Path(tmpDir));
                FileOutputFormat.setOutputPath(sortJob, new Path(outputDir));

                retcode = sortJob.waitForCompletion(true) ? 0 : 1;


            }
            if (false) {

                Job computerJob = Job.getInstance(getConf(), "Chi Computer");

                computerJob.setJarByClass(getClass());
                computerJob.setMapperClass(CategoryChiMapper.class);

                //long totalDocuments = counterJob.getCounters().findCounter(COUNTERGROUP, DOCUMENTS.TOTAL.name()).getValue();
                computerJob.getConfiguration().set(COUNTERGROUP + DOCUMENTS.TOTAL.name(), Long.toString(totalDocuments));
                // set document counters
                //Iterator<Counter> cnts2 = counterJob.getCounters().getGroup(COUNTERGROUP).iterator();
                //while (cnts2.hasNext()) {
                //    Counter counter = cnts2.next();
                //    CategoryChiReducer.setDocumentCount(computerJob, counter.getName(), counter.getValue());
                //}


                computerJob.setReducerClass(CategoryChiReducer.class);
                computerJob.setMapOutputKeyClass(Text.class);
                //job.setMapOutputValueClass(WordCountTuple.class);
                computerJob.setMapOutputValueClass(TokenChiTuple.class);

                computerJob.setOutputKeyClass(Text.class);
                computerJob.setOutputValueClass(Text.class);

                computerJob.setInputFormatClass(SequenceFileInputFormat.class);
                //computerJob.setInputFormatClass(KeyValueTextInputFormat.class);
                computerJob.setOutputFormatClass(TextOutputFormat.class);

                FileInputFormat.addInputPath(computerJob, new Path(tmpDir));
                FileOutputFormat.setOutputPath(computerJob, new Path(outputDir));

                retcode = computerJob.waitForCompletion(true) ? 0 : 1;
            }
        }
        return retcode;
    }


    public static void main(String[] args) throws Exception {
        int exitCode = ToolRunner.run(new AmazonChiDriver(), args);
        System.exit(exitCode);
    }
}