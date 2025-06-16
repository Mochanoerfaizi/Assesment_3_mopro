package com.mochnoerfaizi0109.assesment_3_mopro.model

import com.squareup.moshi.Json

data class Buku(
    val id: Int,
    @Json(name = "nama_buku")
    val namaBuku: String?,
    @Json(name = "penulis_buku")
    val penulisBuku: String?,
    val gambar: String?,
    val userId: Int?
)