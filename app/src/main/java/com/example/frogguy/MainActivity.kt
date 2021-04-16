package com.example.frogguy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs
import kotlin.math.floor

class MainActivity : AppCompatActivity(){
    private lateinit var mDetector: GestureDetectorCompat
    lateinit var outBox: TextView
    lateinit var frog: Frogger
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportActionBar?.hide()

        var height = resources.displayMetrics.heightPixels
        var width = resources.displayMetrics.widthPixels
        val horzBlockCount = 15F
        var blockDim = width/horzBlockCount
        var vertBlockCount = floor(height/blockDim)-4
        var vertPadding = (height - vertBlockCount*blockDim)/2


        frog = Frogger(blockDim, vertBlockCount, horzBlockCount, vertPadding, this)
        mDetector = GestureDetectorCompat(this, MyGestureListener())
        setContentView(frog)

        var updaterThread = Updater(20, frog)

        updaterThread.start()

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class MyGestureListener (): GestureDetector.SimpleOnGestureListener() {

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
//            Log.i(DEBUG_TAG, "$velocityX, $velocityY")
            if(abs(velocityX) > abs(velocityY)){
                if(velocityX > 0){frog.right()}else{frog.left()}
            }else{
                if(velocityY > 0){frog.down()}else{frog.up()}
            }
            return true
        }


    }
}

class Frogger(var blockDim: Float, var vertBlocks: Float, var horzBlocks: Float, var verticalPadding: Float, context: Context?) : View(context) {
    private var frog = Sprite(floor(horzBlocks.toFloat() / 2), 0F)
    private var roadSprites = ArrayList<Sprite>(0)
    private var waterSprites = ArrayList<Sprite>(0)
    private lateinit var canvas: Canvas

    init {
        frog.color.color = Color.rgb(0, 120, 0)
        this.setBackgroundColor(Color.BLACK)


    }

    override fun onDraw(canvas: Canvas?) {

        var field = Paint()
        field.color = Color.rgb(100, 50, 0)
        canvas?.drawRect(0F, verticalPadding, horzBlocks*blockDim, verticalPadding+vertBlocks*blockDim, field)

        for(sp in waterSprites){
            var spLoc = sp.toPixels()
            canvas?.drawRect(spLoc[0], spLoc[1], spLoc[2], spLoc[3], sp.color)
        }


        var fLoc = frog.toPixels()
        canvas?.drawRect(fLoc[0], fLoc[1], fLoc[2], fLoc[3], frog.color)

        for(sp in roadSprites){
            var spLoc = sp.toPixels()
            canvas?.drawRect(spLoc[0], spLoc[1], spLoc[2], spLoc[3], sp.color)
        }

        super.onDraw(canvas)
    }

    fun manageSprites(){
        if(roadSprites.size < 1){
            roadSprites.add(Sprite(0F,10F, -(blockDim*2), xsize = 2F))
        }
        for(sp in roadSprites){
            sp.xoffset += 5
            if(sp.x > horzBlocks){
                roadSprites.remove(sp)
            }
        }
        if(waterSprites.size < 1){
            waterSprites.add(Sprite(0F, 19F, -(blockDim*4), xsize = 4F, ysize = 0.6F))
        }
        for(sp in waterSprites){
            sp.xoffset += 3
            if(sp.x > horzBlocks){
                waterSprites.remove(sp)
            }
        }

    }

    fun up() {
        if (++frog.y >= vertBlocks) {
            frog.y = vertBlocks - 1
        }
    }

    fun down() {
        if (--frog.y < 0) {
            frog.y = 0F
        }
    }

    fun left() {
        if (--frog.x < 0) {
            frog.x = 0F
        }
    }

    fun right() {
        if (++frog.x >= horzBlocks) {
            frog.x = horzBlocks - 1
        }
    }

    inner class Sprite(
            var x: Float = 0F,
            var y: Float = 0F,
            var xoffsetInit: Float = 0F,
            var yoffsetInit: Float = 0F,
            var xsize: Float = 1F,
            var ysize: Float = 1F){
        var xoffset: Float = xoffsetInit
            set(newVal){
                Log.i("frogger", field.toString())
                if(newVal >= blockDim){
                    field = newVal-blockDim
                    x++
                }else if(newVal <= -blockDim){
                    field = newVal+blockDim
                    x--
                }else{
                    field = newVal
                }
            }
        var yoffset: Float = yoffsetInit
        var color = Paint()
        init{
            color.color = Color.WHITE
        }

        fun toPixels(): Array<Float> {
            return Array<Float>(4) {
                if (it % 2 == 0) {
                    blockDim * (x + ((it.toFloat() / 2) * xsize))+xoffset
                } else {
                    verticalPadding + blockDim * (vertBlocks - y - ((it.toFloat() - 1)  / 2) * ysize) + yoffset
                }
            }
        }
    }
}

class Updater(var updateInterval: Long, var gameCanvas: Frogger): Thread() {
    var kill = false
    override fun run() {
        while(!kill){
            Thread.sleep(updateInterval)
            gameCanvas.manageSprites()
            gameCanvas.invalidate()
        }
    }

}