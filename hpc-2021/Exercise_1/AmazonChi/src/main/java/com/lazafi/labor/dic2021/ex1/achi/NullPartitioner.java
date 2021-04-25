package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class NullPartitioner extends Partitioner<NullWritable, Text> {
    @Override
    public int getPartition(NullWritable pair, Text text, int numberOfPartitions) {
        // make sure that partitions are non-negative12
        //return Math.abs(pair.getToken().hashCode() % numberOfPartitions);
        return Math.abs(numberOfPartitions);
    }
}