package com.github.nullptr7.binary

object BTreeImplementation {

  sealed trait BTree[+Element]

  case object EmptyBTree extends BTree[Nothing]

  case class NonEmptyBTree[+Element](left: BTree[Element],
                                     data: Element,
                                     right: BTree[Element]) extends BTree[Element]

}
