package tableandsql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, FileSystem, Schema}

object FileOutputTest {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val tableEnv=StreamTableEnvironment.create(env)

    tableEnv.connect(new FileSystem().path("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/data.txt"))
      .withFormat(new Csv())
      .withSchema(new Schema()
        .field("id",DataTypes.STRING())
        .field("timestamp",DataTypes.BIGINT())
        .field("temperature",DataTypes.DOUBLE())
      )
      .createTemporaryTable("inputTable")

    val inputTable=tableEnv.from("inputTable")
    val resultTable=inputTable
      .select('id,'timestamp)
      .filter('id==="sensor_1")



    tableEnv.connect(new FileSystem().path("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/output.txt"))
      .withFormat(new Csv())
      .withSchema(new Schema()
        .field("id",DataTypes.STRING())
        .field("timestamp",DataTypes.BIGINT())
      )
      .createTemporaryTable("outputTable")


    resultTable.insertInto("outputTable")


    env.execute()

  }

}
