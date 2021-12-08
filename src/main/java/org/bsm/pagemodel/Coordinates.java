package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bsm.utils.Formate;

import java.io.Serializable;

/**
 * @author GZC
 * @create 2021-11-03 0:29
 * @desc 坐标实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    /***/
    Formate formate;

    /**
     * 经度
     */
    double longitude;

    /**
     * 纬度
     */
    double latitude;

    /**
     * 高程
     */
    double altitude;

    public Coordinates(double longitude, double latitude, double altitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }
}
