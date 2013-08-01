package com.romix.scala.serialization.kryo

import java.lang.reflect.Constructor
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.common.Empty
import net.liftweb.common.Failure
import net.liftweb.common.Loggable

/**
 * *
 * This module provides helper classes for serialization of Box
 *
 * @author ayush
 *
 */

class LiftBoxSerializer extends Serializer[Box[_]] with Loggable {
  locally {
    setImmutable(true)
  }

  override def write(kryo: Kryo, output: Output, obj: Box[_]) = {
    obj match {
      case Full(x) =>
        output.writeBoolean(true)
        kryo.writeClassAndObject(output, x)
      case Empty =>
        output.writeBoolean(false)
      case _: Failure => logger.error("Error: Problem in serializing Box")
    }

  }

  override def read(kryo: Kryo, input: Input, typ: Class[Box[_]]): Box[_] = {
    if (input.readBoolean()) {
      val refResolver = kryo.getReferenceResolver
      val refId = refResolver.nextReadId(typ)
      kryo.reference(Empty)

      val inner = kryo.readClassAndObject(input)
      val res = Full(inner)
      refResolver.addReadObject(refId, res)
      res
    } else {
      Empty
    }
  }
}