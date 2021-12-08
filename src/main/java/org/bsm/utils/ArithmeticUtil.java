package org.bsm.utils;

import org.bsm.pagemodel.Coordinates;

/**
 * @author GZC
 */
public class ArithmeticUtil {
    /**
     * 判断位置是否在中国外
     */
    public static double a = 6378245.0;
    //短半轴长
    public static double G_B = 6356752.3142;
    //长半轴长（m）
    public static double CGCS2000_A = 6378137;
    public static String STR_NATIONAL84 = "National-84";
    public static String STR_REALNORTH = "RealNorth";
    public static String STR_SHOOTRANGE = "ShootRange";
    public static String STR_GUNSHOT = "GunShot";
    public static String STR_CUSTOMIZE = "CUSTOMIZE";
    public static double ee = 0.00669342162296594323;
    public static double DEG_TO_RAD = 0.0174532925199433;
    public static double RAD_TO_DEG = 57.295779513082321;
    public static double x_pi = Math.PI * 3000.0 / 180.0;

    public static double CGCS2000_F = 1 / 298.257222101;
    //第一偏心率
    public static double CGCS2000_E = 0.0818191910428158;
    //第一偏心率平方
    public static double CGCS2000_E_2 = 0.0066943800229008;
    //第二偏心率
    public static double CGCS2000_E2 = 0.0820944381519172;
    //第二偏心率平方
    public static double CGCS2000_E2_2 = 0.0067394967754790;
    //长半轴
    public static double WGS84_A = 6378137;
    public static double WGS84_F = 1 / 298.257223563;
    public static double WGS84_E = 0.0818191908426205;
    public static double WGS84_E_2 = 0.0066943799901412;
    public static double WGS84_E2 = 0.0820944379496957;
    public static double WGS84_E2_2 = 0.0067394967422764;

    public static int RAE = 1;
    public static int XYZ = 2;
    public static int BLH = 3;

