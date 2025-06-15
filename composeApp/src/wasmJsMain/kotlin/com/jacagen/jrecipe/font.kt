package com.jacagen.jrecipe

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import jrecipe.composeapp.generated.resources.NotoSansJP_Bold
import jrecipe.composeapp.generated.resources.NotoSansJP_Regular
import jrecipe.composeapp.generated.resources.NotoSans_Bold
import jrecipe.composeapp.generated.resources.NotoSans_Italic
import jrecipe.composeapp.generated.resources.NotoSans_Regular
import jrecipe.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle

// This might want to be memoized, according to ChatGPT.

@Composable
@OptIn(ExperimentalResourceApi::class)
fun latinFontFamily(): FontFamily = FontFamily(
    Font(Res.font.NotoSans_Regular, weight = FontWeight.Normal),
    Font(Res.font.NotoSans_Bold, weight = FontWeight.Bold),
    Font(Res.font.NotoSans_Italic, style = FontStyle.Italic)
)

@Composable
@OptIn(ExperimentalResourceApi::class)
fun japaneseFontFamily(): FontFamily = FontFamily(
    Font(Res.font.NotoSansJP_Regular, weight = FontWeight.Normal),
    Font(Res.font.NotoSansJP_Bold, weight = FontWeight.Bold)
)

@Composable
@OptIn(ExperimentalResourceApi::class)
fun appFontFamily(): FontFamily = FontFamily(
    Font(Res.font.NotoSans_Regular, weight = FontWeight.Normal),
    Font(Res.font.NotoSans_Bold, weight = FontWeight.Bold),
    Font(Res.font.NotoSans_Italic, style = FontStyle.Italic),
    Font(Res.font.NotoSansJP_Regular, weight = FontWeight.Normal),
    Font(Res.font.NotoSansJP_Bold, weight = FontWeight.Bold),
)


@Composable
fun appTypography(): Typography = Typography(
    bodyLarge = TextStyle(fontFamily = appFontFamily()),
    bodyMedium = TextStyle(fontFamily = appFontFamily()),
    bodySmall = TextStyle(fontFamily = appFontFamily()),
    headlineSmall = TextStyle(fontFamily = appFontFamily()),
    headlineLarge = TextStyle(fontFamily = appFontFamily()),
    labelSmall = TextStyle(fontFamily = appFontFamily())
)