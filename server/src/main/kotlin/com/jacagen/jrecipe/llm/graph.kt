package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.llm.tool.RecipeTools
import dev.langchain4j.data.message.UserMessage
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.bsc.langgraph4j.CompileConfig
import org.bsc.langgraph4j.CompiledGraph
import org.bsc.langgraph4j.GraphRepresentation
import org.bsc.langgraph4j.NodeOutput
import org.bsc.langgraph4j.agentexecutor.AgentExecutorEx
import java.io.FileOutputStream

val stateGraph = AgentExecutorEx.builder().chatModel(model) // add object with tools
    .toolsFromObject(RecipeTools()) // add dynamic tool
    .systemMessage(systemMessage).build()
val compileConfig = CompileConfig.builder().checkpointSaver(checkpointSaver).build()
val workflow = stateGraph.compile(compileConfig).also { saveGraphAsPng(it, "graph.png") }

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

fun saveGraphAsPng(compiled: CompiledGraph<*>, filePath: String) {
    // Ask the compiled graph for a PlantUML diagram string
    val plantUml: String = compiled.getGraph(
        GraphRepresentation.Type.PLANTUML, "Graph", true
    ).content()

    // Render to PNG with PlantUML
    val reader = SourceStringReader(plantUml)
    FileOutputStream(filePath).use { out ->
        reader.outputImage(out, 0, FileFormatOption(FileFormat.PNG))
    }
}