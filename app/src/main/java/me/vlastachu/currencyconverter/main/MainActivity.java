package me.vlastachu.currencyconverter.main;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.vlastachu.currencyconverter.R;
import me.vlastachu.currencyconverter.loaders.ValuteLoader;
import me.vlastachu.currencyconverter.model.ValCurs;
import me.vlastachu.currencyconverter.uidataflow.DataFlowActivity;
import me.vlastachu.currencyconverter.uidataflow.StateToViewModel;
import me.vlastachu.currencyconverter.uidataflow.StateUpdater;

public class MainActivity extends DataFlowActivity<ViewModel, State, Actions, MainActivityInterface>
        implements LoaderManager.LoaderCallbacks<ValCurs>, MainActivityInterface, ValuteChangerFragment.OnValuteChanged {

    class SelectValuteAdapter extends PagerAdapter {
        FragmentManager fragmentManager;
        Fragment[] fragments;

        public SelectValuteAdapter(FragmentManager fm){
            fragmentManager = fm;
            fragments = new Fragment[2];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.remove(fragments[position]);
            trans.commit();
            fragments[position] = null;
        }

        @Override
        public Fragment instantiateItem(ViewGroup container, int position){
            Fragment fragment = getItem(position);
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.add(container.getId(),fragment,"fragment:"+position);
            trans.commit();
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object fragment) {
            return ((Fragment) fragment).getView() == view;
        }

        public Fragment getItem(int position){
            if(fragments[position] == null){
                fragments[position] = initFragment(position);
            }
            return fragments[position];
        }
    }
    private Updater updater = new Updater();
    private View emptyLayout;
    private View failedLayout;
    private TextView failedText;
    private Button failedButton;
    private View loadedLayout;
    private TextView fromCurrency;
    private TextView toCurrency;
    private ImageView swapButton;
    private ViewPager valuteSelectorPager;
    private TextView fromName;
    private EditText fromValue;
    private TextView toName;
    private TextView toValue;
    private ValuteChangerFragment fromChangerFragment;
    private ValuteChangerFragment toChangerFragment;
    private SelectValuteAdapter selectValuteAdapter;
    private View valuteSelectorContainer;
    private ViewModel.Loaded lastLoadedViewModel;
    private Button closeSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportLoaderManager().initLoader(0, null, this);

        emptyLayout = findViewById(R.id.empty_layout);
        failedLayout = findViewById(R.id.failed_layout);
        failedText = (TextView) findViewById(R.id.no_connection_tv);
        failedButton = (Button) findViewById(R.id.no_connection_button);
        loadedLayout = findViewById(R.id.loaded_layout);
        fromCurrency = (TextView) findViewById(R.id.from_currency_tv);
        toCurrency = (TextView) findViewById(R.id.to_currency_tv);
        swapButton = (ImageView) findViewById(R.id.swap);
        valuteSelectorPager = (ViewPager) findViewById(R.id.valute_selector);
        fromName = (TextView) findViewById(R.id.from_name);
        fromValue = (EditText) findViewById(R.id.from_value);
        toName = (TextView) findViewById(R.id.to_name);
        toValue = (TextView) findViewById(R.id.to_value);
        valuteSelectorContainer = findViewById(R.id.valute_selector_container);
        closeSelector = (Button) findViewById(R.id.close_selector);


        failedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(new Actions.RequestUpdate());
            }
        });
        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(new Actions.Swap());
            }
        });
        closeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(new Actions.ClosePanel());
            }
        });
        fromCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(new Actions.OpenFromPanel());
            }
        });
        toCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send(new Actions.OpenToPanel());
            }
        });
        fromValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                send(new Actions.PutValuteValue(fromValue.getText().toString()));
            }
        });

        selectValuteAdapter = new SelectValuteAdapter(getSupportFragmentManager());
        valuteSelectorPager.setAdapter(selectValuteAdapter);
        valuteSelectorPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                send(position == 0 ? new Actions.OpenFromPanel() : new Actions.OpenToPanel());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private ValuteChangerFragment initFragment(int pos) {
        ValuteChangerFragment changerFragment = new ValuteChangerFragment();
        changerFragment.setChangingFromValute(pos == 0);
        changerFragment.setOnValuteChanged(this);
        if (pos == 0) {
            fromChangerFragment = changerFragment;
        } else {
            toChangerFragment = changerFragment;
        }
        if (lastLoadedViewModel != null) {
            changerFragment.setValuteItems(pos == 0 ? lastLoadedViewModel.getFromValutes()
                    : lastLoadedViewModel.getToValutes());
        }
        return changerFragment;
    }

    @Override
    public boolean animateChanges(ViewModel previous, final ViewModel current) {
        if (previous instanceof ViewModel.Loaded && current instanceof ViewModel.Loaded) {
            ViewModel.Loaded prevLoaded = (ViewModel.Loaded) previous;
            ViewModel.Loaded curLoaded = (ViewModel.Loaded) current;
            if (curLoaded.getReversed() != prevLoaded.getReversed()) {
                final float fromX = fromCurrency.getX() - toCurrency.getX();
                final float toX = -fromX;
                swapButton.animate().rotationBy(360).alpha(0).scaleX(0).scaleY(0).setDuration(600).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        swapButton.animate().rotationBy(360).alpha(1).scaleX(1).scaleY(1).setDuration(400).start();
                    }
                }).start();
                fromCurrency.animate().translationX(toX).setDuration(1000)
                        .setInterpolator(new AnticipateOvershootInterpolator()).start();
                toCurrency.animate().translationX(fromX).setDuration(1000)
                        .setInterpolator(new AnticipateOvershootInterpolator())
                        .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {}

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        fromCurrency.setTranslationX(0);
                        toCurrency.setTranslationX(0);
                        swapButton.setRotation(0);
                        swapButton.setScaleX(1f);
                        swapButton.setScaleY(1f);
                        swapButton.setAlpha(1f);
                        showViewModel(current);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {
                        onAnimationEnd(animator);
                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {}
                }).start();
                return false;
            }
        }
        return true;
    }

    @Override
    public void showViewModel(ViewModel viewModel) {
        fromCurrency.clearAnimation();
        toCurrency.clearAnimation();
        emptyLayout.setVisibility(View.GONE);
        failedLayout.setVisibility(View.GONE);
        loadedLayout.setVisibility(View.GONE);
        if (viewModel instanceof ViewModel.Empty) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else if (viewModel instanceof ViewModel.NoConnection) {
            ViewModel.NoConnection model = (ViewModel.NoConnection) viewModel;
            failedLayout.setVisibility(View.VISIBLE);
            failedText.setText(model.getErrorText());
        } else if (viewModel instanceof ViewModel.Loaded) {
            ViewModel.Loaded model = (ViewModel.Loaded) viewModel;
            lastLoadedViewModel = model;
            loadedLayout.setVisibility(View.VISIBLE);
            fromCurrency.setText(model.getFromValuteShortName());
            toCurrency.setText(model.getToValuteShortName());
            if (model.getValuteSelectorState() == ValuteSelectorState.Closed) {
                valuteSelectorContainer.setVisibility(View.GONE);
            } else {
                valuteSelectorContainer.setVisibility(View.VISIBLE);
                if (model.getValuteSelectorState() == ValuteSelectorState.FromOpen) {
                    if (valuteSelectorPager.getCurrentItem() != 0) {
                        valuteSelectorPager.setCurrentItem(0);
                    }
                } else if (model.getValuteSelectorState() == ValuteSelectorState.ToOpen) {
                    if (valuteSelectorPager.getCurrentItem() != 1) {
                        valuteSelectorPager.setCurrentItem(1);
                    }
                }

                if (fromChangerFragment != null)
                    fromChangerFragment.setValuteItems(model.getFromValutes());
                if (toChangerFragment != null)
                    toChangerFragment.setValuteItems(model.getToValutes());
            }
            fromName.setText(model.getFromValuteName());
            toName.setText(model.getToValuteName());
            toValue.setText(model.getToValuteValue());
            if(!fromValue.isFocused() && !fromValue.getText().toString().equals(model.getFromValuteValue())) {
                fromValue.setText(model.getToValuteValue());
            }
        }
    }

    @Override
    protected StateUpdater<State, Actions, MainActivityInterface> getStateUpdater() {
        return updater;
    }

    @Override
    protected StateToViewModel<State, ViewModel> getStateToViewModel() {
        return updater;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_reload) {
            send(new Actions.RequestUpdate());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new ValuteLoader(this);
    }

    @Override
    public void onLoadFinished(Loader loader, ValCurs data) {
        if (data == null) {
            send(new Actions.UpdateFailed());
        } else {
            send(new Actions.UpdateCompleted(data));
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void requestLoad() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onValuteChanged(Boolean changingFromValute, String valuteId) {
        if (changingFromValute) {
            send(new Actions.SelectFromValute(valuteId));
        } else {
            send(new Actions.SelectToValute(valuteId));
        }
    }
}
