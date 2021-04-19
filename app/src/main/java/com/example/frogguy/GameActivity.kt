package com.example.frogguy

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs
import kotlin.math.floor


// variables for the sprites on objects
lateinit var b_frog: Bitmap
lateinit var b_log_base: Bitmap
lateinit var b_log: Bitmap
lateinit var b_lilly:Bitmap
lateinit var b_lilly2:Bitmap
lateinit var b_frog_scaled:Bitmap
lateinit var b_car:Bitmap
lateinit var b_truck:Bitmap
var score = 0


class GameActivity : AppCompatActivity(){
    private lateinit var mDetector: GestureDetectorCompat
    lateinit var outBox: TextView
    lateinit var frog: Frogger
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.supportActionBar?.hide()

        var height = resources.displayMetrics.heightPixels
        var width = resources.displayMetrics.widthPixels
        val horzBlockCount = 15F
        var blockDim = width/horzBlockCount
        var vertBlockCount = floor(height / blockDim)-4
        var vertPadding = (height - vertBlockCount*blockDim)/2

        var r = getResources()
        b_frog = BitmapFactory.decodeResource(r, R.drawable.frog)
        b_log_base = BitmapFactory.decodeResource(r, R.drawable.log)
        b_lilly = BitmapFactory.decodeResource(r, R.drawable.lilly1)
        b_lilly2 = BitmapFactory.decodeResource(r, R.drawable.lilly2) // currently unused ...
        b_car = BitmapFactory.decodeResource(r, R.drawable.car)
        b_truck = BitmapFactory.decodeResource(r, R.drawable.truck)


        frog = Frogger(blockDim, vertBlockCount, horzBlockCount, vertPadding, false, this)
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
        var r = getResources()
        b_log_base = BitmapFactory.decodeResource(r, R.drawable.log)
        frog.color.color = Color.rgb(0, 120, 0)
        frog.remainOnScreen = true
        this.setBackgroundColor(Color.BLACK)

