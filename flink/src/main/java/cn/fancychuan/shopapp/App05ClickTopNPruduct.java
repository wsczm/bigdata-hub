package cn.fancychuan.shopapp;

import cn.fancychuan.SensorReading;
import cn.fancychuan.shopapp.bean.ItemCountBean;
import cn.fancychuan.shopapp.bean.UserBehaviorBean;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

/**
 * 需求：实时热门商品（每隔5分钟输出最近一小时内点击量最多的前N个产品
 */
public class App05ClickTopNPruduct {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<String> inputStream = env.readTextFile(App05ClickTopNPruduct.class.getClassLoader().getResource("UserBehavior.csv").getPath());
        SingleOutputStreamOperator<UserBehaviorBean> dataStream =
                inputStream.map(new MapFunction<String, UserBehaviorBean>() {
                    @Override
                    public UserBehaviorBean map(String s) throws Exception {
                        String[] items = s.split(",");
                        UserBehaviorBean userBehaviorBean = new UserBehaviorBean(
                                Long.parseLong(items[0]),
                                Long.parseLong(items[1]),
                                Integer.parseInt(items[2]),
                                items[3],
                                Long.parseLong(items[4])
                        );
                        return userBehaviorBean;
                    }
                }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<UserBehaviorBean>() {
                    // 使用了时间语义，一定要记得分配！
                    @Override
                    public long extractAscendingTimestamp(UserBehaviorBean element) {
                        return element.getTimestamp();
                    }
                });

        SingleOutputStreamOperator<UserBehaviorBean> userBehaviorStream = dataStream.filter((FilterFunction<UserBehaviorBean>) userBehaviorBean -> "pv".equals(userBehaviorBean.getBehavior()));

//        userBehaviorStream.keyBy(UserBehaviorBean::getItemId)
//                .timeWindow(Time.hours(1), Time.minutes(5))
//                //.aggregate();
    }

    private static class CountAgg implements AggregateFunction<SensorReading, ItemCountBean, ItemCountBean> {

        @Override
        public ItemCountBean createAccumulator() {
            return null;
        }

        @Override
        public ItemCountBean add(SensorReading sensorReading, ItemCountBean itemCountBean) {
            return null;
        }

        @Override
        public ItemCountBean getResult(ItemCountBean itemCountBean) {
            return null;
        }

        @Override
        public ItemCountBean merge(ItemCountBean itemCountBean, ItemCountBean acc1) {
            return null;
        }
    }
}
