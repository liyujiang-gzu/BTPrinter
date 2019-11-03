package com.github.gzuliyujiang.bleprinter;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * 参见 https://github.com/heroxuetao/PrintUtils
 * Created by liyujiang on 2019/11/04
 *
 * @author 大定府羡民
 */
@SuppressWarnings("unused")
public class EscPosUtils {

    /**
     * 打印纸一行最大的字节
     */
    private static final int LINE_BYTE_SIZE = 32;

    private static final int LEFT_LENGTH = 20;

    private static final int RIGHT_LENGTH = 12;

    /**
     * 左侧汉字最多显示几个文字
     */
    private static final int LEFT_TEXT_MAX_LENGTH = 8;

    /**
     * 小票打印菜品的名称，上限调到8个字
     */
    public static final int MEAL_NAME_MAX_LENGTH = 8;

    private static OutputStream outputStream = null;

    public static OutputStream getOutputStream() {
        return outputStream;
    }

    public static void setOutputStream(OutputStream os) {
        outputStream = os;
    }


    /**
     * 打印文字
     *
     * @param text 要打印的文字
     */
    public static void printText(String text) {
        if (outputStream == null) {
            return;
        }
        try {
            byte[] data = text.getBytes("gbk");
            outputStream.write(data, 0, data.length);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置打印格式
     *
     * @param command 格式指令
     */
    public static void selectCommand(byte[] command) {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.write(command);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 复位打印机
     */
    public static final byte[] RESET = {0x1b, 0x40};

    /**
     * 左对齐
     */
    public static final byte[] ALIGN_LEFT = {0x1b, 0x61, 0x00};

    /**
     * 中间对齐
     */
    public static final byte[] ALIGN_CENTER = {0x1b, 0x61, 0x01};

    /**
     * 右对齐
     */
    public static final byte[] ALIGN_RIGHT = {0x1b, 0x61, 0x02};

    /**
     * 选择加粗模式
     */
    public static final byte[] BOLD = {0x1b, 0x45, 0x01};

    /**
     * 取消加粗模式
     */
    public static final byte[] BOLD_CANCEL = {0x1b, 0x45, 0x00};

    /**
     * 宽高加倍
     */
    public static final byte[] DOUBLE_HEIGHT_WIDTH = {0x1d, 0x21, 0x11};

    /**
     * 宽加倍
     */
    public static final byte[] DOUBLE_WIDTH = {0x1d, 0x21, 0x10};

    /**
     * 高加倍
     */
    public static final byte[] DOUBLE_HEIGHT = {0x1d, 0x21, 0x01};

    /**
     * 字体不放大
     */
    public static final byte[] NORMAL = {0x1d, 0x21, 0x00};

    /**
     * 设置默认行间距
     */
    public static final byte[] LINE_SPACING_DEFAULT = {0x1b, 0x32};


//    final byte[][] byteCommands = {
//            {0x1b, 0x61, 0x00}, // 左对齐
//            {0x1b, 0x61, 0x01}, // 中间对齐
//            {0x1b, 0x61, 0x02}, // 右对齐
//            {0x1b, 0x40},// 复位打印机
//            {0x1b, 0x4d, 0x00},// 标准ASCII字体
//            {0x1b, 0x4d, 0x01},// 压缩ASCII字体
//            {0x1d, 0x21, 0x00},// 字体不放大
//            {0x1d, 0x21, 0x11},// 宽高加倍
//            {0x1b, 0x45, 0x00},// 取消加粗模式
//            {0x1b, 0x45, 0x01},// 选择加粗模式
//            {0x1b, 0x7b, 0x00},// 取消倒置打印
//            {0x1b, 0x7b, 0x01},// 选择倒置打印
//            {0x1d, 0x42, 0x00},// 取消黑白反显
//            {0x1d, 0x42, 0x01},// 选择黑白反显
//            {0x1b, 0x56, 0x00},// 取消顺时针旋转90°
//            {0x1b, 0x56, 0x01},// 选择顺时针旋转90°
//    };

    /**
     * 打印两列
     *
     * @param leftText  左侧文字
     * @param rightText 右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String format2Column(String leftText, String rightText) {
        StringBuilder sb = new StringBuilder();
        int leftTextLength = calculateBytesLength(leftText);
        int rightTextLength = calculateBytesLength(rightText);
        sb.append(leftText);

        // 计算两侧文字中间的空格
        int marginBetweenMiddleAndRight = LINE_BYTE_SIZE - leftTextLength - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }
        sb.append(rightText);
        return sb.toString();
    }

    /**
     * 打印三列
     *
     * @param leftText   左侧文字
     * @param middleText 中间文字
     * @param rightText  右侧文字
     * @return
     */
    @SuppressLint("NewApi")
    public static String format3Column(String leftText, String middleText, String rightText) {
        StringBuilder sb = new StringBuilder();
        // 左边最多显示 LEFT_TEXT_MAX_LENGTH 个汉字 + 两个点
        if (leftText.length() > LEFT_TEXT_MAX_LENGTH) {
            leftText = leftText.substring(0, LEFT_TEXT_MAX_LENGTH) + "..";
        }
        int leftTextLength = calculateBytesLength(leftText);
        int middleTextLength = calculateBytesLength(middleText);
        int rightTextLength = calculateBytesLength(rightText);

        sb.append(leftText);
        // 计算左侧文字和中间文字的空格长度
        int marginBetweenLeftAndMiddle = LEFT_LENGTH - leftTextLength - middleTextLength / 2;

        for (int i = 0; i < marginBetweenLeftAndMiddle; i++) {
            sb.append(" ");
        }
        sb.append(middleText);

        // 计算右侧文字和中间文字的空格长度
        int marginBetweenMiddleAndRight = RIGHT_LENGTH - middleTextLength / 2 - rightTextLength;

        for (int i = 0; i < marginBetweenMiddleAndRight; i++) {
            sb.append(" ");
        }

        // 打印的时候发现，最右边的文字总是偏右一个字符，所以需要删除一个空格
        sb.delete(sb.length() - 1, sb.length()).append(rightText);
        return sb.toString();
    }

    /**
     * 获取数据长度
     *
     * @param msg
     * @return
     */
    @SuppressLint("NewApi")
    private static int calculateBytesLength(String msg) {
        return msg.getBytes(Charset.forName("GBK")).length;
    }

    public static char[] toChars(byte[] bytes) {
        Charset charset = Charset.forName("GBK");
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes).flip();
        CharBuffer cb = charset.decode(bb);
        return cb.array();
    }

    /**
     * 格式化菜品名称，最多显示MEAL_NAME_MAX_LENGTH个数
     *
     * @param name
     * @return
     */
    public static String formatMealName(String name) {
        if (TextUtils.isEmpty(name)) {
            return name;
        }
        if (name.length() > MEAL_NAME_MAX_LENGTH) {
            return name.substring(0, 8) + "..";
        }
        return name;
    }

    public static void printTest() {
        selectCommand(RESET);
        selectCommand(LINE_SPACING_DEFAULT);
        selectCommand(ALIGN_CENTER);
        printText("美食餐厅\n\n");
        selectCommand(DOUBLE_HEIGHT_WIDTH);
        printText("桌号：1号桌\n\n");
        selectCommand(NORMAL);
        selectCommand(ALIGN_LEFT);
        printText(format2Column("订单编号", "201507161515\n"));
        printText(format2Column("点菜时间", "2016-02-16 10:46\n"));
        printText(format2Column("上菜时间", "2016-02-16 11:46\n"));
        printText(format2Column("人数：2人", "收银员：张三\n"));

        printText("--------------------------------\n");
        selectCommand(BOLD);
        printText(format3Column("项目", "数量", "金额\n"));
        printText("--------------------------------\n");
        selectCommand(BOLD_CANCEL);
        printText(format3Column("面", "1", "0.00\n"));
        printText(format3Column("米饭", "1", "6.00\n"));
        printText(format3Column("铁板烧", "1", "26.00\n"));
        printText(format3Column("一个测试", "1", "226.00\n"));
        printText(format3Column("牛肉面啊啊", "1", "2226.00\n"));
        printText(format3Column("牛肉面啊啊啊牛肉面啊啊啊", "888", "98886.00\n"));

        printText("--------------------------------\n");
        printText(format2Column("合计", "53.50\n"));
        printText(format2Column("抹零", "3.50\n"));
        printText("--------------------------------\n");
        printText(format2Column("应收", "50.00\n"));
        printText("--------------------------------\n");

        selectCommand(ALIGN_LEFT);
        printText("备注：不要辣、不要香菜");
        printText("\n\n\n\n\n");
    }

}
