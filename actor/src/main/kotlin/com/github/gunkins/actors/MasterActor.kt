package com.github.gunkins.actors

import akka.actor.typed.Behavior
import akka.actor.typed.ChildFailed
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import com.github.gunkins.client.OrganicResult
import com.github.gunkins.client.SearchResponse
import java.util.concurrent.CompletableFuture

class MasterActor private constructor(
    context: ActorContext<Command>,
    searchTasks: SearchTasks,
) : AbstractBehavior<MasterActor.Command>(context) {
    private val childrenReplies: MutableList<SearchResponse> = mutableListOf()
    private val resultsPerEngine: Int = searchTasks.resultsPerEngine
    private val resultFuture: CompletableFuture<List<OrganicResult>> = searchTasks.resultFuture
    private val expectedRepliesCount: Int = searchTasks.task.size

    init {
        for (task in searchTasks.task) {
            val childRef = context.spawnAnonymous(ChildActor.create())
            context.watch(childRef)
            childRef.tell(ChildActor.ChildCommand(task, context.self))
        }
        context.setReceiveTimeout(searchTasks.timeout, ReceiveTimeout)
    }

    sealed class Command
    object ReceiveTimeout : Command()
    data class ChildReply(val searchResponse: SearchResponse) : Command()

    override fun createReceive(): Receive<Command> {
        return newReceiveBuilder()
            .onMessage(ReceiveTimeout::class.java) { onReceiveTimeout() }
            .onMessage(ChildReply::class.java, this::onChildReply)
            .onSignal(ChildFailed::class.java, this::onChildFailed)
            .build()
    }

    private fun onReceiveTimeout(): Behavior<Command> {
        completeFuture()
        return Behaviors.stopped()
    }

    private fun onChildReply(childReply: ChildReply): Behavior<Command> {
        childrenReplies += childReply.searchResponse

        return if (childrenReplies.size == expectedRepliesCount) {
            completeFuture()
            return Behaviors.stopped()
        } else {
            Behaviors.same()
        }
    }

    private fun completeFuture() {
        resultFuture.complete(childrenReplies.flatMap { it.organicResults.take(resultsPerEngine) })
    }

    private fun onChildFailed(childFailed: ChildFailed): Behavior<Command> {
        resultFuture.completeExceptionally(childFailed.cause)
        return Behaviors.stopped()
    }

    companion object {
        fun create(searchTasks: SearchTasks): Behavior<Command> {
            return Behaviors.setup { context: ActorContext<Command> ->
                MasterActor(context, searchTasks)
            }
        }
    }
}