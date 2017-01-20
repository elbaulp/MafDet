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

package modules.flowcollector

import Constants._
import org.json4s._
import org.json4s.native.JsonMethods._
import org.log4s._
import scalaj.http.Http

/**
 * Retrieve statistics from the controller
 */
object FlowCollector {

  private val logger = getLogger

  /**
   * Query the controller for the given
   *
   * @param sId dpid for the Switch
   *
   * @return List of values
   */
  def queryController(sId: Int): JValue = {
    logger.trace(s"Calling QueryController")

    val json = parse(Http(FlowStats + sId).asString.body)

    json
  }

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
}
