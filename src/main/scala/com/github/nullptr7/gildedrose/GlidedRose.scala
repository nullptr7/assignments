package com.github.nullptr7.gildedrose

import com.github.nullptr7.gildedrose.GildedRoseProblem.Item
import com.github.nullptr7.gildedrose.GildedRoseProblem.Item._

import scala.util.chaining.scalaUtilChainingOps

sealed trait ItemUpdateStrategy {
  def apply(i: Item): Item
}

/**
 * Once the sell by date has passed, Quality degrades twice as fast
 */
object OrdinaryItem extends ItemUpdateStrategy {
  override def apply(item: Item): Item =
    decreaseSellInQuantity(item).pipe(i => if (i.sellIn >= 0) reduceQuality(i) else (reduceQuality compose reduceQuality) (i))
}

/**
 * "Aged Brie" actually increases in Quality the older it gets
 *
 * an item can never have its Quality increase above 50
 */
object AlwaysImprovingItem extends ItemUpdateStrategy {
  override def apply(item: Item): Item = decreaseSellInQuantity(item).pipe(i => if(i.quality < 50) improveQuality(i) else Item(i.name, i.sellIn, 50))
}

/**
 * an item can never have its Quality increase above 50, however "Sulfuras" is a
 * legendary item and as such its Quality is 80 and it never alters.
 *
 * "Sulfuras", being a legendary item, never has to be sold or decreases in Quality
 */
object LegendaryItem extends ItemUpdateStrategy {
  override def apply(item: Item): Item = item.pipe(i => if (i.quality < 80) improveQuality(i) else Item(i.name, i.sellIn, 80))
}

/**
 * "Backstage passes", like aged brie, increases in Quality as its SellIn value approaches
 */
object BackstagePassesItem extends ItemUpdateStrategy {
  override def apply(item: Item): Item =
    decreaseSellInQuantity(item).pipe(i => if (i.sellIn > 10) improveQuality(i) else i)
                                .pipe(i => if (i.sellIn > 5 && i.sellIn <= 10) (improveQuality compose improveQuality) (i) else i)
                                .pipe(i => if (i.sellIn > 0 && i.sellIn <= 5) (improveQuality compose improveQuality compose improveQuality) (i) else i)
                                .pipe(i => if (i.sellIn <= 0) resetQuality(i) else i)
}

/**
 * "Conjured" items degrade in Quality twice as fast as normal items
 */
object ConjuredItem extends ItemUpdateStrategy {
  override def apply(item: Item): Item = decreaseSellInQuantity(item).pipe(reduceQuality)
                                                                     .pipe(reduceQuality)
}

class GildedRose(val items: Array[Item]){

  def selectStrategy(item: Item): ItemUpdateStrategy =
    Map(
      "Aged Brie"                                 -> AlwaysImprovingItem,
      "Backstage passes to a TAFKAL80ETC concert" -> BackstagePassesItem,
      "Sulfuras, Hand of Ragnaros"                -> LegendaryItem,
      "Conjured"                                  -> ConjuredItem,
      "Ordinary"                                  -> OrdinaryItem
      )(item.name)

  def updateQuality(): Array[Item] =
    for {
      i <- items
      s <- Array(selectStrategy(i))
    } yield s(i)

}