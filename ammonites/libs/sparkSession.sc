val sparkSessionModule = sys.env.get("AMMONITE_REPL") match {
  case None => pwd / RelPath("libs/_SparkSession.sc")
  case Some(_) => pwd / RelPath("libs/_AmmoniteSparkSession.sc")
}
interp.load.module(sparkSessionModule)

@

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders => SparkEncoders, Row}
import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}

implicit val spark: SparkSession = {
  ASparkSession.builder
    .master("local[*]")
    .config("spark.home", sys.env("SPARK_HOME"))
    .config("spark.logConf", "true")
    .getOrCreate()
}
