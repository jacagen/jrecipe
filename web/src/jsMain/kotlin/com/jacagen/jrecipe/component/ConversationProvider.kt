package com.jacagen.jrecipe.component

import react.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

val ConversationIdContext = createContext<String>()

@OptIn(ExperimentalUuidApi::class)
val ConversationProvider = FC<PropsWithChildren> { props ->
    var cid by useState("")

    useEffectOnce {
        cid = Uuid.random().toString()
    }

    if (cid.isBlank()) return@FC // render nothing until initialized

    ConversationIdContext.Provider {
        value = cid
        +props.children
    }
}