package com.github.bumblebee202111.doubean.model.fangorns

enum class HiddenTypeInProfile {
    ABOUT,
    ALL,
    CONTACT,
    DOULIST,
    GALLERY_TOPIC,
    GROUP,
    INTEREST,
    NOTE,
    PHOTO_ALBUM,
    STATUS,
    UNKNOWN;

    companion object {
        fun fromString(type: String): HiddenTypeInProfile {
            return entries.find { it.name.equals(type, ignoreCase = true) } ?: UNKNOWN
        }
    }
}