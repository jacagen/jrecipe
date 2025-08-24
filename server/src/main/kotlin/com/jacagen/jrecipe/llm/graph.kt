package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.llm.tool.RecipeTools
import dev.langchain4j.data.message.UserMessage
import org.bsc.langgraph4j.CompileConfig
import org.bsc.langgraph4j.NodeOutput
import org.bsc.langgraph4j.agentexecutor.AgentExecutorEx

val stateGraph = AgentExecutorEx.builder().chatModel(model) // add object with tools
    .toolsFromObject(RecipeTools()) // add dynamic tool
    .systemMessage(systemMessage).build()
val compileConfig = CompileConfig.builder().checkpointSaver(checkpointSaver).build()
val workflow = stateGraph.compile(compileConfig)

fun chat(msg: String): String {
    val stream = workflow.stream(
        mapOf(
            "messages" to listOf(
                //systemMessage,
                UserMessage.from(msg),
            )
        )
    )
    // workflow.invoke(mapOf("messages" to listOf(UserMessage.from(msg)))).get().finalResponse().get()
    // Should change this to real logging (debug)
    println("***********************************")
    var lastEvent: NodeOutput<AgentExecutorEx.State>? = null
    for (event in stream) {
        lastEvent = event
        println("\n")
        println(event)
        println("\n")
    }
    return lastEvent!!.state().finalResponse().get()
}