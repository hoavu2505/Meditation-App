package com.example.meditation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Content(var id: String ?= null,
              var name: String ?= null,
              var mode: String ?= null,
              var duration: Int ?= null,
              var description: String ?= null,
              var img: String ?= null,
              var type: String ?= null,
              var audio : ArrayList<String> ?= null) : Parcelable