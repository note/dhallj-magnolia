package pl.msitko.dhallj.generic.example

final case class DbConfig(
    host: String,
    port: Int
)

final case class ApiConfig(
    endpoint: EndpointConfig
)

final case class EndpointConfig(
    scheme: String = "http",
    host: String,
    port: Int = 80,
    path: Option[String] = None
)

final case class AppConfig(
    db: DbConfig,
    api1: ApiConfig,
    api2: ApiConfig
)

final case class Errors(errors: List[Error])

sealed trait Error

final case class Error1(msg: String) extends Error

final case class Error2(code: Int, code2: Long) extends Error
