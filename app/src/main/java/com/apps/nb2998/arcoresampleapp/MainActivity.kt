package com.apps.nb2998.arcoresampleapp

import android.annotation.SuppressLint
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.core.Point
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var arFragment: ArFragment
    private var isTracking = false
    private var isHitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        arFragment = sceneform_frag as ArFragment
        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_frag) as ArFragment
        arFragment.arSceneView.scene.addOnUpdateListener {
            arFragment.onUpdate(it)
            onUpdate()
        }

        fab.setOnClickListener{
            addObject(Uri.parse("model.sfb"))
        }
    }

    private fun addObject(parse: Uri) {
        val frame = arFragment.arSceneView.arFrame
        val point = getScreenCenter()
        if(frame != null) {
            val hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for(hit in hits) {
                val trackable  = hit.trackable
                if(trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(arFragment, hit.createAnchor(), parse)
                    break
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private fun placeObject(fragment: ArFragment, createAnchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept {
                addNodeToScene(fragment, createAnchor, it)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message)
                    .setTitle("error!")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun addNodeToScene(fragment: ArFragment, createAnchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(createAnchor)
        val transformableNode = TransformableNode(fragment.transformationSystem)
        transformableNode.renderable = renderable
        transformableNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }

    private fun getScreenCenter(): android.graphics.Point {
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.Point(vw.width / 2, vw.height / 2)
    }

    private fun onUpdate() {

    }
}
