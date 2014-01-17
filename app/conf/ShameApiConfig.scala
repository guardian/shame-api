package conf

import com.gu.conf.ConfigurationFactory

object ShameApiConfig {
  private val conf = ConfigurationFactory.getConfiguration("shame-api")

  lazy val apiKey = conf.getStringProperty("content_api.api_key")
  lazy val userTier = conf.getStringProperty("content_api.tier")
  lazy val targetUrl = conf.getStringProperty("content_api.target_url")
}
