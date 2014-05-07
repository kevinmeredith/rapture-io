/**********************************************************************************************\
* Rapture I/O Library                                                                          *
* Version 0.10.0                                                                               *
*                                                                                              *
* The primary distribution site is                                                             *
*                                                                                              *
*   http://rapture.io/                                                                         *
*                                                                                              *
* Copyright 2010-2014 Jon Pretty, Propensive Ltd.                                              *
*                                                                                              *
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file    *
* except in compliance with the License. You may obtain a copy of the License at               *
*                                                                                              *
*   http://www.apache.org/licenses/LICENSE-2.0                                                 *
*                                                                                              *
* Unless required by applicable law or agreed to in writing, software distributed under the    *
* License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,    *
* either express or implied. See the License for the specific language governing permissions   *
* and limitations under the License.                                                           *
\**********************************************************************************************/
package rapture.io
import rapture.core._

object Copyable {
  case class Summary(streamed: Option[Long]) {
    override def toString = streamed match {
      case None => "copied file"
      case Some(b) => s"streamed $b bytes"
    }
  }

  class Capability[FromType](from: FromType) {
    def copyTo[ToType](to: ToType)(implicit rts: Rts[IoMethods],
        copyable: Copyable[FromType, ToType]): rts.Wrap[Summary, Exception] =
      rts.wrap(?[Copyable[FromType, ToType]].copy(from, to))
  }
}

trait Copyable[FromType, ToType] {
  def copy(from: FromType, to: ToType): Copyable.Summary
}

object Movable {
  case class Summary(streamed: Option[Long]) {
    override def toString = streamed match {
      case None => "moved file"
      case Some(b) => s"streamed, deleted $b bytes"
    }
  }

  class Capability[FromType](from: FromType) {
    def moveTo[ToType](to: ToType)(implicit rts: Rts[IoMethods],
        movable: Movable[FromType, ToType]): rts.Wrap[Summary, Exception] =
      rts.wrap(?[Movable[FromType, ToType]].move(from, to))
  }
}

trait Movable[FromType, ToType] {
  def move(from: FromType, to: ToType): Movable.Summary
}

object Sizable {
  class Capability[Res](res: Res) {
    /** Returns the size in bytes of this resource */
    def size(implicit rts: Rts[IoMethods], sizable: Sizable[Res]): rts.Wrap[Long, Exception] =
      rts wrap sizable.size(res)
  }
}

trait Sizable[Res] {
  type ExceptionType <: Exception
  /** Returns the size in bytes of the specified resource */
  def size(res: Res): Long
}

