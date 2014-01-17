package keywords

import org.scalatest.{ShouldMatchers, FlatSpec}

class KeywordExtractorTest extends FlatSpec with ShouldMatchers{
  val extract = new KeywordExtractor{}

  "Keyword Extractor" should "extract 2 word names from text" in {
    val keywords = extract("Moving on! Vanessa Paradis reveals completely new look amid rumours her ex Johnny Depp is engaged to Amber Heard", "")

    keywords should contain("Vanessa Paradis")
    keywords should contain("Johnny Depp")
    keywords should contain("Amber Heard")
  }

  it should "extract 1 & 2 word names from text" in {
    val keywords = extract("Kelly Osbourne calls Katie Hopkins the C-word over her cruel comments about mum Sharon's surgeries", "")

    keywords should contain("Kelly Osbourne")
    keywords should contain("Katie Hopkins")
    keywords should contain("Sharon")
    keywords should not contain("")
    keywords.size should be(4)
  }

  it should "not include stop words in extracted text" in {
    val keywords = extract("'What are you saying about my girl's mom?' Shia LaBeouf headbutts drinker in London bar brawl as girlfriend screams to stop.", "")

    keywords should contain("Shia LaBeouf")
    keywords should contain("London")
    keywords should not contain("What")
  }

  it should "order results by frequency" in {
    val keywords = extract("Matilda likes Bob", "but Bob doesn't care. He likes Joe but Joe doesn't like Bob.")

    keywords should be(List("Bob", "Joe", "Matilda"))
  }


}