package group.research.aging.cromwell.client

import java.io.{File => JFile}
import java.nio.ByteBuffer

import better.files._

import scala.concurrent.Future

/**
  * Created by antonkulaga on 2/18/17.
  */
trait CromwellClientJVMSpecific {
  self: CromwellClientShared =>

  import  implicits._

  protected def zipFolder(file: File) = {
    /*
    val dir = File.newTemporaryDirectory()
    val child = dir.createChild(file.name, true).createDirectory()
    val zp = dir.createChild(s"${file.name}.zip", false)
    child.zipTo(zp)
    child.zip()
    */
    file.zip()
  }

  def postWorkflowFiles(file: File,
                        workflowInputs: File,
                        workflowOptions: Option[File] = None,
                        wdlDependencies: Option[File] = None): Future[Status] ={
    self.postWorkflowStrings(
      file.lines.mkString("\n"),
      workflowInputs,
      workflowOptions,
      wdlDependencies.map(f=>ByteBuffer.wrap(zipFolder(f).loadBytes))
    )
  }

}