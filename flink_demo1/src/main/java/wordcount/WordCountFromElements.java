package wordcount;

import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import pojo.Score;
import pojo.Word;

import java.util.Collections;


public class WordCountFromElements {
    public static void main(String[] args) throws Exception {

        Configuration conf=new Configuration();
        conf.setInteger(RestOptions.PORT,8082);
        StreamExecutionEnvironment.createLocalEnvironment(2,conf);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        DataStreamSource<Integer> dataStream = env.fromElements(1, 2, -3, 0, 5, -9, 8);
//        SingleOutputStreamOperator<Integer> data = dataStream.filter(input -> input > 0);

//        DataStreamSource<String> dataStream = env.fromElements("Hello World", "Hello is Flink");
//        SingleOutputStreamOperator<String> data = dataStream.flatMap((String input, Collector<String> collector) -> {
//                    for (String s : input.split(" ")) {
//                        collector.collect(s);
//                    }
//                }
//        ).returns(Types.STRING);

//        DataStream<Tuple2<Integer,Double>> dataStream = env.fromElements(
//                Tuple2.of(1,2.0),Tuple2.of(1,4.9),Tuple2.of(2,8.0),Tuple2.of(3,3.0),Tuple2.of(1,2.1),
//                Tuple2.of(2,3.2)
//        );

//        DataStreamSource<Word> dataStream = env.fromElements(new Word("hello", 19), new Word("world", 4), new Word("hello", 2), new Word("hadoop", 12));
//
//        SingleOutputStreamOperator<Word> data = dataStream.keyBy(word-> word.word).max("count");

        DataStreamSource<Object> dataStream = env.fromElements(
            Score.of("zhangsan","english",84),
            Score.of("zhangsan","math",95),
            Score.of("lisi","english",24),
            Score.of("lisi","math",98),
            Score.of("wangwu","english",73),
            Score.of("wangwu","math",75)
        );

//        SingleOutputStreamOperator<Score> data = dataStream.keyBy("name")
//                .reduce((s1, s2) -> Score.of(s1.name, "Sum", s1.score + s1.score));

//        DataStream<Tuple2<Integer,Double>> dataStream1 = env.fromElements(
//                Tuple2.of(1,2.0),Tuple2.of(1,4.9),Tuple2.of(2,8.0),Tuple2.of(3,3.0),Tuple2.of(1,2.1),
//                Tuple2.of(2,3.2)
//        );
//
//        DataStream<Tuple2<Integer,Double>> dataStream2 = env.fromElements(
//                Tuple2.of(1,2.0),Tuple2.of(1,4.9),Tuple2.of(2,8.0),Tuple2.of(3,3.0),Tuple2.of(1,2.1),
//                Tuple2.of(2,3.2)
//        );
//
//        DataStream<Tuple2<Integer, Double>> data = dataStream1.union(dataStream2);


//        DataStream<Tuple2<Integer,Double>> dataStream1 = env.fromElements(
//                Tuple2.of(1,2.0),Tuple2.of(1,4.9),Tuple2.of(2,8.0),Tuple2.of(3,3.0),Tuple2.of(1,2.1),
//                Tuple2.of(2,3.2)
//        );
//
//        DataStream<Tuple2<Integer,Integer>> dataStream2 = env.fromElements(
//                Tuple2.of(1,2),Tuple2.of(1,4),Tuple2.of(2,8),Tuple2.of(3,3),Tuple2.of(1,2),
//                Tuple2.of(2,3)
//        );
//
////        DataStream<Tuple2<Integer, Double>> data = dataStream1.union(dataStream2);
//        ConnectedStreams<Tuple2<Integer, Double>, Tuple2<Integer, Integer>> data = dataStream1.connect(dataStream2);
//        System.out.println(TypeInformation.of(Score.class).createSerializer(new ExecutionConfig()));



        env.execute();

    }
}
