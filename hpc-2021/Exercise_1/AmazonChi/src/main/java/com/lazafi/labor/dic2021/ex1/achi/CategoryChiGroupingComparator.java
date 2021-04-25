package com.lazafi.labor.dic2021.ex1.achi;

import com.lazafi.labor.dic2021.ex1.achi.model.CategoryChiTuple;
import com.lazafi.labor.dic2021.ex1.achi.model.CategoryTokenChiTuple;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CategoryChiGroupingComparator extends WritableComparator {

    public CategoryChiGroupingComparator() {
        super(CategoryChiTuple.class, true);
    }

    @Override
    public int compare(WritableComparable wc1, WritableComparable wc2) {
        CategoryChiTuple k = (CategoryChiTuple) wc1;
        CategoryChiTuple k2 = (CategoryChiTuple) wc2;
        return k.getToken().compareTo(k2.getToken());
    }

}
