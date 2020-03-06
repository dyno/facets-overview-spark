import $ivy.`sh.almond::ammonite-spark:0.4.2`
import org.apache.spark.sql.{AmmoniteSparkSession => ASparkSession}

import org.apache.log4j.{Level, Logger}
Logger.getLogger("org").setLevel(Level.OFF)
