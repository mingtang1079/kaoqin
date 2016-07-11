package qinshi.mylibrary.view;



import android.app.Fragment;
import android.os.Bundle;
import android.view.View;


public abstract class LazyFragment extends Fragment {

    private boolean isFristVisible = true;
    private boolean isPrepared = false;
    private boolean isVisible = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isFristVisible = false;
            isVisible = true;
            if (isPrepared)
                onVisible();
        } else isVisible = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepared = true;

        if (isPrepared && isVisible) {
            onFirstVisible();
        } else return;

    }

    public abstract void onFirstVisible();

    public abstract void onVisible();
}
