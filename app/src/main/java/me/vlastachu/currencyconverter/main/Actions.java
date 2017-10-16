package me.vlastachu.currencyconverter.main;


import me.vlastachu.currencyconverter.model.ValCurs;

public class Actions {
    private Actions() {}
    public static class Swap extends Actions {}
    public static class OpenFromPanel extends Actions {}
    public static class OpenToPanel extends Actions {}
    public static class RequestUpdate extends Actions {}
    public static class UpdateStarted extends Actions {}
    public static class UpdateFailed extends Actions {}
    public static class ClosePanel extends Actions {}

    public static class UpdateCompleted extends Actions {
        private final ValCurs valCurs;

        public UpdateCompleted(ValCurs valCurs) { this.valCurs = valCurs; }

        public ValCurs getValCurs() { return valCurs; }
    }

    public static class SelectFromValute extends Actions {
        private final String id;

        public SelectFromValute(String id) { this.id = id; }

        public String getId() { return id; }
    }

    public static class SelectToValute extends Actions {
        private String id;

        public SelectToValute(String id) { this.id = id; }

        public String getId() { return id; }
    }

    public static class PutValuteValue extends Actions {
        private final String value;

        public PutValuteValue(String value) { this.value = value; }

        public String getValue() { return value; }
    }
}
