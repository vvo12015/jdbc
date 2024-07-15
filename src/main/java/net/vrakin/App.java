package net.vrakin;

import lombok.extern.slf4j.Slf4j;
import net.vrakin.dao.ApartmentDao;
import net.vrakin.dao.DBConnector;
import net.vrakin.dao.RegionDao;
import net.vrakin.dto.Apartment;
import net.vrakin.dto.Region;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Apartment manager
 * This program is crud for apartment
 *
 */
@Slf4j
public class App 
{
    // CREATE DATABASE mydb;
    static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/mydb?serverTimezone=Europe/Kiev";
    static final String DB_USER = "root";
    static final String DB_PASSWORD = "mysqlrooTRootmysql11111!!!";

    static Connection conn;

    //    public static final DBConnector dbConnector = new DBConnector(DB_CONNECTION, DB_USER, DB_PASSWORD);
    private static final RegionDao regionDao = new RegionDao();;
    private static final ApartmentDao apartmentDao = new ApartmentDao();

    public static void main( String[] args ) {

    }
}
