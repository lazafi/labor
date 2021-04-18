```
ssh e26842@lbd.zserv.tuwien.ac.at


 rm -rf target/output
 rm -rf target/tmp
 ~/app/hadoop-3.2.2/bin/hadoop jar target/amazon-chi-0.0.1.jar com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver -conf conf/hadoop-local.xml input2 target/output target/tmp

export HADOOP_CLASSPATH=/home/lazafi/labor/hpc-2021/Exercise_1/AmazonChi/target/classes
~/app/hadoop-3.2.2/bin/hdfs dfs -text target/tmp/part-r-00000 
```
