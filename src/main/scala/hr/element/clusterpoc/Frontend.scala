package hr.element.clusterpoc

import akka.actor._

import scala.util.Random

class Frontend extends Actor with ActorLogging {
  var backends = IndexedSeq.empty[ActorRef]

  override def preStart(): Unit = {
    println("Frontend ready!")
  }

  def receive = {
    case job: FactorialJob if backends.isEmpty =>
      sender ! FactorialFailed(job, "No available backends, try again later!")

    case job: FactorialJob =>
      val randomWorker = backends(Random.nextInt(backends.size))
      randomWorker forward job

    case BackendRegistration if !(backends contains sender) =>
      log.info(s"I see a new backend node: $sender")
      context watch sender
      backends = backends :+ sender

    case Terminated(a) =>
      log.info(s"Backend node croaked: $sender")
      backends = backends.filterNot(_ == a)
  }
}
