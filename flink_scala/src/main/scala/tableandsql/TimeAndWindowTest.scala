package tableandsql

import apitest.SensorReading
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._
import org.apache.flink.types.Row

object TimeAndWindowTest {
  def main(args: Array[String]): Unit = {

    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)


    val tableEnv=StreamTableEnvironment.create(env)


    val stream = env.readTextFile("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/data.txt")
    val dataStream = stream.map(
      data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    )


    val sensorTable = tableEnv.fromDataStream(dataStream, 'id, 'temperature, 'timeStamp, 'pt.proctime)
    sensorTable.printSchema()
    sensorTable.toAppendStream[Row].print()

    env.execute()


  }

}
