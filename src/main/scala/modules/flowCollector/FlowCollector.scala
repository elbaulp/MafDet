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

package modules.flowCollector

import modules.flowCollector.Constants._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.log4s._
import scala.annotation.switch

import scalaj.http.Http

/**
 * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 11/7/16.
 */
object FlowCollector {
  private val logger = getLogger

  /**
    * Get all flow statistics for the given switch ID
    *
    * @param sId dpid for the Switch
    * @return The Http response for the API call
    */
  def getSwitchFlows(sId: Int): JValue = {
    logger.trace(s"Calling getSwtichFlows for ${FlowStats + sId}")
    parse(Http(FlowStats + sId).asString.body)
  }

  /**
    * Query the controller for the given
    *
    * @param sId dpid for the Switch
    * @param key What json key to retrieve
    *
    * @return List of values
    */
  private[this] def queryController(sId: Int, key: String) = {
    logger.trace(s"Calling QueryController")

    val json = parse(Http(FlowStats + sId).asString.body)
    val nFlows = json.children.head.children.size
    val values = (json \\ key \\ classOf[JInt]).sorted

    logger.debug(s"#(flows): $nFlows, #(pkt): $values")

    values
  }

  /**
    * Get Average of Packets per flow (APf)
    *
    * @param sId switch's dpid
    * @return The average packets per flow for the given switch
    */
  def APf(sId: Int): BigInt = {
    logger.trace(s"Calling APf for ${FlowStats + sId}")

    val pktCount = queryController(sId, Constants.PktCountKey)
    computeMedian(pktCount)
  }
  def APf(packets: Seq[Int]): BigInt = computeMedian(packets map (BigInt(_)))

  /**
    * Get the Average of Bytes per Flow (ABf)
    *
    * @param sId switch's dpid
    * @return The average of bytes per flow for the given switch
    */
  def ABf(sId: Int): BigInt = {
    logger.trace(s"Calling ABf for ${FlowStats + sId}")

    val byteCount = queryController(sId, Constants.ByteCountKey)
    computeMedian(byteCount)
  }
  def ABf(bytes: Seq[Int]): BigInt = computeMedian(bytes map (BigInt(_)))

  /**
    * Get median time a flow is stored on the flow table.
    *
    * @param sId switch's dpid
    * @return The median time a flow is kept in the flow table
    */
  def ADf(sId: Int): BigInt = {
    logger.trace(s"Calling ADf for ${FlowStats + sId}")

    val duration = queryController(sId, Constants.DurationSec)
    computeMedian(duration)
  }
  def ADf(duration: Seq[Int]): BigInt = computeMedian(duration map (BigInt(_)))

  /**
    * Compute the median value for a given sequence of packets per flow
    * @param pkt Sequence of packets per flow to compute the median
    * @return The median
    */
  private[this] def computeMedian(pkt: Seq[BigInt]) = {
    logger.trace("Calling computeMedian")
    val nflows = pkt.size
      (nflows & 1: @switch) match {
      case 0 => (pkt((nflows - 1) / 2) + pkt(nflows / 2)) / 2
      case 1 => pkt(nflows / 2)
    }
  }
}
