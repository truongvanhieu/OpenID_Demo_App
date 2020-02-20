package com.map4d.openid_demo_app.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model_Smartcode_Data {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private Results results;

    public Model_Smartcode_Data(String code, String message, Results results) {
        this.code = code;
        this.message = message;
        this.results = results;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public class Results{
        @SerializedName("min")
        @Expose
        private Min min;
        @SerializedName("max")
        @Expose
        private Max max;
        @SerializedName("location")
        @Expose
        private Location location;
        @SerializedName("compoundCode")
        @Expose
        private String compoundCode;
        @SerializedName("smartCode")
        @Expose
        private String smartCode;
        @SerializedName("center")
        @Expose
        private Center center;
        @SerializedName("warning")
        @Expose
        private String warning;

        public Results(Min min, Max max, Location location, String compoundCode, String smartCode, Center center, String warning) {
            this.min = min;
            this.max = max;
            this.location = location;
            this.compoundCode = compoundCode;
            this.smartCode = smartCode;
            this.center = center;
            this.warning = warning;
        }

        public Min getMin() {
            return min;
        }

        public void setMin(Min min) {
            this.min = min;
        }

        public Max getMax() {
            return max;
        }

        public void setMax(Max max) {
            this.max = max;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getCompoundCode() {
            return compoundCode;
        }

        public void setCompoundCode(String compoundCode) {
            this.compoundCode = compoundCode;
        }

        public String getSmartCode() {
            return smartCode;
        }

        public void setSmartCode(String smartCode) {
            this.smartCode = smartCode;
        }

        public Center getCenter() {
            return center;
        }

        public void setCenter(Center center) {
            this.center = center;
        }

        public String getWarning() {
            return warning;
        }

        public void setWarning(String warning) {
            this.warning = warning;
        }

        public class Min{
            @SerializedName("lat")
            @Expose
            private Double latitude;
            @SerializedName("lng")
            @Expose
            private Double longitude;

            public Min(Double latitude, Double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public Double getLatitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            public Double getLongitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
        }
        public class Max{
            @SerializedName("lat")
            @Expose
            private Double latitude;
            @SerializedName("lng")
            @Expose
            private Double longitude;

            public Max(Double latitude, Double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public Double getLatitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            public Double getLongitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
        }
        public class Center{
            @SerializedName("lat")
            @Expose
            private Double latitude;
            @SerializedName("lng")
            @Expose
            private Double longitude;

            public Center(Double latitude, Double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public Double getLatitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            public Double getLongitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
        }
        public class Location{
            @SerializedName("lat")
            @Expose
            private Double latitude;
            @SerializedName("lng")
            @Expose
            private Double longitude;

            public Location(Double latitude, Double longitude) {
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public Double getLatitude() {
                return latitude;
            }

            public void setLatitude(Double latitude) {
                this.latitude = latitude;
            }

            public Double getLongitude() {
                return longitude;
            }

            public void setLongitude(Double longitude) {
                this.longitude = longitude;
            }
        }
    }
}
