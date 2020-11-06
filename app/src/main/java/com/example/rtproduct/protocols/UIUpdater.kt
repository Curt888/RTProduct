package com.example.rtproduct.protocols

interface UIUpdaterInterface {

    fun resetUIWithConnection(status: Boolean)
    fun updateStatusViewWith(status: String)
    fun update(message: String)
    fun topicUpdate(topic: String)



}