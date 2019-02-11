/**
  * Created by zhj on 5/5/17.
  */


import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

import org.apache.spark.mllib.recommendation.ALS
import org.apache.spark.mllib.recommendation.Rating
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel

object FinalProj {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("ALS-Book Recommendation").setMaster("local[4]")
    val sc = new SparkContext(conf)

    //Training data for the recommendation model
    val rawRatings = sc.textFile("file:///home/zhj/IdeaProjects/BigDataFinalData/ratings_Books20M.csv").filter(!_.isEmpty).map(line=>line.split(","))
    val ratings = rawRatings.map(s=>(s(0),s(1),s(2)))

    ////serialize the user_id and Item_id
    val users = ratings.map(_._1).distinct.sortBy(x => x).zipWithIndex().collectAsMap()
	
    val books = ratings.map(_._2).distinct.sortBy(x => x).zipWithIndex().collectAsMap()

    ////training data for ALS model
    val Array(trainingData,testingData) = ratings.randomSplit(Array(0.8, 0.2))
    
    val data_rating_Training = trainingData.map(line => Rating(users(line._1).toInt, books(line._2).toInt, line._3.toDouble))
    val data_rating_testing = testingData.map(line => Rating(users(line._1).toInt, books(line._2).toInt, line._3.toDouble))

    //ALS train
    val rank= 70	
	val iter =14
    val model = ALS.train(data_rating_Training, rank, iter, 0.05)
    
    //testing dataset for prediction
    val testingFit = data_rating_testing.map{case Rating(user_id, book_id, ratings) => (user_id, book_id)}	
    
    //prediction
    val predictions = model.predict(testingFit).map { case Rating(user, movie, rate) =>
      ((user, movie), rate)}
    val ratesAndPreds = data_rating_testing.map { case Rating(user, product, rate) => ((user, product), rate)}.join(predictions)
    val rmse  = math.sqrt(ratesAndPreds.map { case ((user, product), (r1, r2)) => val err =(r1 - r2)
      err*err }.mean())
    println("The rmse of the ALS model = " + rmse)
	
	// Save and load model
	model.save(sc, "target/tmp/myCollaborativeFilter")
    val sameModel = MatrixFactorizationModel.load(sc, "target/tmp/myCollaborativeFilter")



  }


}

