package com.example.ttapp

class AuthHelper {
    var name: String? = null
    var email: String? = null
    var username: String? = null
    var password: String? = null

    constructor(name: String?, email: String?, username: String?, password: String?) {
        this.name = name
        this.email = email
        this.username = username
        this.password = password
    }

    constructor() {}
}

