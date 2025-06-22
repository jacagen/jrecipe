package com.jacagen.jrecipe.theme

import js.objects.unsafeJso
import mui.material.PaletteMode
import mui.material.styles.TypographyOptions
import mui.material.styles.TypographyVariant
import mui.material.styles.createTheme
import web.cssom.atrule.maxWidth
import web.cssom.integer
import web.cssom.px
import web.cssom.rem

private val TYPOGRAPHY_OPTIONS = TypographyOptions {
    fontWeight = integer(500)

    TypographyVariant.h6 {
        fontSize = 1.5.rem

        media(maxWidth(599.px)) {
            fontSize = 1.25.rem
        }
    }
}

object Themes {
    val Light = createTheme(
        unsafeJso {
            palette = unsafeJso {
                mode = PaletteMode.light
                background = unsafeJso {
                    paper = "#f7f9fc" // light bluish-gray, very subtle
                }
            }
            typography = TYPOGRAPHY_OPTIONS
        }
    )

    val Dark = createTheme(
        unsafeJso {
            palette = unsafeJso { mode = PaletteMode.dark }
            typography = TYPOGRAPHY_OPTIONS
        }
    )
}