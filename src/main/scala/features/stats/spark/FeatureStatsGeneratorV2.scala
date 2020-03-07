package features.stats.spark

import featureStatistics.feature_statistics._
import featureStatistics.feature_statistics.StringStatistics.FreqAndValue
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

class FeatureStatsGeneratorV2 {

  val TOTAL = "total"

  /**
   * Creates a feature statistics proto from a set of spark DataFrames.
   *
   * @param namedDataFrames   DataFrame to be analyzed and its name
   * @return The feature statistics proto for the provided tables.
   */
  def protoFromDataFrames(namedDataFrames: List[NamedDataFrame]): DatasetFeatureStatisticsList = {
    val datasets = namedDataFrames.map({ case NamedDataFrame(name, df) => toDatasetFeatureStatistics(name, df) })
    DatasetFeatureStatisticsList().addAllDatasets(datasets)
  }

  def toCommonStatistics(colStats: Map[String, String]): CommonStatistics = {
    val total = colStats(TOTAL).toLong
    val count = colStats("count").toLong
    CommonStatistics().withNumMissing(total - count).withNumNonMissing(count)
  }

  /**
   * All Numerical Features (LongType, FloatType, DoubleType) will be represented with NumericalStatistics
   */
  def toNumericStatistics(colName: String, colStats: Map[String, String]): NumericStatistics = {
    var numStats = NumericStatistics().withCommonStats(toCommonStatistics(colStats))

    colStats.get("mean").foreach(v => numStats = numStats.withMean(v.toDouble))
    colStats.get("max").foreach(v => numStats = numStats.withMax(v.toDouble))
    colStats.get("min").foreach(v => numStats = numStats.withMin(v.toDouble))
    colStats.get("stddev").foreach(v => numStats = numStats.withStdDev(v.toDouble))

    // TODO: .withNumZeros
    // TODO: .withHistograms

    numStats
  }

  /**
   * All Categorical Features (Integer, Boolean, String) will be represented with StringStatistics
   */
  def toStringStatistics(colName: String, colStats: Map[String, String], df: DataFrame): StringStatistics = {
    val spark = df.sqlContext.sparkSession
    import spark.implicits._

    val commonStats = toCommonStatistics(colStats)
    val colDf = df.select(col(colName).cast(StringType).alias(colName)).where(col(colName).isNotNull)
    val valCount = colDf.groupBy(colName).count.as[(String, Long)].collect.sortBy(_._2).reverse
    val freqAndValues = valCount.map({ case (v, c: Long) => FreqAndValue.of(0, v.toString, c.toDouble) })

    StringStatistics().withCommonStats(commonStats).withUnique(valCount.length).addAllTopValues(freqAndValues)
  }

  /**
   * Represent all features statistics in one Dataset
   */
  def toDatasetFeatureStatistics(name: String, dataframe: DataFrame): DatasetFeatureStatistics = {
    // XXX: DataFrame.summary only support NumericType and StringType. collect will discard any type not in recognized here.
    val selectedCols = dataframe.schema.collect { field =>
      field.dataType match {
        case BooleanType                                       => col(field.name).cast(StringType)
        case IntegerType | DoubleType | FloatType | StringType => col(field.name)
      }
    }

    val df = dataframe.select(selectedCols: _*)
    val summary = df.summary()
    val numExamples = df.count()

    val toFeatureNameStatistics = (colName: String, colStats: Map[String, String]) => {
      val featureNameStatistics = FeatureNameStatistics().withName(colName)
      df.schema(colName).dataType match {
        case StringType | IntegerType =>
          val valType = FeatureNameStatistics.Type.STRING
          featureNameStatistics.withStringStats(toStringStatistics(colName, colStats, df)).withType(valType)

        case LongType | DoubleType | FloatType =>
          val valType = FeatureNameStatistics.Type.FLOAT
          featureNameStatistics.withNumStats(toNumericStatistics(colName, colStats)).withType(valType)
      }
    }

    val features = df.columns.map { colName =>
      val colStats = summary.select("summary", colName).collect.collect({ case Row(c: String, v: String) => c -> v }).toMap
      toFeatureNameStatistics(colName, colStats + (TOTAL -> numExamples.toString))
    }

    DatasetFeatureStatistics().withName(name).withNumExamples(numExamples).addAllFeatures(features)
  }
}