    /**
     * 从雷达本地直角坐标系转换到地心直角坐标系
     * xyz,为目标在雷达站本地直角坐标系的位置
     * pos_blh,为雷达站在参考坐标系coor_sys中的经纬度
     * _  _     _                                    _    _  _    _  _
     * | xe |   | -sin(x)  -cos(x)sin(y)  cos(x)cos(y) |  | x1 |  | x2 |
     * | ye | = | cos(x)   -sin(x)sin(y)  sin(x)cos(y) | *| y1 | +| y2 |
     * | ze |   | 0        cos(y)         sin(y)       |  | z1 |  | z2 |
     * -  -     -                                    -    -  -    -   -
     * 其中(xe,ye,ze)为目标在地心坐标系中的位置
     * (x1,y1,z1)为目标在雷达站本地直角坐标系的位置
     * (x2,y2,z2)雷达站在地心坐标系中位置
     * (x,y)为雷达站的经度和纬度
     */
    public static Coordinates RadarToGCS(Coordinates xyz, Coordinates pos_blh, Coordinates pos_xyz) {

        double x = (-1) * Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * xyz.getLongitude() +
                (-1) * Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * Math.sin(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getLatitude() +
                Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getAltitude() +
                pos_xyz.getLongitude();

        double y = Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * xyz.getLongitude() +
                (-1) * Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getLatitude() +
                Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getAltitude() +
                pos_xyz.getLatitude();

        double z = 0 +
                Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getLatitude() +
                Math.sin(pos_blh.getLatitude() * DEG_TO_RAD) * xyz.getAltitude() +
                pos_xyz.getAltitude();
        return new Coordinates(x, y, z);

    }


    /**
     * 公式
     * （x1, y1, z1） 目标在雷达站本地直角坐标系中的坐标
     * （xe, ye, ze ) 目标在地心坐标系中的坐标
     * （x2, y2, z2)  雷达站在地心坐标系中的坐标
     * （b, l) 雷达站在地心坐标系中的经度和纬度
     * —        —     —       —
     * |   x1   |     | xe-x2 |
     * |   y1   |= R* | ye-y2 |
     * |   z1   |     | ze-z2 |
     * —        —     —       —
     * R为旋转矩阵,即RadarToGCS()中使用到的旋转矩阵的逆矩阵
     * —                                       —
     * | -sin(x)          cos(x)       0       |
     * R =|-sin(y)cos(x)  -sin(y)sin(x)   cos(y)  |
     * |cos(y)cos(x)   cos(y)sin(x)   sin(y)   |
     * —                                       —
     */
    public static Coordinates GCSToRadar(Coordinates xyz_e, Coordinates pos_blh, Coordinates pos_xyz) {

        Coordinates temp = new Coordinates(
                xyz_e.getLongitude() - pos_xyz.getLongitude(),
                xyz_e.getLatitude() - pos_xyz.getLatitude(),
                xyz_e.getAltitude() - pos_xyz.getAltitude());

        double x = (-1) * Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLongitude() +
                Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLatitude() +
                0;

        double y = (-1) * Math.sin(pos_blh.getLatitude() * DEG_TO_RAD) * Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLongitude() +
                (-1) * Math.sin(pos_blh.getLatitude() * DEG_TO_RAD) * Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLatitude() +
                Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * temp.getAltitude();

        double z = Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * Math.cos(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLongitude() +
                Math.cos(pos_blh.getLatitude() * DEG_TO_RAD) * Math.sin(pos_blh.getLongitude() * DEG_TO_RAD) * temp.getLatitude() +
                Math.sin(pos_blh.getLatitude() * DEG_TO_RAD) * temp.getAltitude();

        return new Coordinates(x, y, z);
    }

    /**
     * 经纬度转换成地心直角坐标表系坐标
     * coorSys,指名参考坐标系
     */
    public static Coordinates BLHtoXYZ(Coordinates blh, CoorSys coorSys) {
        double a, e2;
        switch (coorSys) {
            case WGS84:
                a = WGS84_A;
                e2 = WGS84_E_2;
                break;
            default:
                a = CGCS2000_A;
                e2 = CGCS2000_E_2;
                break;
        }

        double n = a / Math.sqrt(1 - e2 * Math.sin(blh.getLatitude() * DEG_TO_RAD) * Math.sin(blh.getLatitude() * DEG_TO_RAD));

        double x = (n + blh.getAltitude()) * Math.cos(blh.getLongitude() * DEG_TO_RAD) * Math.cos(blh.getLatitude() * DEG_TO_RAD);
        double y = (n + blh.getAltitude()) * Math.sin(blh.getLongitude() * DEG_TO_RAD) * Math.cos(blh.getLatitude() * DEG_TO_RAD);
        double z = (n * (1 - e2) + blh.getAltitude()) * Math.sin(blh.getLatitude() * DEG_TO_RAD);
        Coordinates result = new Coordinates(x, y, z);
        result.setFormate(Formate.BLH);
        return result;
    }


    /*坐标系转换*/
    public static String CoorSystemToStr(CoorSys coor) {
        String str;
        switch (coor) {
            case WGS84:
                str = "WGS84";
                break;
            case NEW54:
                str = "NEW54";
                break;
            case REALNORTH:
                str = "RealNorth";
                break;
            case SHOOTRANGE:
                str = "ShootRange";
                break;
            case GUNSHOT:
                str = "GunShot";
                break;
            default:
                str = "CGCS2000";
                break;
        }

        return str;
    }

    public static CoorSys StrToCoorSystem(String strCoor) {
        switch (strCoor) {
            case "CGCS2000":
                return CoorSys.CGCS2000;
            case "NEW54":
                return CoorSys.NEW54;
            case "REALNORTH":
                return CoorSys.REALNORTH;
            case "WGS84":
                return CoorSys.WGS84;
            case "SHOOTRANGE":
                return CoorSys.SHOOTRANGE;
            case "GUNSHOT":
                return CoorSys.GUNSHOT;
            default:
                return CoorSys.CGCS2000;
        }
    }

    public static Coordinates XYZtoRAE(Coordinates xyz) {
        Coordinates rae = new Coordinates();
        rae.setLongitude(Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude() + xyz.getAltitude() * xyz.getAltitude()));
        if (rae.getLongitude() <= 0.01) {
            rae.setLongitude(0.0);
            rae.setAltitude(0.0);
            rae.setLatitude(0.0);
            return rae;
        }

        if (xyz.getLatitude() == 0 && xyz.getLongitude() > 0) {
            rae.setLatitude(90);
        } else if (xyz.getLatitude() == 0 && xyz.getLongitude() < 0) {
            rae.setLatitude(270);
        } else if (xyz.getLongitude() == 0 && xyz.getLatitude() > 0) {
            rae.setLatitude(0);
        } else if (xyz.getLongitude() == 0 && xyz.getLatitude() < 0) {
            rae.setLatitude(180);
        } else {
            rae.setLatitude(Math.atan(xyz.getLongitude() / xyz.getLatitude()) * RAD_TO_DEG);
        }

        if (rae.getLatitude() < 0) {
            //第二象限
            if (xyz.getLongitude() > 0) {
                rae.setLatitude(rae.getLatitude() + 180);
            }
            //第四象限
            if (xyz.getLongitude() < 0) {
                rae.setLatitude(rae.getLatitude() + 360);
            }
        } else if (rae.getLatitude() > 0) {
            //第三象限
            if (xyz.getLongitude() < 0) {
                rae.setLatitude(rae.getLatitude() + 180);
            }
        }

        if (0 == xyz.getLongitude() && 0 == xyz.getLatitude()) {
            if (xyz.getAltitude() > 0) {
                rae.setAltitude(90);
            } else if (xyz.getAltitude() < 0) {
                rae.setAltitude(-90);
            } else {
                rae.setAltitude(0);
            }
        }
        rae.setAltitude(Math.atan(xyz.getAltitude() / Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude())) * RAD_TO_DEG);
        rae.setFormate(Formate.RAE);
        return rae;
    }

