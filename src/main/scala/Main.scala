import spray.json.JsonParser

import scalaj.http.Http

/*
 * Copyright 2016 Alejandro Alcalde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
  * Created by Alejandro Alcalde <contacto@elbauldelprogramador.com> on 11/7/16.
  */
object Main extends App {
  val logger = org.log4s.getLogger
  logger.info("Starting app")

  val request = Http("http://192.168.56.102:8080/stats/flow/1")

  val responseOne = request.asString

  logger.debug(s"\n\n${JsonParser(responseOne.body).prettyPrint}")
}