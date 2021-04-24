package com.github.nullptr7.gildedrose

object GildedRoseProblem {

  case class Item(name: String, sellIn: Int, quality: Int)

  object Item {
    def decreaseSellInQuantity: Item => Item = x => Item(x.name, x.sellIn - 1, x.quality)

    def improveQuality: Item => Item = x => Item(x.name, x.sellIn, if (x.quality >= 50) 50 else x.quality + 1)

    def reduceQuality: Item => Item = x => Item(x.name, x.sellIn, if (x.quality <= 0) 0 else x.quality - 1)

    def resetQuality: Item => Item = x => Item(x.name, x.sellIn, 0)
  }
}
