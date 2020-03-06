import $ivy.`com.thesamet.scalapb::scalapb-runtime:0.9.6`

// load the built jar
val jars = ls.rec ! pwd / up / "build" |? { _.last.endsWith(".jar") }
interp.load.cp(jars.head)

interp.load.module(pwd / RelPath("libs/sparkSession.sc"))
@

// -----------------------------------------------------------------------------
// Utility Functions
import features.stats.spark.{DataFrameUtils, NamedDataFrame}

def loadCSVFile(filePath: String)(implicit spark: SparkSession): DataFrame =
  spark.read
    .format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
    .option("header", "false") //reading the headers
    .option("mode", "DROPMALFORMED")
    .option("inferSchema", "true")
    .load(filePath)

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

val projectRoot = "../"
val trainData: DataFrame = loadCSVFile(projectRoot + "src/test/resources/data/adult.data.csv")
val testData: DataFrame = loadCSVFile(projectRoot + "src/test/resources/data/adult.test.txt")
val train = trainData.toDF(columns: _*)
val test = testData.toDF(columns: _*)
val dataframes = List(NamedDataFrame(name = "train", train), NamedDataFrame(name = "test", test))

import features.stats.spark.FeatureStatsGenerator
val generator = new FeatureStatsGenerator(DatasetFeatureStatisticsList())
val proto = generator.protoFromDataFrames(dataframes)

persistProto(proto, base64Encode = false, new File(projectRoot + "src/test/resources/data/stats.pb"))
persistProto(proto, base64Encode = true, new File(projectRoot + "src/test/resources/data/stats.txt"))
