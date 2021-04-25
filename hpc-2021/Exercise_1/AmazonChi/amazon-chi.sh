
hadoop jar amazon-chi-0.0.2.jar com.lazafi.labor.dic2021.ex1.achi.AmazonChiDriver -conf conf/hadoop-cluster.xml /user/pknees/amazon-reviews/full/reviewscombined.json /user/e26842/amazon-chi/output /user/e26842/amazon-chi/stopwords.txt
hadoop fs -getmerge /user/e26842/amazon-chi/output out-merged.txt
hadoop fs -getmerge /user/e26842/amazon-chi/output out2-merged.txt