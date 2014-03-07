/**
 * Created by Karolis Labrencis on 3/1/14.
 * Menu class for Menu Application
 */

package lt.labrencis.menu

import scala.io.Source
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.{Map => MuMap}
import scala.collection.mutable


class Menu(val filename: String = "menu_file.txt") {

  case class Dish(name: String, desc: String, price: String, imgPath: String)

  case class Category(name: String, dishes: ArrayBuffer[Dish]) {
    def dishAt(n: Int) = {
      if (n < dishes.length) Some(dishes(n))
      else None
    }
  }

  var cats = ArrayBuffer[Category]()
  val source = Source.fromURL(getClass.getResource("/" + filename))
  val allLines = source.getLines()
  var lineno = 1
  allLines foreach {
    line =>
      line.split("---").toList match {
        case List(cat, name, desc, price, imgName) =>
          val newDish = Dish(name, desc, price, "/images/" + imgName)
          // TODO: Refactor to own method
          cats find {
            x => x.name == cat
          } match {
            case Some(c) => c.dishes append newDish
            case None => cats append Category(cat, ArrayBuffer(newDish))
          }
        case x =>
          println(x mkString " ")
          throw new IllegalArgumentException(s"Menu file $filename is wrong on line $lineno")
          lineno += 1
      }
  }

  def categoryCount = cats.size

  def getCategoryList = cats.map(x => x.name).toList

  def dishCountInCategory(cat: String) = {
    cats find {
      x => x.name == cat
    } match {
      case Some(c) => c.dishes.length
      case None => throw new IllegalArgumentException("There's no category " + cat)
    }
  }

  def getDishesInCategory(cat: String) = cats find {
    x => x.name == cat
  } match {
    case Some(c) => c.dishes.toArray
    case None => throw new IllegalArgumentException("There's no category " + cat)
  }

  def getDishesInCategory(n: Int) = if (cats.length < n) throw new IllegalArgumentException("nx") else cats(n).dishes

  def getDish(cat: Int, dish: Int) = {
    cats(cat).dishAt(dish)
  }
}

