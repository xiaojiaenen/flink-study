package tableandsql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.scala._

case class SensorReading(id: String, timeStamp: Long, temperature: Double)


object TableDemo1 {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val inputStream=env.readTextFile("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/data.txt")
    val dataStream=inputStream
      .map(data=>{
        val arr=data.split(",")
        SensorReading(arr(0),arr(1).toLong,arr(2).toDouble)
      })

    val tableEnv=StreamTableEnvironment.create(env)

    val dataTable=tableEnv.fromDataStream(dataStream)

    val resultTable = dataTable
      .select("id,temperature")
      .filter("id=='sensor_1'")

    tableEnv.createTemporaryView("dataTable",dataTable)
    val sql="select id,temperature from dataTable where id='sensor_1'"
    val resultSqlTable=tableEnv.sqlQuery(sql)

    resultTable.toAppendStream[(String,Double)].print("table result")
    resultSqlTable.toAppendStream[(String,Double)].print("sql result")

    env.execute()

  }

}
