package mafdet.modules.featureextractor

import akka.actor.{ Actor, Props }

object FeatureActor {

  def props: Props = Props[FeatureActor]
}

class FeatureActor extends Actor with akka.actor.ActorLogging {
  import FeatureActor._

  override def preStart() = {
    log.debug("Starting")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = {
    case x =>
      log.warning("Received unknown message: {}", x)
  }
}
