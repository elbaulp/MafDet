package mafdet.modules.flowcollector

import akka.actor.Actor

import scala.language.postfixOps

class UpdateStatistics extends Actor with akka.actor.ActorLogging {

  override def preStart() = {
    log.debug("Starting")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = {
    case "test" =>
      log.info(s"Receiving request to query controller")
      sender ! FlowCollector.getSwitchFlows(1)
    case "shut" =>
      log.info(s"Shuting down")
      context stop self
    case x => log.warning("Received unknown message: {}", x)
  }
}
