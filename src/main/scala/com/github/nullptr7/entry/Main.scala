package com.github.nullptr7.entry

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.stream.{Materializer, SystemMaterializer}

import java.io.File
import scala.concurrent.duration.DurationInt
import scala.io.BufferedSource
import scala.language.postfixOps

object Main extends App {

  private implicit val system      : ActorSystem  = ActorSystem("Main")
  private implicit val materializer: Materializer = SystemMaterializer(system).materializer

  import system.dispatcher

  system.scheduler.scheduleOnce(5 minutes)(materializer.shutdown())

  case class Data(productId: Int, availableIn: String)

  private val toDataModel: Flow[String, Data, NotUsed] = Flow[String].map(_.split(",", -1))
                                                                     .map(x => Data(x(0).toInt, x(1)))

  private val grouping = Flow[Data].fold(Map.empty[Int, Set[String]]) { (map, e) =>
    if (map.contains(e.productId)) map ++ Map(e.productId -> map(e.productId).+(e.availableIn))
    else map ++ Map(e.productId -> Set(e.availableIn))
  }

  private val inSource: Source[String, NotUsed] = Source.fromIterator(() => {
    source._2.close()
    source._1
  })

  private val printing = Sink.foreach[Map[Int, Set[String]]] { x =>
    x.map(key => s"${key._1} -> [${key._2.mkString(",")}]")
     .foreach(println)
  }

  inSource.via(toDataModel)
          .via(grouping)
          .to(printing)
          .run()

  private def source: (Iterator[String], BufferedSource) = {
    Generator.generateDummyRecords()
    val bufferedSource: BufferedSource   = io.Source.fromFile(new File("src/main/resources/country.csv"))
    val iterable      : Iterator[String] = bufferedSource.getLines()
    (iterable, bufferedSource)
  }

}
