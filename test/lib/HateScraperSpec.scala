package lib

import org.specs2.Specification

class HateScraperSpec extends Specification with ResourceLoading {
  val elements = HateScraper.extract(getTextFile("index.html", "iso-8859-1").get)

  def is = "extracts first element correctly" ! {
    elements.head mustEqual SidebarElement(
      "'Last minute sun': Rita Ora shares bikini snap by the pool in LA before flying out to Vancouver to shoot " +
        "Fifty Shades Of Grey It's 5° Celsius in the city",
      "Remember the weather: Rita Ora catches some sun before flying out to cold Vancouver to shoot Fifty Shades " +
        "Of Grey")
  } ^ "extracts last element correctly" ! {
    elements.last mustEqual SidebarElement(
      "'Hats off to her for finding such a wonderful young man': Emma Watson is welcomed with open arms by family " +
      "of new boyfriend Matthew Janney",
      "Romance: Emma recently began dating Oxford University student Matthew Janney, with the pair meeting through " +
        "mutual friends"
    )
  } ^ "extracts all the elements" ! {
    elements.length mustEqual 107
  }
}
