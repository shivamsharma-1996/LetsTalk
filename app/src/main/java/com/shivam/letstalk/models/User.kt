package com.shivam.letstalk.models

import java.io.Serializable

class User(
    var firstName:String? = null,
    var lastName:String? = null,
    var email:String? = null,
    var token:String? = null
) : Serializable