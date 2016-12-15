import modules.flowCollector.FlowCollector
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}

class FlowCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers {
  def is =
    s2"""
     Retrieving values                                                          ${gettingValues.start}
      Given a json response from previous request
      When extracting key: packet_count
      Then a field look up should return: true                                  ${gettingValues.end}
    """

  private[this] val aJsonKey = aString
  private[this] val jsonResponse = readAs(".*").and((_: String) => FlowCollector.getSwitchFlows(1))

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