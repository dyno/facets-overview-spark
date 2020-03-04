// -----------------------------------------------------------------------------
// SparkSession

import $ivy.`sh.almond::ammonite-spark:0.4.2`
import org.apache.spark.sql.AmmoniteSparkSession

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, Encoder, Encoders => SparkEncoders, Row}
import org.apache.spark.sql.catalyst.encoders.{ExpressionEncoder, RowEncoder}

implicit val spark:SparkSession = {
  AmmoniteSparkSession
    .builder()
    .master("local[*]")
    .config("spark.home", sys.env("SPARK_HOME"))
    .config("spark.logConf", "true")
    .getOrCreate()
}

import org.apache.log4j.{Level, Logger}
Logger.getLogger("org").setLevel(Level.OFF)


// -----------------------------------------------------------------------------
// Utility Functions

//  ln -sf build/libs/facets-overview-spark_2.11-0.1.0-SNAPSHOT.jar ./
// import $cp.`facets-overview-spark_2.11-0.1.0-SNAPSHOT.jar`
val jarPath = pwd/up/RelPath("build/libs/facets-overview-spark_2.11-0.1.0-SNAPSHOT.jar")
interp.load.cp(jarPath)
import features.stats.spark.{DataFrameUtils, NamedDataFrame}

def loadCSVFile(filePath: String)(implicit spark:SparkSession): DataFrame = {
  spark.read
    .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
    .option("header", "false") //reading the headers
    .option("mode", "DROPMALFORMED")
    .option("inferSchema", "true")
    .load(filePath)
}

import $ivy.`com.thesamet.scalapb::scalapb-runtime:0.9.6`
import java.io.File
import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.Base64
import featureStatistics.feature_statistics.DatasetFeatureStatisticsList

def persistProto(proto: DatasetFeatureStatisticsList, base64Encode: Boolean = false, file: File) =
  if (base64Encode) {
    val b = Base64.getEncoder.encode(proto.toByteArray)
    val UTF8_CHARSET = Charset.forName("UTF-8")

    Files.write(Paths.get(file.getPath), new String(b, UTF8_CHARSET).getBytes())
  } else {
    Files.write(Paths.get(file.getPath), proto.toByteArray)
  }

// -----------------------------------------------------------------------------
import features.stats.spark.{DataFrameUtils, NamedDataFrame}

val featureList = Array(
  "Age",
  "Workclass",
  "fnlwgt",
  "Education",
  "Education-Num",
  "Marital Status",
  "Occupation",
  "Relationship",
  "Race",
  "Sex",
  "Capital Gain",
  "Capital Loss",
  "Hours per week",
  "Country",
  "Target")
val columns = DataFrameUtils.sanitizedNames(featureList)

val trainData: DataFrame = loadCSVFile("src/test/resources/data/adult.data.csv")
val testData: DataFrame = loadCSVFile("src/test/resources/data/adult.test.txt")
val train = trainData.toDF(columns: _*)
val test = testData.toDF(columns: _*)
val dataframes = List(NamedDataFrame(name = "train", train), NamedDataFrame(name = "test", test))

import features.stats.spark.FeatureStatsGenerator
val generator = new FeatureStatsGenerator(DatasetFeatureStatisticsList())
val proto = generator.protoFromDataFrames(dataframes)

persistProto(proto,base64Encode = false, new File("src/test/resources/data/stats.pb"))
persistProto(proto,base64Encode = true, new File("src/test/resources/data/stats.txt"))
