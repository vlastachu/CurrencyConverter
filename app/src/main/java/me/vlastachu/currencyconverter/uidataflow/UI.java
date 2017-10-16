package me.vlastachu.currencyconverter.uidataflow;

public interface UI<ViewModel> {
    void showViewModel(ViewModel viewModel);

    /**
     * implement this method if want animate changes between previous and current state
     * @return if true than should call showViewModel after.
     */
    boolean animateChanges(ViewModel previous, ViewModel current);
}
