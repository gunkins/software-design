package com.github.gunkins.actors

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import com.github.gunkins.client.SearchResponse
import java.util.function.Supplier

class ChildActor private constructor(
    context: ActorContext<ChildCommand>
) : AbstractBehavior<ChildActor.ChildCommand>(context) {
    data class ChildCommand(val task: Supplier<SearchResponse>, val replyTo: ActorRef<MasterActor.Command>)

    override fun createReceive(): Receive<ChildCommand> {
        return newReceiveBuilder()
            .onMessage(ChildCommand::class.java, this::onQuery)
            .build()
    }

    private fun onQuery(query: ChildCommand): Behavior<ChildCommand> {
        val taskResult = query.task.get()
        query.replyTo.tell(MasterActor.ChildReply(taskResult))
        return this
    }

    companion object {
        fun create(): Behavior<ChildCommand> {
            return Behaviors.setup { context: ActorContext<ChildCommand> ->
                ChildActor(context)
            }
        }
    }
}