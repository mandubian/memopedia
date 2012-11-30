package models


object Word {
  def defautWords =
    Word("briquet", "lighter", "lighter.jpeg") ::
    Word("ruban", "ribbon", "ribbon.jpeg") ::
    Word("boîte aux lettres", "post box", "postbox.jpeg") ::
    Word("cerf", "deer", "deer.jpeg") ::
    Word("menton", "chin", "chin.jpeg") ::
    Nil
}

case class Word(text: String, trad: String, pic: String)


