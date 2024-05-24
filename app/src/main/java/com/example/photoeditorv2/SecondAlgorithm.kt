package com.example.photoeditorv2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

data class Filter(val name: String, val filterFunction: suspend (Bitmap) -> Bitmap)

class FiltersAdapter(
    private val filters: List<Filter>,
    private val sourceBitmap: Bitmap,
    private val onFilterSelected: (Filter) -> Unit
) : RecyclerView.Adapter<FiltersAdapter.FilterViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_layout, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val filter = filters[position]
        holder.bind(filter, sourceBitmap, position == selectedPosition)
    }

    override fun getItemCount() = filters.size

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val filterIcon: ImageView = itemView.findViewById(R.id.filter_icon)
        private val filterName: TextView = itemView.findViewById(R.id.filter_name)

        fun bind(filter: Filter, sourceBitmap: Bitmap, isSelected: Boolean) {
            filterName.text = filter.name
            CoroutineScope(Dispatchers.Main).launch {
                val previewBitmap = withContext(Dispatchers.Default) {
                    Bitmap.createScaledBitmap(sourceBitmap, 100, 100, true)
                }
                val filteredBitmap = filter.filterFunction(previewBitmap)
                filterIcon.setImageBitmap(filteredBitmap)
            }

            itemView.setBackgroundColor(
                if (isSelected) Color.parseColor("#CCCDCF")
                else Color.parseColor("#FFFFFF")
            )

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onFilterSelected(filter)
            }
        }
    }
}

class SecondAlgorithm : AppCompatActivity() {

