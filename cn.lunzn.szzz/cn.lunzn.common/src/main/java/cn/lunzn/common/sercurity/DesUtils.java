package cn.lunzn.common.sercurity;

import java.security.Key;
import java.security.Security;
import java.util.Properties;
import java.util.Set;

import javax.crypto.Cipher;

/**
 * 加解密工具类
 * 
 * @author  杜浩
 * @version  [版本号, 2017年9月12日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class DesUtils
{
    /**
     * 数字256
     */
    private static final int NUM_256 = 256;
    
    /**
     * 数字16
     */
    private static final int NUM_16 = 16;
    
    /**
     * 数字8
     */
    private static final int NUM_8 = 8;
    
    /** 字符串默认键值 */
    private static String strDefaultKey = "lunzn";
    
    /** 加密工具 */
    private Cipher encryptCipher = null;
    
    /** 解密工具 */
    private Cipher decryptCipher = null;
    
    /**
     * 默认构造方法，使用默认密钥
     * 
     * @throws Exception 异常
     */
    public DesUtils()
        throws Exception
    {
        this(strDefaultKey);
    }
    
    /**
     * 指定密钥构造方法
     * 
     * @param strKey
     *            指定的密钥
     * @throws Exception 异常
     */
    @SuppressWarnings("restriction")
    public DesUtils(String strKey)
        throws Exception
    {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key = getKey(strKey.getBytes("utf-8"));
        
        encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);
        
        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }
    
    /**
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程
     * 
     * @param arrB
     *            需要转换的byte数组
     * @return 转换后的字符串
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static String byteArr2HexStr(byte[] arrB)
        throws Exception
    {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++)
        {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0)
            {
                intTmp = intTmp + NUM_256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < NUM_16)
            {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, NUM_16));
        }
        return sb.toString();
    }
    
    /**
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
     * 互为可逆的转换过程
     * 
     * @param strIn
     *            需要转换的字符串
     * @return 转换后的byte数组
     * @throws Exception
     *             本方法不处理任何异常，所有异常全部抛出
     */
    public static byte[] hexStr2ByteArr(String strIn)
        throws Exception
    {
        byte[] arrB = strIn.getBytes("utf-8");
        int iLen = arrB.length;
        
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2)
        {
            String strTmp = new String(arrB, i, 2, "utf-8");
            arrOut[i / 2] = (byte)Integer.parseInt(strTmp, NUM_16);
        }
        return arrOut;
    }
    
    /**
     * 加密字节数组
     * 
     * @param arrB
     *            需加密的字节数组
     * @return 加密后的字节数组
     * @throws Exception 异常
     */
    public byte[] encrypt(byte[] arrB)
        throws Exception
    {
        return encryptCipher.doFinal(arrB);
    }
    
    /**
     * 加密字符串
     * 
     * @param strIn
     *            需加密的字符串
     * @return 加密后的字符串
     * @throws Exception 异常
     */
    public String encrypt(String strIn)
        throws Exception
    {
        return byteArr2HexStr(encrypt(strIn.getBytes("utf-8")));
    }
    
    /**
     * 解密字节数组
     * 
     * @param arrB
     *            需解密的字节数组
     * @return 解密后的字节数组
     * @throws Exception 异常
     */
    public byte[] decrypt(byte[] arrB)
        throws Exception
    {
        return decryptCipher.doFinal(arrB);
    }
    
    /**
     * 解密字符串
     * 
     * @param strIn
     *            需解密的字符串
     * @return 解密后的字符串
     * @throws Exception 异常
     */
    public String decrypt(String strIn)
        throws Exception
    {
        return new String(decrypt(hexStr2ByteArr(strIn)), "utf-8");
    }
    
    /**
     * 从指定字符串生成密钥，密钥所需的字节数组长度为8位 不足8位时后面补0，超出8位只取前8位
     * 
     * @param arrBTmp
     *            构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception 异常
     */
    private Key getKey(byte[] arrBTmp)
        throws Exception
    {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[NUM_8];
        
        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++)
        {
            arrB[i] = arrBTmp[i];
        }
        
        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");
        
        return key;
    }
    
    /** 
     * 读取配置文件，将前缀为JM的值进行解密
     * @param properties 解密前配置文件
     * @return  Properties 解密后配置文件
     * @throws Exception 异常
     * @see [类、类#方法、类#成员]
     */
    public static Properties processProperties(Properties properties)
        throws Exception
    {
        DesUtils des = new DesUtils();
        Set<Object> keys = properties.keySet();
        for (Object object : keys)
        {
            String keyName = object.toString();
            String value = properties.getProperty(keyName);
            if (value.startsWith("JM"))
            {
                value = des.decrypt(value.replaceAll("JM", ""));
                properties.put(keyName, value);
            }
        }
        return properties;
    }
    
    /*public static void main(String args[])
    {
        
        try
        {
            String test = "Mystyle-123";
            //			DesUtils des = new DesUtils("lanzhengkeji");// 自定义密钥
            DesUtils des = new DesUtils();// 自定义密钥
            System.out.println("加密前的字符：" + test);
            System.out.println("加密后的字符：" + des.encrypt(test));
            System.out.println("解密后的字符：" + des.decrypt(des.encrypt(test)));
            
            System.out.println("解密后的字符：" + des.decrypt("202cb962ac59075b964b07152d234b70"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }*/
}
