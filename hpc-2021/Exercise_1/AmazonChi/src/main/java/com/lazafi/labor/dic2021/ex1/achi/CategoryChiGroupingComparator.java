package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.CategoryTokenChiTuple;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CategoryChiGroupingComparator extends WritableComparator {

    public CategoryChiGroupingComparator() {
        super(CategoryTokenChiTuple.class, true);
    }

    @Override
    public int compare(WritableComparable wc1, WritableComparable wc2) {
        CategoryTokenChiTuple k = (CategoryTokenChiTuple) wc1;
        CategoryTokenChiTuple k2 = (CategoryTokenChiTuple) wc2;
        return k.getCategory().compareTo(k2.getCategory());
    }

}
