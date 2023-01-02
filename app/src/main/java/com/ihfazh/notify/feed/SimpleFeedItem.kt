package com.ihfazh.notify.feed

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

data class SimpleFeedItem(
   val id: Int,
   val title: String,
   val published: String,
   val accessed: Boolean = false,
){
   @SuppressLint("SimpleDateFormat")
   fun parsedPublished(): Date? {
      /*
      sample 2022-12-30T11:31:09.000004+07:00
       */
      return try{
         val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX")
         fmt.parse(published)
      } catch (e: ParseException){
         val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
         fmt.parse(published)
      }
   }
}