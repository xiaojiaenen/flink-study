package tableandsql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.{DataTypes, EnvironmentSettings}
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, Kafka, OldCsv, Schema}

object TableDemo2 {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.setParallelism(1)

    val tableEnv = StreamTableEnvironment.create(env)


    // 连接外部系统读取数据
        val filePath="/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/data.txt"
        tableEnv.connect(new FileSystem().path(filePath))
          .withFormat(new Csv())
          .withSchema(new Schema()
          .field("id",DataTypes.STRING())
          .field("timestamp",DataTypes.BIGINT())
          .field("temperature",DataTypes.DOUBLE()))
          .createTemporaryTable("inputTable")





    //连接kafka
//    tableEnv.connect(new Kafka()
//      .version("universal")
//      .topic("sensor")
////      .property("zookeeper")
//      .property("bootstrap.servers", "master:9092")
//    )
//      .withFormat(new Csv)
//      .withSchema(new Schema()
//        .field("id", DataTypes.STRING())
//        .field("timestamp", DataTypes.BIGINT())
//        .field("temperature", DataTypes.DOUBLE())
//    )
//      .createTemporaryTable("kafkaInputTable")
//

    // 使用table api
    val sensorTable=tableEnv.from("inputTable")

    val resultTable=sensorTable
      .select('id,'temperature)
      .filter('id==="sensor_1")


    // sql
    val resultSqlTable=tableEnv
      .sqlQuery(
        """
          |select id,temperature
          |from inputTable
          |where id='sensor_1'
          |""".stripMargin
      )


    resultSqlTable.toAppendStream[(String,Double)].print("sql api")
    resultTable.toAppendStream[(String,Double)].print("table api")

    env.execute()

  }

}
