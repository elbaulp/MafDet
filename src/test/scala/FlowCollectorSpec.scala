import modules.flowCollector.FlowCollector
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}
import spray.json.JsonParser

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
      Given an api call response: {"1": [{"actions": ["OUTPUT:CONTROLLER"], "idle_timeout": 0, "cookie": 0, "packet_count": 1212, "hard_timeout": 0, "byte_count": 72720, "duration_sec": 432, "duration_nsec": 903000000, "priority": 65535, "length": 96, "flags": 0, "table_id": 0, "match": {"dl_type": 35020, "dl_dst": "01:80:c2:00:00:0e"}}, {"actions": ["OUTPUT:CONTROLLER"], "idle_timeout": 0, "cookie": 0, "packet_count": 49, "hard_timeout": 0, "byte_count": 3890, "duration_sec": 432, "duration_nsec": 938000000, "priority": 0, "length": 80, "flags": 0, "table_id": 0, "match": {}}]}
      When extracting key: packet_count
      Then a field look up should return: true                                  ${gettingValues.end}
    """

  val stepParser = readAs(".*: (.*)$").and((s: String) => s)
  val jsonExtractor = readAs(".+?: (.*)$").and((s: String) => JsonParser(s).asJsObject)
  val aJsonKey = aString

  val connectTest =
    Scenario("connectTest").
      given(aString).
      given(anInt).
      when(stepParser) { case url :: dpid :: _ => FlowCollector.getSwitchFlows(dpid) }.
      andThen(anInt) { case expected :: actual :: _ => actual.code must_== expected }

  val gettingValues =
    Scenario("Getting Values").
      given(jsonExtractor).
      when(aJsonKey) { case key :: json :: _ => json.fields contains key }.
      andThen() {
        case expected :: exists :: _ =>
          if (expected == "true") exists must_== true
          else exists must_== false
      }
}