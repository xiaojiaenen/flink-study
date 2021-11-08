package apitest

import org.apache.flink.api.common.functions.{RichFlatMapFunction, RichMapFunction}
import org.apache.flink.api.common.state.{ListStateDescriptor, ValueState, ValueStateDescriptor}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector

import java.util

object StateTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val stream = env.socketTextStream("127.0.0.1", 44444)
    val dataStream = stream.map(
      data => {

        val arr = data.split(",")
        SensorReading(arr(0), arr(1).toLong, arr(2).toDouble)
      }
    )

    val alertStream = dataStream.keyBy(_.id)
      //      .flatMap(new TempChangeAlert(10.0))
      .flatMapWithState[(String,Double,Double),Double]({
    case (data: SensorReading, None) => (List.empty, Some(data.temperature))
    case (data: SensorReading, lastTemp: Some[Double]) => {
      val diff = (data.temperature - lastTemp.get).abs
      if (diff > 10.0)
        (List((data.id, lastTemp.get, data.temperature)), Some(data.temperature))
        else
        (List.empty, Some(data.temperature))

    }}
    )
    alertStream.print()

    env.execute("state test")

  }
}

class TempChangeAlert(d: Double) extends RichFlatMapFunction[SensorReading, (String, String, Double, Double)] {

  lazy val lastTempState = getRuntimeContext.getState(new ValueStateDescriptor[Double]("last-temp", classOf[Double]))

  override def flatMap(in: SensorReading, collector: Collector[(String, String, Double, Double)]): Unit = {
    val lastTemp = lastTempState.value()
    val diff = (in.temperature - lastTemp).abs
    if (diff > d) {
      collector.collect(("警报！！！", in.id, lastTemp, in.temperature))
    }

    lastTempState.update(in.temperature)
  }
}


//class MyRichMapper extends RichMapFunction[SensorReading,String]{
//  var valueState:ValueState[Double]=_
//  lazy val valueListState=getRuntimeContext.getListState(new ListStateDescriptor[Int]("listState",classOf[Int]))
//
//  override def open(parameters: Configuration): Unit = {
//    valueState=getRuntimeContext.getState(new ValueStateDescriptor[Double]("valueState",classOf[Double]))
//
//  }
//
//  override def map(in: SensorReading): String = {
//    val myV=valueState.value()
//    valueState.update(in.temperature)
//    valueListState.add(1)
//    val list=new util.ArrayList[Int]()
//    list.add(2)
//    list.add(3)
//    valueListState.addAll(list)
//    valueListState.update(list)
//    in.id
//  }
//}