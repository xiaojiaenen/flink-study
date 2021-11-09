package tableandsql

import org.apache.flink.streaming.api.scala._
import org.apache.flink.table.api.DataTypes
import org.apache.flink.table.api.scala._
import org.apache.flink.table.descriptors.{Csv, Json, Kafka, Schema}

object KafkaPipelineTest {

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    val tableEnv = StreamTableEnvironment.create(env)

    tableEnv.connect(new Kafka()
      .version("universal")
      .topic("sensor")
      .property("bootstrap.servers", "master:9092")
    )
      .withFormat(new Csv())
      .withSchema(new Schema()
        .field("id", DataTypes.STRING())
        .field("timestamp", DataTypes.BIGINT())
        .field("temperature", DataTypes.DOUBLE())
      )
      .createTemporaryTable("inputTable")

    val sensorTable = tableEnv.from("inputTable")


    // 简单转换
    val resultTable=sensorTable
      .select('id,'temperature)
      .filter('id==="sensor_1")

    // 聚合转换

    val aggResultSqlTable=sensorTable
      .groupBy('id)
      .select('id,'id.count as 'count)

//    tableEnv.connect(new Kafka()
//      .version("universal")
//      .topic("sinktest")
//      .property("bootstrap.servers", "master:9092")
//    )
//      .withFormat(new Csv())
//      .withSchema(new Schema()
//        .field("id", DataTypes.STRING())
//        .field("temperature", DataTypes.DOUBLE())
//      )
//      .createTemporaryTable("kafkaOutputTable")

    tableEnv.connect(new Kafka()
      .version("universal")
      .topic("sinktest")
      .property("bootstrap.servers", "master:9092")
    )
      .withFormat(new Json())
      .withSchema(new Schema()
        .field("id", DataTypes.STRING())
        .field("count_", DataTypes.BIGINT())
      )
      .createTemporaryTable("kafkaOutputTable")


    aggResultSqlTable.insertInto("kafkaOutputTable")


    env.execute()


  }
}

