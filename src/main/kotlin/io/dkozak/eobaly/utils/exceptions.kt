package io.dkozak.eobaly.utils

fun Throwable.stackTraceAsString() =
        this.stackTrace.fold("",
                { acc, stackTraceElement -> "$acc\n$stackTraceElement" }
        )

