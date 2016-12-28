package com.wos.play.rootdir.model_universal.jsonBeanArray.cmd_upsc;

import java.util.List;

/**
 * Created by user on 2016/11/8.
 */

public class Rules {
    /**
     * repeatType : {"code":1,"text":"每天"}
     * weekday : [{"name":"日","value1":0,"checked":false},{"name":"一","value1":1,"checked":false},{"name":"二","value1":2,"checked":false},{"name":"三","value1":3,"checked":false},{"name":"四","value1":4,"checked":false},{"name":"五","value1":5,"checked":false},{"name":"六","value1":6,"checked":false}]
     * startday : 2016-11-08 17:47:14
     * endday : null
     * stop : false
     * repeatWholeDay : false
     * startTime : 2016-06-02 08:00:00
     * endTime : 2016-06-02 12:00:00
     */

    private RepeatRulesBean repeatRules;

    public RepeatRulesBean getRepeatRules() {
        return repeatRules;
    }

    public void setRepeatRules(RepeatRulesBean repeatRules) {
        this.repeatRules = repeatRules;
    }

    public static class RepeatRulesBean {
        /**
         * code : 1
         * text : 每天
         */

        private RepeatTypeBean repeatType;
        private String startday;
        private String endday;
        private boolean stop;
        private boolean repeatWholeDay;
        private String startTime;
        private String endTime;
        /**
         * name : 日
         * value1 : 0
         * checked : false
         */

        private List<WeekdayBean> weekday;

        public RepeatTypeBean getRepeatType() {
            return repeatType;
        }

        public void setRepeatType(RepeatTypeBean repeatType) {
            this.repeatType = repeatType;
        }

        public String getStartday() {
            return startday;
        }

        public void setStartday(String startday) {
            this.startday = startday;
        }

        public String getEndday() {
            return endday;
        }

        public void setEndday(String endday) {
            this.endday = endday;
        }

        public boolean isStop() {
            return stop;
        }

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        public boolean isRepeatWholeDay() {
            return repeatWholeDay;
        }

        public void setRepeatWholeDay(boolean repeatWholeDay) {
            this.repeatWholeDay = repeatWholeDay;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public List<WeekdayBean> getWeekday() {
            return weekday;
        }

        public void setWeekday(List<WeekdayBean> weekday) {
            this.weekday = weekday;
        }

        public static class RepeatTypeBean {
            private int code;
            private String text;

            public int getCode() {
                return code;
            }

            public void setCode(int code) {
                this.code = code;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }
        }

        public static class WeekdayBean {
            private String name;
            private int value1;
            private boolean checked;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getValue1() {
                return value1;
            }

            public void setValue1(int value1) {
                this.value1 = value1;
            }

            public boolean isChecked() {
                return checked;
            }

            public void setChecked(boolean checked) {
                this.checked = checked;
            }
        }
    }
    /**
     *"rules":{
     "repeatRules":{
     "repeatType":{
     "code":1,
     "text":"每天"
     },
     "weekday":[
     {
     "name":"日",
     "value1":0,
     "checked":false
     },
     {
     "name":"一",
     "value1":1,
     "checked":false
     },
     {
     "name":"二",
     "value1":2,
     "checked":false
     },
     {
     "name":"三",
     "value1":3,
     "checked":false
     },
     {
     "name":"四",
     "value1":4,
     "checked":false
     },
     {
     "name":"五",
     "value1":5,
     "checked":false
     },
     {
     "name":"六",
     "value1":6,
     "checked":false
     }
     ],
     "startday":"2016-11-08 17:47:14",
     "endday":null,
     "stop":false,
     "repeatWholeDay":false,
     "startTime":"2016-06-02 08:00:00",
     "endTime":"2016-06-02 12:00:00"
     }
     }
     */







}
