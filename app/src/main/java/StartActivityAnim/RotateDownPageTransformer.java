package StartActivityAnim;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;

public class RotateDownPageTransformer implements PageTransformer{

	private static final float MAX_ROTATE = 20f;
	
	private float mRot;
	
	@SuppressLint("NewApi") @Override
	public void transformPage(View view, float position) {

		int pageWidth = view.getWidth();
		
		if (position < -1) {
			view.setRotation(0);
			
		}else if (position <= 0) {
			mRot = position * MAX_ROTATE;
			
			view.setPivotX(pageWidth / 2);
			view.setPivotY(view.getMeasuredHeight());
			view.setRotation(mRot);
			
		}else if (position <= 1) {
			mRot = position * MAX_ROTATE;
			
			view.setPivotX(pageWidth / 2);
			view.setPivotY(view.getMeasuredHeight());
			view.setRotation(mRot);
		}else {
			view.setRotation(0);
		}
	}

}
