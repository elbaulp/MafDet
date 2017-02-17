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

import mafdet.modules.featureextractor.{FeatureExtractor, OFMatch}
import mafdet.modules.flowcollector.FlowCollector
import org.specs2.Specification
import org.specs2.specification.script.{GWT, StandardRegexStepParsers}

/**
  * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 12/15/16.
  */
class FeatureCollectorSpec extends Specification
  with GWT
  with StandardRegexStepParsers {
  def is =
    s2"""
    COMPUTE MEDIAN OF AVERAGE PACKETS PER FLOW WHEN # FLOWS IS ODD          ${apfOdd.start}
      Given a odd list of packets per flow: 5, 10, 15, 20, 25
      When computing APf
      Then APf should be: 15                                                ${apfOdd.end}

    COMPUTE MEDIAN OF AVERAGE PACKETS PER FLOW WHEN # FLOWS IS EVEN         ${apfEven.start}
      Given a even list of packets per flow: 5, 100, 10, 15, 20, 25
      When computing APf
      Then APf should be: 17                                                ${apfEven.end}

    COLLECT NUMBER OF FLOWS AND PACKETS FOR TUPLE 1 (AVG PKT COUNT)         ${apf.start}
      Given flow stats url: /stats/flow/
      When getting flow stats for a switch
      Then APf should be > 0
      and the number of flow entries should be > 0                          ${apf.end}

    COMPUTE MEDIAN AVERAGE OF BYTES PER FLOW                                ${abf.start}
      Given a list of number of bytes per flow: 5, 10, 15, 20, 25
      When computing ABf
      Then ABf should be: 15                                                ${abf.end}

    COMPUTE MEDIAN AVERAGE OF BYTES PER FLOW FROM CONTROLLER                ${abfController.start}
      Given a flow stats url: /stats/flow/
      When getting flow stats for a switch with id: 1
      Then APf should be > 0
      and the number of flow entries should be > 0                          ${abfController.end}

    COMPUTE MEDIAN FOR THE DURATION OF TIME A FLOW SPENDS IN THE FLOW TABLE ${adf.start}
      Given a flow stats url: /stats/flow
      When getting flow stats fro a switch with id: 1
      Then ADf should be > 0                                                ${adf.end}

    COMPUTE PERCENTAGE OF PAIR-FLOWS                                        ${ppf.start}
      Given a flow stats url: /stats/flow
      When getting flow stats for a switch with id: 1
      Then PPf should be > 0.6                                              ${ppf.end}

    COMPUTE PERCENTAGE OF PAIR-FLOWS                                        ${ppf2.start}
      Given a flow with nw_src: 10.0.0.1 and nw_dst: 10.0.0.2
      Given a flow with nw_src: 10.0.0.2 and nw_dst: 10.0.0.1
      Given a flow with nw_src: 10.0.0.3 and nw_dst: 10.0.0.6
      Given a flow with nw_src: 10.0.0.4 and nw_dst: 10.0.0.5
      Given a flow with nw_src: 10.0.0.5 and nw_dst: 10.0.0.4
      Given a flow with nw_src: 10.0.0.6 and nw_dst: 10.0.0.7
      When computing PPf
      Then PPf should be 0.6666666666666666                                 ${ppf2.end}

    COMPUTE GROWTH OF SINGLE-FLOWS                                          ${gsf.start}
      Given a flow stats url: /stats/flow
      When getting flow stats for a switch with id: 1
      Then gsf should be < 0.1                                              ${gsf.end}

    COMPUTE GROWTH OF DIFFERENT PORTS                                       ${gdp.start}
      Given a flow stats url: /stats/flow
      When getting flow stats for a switch with id: 1
      Then gdp should be < 0.1                                              ${gdp.end}
   """

  val anIntList = groupAs("\\d+").and((a: Seq[String]) => a map(_.toInt))
  val myD = groupAs("\\d+\\.\\d+").and((s: String) => s.toDouble)
  val anIp = groupAs("\\d+\\.\\d+\\.\\d+\\.\\d+").and((src:String, dst:String) =>
    OFMatch(nw_src=src, nw_dst=dst))

  val switchStatistics = FlowCollector.getSwitchFlows(1)

  private val apf =
    Scenario("APf tuple").
      when() { case dpid :: _ => FeatureExtractor.APf(switchStatistics) }.
      andThen(anInt){ case expected :: response :: _ => response.toInt must be_>=(expected)}.
      andThen(anInt){ case expected :: response :: _ => response.toInt must be_>=(expected)}

  private val apfOdd =
    Scenario("APf with odd number").
      given(anIntList).
      when() {case _ :: pkts :: _ => FeatureExtractor.APf(pkts map(BigInt(_)))}.
      andThen(anInt){ case expected :: result :: _ => expected must_== result}

  private val apfEven =
    apfOdd.withTitle("APF with even number")

  private val abfController =
    Scenario("ABf for controller").
      given(aString).
      when(anInt) { case dpid :: _ => FeatureExtractor.ABf(switchStatistics) }.
      andThen(anInt) { case expected :: response :: _ => response.toInt must be_>=(expected) }.
      andThen(anInt) { case expected :: response :: _ => response.toInt must be_>=(expected) }

  private val abf =
    Scenario("ABf Tuple").
      given(anIntList).
      when() {case _ :: bytes :: _ => FeatureExtractor.ABf(bytes map(BigInt(_))) }.
      andThen(anInt){ case expected :: result :: _ => expected must_== result }

  private val adf =
    Scenario("ADf tuple").
      given(aString).
      when(anInt) { case dpid :: _ => FeatureExtractor.ADf(switchStatistics) }.
      andThen(anInt) { case expected :: result :: _ => result.toInt must be_>=(expected) }

  private val ppf =
    Scenario("PPf tuple").
      given(aString).
      when(anInt) {case dpid :: _ => FeatureExtractor.PPf(switchStatistics)}.
      andThen(myD){ case expected :: result :: _ => result must be>= expected }

  private val ppf2 =
    Scenario("PPf Tuple").
      given(anIp).
      given(anIp).
      given(anIp).
      given(anIp).
      given(anIp).
      given(anIp).
      when() {case _ :: f1 :: f2 :: f3 :: f4 :: f5 :: f6 :: _ =>
        FeatureExtractor.PPf(Seq(f1,f2,f3, f4, f5, f6))}.
      andThen(myD){ case expected :: result :: _ => result must_==expected }

  private val gsf =
    Scenario("GSf tuple").
      given(aString).
      when(anInt) {case dpid :: _ => FeatureExtractor.GSf(switchStatistics)}.
      andThen(myD){ case expected :: result :: _ => result must be<= expected }

  private val gdp =
    Scenario("GDP tuple").
      given(aString).
      when(anInt) {case dpid :: _ => FeatureExtractor.GDp(switchStatistics)}.
      andThen(myD){ case expected :: result :: _ => result must be<= expected }
}
