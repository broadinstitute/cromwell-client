package group.research.aging.cromwell.client

//import java.time.ZonedDateTime

import io.circe._
import io.circe.generic.JsonCodec

import scala.concurrent.duration._


trait CromwellResponse

@JsonCodec case class Stats(workflows: Int, jobs: Int) extends CromwellResponse

@JsonCodec case class Version(cromwell: String) extends CromwellResponse

trait WorkflowResponse extends CromwellResponse
{
  def id: String
}

case class CallOutput(value: Json) extends CromwellResponse

object CallOutput {

  implicit val encode: Encoder[CallOutput] = new Encoder[CallOutput] {
    final def apply(a: CallOutput): Json = a.value
  }

  implicit val decode: Decoder[CallOutput] = new Decoder[CallOutput] {
    final def apply(c: HCursor): Decoder.Result[CallOutput] = c.focus match{
      case None => Left(DecodingFailure("Cannot extract call output!", c.history))
      case Some(json) => Right(CallOutput(json))
    }
  }

}

case class WorkflowOutputs(values: Map[String, String]) extends CromwellResponse

object WorkflowOutputs {
  import io.circe.syntax._
  implicit val encode: Encoder[WorkflowOutputs] = (a: WorkflowOutputs) => a.values.asJson

  implicit val decode: Decoder[WorkflowOutputs] = (c: HCursor) => c.focus match {
    case None => Left(DecodingFailure("Cannot extract workflow output!", c.history))
    case Some(json) => Right(WorkflowOutputs(json.asObject.map(o => o.toMap.mapValues(v => v.toString())).get))
  }

}

case class Inputs(values: Map[String, String]) extends CromwellResponse

object Inputs {
  import io.circe.syntax._
  implicit val encode: Encoder[Inputs] = (a: Inputs) => a.values.asJson

  implicit val decode: Decoder[Inputs] = (c: HCursor) => c.focus match {
    case None => Left(DecodingFailure("Cannot extract input!", c.history))
    case Some(json) => Right(Inputs(json.asObject.map(o => o.toMap.mapValues(v => v.toString())).get))
  }

}


object QueryResults {
  lazy val empty = QueryResults(Nil)
}

@JsonCodec case class QueryResults(results: List[QueryResult]) extends CromwellResponse

@JsonCodec case class QueryResult(id: String, status: String, start: Option[String] = None, end: Option[String] = None) extends WorkflowResponse


//implicit val config: Configuration = Configuration.default.withSnakeCaseKeys
// config: io.circe.generic.extras.Configuration = Configuration(io.circe.generic.extras.Configuration$$$Lambda$2037/501381773@195cef0e,false,None)

object Metadata

@JsonCodec case class Metadata(
                                id: String,
                                submission: String,
                                status: String,
                                start: Option[String],
                                end: Option[String],
                                inputs: Inputs,
                                outputs: WorkflowOutputs,
                                failures: Option[List[WorkflowFailure]] = None,
                                submittedFiles: SubmittedFiles,
                                workflowName: Option[String] = None,
                                workflowRoot: Option[String] = None,
                                calls: Option[Map[String, List[LogCall]]] = None
                              ) extends WorkflowResponse
{

  lazy val startDate: String = start.map(s=>s.substring(0, Math.max(0, s.indexOf("T")))).getOrElse("")
  lazy val endDate: String = end.fold("")(e=>e.substring(0, Math.max(0, e.indexOf("T"))))

  lazy val startTime: String = start.map(s=>s.substring(s.indexOf("T")+1, s.lastIndexOf("."))).getOrElse("")
  lazy val endTime: String = end.fold("")(e=> e.substring(e.indexOf("T")+1, e.lastIndexOf(".")))

  lazy val dates: String = if(endDate==startDate || endDate=="") startDate else s"${startDate}-${endDate}"

  //protected def parse(text: String): LocalDate = LocalDate.parse(text, DateTimeFormatter.ISO_INSTANT)
}

//@JsonCodec case class WorkflowOutputs(outputs: Map[String, String]) extends CromwellResponse

@JsonCodec case class CallOutputs(outputs: Map[String,  CallOutput], id: String) extends WorkflowResponse

@JsonCodec case class StatusInfo(id: String, status: String) extends WorkflowResponse

@JsonCodec case class Logs(id: String, calls: Option[Map[String, List[LogCall]]] = None) extends WorkflowResponse

@JsonCodec case class LogCall(stderr: Option[String] , stdout: Option[String], attempt: Int, shardIndex: Int,
                              callRoot: Option[String] = None,
                              executionStatus: Option[String] = None,
                              callCaching: Option[CallCaching] = None) extends CromwellResponse

@JsonCodec case class Backends(supportedBackends: List[String], defaultBackend: String) extends CromwellResponse

@JsonCodec case class SubmittedFiles(inputs: String, workflow: String, options: String) extends CromwellResponse

@JsonCodec case class WorkflowFailure(message: String, causedBy: List[WorkflowFailure] = Nil) extends CromwellResponse

object CallCaching {

  //implicit val config: Configuration = Configuration.default.withSnakeCaseMemberNames
}

@JsonCodec case class CallCaching(allowResultReuse: Boolean, effectiveCallCachingMode: Option[String], hit: Option[Boolean] = None, result: Option[String] = None)
