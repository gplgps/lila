package lila.streamer

import org.joda.time.DateTime

import lila.user.User

case class Streamer(
    _id: Streamer.Id, // user ID
    listed: Streamer.Listed,
    autoFeatured: Streamer.AutoFeatured,
    picturePath: Option[Streamer.PicturePath],
    name: Option[Streamer.Name],
    description: Option[Streamer.Description],
    twitch: Option[Streamer.Twitch],
    youTube: Option[Streamer.YouTube],
    createdAt: DateTime,
    updatedAt: DateTime
) {

  def id = _id

  def is(user: User) = id.value == user.id

  def hasPicture = picturePath.isDefined
}

object Streamer {

  def make(user: User) = Streamer(
    _id = Id(user.id),
    listed = Listed(true),
    autoFeatured = AutoFeatured(false),
    picturePath = none,
    name = none,
    description = none,
    twitch = none,
    youTube = none,
    createdAt = DateTime.now,
    updatedAt = DateTime.now
  )

  case class Id(value: User.ID) extends AnyVal with StringValue
  case class Listed(value: Boolean) extends AnyVal
  case class AutoFeatured(value: Boolean) extends AnyVal
  case class PicturePath(value: String) extends AnyVal with StringValue
  case class Name(value: String) extends AnyVal with StringValue
  case class Description(value: String) extends AnyVal with StringValue
  case class Live(liveAt: Option[DateTime], checkedAt: Option[DateTime]) {
    def now = liveAt ?? { l =>
      checkedAt ?? { l == }
    }
  }
  object Live {
    val empty = Live(none, none)
  }
  case class Twitch(userId: String, live: Live)
  object Twitch {
    private val UserIdRegex = """^(\w{2,24})$""".r
    private val UrlRegex = """.*twitch\.tv/(\w{2,24}).*""".r
    // https://www.twitch.tv/chessnetwork
    def parseUserId(str: String): Option[String] = str match {
      case UserIdRegex(u) => u.some
      case UrlRegex(u) => u.some
      case _ => none
    }
  }
  case class YouTube(channelId: String, live: Live)
  object YouTube {
    private val ChannelIdRegex = """^(\w{11})$""".r
    private val UrlRegex = """.*(?:youtube\.com|youtu\.be)/(?:watch)?(?:\?v=)?([^"&?\/ ]{11}).*""".r
    def parseChannelId(str: String): Option[String] = str match {
      case ChannelIdRegex(c) => c.some
      case UrlRegex(c) => c.some
      case _ => none
    }
  }
}
