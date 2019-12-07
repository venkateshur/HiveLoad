package com.hiveload

import java.time.LocalDateTime

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.slf4j.LoggerFactory

object CsvToHiveLoadExternal extends App {

  lazy private val logger = LoggerFactory.getLogger(this.getClass.getName)

  logger.info("Application started @ : " + LocalDateTime.now.toString)

  try {
    val inputPath = args(0)
    val outputPath = args(1)
    val delimiter = args(2)

    val sparkSession = initSparkSession
    val inputDf = readCsv(sparkSession, inputPath, delimiter)
    val filterNullDf = inputDf.na.fill(Map("city" -> "UNKNOWN", "sq__ft" -> 0))

    writeToParquet(filterNullDf, outputPath)
  } catch {
    case e: Exception => logger.error(s"Application stopped with error: $e")
      throw e
  }
  def readCsv(spark: SparkSession, inputPath: String, delimiter: String) = {

    logger.info("Reading the input from the path: " + inputPath)

    spark.read.option("delimiter" , delimiter).option("header", "true").csv(inputPath)
  }

  def writeToParquet(filteredDf: DataFrame, outputPath: String): Unit = {

    logger.info("Writing output to the path: " + outputPath)

    filteredDf.write.mode("overwrite").option("header", "true").partitionBy("city").parquet(outputPath)
  }

  private def initSparkSession: SparkSession = {
    SparkSession.builder().appName("loading csv to hive external").getOrCreate()
  }

}
