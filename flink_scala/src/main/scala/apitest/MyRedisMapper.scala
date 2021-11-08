package apitest

import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

class MyRedisMapper extends RedisMapper[SensorReading]{
  override def getCommandDescription: RedisCommandDescription = {
    new RedisCommandDescription(RedisCommand.HSET,"sensor")
  }

  override def getKeyFromData(t: SensorReading): String = {
    t.id
  }

  override def getValueFromData(t: SensorReading): String = {
    t.temperature.toString
  }
}
