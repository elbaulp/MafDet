import scala.concurrent.Await
import scala.concurrent.duration._

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import mafdet.modules.featureextractor._
import mafdet.modules.flowcollector.{FlowCollector, UpdateStatistics}
import mafdet.modules.flowcollector.UpdateStatistics._
import org.json4s._
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}

class FlowCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers {
  def is =
    s2"""
     Retrieving values                                             ${gettingValues.start}
      Given a json response from previous request
      When extracting key: packet_count
      Then a field look up should return: true                     ${gettingValues.end}

     Query Controller with Akka actor                              ${intervals.start}
      Given an akka actor
      When querying the controller through the actor for switch id 1
      Then a vector of size 1 with the statistics must be returned ${intervals.end}

     Extract features using statistics gathered at ten intervals   ${features.start}
      Given a 10 interval value
      When querying the controller 10 times
      Then FeatureExtractor should compute the features            ${features.end}
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
      given().
      when(anInt) {
        case dpid :: _ =>
          val system = ActorSystem("MySystem")
          val actor = system.actorOf(Props[UpdateStatistics])
          implicit val timeout = Timeout(5 seconds)
          val future = actor ? QueryController(dpid)

          Vector(Await.result(future, timeout.duration).asInstanceOf[JValue])
      }.
      andThen(anInt) { case expected :: result :: _ => expected must_== result.size }

  private[this] val features =
    Scenario("Features").
      given(anInt).
      when(anInt) {
        case a :: b :: _ =>
          val system = ActorSystem("MySystem")
          val actor = system.actorOf(Props[UpdateStatistics])
          implicit val timeout = Timeout(5 seconds)
          val q1 = actor ? QueryController(1)

          Await.result(q1, timeout.duration).asInstanceOf[JValue]
      }.
      andThen() { case _ :: b :: _ =>
        val result = FeatureExtractor.getFeatures(b)
        result must contain(allOf(be_>(0.0)))
      }
}
