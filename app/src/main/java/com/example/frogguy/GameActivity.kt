package com.example.frogguy

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs
import kotlin.math.floor

class GameActivity : AppCompatActivity(){
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


        frog = Frogger(blockDim, vertBlockCount, horzBlockCount, vertPadding, false,this)
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

class Frogger(var blockDim: Float, var vertBlocks: Float, var horzBlocks: Float, var verticalPadding: Float, var endless: Boolean, context: Context?) : View(context) {
    private var frog = Sprite(floor(horzBlocks.toFloat() / 2), 0F)
    private var won = false
    private var score = 0
    private lateinit var nextLevelBtn: Button

    private lateinit var canvas: Canvas
    var roads = ArrayList<ObstaclePath>(0)
    var rivers = ArrayList<ObstaclePath>(0)

    init {
        frog.color.color = Color.rgb(0, 120, 0)
        frog.remainOnScreen = true
        this.setBackgroundColor(Color.BLACK)

        roads = arrayListOf(
                ObstaclePath(3,4F,2,true),
                ObstaclePath(4,3F,1,true),
                ObstaclePath(5,2F,2,true),
                ObstaclePath(6,3F,2,true),
                ObstaclePath(7,1F, 3, true),
                ObstaclePath(9,-1F, 2, true),
                ObstaclePath(10,-3F,2,true),
                ObstaclePath(11,-5F,1,true),
                ObstaclePath(12,-1F,2,true),
                ObstaclePath(13,-2F, 3, true)

                )
//        roads = arrayListOf(arrayOf(8,5,4,ArrayList<Sprite>(0)), arrayOf(9,-5), arrayOf(10,5), arrayOf(11,-5))
        rivers = arrayListOf(
                ObstaclePath(16,2F, 4),
                ObstaclePath(17,3F, 4),
                ObstaclePath(18,-3F, 4),
                ObstaclePath(19,4F, 2),
                ObstaclePath(20,-2F, 4),
                ObstaclePath(21,-5F, 2)

                )



    }

    override fun onDraw(canvas: Canvas?) {

        if(won){
            // Win message or redirect here
        }else {

            var field = Paint()
            field.color = Color.rgb(100, 50, 0)
            canvas?.drawRect(0F, verticalPadding, horzBlocks * blockDim, verticalPadding + vertBlocks * blockDim, field)

            for (pathType in arrayOf(roads, rivers)) {
                for (path in pathType) {
                    var row = verticalPadding + blockDim * (vertBlocks - path.verticalRow)
                    canvas?.drawRect(0F, row, horzBlocks * blockDim, row - blockDim, path.color)
                }
            }

            if(!endless){
                var goal = Paint()
                goal.color = Color.YELLOW
                canvas?.drawRect(0F, verticalPadding, horzBlocks*blockDim, verticalPadding+blockDim, goal)
            }

            for (river in rivers) {
                for (sp in river.obs) {
                    var spLoc = sp.toPixels()
                    canvas?.drawRect(spLoc[0], spLoc[1], spLoc[2], spLoc[3], sp.color)
                }
            }



            var fLoc = frog.toPixels()
            canvas?.drawRect(fLoc[0], fLoc[1], fLoc[2], fLoc[3], frog.color)

            for (road in roads) {
                for (sp in road.obs) {
                    var spLoc = sp.toPixels()
                    canvas?.drawRect(spLoc[0], spLoc[1], spLoc[2], spLoc[3], sp.color)
                }
            }
        }


        super.onDraw(canvas)
    }

    fun manageSprites(){
        for(pathType in arrayOf(roads, rivers)){
            for(path in pathType) {
                path.checkSpawn()
                path.moveSprites()
                if(path.verticalRow.toFloat() == frog.y){
                    if(!path.frogSafe(frog)){
                        frog.reset()
                    }
                }
            }
        }
    }

    fun up() {
        if (++frog.y >= vertBlocks) {

            frog.y = vertBlocks - 1
        }
        if(!endless && frog.y == vertBlocks -1 ){
            won=true
        }
        // This will cause x to be changed to the closest target x the remove all offset
        if(frog.xoffset != 0F){
            frog.xoffset += (frog.xoffset/abs(frog.xoffset))*blockDim/2
            frog.xoffset = 0F
        }


    }

