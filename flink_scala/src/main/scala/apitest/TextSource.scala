package apitest

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._

object TextSource {
  def main(args: Array[String]): Unit = {
    val env=StreamExecutionEnvironment.getExecutionEnvironment
    val stream=env.readTextFile("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_scala/src/main/resources/data.txt")
    val result=stream.flatMap(_.split(" ")).map((_,1)).keyBy(0).sum(1)
    result.print()
    env.execute()
  }

}
