package apitest

import org.apache.flink.runtime.state.memory.MemoryStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object SideOutputTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    env.enableCheckpointing(500,CheckpointingMode.EXACTLY_ONCE)
    val stream = env.socketTextStream("127.0.0.1", 44444)
    val dataStream = stream.map(
      data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    )
    val highTempStream=dataStream
      .process(new ProcessFunction[SensorReading,SensorReading]() {
        override def processElement(i: SensorReading, context: ProcessFunction[SensorReading, SensorReading]#Context, collector: Collector[SensorReading]): Unit = {
          if(i.temperature>30){
            collector.collect(i)
          }else{
            context.output(new OutputTag[(String,Long,Double)]("low"),(i.id,i.timeStamp,i.temperature))
          }
        }
      })

    highTempStream.print("high")
    highTempStream.getSideOutput(new OutputTag[(String,Long,Double)]("low")).print("low")

   env.execute("side output test")
  }
}
