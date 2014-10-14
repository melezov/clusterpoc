package hr.element.clusterpoc

import akka.actor._
import akka.pattern._
import akka.util.Timeout
import scala.concurrent._
import scala.concurrent.duration._

import scala.util._

object Main extends App {
  lazy val system = ActorSystem("clusterpoc")

  lazy val threadPool = ExecutionContext.fromExecutor(
    java.util.concurrent.Executors.newCachedThreadPool
  )

  args match {
    case Array(role, port) =>
      sys.props("akka.cluster.roles.1") = role
      sys.props("akka.remote.netty.tcp.port") = port

      role match {
        case "frontend" =>
          val frontend = system.actorOf(Props[Frontend], role)

          while (true) {
            val count = Random.nextInt(100)
            val ordinal = Random.nextInt(1000)
            println(s"Press enter to send $count request for $ordinal! ...")
            io.StdIn.readLine()

            implicit val timeout = Timeout(30 seconds)
            implicit val ec = threadPool

            for (i <- 1 to count) Future {
              val result = frontend ? FactorialJob(ordinal)

              Await.result(result, timeout.duration) match {
                case FactorialResult(result, tookMs) =>
                  val chars = result.toString.take(3) + "..." + result.toString.takeRight(3)
                  println(s"Got result: $chars (took $tookMs ms)")

                case FactorialFailed(_, reason) =>
                  println(reason)
              }
            }
          }

        case "backend" =>
          system.actorOf(Props[Backend], role)

        case _ => sys.error(s"Unknown role: $role")
      }

    case _ =>
      sys.error("Invalid arguments ...")
  }
}
