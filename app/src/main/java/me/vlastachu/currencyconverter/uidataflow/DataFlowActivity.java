package me.vlastachu.currencyconverter.uidataflow;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public abstract class DataFlowActivity<ViewModel, State extends Parcelable, Actions, ViewInterface>
        extends AppCompatActivity
        implements UI<ViewModel> {
    private UIDataFlow<State, ViewModel, Actions, ViewInterface> uiDataFlow;
    private static final String STATE_KEY = "STATE_KEY";
    private static final String TAG = DataFlowActivity.class.getSimpleName();
    protected Boolean actionsLogEnabled = true;
    private State state = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            state = (State) savedInstanceState.getParcelable(STATE_KEY);
            if (state != null) {
                Log.d(TAG, "onCreate: state restored from persist storage");
            }
        }
        if (state == null) {
            Log.d(TAG, "onCreate: new state initialed");
            state = getStateUpdater().getInitState(getView());
        }
        restoreDataFlow();
    }

    @Override
    protected void onResume() {
        super.onResume();
        restoreDataFlow();
        uiDataFlow.attachUI(this);
    }

    private void restoreDataFlow() {
        if (uiDataFlow == null) {
            Object nci = getLastCustomNonConfigurationInstance();
            if (nci != null && nci instanceof UIDataFlow) {
                Log.d(TAG, "onResume: restore uiDataFlow reference from nci");
                uiDataFlow = (UIDataFlow<State, ViewModel, Actions, ViewInterface>) nci;
            } else {
                Log.d(TAG, "onResume: create new uiDataFlow instance");
                uiDataFlow = new UIDataFlow<>(state, getStateUpdater(), getStateToViewModel());
            }
        } else {
            Log.d(TAG, "onResume: uiDataFlow instance kept");
        }
        Log.d(TAG, "attach ui on resume");
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "detach ui (activity) reference from uiDataFlow on pause");
        uiDataFlow.detachUI(this);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "persist state on activity destroy");
        outState.putParcelable(STATE_KEY, uiDataFlow.getLastState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d(TAG, "keep reference to uiDataFlow on screen rotation");
        return uiDataFlow;
    }

    @Override
    abstract public void showViewModel(ViewModel viewModel);

    @Override
    public boolean animateChanges(ViewModel previous, ViewModel current) {
        return true;
    }

    // activity must implement ViewInterface
    // otherwise will be exception on resume
    private ViewInterface getView() {
        return (ViewInterface) this;
    }

    abstract protected StateUpdater<State, Actions, ViewInterface> getStateUpdater();
    abstract protected StateToViewModel<State, ViewModel> getStateToViewModel();

    protected void send(Actions action) {
        if (actionsLogEnabled) {
            Log.i(TAG, "send action: " + action);
        }
        uiDataFlow.sendAction(getView(), action);
    }
}
