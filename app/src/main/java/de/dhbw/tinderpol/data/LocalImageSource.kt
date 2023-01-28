package de.dhbw.tinderpol.data

import android.util.Log
import de.dhbw.tinderpol.SDO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL

class LocalImageSource {
    companion object {

        suspend fun persistImages(toPersist: MutableSet<Notice>, dir: File){
            withContext(Dispatchers.IO){
                val noticeIds: MutableList<String> = mutableListOf()
                toPersist.forEach {notice ->
                    //notice.id[4] is always '/' which is not allowed in file names. Replaced by '_'.
                    val id: String = notice.id.substring(0,4) + "_" + notice.id.substring(5)

                    if (!notice.imgs.isNullOrEmpty()) {
                        notice.imgs!!.forEachIndexed {index, img ->
                            val file = File(dir,id + "_$index")

                            //skipping existing files because notices are never updated
                            if (!file.exists()) {
                                try {
                                    val imgURL = URL(img)
                                    val inputStream = BufferedInputStream(imgURL.openStream())
                                    val outputStream = ByteArrayOutputStream()
                                    val buffer = ByteArray(90000)
                                    var len: Int
                                    while (-1 != inputStream.read(buffer).also { len = it }) {
                                        outputStream.write(buffer, 0, len)
                                    }
                                    outputStream.close()
                                    inputStream.close()
                                    val response = outputStream.toByteArray()

                                    val fos = FileOutputStream(File(dir, id + "_$index"))
                                    fos.write(response)
                                    fos.close()
                                } catch (e: FileNotFoundException) {
                                    Log.e("LocalImageSource",
                                        "encountered FileNotFoundException while getting image $index for notice ${notice.id}"
                                    )
                                    //no exception handling because intended behaviour is that a file is simply not created when an exception occurs
                                }
                            }
                        }
                    }
                    noticeIds.add(id)
                }
                var fileNames: Array<String> = dir.list() ?: arrayOf()
                val numberFiles: Int = fileNames.size
                fileNames = fileNames.filter { !noticeIds.contains(it.substring(0,10)) }.toTypedArray()
                val numberOldFiles: Int = fileNames.size
                fileNames.forEach {
                    File(dir,it).delete()
                }
                Log.i("LocalImageSource", "images persisted successfully")
                Log.i("LocalImageSource", "images stored: " +  numberFiles.minus(numberOldFiles).toString())
                Log.i("LocalImageSource", "images deleted: $numberOldFiles")
            }
        }

        suspend fun loadImages(dir: File) {
            withContext(Dispatchers.IO) {
                val fileNames: Array<String> = dir.list() ?: arrayOf()

                fileNames.forEach {
                    val image = ByteArray(90000)
                    val noticeId: String = it.substring(0,4) + "/" + it.substring(5,10)
                    val imgIndex: Int = it.substring(11).toInt()
                    val inputStream = FileInputStream(File(dir, it))
                    inputStream.read(image)
                    inputStream.close()
                    if (SDO.localImages[noticeId] == null)
                        SDO.localImages[noticeId] = mutableListOf()

                    SDO.localImages[noticeId]!!.add(imgIndex, image)
                }
            }
        }
    }
}