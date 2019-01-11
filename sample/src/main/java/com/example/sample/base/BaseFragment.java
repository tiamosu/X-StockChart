package com.example.sample.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected Activity mActivity;
    private Unbinder mUnbinder;

    protected abstract int getLayoutId();

    protected abstract void onLoadData(View rootView);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = null;
        if (getLayoutId() > 0) {
            rootView = inflater.inflate(getLayoutId(), container, false);
            mUnbinder = ButterKnife.bind(this, rootView);

            onLoadData(rootView);
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        if (toast != null) {
            toast.cancel();
        }
        if (mUnbinder != null && mUnbinder != Unbinder.EMPTY) {
            try {
                //fix Bindings already cleared
                mUnbinder.unbind();
            } catch (IllegalStateException ignored) {
            }
            mUnbinder = null;
        }
        super.onDestroy();
    }

    protected void startToActivity(Class<?> cls) {
        final Intent intent = new Intent(mActivity, cls);
        mActivity.startActivity(intent);
    }

    private Toast toast;

    @SuppressLint("ShowToast")
    public void showToast(CharSequence string) {
        if (toast == null) {
            toast = Toast.makeText(mActivity, string, Toast.LENGTH_SHORT);
        } else {
            toast.setText(string);
        }
        toast.show();
    }
}
