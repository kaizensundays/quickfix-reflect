package com.kaizensundays.fusion.quickfix

/**
 * Created: Saturday 6/25/2022, 12:49 PM Eastern Time
 *
 * @author Sergey Chuykov
 */

fun String.firstCharToUpper() = replaceFirstChar { c -> c.uppercase() }
