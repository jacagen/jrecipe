@file:OptIn(ExperimentalAtomicApi::class)

package com.jacagen.jrecipe.llm

import com.jacagen.jrecipe.data.dao.mongodb.recipeDao
import com.jacagen.jrecipe.llm.tool.RecipeTools
import com.jacagen.jrecipe.model.ChatResponse
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import kotlinx.serialization.json.Json
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.bsc.langgraph4j.CompileConfig
import org.bsc.langgraph4j.CompiledGraph
import org.bsc.langgraph4j.GraphRepresentation
import org.bsc.langgraph4j.NodeOutput
import org.bsc.langgraph4j.agentexecutor.AgentExecutor
import org.slf4j.LoggerFactory
import java.io.FileOutputStream
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

private val logger = LoggerFactory.getLogger("com.jacagen.jrecipe.llm.state")

private val toolArgumentJson = Json { ignoreUnknownKeys = true }

data class TurnState(
    var selectedRecipe: AtomicReference<String?> = AtomicReference(null)
)

// There are some interesting ideas for testing the `chat` method
// [here](https://chatgpt.com/share/68d01d4f-89a0-8003-a851-76623d6aac41)

/**
 * @param cid the client session ID, which should be the same across multiple calls to `chat` for the same end user
 */
suspend fun chat(cid: String, msg: String): ChatResponse {
    val turnState = TurnState()
    val stateGraph = AgentExecutor.builder().chatModel(model) // add object with tools
        .toolsFromObject(RecipeTools(turnState)) // add dynamic tool
        .systemMessage(systemMessage).build()
    val compileConfig = CompileConfig.builder().checkpointSaver(checkpointSaver).build()
    val workflow = stateGraph.compile(compileConfig).also { saveGraphAsPng(it, "graph.png") }

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

        // See if one specific recipe was chosen
        val newSelectedRecipe = checkIfRecipeSelected(event)
        turnState.selectedRecipe.compareAndSet(null, newSelectedRecipe)

        event.state().finalResponse().ifPresent { finalResponse = it }
    }

    return if (finalResponse == null) {
        throw IllegalStateException("FinalResponse was null!!!!")
    } else {
        val selectedRecipe = turnState.selectedRecipe.load()
        if (selectedRecipe == null) ChatResponse(finalResponse)
        else ChatResponse(finalResponse, recipeDao.findById(selectedRecipe))
    }
}

private fun checkIfRecipeSelected(event: NodeOutput<AgentExecutor.State>): String? {
    var recipeId: String? = null
    if (event.node() == "agent") {
        val lastMessage = event.state().messages().last()
        if (lastMessage is AiMessage) {
            val request = lastMessage.toolExecutionRequests().filter { it.name() == "noteChosenRecipe" }
            if (!request.isEmpty()) {
                val args = request[0].arguments()
                recipeId = toolArgumentJson.decodeFromString<Map<String, String>>(args).values.single()
            }
        }
    }
    return recipeId
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