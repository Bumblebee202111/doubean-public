package com.github.bumblebee202111.doubean.model.subjects

data class BookDetail(
    override val id: String,
    override val rating: Rating,
    override val title: String,
    override val cardSubtitle: String,
    override val coverUrl: String,
    override val uri: String,
    override val intro: String,
    override val interest: SubjectInterest?,
    override val isReleased: Boolean,
    override val vendors: List<Vendor> = emptyList(),
    val pubdate: List<String>,
    val author: List<String>,
    val subtitle: String?,
    val producers: List<String>,
    val press: List<String>,
    val pages: List<String>,
) : SubjectDetail {
    override val type: SubjectType = SubjectType.BOOK
}
