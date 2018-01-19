import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.TestFSMRef
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.collection.immutable.Queue

/**
  * Created by knoldus on 19/1/18.
  */
class SizeBasedThrottlerSpec extends FlatSpec with Matchers with BeforeAndAfterEach {
  implicit val system = ActorSystem()

  var fsm: TestFSMRef[State, StateData, SizeBasedThrottler] = _

  override def beforeEach(): Unit =
    fsm = TestFSMRef(new SizeBasedThrottler)

  it should "transition for dummy request" in {
    fsm.stateName shouldBe Waiting
    fsm.stateData shouldBe StateData(Queue.empty)
  }

  it should "transition to Active for one message request" in {
    val msg = Msg(1)
    fsm ! msg
    fsm ! Flush
    fsm.stateName shouldBe Active
    fsm.stateData shouldBe StateData(Queue(msg))
  }

  it should "handle unexpected message" in {
    fsm ! "unexpected"
    fsm.stateName shouldBe Waiting
    fsm.stateData shouldBe StateData(Queue.empty)
  }

  it should "stay in Waiting for one message request" in {
    val msg = Msg(1)
    fsm ! msg
    fsm.stateName shouldBe Waiting
    fsm.stateData shouldBe StateData(Queue(msg))
  }

  it should "transition to Active for multiple message request" in {
    val msg1 = Msg(1)
    val msg2 = Msg(2)

    fsm ! msg1
    fsm ! msg2
    fsm ! Flush
    fsm.stateName shouldBe Active
    fsm.stateData shouldBe StateData(Queue(msg1, msg2))
  }

  it should "transition to waiting after requests are processed" in {
    fsm.setState(Active, StateData(Queue.empty))

    val request = Msg(1)
    fsm ! request

    fsm.stateName shouldBe Waiting
    fsm.stateData shouldBe StateData(Queue(request))
  }

  it should "stay in active state in case of unhandled message" in {
    fsm.setState(Active, StateData(Queue.empty))

    fsm ! "unexpected"

    fsm.stateName shouldBe Active
    fsm.stateData shouldBe StateData(Queue.empty)
  }

  override def afterEach(): Unit =
    fsm ! PoisonPill
}
