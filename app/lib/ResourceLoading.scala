package lib

trait ResourceLoading {
  def getTextFile(path: String) =
    Option(getClass.getClassLoader.getResourceAsStream("index.html")).map(io.Source.fromInputStream(_, "iso-8859-1").mkString)
}
