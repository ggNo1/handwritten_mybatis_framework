package com.mt.mybatis.statement;

import com.mt.mybatis.configuration.MtConfiguration;
import com.mt.mybatis.configuration.MtDataSource;
import com.mt.mybatis.mapper.MapperData;
import com.mt.mybatis.parameter.ParameterHandler;
import com.mt.mybatis.result.ResultHandler;

import java.sql.*;

/**
 * <p>StatementHandler，语句集处理</p>
 *
 * @author grand 2018/6/20
 * @version V1.0
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人}
 * @modify by reason:{方法名}:{原因}
 */
public class StatementHandler {
    private MtConfiguration configuation;

    public StatementHandler(MtConfiguration configuation){
        this.configuation = configuation;
    }

    public <E> E query(MapperData mapperData, Object parameter){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(mapperData.getSql());
            ParameterHandler parameterHandler = new ParameterHandler(ps);
            parameterHandler.setParameters(parameter);
            ps.execute();
            ResultHandler resultHandler = new ResultHandler(mapperData.getType(),ps.getResultSet());
            return (E) resultHandler.handle();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            try {
                if(ps!=null){
                    ps.close();
                }
                if(conn!=null){
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Integer delete(MapperData mapperData, Object parameter){
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement( mapperData.getSql() );
            Object[] objects = null;
            if ( parameter instanceof Object[]){
                objects = (Object[]) parameter;
            }
            for (int i = 0; i < objects.length; i++) {
                ps.setDouble(i+1, Double.valueOf( objects[i].toString() ));
            }
            int i = ps.executeUpdate();
            System.err.println( i );
            return i;
        }catch (Exception e){
            System.err.println( e.getMessage() );
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Connection getConnection() throws SQLException, ClassNotFoundException {
        MtDataSource dataSource = configuation.getDataSource();
        Class.forName(dataSource.getDriver());
        return DriverManager.getConnection(dataSource.getUrl(),dataSource.getUserName(),dataSource.getPassWord());
    }
}
