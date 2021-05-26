package com.databricks.example;

import com.databricks.example.util.Stop;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.util.ToolRunner;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;
import scala.Tuple3;
import scala.Tuple4;
import scala.collection.JavaConverters;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * e0026842
 * Attila Lazar
 * DIC2021
 *
 * Part1.
 *
 * parse amazon reviews and calculate chi values for category token pairs. sort these values and output 150 tokens with
 * the highest chi2 values per category
 */
public class Part1 {

    // delimiters to use for review tokenization
    private final static String DELIMITERS = " \t0123456789.!?,;:()[]{}-_\"'`~#&*%$\\/";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.printf("Usage: %s [generic options] <input.json> <outputdir1>\n", "Part1");
            return;
        }
        String inputPath = args[0];
        String outputPath = args[1];

        ObjectMapper objectMapper = new ObjectMapper();

        // init spark context
        SparkConf conf = new SparkConf().setAppName("appName").setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // load reviews file into RDD
        // every record is one json file = one review
        JavaRDD<String> reviews = sc.textFile(inputPath);

        // obtain counters with rdd transformations
        // count the number of reviews for each category
        JavaRDD<Tuple2<String, Long>> categoryDocumentCount = reviews
                // parse json object and extract the category
                .map(line -> (String) objectMapper.readValue(line, Map.class).get("category"))
                // for every category occurence we create a tupple <categoryname> <1>
                // then we group by category and count the 1-s
                .mapToPair(cat -> new Tuple2<>(cat, 1L))
                .groupByKey()
                // count all the tuples
                .map(tuple2 -> {
                    long size = 0;
                    for (Object o: tuple2._2) {
                        size ++;
                    }
                    return new Tuple2<String, Long>(tuple2._1, size);
                });

        // collect the category counters from the RDD and store them in a list
        List<Tuple2<String, Long>> categoryCount = categoryDocumentCount.collect();
        // for the total document count we count all the records in the RDD
        long totalDocumentCount = reviews.count();

        JavaRDD<Map> reviewsJson = reviews
                .map(line -> objectMapper.readValue(line, Map.class));

        /*
        JavaPairRDD<String, String> tokensCategories = reviewsJson
                .flatMapToPair(review -> {
                    String text = (String) review.get("reviewText");
                    String category = (String) review.get("category");
                    StringTokenizer st = new StringTokenizer(text, DELIMITERS);
                    Set<Tuple2<String, String>> words = new HashSet<>();
                    while (st.hasMoreTokens()) {
                        // lowercase
                        String token = st.nextToken().toLowerCase();
                        // filter stopwords
                        // filter tokens <= 1
                        if (
                                !Stop.words.contains(token)
                                        && token.length() > 1
                        ) {
                            words.add(new Tuple2<String, String>(token, category));
                        }
                    }

                    return words.iterator();
                });
        */

        // transform reviews rdd to obtain per category grouped tokens with chi2 values calculated
        JavaRDD<Tuple2<String, Tuple2<String, Double>>> categoryTokenChi =
                // start with the parsed reviews.
                reviewsJson
                        // tokenize review Text
                        .flatMapToPair(review -> {
                            String text = (String) review.get("reviewText");
                            String category = (String) review.get("category");
                            StringTokenizer st = new StringTokenizer(text, DELIMITERS);
                            // use a set to collect token - category pairs
                            Set<Tuple2<String, String>> words = new HashSet<>();
                            while (st.hasMoreTokens()) {
                                // lowercase
                                String token = st.nextToken().toLowerCase();
                                // filter stopwords
                                // filter tokens <= 1
                                if (
                                        !Stop.words.contains(token)
                                                && token.length() > 1
                                ) {
                                    words.add(new Tuple2<String, String>(token, category));
                                }
                            }
                            // emmit all token-category tuples found in one review as separate records
                            return words.iterator();
                        })
                        // group token-category tuples by token
                        // this will result in recors where the key is the token
                        // and value is a list of categories the token occurs
                .groupByKey()
                        // use flatmap to emmit category-token pairs with counters used for chi2 calculations
                .flatMap(tuple2 -> {

                    // count the occurency of token in categories
                    // use a hashmap to store all counts per category
                    Map<String, Long> categories = new LinkedHashMap<String, Long>();
                    for (String val : tuple2._2) {
                        if (categories.containsKey(val)) {
                            categories.put(val, categories.get(val.toString()).longValue() + 1L);
                        } else {
                            categories.put(val, 1L);
                        }
                    }

                    // total occurences of token
                    // sum up all occurences by category
                    double M = 0;
                    for (Long val :categories.values()) {
                        M += val;
                    }

                    // emmit tuples for each category the token occurs.
                    // also add counters to the tuple for later chi2 calculation
                    List <Tuple4<String,  String, Double, Double>> categoryTokenCount = new ArrayList<>();
                    for (Map.Entry<String, Long> category: categories.entrySet()) {
                        categoryTokenCount.add(new Tuple4<String,  String, Double, Double> (tuple2._1, category.getKey(), category.getValue().doubleValue(), M));
                    }
                    return categoryTokenCount.iterator();
                })
                        // now we have token - category pairs with counters needed for chi2 calculation
                .mapToPair(tuple4 -> {

                    String category = tuple4._2();
                    String token = tuple4._1();
                    // document count by category
                    // lookup from the list obtained above
                    Double P = null;
                    List<Tuple2<String, Long>> documentCountByCategory = categoryCount;
                    for (Object dc : documentCountByCategory) {
                        Tuple2<String, Long> t2 = (Tuple2) dc;
                        if (t2._1.equals(category)) {
                            P = t2._2.doubleValue();
                        }
                    }
                    if ( P == null) {
                        System.out.println("no count value for category: " + tuple4._2());
                    }
                        //token X occurs overall (A+B)
                        double M = tuple4._4();

                        double N = totalDocumentCount;

                        //token X occurs in Category
                        double A = tuple4._3();
                        // chi2
                        double chi2 = (N * Math.pow((A*N - M*P), 2))/(P*M*(N-P)*(N-M));
                    return new Tuple2<String, Tuple2<String, Double>>(category, new Tuple2<String, Double>(token, chi2));
                })
                        // use map to convert JavaPairRDD to JavaRDD
                .map(t->t)
                        // sort by chi2 value
                .sortBy(t->t._2._2.doubleValue(), false, 1)
                ;




        // group values by category
        JavaPairRDD<String, List<Tuple2<String,Tuple2<String,Double>>>> catList = categoryTokenChi
                .groupBy(t -> t._1)
                // for limiting token values to 150 we use java8 stream-api. this will reduce the size of the value-list
                // to 150 tokens with the max chi2 value
                .mapValues(list -> StreamSupport.stream(list.spliterator(), false).limit(150).collect(Collectors.toList()));

        // build output string for token with chi value in one line for each category
        // The result is one string with category and all tokens with chi2 values
       JavaRDD<String> categoryTokenOutput = catList.mapValues(tokenList -> tokenList.stream().map(t -> {
                            StringBuilder sb = new StringBuilder();
                            sb.append(t._2._1).append(":").append(t._2._2);
                            return sb.toString();
                        }).collect(Collectors.joining(", "))
                    )
                .map(tuple2 -> {
                    StringBuilder sb = new StringBuilder();
                    sb.append(tuple2._1).append(" ").append(tuple2._2);
                    return sb.toString();
                });



       // construct dictionar list
        // we again group by category
        JavaRDD<String> dictonaryList = categoryTokenChi
                .groupBy(t -> t._1)
                // limit tokens to 150
                .flatMap(tuple2 -> StreamSupport.stream(tuple2._2.spliterator(), false).limit(150).iterator())
                // we use a series of maps to extract only the tokens
                // only use value tuples
                .map(t -> t._2)
                // only use tokens
                .map(t -> t._1)
                // sort alphabeticaly
                .sortBy(t -> t, true, 1);

                // reduce the RDD to a single String
                String dictonaryLine = dictonaryList.reduce((line, token) -> (line + " " + token));

                // for the output we construct an RDD with only one record in it
                List dl = new ArrayList();
                dl.add(dictonaryLine);
                JavaRDD<String> dictonaryLineRdd = sc.parallelize(dl);

                // we combine the two RDDs to one and output it tu a file
                categoryTokenOutput.union(dictonaryLineRdd).saveAsTextFile(outputPath);

    }
}
