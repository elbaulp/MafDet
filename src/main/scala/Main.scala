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

package mafdet

import akka.actor.ActorDSL._
import scala.concurrent.duration._
import scala.language.postfixOps

import akka.actor.ActorSystem
import mafdet.modules.flowcollector.UpdateStatistics
import mafdet.modules.featureextractor.FeatureActor
import mafdet.modules.flowcollector.UpdateStatistics._

/**
 * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 11/7/16.
 */
object Main extends App {

  implicit val system = ActorSystem("MafDet")
  val fActor = system.actorOf(FeatureActor.props, "FeatureActor")
  val statsCollectorActor = system.actorOf(UpdateStatistics.props(fActor), "UpdateStatisticsActor")

  import system.dispatcher

  val cancellable =
    system.scheduler.schedule(0 milliseconds,
      5 seconds,
      statsCollectorActor,
      QueryController(1))

  //cancellable.cancel()
  //system.terminate()
}
