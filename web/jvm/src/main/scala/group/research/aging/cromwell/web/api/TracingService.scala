package group.research.aging.cromwell.web.api

import akka.http.scaladsl.model.HttpHeader.ParsingResult.Ok
import akka.http.scaladsl.server._
import group.research.aging.cromwell.client
import io.swagger.v3.oas.annotations._
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media._
import io.swagger.v3.oas.annotations.responses._
import javax.ws.rs._

@Path("/api")
class TracingService extends BasicService {
  @GET
  @Path("/trace")
  def traceAny: Route = pathPrefix("trace") {  entity(as[String]) { json =>
     debug("RECEIVED JSON: ")
     debug(json)
      complete(json)
    }
  }

  def routes: Route  = traceAny
}