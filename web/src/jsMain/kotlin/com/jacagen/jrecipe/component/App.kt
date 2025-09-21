package com.jacagen.jrecipe.component

import react.FC
import react.Props
import react.StrictMode

val App = FC<Props> {
    StrictMode {
        ThemeModule {
            ConversationProvider {
                ThreeColumnLayout()
            }
        }
    }
}