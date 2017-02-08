package mafdet.modules.flowcollector

import scala.concurrent.Future

import akka.actor.Actor
import mafdet.modules.featureextractor.FeatureExtractor
import org.json4s._

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
  import UpdateStatistics._

  override def preStart() = {
    log.debug("Starting")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = {

    case QueryController(id) =>
      import context.dispatcher
      log.info(s"Receiving request to query controller")
      Future { FlowCollector.getSwitchFlows(1) } onComplete {
        f => self ! f.get
      }
    case Stop =>
      log.info(s"Shuting down")
      context stop self
    case json:JValue =>
      log.info("Getting json response, computing features...")
      val features = FeatureExtractor.getFeatures(json)
      log.debug(s"Features: $features")
      sender ! features
    case x => log.warning("Received unknown message: {}", x)
  }
}
