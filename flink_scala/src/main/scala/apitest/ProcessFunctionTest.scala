package apitest

import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

object ProcessFunctionTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStateBackend(new FsStateBackend("file:///home/xiaojia/checkpoint",true))
    env.enableCheckpointing(500,CheckpointingMode.EXACTLY_ONCE)

    val stream = env.socketTextStream("127.0.0.1", 44444)
    val dataStream = stream.map(
      data => {
        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    ).keyBy(_.id)
      .process(new KeyedProcessFunction[String,SensorReading,String] {
        lazy val lastTempState=getRuntimeContext.getState(new ValueStateDescriptor[Double]("last-temp",classOf[Double]))
        lazy val timerTsState=getRuntimeContext.getState(new ValueStateDescriptor[Long]("timer-ts",classOf[Long]))


        override def processElement(i: SensorReading, context: KeyedProcessFunction[String, SensorReading, String]#Context, collector: Collector[String]): Unit = {
          val lastTemp=lastTempState.value()
          val timerTs=timerTsState.value()

          lastTempState.update(i.temperature)

          if(i.temperature>lastTemp && timerTs==0){
            val timeTs=context.timerService().currentProcessingTime()+10000
            context.timerService().registerProcessingTimeTimer(timeTs)
            timerTsState.update(timeTs)

          }else if(i.temperature<lastTemp){
            context.timerService().deleteProcessingTimeTimer(timerTsState.value())
            timerTsState.clear()
          }

        }

        override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, SensorReading, String]#OnTimerContext, out: Collector[String]): Unit = {
          out.collect("警告：传感器"+ctx.getCurrentKey+"的温度已经连续10s升高！")
          timerTsState.clear()
        }
      })

    dataStream.print()

    env.execute("process function test")
  }
}