    private lateinit var originalBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page_for_secondalgorithm)

        val app = application as StorageUriImage
        val uri = app.selectedImageUri

        val imageView = findViewById<ImageView>(R.id.CopyImageInputFilter2)
        if (uri != null) {
            imageView.setImageURI(uri)
        }

        val drawable = imageView.drawable
        if (drawable != null) {
            originalBitmap = drawableToBitmap(drawable)
            val filters = listOf(
                Filter("Red", ::redFilter),
                Filter("Green", ::greenFilter),
                Filter("Blue", ::blueFilter),
                Filter("BW", ::blackAndWhiteFilter),
                Filter("Silhouette", ::silhouetteFilter),
                Filter("Negative", ::negativeFilter),
                Filter("Mosaic", ::mosaicFilter),
                Filter("Contrast", ::contrastFilter),
                Filter("Sharpen", ::sharpenFilter),
                Filter("Vignette", ::vignetteFilter)
            )

            val filtersView = findViewById<RecyclerView>(R.id.filtersView)
            filtersView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            filtersView.adapter = FiltersAdapter(filters, originalBitmap) { filter ->
                applyFilter(imageView, filter.filterFunction)
            }
        }

        findViewById<TextView>(R.id.back).setOnClickListener {
            startActivity(Intent(this, InstrumentsActivity::class.java))
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap = if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
                val canvas = Canvas(this)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
            }
        }
        return bitmap
    }

    private suspend fun redFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = Color.red(color)
            pixels[i] = Color.rgb(r, 0, 0)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun greenFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val g = Color.green(color)
            pixels[i] = Color.rgb(0, g, 0)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun blueFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val b = Color.blue(color)
            pixels[i] = Color.rgb(0, 0, b)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun negativeFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = 255 - Color.red(color)
            val g = 255 - Color.green(color)
            val b = 255 - Color.blue(color)
            pixels[i] = Color.rgb(r, g, b)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun blackAndWhiteFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val bw = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
            pixels[i] = Color.rgb(bw, bw, bw)
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun mosaicFilter(source: Bitmap, blockSize: Int = 15): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height step blockSize) {
            for (x in 0 until width step blockSize) {
                var redSum = 0
                var greenSum = 0
                var blueSum = 0
                var pixelsInCurrentBlock = 0

                for (j in 0 until blockSize) {
                    for (i in 0 until blockSize) {
                        val pixelY = y + j
                        val pixelX = x + i
                        if (pixelY < height && pixelX < width) {
                            val color = pixels[pixelY * width + pixelX]
                            redSum += Color.red(color)
                            greenSum += Color.green(color)
                            blueSum += Color.blue(color)
                            pixelsInCurrentBlock++
                        }
                    }
                }

                val averageRed = redSum / pixelsInCurrentBlock
                val averageGreen = greenSum / pixelsInCurrentBlock
                val averageBlue = blueSum / pixelsInCurrentBlock
                val averageColor = Color.rgb(averageRed, averageGreen, averageBlue)

                for (j in 0 until blockSize) {
                    for (i in 0 until blockSize) {
                        val pixelY = y + j
                        val pixelX = x + i
                        if (pixelY < height && pixelX < width) {
                            pixels[pixelY * width + pixelX] = averageColor
                        }
                    }
                }
            }
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun contrastFilter(source: Bitmap, contrastLevel: Double = -100.0): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val factor = (259 * (contrastLevel + 255)) / (255 * (259 - contrastLevel))

        for (i in pixels.indices) {
            val color = pixels[i]

            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val newRed = (factor * (red - 128) + 128).coerceIn(0.0, 255.0)
            val newGreen = (factor * (green - 128) + 128).coerceIn(0.0, 255.0)
            val newBlue = (factor * (blue - 128) + 128).coerceIn(0.0, 255.0)

            pixels[i] = Color.rgb(newRed.toInt(), newGreen.toInt(), newBlue.toInt())
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun sharpenFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val matrix = arrayOf(
            intArrayOf(-1, -1, -1),
            intArrayOf(-1, 9, -1),
            intArrayOf(-1, -1, -1)
        )

        val newPixels = IntArray(pixels.size)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var sumRed = 0
                var sumGreen = 0
                var sumBlue = 0

                for (i in -1..1) {
                    for (j in -1..1) {
                        val neighborPixel = pixels[(y + j) * width + x + i]
                        val weight = matrix[j + 1][i + 1]

                        sumRed += Color.red(neighborPixel) * weight
                        sumGreen += Color.green(neighborPixel) * weight
                        sumBlue += Color.blue(neighborPixel) * weight
                    }
                }

                sumRed = sumRed.coerceIn(0, 255)
                sumGreen = sumGreen.coerceIn(0, 255)
                sumBlue = sumBlue.coerceIn(0, 255)

                newPixels[y * width + x] = Color.rgb(sumRed, sumGreen, sumBlue)
            }
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun vignetteFilter(source: Bitmap, strength: Double = 0.8): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val centerX = width / 2
        val centerY = height / 2
        val maxDistance = Math.sqrt((centerX * centerX + centerY * centerY).toDouble())

        for (y in 0 until height) {
            for (x in 0 until width) {
                val distance = Math.sqrt(((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)).toDouble())
                val vignette = 1 - distance / maxDistance * strength

                val index = y * width + x
                val color = pixels[index]

                val red = (Color.red(color) * vignette).toInt().coerceIn(0, 255)
                val green = (Color.green(color) * vignette).toInt().coerceIn(0, 255)
                val blue = (Color.blue(color) * vignette).toInt().coerceIn(0, 255)

                pixels[index] = Color.rgb(red, green, blue)
            }
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private suspend fun silhouetteFilter(source: Bitmap): Bitmap = withContext(Dispatchers.Default) {
        val width = source.width
        val height = source.height
        val pixels = IntArray(width * height)
        source.getPixels(pixels, 0, width, 0, 0, width, height)

        val threshold = 128

        for (i in pixels.indices) {
            val color = pixels[i]
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val brightness = (red + green + blue) / 3

            val newColor = if (brightness > threshold) Color.WHITE else Color.BLACK

            pixels[i] = newColor
        }

        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        newBitmap
    }

    private fun applyFilter(imageView: ImageView, filterFunction: suspend (Bitmap) -> Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            val filteredBitmap = filterFunction(originalBitmap)
            imageView.setImageBitmap(filteredBitmap)
        }
    }
}