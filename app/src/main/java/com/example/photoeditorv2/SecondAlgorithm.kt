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
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
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
                Filter("Negative", ::negativeFilter),
                Filter("Mosaic", ::mosaicFilter),
                Filter("Contrast", ::contrastFilter)
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

            var newRed = factor * (red - 128) + 128
            var newGreen = factor * (green - 128) + 128
            var newBlue = factor * (blue - 128) + 128

            if (newRed > 255) {
                newRed = 255.0
            }
            if (newGreen > 255) {
                newGreen = 255.0
            }
            if (newBlue > 255) {
                newBlue = 255.0
            }

            if (newRed < 0) {
                newRed = 0.0
            }
            if (newGreen < 0) {
                newGreen = 0.0
            }
            if (newBlue < 0) {
                newBlue = 0.0
            }

            pixels[i] = Color.rgb(newRed.toInt(), newGreen.toInt(), newBlue.toInt())
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