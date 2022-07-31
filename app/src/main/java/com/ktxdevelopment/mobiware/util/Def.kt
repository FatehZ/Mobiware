package com.ktxdevelopment.mobiware.util

fun tryEr(function: () -> Unit) {
        try { function() }catch (e: Throwable) {}
}