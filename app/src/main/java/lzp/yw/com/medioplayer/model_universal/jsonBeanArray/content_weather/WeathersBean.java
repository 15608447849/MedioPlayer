package lzp.yw.com.medioplayer.model_universal.jsonBeanArray.content_weather;

/**
 * Created by user on 2016/11/22.
 */

public class WeathersBean {

    /**
     * loadWeather : true
     * singleDay : true
     * layout : {"vAlign":"center","padding":5,"gap":50,"layout":"horizontal","display":"time, weather"}
     * time : {"timeSize":"50","dateSize":"20","layout":{"gap":20,"layout":"vertical","display":"time, date"},"fontFamily":"微软雅黑","dateColor":"#FFFFFF","timeColor":"#FFFFFF","format":"YYYY年MM月DD日|HH:MI"}
     * weather : {"fontColor":"#FFFFFF","iconH":"100","iconW":"100","fontFamily":"微软雅黑","iconDirPath":"","listLayout":{"gap":10,"layout":"horizontal"},"fontSize":"20","itemLayout":{"gap":10,"layout":"vertical","display":"icon,desc"}}
     */

    private StyleBean style;
    /**
     * currentCity : 宜宾
     */

    private WeatherDataBean weatherData;

    public StyleBean getStyle() {
        return style;
    }

    public void setStyle(StyleBean style) {
        this.style = style;
    }

    public WeatherDataBean getWeatherData() {
        return weatherData;
    }

    public void setWeatherData(WeatherDataBean weatherData) {
        this.weatherData = weatherData;
    }

    public static class StyleBean {
        private boolean loadWeather;
        private boolean singleDay;
        /**
         * vAlign : center
         * padding : 5
         * gap : 50
         * layout : horizontal
         * display : time, weather
         */

        private LayoutBean layout;
        /**
         * timeSize : 50
         * dateSize : 20
         * layout : {"gap":20,"layout":"vertical","display":"time, date"}
         * fontFamily : 微软雅黑
         * dateColor : #FFFFFF
         * timeColor : #FFFFFF
         * format : YYYY年MM月DD日|HH:MI
         */

        private TimeBean time;
        /**
         * fontColor : #FFFFFF
         * iconH : 100
         * iconW : 100
         * fontFamily : 微软雅黑
         * iconDirPath :
         * listLayout : {"gap":10,"layout":"horizontal"}
         * fontSize : 20
         * itemLayout : {"gap":10,"layout":"vertical","display":"icon,desc"}
         */

        private WeatherBean weather;

        public boolean isLoadWeather() {
            return loadWeather;
        }

        public void setLoadWeather(boolean loadWeather) {
            this.loadWeather = loadWeather;
        }

        public boolean isSingleDay() {
            return singleDay;
        }

        public void setSingleDay(boolean singleDay) {
            this.singleDay = singleDay;
        }

        public LayoutBean getLayout() {
            return layout;
        }

        public void setLayout(LayoutBean layout) {
            this.layout = layout;
        }

        public TimeBean getTime() {
            return time;
        }

        public void setTime(TimeBean time) {
            this.time = time;
        }

        public WeatherBean getWeather() {
            return weather;
        }

        public void setWeather(WeatherBean weather) {
            this.weather = weather;
        }

        public static class LayoutBean {
            private String vAlign;
            private int padding;
            private int gap;
            private String layout;
            private String display;

            public String getVAlign() {
                return vAlign;
            }

            public void setVAlign(String vAlign) {
                this.vAlign = vAlign;
            }

            public int getPadding() {
                return padding;
            }

            public void setPadding(int padding) {
                this.padding = padding;
            }

            public int getGap() {
                return gap;
            }

            public void setGap(int gap) {
                this.gap = gap;
            }

            public String getLayout() {
                return layout;
            }

            public void setLayout(String layout) {
                this.layout = layout;
            }

            public String getDisplay() {
                return display;
            }

            public void setDisplay(String display) {
                this.display = display;
            }
        }

        public static class TimeBean {
            private String timeSize;
            private String dateSize;
            /**
             * gap : 20
             * layout : vertical
             * display : time, date
             */

