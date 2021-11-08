package wordcount;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;



public class WordCountSocket {
    public static void main(String[] args) throws Exception {

        Configuration conf=new Configuration();
        conf.setInteger(RestOptions.PORT,8082);
        StreamExecutionEnvironment.createLocalEnvironment(2,conf);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();



        DataStreamSource<String> stream = env.readTextFile("/home/xiaojia/data/data.txt");


        SingleOutputStreamOperator<Tuple2<String, Integer>> wordCount = stream.flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (s, collector) -> {
//            String[] split = s.split(" ");
//            for (String token : split) {
//                if (token.length() > 0) {
                    collector.collect(new Tuple2<>(s, 1));
//                }
//            }
        }).returns(Types.TUPLE(Types.STRING,Types.INT))
                .keyBy(0).timeWindow(Time.seconds(5)).sum(1);

        wordCount.print();
        env.execute();

    }
}
