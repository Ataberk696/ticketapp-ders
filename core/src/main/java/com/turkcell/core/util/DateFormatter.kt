package com.turkcell.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Tüm projedeki "Tarihsel" formatlayıcı..


private val turkishMonthsShort = arrayOf(
    "Oca","Şub","Mar","Nis","May","Haz","Tem","Ağu","Eyl","Eki","Kas","Ara"
)

fun formatEventDate(isoDate: String?): String? {
    if (isoDate.isNullOrBlank()) return ""

    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date: Date = parser.parse(isoDate) ?: return isoDate


        val turkish = Locale.forLanguageTag("tr-TR")
        val formatter = SimpleDateFormat("d MMM yyyy HH:mm", turkish)
        formatter.timeZone = TimeZone.getDefault()
        formatter.format(date)
    }  catch (e : Exception){
        isoDate
    }

}