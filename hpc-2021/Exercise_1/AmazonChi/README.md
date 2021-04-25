```
ssh e26842@lbd.zserv.tuwien.ac.at
scp target/amazon-chi-0.0.3.jar e26842@lbd.zserv.tuwien.ac.at:/home/dic21/e26842/Exercise_1


 rm -rf target/output
 rm -rf target/tmp
 ~/app/hadoop-3.2.2/bin/hadoop jar target/amazon-chi-0.0.1.jar com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver -conf conf/hadoop-local.xml input2 target/output target/tmp

export HADOOP_CLASSPATH=/home/lazafi/labor/hpc-2021/Exercise_1/AmazonChi/target/classes
~/app/hadoop-3.2.2/bin/hdfs dfs -text target/tmp/part-r-00000 
```
hadoop jar amazon-chi-0.0.2.jar com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver -conf conf/hadoop-cluster.xml /user/pknees/amazon-reviews/full/reviews_devset.json /user/e26842/amazon-chi/output /user/e26842/amazon-chi/stopwords.txt 
hadoop jar amazon-chi-0.0.2.jar com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver -conf conf/hadoop-cluster.xml /user/pknees/amazon-reviews/full/reviewscombined.json /user/e26842/amazon-chi/output /user/e26842/amazon-chi/stopwords.txt

#TODO

* improve regexp
* comment
* draw
-* slimmer key for sorting
* run on cluster
* delete tmp 
* fix logging
-* final output (whitespaces) conf.set("mapreduce.output.textoutputformat.separator", " ");
* merged dictionary


