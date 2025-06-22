package com.jacagen.jrecipe.theme

import mui.material.styles.Theme
import react.RequiredContext
import react.StateInstance
import react.StateSetter
import react.createRequiredContext
import react.useRequired

val ThemeContext: RequiredContext<StateInstance<Theme>> =
    createRequiredContext()

fun useTheme(): Theme =
    useRequired(ThemeContext).component1()

fun useSetTheme(): StateSetter<Theme> =
    useRequired(ThemeContext).component2()