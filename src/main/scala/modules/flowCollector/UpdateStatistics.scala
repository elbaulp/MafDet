package mafdet.modules.flowcollector

import UpdateStatistics._
import akka.actor.Actor

object UpdateStatistics {
  /**
   * Query the controller for the given switch Id
   *
   * @param dpId Switch's Id
   */
  case class QueryController(dpId: Int)
  case object Stop
}

class UpdateStatistics extends Actor with akka.actor.ActorLogging {

  override def preStart() = {
    log.debug("Starting")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = {
    case QueryController(id) =>
      log.info(s"Receiving request to query controller")
      sender ! FlowCollector.getSwitchFlows(id)
    case Stop =>
      log.info(s"Shuting down")
      context stop self
    case x => log.warning("Received unknown message: {}", x)
  }
}
