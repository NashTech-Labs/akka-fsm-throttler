import akka.actor.{ActorSystem, FSM, Props}

import scala.collection.immutable
import scala.collection.immutable.Queue
import scala.concurrent.duration._
/**
  * Created by knoldus on 14/1/18.
  */

// The types of incoming message
case class Msg(a: Int)
case object Flush

// States
sealed trait State
case object Waiting extends State
case object Active extends State

// StateData is shared between states
case class StateData(queue: immutable.Queue[Msg])

class SizeBasedThrottler extends FSM[State, StateData] {
  val startTime: Long = System.currentTimeMillis()

  def curTime: String = {
    val time = (System.currentTimeMillis() - startTime) / 1000f
    f"[$time%3.2f]"
  }

  startWith(Waiting, StateData(Queue.empty))

  onTransition {
    case Waiting -> Active =>
      //use nextStateData rather than stateData !
      nextStateData match {
        case StateData(queue) =>
          for(x <- queue) yield println(s"$curTime processing ${x.a} ")
          Thread.sleep(2000L) //
      }
  }

  when(Active) {
    case Event(msg: Msg, _) =>
      println(s"$curTime at Active $msg" )
      // we've just processed old data
      // drop the old queue and create a new one with the new message
      goto(Waiting) using StateData(Queue(msg))
    //handling unexpected message
    case Event(_, _) => stay()
  }

  when(Waiting, stateTimeout = 2 seconds){
    case Event(msg: Msg, StateData(oldQueue)) =>
      val newQueue = oldQueue :+ msg
      println(s"$curTime at Idle $newQueue")
      stay() using StateData(newQueue)
    //time to process requests
    case Event(Flush, StateData(queue)) => goto(Active) using StateData(queue)
    //Wait till given time
    case Event(StateTimeout, StateData(queue)) => goto(Active) using StateData(queue)
     //handling unexpected message
    case Event(_, StateData(queue)) => stay() using StateData(queue)

  }

  initialize()
}

object demo extends App  {
  val threshold = 3
  val numberOfRequests = 20
  val actorSystem = ActorSystem("system")
  val actor = actorSystem.actorOf(Props(classOf[SizeBasedThrottler]))
   for{
     i <- 1 to numberOfRequests
     _ = println(s"Send $i")
     _ = actor ! Msg(i)
     _ = if(i % threshold == 0) actor ! Flush
   } yield {}

}