    fun down() {
        if (--frog.y < 0) {
            frog.y = 0F
        }
        // This will cause x to be changed to the closest target x the remove all offset
        if(frog.xoffset != 0F){
            frog.xoffset += (frog.xoffset/abs(frog.xoffset))*blockDim/2
            frog.xoffset = 0F
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

    inner class Sprite(var x: Float = 0F,var y: Float = 0F,var xoffsetInit: Float = 0F,var yoffsetInit: Float = 0F,var xsize: Float = 1F,var ysize: Float = 1F, var remainOnScreen:Boolean=false){
        var xoffset: Float = xoffsetInit
            set(newVal){
                if(newVal >= blockDim){
                    field = newVal-blockDim
                    x++
                }else if(newVal <= -blockDim){
                    field = newVal+blockDim
                    x--
                }else{
                    field = newVal
                }
                if(remainOnScreen && x <= 0 && field < 0){
                    x = 0F
                    field = 0F
                }else if(remainOnScreen && x >= horzBlocks-1 && field > 0){
                    x = horzBlocks-1
                    field = 0F
                }
            }
        var yoffset: Float = yoffsetInit
        var color = Paint()

        private var inital = arrayOf<Float>(x, y)
        init{
            color.color = Color.WHITE
        }

        fun on(other: Sprite):Boolean{
            var offsets = xoffset-other.xoffset
            var start = if(blockDim/2 <= abs(offsets)){
                x+(offsets/abs(offsets))
            }else{x}
            var end = start+xsize-1
            if(y == other.y && start <= other.x && end >= other.x){
                return true
            }
            return false
        }
        fun touches(other: Sprite):Boolean{
            var start = x
            if(xoffset < 0){
                start--
            }
            var end = start+xsize
            if(y == other.y && start <= other.x && end >= other.x){
                return true
            }
            return false
        }

        fun toPixels(): Array<Float> {
            return Array(4) {
                if (it % 2 == 0) {
                    blockDim * (x + ((it.toFloat() / 2) * xsize))+xoffset
                } else {
                    verticalPadding + blockDim * (vertBlocks - y - ((it.toFloat() - 1)  / 2) * ysize) + yoffset
                }
            }
        }

        fun reset(){
            x = inital[0]
            y = inital[1]
            xoffset = 0F
            yoffset = 0F
//            this.color.color = color
        }
    }
    inner class ObstaclePath(var verticalRow: Int, var speed: Float, var maxObsticals: Int, var deadly: Boolean =false){
        var obs = ArrayList<Sprite>(maxObsticals)
        var maxLength = if(deadly){3}else{4}
        var color = Paint()
        var extraSprites = ArrayList<Sprite>(0)

        init{
            color.color = if(deadly){Color.BLACK}else{Color.BLUE}
        }

        fun checkSpawn(){
            if(obs.size < maxObsticals && (0..2).random() <= 1 ){
                for(sprite in obs){
                    if(speed > 0 && sprite.x < 3){
                        return
                    }else if(speed < 0 && sprite.x + sprite.xsize > horzBlocks-3){
                        return
                    }
                }
                var len = (1..maxLength).random()
                var startLoc = if(speed>0){0F}else{horzBlocks}
                var initOffset = if(speed>0){-len*blockDim}else{0F}
                obs.add(Sprite(startLoc, verticalRow.toFloat(), initOffset, 0F, len.toFloat(), 1F))
            }
        }
        fun moveSprites(){
            var tmpArr = obs.clone() as ArrayList<Sprite>
            for(sprite in tmpArr){
                sprite.xoffset += speed
                if(speed > 0 && sprite.x > horzBlocks){
                    obs.remove(sprite)
                }else if(speed < 0 && sprite.x < 0-sprite.xsize){
                    obs.remove(sprite)
                }
            }
            tmpArr = obs.clone() as ArrayList<Sprite>
            for(extra in tmpArr){
                if(extra.y == verticalRow.toFloat()) {
                    extra.xoffset += speed
                }

                if(!extra.remainOnScreen){
                    if(speed > 0 && extra.x > horzBlocks){
                        obs.remove(extra)
                    }else if(speed < 0 && extra.x < 0-extra.xsize){
                        obs.remove(extra)
                    }
                }
            }
        }
        fun frogSafe(frog: Sprite): Boolean{
            for(sprite in obs){
                if(!deadly && sprite.on(frog)) {
                    if(!extraSprites.contains(frog)){extraSprites.add(frog)}
                    var offsetDif = sprite.xoffset - frog.xoffset
                    if(abs(offsetDif) >= blockDim/2){
                        frog.x -= offsetDif/abs(offsetDif)
                    }
                    frog.xoffset = sprite.xoffset
                    return true

                }else if(deadly && sprite.touches(frog)){
                    return false
                }
            }
            if(!deadly && extraSprites.contains(frog)){
                extraSprites.remove(frog)
            }
            return deadly
        }


    }
}

class Updater(var updateInterval: Long, var gameCanvas: Frogger): Thread() {
    var kill = false
    override fun run() {
        while(!kill){
            sleep(updateInterval)
            gameCanvas.manageSprites()
            gameCanvas.invalidate()
        }
    }

}

