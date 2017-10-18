package me.vlastachu.currencyconverter.main;

import android.app.Application;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

import me.vlastachu.currencyconverter.App;
import me.vlastachu.currencyconverter.R;
import me.vlastachu.currencyconverter.model.ValCurs;
import me.vlastachu.currencyconverter.model.Valute;
import me.vlastachu.currencyconverter.uidataflow.StateToViewModel;
import me.vlastachu.currencyconverter.uidataflow.StateUpdater;
import me.vlastachu.currencyconverter.util.Converter;

public class Updater implements StateUpdater<State, Actions, MainActivityInterface>,
        StateToViewModel<State, ViewModel> {

    @Override
    public ViewModel present(State state) {
        if (state instanceof State.Empty) {
            return new ViewModel.Empty();
        } else if (state instanceof State.LoadFailed) {
            return new ViewModel.NoConnection(App.getContext().getString(R.string.no_connection));
        } else if (state instanceof State.DataLoaded) {
            State.DataLoaded loaded = (State.DataLoaded) state;
            Valute fromValute = loaded.valCurs.findValuteById(loaded.fromValuteId);
            Valute toValute = loaded.valCurs.findValuteById(loaded.toValuteId);
            return new ViewModel.Loaded(
                    loaded.valuteSelectorState,
                    loaded.isUpdating,
                    presentValuteList(loaded.valCurs.getValutes(), fromValute, toValute, true),
                    presentValuteList(loaded.valCurs.getValutes(), fromValute, toValute, false),
                    fromValute.getName(),
                    toValute.getName(),
                    fromValute.getCharCode(),
                    toValute.getCharCode(),
                    Float.toString(loaded.fromValue),
                    Float.toString(loaded.toValue),
                    loaded.isReversed
            );
        }
        return null;
    }

    private List<ViewModel.Loaded.ValuteItem> presentValuteList
            (List<Valute> valutes, Valute from, Valute to, Boolean choosingFrom) {
        ArrayList<ViewModel.Loaded.ValuteItem> valuteItems = new ArrayList<>();
        Valute selectedValute = choosingFrom ? from : to;
        Valute disabledValute = choosingFrom ? to : from;
        for (Valute valute: valutes) {
            ViewModel.Loaded.ValuteItem valuteItem = new ViewModel.Loaded.ValuteItem(
                    valute.getId().equals(selectedValute.getId()),
                    valute.getId().equals(disabledValute.getId()),
                    valute.getName(),
                    0, // TODO
                    choosingFrom,
                    valute.getId()
                    );
            valuteItems.add(valuteItem);
        }
        return valuteItems;
    }

    @Override
    public State getInitState(MainActivityInterface mainActivityInterface) {
        return new State.Empty();
    }

    @Override
    public State update(MainActivityInterface mainActivityInterface, State lastState, Actions action) {
        if (lastState instanceof State.Empty) {
            if (action instanceof Actions.UpdateCompleted) {
                ValCurs valCurs = ((Actions.UpdateCompleted)action).getValCurs();
                return getStateFromValCurs(valCurs);
            } else if (action instanceof Actions.UpdateFailed) {
                return new State.LoadFailed();
            }
        } else if (lastState instanceof State.LoadFailed) {
            if (action instanceof Actions.RequestUpdate) {
                mainActivityInterface.requestLoad();
                return new State.Empty();
            }
        } else if (lastState instanceof State.DataLoaded) {
            State.DataLoaded state = (State.DataLoaded) lastState;
            if (action instanceof Actions.RequestUpdate) {
                mainActivityInterface.requestLoad();
                state.isUpdating = true;
                return state;
            } else if (action instanceof Actions.UpdateCompleted) {
                state.valuteSelectorState = ValuteSelectorState.Closed;
                ValCurs valCurs = ((Actions.UpdateCompleted)action).getValCurs();
                // FIXME possible valute ids changed
                state.valCurs = valCurs;
                state.isUpdating = false;
                return state;
            } else if (action instanceof Actions.Swap) {
                state.valuteSelectorState = ValuteSelectorState.Closed;
                String tmp = state.fromValuteId;
                state.fromValuteId = state.toValuteId;
                state.toValuteId = tmp;
                state.isReversed = !state.isReversed;
                state.toValue = Converter.convert(state.fromValue, state.valCurs.findValuteById(state.fromValuteId),
                        state.valCurs.findValuteById(state.toValuteId));
                return state;
            } else if (action instanceof Actions.UpdateFailed) {
                // TODO show error
                state.isUpdating = false;
                return state;
            } else if (action instanceof Actions.OpenFromPanel) {
                state.valuteSelectorState = ValuteSelectorState.FromOpen;
                return state;
            } else if (action instanceof Actions.OpenToPanel) {
                state.valuteSelectorState = ValuteSelectorState.ToOpen;
                return state;
            } else if (action instanceof Actions.ClosePanel) {
                state.valuteSelectorState = ValuteSelectorState.Closed;
                return state;
            } else if (action instanceof Actions.PutValuteValue) {
                String fromStr = ((Actions.PutValuteValue)action).getValue();
                try {
                    Float fromValue = Float.valueOf (fromStr);
                    if (fromValue == null) return state;
                    else state.fromValue = fromValue;
                    state.toValue = Converter.convert(state.fromValue, state.valCurs.findValuteById(state.fromValuteId),
                            state.valCurs.findValuteById(state.toValuteId));
                } catch (NumberFormatException e) {
                    // skip
                }
                return state;
            } else if (action instanceof Actions.SelectFromValute) {
                state.fromValuteId = ((Actions.SelectFromValute)action).getId();
                state.valuteSelectorState = ValuteSelectorState.Closed;
                state.toValue = Converter.convert(state.fromValue, state.valCurs.findValuteById(state.fromValuteId),
                        state.valCurs.findValuteById(state.toValuteId));
                return state;
            } else if (action instanceof Actions.SelectToValute) {
                state.toValuteId = ((Actions.SelectToValute)action).getId();
                state.valuteSelectorState = ValuteSelectorState.Closed;
                state.toValue = Converter.convert(state.fromValue, state.valCurs.findValuteById(state.fromValuteId),
                        state.valCurs.findValuteById(state.toValuteId));
                return state;
            }
        }
        return lastState;
    }

    private State.DataLoaded getStateFromValCurs(ValCurs valCurs) {
        State.DataLoaded state = new State.DataLoaded();
        state.valuteSelectorState = ValuteSelectorState.Closed;
        state.fromValue = 0;
        state.toValue = 0;
        Valute from = valCurs.findValuteByCharCode("RUB");
        Valute to = valCurs.findValuteByCharCode("USD");
        if (from != null && to != null) {
            state.fromValuteId = from.getId();
            state.toValuteId = to.getId();
        } else {
            state.fromValuteId = valCurs.getValutes().get(0).getId();
            state.toValuteId = valCurs.getValutes().get(1).getId();
        }
        state.isReversed = false;
        state.isUpdating = false;
        state.valCurs = valCurs;
        return state;
    }
}
