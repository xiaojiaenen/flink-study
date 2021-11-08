package redis;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.netty4.io.netty.handler.ssl.JdkSslContext;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.redis.RedisSink;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommandDescription;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisMapper;
import pojo.Score;

public class MyRedis {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

//        DataStreamSource<Object> dataStream = env.fromElements(
//                Score.of("zhangsan","english",84),
//                Score.of("zhangsan","math",95),
//                Score.of("lisi","english",24),
//                Score.of("lisi","math",98),
//                Score.of("wangwu","english",73),
//                Score.of("wangwu","math",75)
//        );
        DataStream<Tuple2<Integer,Double>> dataStream = env.fromElements(
                Tuple2.of(1,2.0),Tuple2.of(1,4.9),Tuple2.of(2,8.0),Tuple2.of(3,3.0),Tuple2.of(1,2.1),
                Tuple2.of(2,3.2)
        );
        FlinkJedisPoolConfig config = new FlinkJedisPoolConfig.Builder().setHost("master").setPort(6379).build();
        dataStream.addSink(new RedisSink<Tuple2<Integer,Double>>(config,new MyRedisMapper()));
    }
}

class MyRedisMapper implements RedisMapper<Tuple2<Integer,Double>> {

    @Override
    public RedisCommandDescription getCommandDescription() {
        return new RedisCommandDescription(RedisCommand.HSET, "score");
    }

    @Override
    public String getKeyFromData(Tuple2<Integer,Double> data) {
        return data.f0.toString();
    }

    @Override
    public String getValueFromData(Tuple2<Integer,Double> data) {
        return data.f1.toString();
    }
}
