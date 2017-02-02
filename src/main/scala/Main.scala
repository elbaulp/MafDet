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

import akka.actor.{Actor, Props, ActorSystem}
import scala.concurrent.duration._
import scala.language.postfixOps


/* object MyActor {
 *   case class Greeting(from: String)
 *   case object Goodbye
 * }
 * class MyActor extends Actor with ActorLogging {
 *   import MyActor._
 *   def receive = {
 *     case Greeting(greeter) => log.info(s"I was greeted by $greeter.")
 *     case Goodbye
 *         => log.info("Someone said goodbye to me.")
 *   }
 * } */

/**
 * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 11/7/16.
 */
object Main extends App {
  /* val system = ActorSystem("MySystem")
   * val actor = system.actorOf(Props[UpdateStatistics])
   *
   * import system.dispatcher
   *
   * val cancellable =
   *   system.scheduler.schedule(0 milliseconds,
   *     50 milliseconds,
   *     actor,
   *     "test") */
}
