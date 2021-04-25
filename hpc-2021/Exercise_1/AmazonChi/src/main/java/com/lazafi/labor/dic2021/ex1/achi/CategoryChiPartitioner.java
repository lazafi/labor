package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.TokenChiTuple;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class CategoryChiPartitioner extends Partitioner<CategoryChiTuple, TokenChiTuple> {
    @Override
    public int getPartition(CategoryChiTuple pair, TokenChiTuple text, int numberOfPartitions) {
        // make sure that partitions are non-negative12
        return Math.abs(pair.getToken().hashCode() % numberOfPartitions);
    }
}