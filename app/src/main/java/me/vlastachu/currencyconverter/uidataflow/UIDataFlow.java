package me.vlastachu.currencyconverter.uidataflow;

import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


// FIXME should be many ViewModels probably
// viewModel - explicit denormalized state of view, like model in this article http://hannesdorfmann.com/android/mosby3-mvi-1
public final class UIDataFlow<State, ViewModel, Actions, ViewInterface> {
    private State lastState;
    private List<UI<ViewModel>> uiList = new ArrayList<>();
    private final StateUpdater<State, Actions, ViewInterface> updater;
    private final StateToViewModel<State, ViewModel> presenter;

    public UIDataFlow(State initState,
                      StateUpdater<State, Actions, ViewInterface> updater,
                      StateToViewModel<State, ViewModel> presenter) {
        this.updater = updater;
        lastState = initState;
        this.presenter = presenter;
    }

    public void attachUI(UI<ViewModel> ui) {
        if (!uiList.contains(ui)) {
            uiList.add(ui);
            ui.showViewModel(presenter.present(lastState));
        }
        // log info
    }

    public void detachUI(UI<ViewModel> ui) {
        uiList.remove(ui);
    }

    public void sendAction(ViewInterface viewInterface, Actions action) {
        ViewModel previousViewModel = presenter.present(lastState);
        lastState = updater.update(viewInterface, lastState, action);
        ViewModel currentViewModel = presenter.present(lastState);
        for (UI<ViewModel> ui: uiList) {
            if (ui.animateChanges(previousViewModel, currentViewModel)) {
               ui.showViewModel(currentViewModel);
            }
        }
    }

    public State getLastState() {
        return lastState;
    }
}
