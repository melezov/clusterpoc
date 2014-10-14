package hr.element.clusterpoc

import akka.actor._
import akka.cluster._
import ClusterEvent._
import scala.concurrent._

object Calculator {
  private def accumulate(ordinal: Int, product: BigInt): BigInt =
    ordinal match {
      case x if x < 1 =>
        product

      case _ =>
        accumulate(ordinal - 1, product * ordinal)
    }

  def apply(ordinal: Int) = accumulate(ordinal, 1)
}

class Backend
    extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
    println("Backend ready!")
  }

  override def postStop(): Unit =
    cluster.unsubscribe(self)

  private implicit val ec = Main.threadPool

  def receive = {
    case FactorialJob(ordinal) =>
      val startAt = System.currentTimeMillis
      val origin = sender

      Future {
        log.info(s"Processing request from $origin ($ordinal) ...")

        val result = Calculator(ordinal)
        val endAt = System.currentTimeMillis

        log.info(s"Done processing request from $origin ($ordinal)!")
        origin ! FactorialResult(result, endAt - startAt)
      }

    case state: CurrentClusterState =>
      state.members filter (_.status == MemberStatus.Up) foreach register

    case MemberUp(m) =>
      register(m)
  }

  def register(member: Member): Unit =
    if (member hasRole "frontend") {
      log.info("Registering backend to: " + member)
      context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
    }
}
