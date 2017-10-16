package me.vlastachu.currencyconverter.main;

import java.util.List;

public abstract class ViewModel {
    private ViewModel() {}
    public static class Empty extends ViewModel {}
    public static class NoConnection extends ViewModel {
        private String errorText;

        public NoConnection(String errorText) {
            this.errorText = errorText;
        }

        public String getErrorText() {
            return errorText;
        }
    }
    public static class Loaded extends ViewModel {
        public static class ValuteItem {
            private Boolean isSelected;
            private Boolean isDisabled;
            private String name;
            private int iconResId;
            private Boolean isLeft;
            private String id;

            public ValuteItem(Boolean isSelected, Boolean isDisabled, String name, int iconResId, Boolean isLeft, String id) {
                this.isSelected = isSelected;
                this.isDisabled = isDisabled;
                this.name = name;
                this.iconResId = iconResId;
                this.isLeft = isLeft;
                this.id = id;
            }

            public Boolean getSelected() {
                return isSelected;
            }

            public Boolean getDisabled() {
                return isDisabled;
            }

            public String getName() {
                return name;
            }

            public int getIconResId() {
                return iconResId;
            }

            public Boolean getLeft() {
                return isLeft;
            }

            public String getId() {
                return id;
            }
        }
        private ValuteSelectorState valuteSelectorState;
        private Boolean updatingIconAnimate;
        private List<ValuteItem> fromValutes;
        private List<ValuteItem> toValutes;
        private String fromValuteName;
        private String toValuteName;
        private String fromValuteShortName;
        private String toValuteShortName;
        private String fromValuteValue;
        private String toValuteValue;

        public Loaded(ValuteSelectorState valuteSelectorState,
                      Boolean updatingIconAnimate,
                      List<ValuteItem> fromValutes,
                      List<ValuteItem> toValutes,
                      String fromValuteName,
                      String toValuteName,
                      String fromValuteShortName,
                      String toValuteShortName,
                      String fromValuteValue,
                      String toValuteValue) {
            this.valuteSelectorState = valuteSelectorState;
            this.updatingIconAnimate = updatingIconAnimate;
            this.fromValutes = fromValutes;
            this.toValutes = toValutes;
            this.fromValuteName = fromValuteName;
            this.toValuteName = toValuteName;
            this.fromValuteShortName = fromValuteShortName;
            this.toValuteShortName = toValuteShortName;
            this.fromValuteValue = fromValuteValue;
            this.toValuteValue = toValuteValue;
        }

        public ValuteSelectorState getValuteSelectorState() {
            return valuteSelectorState;
        }

        public Boolean getUpdatingIconAnimate() {
            return updatingIconAnimate;
        }

        public List<ValuteItem> getFromValutes() {
            return fromValutes;
        }

        public List<ValuteItem> getToValutes() {
            return toValutes;
        }

        public String getFromValuteName() {
            return fromValuteName;
        }

        public String getToValuteName() {
            return toValuteName;
        }

        public String getFromValuteShortName() {
            return fromValuteShortName;
        }

        public String getToValuteShortName() {
            return toValuteShortName;
        }

        public String getFromValuteValue() {
            return fromValuteValue;
        }

        public String getToValuteValue() {
            return toValuteValue;
        }
    }
}
