package org.bsm.utils;


import java.util.*;

/**
 * @author GZC
 * @create 2021-11-02 23:44
 * @desc 随机字符串工具类
 */
class RandomUtils {
    /**
     * 所有字符
     */
    public static final int MULTIPLE = 0;
    /**
     * 只包含小写字母
     */
    public static final int LOWER = 1;
    /**
     * 只包含大写字母
     */
    public static final int UPPER = 2;
    /**
     * 只包含数字
     */
    public static final int NUMBER = 3;
    /**
     * 包含大小写字母
     */
    public static final int LOWER_AND_UPPER = 4;
    /**
     * 包含小写字母和数字
     */
    public static final int LOWER_AND_NUMBER = 5;
    /**
     * 包含大写字母和数字
     */
    public static final int UPPER_AND_NUMBER = 6;
    final static Random RANDOM = new Random();
    final static char[] LOWER_CHAR = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y'};
    final static char[] UPPER_CHAR = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y'};
    final static char[] NUMBER_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 拼接字符数组
     */
    public static char[] concat(char[]... a) {
        LinkedList<Character> list = new LinkedList<>();
        for (char[] chars : a) {
            for (char aChar : chars) {
                list.add(aChar);
            }
        }
        char[] result = new char[list.size()];
        Iterator<Character> it = list.iterator();
        for (int i = 0, length = result.length; i < length; i++) {
            result[i] = it.next();
        }
        return result;
    }

    /**
     * 获取随机字符串，包含大小写字母和数字，可以有重复字符
     *
     * @param length 字符串长度
     */
    public static String getRandomString(int length) {
        return getRandomString(length, MULTIPLE, true);
    }

    /**
     * 获取随机字符串
     *
     * @param length     字符串长度
     * @param randomType 随机字符的类型
     * @param repeat     是否可以有重复字符，true表示可以重复，false表示不允许重复。如果length大于randomType库的长度，则默认采用true值。
     */
    public static String getRandomString(int length, int randomType, boolean repeat) {
        StringBuilder sb = new StringBuilder();
        char[] find = null;
        switch (randomType) {
            case LOWER:
                find = LOWER_CHAR;
                break;
            case UPPER:
                find = UPPER_CHAR;
                break;
            case NUMBER:
                find = NUMBER_CHAR;
                break;
            case MULTIPLE:
                find = concat(LOWER_CHAR, UPPER_CHAR, NUMBER_CHAR);
                break;
            case LOWER_AND_NUMBER:
                find = concat(LOWER_CHAR, NUMBER_CHAR);
                break;
            case UPPER_AND_NUMBER:
                find = concat(UPPER_CHAR, NUMBER_CHAR);
                break;
            case LOWER_AND_UPPER:
                find = concat(LOWER_CHAR, UPPER_CHAR);
                break;
            default:
                break;
        }
        if (find == null) {
            return "";
        }
        if (length > find.length) {
            repeat = true;
        }
        if (repeat) {
            for (int i = 0; i < length; i++) {
                sb.append(find[RANDOM.nextInt(find.length)]);
            }
        } else {
            HashSet<Integer> indexset = new HashSet<>();
            for (int i = 0; i < length; i++) {
                int index = RANDOM.nextInt(find.length);
                while (indexset.contains(index)) {
                    index = RANDOM.nextInt(find.length);
                }
                sb.append(find[index]);
                indexset.add(index);
            }
        }
        return sb.toString();
    }

    /**
     * 获取随机字符串，可以有重复字符
     *
     * @param length     字符串长度
     * @param randomType 随机字符的类型
     */
    public String getRandomString(int length, int randomType) {
        return getRandomString(length, randomType, true);
    }

    /**
     * 获取随机字符串
     *
     * @param length 字符串长度
     * @param repeat 是否可以存在重复的字符
     * @param ch     自定义字符集
     */
    public String getRandomString(int length, boolean repeat, char[]... ch) {
        List<Character> list = new LinkedList<>();
        for (char[] cs : ch) {
            for (char c : cs) {
                list.add(c);
            }
        }
        if (list.size() == 0) {
            return "";
        }
        if (length > list.size()) {
            repeat = true;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (repeat) {
                sb.append(list.get(RANDOM.nextInt(list.size())));
            } else {
                sb.append(list.remove(RANDOM.nextInt(list.size())));
            }
        }
        return sb.toString();
    }
}