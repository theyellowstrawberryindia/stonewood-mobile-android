package uk.co.savills.stonewood.util.photo

interface PhotoClickListener {
    fun onAddPhotoClick()

    fun onRemovePhotoClick(filePath: String)
}
