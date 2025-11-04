package com.github.bumblebee202111.doubean.security

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun ByteArray.hmacSha1(key: ByteArray): ByteArray {
    val secretKeySpec = SecretKeySpec(key, "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(secretKeySpec)
    return mac.doFinal(this)
}