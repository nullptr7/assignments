package com.github.nullptr7.gildedrose

import com.github.nullptr7.gildedrose.GildedRoseProblem.Item

object Testing extends App {

  val a: Array[Item] = Array(Item("Sulfuras, Hand of Ragnaros", 10, 81))

  val glidedRose = new GildedRose(a)

  glidedRose.updateQuality().foreach(println)

}
