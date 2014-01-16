package lib

trait ResourceLoading {
  def getTextFile(path: String, encoding: String) =
    Option(getClass.getClassLoader.getResourceAsStream("index.html")).map(io.Source.fromInputStream(_, encoding).mkString)
}
