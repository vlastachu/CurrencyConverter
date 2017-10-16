package me.vlastachu.currencyconverter.uidataflow;

public interface StateUpdater<State, Action, ViewInterface> {
    State getInitState(ViewInterface viewInterface);
    State update(ViewInterface viewInterface, State lastState, Action action);
}
