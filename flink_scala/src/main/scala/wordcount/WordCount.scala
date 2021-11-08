package wordcount

import org.apache.flink.api.scala.ExecutionEnvironment
import org.apache.flink.api.scala._

// 批处理
object WordCount {

  def main(args: Array[String]): Unit = {
    val env=ExecutionEnvironment.getExecutionEnvironment
    val dataStream = env.readTextFile("/home/xiaojia/Projects/IDEAProject/FlinkStudy/flink_demo1/src/main/resources/hello.txt")
    val result = dataStream.flatMap(_.split(" ")).map((_, 1)).groupBy(0).sum(1)
    result.print()
  }
}
