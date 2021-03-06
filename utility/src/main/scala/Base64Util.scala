package utility

import scala.io.Source
import org.apache.commons.codec.binary.Base64._
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.io.File
import java.io.FileOutputStream
import scala.xml.pull._
import scala.xml.XML

object Base64Util extends App {
  val xmlFile = args(0)
  val outputLocation = new File(args(1))

  val loadnode = XML.loadFile(xmlFile)
  val base64Str = (loadnode \ "peaks").text
//  val base64Str = "QuCQmEBojzBC4jg8QKmTbkLuUC5BKe9MQwIDAECDzGtDBx4zQEwNwEMIL2pAtucZQwr0vkCmxylDC//UQLwpX0MOI29AJ+muQxQKpECJ2bhDGDFoQQUcV0MeLehAvF0gQyFBgEEu9adDIkQqQNRknEMpHXBByehCQyoS5UBbct1DKxu6QHnqSkMsFddArRmfQy3yQkEzwmJDL08wQOelKkMxKJ1A0hc/QzRL1kEw1c9DNPi4QSiYXEM4NYFAa3vTQzkAakEaQl1DPtz2QJHyqENBB7JBOb0MQ0T9gUGYZE1DRgLQQQmUh0NHMTBBswjEQ0fqLED/enNDSPa6QTRV+UNL/XhAlPpYQ03+yUCbJvZDUQWQQJWr6ENTD7xBCaiuQ1UElEBvd+hDVy+wQYuh8ENYHohB93Q2Q1lAWEGELDlDWwYsQQ/iXkNb0k5ArIkDQ19A8EERZzpDYRAYQM4P3kNiIXhAbzPhQ2LavkA9JfhDZdXaQIpXNkNqBthBMzEDQ2saqkG7NO9Da/2iQlBbPENs7DBBH5k9Q23sfEEsW8hDbwhSQd57XkNyL15A5PGbQ3LkOEEWbFZDdDdcQZtZCkN1FFRBbEAVQ3YLEECu9vJDeBGWQe0LvkN5C2BAPoOqQ3z7OEJPTcxDfgPAQw6bqkN+9uxDA0o0Q4INNEGA36FDgnBKQO+sEEODAwxAYh/oQ4WzhkCr1DBDhgIoQOVCGEOGjCBBpFyqQ4dplUG8NE1DiHIZQDqunUOI6ztArgrsQ4oI70HnEg9DipD/QW/6VkOLjDBAiytMQ4vsXkBnOC9DjfiRQKAQekOOjb1AVlicQ48bmkEIMrxDj5dFQZ5Cx0OP+exBgMbqQ5CEDUJgKlNDkRSTRDXsgkORfJBCDqSHQ5IipkCbakNDkoYZQc0dAUOS4f9BV0mfQ5N4yEJTHXRDlBLFQTZ4YkOVD8NAwYecQ5Xmq0D5ROpDltiwQdicfEOXbapAtz7wQ5gKJUFENktDmPkBQF4QbEOcEcFAj5NcQ50qZEL3XbRDnXu/QzLHWUOeEMtB82OEQ56MS0EUErNDnxcNQTdQd0OhyqlApZ/pQ6XVQUGZ37FDxYz2QHMYQ0PHo61BgftWQ+0E9EB7bmxD8xArQLo79kP5JEdAn8WjRARD4UCLLkFEBhJ2QV5oWEQJXu9AuX68RAmSpkCykWJEC9VlQJ4EaEQNzAVAjXllRA9Vp0DWu4Q="

  // Group decoded-bytes by 4; return an iterator of 4-byte items
  val strDecodedByteGroup = decodeBase64(base64Str) grouped 4
//  val strToFloat = ByteBuffer.wrap(strDecoded).order(ByteOrder.BIG_ENDIAN).getFloat()
  // Unpack each 4-byte array to a float with 32 precision; return an iterator of floats
  val byteToFloat = strDecodedByteGroup map(x => ByteBuffer.wrap(x).order(ByteOrder.BIG_ENDIAN).getFloat())
  // Re-group float numbers into List(m/z, Intensity); return an iterator of such Lists
  val mzIntensity = byteToFloat grouped 2

//  val x = XML.loadString(s)
//  val msLevel = x.attribute("msLevel").toList(0).toString
  val scanNum = (loadnode \@ "num").toList(0).toString
//  val precursorMz = (loadnode \ "precursorMz").text
  val precursorScanNum = (loadnode \ "precursorMz" \@ "precursorScanNum")
  val f = new File(outputLocation, precursorScanNum + "_" + scanNum + ".csv")
  println("writing to: " + f.getAbsolutePath())
  val out = new FileOutputStream(f)
  // Write the (m/z, Intensity) iterator to file
  mzIntensity.foreach(x => out.write(x.mkString("", ",", "\n").getBytes()))
  out.close
}
