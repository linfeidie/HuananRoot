package ac.scri.com.huananroot.view.nicedialog;

import android.support.v4.app.DialogFragment;
import android.view.View;

public interface OnItemClickLitener {
	
	void onItemClick(View view, int position, DialogFragment dialogFragment);
	void onItemLongClick(View view, int position);
}