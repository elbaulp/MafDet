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

package mafdet.modules.featureextractor

import scala.annotation.switch

import mafdet.modules.flowcollector.Constants
import org.json4s._
import org.log4s._

/**
 * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 11/7/16.
 */
object FeatureExtractor {

  private val logger = getLogger

  /** Query the controller at `interval` periods of time (in ms) */
  val interval = 1000.0

  /**
   * Get Average of Packets per flow (APf)
   *
   * @param packets Packets per flow
   * @return The average packets per flow for the given switch
   */
  def APf(packets: Seq[BigInt]): BigInt = computeMedian(packets)
  def APf(flows: JValue): BigInt = computeMedian(flows \\ Constants.PktCountKey \\ classOf[JInt])

  /**
   * Get the Average of Bytes per Flow (ABf)
   *
   * @param bytes Bytes per flow
   * @return The average of bytes per flow for the given switch
   */
  def ABf(bytes: Seq[BigInt]): BigInt = computeMedian(bytes)
  def ABf(flows: JValue): BigInt = computeMedian(flows \\ Constants.ByteCountKey \\ classOf[JInt])

  /**
   * Get median time a flow is stored on the flow table.
   *
   * @param duration of flow
   * @return The median time a flow is kept in the flow table
   */
  def ADf(duration: Seq[BigInt]): BigInt = computeMedian(duration)
  def ADf(flows: JValue): BigInt = computeMedian(flows \\ Constants.DurationSec \\ classOf[JInt])

  /**
   * Get the number of Pair-Flows
   *
   * @param flows Flows in the switch
   * @return Number of Pair-Flows
   */
  def PPf(flows: JValue): Double = {
    implicit val formats = DefaultFormats
    computePairFlows((flows \\ Constants.MatchKey \ Constants.MatchKey).extract[List[OFMatch]])
  }
  def PPf(matchs: Seq[OFMatch]): Double = computePairFlows(matchs)

  /**
   * Growth of Single-Flows.
   *
   * @param
   * @return Growth of single flows in interval
   */
  def GSf(flows: JValue): Double = {
    val pairsFlows = PPf(flows)
    val nFlows = (flows \\ Constants.ByteCountKey \ classOf[JInt]).size

    (nFlows - (2 * pairsFlows)) / interval
  }

  /**
    * Growth of Different ports
    *
    * @param
    * @return Growth of different ports in interval
    */
  def GDp(flows: JValue): Double = {
    val uniquePorts = (flows \\ Constants.TcpDstKey \ classOf[JInt]).distinct.size

    uniquePorts / interval
  }

  /**
   * Percentage of Pair-Flows (PPf)
   *
   * @param total Total number of flows in table
   * @param pairs How many pair flows
   *
   * @return Percentage of pairs flows
   */
  private[this] def computePairFlows(flows: Seq[OFMatch]): Double = {
    logger.trace("Calling computePairFlows")

    val table = flows./:(Map.empty[String, Int]) {
      case (m, f) =>
        val key = f.nw_src + f.nw_dst + f.dl_type
        val inverseKey = f.nw_dst + f.nw_src + f.dl_type
        val haspair = m get inverseKey match {
          case Some(v) => v + 1
          case None => 0
        }
        m + (key -> haspair)
    }

    val pairs = table.filter(_._2 > 0)

    logger.debug(s"Pairflows: ${pairs.size}, flows: ${flows.size}")

    2.0 * pairs.size / flows.size
  }

  /**
   * Compute the median value for a given sequence of packets per flow
   * @param pkt Sequence of packets per flow to compute the median
   * @return The median
   */
  private[this] def computeMedian(pkt: Seq[BigInt]) = {
    logger.trace("Calling computeMedian")
    val pktSorted = pkt.sorted
    val nflows = pktSorted.size
    (nflows & 1: @switch) match {
      case 0 => (pktSorted((nflows - 1) / 2) + pktSorted(nflows / 2)) / 2
      case 1 => pktSorted(nflows / 2)
    }
  }
}
