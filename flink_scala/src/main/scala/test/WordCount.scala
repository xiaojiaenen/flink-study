package test

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.Properties


object WordCount {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val prop=new Properties
    prop.setProperty("bootstrap.servers","master:9092,slave1:9092,slave2:9092")
    prop.setProperty("group.id","flink")
    prop.setProperty("key.deserializer",classOf[StringDeserializer].toString)
    prop.setProperty("value.deserializer",classOf[StringDeserializer].toString)

    val stream=env.addSource(new FlinkKafkaConsumer[String]("sensor",new SimpleStringSchema(),prop).setStartFromLatest())
    val result=stream.flatMap(_.split("\\s+")).map((_,1)).keyBy(0).sum(1)
    result.print()
    val conf=new FlinkJedisPoolConfig.Builder().setHost("master").setPort(6379).build()
//    result.addSink(new RedisSink[(String, Int)](conf,new RedisMapper[(String, Int)] {
//      override def getCommandDescription: RedisCommandDescription = {
//        new RedisCommandDescription(RedisCommand.HSET,"sensor2")
//      }
//
//      override def getKeyFromData(t: (String, Int)): String = {
//        t._1
//      }
//
//      override def getValueFromData(t: (String, Int)): String = {
//        t._2.toString
//      }
//    }))
    env.execute()
  }
}
