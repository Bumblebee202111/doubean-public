package com.github.bumblebee202111.doubean.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

private val BaseChineseTextStyle = TextStyle(
    fontFamily = FontFamily.Default,
    lineHeightStyle = LineHeightStyle(
        alignment = LineHeightStyle.Alignment.Center,
        trim = LineHeightStyle.Trim.None,
    ),
)

private val DisplayLarge = BaseChineseTextStyle.copy(
    fontSize = 40.sp,
    lineHeight = 48.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val DisplayMedium = BaseChineseTextStyle.copy(
    fontSize = 34.sp,
    lineHeight = 42.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val DisplaySmall = BaseChineseTextStyle.copy(
    fontSize = 28.sp,
    lineHeight = 36.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val HeadlineLarge = BaseChineseTextStyle.copy(
    fontSize = 28.sp,
    lineHeight = 36.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val HeadlineMedium = BaseChineseTextStyle.copy(
    fontSize = 24.sp,
    lineHeight = 32.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val HeadlineSmall = BaseChineseTextStyle.copy(
    fontSize = 22.sp,
    lineHeight = 30.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val TitleLarge = BaseChineseTextStyle.copy(
    fontSize = 19.sp,
    lineHeight = 26.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val TitleMedium = BaseChineseTextStyle.copy(
    fontSize = 16.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val TitleSmall = BaseChineseTextStyle.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.SemiBold,
    letterSpacing = 0.sp,
)
private val BodyLarge = BaseChineseTextStyle.copy(
    fontSize = 16.sp,
    lineHeight = 25.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
)
private val BodyMedium = BaseChineseTextStyle.copy(
    fontSize = 15.sp,
    lineHeight = 23.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
)
private val BodySmall = BaseChineseTextStyle.copy(
    fontSize = 12.sp,
    lineHeight = 18.sp,
    fontWeight = FontWeight.Normal,
    letterSpacing = 0.sp,
)
private val LabelLarge = BaseChineseTextStyle.copy(
    fontSize = 14.sp,
    lineHeight = 20.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.sp,
)
private val LabelMedium = BaseChineseTextStyle.copy(
    fontSize = 12.sp,
    lineHeight = 16.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.1.sp,
)
private val LabelSmall = BaseChineseTextStyle.copy(
    fontSize = 11.sp,
    lineHeight = 15.sp,
    fontWeight = FontWeight.Medium,
    letterSpacing = 0.1.sp,
)

private fun TextStyle.emphasized(): TextStyle = copy(
    fontWeight = when (fontWeight) {
        FontWeight.Normal -> FontWeight.Medium
        FontWeight.Medium -> FontWeight.SemiBold
        else -> FontWeight.Bold
    }
)

val AppTypography = Typography(
    displayLarge = DisplayLarge,
    displayMedium = DisplayMedium,
    displaySmall = DisplaySmall,
    headlineLarge = HeadlineLarge,
    headlineMedium = HeadlineMedium,
    headlineSmall = HeadlineSmall,
    titleLarge = TitleLarge,
    titleMedium = TitleMedium,
    titleSmall = TitleSmall,
    bodyLarge = BodyLarge,
    bodyMedium = BodyMedium,
    bodySmall = BodySmall,
    labelLarge = LabelLarge,
    labelMedium = LabelMedium,
    labelSmall = LabelSmall,
    displayLargeEmphasized = DisplayLarge.emphasized(),
    displayMediumEmphasized = DisplayMedium.emphasized(),
    displaySmallEmphasized = DisplaySmall.emphasized(),
    headlineLargeEmphasized = HeadlineLarge.emphasized(),
    headlineMediumEmphasized = HeadlineMedium.emphasized(),
    headlineSmallEmphasized = HeadlineSmall.emphasized(),
    titleLargeEmphasized = TitleLarge.emphasized(),
    titleMediumEmphasized = TitleMedium.emphasized(),
    titleSmallEmphasized = TitleSmall.emphasized(),
    bodyLargeEmphasized = BodyLarge.emphasized(),
    bodyMediumEmphasized = BodyMedium.emphasized(),
    bodySmallEmphasized = BodySmall.emphasized(),
    labelLargeEmphasized = LabelLarge.emphasized(),
    labelMediumEmphasized = LabelMedium.emphasized(),
    labelSmallEmphasized = LabelSmall.emphasized(),
)