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

package mafdet.modules.featureextractor

import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor._
import akka.event.LoggingReceive
import mafdet.modules.flowcollector.UpdateStatistics

object FeatureActor {
  def props: Props = Props[FeatureActor]
}

class FeatureActor extends Actor with ActorLogging {
  import UpdateStatistics._

  // If we don’t get any progress within 15 seconds then the service is unavailable
  context.setReceiveTimeout(15 seconds)

  override def preRestart(reason: Throwable, message: Option[Any]): Unit =
    log.error(reason, "Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))

  def receive = LoggingReceive {
    case Feature(a) =>
      log.info("Got feature {}", a)
    case Stop =>
      log.info("Receive Stop message, shutting down")
      context.system.terminate()
    case ReceiveTimeout =>
      log.error("Shutting down due to unavailable service")
      context.system.terminate()
    case x =>
      log.warning("Received unknown message: {}", x)
  }
}
