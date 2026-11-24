package com.github.bumblebee202111.doubean.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF3F6836)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFC0EFB0)
val onPrimaryContainerLight = Color(0xFF002200)
val secondaryLight = Color(0xFF54634E)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFD7E8CD)
val onSecondaryContainerLight = Color(0xFF121F0E)
val tertiaryLight = Color(0xFF386569)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFBCEBEE)
val onTertiaryContainerLight = Color(0xFF002022)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFF8FBF1)
val onBackgroundLight = Color(0xFF191D17)
val surfaceLight = Color(0xFFF8FBF1)
val onSurfaceLight = Color(0xFF191D17)
val surfaceVariantLight = Color(0xFFDFE4D8)
val onSurfaceVariantLight = Color(0xFF43483F)
val outlineLight = Color(0xFF73796E)
val outlineVariantLight = Color(0xFFC3C8BC)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF2E322B)
val inverseOnSurfaceLight = Color(0xFFEFF2E8)
val inversePrimaryLight = Color(0xFFA5D396)
val surfaceDimLight = Color(0xFFD8DBD2)
val surfaceBrightLight = Color(0xFFF8FBF1)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFF2F5EB)
val surfaceContainerLight = Color(0xFFECEFE5)
val surfaceContainerHighLight = Color(0xFFE6E9E0)
val surfaceContainerHighestLight = Color(0xFFE1E4DA)

val primaryDark = Color(0xFFA5D396)
val onPrimaryDark = Color(0xFF10380C)
val primaryContainerDark = Color(0xFF285021)
val onPrimaryContainerDark = Color(0xFFC0EFB0)
val secondaryDark = Color(0xFFBBCBB2)
val onSecondaryDark = Color(0xFF263422)
val secondaryContainerDark = Color(0xFF3C4B37)
val onSecondaryContainerDark = Color(0xFFD7E8CD)
val tertiaryDark = Color(0xFFA0CFD2)
val onTertiaryDark = Color(0xFF00373A)
val tertiaryContainerDark = Color(0xFF1E4D51)
val onTertiaryContainerDark = Color(0xFFBCEBEE)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF11140F)
val onBackgroundDark = Color(0xFFE1E4DA)
val surfaceDark = Color(0xFF11140F)
val onSurfaceDark = Color(0xFFE1E4DA)
val surfaceVariantDark = Color(0xFF43483F)
val onSurfaceVariantDark = Color(0xFFC3C8BC)
val outlineDark = Color(0xFF8D9387)
val outlineVariantDark = Color(0xFF43483F)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFE1E4DA)
val inverseOnSurfaceDark = Color(0xFF2E322B)
val inversePrimaryDark = Color(0xFF3F6836)
val surfaceDimDark = Color(0xFF11140F)
val surfaceBrightDark = Color(0xFF363A34)
val surfaceContainerLowestDark = Color(0xFF0B0F0A)
val surfaceContainerLowDark = Color(0xFF191D17)
val surfaceContainerDark = Color(0xFF1D211B)
val surfaceContainerHighDark = Color(0xFF272B25)
val surfaceContainerHighestDark = Color(0xFF32362F)

@Immutable
data class DoubeanExtendedColors(
    val ratingStar: Color,
    val rankFirst: Color,
    val rankSecond: Color,
    val rankThird: Color,
)

internal val LightDoubeanExtendedColors = DoubeanExtendedColors(
    ratingStar = Color(0xFFE59800),
    rankFirst = Color(0xFFC87D00),
    rankSecond = Color(0xFF6E7781),
    rankThird = Color(0xFFA6633C),
)

internal val DarkDoubeanExtendedColors = DoubeanExtendedColors(
    ratingStar = Color(0xFFFFCA4B),
    rankFirst = Color(0xFFFFCA4B),
    rankSecond = Color(0xFFB8C0C8),
    rankThird = Color(0xFFE09F78),
)

internal val LocalDoubeanExtendedColors = staticCompositionLocalOf { LightDoubeanExtendedColors }