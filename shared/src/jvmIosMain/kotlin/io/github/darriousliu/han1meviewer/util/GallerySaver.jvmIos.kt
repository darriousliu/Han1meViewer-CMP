package io.github.darriousliu.han1meviewer.util

internal actual suspend fun saveImageToGallery(imageUrl: String) = saveImageViaFileKit(imageUrl)
