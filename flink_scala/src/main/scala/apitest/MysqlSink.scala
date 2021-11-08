package apitest

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}

import java.sql.{Connection, DriverManager, PreparedStatement}


class MysqlSink extends RichSinkFunction[SensorReading]{

  var conn:Connection=_
  var insertPs:PreparedStatement=_
//  var updatePs:PreparedStatement=_

  override def open(parameters: Configuration): Unit = {
    conn=DriverManager.getConnection("jdbc:mysql://master:3306/test?createDatabaseIfNotExist=true","root","123456")
    insertPs=conn.prepareStatement("insert into sensor values(?,?,?)")
  }

  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    insertPs.setString(1,value.id)
    insertPs.setLong(2,value.timeStamp)
    insertPs.setDouble(3,value.temperature)
    insertPs.execute()
  }

  override def close(): Unit = {
    insertPs.close()
    conn.close()
  }

}
