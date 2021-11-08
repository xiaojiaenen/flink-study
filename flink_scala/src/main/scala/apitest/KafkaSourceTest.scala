package apitest

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties
import scala.util.Properties

object KafkaSourceTest {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment

    val prop=new Properties
    prop.setProperty("bootstrap.servers","master:9092,slave1:9092,slave2:9092")
    prop.setProperty("group.id","flink_group")
    prop.setProperty("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer")
    prop.setProperty("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer")

//    val stream = env.addSource(new FlinkKafkaConsumer[String]("first", new SimpleStringSchema(), prop))
//
//    stream.flatMap(_.split(" ")).map((_,1)).keyBy(0).sum(1)
    val stream=env.addSource(new MySensorSource())
    val conf=new FlinkJedisPoolConfig.Builder().setHost("master").setPort(6379).build()

//    stream.addSink(new MysqlSink())
    stream.addSink(new RedisSink[SensorReading](conf,new MyRedisMapper))

    stream.print()

    env.execute()

  }

}
