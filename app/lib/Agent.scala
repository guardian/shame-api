package lib

object Agent {
  @volatile var keywordsFromDM: List[String] = List.empty

  @volatile var shameWall: List[Shame] = List.empty
}
