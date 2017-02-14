/*
 * MIT License
 *
 * Copyright (c) 2016 Alejandro Alcalde
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mafdet.modules.flowcollector

import scala.concurrent.Future

import akka.actor.{Actor, Props, ActorRef}
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

  /**
   * Create Props for an actor of this type.
   *
   * @return a Props for creating this actor, which can then be further configured
   * (e.g. calling ‘.withDispatcher()‘ on it)
   *
   */
  def props(fActor: ActorRef): Props = Props(new UpdateStatistics(fActor))
}

class UpdateStatistics(fActor: ActorRef) extends Actor with akka.actor.ActorLogging {
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
    case json: JValue =>
      log.info("Getting json response, computing features...")
      val features = FeatureExtractor.getFeatures(json)
      log.debug(s"Features: $features")
      fActor ! features
    case x =>
      log.warning("Received unknown message: {}", x)
  }
}
