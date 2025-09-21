package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.llm.tool.RecipeTools
import dev.langchain4j.data.message.UserMessage
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.bsc.langgraph4j.CompileConfig
import org.bsc.langgraph4j.CompiledGraph
import org.bsc.langgraph4j.GraphRepresentation
import org.bsc.langgraph4j.agentexecutor.AgentExecutor
import org.slf4j.LoggerFactory
import java.io.FileOutputStream

private val logger = LoggerFactory.getLogger("com.jacagen.jrecipe.llm.state")

val stateGraph = AgentExecutor.builder().chatModel(model) // add object with tools
    .toolsFromObject(RecipeTools()) // add dynamic tool
    .systemMessage(systemMessage).build()
val compileConfig = CompileConfig.builder().checkpointSaver(checkpointSaver).build()
val workflow = stateGraph.compile(compileConfig).also { saveGraphAsPng(it, "graph.png") }

// There are some interesting ideas for testing the `chat` method
// [here](https://chatgpt.com/share/68d01d4f-89a0-8003-a851-76623d6aac41)

/**
 * @param cid the client session ID, which should be the same across multiple calls to `chat` for the same end user
 */
fun chat(cid: String, msg: String): String {
    val stream = workflow.stream(
        mapOf(
            "messages" to listOf(
                //systemMessage,
                UserMessage.from(msg),
            ),
            "thread_id" to cid,
            "agent_response" to null,   // This seems to be critical, as stale responses seem to wreak havoc
        )
    )

    logger.debug("*********************************** $msg")
    var finalResponse: String? = null
    for (event in stream) {
        logger.debug("\n---------") // use trace instead?
        logger.debug(event.toString())
        logger.debug("--------\n")

        event.state().finalResponse().ifPresent { finalResponse = it }
    }

    if (finalResponse == null) {
        throw IllegalStateException("FinalResponse was null!!!!")
    } else {
        return finalResponse
    }
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