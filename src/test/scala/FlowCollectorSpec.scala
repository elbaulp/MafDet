import modules.flowCollector.FlowCollector
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}
import spray.json._

class FlowCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers {
  def is =
    s2"""

     Given a API route to get flows                                             ${connectTest.start}
      Given an access to the API URL: /stats/flow/
      When getting flow stats for a switch with id: 1
      Then status code should be: 200                                          ${connectTest.end}

     Retrieving values                                                          ${gettingValues.start}
      Given a json response from previous request
      When extracting key: packet_count
      Then a field look up should return: true                                  ${gettingValues.end}
    """

  private[this] val aJsonKey = aString
  private[this] val jsonResponse = readAs(".*").and((_: String) =>
    FlowCollector.getSwitchFlows(1).body.parseJson.asJsObject)

  private[this] val connectTest =
    Scenario("connectTest").
      given(aString).
      when(anInt) { case dpid :: _ => FlowCollector.getSwitchFlows(dpid) }.
      andThen(anInt) { case expected :: actual :: _ => actual.code must_== expected }

  private[this] val gettingValues =
    Scenario("Getting Values").
      given(jsonResponse).
      when(aJsonKey) { case key :: json :: _ => json.fields contains key }.
      andThen() {
        case expected :: exists :: _ =>
          if (expected == "true") exists must_== true
          else exists must_== false
      }
}