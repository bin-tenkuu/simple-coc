package com.github.bin.dao

import kotlinx.serialization.Serializable

@Serializable
class Tag(val name: String, val type: String = "", val color: String = "")
