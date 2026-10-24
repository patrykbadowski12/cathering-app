package com.cateringplatform.diet.domain

import java.text.Normalizer

object Slug {

    internal fun toSlug(name: String) = Normalizer.normalize(name, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .replace('ł', 'l')
        .replace('Ł', 'L')
        .lowercase()
        .replace("[^a-z0-9]+".toRegex(), "-")
        .trim('-')
}
