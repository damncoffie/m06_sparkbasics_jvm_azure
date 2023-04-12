package config

import org.apache.spark.sql.SparkSession
import service.SparkService

/**
 * Application' configuration properties.
 */
object AppConfig {

  private val session: SparkSession = SparkService.getSession

  // spark-submit command ignores configs which are not start with "spark." prefix
  val openCageApiKey: String = session.conf.get("spark.openCage.apiKey")
  val outputPath: String = session.conf.get("spark.azure.outputPath")
  val abfssPath: String = session.conf.get("spark.azure.abfssPath")

  private val accountName: String = session.conf.get("spark.azure.accountName")
  private val clientId: String = session.conf.get("spark.azure.clientId")
  private val secret: String = session.conf.get("spark.azure.secret")
  private val endpoint: String = session.conf.get("spark.azure.endpoint")

  session.conf.set("fs.azure.account.auth.type." + accountName + ".dfs.core.windows.net", "OAuth")
  session.conf.set("fs.azure.account.oauth.provider.type." + accountName + ".dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
  session.conf.set("fs.azure.account.oauth2.client.id." + accountName + ".dfs.core.windows.net", clientId)
  session.conf.set("fs.azure.account.oauth2.client.secret." + accountName + ".dfs.core.windows.net", secret)
  session.conf.set("fs.azure.account.oauth2.client.endpoint." + accountName + ".dfs.core.windows.net", endpoint)
}