    public static Coordinates RAEtoXYZ(Coordinates rae) {
        Coordinates xyz = new Coordinates();
        xyz.setLongitude(rae.getLongitude() * Math.cos(rae.getAltitude() * DEG_TO_RAD) * Math.sin(rae.getLatitude() * DEG_TO_RAD));
        xyz.setLatitude(rae.getLongitude() * Math.cos(rae.getAltitude() * DEG_TO_RAD) * Math.cos(rae.getLatitude() * DEG_TO_RAD));
        xyz.setAltitude(rae.getLongitude() * Math.sin(rae.getAltitude() * DEG_TO_RAD));
        xyz.setFormate(Formate.XYZ);
        return xyz;
    }

    public static Coordinates XYZtoBLH(Coordinates xyz, CoorSys coorSys) {
        Coordinates blh = new Coordinates();
        double a, g_b, e2;
        switch (coorSys) {
            case WGS84:
                a = WGS84_A;
                g_b = G_B;
                e2 = WGS84_E_2;
                break;
            default:
                a = CGCS2000_A;
                g_b = G_B;
                e2 = CGCS2000_E_2;
                break;
        }

        //经度
        blh.setLongitude(Math.atan(xyz.getLatitude() / xyz.getLongitude()) * RAD_TO_DEG);
        //纬度,迭代法计算，使用弧度计算，最后的结果再转成角度
        double b, n, h, div1, div2, tmp1, tmp2;
        div1 = div2 = 1;
        n = a;
        h = Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude() + xyz.getAltitude() * xyz.getAltitude()) - Math.sqrt(a * g_b);
        b = Math.atan((xyz.getAltitude() / Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude())) * ((n + h) / (n * (1 - e2) + h)));

        int time = 0;
        //迭代超过1000次则结束循环 by shao 1217
        while (Math.abs(div1) > 0.0000001 && Math.abs(div2) > 0.00000001 && time < 1000) {
            tmp1 = h;
            tmp2 = b;
            n = a / Math.sqrt(1 - e2 * Math.sin(b) * Math.sin(b));
            h = Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude()) / Math.cos(b) - n;
            b = Math.atan((xyz.getAltitude() / Math.sqrt(xyz.getLongitude() * xyz.getLongitude() + xyz.getLatitude() * xyz.getLatitude())) * ((n + h) / (n * (1 - e2) + h)));

            div1 = tmp1 - h;
            div2 = tmp2 - b;
            time++;

        }

        blh.setLatitude(b * RAD_TO_DEG);
        blh.setAltitude(h);
        if (xyz.getLongitude() < 0 && xyz.getLatitude() > 0) {
            blh.setLongitude(blh.getLongitude() + 180);
        }
        if (xyz.getLongitude() < 0 && xyz.getLatitude() < 0) {
            blh.setLongitude(blh.getLongitude() - 180);
        }

        blh.setFormate(Formate.BLH);
        return blh;
    }

    public static Coordinates XYZtoBLH_alter(Coordinates xyz, CoorSys coorSys) {
        Coordinates blh = new Coordinates();
        double a, b, e2, e2_2;

        switch (coorSys) {
            case WGS84:
                a = WGS84_A;
                b = G_B;
                e2 = WGS84_E_2;
                e2_2 = WGS84_E2_2;
                break;
            default:
                a = CGCS2000_A;
                b = G_B;
                e2 = CGCS2000_E_2;
                e2_2 = CGCS2000_E2_2;
                break;
        }

        //经度
        blh.setLongitude(Math.atan
                (xyz.getLatitude()
                        / xyz.getLongitude()
                ) * RAD_TO_DEG);
        double u = Math.atan
                (xyz.getAltitude()
                        / (Math.sqrt((xyz.getLongitude()
                        * xyz.getLongitude()
                        + xyz.getLatitude()
                        * xyz.getLatitude()
                ) * (1 - e2))));
        blh.setLatitude(Math.atan
                (xyz.getAltitude() + b * e2_2 * Math.pow(Math.sin(u), 3)) / (Math.sqrt(xyz.getLongitude()
                * xyz.getLongitude()
                + xyz.getLatitude()
                * xyz.getLatitude()
        ) - a * e2 * Math.pow(Math.cos(u), 3)));
        blh.setAltitude(xyz.getAltitude()/ Math.sin(blh.getLatitude()) - a * (1 - e2) / Math.sqrt(1 - e2 * Math.pow(Math.sin(blh.getLatitude()), 2)));

        blh.setLatitude(blh.getLatitude() * RAD_TO_DEG);
        blh.setFormate(Formate.BLH);
        return blh;
    }
    public static Coordinates XYZtoBLH_alter2(Coordinates xyz,Coordinates a0,Coordinates b0,CoorSys coorSys) {
        Coordinates blh = new Coordinates();
        double a,g_b,e2;

        switch (coorSys) {
            case WGS84:
                a=WGS84_A;
                g_b=G_B;
                e2=WGS84_E_2;
                break;
            default:
                a=CGCS2000_A;
                g_b=G_B;
                e2=CGCS2000_E_2;
                break;
        }

        //经度
        blh.setLongitude(Math.atan(xyz.getLatitude()/xyz.getLongitude()) * RAD_TO_DEG);
        //纬度,迭代法计算，使用弧度计算，最后的结果再转成角度

        double b,n,h,div1,div2,tmp1,tmp2;
        div1=div2 = 1;
        n = a;
        h = Math.sqrt(xyz.getLongitude()*xyz.getLongitude()+xyz.getLatitude()*xyz.getLatitude()+xyz.getAltitude()*xyz.getAltitude()) - Math.sqrt(a*g_b);
        b = Math.atan((xyz.getAltitude()/Math.sqrt(xyz.getLongitude()*xyz.getLongitude()+xyz.getLatitude()*xyz.getLatitude()))*((n+h)/(n*(1-e2)+h)));

        int time = 0;
        while(Math.abs(div1) > 0.0000001 && Math.abs(div2) > 0.00000001 && time < 1000) //迭代超过1000次则结束循环 by shao 1217
        {
            tmp1 = h;
            tmp2 = b;
            n = a / Math.sqrt(1-e2*Math.sin(b)*Math.sin(b));
            h = Math.sqrt(xyz.getLongitude()*xyz.getLongitude() +xyz.getLatitude()*xyz.getLatitude()) / Math.cos(b) - n;
            b = Math.atan((xyz.getAltitude()/Math.sqrt(xyz.getLongitude()*xyz.getLongitude()+xyz.getLatitude()*xyz.getLatitude()))*((n+h)/(n*(1-e2)+h)));
            div1 = tmp1-h;
            div2 = tmp2-b;
            time++;
        }

        blh.setLatitude(b*RAD_TO_DEG);
        blh.setAltitude(h);
        double c1 = Math.sqrt(Math.pow(a0.getLongitude(),2)+Math.pow(a0.getLatitude(),2));
        double c2 = Math.sqrt(Math.pow(b0.getLongitude(),2)+Math.pow(b0.getLatitude(),2));
        double k = (a0.getAltitude()-b0.getAltitude())/(c1-c2);
        double bb = c1*k + a0.getAltitude();

        double cx = Math.sqrt(Math.pow(xyz.getLongitude(),2)+Math.pow(xyz.getLatitude(),2));
        h = Math.abs(k*cx-xyz.getAltitude()+bb)/Math.sqrt(k*k+1);
        blh.setAltitude(h-5978104.263074);

        if(xyz.getLongitude()<0 && xyz.getLatitude()>0) {
            blh.setLongitude(blh.getLongitude()+180);
        }
        if(xyz.getLongitude()<0 && xyz.getLatitude()<0) {
            blh.setLongitude(blh.getLongitude()-180);
        }

        blh.setFormate(Formate.BLH);
        return blh;
    }

    public static Coordinates coorTransTo(Coordinates xyz, Coordinates offset,double r_x,double r_y, double r_z){
        Coordinates xyz_out = new Coordinates();
        //在某些情况下，r_x和r_y可以忽略，计为0.
        xyz_out.setLongitude(offset.getLongitude() + xyz.getLongitude()*Math.cos(r_y*DEG_TO_RAD)*Math.cos(r_z*DEG_TO_RAD)+
                xyz.getLatitude()*(Math.sin(r_x*DEG_TO_RAD)*Math.sin(r_y*DEG_TO_RAD)*Math.cos(r_z*DEG_TO_RAD)+
                        Math.cos(r_x*DEG_TO_RAD)*Math.sin(r_z*DEG_TO_RAD)) +
                xyz.getAltitude()*(Math.sin(r_x*DEG_TO_RAD)*Math.sin(r_z*DEG_TO_RAD)-
                        Math.cos(r_x*DEG_TO_RAD)*Math.sin(r_y*DEG_TO_RAD)*Math.cos(r_z*DEG_TO_RAD)));

        xyz_out.setLatitude(offset.getLatitude() - xyz.getLongitude()*Math.cos(r_y*DEG_TO_RAD)*Math.sin(r_z*DEG_TO_RAD) +
                xyz.getLatitude()*(Math.cos(r_x*DEG_TO_RAD)*Math.cos(r_z*DEG_TO_RAD)-
                        Math.sin(r_x*DEG_TO_RAD)*Math.sin(r_y*DEG_TO_RAD)*Math.sin(r_z*DEG_TO_RAD))+
                xyz.getAltitude()*(Math.sin(r_x*DEG_TO_RAD)*Math.cos(r_z*DEG_TO_RAD)-
                        Math.cos(r_x*DEG_TO_RAD)*Math.sin(r_y*DEG_TO_RAD)*Math.sin(r_z*DEG_TO_RAD)));

        xyz_out.setAltitude(offset.getAltitude() + xyz.getLongitude()*Math.sin(r_z*DEG_TO_RAD)-
                xyz.getLatitude()*Math.sin(r_x*DEG_TO_RAD)*Math.cos(r_y*DEG_TO_RAD) +
                xyz.getAltitude()*Math.cos(r_x*DEG_TO_RAD)*Math.cos(r_y*DEG_TO_RAD));
        return xyz_out;
    }

    public static void main(String[] args) {
        Coordinates coordinates = new Coordinates(116.2328,39.919583,0);
        Coordinates coordinates1 = XYZtoBLH_alter(coordinates, CoorSys.WGS84);
        System.out.println(coordinates1);
    }

}

