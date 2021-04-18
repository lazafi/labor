package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.CategoryTokenChiTuple;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CategoryChiPartitioner extends Partitioner<CategoryTokenChiTuple, Text> {
    @Override
    public int getPartition(CategoryTokenChiTuple pair, Text text, int numberOfPartitions) {
        // make sure that partitions are non-negative12
        return Math.abs(pair.getCategory().hashCode() % numberOfPartitions);
    }
}