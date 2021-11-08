package wordcount

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time

object StreamWordCount {
  def main(args: Array[String]): Unit = {

    val parameterTool=ParameterTool.fromArgs(args)
    val host=parameterTool.get("host")
    val port=parameterTool.getInt("port")

    val env=StreamExecutionEnvironment.getExecutionEnvironment
//    env.setParallelism(1)
    val dataSet=env.socketTextStream(host,port)
    val result=dataSet.flatMap(_.split(" ")).map((_,1)).keyBy(0).timeWindow(Time.seconds(5)).sum(1)
    result.print()
    env.execute("stream word count")

  }

}
