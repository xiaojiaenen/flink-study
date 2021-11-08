package wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
//import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import scala.Tuple2;


import java.util.Properties;

public class WordCountKafkaInStdOut {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        DataStreamSource<String> stream = env.readTextFile("/home/xiaojia/data/data.txt");

        Properties prop=new Properties();
        prop.setProperty("bootstrap.servers","master:9092,slave1:9092,slave2:9092");
        prop.setProperty("group.id","wordCount");
        prop.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        prop.setProperty("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        String inputTopic="second";
//        String outputTopic="WordCount";

        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(inputTopic, new SimpleStringSchema(), prop);
        DataStreamSource<String> stream = env.addSource(consumer);

//        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCount = stream.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
//            String[] split = s.split(" ");
//            for (String token : split) {
//                if (token.length() > 0) {
//                    collector.collect(new Tuple2<>(token, 1));
//                }
//            }
//        })
//                .keyBy(0).timeWindow(Time.seconds(5)).sum(1);

        stream.print();
        env.execute();

    }
}
