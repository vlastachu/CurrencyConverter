package me.vlastachu.currencyconverter.uidataflow;

public interface StateToViewModel<State, ViewModel> {
    ViewModel present(State state);
}
