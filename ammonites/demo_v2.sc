import $ivy.`com.thesamet.scalapb::scalapb-runtime:0.9.6`

// load the built jar
val jars = ls.rec ! pwd / up / "build" |? { _.last.endsWith(".jar") }
interp.load.cp(jars.head)

interp.load.module(pwd / RelPath("libs/sparkSession.sc"))

@

// -----------------------------------------------------------------------------
// Utility Functions
import features.stats.spark.NamedDataFrame

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

import features.stats.spark.NamedDataFrame


val censusDf = spark.read.parquet("parquet/")
val dataframes = List(NamedDataFrame(name = "census", censusDf))

import features.stats.spark.FeatureStatsGeneratorV2
val generator = new FeatureStatsGeneratorV2()
val proto = generator.protoFromDataFrames(dataframes)
print(proto)

persistProto(proto, base64Encode = true, new File("census_stats.txt"))
