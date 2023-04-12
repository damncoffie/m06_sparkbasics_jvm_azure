package service

class ParentTest {

  // common spark test config
  SparkService.getSession.conf.set("spark.openCage.apiKey", "1")
  SparkService.getSession.conf.set("spark.azure.abfssPath", "path")
  SparkService.getSession.conf.set("spark.azure.authType", "type")
  SparkService.getSession.conf.set("spark.azure.providerType", "type")
  SparkService.getSession.conf.set("spark.azure.accountName", "name")
  SparkService.getSession.conf.set("spark.azure.clientId", "id")
  SparkService.getSession.conf.set("spark.azure.secret", "sec")
  SparkService.getSession.conf.set("spark.azure.endpoint", "endpoint")
  SparkService.getSession.conf.set("spark.azure.outputPath", "path")
}
