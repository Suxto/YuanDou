package Utils;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class JDBC {
    //获取数据库的连接

    public static Connection getConnection() throws Exception {
        InputStream is = JDBC.class.getResourceAsStream("/Utils/jdbc.properties");
        Properties pros = new Properties();
        pros.load(is);

        //从配置文件读取字段
        String url = pros.getProperty("url");
        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String driverClass = pros.getProperty("driverClass");
        //System.out.println(url+' '+user+' '+password+' '+driverClass);
        //加载驱动
        Class.forName(driverClass);
        //获取连接
        return DriverManager.getConnection(url, user, password);
    }

    //关闭 连接 & statement
    public static void closeResource(Connection conn, Statement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (ps != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
        closeResource(conn, ps);
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> getForList(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBC.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            //创建集合对象
            ArrayList<T> list = new ArrayList<>();
            Constructor<T> con = clazz.getConstructor();

            while (rs.next()) {
                T t = getInstance(clazz, rs, con);
                list.add(t);
            }
            //返回表
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBC.closeResource(conn, ps, rs);
        }
        return null;
    }


    public static <T> T getOne(Class<T> clazz, String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBC.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            Constructor<T> con = clazz.getConstructor();

            if (rs.next()) {
                return getInstance(clazz, rs, con);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(conn, ps, rs);
        }
        return null;
    }

    public static <T> T getInstance(Class<T> clazz, ResultSet rs, Constructor<T> con) {
        T t;
        ResultSetMetaData metaData;
        try {
            t = con.newInstance();
            metaData = rs.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                Object value = rs.getObject(i + 1);
                String columnLabel = metaData.getColumnLabel(i + 1);
                Field field = clazz.getDeclaredField(columnLabel);
                field.setAccessible(true);
                field.set(t, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //返回对象
        return t;
    }

    public static long getCount(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBC.getConnection();
            ps = conn.prepareStatement(sql);

            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                return (Long) rs.getObject(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResource(conn, ps, rs);
        }
        return 0;
    }


    public static void update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            ps.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBC.closeResource(conn, ps);
        }
    }

    public static ResultSet getResultSet(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = JDBC.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }

}
