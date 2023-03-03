package pl.msitko.dhallj.generic.example

import pl.msitko.dhallj.generic.example.{ApiConfig, AppConfig, DbConfig, EndpointConfig}

trait Fixtures {

  val someAppConfig = AppConfig(
    db = DbConfig(host = "host.com", port = 5432),
    api1 = ApiConfig(endpoint = EndpointConfig(host = "some.host")),
    api2 = ApiConfig(
      endpoint = EndpointConfig(
        scheme = "https",
        host = "some.host2",
        port = 8080,
        path = Some("/")
      )),
  )

}
