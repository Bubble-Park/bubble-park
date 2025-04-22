package fr.iutlens.mmi.demo.utils

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener


@UnstableApi
class ResolvingByteArrayDataSource(private val uriResolver: (Uri) -> ByteArray) : DataSource {
    private lateinit var uri: Uri
    private lateinit var byteArrayDataSource: ByteArrayDataSource
    private val listeners = ArrayList<TransferListener>(1)

    class Factory(private val uriResolver: (Uri) -> ByteArray) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return ResolvingByteArrayDataSource(uriResolver)
        }
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        return byteArrayDataSource.read(buffer, offset, length)
    }

    override fun addTransferListener(transferListener: TransferListener) {
        if (transferListener !in listeners) {
            listeners.add(transferListener)
        }
    }

    override fun open(dataSpec: DataSpec): Long {
        uri = dataSpec.uri
        byteArrayDataSource = ByteArrayDataSource(uriResolver(uri))
        listeners.forEach { byteArrayDataSource.addTransferListener(it) }
        return byteArrayDataSource.open(dataSpec)
    }

    override fun getUri(): Uri = uri

    override fun close() {
        byteArrayDataSource.close()
    }
}