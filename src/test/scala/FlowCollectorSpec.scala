import modules.flowCollector.FlowCollector
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}

class FlowCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers { def is = s2"""

 Given a API route to get flows                                             ${apiCaller.start}
   Given an access to the API URL: http://192.168.56.102:8080/stats/flow/1
   When the API responds, then status code should be: 200                   ${apiCaller.end}
"""

  val anAPIUri = readAs(".*: (.*)$").and((s: String) => s)

  val apiCaller =
    Scenario("apiCaller").
      given(aString).
      when(anAPIUri) {case url :: _ => FlowCollector.getSwitchFlows(1)}.
      andThen(anInt) {case expected :: actual :: _ => actual.code must_== expected}
}