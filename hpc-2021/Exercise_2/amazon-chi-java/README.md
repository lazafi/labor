
## local

mvn clean package && ~/app/spark-3.1.1-bin-hadoop2.7/bin/spark-submit --conf spark.driver.extraJavaOptions="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=7777" target/exercise2.1-0.1.jar ~/labor/hpc-2021/Exercise_2/reviews_devset.json 
scp target/exercise2.1-0.1.jar  e26842@lbd.zserv.tuwien.ac.at:/home/dic21/e26842/Exercise_2

## cluster
spark-submit exercise2.1-0.1.jar /user/pknees/amazon-reviews/full/reviews_devset.json Exercise_2/output
hadoop fs -getmerge hdfs://nameservice1/user/e26842/Exercise_2/output ./output_rdd.txt
hadoop fs -rm hdfs://nameservice1/user/e26842/Exercise_2/output/*