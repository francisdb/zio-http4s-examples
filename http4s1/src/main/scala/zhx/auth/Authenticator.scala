package zhx.auth

import zhx.auth.Authenticator.AuthenticationError
import zio._
import Authenticator._


object Authenticator {

  type Authenticator = Has[Service]

  case class AuthToken(tok: String)

  trait AuthenticationError extends Throwable

  val authenticationError: AuthenticationError = new AuthenticationError {
    override def getMessage: String = "Authentication Error"
  }

  trait Service {
    def authenticate(userName: String, password: String): Task[AuthToken]
  }

  val friendlyAuthenticator: Service = { (userName, password) =>
    password match {
      case "friend" => IO.succeed(AuthToken(userName)) // rather trivial implementation but does allow us to inject variety
      case _ => IO.fail(authenticationError)
    }
  }

  val friendly = ZLayer.succeed(friendlyAuthenticator)
}

package object authenticator {
  def authenticate(userName: String, password: String): RIO[Authenticator, AuthToken]
  = ZIO.accessM[Authenticator](_.get.authenticate(userName, password))
}


