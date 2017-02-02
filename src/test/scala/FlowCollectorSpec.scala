import mafdet.modules.flowcollector.{FlowCollector, UpdateStatistics}
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}

import akka.actor.{Actor, Props, ActorSystem}
import scala.concurrent.duration._
import scala.language.postfixOps

import akka.testkit.TestActorRef
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.ask

import org.json4s._
import org.json4s.native.JsonMethods._

class FlowCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers {
  def is =
    s2"""
     Retrieving values                              ${gettingValues.start}
      Given a json response from previous request
      When extracting key: packet_count
      Then a field look up should return: true      ${gettingValues.end}

     Query Controller at fixed intervals            ${intervals.start}
      Given a 50 milliseconds interval
      When querying the controller for 10 seconds
      Then the size should be 200                   ${intervals.end}
    """

  private[this] val aJsonKey = aString
  private[this] val jsonResponse = readAs(".*").and((_: String) =>
    FlowCollector.getSwitchFlows(1))

  private[this] val gettingValues =
    Scenario("Getting Values").
      given(jsonResponse).
      when(aJsonKey) { case key :: json :: _ => json.children contains key }.
      andThen() {
        case expected :: exists :: _ =>
          if (expected == "true") exists must_== true
          else exists must_== false
      }

  private[this] val intervals =
    Scenario("Fixed intervals").
      given(anInt).
      when(anInt) { case duration :: interval :: _ =>
        val system = ActorSystem("MySystem")
        val actor = system.actorOf(Props[UpdateStatistics])
        implicit val timeout = Timeout(5 seconds)
        val future = actor ? "test"

        Await.result(future, timeout.duration).asInstanceOf[JValue]
      }.
      andThen(anInt) { case expected :: actual :: _ => expected must_== actual }
}
