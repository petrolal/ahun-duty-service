package com.petrolal.ahun.ahundutyservice.application.ports

/**
 * Outbound port interface for managing file storage (images, background templates).
 * Abstraction layer isolating application core from file system, GCS, or S3 storage adapters.
 */
interface FileStoragePort {
    /**
     * Stores an image file and returns its unique image filename or URI.
     */
    fun saveImage(originalFilename: String, bytes: ByteArray): String

    /**
     * Loads an image as raw byte array given its filename.
     */
    fun loadImageAsBytes(imageName: String): ByteArray

    /**
     * Deletes an image file by filename.
     */
    fun deleteImage(imageName: String): Boolean
}
