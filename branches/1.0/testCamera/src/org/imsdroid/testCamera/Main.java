package org.imsdroid.testCamera;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;


public class Main extends Activity {
	
	private final static String TAG = Main.class.getCanonicalName();
	private Preview mPreview;
	
	LinearLayout llayout;
	Button button;
	//TextView textView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mPreview = new Preview(this, this.previewCallback);
        
        setContentView(R.layout.main);
        
        llayout = (LinearLayout)findViewById(R.id.LinearLayout01);
        button = (Button)findViewById(R.id.Button01);
        //textView = (TextView)findViewById(R.id.TextView01);
        
        //llayout.removeAllViews();
        //llayout.addView(mPreview);
        //setContentView(mPreview);
        
        
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
//				try {
//					textView.setText("Start Profiling...");
//					
//					Method[] m = Class.forName("android.hardware.Camera").getMethods();
//					for(int i=0; i<m.length; i++){
//						Log.d(Main.TAG,"  Camera-method:" + m[i].toString());
//					}
//				} catch (Exception e) {
//					Log.e(Main.TAG, e.toString());
//				}
//				finally
//				{
//					textView.setText("Done!!");
//				}
				
				llayout.removeAllViews();
		        llayout.addView(mPreview);
			}
		}); 
    }
    
    
    PreviewCallback previewCallback = new PreviewCallback() {
    	  public void onPreviewFrame(byte[] _data, Camera _camera) {
    		  int i = _data.length;
    		  i++;
    		  Log.d(Main.TAG, "i="+i);
    	  }
    	};
    
    
 // ----------------------------------------------------------------------

    class Preview extends SurfaceView implements SurfaceHolder.Callback {
        SurfaceHolder mHolder;
        Camera mCamera;
        PreviewCallback callback;
        
        Preview(Context context, PreviewCallback callback) {
            super(context);
            
            this.callback = callback;
            
            //layout(0, 0, 176, 144);
            
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // display
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, acquire the camera and tell it where
            // to draw.
            mCamera = Camera.open();
            try {
            	
            	String packages[] = {
        				"com.dell.android.hardware",
        				"com.android.camera",
        				"android.hardware.Camera"
        		};
            	for(String pckg : packages){
            		try{
	            		Method[] m = Class.forName(pckg).getMethods();
						for(int i=0; i<m.length; i++){
							Log.d(Main.TAG,"  Camera-method:" + m[i].toString());
							
							if(m[i].getName().equals("openDualCamera")){
								Log.d(Main.TAG,"openDualCamera FOUND");
								m[i].invoke(mCamera, (int)1);
								break;
							}
						}
            		}
            		catch (Exception e) {
        				Log.e(Main.TAG, e.toString());
        			}
        		}
            	
            	Camera.Parameters parameters = mCamera.getParameters();
            	parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
            	parameters.setPreviewFrameRate(15);
            	//parameters.setPictureSize(176, 144);
            	mCamera.setParameters(parameters);
            	
               mCamera.setPreviewDisplay(holder);
               mCamera.setPreviewCallback(this.callback);
            } catch (Exception exception) {
                mCamera.release();
                mCamera = null;
                

                Log.e(Main.TAG, exception.toString());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
        	mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(w, h);
            mCamera.setParameters(parameters);
           
           mCamera.startPreview();
        }
    }
}