        roads = arrayListOf(
                ObstaclePath(3, 4F, 2, true),
                ObstaclePath(4, 3F, 1, true),
                ObstaclePath(5, 2F, 2, true),
                ObstaclePath(6, 3F, 2, true),
                ObstaclePath(7, 1F, 3, true),
                ObstaclePath(9, -1F, 2, true),
                ObstaclePath(10, -3F, 2, true),
                ObstaclePath(11, -5F, 1, true),
                ObstaclePath(12, -1F, 2, true),
                ObstaclePath(13, -2F, 3, true)

        )
//        roads = arrayListOf(arrayOf(8,5,4,ArrayList<Sprite>(0)), arrayOf(9,-5), arrayOf(10,5), arrayOf(11,-5))
        rivers = arrayListOf(
                ObstaclePath(16, 2F, 4),
                ObstaclePath(17, 3F, 4),
                ObstaclePath(18, -3F, 4),
                ObstaclePath(19, 4F, 2),
                ObstaclePath(20, -2F, 4),
                ObstaclePath(21, -5F, 2)

        )



    }

    override fun onDraw(canvas: Canvas?) {
        var f = frog.toPixels()
        if (::b_frog_scaled.isInitialized == false) {
            b_frog_scaled = Bitmap.createScaledBitmap(
                    b_frog,
                    (f[3] - f[1]).toInt(),
                    (f[2] - f[0]).toInt(),
                    false
            )
        }

        if(won){
            score += 10 // bonus for winning

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
                canvas?.drawRect(0F, verticalPadding, horzBlocks * blockDim, verticalPadding + blockDim, goal)
            }

            for (river in rivers) {
                for (sp in river.obs) {
                    var spLoc = sp.toPixels()
                    canvas?.drawBitmap(sp.img, spLoc[0], spLoc[3], sp.color)
                }
            }



            var fLoc = frog.toPixels()
            canvas?.drawBitmap(b_frog_scaled, fLoc[0], fLoc[3], frog.color)
            //canvas?.drawRect(fLoc[0], fLoc[1], fLoc[2], fLoc[3], frog.color)

            for (road in roads) {
                for (sp in road.obs) {
                    var spLoc = sp.toPixels()
                    //canvas?.drawRect(spLoc[0], spLoc[1], spLoc[2], spLoc[3], sp.color)
                    canvas?.drawBitmap(sp.img, spLoc[0], spLoc[3], sp.color)
                }
            }
        }

        var pz = Paint()
        pz.setColor(Color.WHITE)
        // for modulatiry set this size to be based on the screen size instead :)
        pz.setTextSize(50f)
        canvas?.drawText("Score:" + score, 10f, 100f, pz)

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
        score += 1
        if (++frog.y >= vertBlocks) {

            frog.y = vertBlocks - 1
        }
        if(!endless && frog.y == vertBlocks -1 ){
            //won=true
                score += 5 // bonus for suviving
            frog.reset(true)
        }
        // This will cause x to be changed to the closest target x the remove all offset
        if(frog.xoffset != 0F){
            frog.xoffset += (frog.xoffset/abs(frog.xoffset))*blockDim/2
            frog.xoffset = 0F
        }
    }

    fun down() {
        score -= 1
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

    inner class Sprite(var x: Float = 0F, var y: Float = 0F, var xoffsetInit: Float = 0F, var yoffsetInit: Float = 0F, var xsize: Float = 1F, var ysize: Float = 1F, var remainOnScreen: Boolean = false, var type: Int = 0){
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
        lateinit var img:Bitmap
        private var inital = arrayOf<Float>(x, y)
        init{
            color.color = Color.WHITE
            var z = toPixels()
            if (xsize == 1f) {
                if (type == 1) {
                    if (xoffset == 0f) { //set the direction of the sprite
                        // im not actually doing anything here lol the car sprite is so low res that you really can't tell that it's backwards
                        img = Bitmap.createScaledBitmap(b_car, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                    } else {
                        img = Bitmap.createScaledBitmap(b_car, (z[0]).toInt(), (z[1] - z[3]).toInt(), false)
                    }
                }else {
                    img = Bitmap.createScaledBitmap(b_lilly, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                }
            }else {
                if (type == 1) {
                    if (xoffset == 0f) { // flips the trucks the correct direction
                        // this is the one to be changed
                        img = Bitmap.createScaledBitmap(b_truck, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                    }else {
                        img = Bitmap.createScaledBitmap(b_truck, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                        val matrix = Matrix()
                        matrix.setScale((-1).toFloat(), 1f)
                        matrix.postTranslate(img.getWidth().toFloat(), 0f)
                        img = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true)
                    }
                }else {
                    img = Bitmap.createScaledBitmap(b_log_base, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                }

            }
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
        fun setImage(){ // wait do I ever run this ? lol I think this got moved to the contructor...
            var z = toPixels()
            if (xsize == 1f) { // check size for small vs large
                if (type == 1) { // check type for what kind of object it is
                    img = Bitmap.createScaledBitmap(b_car, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)

                }else {
                    img = Bitmap.createScaledBitmap(b_lilly, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                }

            }else {
                if (type == 1) {
                    img = Bitmap.createScaledBitmap(b_truck, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                }else {
                    img = Bitmap.createScaledBitmap(b_log_base, (z[0] - z[2]).toInt(), (z[1] - z[3]).toInt(), false)
                }

            }
        }
//        fun showToast(toast: String?) {
//            activity.runOnUiThread(Runnable { Toast.makeText(context, toast, Toast.LENGTH_SHORT).show() })
//        }
        fun reset(survived: Boolean = false){
            var alert = AlertDialog.Builder(context)
            Handler(Looper.getMainLooper()).post {
                if (survived) {
                    alert.setTitle("Victory")
                    alert.setMessage("You completed this level! move on to try for a better score, or save this score?")
                    alert.setPositiveButton("Continue", { dialogInterface: DialogInterface, i: Int -> }) // we have to do even less than we had to do with the other one :)
                    alert.setNegativeButton("Save my score and exit", { dialogInterface: DialogInterface, i: Int -> })
                    alert.show()

                }else {
                    alert.setTitle("Defeat")
                    alert.setMessage("You died, try again for a better score, or save this score to see where it is on the leaderboard? ")
                    alert.setPositiveButton("Try again", { dialogInterface: DialogInterface, i: Int -> score = 0}) // that's all we have to do
                    alert.setNegativeButton("Save my score and exit") { dialogInterface: DialogInterface, i: Int -> } // quit the game, and save the score :)
                    alert.show()
                }
            }
            x = inital[0]
            y = inital[1]
            xoffset = 0F
            yoffset = 0F
//            this.color.color = color
        }
    }
    inner class ObstaclePath(var verticalRow: Int, var speed: Float, var maxObsticals: Int, var deadly: Boolean = false){
        var obs = ArrayList<Sprite>(maxObsticals)
        var maxLength = if(deadly){3}else{4}
        var color = Paint()
        var extraSprites = ArrayList<Sprite>(0)

        init{
            color.color = if(deadly){Color.BLACK}else{Color.BLUE}
            for (sprite in obs){
                if (sprite.type == 0) { // if the sprites don't have a type yet, then set it here :)
                    if (deadly) {
                        // these sprites are cars
                        sprite.type = 1
                    }else {
                        sprite.type = 2
                    }
                    sprite.setImage()
                }
            }
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
                if (deadly) {
                    obs.add(Sprite(startLoc, verticalRow.toFloat(), initOffset, 0F, len.toFloat(), 1F, false, 1))
                }else {
                    obs.add(Sprite(startLoc, verticalRow.toFloat(), initOffset, 0F, len.toFloat(), 1F, false, 2))
                }
                //obs.add(Sprite(startLoc, verticalRow.toFloat(), initOffset, 0F, len.toFloat(), 1F))
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
            //return true // testing the game, because I'm bad :)
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

