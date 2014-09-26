import org.scalatest.FunSuite

import gov.ornl.stucco.morph.ast._
import gov.ornl.stucco.morph.ast.Implicits._
import gov.ornl.stucco.morph.ast.DSL._
import gov.ornl.stucco.morph.parser._
import gov.ornl.stucco.morph.parser.Interface._
import gov.ornl.stucco.morph.utils.Utils._

import gov.ornl.stucco.extractors._

import org.apache.commons.io._

class LoginEventExtractorSuite extends FunSuite {

  // to make testing easier
  val O = ObjectNode
  val A = ArrayNode
  val S = StringNode
  val N = NumberNode

  test("parse an empty line (no header)") {
    var text = """,,,,,,,,,,,,,,,,,,,,,
"""
    val node = CsvParser(text)
    val loginEvent = LoginEventExtractor.extract(node)

    assert(loginEvent ~> "vertices" ~> 0 === None)
    assert(loginEvent ~> "edges" ~> 0 === None)
  }

  test("parse an empty LoginEvent element (header included)") {
    var text = """date_time,hostname,login_software,status,user,from_ip
,,,,,,,,,,,,,,,,,,,,,
"""
    val node = CsvParser(text)
    val loginEvent = LoginEventExtractor.extract(node)

    assert(loginEvent ~> "vertices" ~> 0 === None)
    assert(loginEvent ~> "edges" ~> 0 === None)
  }

  test("parse 1 LoginEvent element") {
    var text = """Sep 24 15:11:03,StuccoHost,sshd,Accepted,StuccoUser,192.168.10.11
"""
    val node = CsvParser(text)
    val loginEvent = LoginEventExtractor.extract(node)

    assert(loginEvent ~> "vertices" ~> 0 ~> "_id" === Some(S("StuccoHost")))
    assert(loginEvent ~> "vertices" ~> 0 ~> "_type" === Some(S("vertex")))
    assert(loginEvent ~> "vertices" ~> 0 ~> "source" === Some(S("LoginEvent")))
    assert(loginEvent ~> "vertices" ~> 0 ~> "vertexType" === Some(S("host")))

    assert(loginEvent ~> "vertices" ~> 1 ~> "_id" === Some(S("StuccoUser")))
    assert(loginEvent ~> "vertices" ~> 1 ~> "_type" === Some(S("vertex")))
    assert(loginEvent ~> "vertices" ~> 1 ~> "source" === Some(S("LoginEvent")))
    assert(loginEvent ~> "vertices" ~> 1 ~> "vertexType" === Some(S("account")))

    assert(loginEvent ~> "vertices" ~> 2 ~> "_id" === Some(S("sshd")))
    assert(loginEvent ~> "vertices" ~> 2 ~> "_type" === Some(S("vertex")))
    assert(loginEvent ~> "vertices" ~> 2 ~> "source" === Some(S("LoginEvent")))
    assert(loginEvent ~> "vertices" ~> 2 ~> "vertexType" === Some(S("software")))

    assert(loginEvent ~> "edges" ~> 0 ~> "_id" === Some(S("StuccoUser_loginsTo_StuccoHost")))
    assert(loginEvent ~> "edges" ~> 0 ~> "_outV" === Some(S("StuccoUser")))
    assert(loginEvent ~> "edges" ~> 0 ~> "_inV" === Some(S("StuccoHost")))
    assert(loginEvent ~> "edges" ~> 0 ~> "_type" === Some(S("edge")))
    assert(loginEvent ~> "edges" ~> 0 ~> "_label" === Some(S("loginsTo")))
    assert(loginEvent ~> "edges" ~> 0 ~> "source" === Some(S("LoginEvent")))
    assert(loginEvent ~> "edges" ~> 0 ~> "outVType" === Some(S("account")))
    assert(loginEvent ~> "edges" ~> 0 ~> "inVType" === Some(S("host")))

    assert(loginEvent ~> "edges" ~> 1 ~> "_id" === Some(S("StuccoHost_runs_sshd")))
    assert(loginEvent ~> "edges" ~> 1 ~> "_outV" === Some(S("StuccoHost")))
    assert(loginEvent ~> "edges" ~> 1 ~> "_inV" === Some(S("sshd")))
    assert(loginEvent ~> "edges" ~> 1 ~> "_type" === Some(S("edge")))
    assert(loginEvent ~> "edges" ~> 1 ~> "_label" === Some(S("runs")))
    assert(loginEvent ~> "edges" ~> 1 ~> "source" === Some(S("LoginEvent")))
    assert(loginEvent ~> "edges" ~> 1 ~> "outVType" === Some(S("host")))
    assert(loginEvent ~> "edges" ~> 1 ~> "inVType" === Some(S("software")))
  }
}

