package apitest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._


case class SensorReading(id: String, timeStamp: Long, temperature: Double)

object SourceTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataList = List(
      SensorReading("sensor_1", 1547718123, 35.8),
      SensorReading("sensor_6", 1547718225, 15.4),
      SensorReading("sensor_7", 1547718223, 6.7),
      SensorReading("sensor_10", 1547718205, 38.1),
      SensorReading("sensor_1", 1547718192, 32.5),
      SensorReading("sensor_6", 1547718204, 25.4),
      SensorReading("sensor_7", 1547718201, 16.47),
      SensorReading("sensor_10", 1547718202, 18.1)
    )

    val stream=env.fromCollection(dataList).keyBy("id").maxBy("temperature")
    stream.print()
    env.execute()
  }

}