            private LayoutBean layout;
            private String fontFamily;
            private String dateColor;
            private String timeColor;
            private String format;

            public String getTimeSize() {
                return timeSize;
            }

            public void setTimeSize(String timeSize) {
                this.timeSize = timeSize;
            }

            public String getDateSize() {
                return dateSize;
            }

            public void setDateSize(String dateSize) {
                this.dateSize = dateSize;
            }

            public LayoutBean getLayout() {
                return layout;
            }

            public void setLayout(LayoutBean layout) {
                this.layout = layout;
            }

            public String getFontFamily() {
                return fontFamily;
            }

            public void setFontFamily(String fontFamily) {
                this.fontFamily = fontFamily;
            }

            public String getDateColor() {
                return dateColor;
            }

            public void setDateColor(String dateColor) {
                this.dateColor = dateColor;
            }

            public String getTimeColor() {
                return timeColor;
            }

            public void setTimeColor(String timeColor) {
                this.timeColor = timeColor;
            }

            public String getFormat() {
                return format;
            }

            public void setFormat(String format) {
                this.format = format;
            }

            public static class LayoutBean {
                private int gap;
                private String layout;
                private String display;

                public int getGap() {
                    return gap;
                }

                public void setGap(int gap) {
                    this.gap = gap;
                }

                public String getLayout() {
                    return layout;
                }

                public void setLayout(String layout) {
                    this.layout = layout;
                }

                public String getDisplay() {
                    return display;
                }

                public void setDisplay(String display) {
                    this.display = display;
                }
            }
        }

        public static class WeatherBean {
            private String fontColor;
            private String iconH;
            private String iconW;
            private String fontFamily;
            private String iconDirPath;
            /**
             * gap : 10
             * layout : horizontal
             */

            private ListLayoutBean listLayout;
            private String fontSize;
            /**
             * gap : 10
             * layout : vertical
             * display : icon,desc
             */

            private ItemLayoutBean itemLayout;

            public String getFontColor() {
                return fontColor;
            }

            public void setFontColor(String fontColor) {
                this.fontColor = fontColor;
            }

            public String getIconH() {
                return iconH;
            }

            public void setIconH(String iconH) {
                this.iconH = iconH;
            }

            public String getIconW() {
                return iconW;
            }

            public void setIconW(String iconW) {
                this.iconW = iconW;
            }

            public String getFontFamily() {
                return fontFamily;
            }

            public void setFontFamily(String fontFamily) {
                this.fontFamily = fontFamily;
            }

            public String getIconDirPath() {
                return iconDirPath;
            }

            public void setIconDirPath(String iconDirPath) {
                this.iconDirPath = iconDirPath;
            }

            public ListLayoutBean getListLayout() {
                return listLayout;
            }

            public void setListLayout(ListLayoutBean listLayout) {
                this.listLayout = listLayout;
            }

            public String getFontSize() {
                return fontSize;
            }

            public void setFontSize(String fontSize) {
                this.fontSize = fontSize;
            }

            public ItemLayoutBean getItemLayout() {
                return itemLayout;
            }

            public void setItemLayout(ItemLayoutBean itemLayout) {
                this.itemLayout = itemLayout;
            }

            public static class ListLayoutBean {
                private int gap;
                private String layout;

                public int getGap() {
                    return gap;
                }

                public void setGap(int gap) {
                    this.gap = gap;
                }

                public String getLayout() {
                    return layout;
                }

                public void setLayout(String layout) {
                    this.layout = layout;
                }
            }

            public static class ItemLayoutBean {
                private int gap;
                private String layout;
                private String display;

                public int getGap() {
                    return gap;
                }

                public void setGap(int gap) {
                    this.gap = gap;
                }

                public String getLayout() {
                    return layout;
                }

                public void setLayout(String layout) {
                    this.layout = layout;
                }

                public String getDisplay() {
                    return display;
                }

                public void setDisplay(String display) {
                    this.display = display;
                }
            }
        }
    }

    public static class WeatherDataBean {
        private String currentCity;

        public String getCurrentCity() {
            return currentCity;
        }

        public void setCurrentCity(String currentCity) {
            this.currentCity = currentCity;
        }
    }
}
