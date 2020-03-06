interp.load.module(pwd / RelPath("libs/sparkSession.sc"))
@

import spark.implicits._

val sourceDf = spark.read.parquet("parquet/")

val df = sourceDf.select($"Height_cm")
df.stat.approxQuantile("Height_cm", Array(0.25,0.5,0.75),0.0)

val desc = sourceDf.describe().show()
val summary = sourceDf.summary().show()

// https://databricks.com/blog/2015/06/02/statistical-and-mathematical-functions-with-dataframes-in-spark.html
// https://jaceklaskowski.gitbooks.io/mastering-spark-sql/spark-sql-StatFunctions.html
// https://spark.apache.org/docs/2.4.5/api/java/index.html?org/apache/spark/sql/DataFrameStatFunctions.html
