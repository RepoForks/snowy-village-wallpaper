package com.novoda.snowyvillagewallpaper.snow;

import java.util.Comparator;

class SnowflakeSizeComparator implements Comparator<Snowflake> {

    @Override
    public int compare(Snowflake lhs, Snowflake rhs) {
        return lhs.getSnowflakeType().compareTo(rhs.getSnowflakeType());
    }

}
