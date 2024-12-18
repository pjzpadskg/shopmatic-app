package controllers

import javax.inject._
import play.api._
import play.api.mvc._

import scala.concurrent._

import domain.user._

case class AuthenticatedRequest[A](email: String, request: Request[A]) extends WrappedRequest[A](request)

class SecureAction @Inject() (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext) extends ActionBuilder[AuthenticatedRequest, AnyContent] {
  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    val email = request.session.get("email")
    email match {
      case Some(_) => block(AuthenticatedRequest(request.session("email"), request))
      case None => Future.successful(Results.Unauthorized("Unauthorized access"))
    }
  }
}

