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

import modules.flowCollector.FlowCollector
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
    Compute median of Average Packets per Flow when # flows is odd  ${apfOdd.start}
      Given a odd list of packets per flow: 5, 10, 15, 20, 25
      When computing APf
      Then APf should be: 15                                        ${apfOdd.end}

    Compute median of Average Packets per Flow when # flows is even ${apfEven.start}
      Given a even list of packets per flow: 5, 100, 10, 15, 20, 25
      When computing APf
      Then APf should be: 12                                        ${apfEven.end}

    Collect number of flows and packets for tuple 1 (Avg pkt count) ${apf.start}
      Given flow stats url: /stats/flow/
      When getting flow stats for a switch with id: 1
      Then APf should be > 0
      and the number of flow entries should be > 0                  ${apf.end}

    Compute median Average of Bytes per Flow                        ${abf.start}
      Given a list of number of bytes per flow: 5, 10, 15, 20, 25
      When computing ABf
      Then ABf should be: 15                                        ${abf.end}

    Compute median Average of Bytes per Flow from controller        ${abfController.start}
      Given a flow stats url: /stats/flow/
      When getting flow stats for a switch with id: 1
      Then APf should be > 0
      and the number of flow entries should be > 0                  ${abfController.end}

    """

  val anIntList = groupAs("\\d+").and((a: Seq[String]) => a map(_.toInt))

  private val apf =
    Scenario("APf tuple").
      given(aString).
      when(anInt) { case dpid :: _ => FlowCollector.APf(dpid) }.
      andThen(anInt){ case expected :: response :: _ => response.toInt must be_>=(expected)}.
      andThen(anInt){ case expected :: response :: _ => response.toInt must be_>=(expected)}

  private val apfOdd =
    Scenario("APf with odd number").
      given(anIntList).
      when() {case _ :: pkts :: _ => FlowCollector.APf(pkts)}.
      andThen(anInt){ case expected :: result :: _ => expected must_== result}

  private val apfEven =
    apfOdd.withTitle("APF with even number")

  private val abfController =
    Scenario("ABf for controller").
      given(aString).
      when(anInt) { case dpid :: _ => FlowCollector.ABf(dpid) }.
      andThen(anInt) { case expected :: response :: _ => response.toInt must be_>=(expected) }.
      andThen(anInt) { case expected :: response :: _ => response.toInt must be_>=(expected) }

  private val abf =
    Scenario("ABf Tuple").
      given(anIntList).
      when() {case _ :: bytes :: _ => FlowCollector.ABf(bytes) }.
      andThen(anInt){ case expected :: result :: _ => expected must_== result }
}
