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
import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import mafdet.modules.featureextractor.FeatureExtractor
import org.json4s._

object UpdateStatistics {
  /**
   * Query the controller for the given switch Id
   *
   * @param dpId Switch's Id
    */
  final case class Feature(val value: Vector[Double]) extends AnyVal
  case class QueryController(dpId: Int)
  case object Start
  case object Stop

  /**
   * Create Props for an actor of this type.
   *
   * @return a Props for creating this actor, which can then be further configured
   * (e.g. calling ‘.withDispatcher()‘ on it)
   *
   */
  def props: Props = Props[UpdateStatistics]
}

class UpdateStatistics extends Actor with ActorLogging {
  import UpdateStatistics._

  var featureListener: Option[ActorRef] = None
  import context.dispatcher

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = LoggingReceive {

    case Start if featureListener.isEmpty =>
      featureListener = Some(sender())
      context.system.scheduler.schedule(Duration.Zero, 1 second, self, QueryController(1))

    case QueryController(id) =>
      log.info(s"Receiving request to query controller")
      Future { FlowCollector.getSwitchFlows(1) } onComplete {
        f => self ! f.get
      }
    case Stop =>
      log.info(s"Shuting down")
      context stop self
    case json: JValue =>
      log.info("Getting json response, computing features...")
      val features = Feature(FeatureExtractor.getFeatures(json))
      log.debug(s"Features: $features")
      featureListener.get ! features
    case x =>
      log.warning("Received unknown message: {}", x)
  }
}
