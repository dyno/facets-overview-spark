interp.load.module(pwd / RelPath("libs/sparkSession.sc"))
@

val tableName = "census"

val tables = spark.sql("SHOW TABLES").select("tableName").collect().map(_(0))
if (!tables.contains(tableName)) {
  val df = spark.read.parquet("parquet/")
  df.write.format("parquet").saveAsTable(tableName)
}

// https://cwiki.apache.org/confluence/display/Hive/StatsDev#StatsDev-ColumnStatistics
val table = spark.sql(s"SELECT * FROM $tableName")

spark.sql(s"ANALYZE TABLE $tableName COMPUTE STATISTICS")
val tableStats = spark.sql(s"DESCRIBE EXTENDED $tableName")
tableStats.show()

val colsStr = table.columns.mkString(",")
spark.sql(s"ANALYZE TABLE $tableName COMPUTE STATISTICS FOR COLUMNS $colsStr")

val stats = table.columns.map(col => spark.sql(s"DESCRIBE EXTENDED $tableName $col"))
stats.foreach(_.show())
