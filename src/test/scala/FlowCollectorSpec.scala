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
      Given an access to the API URL: http://192.168.56.102:8080/stats/flow/
      When getting flow stats for a switch with id: 1
      Then status code should be: 200                                          ${connectTest.end}

     Retrieving values                                                          ${gettingValues.start}
      Given a json response from previous request
      When extracting key: packet_count
      Then a field look up should return: true                                  ${gettingValues.end}
    """

  val stepParser = readAs(".*: (.*)$").and((s: String) => s)
  val jsonExtractor = readAs(".+?: (.*)$").and((s: String) => JsonParser(s).asJsObject)
  val aJsonKey = aString
  val jsonResponse = readAs(".*").and((_: String) => FlowCollector.getSwitchFlows(1).body.parseJson.asJsObject)

  val connectTest =
    Scenario("connectTest").
      given(aString).
      given(anInt).
      when(stepParser) { case url :: dpid :: _ => FlowCollector.getSwitchFlows(dpid) }.
      andThen(anInt) { case expected :: actual :: _ => actual.code must_== expected }

  val gettingValues =
    Scenario("Getting Values").
      given(jsonResponse).
      when(aJsonKey) { case key :: json :: _ => json.fields contains key }.
      andThen() {
        case expected :: exists :: _ =>
          if (expected == "true") exists must_== true
          else exists must_== false
      }
}